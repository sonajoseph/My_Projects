package com.shopperapp.service;

public interface GoogleAPIService { 
	String fetchMessageId(String accessToken, String mailId); 
	String devFetchMessageId(String accessToken, String mailId);  

	
}
