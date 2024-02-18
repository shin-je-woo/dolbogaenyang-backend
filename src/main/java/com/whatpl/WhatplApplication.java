package com.whatpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WhatplApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhatplApplication.class, args);
	}

}
