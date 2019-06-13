package com.cre.drachenbasis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DrachenbasisApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrachenbasisApplication.class, args);
	}
}
