package com.payflow.app.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.payflow.app.entity.User;
import com.payflow.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository; // must be final for Lombok injection

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		// If your User.role is an enum (Role), convert it to string:
		String roleName = (user.getRole() == null) ? null : user.getRole().name();

		if (roleName == null) {
			throw new UsernameNotFoundException("User has no role assigned: " + username);
		}

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(),
				authorities);
	}
}
