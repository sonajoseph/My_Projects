package com.democonfiguration.dao;

import com.democonfiguration.dto.UserDto;
import com.democonfiguration.model.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, Long> {
    User findByUsername(String username);

	User save(UserDto user);
}
