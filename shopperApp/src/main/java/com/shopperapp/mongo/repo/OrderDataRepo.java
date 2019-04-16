package com.shopperapp.mongo.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shopperapp.mongo.models.OrderDatas;

public interface OrderDataRepo extends MongoRepository<OrderDatas, String> {
	List<OrderDatas> findByMailId(String mailid);

}
