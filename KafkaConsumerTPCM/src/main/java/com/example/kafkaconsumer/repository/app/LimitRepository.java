package com.example.kafkaconsumer.repository.app;

import com.example.kafkaconsumer.model.app.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("appLimitRepository")
public interface LimitRepository extends JpaRepository<Limit, Long> {

    Limit findBySubscriberID(@Param("subscriberID") Long subscriberID);
}