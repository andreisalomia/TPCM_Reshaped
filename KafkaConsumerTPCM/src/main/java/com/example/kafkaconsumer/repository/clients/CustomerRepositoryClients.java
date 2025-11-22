package com.example.kafkaconsumer.repository.clients;

import com.example.kafkaconsumer.model.clients.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("clientsCustomerRepository")
public interface CustomerRepositoryClients extends JpaRepository<Customer, Long> {
}