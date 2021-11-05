package com.loumarrionutr40.mftoken.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MFTokenNotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MFTokenNotifyApplication.class, args);
    }

}
