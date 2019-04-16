package com.shopperapp.mysql.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopperapp.mysql.models.UserInformationModel;
import com.shopperapp.mysql.service.UserInformationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/userinformation")
@RequiredArgsConstructor
public class UserInfromationController {
	private static final Logger logger = LoggerFactory.getLogger(UserInfromationController.class);

	private UserInformationService userInformationService;
	@PostMapping("/store")
	public void storeUserInformation(@RequestBody UserInformationModel informationModel ) {
		logger.info("At storeUserInformation...");
		
		userInformationService.storeUserData(informationModel);
	}
}
