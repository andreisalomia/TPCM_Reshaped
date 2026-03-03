package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.UpdateUser;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserProcessor extends AbstractMessageProcessor<UpdateUser> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processUpdateUser")
    @Timed("processUpdateUser")
    public void process(UpdateUser updateUser) {
        log.info("Processing UPDATE_USER for user ID: {}", updateUser.getUserID());

        tpcmApiService.updateUser(
                updateUser.getUserID(),
                updateUser.getUsername(),
                updateUser.getRole()
        );

        log.info("Finished processing UPDATE_USER for user ID: {}", updateUser.getUserID());
    }
}