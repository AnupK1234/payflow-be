//package com.payflow.app.security.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import com.payflow.app.security.filter.JwtAuthenticationFilter;
//import com.payflow.app.security.jwt.JwtUtil;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityConfig {
//
//	private final JwtUtil jwtUtil;
//
//	public SecurityConfig(JwtUtil jwtUtil) {
//		this.jwtUtil = jwtUtil;
//	}
//
//	@Bean
//	public BCryptPasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		var jwtFilter = new JwtAuthenticationFilter(jwtUtil);
//
//		http.csrf().disable().authorizeHttpRequests(auth -> auth
//				// public
//				.requestMatchers("/api/auth/**", "/api/public/**", "/v3/api-docs/**", "/swagger-ui/**",
//						"/swagger-ui.html")
//				.permitAll()
//				// bank endpoints require BANK_ADMIN authority
//				.requestMatchers("/api/bank/**").hasAuthority("BANK_ADMIN")
//				// everything else authenticated
//				.anyRequest().authenticated()).addFilterBefore(jwtFilter,
//						org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
//
//		return http.build();
//	}
//}
