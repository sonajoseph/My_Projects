package com.democonfiguration.controller;

import com.democonfiguration.dto.UserDto;
import com.democonfiguration.model.User;
import com.democonfiguration.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @ApiOperation(value = "all users")
    @RequestMapping(value="/user", method = RequestMethod.GET)
//    @Api(value="user resource",description="shows all users")
    public List<User> listUser(){
        return userService.findAll();
    }
    
    @ApiOperation(value = " user with specified id")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User getOne(@PathVariable(value = "id") Long id){
        return userService.findById(id);
    }
    
    @ApiOperation(value = " register the user")
    @RequestMapping(value="/signup", method = RequestMethod.POST)
    public User saveUser(@RequestBody UserDto user){
    	System.out.println(">>>>>>>>>>>>>>>>>>>"+user.toString());
    	return userService.save(user);
    }
    
    
    
    
   
    
    

}
