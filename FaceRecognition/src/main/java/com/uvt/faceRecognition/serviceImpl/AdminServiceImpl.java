package com.uvt.faceRecognition.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uvt.faceRecognition.config.UserConverter;
import com.uvt.faceRecognition.model.User;
import com.uvt.faceRecognition.repository.UserRepository;
import com.uvt.faceRecognition.service.AdminService;
import com.uvt.faceRecognition.service.utils.BasicResponse;
import com.uvt.faceRecognition.service.utils.Role;
import com.uvt.faceRecognition.service.utils.SuccessMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService{
	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
	private final PasswordEncoder passwordEncoder;
	private final UserConverter userConverter;

	
	@Autowired
	UserRepository userRepository;
	@Override
	public BasicResponse<User> createAdmin(User userRequest, String role) {
		logger.info("At createUser ....");
//		User convertedUser = userConverter.convert(userRequest);
		userRequest.setEnabled(true); 
		userRequest.setUsername(userRequest.getEmail());
		if(role.equalsIgnoreCase(Role.ADMIN.toString())) {
			userRequest.setRole(Role.ADMIN);
		}else {
			userRequest.setRole(Role.COMMUNICATOR);
		}
		
		System.err.println("convertedUser ::: \n "+userRequest.toString());
//		String password= randomPasswordGenerator.getPassword(12); 
//		
//		userRequest.setPassword(passwordEncoder.encode("uvionics123"));
		System.err.println("convertedUser ::: \n "+userRequest.toString());
		User user = userRepository.save(userRequest); 
//		try {
//			emailSender.sendEmail(userRequest.getEmail(), password, userRequest.getUsername(), userRequest.getName());
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
		logger.info("USER...: "+user.toString());
		return new BasicResponse<>(SuccessMessage.CREATED, user);
	}

}
