package com.shopperapp.mysql.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopperapp.mysql.models.UserToken;

public interface UserTokenRepo extends JpaRepository<UserToken, Long> {
	UserToken findByRefreshToken(String refreshToken);
	UserToken findByMailId(String mailId);
	
//	List<UserToken> findDistinctByMailId();
}
