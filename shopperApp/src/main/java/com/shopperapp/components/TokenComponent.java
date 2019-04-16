package com.shopperapp.components;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.shopperapp.dto.LoginDTO;
import com.shopperapp.dto.UserTokenDTO;
import com.shopperapp.mysql.models.UserToken;
import com.shopperapp.mysql.repo.UserTokenRepo;

@Component
public class TokenComponent {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenComponent.class);

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private UserTokenRepo userTokenRepo;

	@Autowired
	NotificationMessageFCM notificationMessageFCM;
	
	public String getAccessTokenAndRefreshToken(LoginDTO data) {
		logger.info("At getAccessTokenAndRefreshToken . . .");
		URI uri = null;
		try {
			uri = new URI("https://www.googleapis.com/oauth2/v4/token");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("client_id", "604387373464-f179jsvbjdl9hpmi4rgnbrnincrmjfa8.apps.googleusercontent.com");
		uriVariables.put("client_secret", "ihTMZ6RsFxMHnv7XL-omdgXl");
		uriVariables.put("code", data.getAuthcode());
		uriVariables.put("redirect_uri", "https://shopper-app-8e6db.firebaseapp.com/__/auth/handler");
		uriVariables.put("grant_type", "authorization_code");
		ResponseEntity<UserTokenDTO> responseFromGoogle = restTemplate.postForEntity(uri, uriVariables, UserTokenDTO.class);
		  
		System.err.println(responseFromGoogle.toString());
		if (responseFromGoogle != null) {
			
			if (responseFromGoogle.getStatusCodeValue() == 200) {
				UserToken userTokenDB = userTokenRepo.findByMailId(data.getMailId());
				if(userTokenDB == null) {
					UserToken userToken = new UserToken();
					System.err.println("--------------------------");
					System.err.println(responseFromGoogle.getBody());
					UserTokenDTO userTokenDTO = (UserTokenDTO) responseFromGoogle.getBody();
					BeanUtils.copyProperties(userTokenDTO, userToken);
					userToken.setMailId(data.getMailId());
					userToken.setDeviceId(data.getDeviceId());

					System.err.println("userTokenuserTokenuserTokenuserTokenuserToken : \n "+userToken.toString());
        			userToken.setUpdatedAt(new Date());
					UserToken save = userTokenRepo.save(userToken);
					if (save != null) {
						return "Bearer " + save.getAccessToken();
					}
				}else { 
					UserToken userToken = new UserToken();
					System.err.println("--------------------------");
					System.err.println(responseFromGoogle.getBody());
					UserTokenDTO userTokenDTO = (UserTokenDTO) responseFromGoogle.getBody();
					BeanUtils.copyProperties(userTokenDTO, userToken);
					userTokenDB.setAccessToken(userTokenDTO.getAccessToken());
					userTokenDB.setRefreshToken(userTokenDTO.getRefreshToken());
					userTokenDB.setDeviceId(data.getDeviceId());
					
				
//					userToken.setMailId(mailId);

        			userToken.setUpdatedAt(new Date());
					UserToken save = userTokenRepo.save(userTokenDB);
					System.err.println("userTokenuserTokenuserTokenuserTokenuserToken :  2222\n "+save.toString());

					if (save != null) {
						return "Bearer " + save.getAccessToken();
					}
				}
				
			}
		}

		return "Login Failed";
	}
	public String getNewAccessTokenUsingRefreshToken(String refreshToken) {
		logger.info("At getNewAccessTokenUsingRefreshToken . . .");
		
		URI uri = null;
		try {
			uri = new URI("https://www.googleapis.com/oauth2/v4/token");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("client_id", "604387373464-f179jsvbjdl9hpmi4rgnbrnincrmjfa8.apps.googleusercontent.com");
		uriVariables.put("client_secret", "ihTMZ6RsFxMHnv7XL-omdgXl");
		uriVariables.put("refresh_token", refreshToken);
		uriVariables.put("grant_type", "refresh_token");
		ResponseEntity<UserTokenDTO> responseFromGoogle = restTemplate.postForEntity(uri, uriVariables, UserTokenDTO.class);
		if (responseFromGoogle != null) {
			if (responseFromGoogle.getStatusCodeValue() == 200) {  
				UserToken userToken = userTokenRepo.findByRefreshToken(refreshToken);
				UserTokenDTO userTokenDTO = (UserTokenDTO) responseFromGoogle.getBody();
				userToken.setAccessToken(userTokenDTO.getAccessToken());
    			userToken.setUpdatedAt(new Date());
				UserToken save = userTokenRepo.save(userToken);
				if (save != null) {
					return "Bearer " + save.getAccessToken();
				}
			}
		}
		return "";
	}
	
	public String getNewAccessTokenUsingRefreshTokenByEmailId(String emailId) {
		logger.info("At getNewAccessTokenUsingRefreshTokenByEmailId . . .");
		
		URI uri = null;
		try {
			uri = new URI("https://www.googleapis.com/oauth2/v4/token");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		UserToken userToken = userTokenRepo.findByMailId(emailId);
		try {
		
		if(userToken != null && userToken.getDeviceId() != null && userToken.getDeviceId() != "") {
			System.err.println("userToken ::: "+userToken.toString());
			Map<String, String> uriVariables = new HashMap<>();
			uriVariables.put("client_id", "604387373464-f179jsvbjdl9hpmi4rgnbrnincrmjfa8.apps.googleusercontent.com");
			uriVariables.put("client_secret", "ihTMZ6RsFxMHnv7XL-omdgXl");
			uriVariables.put("refresh_token", userToken.getRefreshToken());
			uriVariables.put("grant_type", "refresh_token");
			ResponseEntity<UserTokenDTO> responseFromGoogle = restTemplate.postForEntity(uri, uriVariables, UserTokenDTO.class);
			if (responseFromGoogle != null) {
				if (responseFromGoogle.getStatusCodeValue() == 200) {
					
					UserTokenDTO userTokenDTO = (UserTokenDTO) responseFromGoogle.getBody();
				    userToken.setAccessToken(userTokenDTO.getAccessToken());
					System.err.println("-------Date--------------" + new Date());
        			userToken.setUpdatedAt(new Date());
					UserToken save = userTokenRepo.save(userToken);
					if (save != null) {
						return "Bearer " + save.getAccessToken();
					}
				}
			}
		}
		}catch(Exception e1) {
			e1.printStackTrace();
			String msg = "Please relogin to Shopper App.";
			notificationMessageFCM.fcmMailNotificationMessage(msg, userToken.getDeviceId(),"");
			userToken.setDeviceId(""); 
			userTokenRepo.save(userToken);
			
		}
		
		return "";
	}
	
}
