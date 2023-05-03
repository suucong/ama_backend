package com.example.ama_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class AmaBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(AmaBackendApplication.class, args);
	}

}
