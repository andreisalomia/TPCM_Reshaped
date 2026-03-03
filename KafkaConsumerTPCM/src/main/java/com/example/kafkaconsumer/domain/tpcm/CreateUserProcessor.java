package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CreateUser;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserProcessor extends AbstractMessageProcessor<CreateUser> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processCreateUser")
    @Timed("processCreateUser")
    public void process(CreateUser createUser) {
        log.info("Processing CREATE_USER for user ID: {}", createUser.getUserID());

        tpcmApiService.createUser(
                createUser.getUserID(),
                createUser.getUsername(),
                createUser.getRole()
        );

        log.info("Finished processing CREATE_USER for user ID: {}", createUser.getUserID());
    }
}