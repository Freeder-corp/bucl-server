package com.freeder.buclserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableFeignClients
@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class BuclServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuclServerApplication.class, args);
	}

}
