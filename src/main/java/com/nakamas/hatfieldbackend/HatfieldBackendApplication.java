package com.nakamas.hatfieldbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HatfieldBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HatfieldBackendApplication.class, args);
	}

}
