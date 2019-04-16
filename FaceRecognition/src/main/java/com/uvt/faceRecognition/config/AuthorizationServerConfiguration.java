package com.uvt.faceRecognition.config;

import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.uvt.faceRecognition.config.SecurityConfigProperties.SecurityOAuthClientProperties;

import lombok.RequiredArgsConstructor;

 
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	private final AuthenticationManager authenticationManager;

	private final UserDetailsService userDetailsService;

	private final SecurityOAuthClientProperties securityOAuthClientProperties;

	private final TokenEnhancer tokenEnhancer;

	private final JwtAccessTokenConverter jwtAccessTokenConverter;

	private final TokenStore tokenStore;

	/**
	 * configuring client id, client secerts, authorization types, access token and
	 * refresh token validity
	 * 
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient(securityOAuthClientProperties.getClientId())
				.secret(securityOAuthClientProperties.getClientSecret())
				.authorizedGrantTypes(securityOAuthClientProperties.getAuthorizedGrantTypes())
				.scopes(securityOAuthClientProperties.getScope())
				.accessTokenValiditySeconds(securityOAuthClientProperties.getAccessTokenValiditySeconds())
				.refreshTokenValiditySeconds(securityOAuthClientProperties.getRefreshTokenValiditySeconds());
	}

	/**
	 * Overriding default configuration of AuthorizationServerEndpointsConfigurer
	 * 
	 * @see com.uvionicstech.nextbillion.config.CustomTokenEnhancer
	 * @see org.springframework.security.oauth2.provider.token.store
	 * @see org.springframework.security.authentication.AuthenticationManager
	 * @see org.springframework.security.core.userdetails.UserDetailsService
	 * @see com.uvionicstech.nextbillion.security.UserDetailsServiceImpl
	 * 
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer, jwtAccessTokenConverter));
		endpoints.authenticationManager(authenticationManager).userDetailsService(userDetailsService)
				.tokenEnhancer(tokenEnhancerChain).tokenStore(tokenStore).pathMapping("/oauth/token", "/login");
	}

	/**
	 * Overriding
	 * org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
	 * /oauth/token_key to permitAll() and /oauth/check_token to isAuthenticated()
	 * 
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

}