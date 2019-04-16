package com.shopperapp.mongo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopperapp.dto.ResponseDTO;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.service.GetAllMailsService;
import com.shopperapp.mongo.service.OrderService;

@RestController
@RequestMapping("/order/")
public class OrderController {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private GetAllMailsService getAllMailService;

	
	@Autowired
	private OrderService orderServiceImpl;
	
	@GetMapping("/statusChange/{orderId}/{vendorName}")
	private ResponseEntity<?> statusChange(@PathVariable String orderId, @PathVariable String vendorName) {
		logger.info("At statusChange ...");
		
		ResponseDTO response = new ResponseDTO();
		
		response.setMessage(orderServiceImpl.changeOrderStatus(vendorName, orderId));
		
		return new ResponseEntity<ResponseDTO>(response, HttpStatus.OK);
	}
	

}
