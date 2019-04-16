package com.shopperapp.mongo.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shopperapp.dto.OrderItem;

import lombok.Data;

@Document(collection = "mail_data")
@Data
public class MailData {
	@Id
	private String id;
	private String fromMailId;
	private Date date;
	private String currentStatus;
	private Integer currentStatusCode; 
	/**
	 * 1 : Ordered
	 * 2 : Packed 
	 * 3 : Shipped
	 * 4 : Delivered
	 * 5 : Cancelled
	 */
//	private Date orderedDate;
//	private Date packedDate;
	private Date arrivesOn;
	private String orderNumber;
	private List<OrderItem> orderItemsList;
	private String toMaild; 
	private String vendor;
	private String itemsTotalsCost;
	private String itemsTotalDeliveryCharge;
	private String grandTotal;
	private String gstTotal;
	private String deliveryAddress;
//	private String delliveyBy;
	
	private Boolean arrivesOnNotificationStatus = false;
	
	@JsonIgnore
	private Date createdAt;
	@JsonIgnore
	private Date updatedAt;
	
}
