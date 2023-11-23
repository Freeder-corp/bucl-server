package com.freeder.buclserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BuclServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuclServerApplication.class, args);
	}

}
