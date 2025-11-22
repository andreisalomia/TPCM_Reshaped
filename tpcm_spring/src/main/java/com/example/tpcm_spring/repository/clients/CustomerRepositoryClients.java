package com.example.tpcm_spring.repository.clients;

import com.example.tpcm_spring.models.clients.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepositoryClients extends JpaRepository<Customer, Long> {
    List<Customer> findByNameIgnoreCase(String name);

    List<Customer> findByTypeIgnoreCase(String type);

    List<Customer> findByBillCycleDay(Integer billCycleDay);
}
