package com.reception.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reception.model.Role;



@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	//public Roles save (Roles role);
	
	

}
