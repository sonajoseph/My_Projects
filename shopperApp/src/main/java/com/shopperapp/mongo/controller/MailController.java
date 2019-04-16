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
import com.shopperapp.dto.VendorMailsCountDTO;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.service.GetAllMailsService;

@RestController
@RequestMapping("/google/mails/")
public class MailController {
	private static final Logger logger = LoggerFactory.getLogger(MailController.class);

	@Autowired
	private GetAllMailsService getAllMailService;

	@GetMapping("/list/{mailId}") //get all products mails which are not delivered.
	private ResponseDTO<List<List<MailData>>> listAllMails(@PathVariable String mailId) {
		logger.info("At listAllMails ...");
		return getAllMailService.getAllMails(mailId);
	}
	
	@GetMapping("/list/vendor/{mailId}")
	private ResponseDTO<List<VendorMailsCountDTO>> getCountOfVendorsMails(@PathVariable String mailId) {
		logger.info("At getCountOfVendorsMails................."+mailId);
		return getAllMailService.getCountOfVendorsMails(mailId);
	}
	
	@GetMapping("/list/vendor/orders/{vendorName}/{mailId}")
	private ResponseDTO<List<List<MailData>>> getListOfVendorMails(
			@PathVariable String mailId, 
			@PathVariable String vendorName) {
		logger.info("At getCountOfVendorsMails.................");
		logger.info("venderName :: "+vendorName+"\t mailId ::: "+mailId); 
		return getAllMailService.getListOfVendorMails(mailId,vendorName);
	}
	
	/*@GetMapping("/order/{orderId}")
	private ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {
		logger.info("At listAllMails ...");
		return new ResponseEntity(getAllMailService.getAllMailsByOrderId(orderId),HttpStatus.OK);
		
	}*/
	
}
