package com.uvt.faceRecognition.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * read security configuration from configuration file
 * 
 * @author mohamed ashiq
 *
 */
@Configuration
public class SecurityConfigProperties {

	@Getter
	@Setter
	@Configuration
	@ConfigurationProperties(prefix = "security.oauth2.client")
	public static class SecurityOAuthClientProperties {

		private String clientId;
		private String clientSecret;
		private String[] authorizedGrantTypes;
		private String[] scope;
		private int accessTokenValiditySeconds;
		private int refreshTokenValiditySeconds;

	}

	@Getter
	@Setter
	@Component
	@ConfigurationProperties(prefix = "security.jwt")
	public static class JwtProperties {

		private ClassPathResource resource;
		private char[] password;
		private String alias;

	}

}