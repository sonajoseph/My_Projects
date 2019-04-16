package com.shopperapp.mongo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shopperapp.mongo.models.OrderItemStatus;

@Repository
public interface OrderItemStatusRepo extends MongoRepository<OrderItemStatus, String> {

}
