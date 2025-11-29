package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.SetBillDay;
import com.example.kafkaconsumer.service.TpcmApiService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SetBillDayProcessor extends AbstractMessageProcessor<SetBillDay> {

    private final TpcmApiService tpcmApiService;

    @Override
    @Counted("processSetBillDay")
    @Timed("processSetBillDay")
    public void process(SetBillDay setBillDay) {
        log.info("Processing SET_BILL_DAY for customer ID: {}", setBillDay.getCustomerID());

        tpcmApiService.updateCustomer(
                setBillDay.getCustomerID(),
                null,
                null,
                setBillDay.getBillCycleDay(),
                null
        );

        log.info("Successfully processed SET_BILL_DAY for customer ID: {}", setBillDay.getCustomerID());
    }
}