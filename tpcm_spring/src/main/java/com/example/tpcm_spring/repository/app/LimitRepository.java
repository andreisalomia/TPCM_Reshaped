package com.example.tpcm_spring.repository.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.example.tpcm_spring.models.app.Limit;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {

    @Query("SELECT l FROM Limit l WHERE l.subscriber.subscriberID = :subscriberID")
    Limit findBySubscriberID(@Param("subscriberID") Long subscriberID);
}
