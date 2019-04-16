package com.uvt.faceRecognition.config;

import org.springframework.stereotype.Component;

import com.uvt.faceRecognition.dto.UserRequest;
import com.uvt.faceRecognition.dto.UserResponse;
import com.uvt.faceRecognition.model.User;
import com.uvt.faceRecognition.service.utils.Role;

@Component
public class UserConverter {

	public User convert(UserRequest userRequest) {
		User user = new User();
		user.setName(userRequest.getName());
		user.setUsername(userRequest.getUserName());
		user.setRole(Role.COMMUNICATOR);
		return user;
	}

	public UserResponse convert(User user) {
		UserResponse reponse = new UserResponse(user);
		return reponse;
	}

}
