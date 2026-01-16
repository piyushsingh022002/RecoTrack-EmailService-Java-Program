# OpenAPI 3.0 Documentation - Email Service API

## Overview

This document describes the comprehensive OpenAPI 3.0 documentation implementation for the RecoTrack Email Service API using **springdoc-openapi-starter-webmvc-ui**.

### Key Features

✅ **OpenAPI 3.0 Specification** - Full compliance with OpenAPI 3.0 standard  
✅ **Swagger UI** - Interactive API documentation at `/swagger-ui.html`  
✅ **OpenAPI JSON Spec** - Machine-readable spec at `/v3/api-docs`  
✅ **Multiple Security Schemes** - User JWT + Service Token authentication  
✅ **Flexible Authentication** - Support for different token combinations per endpoint  
✅ **Rich Annotations** - Comprehensive @Operation, @SecurityRequirement, @Tag annotations  
✅ **Best Practices** - Clean, reusable, generic configuration (no hardcoded tokens)  

---

## Access Points

| Endpoint | Purpose | Format |
|----------|---------|--------|
| `http://localhost:8080/swagger-ui.html` | Interactive API documentation | HTML UI |
| `http://localhost:8080/v3/api-docs` | Machine-readable OpenAPI spec | JSON |
| `http://localhost:8080/v3/api-docs.yaml` | Machine-readable OpenAPI spec | YAML |

---

## Security Schemes

### 1. User JWT Token (HTTP Bearer)

**Type:** HTTP Bearer Authentication  
**Header:** `Authorization`  
**Format:** `Authorization: Bearer <jwt-token>`  
**Use Case:** User-initiated operations (e.g., sending emails as authenticated user)  
**Scope:** `SCOPE_EMAIL_SEND`

#### Example
```bash
curl -X POST http://localhost:8080/api/email/send \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{"actionId":"action-12345"}'
```

---

### 2. Service Token (API Key)

**Type:** API Key in Header  
**Header:** `X-Service-Token`  
**Format:** `X-Service-Token: <service-token>`  
**Use Case:** Service-to-service communication  
**Scope:** `SERVICE_EMAIL_SEND`, `SERVICE_EMAIL_CRITICAL`

#### Example
```bash
curl -X POST http://localhost:8080/api/email/send/service \
  -H "X-Service-Token: service-token-abc123" \
  -H "Content-Type: application/json" \
  -d '{"actionId":"action-12345"}'
```

---

## Endpoints

### 1. Send Email (User JWT Only)

```
POST /api/email/send
```

**Security:** User JWT Required  
**Scope:** `SCOPE_EMAIL_SEND`

**Authentication:** User must have valid JWT token in Authorization header

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/email/send \
  -H "Authorization: Bearer <jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "actionId": "action-12345"
  }'
```

**Response:**
- `200 OK` - Email sent successfully
- `400 Bad Request` - Invalid request payload
- `401 Unauthorized` - Missing or invalid JWT token
- `403 Forbidden` - Insufficient permissions for EMAIL_SEND scope

---

### 2. Send Email (Service Token Only)

```
POST /api/email/send/service
```

**Security:** Service Token Required  
**Scope:** `SERVICE_EMAIL_SEND`

**Authentication:** Service must provide valid X-Service-Token header

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/email/send/service \
  -H "X-Service-Token: service-token-abc123" \
  -H "Content-Type: application/json" \
  -d '{
    "actionId": "action-12345"
  }'
```

**Response:**
- `200 OK` - Email sent successfully
- `400 Bad Request` - Invalid request payload
- `401 Unauthorized` - Missing or invalid service token
- `403 Forbidden` - Service not authorized

---

### 3. Send Critical Email (Both JWT + Service Token)

```
POST /api/email/send/critical
```

**Security:** User JWT AND Service Token Required  
**Scopes:** `SCOPE_EMAIL_SEND` AND `SERVICE_EMAIL_CRITICAL`

**Authentication:** Both JWT and Service Token required in headers

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/email/send/critical \
  -H "Authorization: Bearer <jwt-token>" \
  -H "X-Service-Token: service-token-xyz789" \
  -H "Content-Type: application/json" \
  -d '{
    "actionId": "action-12345"
  }'
```

**Response:**
- `202 Accepted` - Email accepted for processing
- `400 Bad Request` - Invalid request payload
- `401 Unauthorized` - Missing or invalid credentials
- `403 Forbidden` - Insufficient permissions

---

### 4. Verify Email (Public)

```
GET /api/email/verify/{emailId}
```

**Security:** None (Public endpoint)

**Authentication:** Not required

**Example Request:**
```bash
curl -X GET http://localhost:8080/api/email/verify/email-12345
```

**Response:**
- `200 OK` - Email verified successfully
- `404 Not Found` - Email not found

---

### 5. Health Check (Public)

```
GET /health
```

**Security:** None (Public endpoint)

**Example Request:**
```bash
curl -X GET http://localhost:8080/health
```

**Response:**
```
Email Service is up and running!
```

---

### 6. Health Status (Public)

```
GET /health/status
```

**Security:** None (Public endpoint)

**Example Request:**
```bash
curl -X GET http://localhost:8080/health/status
```

**Response:**
```
Email Service Status: Operational | Version: 1.0.0 | Ready to accept requests
```

---

## Configuration

### OpenApiConfig

Location: `src/main/java/com/reco/emailservice/config/OpenApiConfig.java`

**Key Components:**

1. **Security Scheme Definitions**
   - UserJWT: HTTP Bearer with JWT format
   - ServiceToken: API Key in X-Service-Token header

2. **API Information**
   - Title: "RecoTrack Email Service API"
   - Version: "1.0.0"
   - Contact: RecoTrack Team
   - License: Apache 2.0

3. **Server Configuration**
   - Development: `http://localhost:8080`

4. **Security Constants** (used in annotations)
   ```java
   public static final String SECURITY_SCHEME_USER_JWT = "UserJWT";
   public static final String SECURITY_SCHEME_SERVICE_TOKEN = "ServiceToken";
   ```

---

## Using Swagger UI

### 1. Access Swagger UI

Open browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

### 2. Add JWT Token

1. Click the **Authorize** button (lock icon) at top-right
2. In the "UserJWT" section, enter your JWT token:
   ```
   Bearer <your-jwt-token>
   ```
3. Click **Authorize**
4. Click **Close**

### 3. Add Service Token

1. Click the **Authorize** button again
2. In the "ServiceToken" section, enter your service token:
   ```
   service-token-abc123
   ```
3. Click **Authorize**
4. Click **Close**

### 4. Execute Requests

Now you can execute API requests directly from Swagger UI. The tokens will be automatically included in the request headers.

---

## Request/Response Examples

### Email Action Request

**Model:** `EmailActionRequest`

```json
{
  "actionId": "action-12345"
}
```

**Validation Rules:**
- `actionId` is required (must not be blank)
- Must be a valid email action identifier

---

## Code Examples

### Java Client Example

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/api/email/send";

// Create request body
EmailActionRequest request = new EmailActionRequest("action-12345");

// Create headers with JWT
HttpHeaders headers = new HttpHeaders();
headers.set("Authorization", "Bearer " + jwtToken);
headers.set("Content-Type", "application/json");

// Send request
HttpEntity<EmailActionRequest> entity = new HttpEntity<>(request, headers);
restTemplate.postForEntity(url, entity, Void.class);
```

### cURL Example

```bash
# Send email with User JWT
curl -X POST http://localhost:8080/api/email/send \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{"actionId":"action-12345"}'

# Send email with Service Token
curl -X POST http://localhost:8080/api/email/send/service \
  -H "X-Service-Token: service-token-abc123" \
  -H "Content-Type: application/json" \
  -d '{"actionId":"action-12345"}'

# Send critical email with both tokens
curl -X POST http://localhost:8080/api/email/send/critical \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "X-Service-Token: service-token-xyz789" \
  -H "Content-Type: application/json" \
  -d '{"actionId":"action-12345"}'
```

---

## Implementation Details

### Annotations Used

#### Class-Level
- `@RestController` - Marks class as REST controller
- `@RequestMapping` - Base path for all endpoints in controller
- `@Tag` - Groups endpoints in Swagger UI

#### Method-Level
- `@Operation` - Describes endpoint operation
- `@PostMapping`, `@GetMapping` - HTTP method and path
- `@SecurityRequirement` - Specifies required security schemes
- `@ApiResponse`, `@ApiResponses` - Describes possible responses
- `@PreAuthorize` - Method-level security

#### Parameter-Level
- `@RequestBody` - Request body parameter
- `@RequestHeader` - Header parameter
- `@PathVariable` - Path variable parameter
- `@Parameter` - Parameter documentation
- `@Valid` - Validation annotation

#### Model-Level
- `@Schema` - Describes model structure
- `@NotBlank` - Validation constraint

---

## Security Best Practices

### Implemented

✅ **No Hardcoded Tokens** - Configuration is generic and reusable  
✅ **HTTP Bearer Format** - Standard JWT format with "Bearer" prefix  
✅ **API Key in Header** - Service tokens in custom header  
✅ **Clear Descriptions** - Security schemes documented in OpenAPI spec  
✅ **Flexible Authentication** - Support for different token combinations  
✅ **Method-Level Security** - @PreAuthorize for fine-grained control  
✅ **HTTPS Ready** - Configuration supports production SSL/TLS  
✅ **Token Validation** - JwtValidator validates all tokens  

### Recommended

- Use HTTPS in production
- Implement token rotation
- Add rate limiting for API endpoints
- Log authentication failures (not tokens)
- Use strong signing keys for JWT

---

## Configuration Files

### pom.xml

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### application.yml

Optional configuration for customization:

```yaml
springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
```

---

## Testing

### Test Endpoints with Valid Tokens

```bash
# Get sample JWT and Service Token first
export JWT_TOKEN="your-jwt-token-here"
export SERVICE_TOKEN="your-service-token-here"

# Test User JWT endpoint
curl -X POST http://localhost:8080/api/email/send \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"actionId":"test-action-001"}'

# Test Service Token endpoint
curl -X POST http://localhost:8080/api/email/send/service \
  -H "X-Service-Token: $SERVICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"actionId":"test-action-001"}'

# Test both tokens endpoint
curl -X POST http://localhost:8080/api/email/send/critical \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "X-Service-Token: $SERVICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"actionId":"test-action-001"}'
```

---

## Troubleshooting

### Issue: Swagger UI not loading

**Solution:** Ensure springdoc-openapi-starter-webmvc-ui is in pom.xml and server is running

### Issue: "Missing or invalid JWT token"

**Solution:** Provide a valid JWT token in Authorization header or use /health endpoint

### Issue: "Invalid Service token"

**Solution:** Provide a valid service token in X-Service-Token header

### Issue: @SecurityRequirement not working

**Solution:** Verify security scheme names match exactly with constants in OpenApiConfig

---

## References

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Spring Security](https://spring.io/projects/spring-security)

---

## Summary

This implementation provides a **production-ready, fully-documented Email Service API** with:

- ✅ Complete OpenAPI 3.0 specification
- ✅ Interactive Swagger UI for testing
- ✅ Multiple flexible authentication schemes
- ✅ Clear endpoint documentation
- ✅ Best practices for security and configuration
- ✅ Examples for all authentication scenarios
- ✅ Ready for development and production environments

The configuration is **generic and reusable**, with no hardcoded values, making it easy to adapt for other services and environments.
