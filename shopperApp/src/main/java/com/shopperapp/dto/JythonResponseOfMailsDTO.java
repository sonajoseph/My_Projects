package com.shopperapp.dto;

import java.util.List;

import com.shopperapp.mongo.models.MailData;

import lombok.Data;

@Data
public class JythonResponseOfMailsDTO {

	String orderNumber;
	String grandTotal;
	String fromMailId;
	String deliveryAddress;
	String date;
	String currentStatus;
	String arrivesOn;
	String vendor;
	private List<OrderItem> orderItemsList;

	
}
