package com.hanaset.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@EnableScheduling
@Slf4j
@SpringBootApplication
public class SkyApplication {

    private final Environment environment;

    @Autowired
    public SkyApplication(Environment environment) { this.environment = environment; }

    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReadyEvent() {
        log.info("applicationReady, profiles = {}",
                Arrays.toString(environment.getActiveProfiles()));
    }

}
