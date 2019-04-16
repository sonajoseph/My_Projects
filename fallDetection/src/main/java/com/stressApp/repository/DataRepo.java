package com.stressApp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import com.stressApp.model.StressData;


@Repository

	
	public interface DataRepo extends MongoRepository<StressData, String> {
	public List<StressData> findAll();

}
