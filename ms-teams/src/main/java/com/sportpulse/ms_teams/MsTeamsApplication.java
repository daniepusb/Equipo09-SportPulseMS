package com.sportpulse.ms_teams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MsTeamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTeamsApplication.class, args);
	}

}
