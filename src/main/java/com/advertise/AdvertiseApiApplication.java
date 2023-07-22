package com.advertise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
//@OpenAPIDefinition
public class AdvertiseApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdvertiseApiApplication.class, args);
	}

}
