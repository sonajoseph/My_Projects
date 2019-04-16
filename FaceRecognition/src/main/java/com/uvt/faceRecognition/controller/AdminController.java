package com.uvt.faceRecognition.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.uvt.faceRecognition.model.User;
import com.uvt.faceRecognition.service.AdminService;
import com.uvt.faceRecognition.service.UserService;
import com.uvt.faceRecognition.service.utils.BasicResponse;

@RestController
public class AdminController {
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	UserService userService;
	
	@Autowired
	AdminService adminService;
	
	@PostMapping("/addAdmin")	
	public BasicResponse<User> addAdmin(@RequestBody @Valid User userRequest) {
		logger.info("At register");
		return adminService.createAdmin(userRequest,"ADMIN");
	}
	
}
