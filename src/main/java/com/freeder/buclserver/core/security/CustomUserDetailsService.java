package com.freeder.buclserver.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.freeder.buclserver.domain.user.dto.UserDto;
import com.freeder.buclserver.domain.user.repository.UserRepository;

@Configuration
public class CustomUserDetailsService {

	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
		return username -> userRepository
			.findById(Long.valueOf(username))
			.map(UserDto::from)
			.map(CustomUserDetails::of)
			.orElseThrow(() -> new UsernameNotFoundException("해당 아이디(PK)를 가진 사용자를 찾을 수 없습니다."));
	}
}
