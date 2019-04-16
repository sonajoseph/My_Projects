package com.shopperapp.serviceimpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.shopperapp.dto.OrderItem;
import com.shopperapp.dto.RawMessageResponseDTO;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.models.Order;
import com.shopperapp.mongo.models.OrderItemStatus;
import com.shopperapp.mongo.models.OrderItems;
import com.shopperapp.mongo.repo.MailDataRepo;
import com.shopperapp.mongo.repo.OrderRepo;
import com.shopperapp.service.ZaloraService;

@Service
public class ZaloraAPIServiceImpl implements ZaloraService {
	private static final Logger logger = LoggerFactory.getLogger(GoogleAPIServiceImpl.class);
//	@Autowired
//	private MailDataRepo mailDataRepo;

	@Autowired
	NotificationMessageFCM notificationMessageFCM;

	@Autowired
	DateComponents dateComponent;

	@Autowired
	OrderRepo orderRepo;

	@Override
	public void readDataFromZalora(String decodedMail, String toMailId, String subject, Date date, String fromMail,
			String deviceId, RawMessageResponseDTO rawMessageResponseDTO,
			ResponseEntity<MessageResponseDTO> forEntity) {

		String mailDataBody = forEntity.getBody().getPayload().getBody().getData();
		System.err.println("mailDataBody : " + mailDataBody);

		String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));

		Document doc = Jsoup.parseBodyFragment(decodedMail1);
		System.err.println("SUBJECT IS..........." + subject);
		
		// coupons code.
		if(decodedMail1.contains("Use Code:") || decodedMail1.contains("Use code:")) {
			System.err.println("HERE**************************88");
			
			Elements useCodeTrs = doc.select("tr");
			String useCode = "";
			
			try {
				for(int i=useCodeTrs.size()-1; i>=0; i--) {
					
					String elementTr =useCodeTrs.get(i).text();
					
					if(elementTr.contains("Use Code: ")) {
						useCode = elementTr.split("Use Code: ")[1].trim();
						
						System.err.println("USE CODE ::::::::: "+ useCode );
						break;
					}
	//				String table0=doc.select("table").get(20).select("tr").get(1).text().split(" ")[2].trim();
				}
				
				if(useCode.isEmpty() || useCode == "" || useCode == null) {
					
					for(int i=useCodeTrs.size()-1; i>=0; i--) {
						
						String elementTr =useCodeTrs.get(i).toString();
						
						if(elementTr.contains("Use Code: ") || elementTr.contains("Use code: ") ) {
							
							
							useCode = useCodeTrs.get(i).select("img").get(0).attr("alt");
							
							System.err.println("USE CODE :::::::1111111:: "+ useCode.split("Use code: ")[1].split(" ")[0].replaceAll(",", ""));
							break;  //prevents repeating of same item.
						}
		//				String table0=doc.select("table").get(20).select("tr").get(1).text().split(" ")[2].trim();
					}
					
				}
			}catch(Exception e1) {
				e1.getMessage();
				e1.printStackTrace();
			}
			
			
			try {
				for(int i=useCodeTrs.size()-1; i>=0; i--) {
					
					String elementTr =useCodeTrs.get(i).text();
					
					if(elementTr.contains("Valid till")) {
						System.err.println("Valid till ::::::::: "+elementTr.split("Valid till")[1].split(",")[0]);
						break;
					}
					
	//				String table0=doc.select("table").get(20).select("tr").get(1).text().split(" ")[2].trim();
				}
			}catch(Exception e1) {
				e1.getMessage();
				e1.printStackTrace();
			}
			
//			System.err.println("table is...................."+table0);
//			
//			//coupns validity
//			
//			String table1=doc.select("table").get(22).text();
//			try {
//				System.err.println("table is......++++++++++.............."+table1.split("Valid till ")[1].split("\\.")[0]);
//			}catch(Exception e) {
//				e.printStackTrace();
//			}
			
			
			
		}else {
			// Table Reading
			String orderNo = "";
			if (subject != null) {
				orderNo = fetchZaloraOrderNo(subject);
				System.err.println("ORDER NO IS........................." + orderNo);
			}
	
			List<OrderItems> orderItemsList = new ArrayList<>();  
			if (subject.contains("confirmed")) {
			
	
				try {
					logger.info("At readZaloraMails......................");
	
					Order order = orderRepo.findOneByOrderNumAndVendorName(orderNo, "zalora");
					if (order == null) {
						order = new Order();
						System.err.println("Reading zaloraMails..............................");
	
						Element table0 = doc.select("table").get(24);
	
						Elements dataTables = table0.select("tr").get(0).select("td").get(0).select("table");
	
						
						for (int i = 0; i < dataTables.size(); i++) {
	
							OrderItems orderItem = new OrderItems();
							orderItem.setOrderId(orderNo);
							orderItem.setCreatedAt(new Date());
	
							Elements dataColumns = dataTables.get(i).select("tr").get(0).select("td");
	
							// System.err.println("\n\n\n dataColumns --------------------> "
							// +dataColumns.toString()+"\n\n\n ");
							if (dataColumns.text().contains("SOLD BY")) {
								i = i + 1;
								dataColumns = dataTables.get(i).select("tr").get(0).select("td");
								// System.err.println("\n\n\n dataColumns --22222------------------> "
								// +dataColumns.toString()+"\n\n\n ");
							}
							String itemName = dataColumns.get(1).select("b").get(0).text();
							// System.err.println("itemName : "+itemName);
	
							String itemq = dataColumns.get(1).text();
							System.err.println("..............." + itemq);
							String itemQty = dataColumns.get(1).text().split("Qty: ")[1].trim();
							System.err.println("itemQty : " + itemQty);
	
							String itemDeliveryDate = dataColumns.get(2).text();
							System.err.println("itemDeliveryDate : " + itemDeliveryDate);
	
							if (itemDeliveryDate.contains("Express Shipping")) {
								try {
									Date finalDate = dateComponent.addDays(new Date(), 1);
									System.err.println("DATE IS..........." + finalDate);
									
									
	
								} catch (Exception dateExp) {
	
								}
							} else if (itemDeliveryDate.contains("working days")) {
	
								itemDeliveryDate = itemDeliveryDate.split("-")[1].replaceAll("[^\\d.]", "");
								System.err.println("Item delivery date is........" + itemDeliveryDate);
	
								try {
									Date finalDate = dateComponent.addDays(new Date(), Integer.parseInt(itemDeliveryDate));
									System.err.println("DATE IS..........." + finalDate);
									
	
								} catch (Exception dateExp) {
	
								}
							} else {
								LocalDate now = LocalDate.now();
								int year = now.getYear();
	
								System.err.println("REACHED.....................");
								String deliveyDate = itemDeliveryDate.split("-")[1];
								deliveyDate = deliveyDate + "/" + year;
								System.err.println("DELIVERY DATE IS................." + deliveyDate);
	
								System.err.println("Delivery Date " + orderNo + " " + deliveyDate);
	
								try {
									Date finalDate = dateComponent.getStringToDate2(deliveyDate);
									System.err.println("DATE IS..........." + finalDate);
									orderItem.setDeliveryDate(finalDate);
	                               
								} catch (Exception dateExp) {
	
								}
							}
							String subTotal = dataColumns.get(3).text();
							System.err.println("subTotal : " + subTotal);
							
	
							System.err.println("orderItem :: ======>>>>>" + orderItem.toString());
							/*
							 * if(orderItem.getItem() != null || !orderItem.getItem().isEmpty()) {
							 * orderItemList.add(orderItem); System.err.println("ITEM ADDED"); }
							 */
	
							orderItem.setProductName(itemName);
							orderItem.setQty(Integer.parseInt(itemQty));
							orderItem.setPrice("$ " + subTotal);
						
	
							OrderItemStatus orderItemStatus = new OrderItemStatus();
							orderItemStatus.setOrderNumber(orderNo);
							orderItemStatus.setCurrentStatus("Ordered");
							orderItemStatus.setCurrentStatusCode(1);
							orderItemStatus.setCrDate(new Date());
	
							List<OrderItemStatus> status = new ArrayList<OrderItemStatus>();
							status.add(orderItemStatus);
	
							orderItem.setStatus(status);
							orderItemsList.add(orderItem);
	
						}
	
						
	
						Element table1 = doc.select("table").get((24 + dataTables.size() + 1));
						
	
						Elements tableRows = table1.select("tr");
						for (int i = 0; i < tableRows.size(); i++) {
							if (tableRows.get(i).select("table").text().contains("Shipping")) {
								String shipppingCharges = tableRows.get(i).select("td").select("table").select("tr").get(0)
										.select("td").get(1).text();
								System.out.println("shipppingCharges :::: " + shipppingCharges);
								order.setItemsTotalDeliveryCharge(shipppingCharges);
	
								if (!shipppingCharges.contains("FREE")) {
									shipppingCharges = "$ " + shipppingCharges;
									order.setItemsTotalDeliveryCharge(shipppingCharges);
								}
	
							
	
							} else if (tableRows.get(i).text().contains("Total S$ incl. GST")) {
							
								String grandTotalPrice = tableRows.get(i + 1).select("tr").get(0).select("td").get(1)
										.text();
								System.out.println("Total S$ incl. GST :::: " + grandTotalPrice);
	
	
								order.setGrandTotal("$ " + grandTotalPrice);
	
								String gstPrice = tableRows.get(i + 2).select("tr").get(0).select("td").get(1).text();
								System.out.println("gstPrice :::: " + gstPrice);
	
								order.setGstTotal("$ " + gstPrice);
								break;
							}
						}
	
						Element table2 = doc.select("table").get((24 + dataTables.size() + 4));
						
	
						String deliveryDetails = table2.text();
						String deliveryDetailsArr[] = deliveryDetails.split("\\:");
					
	
						String deliveyAddress = deliveryDetailsArr[1].replaceAll("Delivery Address", "") + ", "
								+ deliveryDetailsArr[2].replaceAll("Payment Method", "");
						System.out.println("deliveyAddress ::: " + deliveyAddress);
						
						//contactSeller
						try {
							
						
						/*Element contact=doc.select("table").get(37).select("a").get(0);
						System.err.println("contact us here::::::::::::"+contact.attr("href"));
						order.setContactSeller(contact.attr("href"));
						*/
							
						Element contact=doc.select("table").get(52).select("a").get(0);
						System.err.println("contact us here::::::::::::"+contact.attr("href"));
						order.setContactSeller(contact.attr("href"));
						
						
						
						//trackId
						Element track=doc.select("table").get(19).select("tr").get(0).select("a").get(0);
						System.err.println("trackid is:::::::::::::;"+track.attr("href"));
						order.setTrackIt(track.attr("href"));
						}catch(Exception e) {
							e.printStackTrace();
						}
	
						Order orderDataDB = orderRepo.findOneByOrderNumAndVendorName(orderNo, "zalora");
						System.err.println("Order Num : " + orderNo + "\t mailDataDB :: " + orderDataDB);
						if (orderDataDB == null) {
							System.err.println("REACHED...................................");
	
							order.setOrderNum(orderNo);
							order.setVendorName("Zalora");
							order.setDeliveryAddress(deliveyAddress);
							order.setCreatedDate(new Date());
							order.setUserEmailId(toMailId);
							order.setFromMailId(fromMail);
						
						
							order.setOrderItems(orderItemsList);
							orderRepo.save(order);
	
							String msg = "Your order at zalora, Order Id : " + orderNo + " had placed successfully."
									+ " You can now view your order details at ShopperApp.";
							notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNo);
						}
	
						System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNo);
	
						System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (subject.contains("is on its way")) {
				System.err.println("Inside is on its way ::: " + orderNo);
				String orderNumber =orderNo;
				Element product=doc.select("table").get(22).select("tr").get(0).select("td").get(1);
				System.err.println(":::::::::::::::::::::::::::;;;+++++++++++++++"+product.text().split("SKU")[0].trim());
				String productName=product.text().split("SKU")[0].trim();
				
				try {
					Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "Zalora");
					System.err.println("order : "+order); 
					
					if(order != null) {
						for(int i=0; i<order.getOrderItems().size(); i++) {
							//checking product name in db vs the product name fetched from mail.
							if(order.getOrderItems().get(i).getProductName().contains(productName)) {
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
									orderItemsStatus.setCurrentStatusCode(3);
									orderItemsStatus.setCurrentStatus("shipped");
									orderItemsStatus.setOrderNumber(orderNumber);
									statusList.add(orderItemsStatus);
									order.getOrderItems().get(i).setStatus(statusList);
									orderRepo.save(order);
								}
							}
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				String msg = "Your order at zalora, Order Id : " + orderNumber + "is shipped successfully."
						+ " You can now view your order details at ShopperApp.";
				notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId,orderNumber);
	
				System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNumber);
			}else if(subject.contains("cancelled")) {
				//trim will remove space.
				String orderNumber = doc.select("table").get(20).select("tr").get(0).select("td").get(0).text().split(" ")[2];
				System.err.println("orderID IS...:  " + orderNumber);
	
				String productName=doc.select("table").get(22).select("tr").get(0).text().split("SKU")[0].trim();
				System.err.println("productName.*********************"+productName);
			
				try {
					Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "Zalora");
					System.err.println("order : "+order);
					if(order != null) {
						for(int i=0; i<order.getOrderItems().size(); i++) {
							//checking product name in db vs the product name fetched from mail.
							if(order.getOrderItems().get(i).getProductName().contains(productName)) {
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
				}catch (Exception e) {
					e.printStackTrace();
				}
				String msg = "Your order at zalora, Order Id : " + orderNumber + "is cancelled successfully."
						+ " You can now view your order details at ShopperApp.";
				
				notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId,orderNumber);
				System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNumber);
			}
		}
		
	}

	private String fetchZaloraOrderNo(String subject) {
		String orderNo = "";
		orderNo = subject.replaceAll("[^\\d.]", "");
		// System.err.println("Order No :: "+orderNo);

		return orderNo;
	}

	private String fetchFromMail(String fromMail) {
		String mailId = "";
		Pattern compile = Pattern.compile("<(.*?)>");
		Matcher matcher = compile.matcher(fromMail);
		while (matcher.find()) {
			mailId = matcher.group(1);
		}
		return mailId;
	}

}
