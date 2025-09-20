package com.payflow.app.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtService {

	private final Environment env;

	private SecretKey key() {
		String secret = env.getProperty("app.jwt.secret");
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	// Generate token (single role)
	public String generateToken(UserDetails user, long expirationMs) {
		Instant now = Instant.now();

		return Jwts.builder().subject(user.getUsername()).issuedAt(Date.from(now))
				.expiration(Date.from(now.plusMillis(expirationMs)))
				.claim("role", user.getAuthorities().iterator().next().getAuthority()) // only one role
				.signWith(key()) // algorithm inferred from key
				.compact();
	}

	public String extractUsername(String token) {
		return parse(token).getPayload().getSubject();
	}

	public String extractRole(String token) {
		return parse(token).getPayload().get("role", String.class);
	}

	public boolean isValid(String token, UserDetails user) {
		return user.getUsername().equals(extractUsername(token))
				&& parse(token).getPayload().getExpiration().after(new Date());
	}

	private Jws<Claims> parse(String token) {
		return Jwts.parser().verifyWith(key()) // must be SecretKey
				.build().parseSignedClaims(token);
	}
}
