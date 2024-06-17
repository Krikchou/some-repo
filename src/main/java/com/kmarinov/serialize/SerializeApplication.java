package com.kmarinov.serialize;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@EnableWebMvc
@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties
public class SerializeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SerializeApplication.class, args);
	}

}
