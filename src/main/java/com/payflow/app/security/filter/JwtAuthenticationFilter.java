//package com.payflow.app.security.filter;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.payflow.app.security.jwt.JwtUtil;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.JwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//	private final JwtUtil jwtUtil;
//
//	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
//		this.jwtUtil = jwtUtil;
//	}
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
//			throws ServletException, IOException {
//
//		String header = req.getHeader(HttpHeaders.AUTHORIZATION);
//		if (header != null && header.startsWith("Bearer ")) {
//			String token = header.substring(7);
//			try {
//				Jws<Claims> parsed = jwtUtil.validateToken(token);
//				Claims claims = parsed.getBody();
//				String username = claims.getSubject();
//				String role = claims.get("role", String.class);
//				if (username != null) {
//					var auth = new UsernamePasswordAuthenticationToken(username, null,
//							List.of(new SimpleGrantedAuthority(role)));
//					SecurityContextHolder.getContext().setAuthentication(auth);
//				}
//			} catch (JwtException ex) {
//				// invalid token -> clear context (do not throw here; downstream will block if
//				// needed)
//				SecurityContextHolder.clearContext();
//			}
//		}
//
//		chain.doFilter(req, res);
//	}
//}
