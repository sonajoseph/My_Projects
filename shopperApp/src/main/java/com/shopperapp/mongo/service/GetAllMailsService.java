package com.shopperapp.mongo.service;

import java.util.List;

import com.shopperapp.dto.ResponseDTO;
import com.shopperapp.dto.VendorMailsCountDTO;
import com.shopperapp.mongo.models.MailData;

public interface GetAllMailsService {

	ResponseDTO<List<List<MailData>>> getAllMails(String mailId);
	
	ResponseDTO<List<VendorMailsCountDTO>> getCountOfVendorsMails(String mailId);
	ResponseDTO<List<List<MailData>>> getListOfVendorMails(String mailId, String vendorName);

//	ResponseDTO<List<List<MailData>>> getAllMailsByOrderId(String orderId);
	

}
