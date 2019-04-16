package com.democonfiguration.service;

import com.democonfiguration.dto.UserDto;
import com.democonfiguration.model.User;

import java.util.List;

public interface UserService {

//    User save(User user);
    List<User> findAll();
    void delete(long id);
    User findOne(String username);

    User findById(Long id);
	User save(UserDto user);
}
