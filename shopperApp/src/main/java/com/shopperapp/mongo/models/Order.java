package com.shopperapp.mongo.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "order")
@Data
public class Order {
	@Id
	private String id;

	private String orderNum;

	private String userEmailId;

	private Date createdDate;

	private List<OrderItems>  orderItems;
	
	private String vendorName;
	private String fromMailId; 
	
	private String currencySymbol;
	private double itemsTotalsCostCal;
	private double itemsTotalDeliveryChargeCal;
	private double grandTotalCal;
	
	private String itemsTotalsCost;
	private String itemsTotalDeliveryCharge;
	private String grandTotal;
	private String gstTotal;
	private String deliveryAddress;
	
	
	private String trackIt;
	private String contactSeller;
	
	private Boolean arrivesOnNotificationStatus = false;
	
	 

}
