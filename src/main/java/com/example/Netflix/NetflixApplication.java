package com.example.Netflix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class NetflixApplication {
	@Autowired
	private PasswordEncoder passwordEncoder;



	public static void main(String[] args) {
		SpringApplication.run(NetflixApplication.class, args);
	}

}
