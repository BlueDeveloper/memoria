package com.brp.memoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class MemoriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoriaApplication.class, args);
    }
}
