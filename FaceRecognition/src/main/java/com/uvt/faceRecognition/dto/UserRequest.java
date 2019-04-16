package com.uvt.faceRecognition.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UserRequest {
	@NotEmpty
	private String name;
	@NotEmpty
	private String userName;
	@NotEmpty
	private String password;
	@NotEmpty
	private String email;

}
