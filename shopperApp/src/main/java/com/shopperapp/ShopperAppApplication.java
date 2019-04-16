package com.shopperapp;

import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync

@EnableScheduling
public class ShopperAppApplication {
	@PostConstruct
	  public void init(){
	    // Setting Spring Boot SetTimeZone
	    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	  }
	public static void main(String[] args) {
		SpringApplication.run(ShopperAppApplication.class, args);
		System.err.println("--------------------------" + new Date());
		
	}
}
