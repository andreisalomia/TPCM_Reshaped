package com.example.kafkaconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TpcmApiService {

    private final WebClient tpcmWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void createCustomer(Long customerID, String name, String type, Integer billCycleDay) {
        try {
            ObjectNode customer = objectMapper.createObjectNode();
            customer.put("name", name);
            customer.put("type", type);
            customer.put("billCycleDay", billCycleDay);

            tpcmWebClient.post()
                    .uri("/api/app/customers")
                    .bodyValue(customer)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("Customer {} created in app DB", customerID))
                    .doOnError(error -> log.error("Failed to create customer {} in app DB: {}", customerID, error.getMessage()))
                    .block();
        } catch (Exception e) {
            log.error("Exception creating customer {} in app DB: {}", customerID, e.getMessage());
        }
    }

    public void updateCustomer(Long customerId, String name, String type, Integer billCycleDay, Integer nrSubscribers) {
        try {
            String customerJson = tpcmWebClient.get()
                    .uri("/api/app/customers/{id}", customerId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (customerJson == null) {
                log.error("Customer {} not found in app DB, importing from clients DB", customerId);
                importCustomerFromClients(customerId);
                return;
            }

            ObjectNode customer = (ObjectNode) objectMapper.readTree(customerJson);

            if (name != null) customer.put("name", name);
            if (type != null) customer.put("type", type);
            if (billCycleDay != null) customer.put("billCycleDay", billCycleDay);
            if (nrSubscribers != null) customer.put("nrSubscribers", nrSubscribers);

            tpcmWebClient.put()
                    .uri("/api/app/customers/{id}", customerId)
                    .bodyValue(customer)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("Customer {} updated in app DB", customerId))
                    .doOnError(error -> log.error("Failed to update customer {} in app DB: {}", customerId, error.getMessage()))
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Customer {} not found in app DB", customerId);
            importCustomerFromClients(customerId);
        } catch (Exception e) {
            log.error("Exception updating customer {} in app DB: {}", customerId, e.getMessage());
        }
    }

    public void deleteCustomer(Long customerId) {
        try {
            tpcmWebClient.delete()
                    .uri("/api/app/customers/{id}", customerId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnSuccess(response -> log.info("Customer {} deleted from app DB", customerId))
                    .doOnError(error -> log.error("Failed to delete customer {} from app DB: {}", customerId, error.getMessage()))
                    .block();
        } catch (Exception e) {
            log.error("Exception deleting customer {} from app DB: {}", customerId, e.getMessage());
        }
    }

    public void createSubscriber(Long subscriberID, String msisdn, String status, String subscriptionType, Long customerID) {
        try {
            log.info("About to create subscriber with data: msisdn={}, status={}, type={}, customerID={}",
                    msisdn, status, subscriptionType, customerID);

            ObjectNode subscriber = objectMapper.createObjectNode();
            subscriber.put("msisdn", msisdn);
            subscriber.put("status", status);
            subscriber.put("subscriptionType", subscriptionType);

            ObjectNode customer = objectMapper.createObjectNode();
            customer.put("customerID", customerID);
            subscriber.set("customer", customer);

            String response = tpcmWebClient.post()
                    .uri("/api/app/subscribers")
                    .bodyValue(subscriber)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectNode createdSubscriber = (ObjectNode) objectMapper.readTree(response);
            Long appSubscriberId = createdSubscriber.get("subscriberID").asLong();

            log.info("Subscriber created in app DB with ID {} (clients DB ID was {})", appSubscriberId, subscriberID);

            createDefaultLimitForSubscriber(appSubscriberId);
            updateCustomerSubscriberCount(customerID);
        } catch (Exception e) {
            log.error("EXCEPTION caught: creating subscriber in app DB: {}", e.getMessage());
            log.error("Exception class: {}", e.getClass().getName());
            log.error("Full exception stack trace:", e);
        }
    }

    public void updateSubscriber(Long subscriberId, String msisdn, String status, String subscriptionType, Long customerID) {
        try {
            String subscriberJson = tpcmWebClient.get()
                    .uri("/api/app/subscribers/{id}", subscriberId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (subscriberJson == null) {
                log.error("Subscriber {} not found in app DB, importing from clients DB", subscriberId);
                importSubscriberFromClients(subscriberId);
                return;
            }

            ObjectNode subscriber = (ObjectNode) objectMapper.readTree(subscriberJson);

            if (msisdn != null) subscriber.put("msisdn", msisdn);
            if (status != null) subscriber.put("status", status);
            if (subscriptionType != null) subscriber.put("subscriptionType", subscriptionType);
            if (customerID != null) {
                Long oldCustomerID = subscriber.get("customer").get("customerID").asLong();

                ObjectNode customer = objectMapper.createObjectNode();
                customer.put("customerID", customerID);
                subscriber.set("customer", customer);

                if(!oldCustomerID.equals(customerID)) {
                    updateCustomerSubscriberCount(oldCustomerID);
                    updateCustomerSubscriberCount(customerID);
                }
            }

            tpcmWebClient.put()
                    .uri("/api/app/subscribers/{id}", subscriberId)
                    .bodyValue(subscriber)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("Subscriber {} updated in app DB", subscriberId))
                    .doOnError(error -> log.error("Failed to update subscriber {} in app DB: {}", subscriberId, error.getMessage()))
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Subscriber {} not found in app DB", subscriberId);
            importSubscriberFromClients(subscriberId);
        } catch (Exception e) {
            log.error("Exception updating subscriber {} in app DB: {}", subscriberId, e.getMessage());
        }
    }

    public void deleteSubscriber(Long subscriberId) {
        try {
            String subscriberJson = tpcmWebClient.get()
                    .uri("/api/app/subscribers/{id}", subscriberId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Long customerID = null;
            if (subscriberJson != null) {
                ObjectNode subscriber = (ObjectNode) objectMapper.readTree(subscriberJson);
                customerID = subscriber.get("customer").get("customerID").asLong();
            }

            tpcmWebClient.delete()
                    .uri("/api/app/subscribers/{id}", subscriberId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Subscriber {} deleted from app DB", subscriberId);
            if (customerID != null) {
                updateCustomerSubscriberCount(customerID);
            }
        } catch (Exception e) {
            log.error("Exception deleting subscriber {} from app DB: {}", subscriberId, e.getMessage());
        }
    }

    public void createUser(Long userID, String username, String role) {
        try {
            ObjectNode user = objectMapper.createObjectNode();
            user.put("username", username);
            user.put("role", role);

            tpcmWebClient.post()
                    .uri("/api/app/users")
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("User {} created in app DB", userID))
                    .doOnError(error -> log.error("Failed to create user {} in app DB: {}", userID, error.getMessage()))
                    .block();
        } catch (Exception e) {
            log.error("Exception creating user {} in app DB: {}", userID, e.getMessage());
        }
    }

    public void updateUser(Long userId, String username, String role) {
        try {
            String userJson = tpcmWebClient.get()
                    .uri("/api/app/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (userJson == null) {
                log.error("User {} not found in app DB, importing from clients DB", userId);
                importUserFromClients(userId);
                return;
            }

            ObjectNode user = (ObjectNode) objectMapper.readTree(userJson);

            if (username != null) user.put("username", username);
            if (role != null) user.put("role", role);

            tpcmWebClient.put()
                    .uri("/api/app/users/{id}", userId)
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> log.info("User {} updated in app DB", userId))
                    .doOnError(error -> log.error("Failed to update user {} in app DB: {}", userId, error.getMessage()))
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("User {} not found in app DB", userId);
            importUserFromClients(userId);
        } catch (Exception e) {
            log.error("Exception updating user {} in app DB: {}", userId, e.getMessage());
        }
    }

    public void deleteUser(Long userId) {
        try {
            tpcmWebClient.delete()
                    .uri("/api/app/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnSuccess(response -> log.info("User {} deleted from app DB", userId))
                    .doOnError(error -> log.error("Failed to delete user {} from app DB: {}", userId, error.getMessage()))
                    .block();
        } catch (Exception e) {
            log.error("Exception deleting user {} from app DB: {}", userId, e.getMessage());
        }
    }

    public void importCustomerFromClients(Long customerId) {
        try {
            String clientsCustomerJson = tpcmWebClient.get()
                    .uri("/api/clients/customers/{id}", customerId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (clientsCustomerJson == null) {
                log.error("Customer {} not found in clients DB", customerId);
                return;
            }

            ObjectNode clientsCustomer = (ObjectNode) objectMapper.readTree(clientsCustomerJson);

            ObjectNode appCustomer = objectMapper.createObjectNode();
            appCustomer.put("name", clientsCustomer.get("name").asText());
            appCustomer.put("type", clientsCustomer.get("type").asText());
            appCustomer.put("billCycleDay", clientsCustomer.get("billCycleDay").asInt());
            appCustomer.put("nrSubscribers", clientsCustomer.has("nrSubscribers") ? clientsCustomer.get("nrSubscribers").asInt() : 0);

            String response = tpcmWebClient.post()
                    .uri("/api/app/customers")
                    .bodyValue(appCustomer)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectNode createdCustomer = (ObjectNode) objectMapper.readTree(response);
            Long appCustomerId = createdCustomer.get("customerID").asLong();

            log.info("Customer {} from clients DB imported to app DB with ID {}", customerId, appCustomerId);

        } catch (WebClientResponseException.NotFound e) {
            log.error("Customer {} not found in clients DB", customerId);
        } catch (Exception e) {
            log.error("Exception importing customer {} from clients DB: {}", customerId, e.getMessage());
        }
    }

    public void importSubscriberFromClients(Long subscriberId) {
        try {
            String clientsSubscriberJson = tpcmWebClient.get()
                    .uri("/api/clients/subscribers/{id}", subscriberId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (clientsSubscriberJson == null) {
                log.error("Subscriber {} not found in clients DB", subscriberId);
                return;
            }

            ObjectNode clientsSubscriber = (ObjectNode) objectMapper.readTree(clientsSubscriberJson);
            Long customerID = clientsSubscriber.get("customer").get("customerID").asLong();

            try {
                tpcmWebClient.get()
                        .uri("/api/app/customers/{id}", customerID)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } catch (WebClientResponseException.NotFound e) {
                log.info("Customer {} not found, importing first", customerID);
                importCustomerFromClients(customerID);
            }

            ObjectNode appSubscriber = objectMapper.createObjectNode();
            appSubscriber.put("msisdn", clientsSubscriber.get("msisdn").asText());
            appSubscriber.put("status", clientsSubscriber.get("status").asText());
            appSubscriber.put("subscriptionType", clientsSubscriber.get("subscriptionType").asText());

            ObjectNode customer = objectMapper.createObjectNode();
            customer.put("customerID", customerID);
            appSubscriber.set("customer", customer);

            String response = tpcmWebClient.post()
                    .uri("/api/app/subscribers")
                    .bodyValue(appSubscriber)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectNode createdSubscriber = (ObjectNode) objectMapper.readTree(response);
            Long appSubscriberId = createdSubscriber.get("subscriberID").asLong();

            log.info("Subscriber {} from clients DB imported to app DB with ID {}", subscriberId, appSubscriberId);

            createDefaultLimitForSubscriber(appSubscriberId);
            updateCustomerSubscriberCount(customerID);

        } catch (Exception e) {
            log.error("Exception importing subscriber {} from clients DB: {}", subscriberId, e.getMessage());
        }
    }

    public void importUserFromClients(Long userId) {
        try {
            String clientsUserJson = tpcmWebClient.get()
                    .uri("/api/clients/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (clientsUserJson == null) {
                log.error("User {} not found in clients DB", userId);
                return;
            }

            ObjectNode clientsUser = (ObjectNode) objectMapper.readTree(clientsUserJson);

            ObjectNode appUser = objectMapper.createObjectNode();
            appUser.put("username", clientsUser.get("username").asText());
            appUser.put("role", clientsUser.get("role").asText());

            String response = tpcmWebClient.post()
                    .uri("/api/app/users")
                    .bodyValue(appUser)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectNode createdUser = (ObjectNode) objectMapper.readTree(response);
            Long appUserId = createdUser.get("userID").asLong();

            log.info("User {} from clients DB imported to app DB with ID {}", userId, appUserId);

        } catch (Exception e) {
            log.error("Exception importing user {} from clients DB: {}", userId, e.getMessage());
        }
    }

    private void createDefaultLimitForSubscriber(Long subscriberId) {
        try {
            try {
                tpcmWebClient.get()
                        .uri("/api/app/limits/{subscriberId}", subscriberId)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                log.info("Limit already exists for subscriber {}", subscriberId);
                return;
            } catch (WebClientResponseException.NotFound e) {
                log.info("No existing limit for subscriber {}, creating default", subscriberId);
            }

            ObjectNode limit = objectMapper.createObjectNode();

            ObjectNode subscriber = objectMapper.createObjectNode();
            subscriber.put("subscriberID", subscriberId);
            limit.set("subscriber", subscriber);

            limit.put("consumedAmount", 0.0);
            limit.put("maxAmountCycle", 300);
            limit.put("maxAmountTransaction", 50);
            limit.put("lastReset", System.currentTimeMillis());

            log.info("Creating limit with payload: {}", limit.toString());

            String response = tpcmWebClient.post()
                    .uri("/api/app/limits")
                    .bodyValue(limit)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Default limit created for subscriber {}", subscriberId);

        } catch (WebClientResponseException e) {
            log.error("Exception creating default limit for subscriber {}: {} - Response: {}",
                    subscriberId, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Exception creating default limit for subscriber {}: {}", subscriberId, e.getMessage(), e);
        }
    }

    private void updateCustomerSubscriberCount(Long customerId) {
        try {
            log.info("Fetching subscriber count for customer {}", customerId);

            String countJson = tpcmWebClient.get()
                    .uri("/api/app/subscribers/count?customerId={customerId}", customerId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Received count: {} for customer {}", countJson, customerId);

            if (countJson != null) {
                int count = Integer.parseInt(countJson);
                log.info("Updating customer {} with subscriber count {}", customerId, count);
                updateCustomer(customerId, null, null, null, count);
            }
        } catch (WebClientResponseException e) {
            log.error("Exception updating subscriber count for customer {}: {} - Response: {}",
                    customerId, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Exception updating subscriber count for customer {}: {}", customerId, e.getMessage(), e);
        }
    }
}