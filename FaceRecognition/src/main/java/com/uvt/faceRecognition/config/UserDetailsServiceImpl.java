package com.uvt.faceRecognition.config;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.uvt.faceRecognition.model.User;
import com.uvt.faceRecognition.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * a org.springframework.security.core.userdetails.UserDetailsService
 * implemetation to find user for login
 * 
 *
 */
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	/**
	 * find user for login
	 */
	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<User> hasUser = userRepository.findByUsername(username);
		if (!hasUser.isPresent())
			throw new UsernameNotFoundException("user not found");
		return hasUser.get();

	}

}
