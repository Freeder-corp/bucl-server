package com.freeder.buclserver.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 특정 엔드포인트에 대한 보안을 해제 (개발 및 테스트 목적)
		http.authorizeRequests()
			.antMatchers("/api/v1/products/**").permitAll()
			.antMatchers("/api/v1/products/category/**").permitAll()
			.anyRequest().authenticated()
			.and()
			.httpBasic();
	}
}

