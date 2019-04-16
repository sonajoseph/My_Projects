package com.reception.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.reception.model.Receptionist;

@Repository

public interface ReceptionistRepository extends JpaRepository<Receptionist,Integer>{
	Receptionist save(Receptionist receptionist);
	
	
	

}
