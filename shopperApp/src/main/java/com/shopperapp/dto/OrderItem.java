package com.shopperapp.dto;

import java.util.Date;

import lombok.Data;

@Data
public class OrderItem {

	public String item;
	public String qty;
	public String subTotal;
	public Date deliveryDate;
	public String seller;
	public String deliveryCharges;
	
	
}
