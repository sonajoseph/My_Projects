package com.uvt.faceRecognition.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

 
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;

	/**
	 * creating AuthenticationManager Bean
	 * 
	 * @see org.springframework.security.authentication.AuthenticationManager
	 * 
	 */
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * creating PasswordEncoder Bean
	 * 
	 * @see org.springframework.security.crypto.password.PasswordEncoder
	 * @see org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
	 * 
	 * @return PasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Overrideing AuthenticationManagerBuilder
	 * 
	 * @see org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
	 * @see org.springframework.security.core.userdetails.UserDetailsService
	 * @see com.uvionicstech.nextbillion.security.UserDetailsServiceImpl
	 * 
	 *      adding UserDetailsService and PasswordEncoder to
	 *      AuthenticationManagerBuilder
	 * 
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
}
