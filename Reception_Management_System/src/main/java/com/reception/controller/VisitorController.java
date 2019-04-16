package com.reception.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.reception.dto.VisitorDto;
import com.reception.model.Category;
import com.reception.model.Events;
import com.reception.model.Hosts;
import com.reception.model.Visitors;
import com.reception.repository.CategoryRepository;
import com.reception.service.CategoryService;
import com.reception.service.EventService;
import com.reception.service.HostService;
import com.reception.service.VisitorService;

@RestController
@RequestMapping("/visitors")

public class VisitorController {
	@Autowired
	VisitorService visitorservice;
	@Autowired
	HostService hostService;
	@Autowired
	EventService eventService;
	@Autowired
	CategoryService categoryService;
	
	
	

	@PostMapping
	@PreAuthorize("hasAnyRole('ROLE_RECEPTIONIST','ROLE_HOST')")
	public String registerVisitors(@RequestBody VisitorDto visitor) {
		System.out.println(visitor.toString());
		visitorservice.saveVisitor(visitor);
		return "succesfully entered";
	}
	@GetMapping("/category")
	@PreAuthorize("hasRole('ROLE_VISITOR')")
	public List<Category> findAll(){
		return categoryService.findAll();
		
	}
	
	@GetMapping("/choosecategory/{item}")
	@PreAuthorize("hasRole('ROLE_VISITOR')")
	 public List<?> findByRoomno(@PathVariable String item ){
	
     if(item.equalsIgnoreCase("meetstaff")){
		  return hostService.findAll();
	  }
	  else if(item.equalsIgnoreCase("events")){
		  return eventService.findAll();
	  }
	
     return null;
	
	
	

}
	
//	@GetMapping("/monthlyvisitors/{type}/{i}/{date1}/{date2}")
	@GetMapping("/monthlyvisitors/{type}/{date1}/{date2}")
	@PreAuthorize("hasRole('ROLE_VISITOR')")
//
//		public List<?> findByDate(@PathVariable String type,@PathVariable Integer i,@PathVariable String date1, @PathVariable String date2) 
//				throws ParseException{
		public List<?> findByDate(@PathVariable String type,@PathVariable String date1, @PathVariable String date2) throws ParseException{
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date date11 = format.parse(date1);
		Date date12 = format.parse(date2);
//     if(type.equalsIgnoreCase("eventid")){
//		   return  visitorservice.findByDate(i,date11, date12);
//	  }
//	  else if(type.equalsIgnoreCase("hostid")){
//		  return hostService.findByDate(i,date11,date12);
//	  
//	  }
//     return null;
//	
		 if(type.equalsIgnoreCase("events")){
			   return  visitorservice.findByEvents(date11, date12);
		  }
		  else if(type.equalsIgnoreCase("hosts")){
			  return visitorservice.findByHosts(date11,date12);
		  
		  }
	     return null;
	
	

}

	@GetMapping("/monthlyvisitors/{date1}/{date2}")
	
	
	@PreAuthorize("hasRole('ROLE_VISITOR')")
	public List<Visitors> findByDate(@PathVariable String date1, @PathVariable String date2) throws ParseException{
	
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date date11 = format.parse(date1);
		Date date12 = format.parse(date2);
		System.out.println(date11+"date1)))))))))))))))))))))))))))))"); // Sat Jan 02 00:00:00 GMT 2010
		System.out.println(date12+"date2)))))))))))))))))))))))))))))");
		
		
		return  visitorservice.findByDate(date11, date12);
	
	}


	

	//
	// @GetMapping("/{name}")
	// @PreAuthorize("hasRole('ROLE_VISITOR')")
	//// @RequestMapping(value = "/category/meetstaff/name/{name}", method = {
	// RequestMethod.GET })
	// public List<Hosts> findByName(@PathVariable String name) {
	// System.out.println(">>>>>>>>>>>>>>>>>>>>>>" + name);
	//
	// return hostservice.findByName(name);
	// }
	//
	// @GetMapping("/{designation}")
	// @PreAuthorize("hasRole('ROLE_VISITOR')")
	//// @RequestMapping(value =
	// "/category/meetstaff/designation/{designation}", method = {
	// RequestMethod.GET })
	// public List<Hosts> findByDesignation(@PathVariable String designation) {
	// return hostservice.findByDesignation(designation);
	// }
	//
	//
	// @GetMapping("/{department}")
	// @PreAuthorize("hasRole('ROLE_VISITOR')")
	//// @RequestMapping(value = "/category/meetstaff/department/{department}",
	// method = { RequestMethod.GET })
	// public List<Hosts> findByDepartment(@PathVariable String department) {
	// return hostservice.findByDepartment(department);
	// }
	//
	

	
	
    
	
	}

	
	

	
	
	
	
	
	
	
	
	
	


