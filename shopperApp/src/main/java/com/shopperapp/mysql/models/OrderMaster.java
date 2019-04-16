package com.shopperapp.mysql.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity

@Table (name="OrderMaster")
public class OrderMaster {
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO ) 
	private Long orderMasterId;
	private String orderId;
	private String deliveryAddress;
	private Long itemsCount;  
	private String totalPrice;
	private String totalDeliveryCharge;
	private String totalGst;
	private String finalTotal;
	
	private String vendorName;
	private String userMailId;
	private Date orderDate;
	
	private Date createdAt;
	private Date updatedAt;
	
	
	

}
