package com.shopperapp.mongo.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shopperapp.mongo.models.ShippedDatas;
@Repository
public interface ShippedDatasRepo extends MongoRepository<ShippedDatas, String> {
	public List<ShippedDatas> findByMailId(String mailid);
}
