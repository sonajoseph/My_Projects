package com.uvt.faceRecognition.service;

import com.uvt.faceRecognition.model.User;
import com.uvt.faceRecognition.service.utils.BasicResponse;

public interface AdminService {
	public BasicResponse<User> createAdmin(User userRequest, String role);


}
