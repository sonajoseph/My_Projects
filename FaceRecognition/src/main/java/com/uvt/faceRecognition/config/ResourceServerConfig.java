package com.uvt.faceRecognition.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

 
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	/**
	 * api level security
	 * 
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/v2/api-docs").permitAll()
				.antMatchers("/actuator/refresh").permitAll()
				.antMatchers("/login").permitAll()
				.antMatchers("/addAdmin").permitAll()
				.antMatchers("/admin/*").permitAll()
//				.antMatchers(HttpMethod.GET, "/communicationtracker/user/emailId/*").permitAll() 
//				.antMatchers(HttpMethod.GET, "/communicationtracker/user/userName/*").permitAll() 
//				.antMatchers( "/swagger-ui.html").permitAll().anyRequest().authenticated();
				.antMatchers( "/api/emp/*").permitAll().anyRequest().authenticated();
//				.antMatchers("/api/**").permitAll() 
//				.antMatchers("/api/userLinksDetails/").permitAll()
//				.antMatchers( "/api/userLinks").permitAll().anyRequest().authenticated();
	}
}
