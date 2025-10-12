package com.payflow.app.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.app.dto.response.UserResponse;
import com.payflow.app.entity.User;
import com.payflow.app.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtService {

	private final Environment env;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final ObjectMapper objectMapper;

	private SecretKey key() {
		String secret = env.getProperty("app.jwt.secret");
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	// Generate token (single role)
	public String generateToken(UserDetails user, long expirationMs) {
		Instant now = Instant.now();

		try {
			User userObj = userRepository.findByUsername(user.getUsername())
					.orElseThrow(() -> new RuntimeException("User not found"));

			UserResponse userResponse = modelMapper.map(userObj, UserResponse.class);
			String userJson = objectMapper.writeValueAsString(userResponse);

			return Jwts.builder().subject(user.getUsername()).issuedAt(Date.from(now))
					.expiration(Date.from(now.plusMillis(expirationMs)))
					.claim("role", user.getAuthorities().iterator().next().getAuthority()) // only one role
					.claim("user", userJson).signWith(key()) // algorithm inferred from key
					.compact();

		} catch (Exception e) {
			return "";
		}
	}

	public String extractUsername(String token) {
		return parse(token).getPayload().getSubject();
	}

	public String extractRole(String token) {
		return parse(token).getPayload().get("role", String.class);
	}

	public UserResponse extractUser(String token) throws Exception {
		String userJson = parse(token).getPayload().get("user", String.class);
		return objectMapper.readValue(userJson, UserResponse.class);
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
