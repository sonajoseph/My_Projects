package com.uvt.faceRecognition.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * enabling method level security, like which role or scope user has the access
 * to the method
 * 
 * @author Arun Johnson
 *
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class GlobalMethodSecurityConfig {

}
