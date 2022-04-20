package com.tournabay.api;

import com.tournabay.api.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableConfigurationProperties(AppProperties.class)
public class TournabayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TournabayApplication.class, args);
    }

    // return number between 50 and 100
    public static int getRandomNumber() {
        return (int) (Math.random() * 50) + 50;
    }

}
