package com.shopperapp.mongo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shopperapp.mongo.models.Order;

@Repository
public interface OrderRepo extends MongoRepository<Order, String> {
    
	Order findOneByOrderNum(String orderNum);
	//orderNUm and vendorName is present in order table
	Order findOneByOrderNumAndVendorName(String orderNum, String vendorName);
	
}
