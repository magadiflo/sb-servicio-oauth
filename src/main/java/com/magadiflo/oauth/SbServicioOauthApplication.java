package com.magadiflo.oauth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class SbServicioOauthApplication implements CommandLineRunner {
	
	private final PasswordEncoder passwordEncoder;
	
	public SbServicioOauthApplication(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;		
	}	

	public static void main(String[] args) {
		SpringApplication.run(SbServicioOauthApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String password = "12345";
		for(int i = 0; i < 4; i++) {
			String passwordBcrypt = this.passwordEncoder.encode(password);
			System.out.println(passwordBcrypt);
		}
	}

}
