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

import com.reception.dto.HostDTO;
import com.reception.model.Category;
import com.reception.model.Events;
import com.reception.model.Hosts;
import com.reception.repository.HostRepository;
import com.reception.service.HostService;

@RestController
@RequestMapping("/hosts")
public class HostController {
	 
	  @Autowired
		 private HostService hostService;
	
	  
	  
	  
	  @PreAuthorize("hasRole('ROLE_RECEPTIONIST')")
	  @PostMapping
	 
	  public String registerHosts(@RequestBody HostDTO host) {
			System.out.println(host.toString());
			hostService.saveHost(host);
			return "succesfully entered";
		}
	    
	   
	   
	  @PreAuthorize("hasRole('ROLE_VISITOR')")
	  @GetMapping
		 
		  public List<Hosts> findAll() {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
//				List<String> collect = hostservice.findAll().stream().map(name -> name.getName()).collect(Collectors.toList());
//				collect.forEach(System.out::println);
				hostService.findAll().forEach(System.out::println);
				return hostService.findAll();
			}
	  
	  
	  @GetMapping("/{hostname}")
	  @PreAuthorize("hasRole('ROLE_VISITOR')")

	  public List<Hosts> findByHostname(@PathVariable String hostname ){
	  return hostService.findByHostname(hostname);
	  }
	 
//	   
	   
//		@GetMapping
//		
//	     public List<String> findHostByName(){
//   		  
//			return hostservice.findHostByName();
//			
//			
//		}
	  
	  
	 // @GetMapping
	   
//	  @PreAuthorize("hasRole('ROLE_VISITOR')")
	 
//	  public List<Hosts> findAll() {
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
//			
//			return hostservice.findAll();
//	  }
//


}
