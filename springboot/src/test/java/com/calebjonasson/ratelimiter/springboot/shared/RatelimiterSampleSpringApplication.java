package com.calebjonasson.ratelimiter.springboot.shared;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class RatelimiterSampleSpringApplication {

	public static void main(String[] args) {
		new SpringApplication(RatelimiterSampleSpringApplication.class);
	}

}