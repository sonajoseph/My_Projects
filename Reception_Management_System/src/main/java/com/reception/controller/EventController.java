package com.reception.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.reception.model.Events;
import com.reception.service.EventService;

  @RestController
  @RequestMapping("/events")

public class EventController {
	  
	  @Autowired
		EventService eventservice;
		
	
	  @PostMapping
	  @PreAuthorize("hasRole('ROLE_RECEPTIONIST')")
	  public String registerEvents(@RequestBody Events event) {
			System.out.println(event.toString());
			eventservice.save(event);
			
			return "succesfully entered";
		}
	  
	  @GetMapping("/all")
	  @PreAuthorize("hasRole('ROLE_VISITOR')")
	  public List<Events> findAllevents() {
		  System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
		  return eventservice.findAll();
		  }
	  
	  
	  @GetMapping("/{eventname}")
	  @PreAuthorize("hasRole('ROLE_VISITOR')")

	  public List<Events> findByEventname(@PathVariable String eventname ){
	  return eventservice.findByName(eventname);
	  }
	  
	  
	  
	  @GetMapping("/date/{date}")
	  
	  @PreAuthorize("hasRole('ROLE_VISITOR')")
	 
	  
	  public List<Events> findByDate(@PathVariable String date ){
	  return eventservice.findByDate(date);
	  
	  }
	  
	  @GetMapping("/roomno/{roomno}")
	  @PreAuthorize("hasRole('ROLE_VISITOR')")
	 
	  public List<Events> findByRoomno(@PathVariable String roomno ){
	  return eventservice.findByRoomno(roomno);
	  
	  }
	  

}
