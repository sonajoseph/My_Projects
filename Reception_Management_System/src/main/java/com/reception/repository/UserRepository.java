package com.reception.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

//import com.reception.model.Roles;
import com.reception.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer>{
	Users save(Users user);

	Users findByUsername(String userid);
//	Roles findByRole(Integer roleid);

}
