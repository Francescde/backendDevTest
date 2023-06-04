package com.example.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SpringBootApplication
public class MyAppApplication {
	private static final Logger logger = LoggerFactory.getLogger(MyAppApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(MyAppApplication.class, args);

		logger.info("MyApp started");
	}

}
