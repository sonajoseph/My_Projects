package com.shopperapp.mongo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shopperapp.mongo.models.OrderItems;

@Repository
public interface OrderItemsRepo extends MongoRepository<OrderItems, String> {

}
