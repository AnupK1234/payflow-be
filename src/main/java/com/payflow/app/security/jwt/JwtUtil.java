package com.payflow.app.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payflow.app.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final Key key;
	private final long validityMs;

	public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms:28800000}") long validityMs) { // default
																														// 8h
		if (secret == null || secret.length() < 32) {
			throw new IllegalArgumentException("JWT secret must be set and at least 256 bits (32 characters).");
		}
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.validityMs = validityMs;
	}

	public String generateToken(User user) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + validityMs);

		JwtBuilder b = Jwts.builder().setSubject(user.getUsername()).setIssuedAt(now).setExpiration(exp)
				// include useful user info as claims
				.addClaims(Map.of("id", user.getId(), "username", user.getUsername(), "email", user.getEmail(), "role",
						user.getRole(), "organizationId",
						user.getOrganization() != null ? user.getOrganization().getId() : null, "mustResetPassword",
						user.getMustResetPassword()))
				.signWith(key, SignatureAlgorithm.HS256);

		return b.compact();
	}

	public Jws<Claims> validateToken(String token) throws JwtException {
		return null;
		// return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	}

	// helper: get claim value safely
	public <T> T getClaim(String token, String claimName, Class<T> cls) {
		Claims claims = validateToken(token).getBody();
		Object v = claims.get(claimName);
		if (v == null)
			return null;
		return cls.cast(v);
	}
}
