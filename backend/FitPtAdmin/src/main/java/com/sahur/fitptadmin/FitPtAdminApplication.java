package com.sahur.fitptadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class FitPtAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitPtAdminApplication.class, args);
    }

}
