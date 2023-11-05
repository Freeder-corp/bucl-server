package com.freeder.buclserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BuclServerApplication {
	private String ai, bi;

	public static void main(String[] args) {
		SpringApplication.run(BuclServerApplication.class, args);
	}

}
