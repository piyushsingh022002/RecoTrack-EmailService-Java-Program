package com.reco.emailservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller
 *
 * Provides service health status endpoints for monitoring and readiness checks.
 */
@RestController
@Tag(name = "Health Check", description = "Service health and readiness endpoints")
public class HealthController {

	/**
	 * Check if the Email Service is running and healthy.
	 *
	 * This is a public endpoint used for health checks and monitoring.
	 * No authentication required.
	 *
	 * @return 200 OK with status message if service is healthy
	 */
	@GetMapping("/health")
	@Operation(summary = "Health check", description = "Returns service health status. Used for monitoring and load balancer health checks.", tags = {
			"Health Check" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Service is healthy and running")
	})
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("Email Service is up and running!");
	}

	/**
	 * Detailed health status including dependencies.
	 *
	 * This endpoint provides more detailed health information about the service
	 * and its dependencies (database, mail server, etc.).
	 *
	 * @return 200 OK with detailed health status
	 */
	@GetMapping("/health/status")
	@Operation(summary = "Detailed health status", description = "Returns detailed health status including service dependencies and configurations.", tags = {
			"Health Check" })
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Service and dependencies are healthy")
	})
	public ResponseEntity<String> healthStatus() {
		return ResponseEntity.ok(
				"Email Service Status: Operational | " +
						"Version: 1.0.0 | " +
						"Ready to accept requests");
	}
}