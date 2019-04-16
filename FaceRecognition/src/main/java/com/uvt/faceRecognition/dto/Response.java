package com.uvt.faceRecognition.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString

public class Response {

	private String message;
	public Response(String message){
		this.message=message;
	}
}
