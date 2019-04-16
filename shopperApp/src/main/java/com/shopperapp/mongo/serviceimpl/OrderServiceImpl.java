package com.shopperapp.mongo.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopperapp.dto.ResponseDTO;
import com.shopperapp.mongo.controller.OrderController;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.repo.MailDataRepo;
import com.shopperapp.mongo.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	MailDataRepo mailDataRepo;
	
	@Override  
	public String  changeOrderStatus(String vendor, String orderID) {
		logger.info("At  changeOrderStatus.....orderID :: "+orderID+"\t vendor : "+vendor); 
		
		MailData dataDB = mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatusCode(orderID, vendor,1);
		try {
		if(dataDB != null) {
			MailData dataDB3 = mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatusCode(orderID, vendor,3);
			System.err.println(dataDB3);
			if(dataDB3 != null) { 
				// shipped mailis there but delivered mail  doesn't arrived.manually we set mark as delivered.
				MailData dataDelivered = new MailData();
				
				dataDelivered.setCurrentStatus("Delivered");
				dataDelivered.setCurrentStatusCode(4);
				dataDelivered.setOrderNumber(orderID);
				dataDelivered.setOrderItemsList(dataDB.getOrderItemsList());
				dataDelivered.setFromMailId(dataDB.getFromMailId());
				dataDelivered.setVendor(dataDB.getVendor());
				dataDelivered.setToMaild(dataDB.getToMaild());
				dataDelivered.setDeliveryAddress(dataDB.getDeliveryAddress());
				dataDelivered.setDate(new Date());
				dataDelivered.setGrandTotal(dataDB.getGrandTotal());
				dataDelivered.setItemsTotalDeliveryCharge(dataDB.getItemsTotalDeliveryCharge());
				dataDelivered.setItemsTotalsCost(dataDB.getItemsTotalsCost());
				dataDelivered.setArrivesOn(dataDB.getArrivesOn());
				MailData delivedDb = mailDataRepo.save(dataDelivered);
				 if(delivedDb != null) {
					 return "Success";
				 } 
					 return "Failed";
			}
			else { 
			//	in case shipped mail is not there..manually marked it   as shipped and delivered. 
				MailData dataDelivered = new MailData();
				
				dataDelivered.setCurrentStatus("Delivered");
				dataDelivered.setCurrentStatusCode(4);
				dataDelivered.setOrderNumber(orderID);
				dataDelivered.setOrderItemsList(dataDB.getOrderItemsList());
				dataDelivered.setFromMailId(dataDB.getFromMailId());
				dataDelivered.setVendor(dataDB.getVendor());
				dataDelivered.setToMaild(dataDB.getToMaild());
				dataDelivered.setDeliveryAddress(dataDB.getDeliveryAddress());
				dataDelivered.setGrandTotal(dataDB.getGrandTotal());
				dataDelivered.setItemsTotalDeliveryCharge(dataDB.getItemsTotalDeliveryCharge());
				dataDelivered.setItemsTotalsCost(dataDB.getItemsTotalsCost());
				dataDelivered.setArrivesOn(dataDB.getArrivesOn());
				dataDelivered.setDate(new Date());
				
				MailData delivedDb = mailDataRepo.save(dataDelivered);
				
				MailData dataDelivered1 = new MailData();
				dataDelivered1.setCurrentStatus("Shipped"); 
				dataDelivered1.setCurrentStatusCode(3);
				dataDelivered1.setOrderNumber(orderID);
				dataDelivered1.setOrderItemsList(dataDB.getOrderItemsList());
				dataDelivered1.setFromMailId(dataDB.getFromMailId());
				dataDelivered1.setVendor(dataDB.getVendor());
				dataDelivered1.setToMaild(dataDB.getToMaild());
				dataDelivered1.setDeliveryAddress(dataDB.getDeliveryAddress());
				dataDelivered1.setGrandTotal(dataDB.getGrandTotal());
				dataDelivered1.setItemsTotalDeliveryCharge(dataDB.getItemsTotalDeliveryCharge());
				dataDelivered1.setItemsTotalsCost(dataDB.getItemsTotalsCost());
				dataDelivered1.setArrivesOn(dataDB.getArrivesOn());
				dataDelivered1.setDate(new Date());
				delivedDb = mailDataRepo.save(dataDelivered1);
				
				
				if(delivedDb != null) {
					return "Success";
				} 
				return "Failed"; 
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
		} 
		 return "Failed";
	}

}
