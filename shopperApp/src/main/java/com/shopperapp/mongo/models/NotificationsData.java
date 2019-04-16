package com.shopperapp.mongo.models;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
@Document(collection = "notificationsData")
@Data
public class NotificationsData {
	 
	@Id
	private String id;
	private String userDeviceId;
//	private String emailId;
	private String message;
	private String orderId;
//	private String type;
//	private String vendorName;
	@JsonIgnore
	private Date createdAt;
	@JsonIgnore
	private Date updatedAt;
		
	}