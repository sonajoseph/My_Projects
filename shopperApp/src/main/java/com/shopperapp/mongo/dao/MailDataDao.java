package com.shopperapp.mongo.dao;

import java.util.List;

import com.shopperapp.dto.VendorMailsCountDTO;
import com.shopperapp.mongo.models.MailData;

public interface MailDataDao {

	
    List<VendorMailsCountDTO> vendorMailCount(String mailId);
    
    List<MailData> nonDeliveredMails();

    
}
