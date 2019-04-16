package com.uvt.faceRecognition.service;


import org.springframework.web.multipart.MultipartFile;

import com.uvt.faceRecognition.dto.Response;
import com.uvt.faceRecognition.model.User;

public interface UserService{

	 User getUserByUvtId(String uvtId);
	 
	Response save (User user, MultipartFile file);
	
	
	
}
