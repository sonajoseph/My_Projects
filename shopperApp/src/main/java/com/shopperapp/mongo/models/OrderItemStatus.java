package com.shopperapp.mongo.models;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "orderItemsStatus")
@Data
public class OrderItemStatus {
	
	@Id
	private String id;
	
//	private String orderItemId;
	private String orderNumber;
	
	private String currentStatus;
	private int currentStatusCode;
	
	private Date crDate;
	

}
