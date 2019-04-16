package com.shopperapp.dto;

import java.util.List;

import com.shopperapp.mongo.models.MailData;

import lombok.Data;

@Data
public class ListMailReponseDTO {
	private List<MailData> orderDatas;
	private List<MailData> shippedDatas;
}
