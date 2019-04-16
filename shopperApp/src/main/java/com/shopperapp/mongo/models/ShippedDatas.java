package com.shopperapp.mongo.models;


import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Document(collection = "shipped_datas")
@Data
public class ShippedDatas  {
	@Id
	private String id;
	private String data;
	private String orderId;
	private String mailId;
	private Date date;
}
