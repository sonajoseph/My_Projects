package com.uvt.faceRecognition.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.uvt.faceRecognition.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long>  {
 		Optional<User> findByUsername(String username);
		List<User> findAllByRole(String role);
		List<User> findByEmail(String email);
		List<User> findByEmployeeId(String employeeId);
}
