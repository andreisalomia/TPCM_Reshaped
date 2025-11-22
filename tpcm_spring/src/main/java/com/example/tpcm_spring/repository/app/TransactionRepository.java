package com.example.tpcm_spring.repository.app;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tpcm_spring.models.app.Transaction;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySubscriber_SubscriberID(Long subscriberID);

    List<Transaction> findByCreatedDateBetween(Timestamp from, Timestamp to);

    List<Transaction> findByThirdParty_ThirdPartyID(Long thirdPartyID);

    List<Transaction> findByMsisdn(String msisdn);
}
