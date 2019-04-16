package com.shopperapp.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.client.util.StringUtils;
import com.shopperapp.components.DateComponents;
import com.shopperapp.components.NotificationMessageFCM;
import com.shopperapp.dto.MessageResponseDTO;
import com.shopperapp.dto.RawMessageResponseDTO;
import com.shopperapp.mongo.models.Order;
import com.shopperapp.mongo.models.OrderItemStatus;
import com.shopperapp.mongo.models.OrderItems;
import com.shopperapp.mongo.repo.MailDataRepo;
import com.shopperapp.mongo.repo.OrderRepo;
import com.shopperapp.service.SnapdelMailService;

@Service
public class SnapdelMailServiceImpl implements SnapdelMailService {
	private static final Logger logger = LoggerFactory.getLogger(SnapdelMailServiceImpl.class);

	
	@Autowired
	NotificationMessageFCM notificationMessageFCM;
	
	@Autowired
	MailDataRepo mailDataRepo;
	
	@Autowired
	DateComponents dateComponent;
	
	@Autowired
	OrderRepo orderRepo;
	
	
	@Override
	public void readDataFromSnapdeal(  String userMailId, String subjectData, Date date,
			String sellerMailId, String deviceId, RawMessageResponseDTO rawMessageResponseDTO,
			ResponseEntity<MessageResponseDTO> forEntity) {
		logger.info("Now at readDataFromSnapdeal.......");
		
		
		
		System.out.println("Subject Data ------------> " + subjectData);
		
		String mailDataBody= forEntity.getBody().getPayload().getParts().get(0).getParts().get(0).getBody().getData();  
		System.err.println("mailDataBody : "+mailDataBody); 
		
		String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));

		
		try {
			Document doc = Jsoup.parseBodyFragment(decodedMail1);
			
			
			List<OrderItems> orderItemsList = new ArrayList<>();
			if (subjectData.contains("confirmed")) {
				
				Element ordNumTable = doc.select("table").get(8).select("tr").get(0).select("td").get(0);
				System.err.println("ordNumTable  :::::: \n \n "+ordNumTable.text()+"\n");
				
				String orderNumber=ordNumTable.text().replaceAll("[^\\d.]", "");
				System.err.println("ordNumTable  ::::::   "+orderNumber);
				
				Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "SnapDeals");
				if(order == null) {
					order =  new Order();
					
					Element deliveryAddresstable = doc.select("table").get(11).select("tr").get(0).select("td").get(1);
//					System.err.println("deliveryAddresstable  :::::: \n \n "+deliveryAddresstable.toString()+"\n");
					String deliveryAddress = deliveryAddresstable.text().replaceFirst("Delivery Address ", "");
					System.err.println("deliveryAddress  ::::::   "+deliveryAddress);
					
					order.setOrderNum(orderNumber);
					order.setVendorName("SnapDeals");
					order.setDeliveryAddress(deliveryAddress);
					order.setCreatedDate(new Date());
					order.setUserEmailId(userMailId);
					order.setFromMailId(sellerMailId);
					
				
				
				
					Element totalPricetable = doc.select("table").get(16);
	//				System.err.println("totalPricetable  :::::: \n \n "+totalPricetable.toString()+"\n");
	//				String deliveryAddress = deliveryAddresstable.text().replaceFirst("Delivery Address ", "");
	//				System.err.println("deliveryAddress  ::::::   "+deliveryAddress);
					
					String itemsTotalsCost = "";
					if(totalPricetable.select("tr").get(0).select("td").get(1).text().contains("Total Price")) {
						itemsTotalsCost = totalPricetable.select("tr").get(0).select("td").get(2).text();
						
						order.setItemsTotalsCost(itemsTotalsCost);
					}
					
					String itemsTotalDeliveryCharge = "";
					if(totalPricetable.select("tr").get(1).select("td").get(1).text().contains("Delivery Charges")) {
						itemsTotalDeliveryCharge = totalPricetable.select("tr").get(1).select("td").get(2).text();
						
						order.setItemsTotalDeliveryCharge(itemsTotalDeliveryCharge);
					}
					
					String grandTotal = ""; 	
					if(totalPricetable.select("tr").get(2).text().contains("You Need to Pay")) {
						grandTotal = totalPricetable.select("tr").get(2).select("table").get(0).select("tr").get(0).select("td").get(2).text();
						
						order.setGrandTotal(grandTotal);
					}
	
//					System.err.println("itemsTotalsCost ::: "+itemsTotalsCost);
//					System.err.println("itemsTotalDeliveryCharge ::: "+itemsTotalDeliveryCharge); 
//					System.err.println("grandTotal ::: "+grandTotal);
					
					int itemsTableIndex = 22;
					Element itemsTable = doc.select("table").get(itemsTableIndex);
	
	//				System.err.println("itemsTable  :::::: \n \n "+itemsTable.toString()+"\n");
					
					while(itemsTable.text().contains("Quantity") && itemsTable.text().contains("Price") && itemsTable.text().contains("Delivery Charge")) {
						
						OrderItems orderItem = new OrderItems();
						orderItem.setOrderId(orderNumber);
						orderItem.setCreatedAt(new Date());
						
						
						Elements itemsTables=itemsTable.select("table");
						
						System.err.println("itemsTables size :::: "+itemsTables.size());
						int totalItems=(itemsTables.size()-1)/5;
						System.err.println("Total Items : "+totalItems);
						for(int i=1; i<=totalItems; i++) {
							
							itemsTableIndex = itemsTableIndex+3;
							
							Element itemDetails = doc.select("table").get(itemsTableIndex);
							
							String itemName="";
							
							itemName = itemDetails.select("tr").get(0).text();
//							System.err.println("Item Name : "+itemName);
							
							orderItem.setProductName(itemName);
							
							
							String deliveyDateStr=itemDetails.select("tr").get(1).text();
							System.err.println("deliveyDateStr : "+deliveyDateStr);
							
//							orderItem.setDeliveryDate(deliveryDate);
							
							String itemPrice=itemDetails.select("tr").get(4).select("table").get(0).select("tr").get(0).select("td").get(1).text();
							System.err.println("itemPrice : "+itemPrice);
							
							orderItem.setPrice(itemPrice);
							
							String itemDeliveryPrice=itemDetails.select("tr").get(4).select("table").get(0).select("tr").get(1).select("td").get(1).text();
							System.err.println("itemDeliveryPrice : "+itemDeliveryPrice);
							
							orderItem.setDeliveryCharge(itemDeliveryPrice);
							
							
							String itemQuantity=itemDetails.select("tr").get(4).select("table").get(0).select("tr").get(2).select("td").get(1).text();
							System.err.println("itemQuantity : "+itemQuantity);
							orderItem.setQty(Integer.parseInt(itemQuantity));
							
							OrderItemStatus orderItemStatus = new OrderItemStatus();
							orderItemStatus.setOrderNumber(orderNumber);
							orderItemStatus.setCurrentStatus("Ordered");
							orderItemStatus.setCurrentStatusCode(1); 
							orderItemStatus.setCrDate(new Date());
							
							 List<OrderItemStatus> status=new ArrayList<OrderItemStatus>();
							 status.add(orderItemStatus);
							 
							 
							orderItem.setStatus(status);
						}
						orderItemsList.add(orderItem);
						itemsTableIndex = itemsTableIndex + itemsTables.size() - 3;
						itemsTable = doc.select("table").get(itemsTableIndex);
					}
					
					order.setOrderItems(orderItemsList);
					
					orderRepo.save(order);
					
				}
			} 
			else if(subjectData.contains("cancelled") && doc.select("table").get(5).text().contains("Your order has been cancelled")) {
				logger.info("Now at Cancelled..... \n\n\n\n "+doc.select("table").get(5).select("tr").get(0).select("td").get(0).text().split(",")[1]);
				//Your order has been cancelled 
				
				String orderNumber = doc.select("table").get(8).select("tr").get(0).select("td").get(0).text().replaceAll("[^\\d.]", "");
				logger.info("orderNumber :: "+orderNumber);
				String productName = doc.select("table").get(12).text();
				logger.info("productName :: "+productName);
				try {
					Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "SnapDeals");
					System.err.println("order : "+order);
					if(order != null) {
						for(int i=0; i<order.getOrderItems().size(); i++) {
							if(order.getOrderItems().get(i).getProductName().equalsIgnoreCase(productName)) {
								List <OrderItemStatus> statusList=order.getOrderItems().get(i).getStatus();
								Boolean packedData =false;
								for(int j=0; j< statusList.size(); j++) {
									if(statusList.get(j).getCurrentStatus().equalsIgnoreCase("Cancelled")) {
										packedData=true;
									}
								}
								//if packedData!=true.
								if(!packedData) {
									OrderItemStatus orderItemsStatus = new OrderItemStatus();
									orderItemsStatus.setCrDate(new Date());
									orderItemsStatus.setCurrentStatusCode(5);
									orderItemsStatus.setCurrentStatus("Cancelled");
									orderItemsStatus.setOrderNumber(orderNumber);
									statusList.add(orderItemsStatus);
									order.getOrderItems().get(i).setStatus(statusList);
									orderRepo.save(order);
								}
							}
						}
					}
				}catch(Exception e2) {
					e2.getMessage();
					e2.printStackTrace();
				}
			}else if(subjectData.contains("ready to go") && doc.select("table").get(5).text().contains("is ready to be shipped") ) {
				String orderNumber = doc.select("table").get(8).select("tr").get(0).select("td").get(0).text().replaceAll("[^\\d.]", "");
//				logger.info("orderNumber :: "+orderNumber);
				String productName = doc.select("table").get(5).select("tr").get(0).select("td").get(0).text().split(",")[1].split("...")[0];
//				logger.info("productName :: "+productName);
				try {
					Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "SnapDeals");
	//				System.err.println("order : "+order);
					if(order != null) { 
						for(int i=0; i<order.getOrderItems().size(); i++) {
							if(order.getOrderItems().get(i).getProductName().contains(productName)) {
								List <OrderItemStatus> statusList=order.getOrderItems().get(i).getStatus();
								
								Boolean packedData =false;
								
								for(int j=0; j< statusList.size(); j++) {
									if(statusList.get(j).getCurrentStatus().equalsIgnoreCase("Packed")) {
										packedData=true;
									}
								}
								if(!packedData) {
									OrderItemStatus orderItemsStatus = new OrderItemStatus();
									orderItemsStatus.setCrDate(new Date());
									orderItemsStatus.setCurrentStatusCode(2);
									orderItemsStatus.setCurrentStatus("Packed");
									orderItemsStatus.setOrderNumber(orderNumber);
									statusList.add(orderItemsStatus);
									order.getOrderItems().get(i).setStatus(statusList);
									orderRepo.save(order);
								}
							}
						}
					}
				}catch(Exception e2) {
					e2.getMessage();
					e2.printStackTrace();
				}
			}else if(subjectData.contains("delivered in between") && doc.select("table").get(5).text().contains("is on its way") ) {
				String orderNumber = doc.select("table").get(8).select("tr").get(0).select("td").get(0).text().replaceAll("[^\\d.]", "");
//				logger.info("orderNumber :: "+orderNumber);
				String productName = doc.select("table").get(26).select("tr").get(0).select("td").get(0).text().split("\\. ")[1];
//				logger.info("productName :: "+productName);
				try {
					Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "SnapDeals");
					if(order != null) {
						for(int i=0; i<order.getOrderItems().size(); i++) {
							if(order.getOrderItems().get(i).getProductName().equalsIgnoreCase(productName) || productName.contains(order.getOrderItems().get(i).getProductName())) {
								List <OrderItemStatus> statusList=order.getOrderItems().get(i).getStatus();
								Boolean packedData =false;
								for(int j=0; j< statusList.size(); j++) {
									if(statusList.get(j).getCurrentStatus().equalsIgnoreCase("Shipped")) {
										packedData=true;
									}
								}
								if(!packedData) {
									OrderItemStatus orderItemsStatus = new OrderItemStatus();
									orderItemsStatus.setCrDate(new Date());
									orderItemsStatus.setCurrentStatusCode(3);
									orderItemsStatus.setCurrentStatus("Shipped");
									orderItemsStatus.setOrderNumber(orderNumber);
									statusList.add(orderItemsStatus);
									order.getOrderItems().get(i).setStatus(statusList);
									orderRepo.save(order);
								}
							}
						}
					}
				}catch(Exception e2) {
					e2.getMessage();
					e2.printStackTrace();
				}
			}else {
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			
		} 
		
		
		
	}

}
