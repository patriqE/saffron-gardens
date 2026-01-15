package com.saffrongardens.saffron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SaffronApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaffronApplication.class, args);
	}

}
