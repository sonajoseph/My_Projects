package com.reception.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reception.model.Events;
import com.reception.model.Users;
import com.reception.service.EventService;
import com.reception.service.HostService;
import com.reception.service.UserService;

@RestController
@RequestMapping("/admin")


public class UserController {
	@Autowired 
	UserService userservice;
	@Autowired 
	EventService eventservice;
	@Autowired 
	HostService hostservice;
	
	
	@PostMapping
	 
	  public String registerAdmin(@RequestBody Users  user) {
			System.out.println(user.toString());
			userservice.save(user);
			
			return "succesfully registered";
		}
	
}
