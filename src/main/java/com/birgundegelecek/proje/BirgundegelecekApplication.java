package com.birgundegelecek.proje;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BirgundegelecekApplication {

	public static void main(String[] args) {
		SpringApplication.run(BirgundegelecekApplication.class, args);
	}
 //http://localhost:8080/index.html
}
