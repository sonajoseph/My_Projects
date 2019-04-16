package com.shopperapp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response {
	String message;
	Boolean exist;
	
	public Response(String message){
		this.message=message;
		
	}
	public Response(String message, Boolean exist){
		this.message=message;
		this.exist = exist;
		
	}
	

}
