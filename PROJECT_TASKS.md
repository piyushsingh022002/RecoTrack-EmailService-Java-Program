## MVP Scope Definition

The following sections are REQUIRED for the initial production release:
- Architecture & Design
- API Design & Contracts
- Controller Layer
- Service Layer
- Email Template & Action Strategy
- Validation & Error Handling
- Persistence & Database Concerns
- Configuration Management
- Security & Abuse Prevention (basic)
- Logging (basic)
- Testing (unit + basic integration)
- Deployment (Render-specific)
- Documentation
- Code Quality & Maintainability
- Cleanup & Technical Debt

Sections marked **Future Enhancements (Optional)** must NOT block release.

## Architecture & Design
- [ ] **Define overall topology and components**  
  Describe service boundaries, integrations (MongoDB, email provider, Render), and runtime constraints to ensure readiness for production deployment and ease future onboarding.  
  *Why:* Sets a shared understanding and prevents architectural drift in a distributed system.  
  *Suggested:* Use C4 diagrams and a component overview in Markdown.
- [ ] **Document non-functional requirements**  
  Capture availability, scalability, latency, throughput, and reliability targets, including Render imposed limits.  
  *Why:* Forces design decisions around scaling strategies and SLAs.
- [ ] **Establish environment strategy**  
  Define dev/test/prod environment separation, branching strategy, and Render service tiers.  
  *Why:* Ensures predictable deployments and compliance with Render’s free tier limits.

## API Design & Contracts
- [ ] **Specify HTTP request/response contracts**  
  Define JSON schemas for request (userId, username, action) and structured response (status, message, metadata).  
  *Why:* Enables clear consumer expectations and test coverage; consider OpenAPI/Swagger spec.
- [ ] **Design meaningful status codes and error bodies**  
  Map success, validation, not-found, and delivery-failure scenarios to REST-friendly HTTP codes with machine-readable payloads.  
  *Why:* Helps clients react appropriately and aids monitoring.
- [ ] **Document API in OpenAPI**  
  Add an `openapi.yaml` or `springdoc` annotations for auto-generated docs.  
  *Why:* Improves discoverability and reduces integration time.

## Controller Layer
- [ ] **Implement `EmailController` with single POST endpoint**  
  Accept request payload, delegate to service, and return structured response with HTTP code.  
  *Why:* Ensures single entry point for API aligned to contract.
- [ ] **Add request/response DTOs and use `@Validated`**  
  Separate inbound/outbound payload models and annotate controller with `@Validated` and `@RequestBody`.  
  *Why:* Keeps controller focused and prevents leaking entity models.
- [ ] **Document controller behavior with Swagger annotations**  
  Add `@Operation`, `@ApiResponse` etc.  
  *Why:* Provides clarity for API consumers and auto docs.

## Service Layer
- [ ] **Create `EmailService` interface and impl**  
  Handle orchestration: fetch user, decide template, call email sender, return response.  
  *Why:* Enforces separation of concerns and makes unit testing easy.
- [ ] **Add action-to-handler strategy**  
  Implement `ActionStrategy` map to handle behaviors per action (e.g., passwordReset, welcome).  
  *Why:* Facilitates future actions without controller changes.
- [ ] **Introduce `EmailSender` abstraction**  
  Wraps provider SDK/SMTP to allow mocking and swapping providers.  
  *Why:* Enables extensibility and decouples provider-specific concerns.

## Email Template & Action Strategy
- [ ] **Define template registry**  
  Maintain templates for actions (`welcome`, `reset`, `notification`) using Thymeleaf, FreeMarker, or simple text with placeholders.  
  *Why:* Keeps content consistent and manageable for marketing/legal teams.
- [ ] **Create `EmailContent` builder**  
  Generate subject/body dynamically based on user data, action, and optional metadata.  
  *Why:* Ensures personalization and clarity in output.
- [ ] **Add localization placeholders**  
  Prepare template system for multi-lingual content even if single language today.  
  *Why:* Future proofs service for global users.

## Validation & Error Handling
- [ ] **Add bean validation for request DTO**  
  Annotate fields (`@NotBlank`, `@Email`) and enforce in controller.  
  *Why:* Prevents bad data reaching service layer.
- [ ] **Implement centralized exception handling**  
  Use `@ControllerAdvice` to map custom exceptions (Validation, NotFound, DeliveryFailure) to response payloads and status codes.  
  *Why:* Keeps controller code clean and ensures consistent client experience.
- [ ] **Define custom exceptions for domain faults**  
  Introduce `UserNotFoundException`, `UnsupportedActionException`, `EmailDeliveryException`, etc.  
  *Why:* Enables meaningful logs and responses.
- [ ] **Add retry/backoff for transient email failures**  
  Use Spring Retry or resilience4j around email provider calls with circuit-breakers.  
  *Why:* Improves resilience when provider has intermittent issues.

## Persistence & Database Concerns
- [ ] **Add unique constraints/indexes**  
  Ensure Mongo collection enforces uniqueness on `email` and `userId`.  
  *Why:* Guarantees data integrity and query performance.
- [ ] **Introduce repository layer DTOs/Projections**  
  Return only required fields (email, name) to avoid overfetching.  
  *Why:* Limits payloads and prepares for future schema evolution.
- [ ] **Add integration test for Mongo repository**  
  Use `@DataMongoTest` with embedded Mongo (e.g., Testcontainers/Flapdoodle).  
  *Why:* Validates repository behavior in isolation.

## Configuration Management
- [ ] **Externalize all configs via Spring `@ConfigurationProperties`**  
  Group Mongo, email provider, Render settings, timeouts.  
  *Why:* Simplifies management and documentation for different environments.
- [ ] **Add environment-specific YAML/profiles**  
  Provide `application-dev.yml`, `application-prod.yml` and `spring.profiles.active`.  
  *Why:* Supports local vs Render configurations without code changes.
- [ ] **Validate required env vars on startup**  
  Fail fast if `MONGODB_URI`, email provider credentials are missing.  
  *Why:* Prevents slow deployments with hidden config issues.

## Security & Abuse Prevention
- [ ] **Authenticate external callers**  
  Wire up API key authentication or OAuth guard for endpoints.  
  *Why:* Protects against unauthorized use of email sending.
- [ ] **Authorize actions per user**  
  Ensure a caller can only request actions for their own userId.  
  *Why:* Prevents cross-tenant abuse and data leaks.
- [ ] **Rate-limit email endpoint**  
  Use Spring Cloud Gateway, rate limiter, or bucket algorithm to throttle requests.  
  *Why:* Protects from spam/DoS and provider abuse.
- [ ] **Sanitize inputs before templating**  
  Escape/validate user data when building email bodies.  
  *Why:* Prevents injection attacks via user-controlled values.
- [ ] **Add API key authentication filter**
  Implement simple API-key auth using Spring Security filter chain.
  *Why:* Protects endpoint from public abuse.
- [ ] **Externalize API keys via environment variables**
  Load allowed keys from env or config.
  *Why:* Prevents hardcoded secrets.


## Logging & Observability
- [ ] **Add structured logging (e.g., MDC)**  
  Log request IDs, userId, action, duration using `Logback`/SLF4J with JSON encoder.  
  *Why:* Enables tracing and debugging in Render logs.
- [ ] **Emit metrics for email delivery**  
  Track success, failures, duration via Micrometer (Prometheus/Graphite).  
  *Why:* Monitors SLA and helps ops detect issues.
- [ ] **Implement distributed tracing keys**  
  Propagate headers (traceparent) and hook into Zipkin/OpenTelemetry.  
  *Why:* Diagnostics across Render services.
- [ ] **Enrich logs on exceptions**  
  Capture stack trace, action, and sanitized payload for every failure.  
  *Why:* Speeds root cause analysis.

## Testing (Unit, Integration, Contract)
- [ ] **Unit test service and strategies**  
  Mock repository/provider to verify business logic per action.  
  *Why:* Ensures expected behavior before integration.
- [ ] **Integration tests for REST endpoint**  
  Use `@SpringBootTest` with `MockMvc` or `WebTestClient` to cover end-to-end flow.  
  *Why:* Validates serialization, validation, controller wiring.
- [ ] **Contract tests for email provider**  
  Use Pact or provider mocks to ensure payloads meet provider expectations.  
  *Why:* Prevents breaking API changes when provider upgrades.
- [ ] **Add regression tests for failure scenarios**  
  Cover user not found, template missing, email send failure.  
  *Why:* Protects critical error handling.

## Performance & Optimization
- [ ] **Benchmark template rendering and email sending**  
  Measure latency using `Spring Boot actuator` or custom timer.  
  *Why:* Ensures Render free tier CPU/memory limits are met.
- [ ] **Optimize DB queries with projections/indexes**  
  Only fetch necessary fields and ensure indexes exist.  
  *Why:* Minimizes latency and Mongo resource usage.
- [ ] **Configure connection pooling/timeouts**  
  Tune Mongo client and email provider HTTP clients for idle time and keepalives.  
  *Why:* Prevents blocked threads under load.
- [ ] **Enable asynchronous email dispatch**  
  Offload provider call to `@Async` or message queue with confirmation path.  
  *Why:* Improves API responsiveness and throughput.

## CI/CD Enhancements
- [ ] **Add Maven quality gates**  
  Run `mvn test`, `spotless:check`, and `jacoco` in pipeline.  
  *Why:* Keeps code healthy before deployment.
- [ ] **Publish code coverage report**  
  Upload to GitHub Actions artifacts or codecov.  
  *Why:* Tracks test coverage over time.
- [ ] **Automate Docker image build and push**  
  Extend workflow to build multi-stage image and push to GitHub Container Registry.  
  *Why:* Prepares artifacts for Render deployment.
- [ ] **Add integration smoke tests**  
  Trigger lightweight REST call post-build to verify service boots.  
  *Why:* Catches regressions before release.

## Deployment (Render-specific)
- [ ] **Document Render service setup**  
  Outline steps to connect repo, set env vars, and configure `Dockerfile` or `Build Command`.  
  *Why:* Speeds deployments for team members.  
- [ ] **Configure Render health checks and env**  
  Point to `/health`, set `MONGODB_URI`, email credentials, and Render secrets.  
  *Why:* Aligns platform config with app needs.
- [ ] **Enable Render auto-deploy from main**  
  Ensure Render deploys when main updates and rolls back on failure.  
  *Why:* Keeps production fresh and safe.
- [ ] **Set resource limits and scaling policy**  
  Configure Render free tier instance size, concurrency, and auto-sleep settings.  
  *Why:* Avoid overconsumption and manage cost.
- [ ] **Add Render logs/alerting stream**  
  Ensure logs accessible and set basic alerts (failed deploys, unhealthy).  
  *Why:* Maintains production observability.

## Documentation
- [ ] **Expand README with architecture overview**  
  Include diagram, endpoint info, configuration, and Render deployment steps.  
  *Why:* Improves developer onboarding.
- [ ] **Add `HELP.md` section for running/testing locally**  
  Describe Mongo mock/test setup, env var requirements.  
  *Why:* Provides quick reference.
- [ ] **Document email templates and action list**  
  Explain what each action does, placeholders, and approval workflows.  
  *Why:* Keeps compliance/marketing stakeholders aligned.
- [ ] **Write troubleshooting guide**  
  Cover common failures (Mongo connection, email provider, rate limits).  
  *Why:* Reduces mean time to recovery.

## Code Quality & Maintainability
- [ ] **Introduce static analysis**  
  Integrate `SpotBugs`, `Checkstyle`, or `PMD` via Maven.  
  *Why:* Detects anti-patterns early.
- [ ] **Standardize formatting (Spotless)**  
  Add consistent formatting plugin to enforce conventions.  
  *Why:* Improves readability across contributors.
- [ ] **Refactor to use DTOs/services**  
  Replace any business logic in controllers/application entry point with services/strategies.  
  *Why:* Improves testability and SRP adherence.
- [ ] **Add `@Slf4j` logging in services**  
  Use Lombok or manual loggers to emit structured logs with context.  
  *Why:* Enhances debugging without verbose boilerplate.

## Cleanup & Technical Debt
- [ ] **Remove unused starter code**  
  Delete placeholder classes left from Spring Initializr (if any).  
  *Why:* Keeps repo focused.
- [ ] **Audit dependencies**  
  Remove unused Maven dependencies and fix version ranges.  
  *Why:* Reduces attack surface and binary size.
- [ ] **Migrate property placeholders to constants**  
  Replace string literals in code with constants/enums for actions and statuses.  
  *Why:* Prevents hard-to-maintain code duplication.
- [ ] **Ensure `CommandLineRunner` only runs in dev**  
  Guard seeding logic behind `@Profile("dev")` so production stays clean.  
  *Why:* Prevents unexpected data writes.

## Future Enhancements (Optional)
- [ ] **Add templating UI/editor**  
  Provide admin endpoint for editing templates with approval workflow.  
  *Why:* Enables marketing to update content without deployments.
- [ ] **Support multiple email providers**  
  Implement provider `Chain` to failover between SMTP/API services.  
  *Why:* Increases delivery reliability.
- [ ] **Integrate with workflow queue**  
  Accept requests via messaging (Kafka/RabbitMQ) and process asynchronously.  
  *Why:* Improves throughput and acts as buffer for spikes.
- [ ] **Add analytics export**  
  Capture email send metadata and expose metrics via REST or streaming.  
  *Why:* Drives insights for product/ops.
