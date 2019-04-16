package com.uvt.faceRecognition.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uvt.faceRecognition.config.UserConverter;
import com.uvt.faceRecognition.dto.Response;
import com.uvt.faceRecognition.model.User;
import com.uvt.faceRecognition.repository.UserRepository;
import com.uvt.faceRecognition.service.StorageService;
import com.uvt.faceRecognition.service.UserService;
import com.uvt.faceRecognition.service.utils.BasicResponse;
import com.uvt.faceRecognition.service.utils.Role;
import com.uvt.faceRecognition.service.utils.SuccessMessage;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private final PasswordEncoder passwordEncoder;
	private final UserConverter userConverter;

	@Autowired
	UserRepository userRepo;

	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	StorageService storageService;
	
	@Override
	public User getUserByUvtId(String uvtId) {
		logger.info("At getUserByUvtId ..");
		List<User> user = userRepo.findByEmployeeId(uvtId);
		System.err.println("the users details are............." + user);
		if( ! user.isEmpty() && user != null) {
			return user.get(0);
		}else {
			return null;
		}
		
	}

	@Override
	public Response save(User user, MultipartFile file) { 
		logger.info("At save ..");
		if(user.getEmail().equals(null) || user.getEmail().isEmpty()){
			return new Response("Invalid Email ID.");
		}
		if(user.getEmployeeId().equals(null) || user.getEmployeeId().isEmpty()){
			return new Response("Invalid Employee ID.");
		}
		if(user.getName().equals(null) || user.getName().isEmpty()){
			return new Response("Invalid Employee Name.");
		}
		
		
		
		user.setEnabled(true); 
		user.setUsername(user.getEmail());
		user.setRole(Role.COMMUNICATOR);
		
		
		System.err.println("convertedUser ::: \n "+user.toString());
//		String password= randomPasswordGenerator.getPassword(12); 
//		
		user.setPassword(passwordEncoder.encode("uvionics123"));
		System.err.println("convertedUser ::: \n "+user.toString());
		User userDB = userRepository.save(user); 
//		try {
//			emailSender.sendEmail(userRequest.getEmail(), password, userRequest.getUsername(), userRequest.getName());
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
		logger.info("USER...: "+userDB.toString());
		if(userDB != null) {
			String fileName=	storageService.store(file, userDB.getId() );
			if(fileName.equalsIgnoreCase("Failed")) {
				return new Response("Unable To Connect to Internal Server.");
			}
			userDB.setFileName(fileName);
			System.err.println("reached");
			userDB = userRepository.save(userDB); 
			return new Response("Success");
		}else {
			return new Response("Failed");
		} 
	}

}
