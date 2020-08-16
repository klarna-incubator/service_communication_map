package com.klarna.secoma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.klarna.secoma.config")
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
