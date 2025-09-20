package com.payflow.app.security.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // disable CSRF for APIs
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/organizations/register", "/v3/api-docs/**",
						"/swagger-ui/**", "/swagger-ui.html").permitAll().anyRequest().authenticated())
				.httpBasic(withDefaults()); // basic auth for other endpoints

		return http.build();
	}

}
