# Authentication and Authorization Interview Questions
## Based on Spring Boot E-commerce Project Implementation

### **BASIC AUTHENTICATION CONCEPTS**

#### 1. **Spring Security Fundamentals**
**Q1:** Explain the authentication flow in this Spring Boot application. Walk me through what happens when a user submits login credentials.

**Expected Answer:** 
- User submits username/password via `/api/auth/signin`
- `AuthController.authenticateUser()` receives the request
- `AuthenticationManager` validates credentials using `DaoAuthenticationProvider`
- `UserDetailServiceImpl.loadUserByUsername()` fetches user from database
- Password is verified using `BCryptPasswordEncoder`
- Upon success, JWT token is generated and returned as HTTP-only cookie
- `SecurityContextHolder` stores the authentication context

**Q2:** What is the difference between authentication and authorization in this project? Give specific examples from the codebase.

**Expected Answer:**
- **Authentication:** Verifying user identity (login process, JWT validation in `AuthTokenFilter`)
- **Authorization:** Determining what authenticated users can access (role-based permissions)
- Examples: Login endpoint handles authentication, role assignments (USER, SELLER, ADMIN) handle authorization

#### 2. **Security Configuration**
**Q3:** Explain the `WebSecurityConfig` class. What security measures are implemented and why?

**Expected Answer:**
- CSRF disabled (stateless JWT authentication)
- Stateless session management (`SessionCreationPolicy.STATELESS`)
- Custom authentication entry point (`AuthEntryPointJwt`)
- JWT filter before `UsernamePasswordAuthenticationFilter`
- Public endpoints: `/api/auth/**`, `/h2-console/**`, etc.
- BCrypt password encoding
- Frame options for H2 console access

**Q4:** Why is CSRF protection disabled in this application? Is this secure?

**Expected Answer:**
- CSRF disabled because application uses stateless JWT tokens
- No session cookies that could be vulnerable to CSRF attacks
- Secure when properly implemented with JWT tokens
- Alternative: could use double-submit cookie pattern if needed

### **JWT IMPLEMENTATION DEEP DIVE**

#### 3. **JWT Token Management**
**Q5:** Walk through the JWT implementation in `JwtUtils`. What are the key methods and their purposes?

**Expected Answer:**
- `generateTokenFromUsername()`: Creates JWT with username, issue date, expiration
- `validateJwtToken()`: Validates token signature and expiration
- `getUsernameFromToken()`: Extracts username from valid JWT
- `generateJwtCookie()`/`getCleanJwtCookie()`: Manages HTTP-only cookies
- `key()`: Generates HMAC signing key from secret

**Q6:** The application uses cookies for JWT storage instead of Authorization headers. What are the pros and cons of this approach?

**Expected Answer:**
**Pros:**
- HTTP-only cookies prevent XSS attacks
- Automatic handling by browsers
- CSRF protection if implemented

**Cons:**
- CSRF vulnerability (mitigated by stateless design)
- Complex for mobile/API clients
- Cookie size limitations

**Alternative:** Authorization header with `Bearer` token (code shows commented implementation)

**Q7:** What security measures are implemented in the JWT configuration? What could be improved?

**Expected Answer:**
**Current Security:**
- HMAC-SHA256 signing
- HTTP-only cookies
- Token expiration (5 minutes based on config)

**Improvements:**
- Shorter token expiration with refresh tokens
- Token blacklisting for logout
- RSA keys instead of HMAC for distributed systems
- Secure flag for cookies in production

#### 4. **Token Validation and Filtering**
**Q8:** Explain the `AuthTokenFilter` class. How does it integrate with Spring Security's filter chain?

**Expected Answer:**
- Extends `OncePerRequestFilter` for single execution per request
- Extracts JWT from cookie using `parseJwt()`
- Validates token using `jwtUtils.validateJwtToken()`
- Loads user details and creates authentication object
- Sets authentication in `SecurityContextHolder`
- Positioned before `UsernamePasswordAuthenticationFilter`

**Q9:** What happens if a JWT token is expired or invalid? How does the application handle these scenarios?

**Expected Answer:**
- `validateJwtToken()` catches specific exceptions:
  - `ExpiredJwtException`: Token expired
  - `MalformedJwtException`: Invalid format
  - `UnsupportedJwtException`: Unsupported algorithm
  - `IllegalArgumentException`: Empty claims
- Logs error messages for debugging
- Returns false, preventing authentication
- User must re-authenticate

### **ROLE-BASED ACCESS CONTROL**

#### 5. **User and Role Management**
**Q10:** Describe the relationship between User and Role entities. How is this implemented in the database?

**Expected Answer:**
- Many-to-many relationship using `@ManyToMany`
- Junction table `user_role` with foreign keys
- User can have multiple roles (admin has USER, SELLER, ADMIN)
- Roles defined as enum (`AppRole`: USER, SELLER, ADMIN)
- Eager fetching for roles to avoid N+1 queries

**Q11:** How are default roles assigned during user registration? Show the code flow.

**Expected Answer:**
- `AuthController.registerUser()` processes signup
- If no roles specified, assigns `ROLE_USER` by default
- Role assignment logic in switch statement:
  - "admin" → `ROLE_ADMIN`
  - "seller" → `ROLE_SELLER`
  - default → `ROLE_USER`
- Roles fetched from database using `RoleRepository.findByRoleName()`

#### 6. **Authorization Implementation**
**Q12:** This application doesn't use `@PreAuthorize` annotations. How would you implement method-level security for different endpoints?

**Expected Answer:**
```java
// Example implementations:
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")

@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
@PostMapping("/products")

@PreAuthorize("hasRole('USER') and #userId == principal.id")
@GetMapping("/user/{userId}/orders")
```
- Enable with `@EnableMethodSecurity` in config
- Use SpEL expressions for complex authorization

**Q13:** How would you implement endpoint-level authorization for this e-commerce application? What access levels should different roles have?

**Expected Answer:**
- **USER:** View products, manage own orders, update profile
- **SELLER:** All USER permissions + manage own products, view own sales
- **ADMIN:** All permissions + user management, system configuration
- Implement using `antMatchers()` in security config or method-level annotations

### **ADVANCED SECURITY TOPICS**

#### 7. **Password Security**
**Q14:** Explain the password encoding strategy used. Why is BCrypt chosen and how does it work?

**Expected Answer:**
- Uses `BCryptPasswordEncoder` with adaptive hashing
- BCrypt includes salt automatically (prevents rainbow table attacks)
- Computationally expensive (slows brute force attacks)
- Cost factor can be adjusted based on hardware capabilities
- Each hash is unique even for same password

**Q15:** How would you implement password complexity requirements and account lockout mechanisms?

**Expected Answer:**
- Custom validation annotations for password complexity
- Account lockout: add fields to User entity (failed attempts, lockout time)
- Custom authentication failure handler
- Scheduled tasks to unlock accounts
- Consider using Spring Security's account status methods

#### 8. **Session Management**
**Q16:** The application uses stateless sessions. What are the implications and how does logout work?

**Expected Answer:**
- No server-side session storage
- Each request must include valid JWT
- Logout clears client-side cookie (`getCleanJwtCookie()`)
- Token remains valid until expiration (no server-side invalidation)
- For true logout: implement token blacklisting with Redis/database

**Q17:** How would you implement "Remember Me" functionality in this JWT-based system?

**Expected Answer:**
- Separate long-lived refresh tokens
- Store refresh tokens securely (database, HTTP-only cookies)
- Access tokens with short expiration
- Refresh endpoint to generate new access tokens
- Revoke refresh tokens on logout

#### 9. **Security Best Practices**
**Q18:** What security vulnerabilities exist in this implementation and how would you fix them?

**Expected Answer:**
**Vulnerabilities:**
- Hardcoded JWT secret in properties
- No rate limiting on auth endpoints
- No account lockout mechanism
- Debug logging enabled (exposes sensitive data)

**Fixes:**
- Use environment variables/vault for secrets
- Implement rate limiting (Spring Cloud Gateway, Redis)
- Add account lockout logic
- Disable debug logging in production

**Q19:** How would you handle JWT token refresh and revocation in a production environment?

**Expected Answer:**
- Implement refresh token rotation
- Store refresh tokens in database/Redis
- Short-lived access tokens (15 minutes)
- Longer refresh tokens (days/weeks)
- Token blacklisting for immediate revocation
- Sliding window expiration for active users

#### 10. **Integration and Testing**
**Q20:** How would you test the authentication and authorization features? What test scenarios are critical?

**Expected Answer:**
**Test Scenarios:**
- Valid/invalid login credentials
- JWT token generation and validation
- Role-based access control
- Token expiration handling
- Password encoding/verification
- Unauthorized access attempts

**Test Types:**
- Unit tests for security components
- Integration tests for auth endpoints
- Security tests with `@WithMockUser`
- Load testing for auth performance

### **REAL-WORLD SCENARIOS**

#### 11. **Scalability and Performance**
**Q21:** How would you scale this authentication system for a high-traffic e-commerce platform?

**Expected Answer:**
- JWT enables horizontal scaling (stateless)
- Implement caching for user details (Redis)
- Database read replicas for user lookups
- Rate limiting and DDoS protection
- CDN for static authentication assets
- Microservices architecture with centralized auth service

**Q22:** How would you implement single sign-on (SSO) with external providers (Google, Facebook) in this system?

**Expected Answer:**
- Spring Security OAuth2 integration
- Implement OAuth2 authorization code flow
- Map external user info to local User entity
- Generate internal JWT after OAuth validation
- Handle account linking scenarios
- Maintain role assignments for external users

#### 12. **Monitoring and Auditing**
**Q23:** What logging and monitoring would you implement for security events?

**Expected Answer:**
- Authentication success/failure events
- Authorization violations
- Suspicious login patterns
- Token validation failures
- Account lockout events
- Integration with security monitoring tools (Splunk, ELK)

**Q24:** How would you implement audit trails for user actions in this e-commerce system?

**Expected Answer:**
- Audit entity with user, action, timestamp, IP
- AOP interceptors for sensitive operations
- Database triggers for data changes
- Immutable audit logs
- Retention policies for compliance
- Real-time alerting for critical actions

### **ARCHITECTURE AND DESIGN PATTERNS**

#### 13. **Design Patterns in Security**
**Q25:** What design patterns are used in the Spring Security implementation? How do they benefit the system?

**Expected Answer:**
- **Filter Chain Pattern:** Sequential request processing
- **Strategy Pattern:** Multiple authentication providers
- **Template Method:** UserDetailsService implementation
- **Builder Pattern:** Security configuration
- **Factory Pattern:** Authentication objects creation

**Q26:** How would you refactor this authentication system for better maintainability and testability?

**Expected Answer:**
- Extract JWT operations to separate service
- Implement authentication strategy pattern
- Use configuration properties instead of hardcoded values
- Add comprehensive error handling
- Implement caching strategies
- Create authentication events for monitoring

This comprehensive set of questions covers the full spectrum of authentication and authorization concepts implemented in your Spring Boot e-commerce project, from basic concepts to advanced production considerations.