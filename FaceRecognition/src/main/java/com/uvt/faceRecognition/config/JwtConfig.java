package com.uvt.faceRecognition.config;

import java.security.KeyPair;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import com.uvt.faceRecognition.config.SecurityConfigProperties.JwtProperties;

import lombok.RequiredArgsConstructor;

/**
 * jwt configs
 * 
 * @author mohamed ashiq
 *
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

	private final JwtProperties jwtProperties;

	/**
	 * jwt token store bean
	 * 
	 * @see org.springframework.security.oauth2.provider.token.store.JwtTokenStore
	 * 
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}

	/**
	 * convert jwt token from and to oauth2 authentication
	 * 
	 */
	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		KeyPair keyPair = new KeyStoreKeyFactory(jwtProperties.getResource(), jwtProperties.getPassword())
				.getKeyPair(jwtProperties.getAlias());
		converter.setKeyPair(keyPair);
		return converter;
	}
}
