package com.shopperapp.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopperapp.components.TokenComponent;
import com.shopperapp.dto.LoginDTO;
import com.shopperapp.dto.Response;
import com.shopperapp.mysql.models.UserToken;
import com.shopperapp.mysql.repo.UserTokenRepo;
import com.shopperapp.service.GoogleAPIService;
@RestController
@RequestMapping("/google")
public class GoogleApiController {
	private static final Logger logger = LoggerFactory.getLogger(GoogleApiController.class);

	@Autowired
	private GoogleAPIService googleAPIService;
	@Autowired
	UserTokenRepo userTokenRepo;
	

	@Autowired
	private TokenComponent tokenComponent;
	
	
	@PostMapping("/message-id")
	public ResponseEntity<?> getMails(@RequestBody LoginDTO data) {
		logger.info("At getMails.");
		logger.info(data.toString()); 
		UserToken userToken = userTokenRepo.findByMailId(data.getMailId());
		String accessToken = tokenComponent.getAccessTokenAndRefreshToken(data);
		if (accessToken.equalsIgnoreCase("Login Failed")) { 
			return new ResponseEntity<Response>(new Response(accessToken), HttpStatus.OK);
		}
		String message=googleAPIService.fetchMessageId(accessToken, data.getMailId());
		
		//The Below return to tell the Mobile team that is the user first 
		//time logging in or already logged in some time back.
		if(userToken != null) {
			return new ResponseEntity<Response>(new Response(message, true), HttpStatus.OK); 
		}
		else {
			return new ResponseEntity<Response>(new Response(message, false), HttpStatus.OK); 
		}  
	}

	@GetMapping("/dev/message-id/{accessToken}/{mailId}")
	public ResponseEntity<?> getMailsDev(@PathVariable String accessToken, @PathVariable String mailId) {
		logger.info("At getMails.");
		String message=googleAPIService.devFetchMessageId(accessToken,mailId);
		//return new ResponseEntity<Response>(new Response(message), HttpStatus.OK);
		UserToken userToken = userTokenRepo.findByMailId(mailId);
		if(userToken != null) {
			return new ResponseEntity<Response>(new Response(message, true), HttpStatus.OK); 
		}
		else {
			return new ResponseEntity<Response>(new Response(message, false), HttpStatus.OK); 
		}  
	}
	
	@GetMapping("/logout/{mailId}")
	public ResponseEntity<?> logout(@PathVariable String mailId) {
		logger.info("At logout.");
		
		UserToken user=userTokenRepo.findByMailId(mailId);
		if(user != null) {
			user.setDeviceId("");
			userTokenRepo.save(user);
		}
		return new ResponseEntity<Response>(new Response("Success"), HttpStatus.OK);
		
		
	}
	
	

	
}
