package com.uvt.faceRecognition.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.uvt.faceRecognition.dto.Response;
import com.uvt.faceRecognition.exception.StorageException;
import com.uvt.faceRecognition.model.User;
import com.uvt.faceRecognition.service.StorageService;
import com.uvt.faceRecognition.service.UserService;

@RestController 
@RequestMapping("admin") 
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	private final StorageService storageService; 
	@Autowired
	RestTemplate restTemplate;
	@Autowired
	RestTemplateBuilder restTemplateBuilder; 

	@Autowired
	UserService userService;
	
	public UserController(StorageService storageService) {
		this.storageService = storageService;
	}
  

	@PostMapping("/signup") 
	public ResponseEntity<?> registerUsers(@RequestParam("user") String userParam, @RequestParam("file") MultipartFile file) {
		System.out.println(userParam); 
		ObjectMapper objectMapper = new ObjectMapper();
	    
		try {
			User user = objectMapper.readValue(userParam, User.class);
			System.err.println("userv  :::::: "+user.toString());
			return new ResponseEntity<Response>(userService.save(user, file), HttpStatus.OK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<Response>(new Response("Failed"), HttpStatus.OK);
		}

		
	}

	@PostMapping("/signin")
	public ResponseEntity<?> detectUserDetails(@RequestParam("file") MultipartFile file) {

//		try {
//			String fileName = storageService.store(file);
//
//			System.out.println(fileName);
//
//			// this.restTemplate =
//			// restTemplateBuilder.errorHandler(restTemplateErrorHandler).build();
//			// HttpHeaders headers = new HttpHeaders();
//			// headers.add("Authorization","token");
//			// HttpEntity<String> entity=new HttpEntity<>(headers);
//			String api = "http://13.67.76.165:5000/" + fileName;
//
//			ResponseEntity<Object> response = restTemplate.getForEntity(api, Object.class);
//
//			// ResponseEntity<String> response = restTemplate.postForEntity( url, params,
//			// String.class );
//
//			JSONPObject userDetails = (JSONPObject) response.getBody();
//			
//			System.out.println(userDetails);
//
////			SigninResponse signinResponse = new SigninResponse("You successfully uploaded " + fileName);
//
//			System.err.println("********" + userDetails);
//			return new ResponseEntity<Object>(userDetails, HttpStatus.OK);
//		} catch (StorageException se) {
//			LOGGER.error("storage error", se);
//			System.err.println("storage error");
//			return new ResponseEntity<Response>(new Response("storage error"), HttpStatus.BAD_REQUEST);
//		}
		return new ResponseEntity<Response>(new Response("storage error"), HttpStatus.BAD_REQUEST);
	}

} 