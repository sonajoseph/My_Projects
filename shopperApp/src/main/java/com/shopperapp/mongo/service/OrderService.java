package com.shopperapp.mongo.service;

import java.util.List;

import com.shopperapp.dto.ResponseDTO;
import com.shopperapp.mongo.models.MailData;

public interface OrderService {
	
	String changeOrderStatus(String vendor, String orderID);

}
