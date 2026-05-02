package com.sportpulse.ms_leagues;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MsLeaguesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsLeaguesApplication.class, args);
	}

}
