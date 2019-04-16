package com.uvt.faceRecognition.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.uvt.faceRecognition.model.User;
import com.uvt.faceRecognition.service.UserService;

import lombok.RequiredArgsConstructor;

 
@RequiredArgsConstructor
@Primary
@Component
public class CustomTokenEnhancer implements TokenEnhancer {

	private final UserService userService;

	/**
	 * adding custom claims to jwt token
	 * 
	 */
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		final Map<String, Object> additionalInfo = new HashMap<>();
		additionalInfo.put("user_id", user.getId());
		if (null != user.getName())
			additionalInfo.put("name", user.getName());
			additionalInfo.put("role", user.getRole());

		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		return accessToken;
	}

}
