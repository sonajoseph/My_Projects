package com.shopperapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.websocket.server.PathParam;

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
import com.shopperapp.mongo.models.NotificationsData;
import com.shopperapp.mongo.repo.MailDataRepo;
import com.shopperapp.mongo.repo.NotificationsDataRepo;
import com.shopperapp.mysql.models.UserToken;
import com.shopperapp.mysql.repo.UserTokenRepo;

@RestController
@RequestMapping("/dev")
public class DevelopersController {
	private static final Logger logger = LoggerFactory.getLogger(DevelopersController.class);

	
	@Autowired
	UserTokenRepo userTokenRepo;
	
	@Autowired
	MailDataRepo mailDataRepo;
	
	@Autowired
	NotificationsDataRepo notificationsDataRepo;
	
	@GetMapping("/userToken/all")
	public ResponseEntity<?> getUserTokensList(){
		logger.info("At getUserTokensList" );
		List<UserToken> usersList = userTokenRepo.findAll();
		return new ResponseEntity<List<UserToken>>(usersList, HttpStatus.OK);
//		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/userToken/user/{emailId}")
	public ResponseEntity<?> getUserTokenData(@PathVariable String emailId){
		logger.info("At getUserTokenData" );
		
		UserToken users = userTokenRepo.findByMailId(emailId);
		return new ResponseEntity<UserToken>(users, HttpStatus.OK);
	}
	
	
	@GetMapping("/mailData/user/{emailId}")
	public ResponseEntity<?> getUserMailData(@PathVariable String emailId){
		logger.info("At getUserMailData" );
		
		List<MailData> mailDataList = mailDataRepo.findByToMaild(emailId);
		return new ResponseEntity<List<MailData>>(mailDataList, HttpStatus.OK);
	}
	
	@GetMapping("/mailData/userOrderGroup/{emailId}")
	public ResponseEntity<?> getUserOrderGroup(@PathVariable String emailId){
		logger.info("At getUserOrderGroup" );
		

		ResponseDTO<List<List<MailData>>> responseDTO = new ResponseDTO<>();
		try {

			logger.info("At  getAllMails.....");
			
			List<List<MailData>> listOLists = new ArrayList<List<MailData>>();
			List<MailData> findByFromMailId = mailDataRepo.findByToMaild(emailId);
			
			List<String> orderNosList=new ArrayList<String>();
			
			for(int i=0;i<findByFromMailId.size();i++) {
				Boolean exist=false;
				for( int j=0;j<orderNosList.size() ;j++) {
					 if(orderNosList.get(j).equalsIgnoreCase(findByFromMailId.get(i).getOrderNumber())) {
						 exist=true;
						 break;
					 }
				}
				if(!exist) {
					orderNosList.add(findByFromMailId.get(i).getOrderNumber());
				}
			}
			orderNosList.forEach(data -> {
					List<MailData> findByOrder = mailDataRepo.findByOrderNumberOrderByCurrentStatusCode(data);
					listOLists.add(findByOrder);
			});
			listOLists.forEach(System.err::println);
			Collections.reverse(listOLists);
			responseDTO.setData(listOLists);
			
		}catch(Exception e0) {
		}
		return new ResponseEntity<ResponseDTO<List<List<MailData>>>>(responseDTO, HttpStatus.OK);
	}
	
	@GetMapping("/mailData/orderDetails/{orderId}")
	public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){
		logger.info("At getOrderDetails" );
		

		ResponseDTO<List<List<MailData>>> responseDTO = new ResponseDTO<>();
		try {

			logger.info("At  getAllMails.....");
			
			List<List<MailData>> listOLists = new ArrayList<List<MailData>>();
			
			  
			List<MailData> findByOrder = mailDataRepo.findByOrderNumberOrderByCurrentStatusCode(orderId);
			listOLists.add(findByOrder);
			Collections.reverse(listOLists);
			responseDTO.setData(listOLists);
			
		}catch(Exception e0) {
		}
		 
		return new ResponseEntity<ResponseDTO<List<List<MailData>>>>(responseDTO, HttpStatus.OK);
	}
	
	@GetMapping("/notifications/order/{orderId}")
	public ResponseEntity<?> getOrderNotifications(@PathVariable String orderId){
		logger.info("At getOrderNotifications" );
		ResponseDTO<List<NotificationsData>> responseDTO = new ResponseDTO<>();
		List<NotificationsData> findByOrder = notificationsDataRepo.findByOrderId(orderId);
		return new ResponseEntity<List<NotificationsData>>(findByOrder, HttpStatus.OK);
	}
	
	@GetMapping("/notifications/allOrder")
	public ResponseEntity<?> getAllOrderNotifications(){
		logger.info("At getAllOrderNotifications" );
		ResponseDTO<List<NotificationsData>> responseDTO = new ResponseDTO<>();
		List<NotificationsData> findByOrder = notificationsDataRepo.findAll();
		return new ResponseEntity<List<NotificationsData>>(findByOrder, HttpStatus.OK);
	}

	

}
