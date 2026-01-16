# OpenAPI Implementation - Quick Reference

## Files Modified/Created

| File | Changes |
|------|---------|
| `src/main/java/com/reco/emailservice/config/OpenApiConfig.java` | ✅ Complete rewrite with comprehensive configuration |
| `src/main/java/com/reco/emailservice/controller/EmailController.java` | ✅ Added 4 endpoints with full OpenAPI annotations |
| `src/main/java/com/reco/emailservice/controller/HealthController.java` | ✅ Enhanced with OpenAPI annotations |
| `src/main/java/com/reco/emailservice/model/EmailActionRequest.java` | ✅ Added @Schema annotations |
| `src/main/java/com/reco/emailservice/service/EmailService.java` | ✅ Added 3 new methods with JavaDoc |
| `src/main/java/com/reco/emailservice/security/SecurityConfig.java` | ✅ Enhanced with better documentation |
| `src/main/java/com/reco/emailservice/security/JwtAuthenticationFilter.java` | ✅ Added support for X-Service-Token header |
| `OPENAPI_DOCUMENTATION.md` | ✅ NEW - Complete documentation |

---

## Access Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## Security Schemes in Swagger UI

### Add Credentials:

1. **User JWT Token**
   - Click **Authorize** button
   - Name: `UserJWT`
   - Type: HTTP Bearer
   - Enter: `Bearer <your-jwt-token>`

2. **Service Token**
   - Click **Authorize** button
   - Name: `ServiceToken`
   - Type: API Key (Header)
   - Header: `X-Service-Token`
   - Enter: `<your-service-token>`

---

## Endpoint Summary

| Endpoint | Auth | HTTP Method | Purpose |
|----------|------|-------------|---------|
| `/api/email/send` | User JWT | POST | User-initiated email send |
| `/api/email/send/service` | Service Token | POST | Service-to-service email send |
| `/api/email/send/critical` | Both (JWT + Token) | POST | Critical operation (both auth required) |
| `/api/email/verify/{emailId}` | None | GET | Public email verification |
| `/health` | None | GET | Public health check |
| `/health/status` | None | GET | Public detailed health status |

---

## Key Annotations

### Controller Level
```java
@RestController
@RequestMapping("/api/email")
@Tag(name = "Email Service", description = "...")
```

### Method Level
```java
@PostMapping("/send")
@Operation(summary = "...", description = "...")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_USER_JWT)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "..."),
    @ApiResponse(responseCode = "401", description = "...")
})
```

### Parameter Level
```java
@RequestBody EmailActionRequest request
@RequestHeader(value = "X-Service-Token") String serviceTokenHeader
@PathVariable String emailId
```

---

## Request/Response Examples

### Send Email (User JWT)
```bash
POST /api/email/send
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "actionId": "action-12345"
}

Response: 200 OK
```

### Send Email (Service Token)
```bash
POST /api/email/send/service
X-Service-Token: service-token-abc123
Content-Type: application/json

{
  "actionId": "action-12345"
}

Response: 200 OK
```

### Send Critical Email (Both)
```bash
POST /api/email/send/critical
Authorization: Bearer <jwt-token>
X-Service-Token: service-token-xyz789
Content-Type: application/json

{
  "actionId": "action-12345"
}

Response: 202 Accepted
```

---

## Configuration Constants

```java
// In OpenApiConfig.java
public static final String SECURITY_SCHEME_USER_JWT = "UserJWT";
public static final String SECURITY_SCHEME_SERVICE_TOKEN = "ServiceToken";
```

Use these in @SecurityRequirement annotations:
```java
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_USER_JWT)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_SERVICE_TOKEN)
```

---

## Build & Run

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Access Swagger UI
# Open browser: http://localhost:8080/swagger-ui.html

# Get OpenAPI spec
# JSON: http://localhost:8080/v3/api-docs
# YAML: http://localhost:8080/v3/api-docs.yaml
```

---

## Testing with cURL

```bash
# Health check (public)
curl -X GET http://localhost:8080/health

# Send with User JWT
curl -X POST http://localhost:8080/api/email/send \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"actionId":"action-12345"}'

# Send with Service Token
curl -X POST http://localhost:8080/api/email/send/service \
  -H "X-Service-Token: YOUR_SERVICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"actionId":"action-12345"}'

# Send with Both Tokens
curl -X POST http://localhost:8080/api/email/send/critical \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Service-Token: YOUR_SERVICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"actionId":"action-12345"}'
```

---

## Best Practices Implemented

✅ No hardcoded tokens or environment-specific values  
✅ Generic, reusable configuration  
✅ HTTP Bearer standard for JWT  
✅ API Key in custom header for service tokens  
✅ Comprehensive endpoint documentation  
✅ Clear security scheme definitions  
✅ Method-level security with @PreAuthorize  
✅ Proper HTTP status codes  
✅ Input validation with @Valid  
✅ Flexible authentication combinations  

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│              Swagger UI / OpenAPI UI               │
│        http://localhost:8080/swagger-ui.html        │
└────────────────────────┬────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────┐
│            OpenAPI Configuration                   │
│  (OpenApiConfig.java - Security Schemes)           │
└────────────────────────┬────────────────────────────┘
                         │
     ┌───────────────────┼───────────────────┐
     │                   │                   │
┌────▼──────┐  ┌────────▼──────┐  ┌────────▼──────┐
│ User JWT  │  │ Service Token │  │Both (Dual Auth)
│ (Bearer)  │  │ (API Key)     │  │
└────┬──────┘  └────────┬──────┘  └────────┬──────┘
     │                  │                   │
┌────▼──────────────────▼───────────────────▼──────┐
│         SecurityConfig & JwtAuthenticationFilter │
│         (Authentication & Authorization)         │
└────────────────────────┬────────────────────────┘
                         │
     ┌───────────────────┼───────────────────┐
     │                   │                   │
┌────▼──────┐  ┌────────▼──────┐  ┌────────▼──────┐
│EmailCtrl  │  │HealthCtrl    │  │Public Endpoints
│/api/email │  │/health       │  │/verify, etc
└───────────┘  └──────────────┘  └────────────────┘
```

---

## Performance Notes

- OpenAPI spec is generated at startup (minimal overhead)
- Swagger UI is served statically (fast)
- Security annotations have negligible performance impact
- JWT validation happens once per request (efficient)

---

## Version Information

- **Spring Boot:** 4.0.1
- **Java:** 17
- **springdoc-openapi:** 2.3.0
- **OpenAPI Specification:** 3.0.3

---

## Support & References

- Full documentation: See `OPENAPI_DOCUMENTATION.md`
- SpringDoc: https://springdoc.org/
- OpenAPI 3.0: https://spec.openapis.org/oas/v3.0.3
- Spring Security: https://spring.io/projects/spring-security
