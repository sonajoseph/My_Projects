package com.shopperapp.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OrderDetail {  
	List<OrderItem> orderItemsList;
	String subTotal;
	String deliveryFee;
	String grandTotal;
	String gstTotal;
	Date orderedDate;
	Date packedDate;  
	Date arrivesOn;
	Date deliveryDate;
	String currentStatus;
	Integer currentStatusCode;
	String orderNumber;
	String fromMailId;
	Date date;
    String itemsTotalDeliveryCharge;
	String toMaild;
	String vendor;
}
