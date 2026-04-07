package com.vacation.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"com.vacation"})
@EntityScan(basePackages = {"com.vacation"})
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
