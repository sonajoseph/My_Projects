package com.shopperapp.mongo.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shopperapp.mongo.models.MailData;

public interface MailDataRepo extends MongoRepository<MailData, String> {
	List<MailData> findByToMaild(String mailId);
	List<MailData> findByToMaildAndCurrentStatusNot(String mailId, String status);
	
	
	List<MailData> findByToMaildAndCurrentStatusCode(String mailId);
	List<MailData> findByToMaildOrderByDateDesc(String mailId);
	List<MailData> findByToMaildAndVendor(String mailId,String vendor); 

	List<MailData> findByToMaildAndVendorAndCurrentStatus(String mailId,String vendor, String status); 
	List<MailData> findByOrderNumber(String orderId);
	List<MailData> findByOrderNumberOrderByCurrentStatusCode(String orderId);
	List<MailData> findByOrderNumberAndCurrentStatusNot(String orderId, String status); 
	List<MailData> findByOrderNumberAndCurrentStatus(String orderId, String status);
	MailData findOneByOrderNumberAndVendor(String orderId, String vendorName);
	MailData findOneByOrderNumberAndVendorAndCurrentStatus(String orderId, String vendorName, String currentStatus);
//	List<MailData> findByOrderNumberOrderByCurrentStatusCodeAsc(String orderId);
	MailData findOneByOrderNumberAndVendorAndCurrentStatusCode(String orderId, String vendorName, Integer currentStatusCode);
	
	List<MailData> findByCurrentStatusAndArrivesOnGreaterThan(String currentStatus, Date arrivesOn);
	List<MailData> findByCurrentStatusAndArrivesOnNotificationStatusAndArrivesOnGreaterThan(String currentStatus,Boolean status, Date arrivesOn);
}
