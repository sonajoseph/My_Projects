package com.shopperapp.mongo.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "orderItems")
@Data
public class OrderItems {

	@Id 
	private Long id;
	private String orderId;
	private String productName;
	private int qty;
	private String price;
	private String unitPrice;
	private String savings;
	private String deliveryCharge;
	private String deliveryChargeDiscount;
	private String sellerName;
	private String description;
	private String size;
	private String discountAmount;
	private Date deliveryDate; 
	
	private List<OrderItemStatus> status;
	private Date createdAt;
	private Date updatedAt;
}
