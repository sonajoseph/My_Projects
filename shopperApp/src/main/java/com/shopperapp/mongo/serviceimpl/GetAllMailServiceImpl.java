package com.shopperapp.mongo.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopperapp.dto.ResponseDTO;
import com.shopperapp.dto.VendorMailsCountDTO;
import com.shopperapp.mongo.dao.MailDataDao;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.repo.MailDataRepo;
import com.shopperapp.mongo.repo.OrderDataRepo;
import com.shopperapp.mongo.repo.ShippedDatasRepo;
import com.shopperapp.mongo.service.GetAllMailsService;

@Service
public class GetAllMailServiceImpl implements GetAllMailsService {
	
	private static final Logger logger = LoggerFactory.getLogger(GetAllMailServiceImpl.class);

	@Autowired
	private OrderDataRepo orderDatasRepo;
	@Autowired
	private ShippedDatasRepo shippedDatasRepo;
	@Autowired
	private MailDataRepo mailDataRepo;
	
	@Autowired
	private MailDataDao mailDataDao;
	
	@Override 
	public ResponseDTO<List<List<MailData>>> getAllMails(String mailId) {
		logger.info("At  getAllMails.....");
		
		List<List<MailData>> listOLists = new ArrayList<List<MailData>>();
		ResponseDTO<List<List<MailData>>> responseDTO = new ResponseDTO<>();
		//fetch all the items mail that are not delivered.
		List<MailData> findByFromMailId = mailDataRepo.findByToMaildAndCurrentStatusNot(mailId, "Delivered");
		
		List<String> orderNosList=new ArrayList<String>();
		
		for(int i=0;i<findByFromMailId.size();i++) {
			Boolean exist=false;
			for( int j=0;j<orderNosList.size() ;j++) {
				 if(orderNosList.get(j).equalsIgnoreCase(findByFromMailId.get(i).getOrderNumber())) {
					 exist=true;
					 break;
				 }
			}
			if(!exist) {
				orderNosList.add(findByFromMailId.get(i).getOrderNumber());
			}
		}
		
//		Set<String>orderIds = findByFromMailId.stream().map(data -> data.getOrderNumber()).collect(Collectors.toSet());
		orderNosList.forEach(data -> {
			
			List<MailData> findByOrderNumber = mailDataRepo.findByOrderNumberAndCurrentStatus(data, "Delivered");
			if(findByOrderNumber == null || findByOrderNumber.isEmpty()) {
				List<MailData> findByOrder = mailDataRepo.findByOrderNumberOrderByCurrentStatusCode(data);
				listOLists.add(findByOrder);
			}
			
		});
		
		listOLists.forEach(System.err::println);
		Collections.reverse(listOLists);
		
		responseDTO.setData(listOLists);
		
		return responseDTO;
	}
	@Override
	public ResponseDTO<List<VendorMailsCountDTO>> getCountOfVendorsMails(String mailId) {
		logger.info("At  getCountOfVendorsMails.....");

		ResponseDTO returnDto= new ResponseDTO();
		List<VendorMailsCountDTO> returnList =  mailDataDao.vendorMailCount(mailId);
		
		returnDto.setData(returnList);
		
		return returnDto;
	}
	@Override
	public ResponseDTO<List<List<MailData>>> getListOfVendorMails(String mailId, String vendorName) {
		logger.info("At  getListOfVendorMails.....");
		List<List<MailData>> listOLists = new ArrayList<List<MailData>>();
		ResponseDTO<List<List<MailData>>> responseDTO = new ResponseDTO<>(); 
		List<MailData> findByFromMailId = mailDataRepo.findByToMaildAndVendor(mailId, vendorName);
		findByFromMailId.forEach(System.err::println);
//		Set<String>orderIds = findByFromMailId.stream().map(data -> data.getOrderNumber()).collect(Collectors.toSet());
		
		List<String> orderNosList=new ArrayList<String>();
		for(int i=0;i<findByFromMailId.size();i++) {
			Boolean exist=false;
			for( int j=0;j<orderNosList.size() ;j++) {
					 if(orderNosList.get(j).equalsIgnoreCase(findByFromMailId.get(i).getOrderNumber())) {
						 exist=true;
						 break;
					 }
			}
			if(!exist) {
				orderNosList.add(findByFromMailId.get(i).getOrderNumber());
			}
		}
		

		orderNosList.forEach(data -> {
			List<MailData> findByOrderNumber = mailDataRepo.findByOrderNumberAndCurrentStatus(data, "Delivered");
			if(findByOrderNumber.size()>0 && findByOrderNumber != null) {
				List<MailData> findByOrder = mailDataRepo.findByOrderNumberOrderByCurrentStatusCode(data);
				listOLists.add(findByOrder);
			}
			
		});
//		Collections.reverse(listOLists);
		
		responseDTO.setData(listOLists);
		
		return responseDTO;
	}
	/*@Override
	public ResponseDTO<List<List<MailData>>> getAllMailsByOrderId(String orderId) {
       logger.info("At  getAllMailsByOrderId.....");
		
		List<List<MailData>> listOLists = new ArrayList<List<MailData>>();
		ResponseDTO<List<List<MailData>>> responseDTO = new ResponseDTO<>();
//		List<MailData> findByFromMailId = mailDataRepo.findByToMaildAndCurrentStatusCode(mailId);
		List<MailData> findByFromOrderId = mailDataRepo.findByOrderNumberOrderByCurrentStatusCodeAsc(orderId);
		
		return (ResponseDTO<List<List<MailData>>>) findByFromOrderId;
	}*/

}
