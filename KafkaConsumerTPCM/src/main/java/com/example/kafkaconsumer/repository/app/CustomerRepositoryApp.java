package com.example.kafkaconsumer.repository.app;

import com.example.kafkaconsumer.model.app.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("appCustomerRepository")
public interface CustomerRepositoryApp extends JpaRepository<Customer, Long> {
    List<Customer> findByNameIgnoreCase(String name);
    List<Customer> findByTypeIgnoreCase(String type);
    List<Customer> findByBillCycleDay(Integer billCycleDay);
}