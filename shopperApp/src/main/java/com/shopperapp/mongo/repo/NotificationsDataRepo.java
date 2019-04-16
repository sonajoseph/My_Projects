package com.shopperapp.mongo.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shopperapp.mongo.models.NotificationsData;

public interface NotificationsDataRepo extends MongoRepository<NotificationsData, String> {
	
//	List<NotificationsData> findByEmailId(String emailId);
	List<NotificationsData> findByOrderId(String orderId); 
	
//	List<NotificationsData> findByEmailIdAndOrderId(String emailId ,String orderId);
//	List<NotificationsData> findByEmailIdAndOrderIdAndOrderStatus( String emailId ,String orderId, String orderStatus);
	
}
