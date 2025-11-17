package com.example.tpcmscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TpcmSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TpcmSchedulerApplication.class, args);
    }

}
