package com.reco.emailservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HealthController{

    @GetMapping("/health")
	public String healthCheck() {
		return "Email Service is up and running!";
	}
}