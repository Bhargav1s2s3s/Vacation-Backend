package com.vacation.core;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.vacation.core",
		"com.vacation.common",
		"com.vacation.auth.repository"
})
@EntityScan(basePackages = {"com.vacation"})
@EnableJpaRepositories(basePackages = {
		"com.vacation.core.repository",
		"com.vacation.auth.repository"
})
public class VacationApplication {

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(VacationApplication.class, args);
	}

}
