package com.sportpulse.ms_fixtures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class MsFixturesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsFixturesApplication.class, args);
	}

}
