package com.shopperapp.mongo.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "order_data")
@Data
public class OrderDatas {
	@Id
	private String id;
	private String data;
	private String orderId;
	private String mailId;
	private Date date;
}
