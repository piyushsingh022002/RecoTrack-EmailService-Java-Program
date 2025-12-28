package com.reco.emailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.reco.emailservice.model.User;
import com.reco.emailservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EmailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner testMongo(UserRepository userRepository) {
		return args -> {
			User u = newUser("1","Test User", "test@example.com");
			userRepository.save(u);
			System.out.println("saved" + u.getId());
		};
	}

	private User newUser(String id, String name, String email) {
		return new User(id, email, name);
	}

}
