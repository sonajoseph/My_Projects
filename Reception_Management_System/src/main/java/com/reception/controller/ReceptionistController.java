package com.reception.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reception.dto.ReceptionistDTO;
import com.reception.service.ReceptionistService;

@RestController
@RequestMapping("/receptionist")
public class ReceptionistController {
	
	
	@Autowired 
	ReceptionistService receptionistService;
	@PostMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String registerReceptionist(@RequestBody ReceptionistDTO receptionist){
		receptionistService.saveReceptionist(receptionist);
		return "successfully registered";
		
	}
	

}
