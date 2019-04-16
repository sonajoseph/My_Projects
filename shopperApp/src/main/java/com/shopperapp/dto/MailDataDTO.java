package com.shopperapp.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MailDataDTO {
	private String id;
	private String data;
	private String orderId;
	private String mailId;
	private Date date;
}
