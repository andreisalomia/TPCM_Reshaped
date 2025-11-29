package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CloseUser;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloseUserProcessor extends AbstractMessageProcessor<CloseUser> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processCloseUser")
    @Timed("processCloseUser")
    public void process(CloseUser closeUser) {
        log.info("Processing CLOSE_USER for user ID: {}", closeUser.getUserID());

        tpcmApiService.deleteUser(closeUser.getUserID());

        log.info("Successfully processed CLOSE_USER for user ID: {}", closeUser.getUserID());
    }
}