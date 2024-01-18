package com.freeder.buclserver.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/api/v1/products/**").permitAll()
			.antMatchers("/api/v1/products/category/**").permitAll()
			.antMatchers("/api/v1/rewards/**").permitAll()
			.antMatchers("/api/v1/openapi/**").permitAll()
			.antMatchers("/api/v1/categories/**").permitAll()
			.anyRequest().authenticated()
			.and().csrf().disable()
			.httpBasic();
	}
}


