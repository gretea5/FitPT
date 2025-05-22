package com.sahur.fitpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FitPtApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitPtApplication.class, args);
    }

}
