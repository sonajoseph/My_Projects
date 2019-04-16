package com.shopperapp.mongo.iDao;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.shopperapp.dto.VendorMailsCountDTO;
import com.shopperapp.mongo.dao.MailDataDao;
import com.shopperapp.mongo.models.MailData;

@Repository
public class MailDataDaoImpl implements MailDataDao {
	 
	private static final Logger logger = LoggerFactory.getLogger(MailDataDaoImpl.class);

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public List<VendorMailsCountDTO> vendorMailCount(String mailId)  {
		logger.info("At vendorMailCount.....");
		
//		TypedAggregation<MailData> aggregation = newAggregation(MailData.class,
//				match(Criteria.where("currentStatus").is("Ordered")),
//				group("vendor").count().as("ordersCount")
//				,
//				project("ordersCount").and("vendor").previousOperation()
////				sort(Direction.ASC, "currentStatusCode")
//				);
		
		Aggregation aggregation = newAggregation(
				match(Criteria.where("currentStatus").is("Delivered").and("toMaild").is(mailId)), 
				group("vendor").count().as("ordersCount"),
				project("ordersCount").and("vendor").previousOperation() 
					
			);
		 
			AggregationResults<VendorMailsCountDTO> results = mongoTemplate.aggregate(aggregation,MailData.class, VendorMailsCountDTO.class);
			List<VendorMailsCountDTO> list = (List<VendorMailsCountDTO>) results.getRawResults().get("results"); 
			System.out.println(list); 
		return list;
	}

	@Override
	public List<MailData> nonDeliveredMails() {
//		logger.info("At nonDeliveredMails.....");
//		Aggregation aggregation = newAggregation(
//				match(Criteria.where("currentStatus").is("Ordered").and("Packed").and("Shipped")),
//				group("vendor").count().as("ordersCount"),
//				project("ordersCount").and("vendor").previousOperation() 
//					
//			);
		
		return null;
	}
	

}
