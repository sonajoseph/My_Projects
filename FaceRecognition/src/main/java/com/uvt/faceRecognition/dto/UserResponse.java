package com.uvt.faceRecognition.dto;

import com.uvt.faceRecognition.model.User;

import lombok.Data;

@Data
public class UserResponse {

	private String name;
	private String id; 
	
	public UserResponse(User user) {
		this.name = user.getName();
		this.id = user.getId();
	}
	
}