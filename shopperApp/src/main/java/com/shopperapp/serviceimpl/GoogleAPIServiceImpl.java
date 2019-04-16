package com.shopperapp.serviceimpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.util.StringUtils;
import com.shopperapp.components.DateComponents;
import com.shopperapp.components.NotificationMessageFCM;
import com.shopperapp.components.PropertyComponents;
import com.shopperapp.components.RestTemplateErrorHandler;
import com.shopperapp.components.TokenComponent;
import com.shopperapp.dto.Headers;
import com.shopperapp.dto.MessageIdDTO;
import com.shopperapp.dto.MessageResponseDTO;
import com.shopperapp.dto.Messages;
import com.shopperapp.dto.OrderDetail;
import com.shopperapp.dto.OrderItem;
import com.shopperapp.dto.RawMessageResponseDTO;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.repo.MailDataRepo;
import com.shopperapp.mongo.serviceimpl.JythonService;
import com.shopperapp.mysql.models.CompanyMoreDetails;
import com.shopperapp.mysql.models.UserToken;
import com.shopperapp.mysql.repo.CompanyMoreDetailsRepo;
import com.shopperapp.mysql.repo.UserTokenRepo;
import com.shopperapp.service.BigBasketService;
import com.shopperapp.service.FlipkartService;
import com.shopperapp.service.GoogleAPIService;
import com.shopperapp.service.JabongService;
import com.shopperapp.service.MyntraService;
import com.shopperapp.service.OpenTasteService;
import com.shopperapp.service.RedmartService;
import com.shopperapp.service.SnapdelMailService;
import com.shopperapp.service.ZaloraService; 

@Service
public class GoogleAPIServiceImpl implements GoogleAPIService {
	private static final Logger logger = LoggerFactory.getLogger(GoogleAPIServiceImpl.class);

	@Autowired
	JythonService jythonService;
	@Autowired
	MyntraService myntraService;
	@Autowired
	JabongService jabongService ;
	@Autowired
	BigBasketService bigBasketService;
	@Autowired
	ZaloraService zaloraService;
	

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;
	@Autowired
	private RestTemplateErrorHandler restTemplateErrorHandler;
	@Autowired
	private PropertyComponents proprtyComponents;

	@Autowired
	private CompanyMoreDetailsRepo companyMoreDetailsRepo;

	@Autowired
	private MailDataRepo mailDataRepo;
	@Autowired
	private UserTokenRepo userTokenRepo;

	@Autowired
	private TokenComponent tokenComponent;

	@Autowired
	DateComponents dateComponent;

	@Autowired
	NotificationMessageFCM notificationMessageFCM;
	
	@Autowired
	SnapdelMailService snapdelMailService;

	@Autowired
	FlipkartService flipkartService;
	
	@Autowired
	OpenTasteService openTasteService;
	@Autowired
	RedmartService redmartService;
	
	@Override
	public String fetchMessageId(String accessToken, String mailId) {
		logger.info("At  fetchMessageId . .. . . .");

		Boolean stopMailRead = false;
		System.out.println("accessToken ::: " + accessToken);
		logger.info("At fetchMessageId");
		UserToken userToken = userTokenRepo.findByMailId(mailId);
		String lastMessageId = userToken.getLastMessageId();
		String deviceId = userToken.getDeviceId();
		
		List<Messages> mailIdsList = new ArrayList<>();
		if (userToken != null && userToken.getLastMessageId() != null && userToken.getLastMessageId() != ""
				&& !userToken.getLastMessageId().isEmpty()) {
			Date currentDate = new Date();
			//checking mails one day before current date.
			java.util.Date getMailsAfterDate = dateComponent.subDays(currentDate, 2);
			

			this.restTemplate = restTemplateBuilder.errorHandler(restTemplateErrorHandler).build();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", accessToken);
			HttpEntity<String> entity = new HttpEntity<>(headers);
 
//			String api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=after:"+dateComponent.convertUtilDateToEpoch(getMailsAfterDate); 
//			String api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:info@e.zalora.sg"; 
//			String api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:orders@orders.lazada.sg"; 
			String api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:no-reply@flipkart.com"; 
//			String api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:auto-confirm@amazon.in"; 
//			String api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:noreply@snapdeals.co.in"; 
//			String api="https://www.googleapis.com/gmail/v1/users/userid/messages"; 
			System.out.println("api ::::: "+api);
//			String api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:redmartnoreply@gmail.com";  
 			proprtyComponents.setGoogleMessageIdUrl(api);
			String url = proprtyComponents.getGoogleMessageIdUrl().replaceAll("userid", mailId);
			System.err.println(url);
			ResponseEntity<MessageIdDTO> forEntity = restTemplate.exchange(url, HttpMethod.GET, entity, MessageIdDTO.class);
			System.out.println(forEntity);
 
			System.err.println("forEntity.getBody().getResultSizeEstimate() ::: " + forEntity.getBody().getResultSizeEstimate());
			if (forEntity.getBody().getResultSizeEstimate() != null && forEntity.getBody().getResultSizeEstimate() > 0) {
				System.err.println("GOING TO ADD MAIL IDS");
				// Updating the Last Read Message Id
				userToken.setLastMessageId(forEntity.getBody().getMessages().get(0).getId());
				userTokenRepo.save(userToken);
				for (int r = 0; r < forEntity.getBody().getMessages().size(); r++) {
					Messages messages = forEntity.getBody().getMessages().get(r);
//					System.err.println("lastMessageId  : "+lastMessageId);
					if (!lastMessageId.equalsIgnoreCase(messages.getId()) && !stopMailRead) {
//					if(messages.getId().equalsIgnoreCase("168a8f3830967d06")){
						mailIdsList.add(messages);
					}  
					else {
						stopMailRead = true;
						break;
					}
				}
				// nextPage Mails Starts
				try {
					if (!stopMailRead) {
						while (forEntity.getBody().getNextPageToken() != null && !stopMailRead) {
							this.restTemplate = restTemplateBuilder.errorHandler(restTemplateErrorHandler).build();
							proprtyComponents.setGoogleMessageIdUrl(
									api + "&pageToken=" + forEntity.getBody().getNextPageToken());
							url = proprtyComponents.getGoogleMessageIdUrl().replaceAll("userid", mailId);
							forEntity = restTemplate.exchange(url, HttpMethod.GET, entity, MessageIdDTO.class);
							if (forEntity.getBody().getResultSizeEstimate() != null
									&& forEntity.getBody().getResultSizeEstimate() > 0) {
								// mailIdsList.addAll(forEntity.getBody().getMessages());
								for (int r = 0; r < forEntity.getBody().getMessages().size(); r++) {
									Messages messages = forEntity.getBody().getMessages().get(r);
									if (lastMessageId != messages.getId() && !stopMailRead) {
										mailIdsList.add(messages);
										System.err.println("ADDEDDDD");
									} else {
										stopMailRead = true;
										break;
 									}
								}
							}
						}
					}
				} catch (Exception e) {
					System.err.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
					e.printStackTrace();
				}
				
				
				ListIterator<Messages> li = mailIdsList.listIterator(mailIdsList.size());
				while (li.hasPrevious()) {
					Messages messages = (Messages) li.previous();
					System.err.println("MESSAGES ARE "+messages);
					try {
						getData(messages.getId(), accessToken, mailId, deviceId);
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return "Success";
		} else {
			System.err.println("Previous Data Not There......");
			this.restTemplate = restTemplateBuilder.errorHandler(restTemplateErrorHandler).build();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", accessToken);
			HttpEntity<String> entity = new HttpEntity<>(headers);
			proprtyComponents.setGoogleMessageIdUrl("https://www.googleapis.com/gmail/v1/users/userid/messages");
			String url = proprtyComponents.getGoogleMessageIdUrl().replaceAll("userid", mailId);
			System.err.println(url);
			ResponseEntity<MessageIdDTO> forEntity = restTemplate.exchange(url, HttpMethod.GET, entity,
					MessageIdDTO.class);
			System.err.println(forEntity.toString());
			System.err.println("--------1");
			if (forEntity.getBody().getResultSizeEstimate() != null
					&& forEntity.getBody().getResultSizeEstimate() > 0) {
				userToken.setLastMessageId(forEntity.getBody().getMessages().get(0).getId());
				userTokenRepo.save(userToken);
			}
		}
		return "Success";
	}

	private Object getData(String id, String accessToken, String mailId, String deviceId) throws MessagingException {
		logger.info("At getData");

		List<String> data = new ArrayList<>();
		this.restTemplate = restTemplateBuilder.errorHandler(restTemplateErrorHandler).build();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", accessToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		String url = "https://www.googleapis.com/gmail/v1/users/userId/messages/id";
		url = url.replaceAll("userId", mailId).replaceAll("id", id);

		ResponseEntity<MessageResponseDTO> forEntity = restTemplate.exchange(url, HttpMethod.GET, entity,
				MessageResponseDTO.class);

		List<Headers> collect = forEntity.getBody().getPayload().getHeaders().stream()
				.filter(dats -> dats.getName().equalsIgnoreCase("subject")).collect(Collectors.toList());
		Headers headers2 = collect.get(0);
		String value = headers2.getValue();
		List<Headers> fromMail = forEntity.getBody().getPayload().getHeaders().stream()
				.filter(dats -> dats.getName().equalsIgnoreCase("From")).collect(Collectors.toList());
		Headers headers3 = fromMail.get(0);
		System.err.println(headers3.getValue());
		retrive(value, id, accessToken, mailId, headers3.getValue(), deviceId, forEntity);
		return null;
	}

	private Boolean retrive(String value, String id, String accessToken, String mailID, String fromMail,
			String deviceId, ResponseEntity<MessageResponseDTO> forEntity) {
		logger.info("At retrive ++++++++++++" + mailID);
		Pattern compile = Pattern.compile("<(.*?)>");
		Matcher matcher = compile.matcher(value);

		while (matcher.find()) {
			mailID = matcher.group(1);
		}
		Optional<CompanyMoreDetails> emailData = companyMoreDetailsRepo.findOneByEmailId(mailID);

		List<String> data = new ArrayList<>();
		this.restTemplate = restTemplateBuilder.errorHandler(restTemplateErrorHandler).build();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", accessToken);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		String urlRaw = "https://www.googleapis.com/gmail/v1/users/userId/messages/id?format=raw";
		urlRaw = urlRaw.replaceAll("userId", mailID).replaceAll("id", id);

		ResponseEntity<RawMessageResponseDTO> rawMail = restTemplate.exchange(urlRaw, HttpMethod.GET, entity,
				RawMessageResponseDTO.class);
		String decodedMail = StringUtils.newStringUtf8(Base64.decodeBase64(rawMail.getBody().getRaw()));

		OrderDetail mailData = new OrderDetail();

		System.err.println("fromMail ::::::::: "+fromMail);
 
		if(fromMail.contains("noreply@snapdeals.co.in")) {
			System.err.println("calling ::::::::: Snapdeals" );
			try {
				snapdelMailService.readDataFromSnapdeal(  mailID, value, new
			  Date(rawMail.getBody().getInternalDate()), fromMail, deviceId, rawMail.getBody(), forEntity); 
			}catch(Exception e) {
				
			}
		} 
		else if(fromMail.contains("amazon")) {
			System.err.println("calling ::::::::: amazon" );
			try {
				jythonService.execAmazonMailData(fromMail, id,accessToken,forEntity,new
					  Date(rawMail.getBody().getInternalDate()),deviceId);
			}catch(Exception e) {
				
			}
		}  
		else 
			if (decodedMail.contains("RedMart")) {
			System.err.println("calling ::::::::: RedMart" );
			try {
				/*mailData = readRedMartMails(decodedMail, mailID,value,new
				Date(rawMail.getBody().getInternalDate()),fromMail, deviceId, forEntity);*/
				redmartService.readDataFromRedmart(decodedMail, mailID, value, new
						  Date(rawMail.getBody().getInternalDate()), fromMail, deviceId, rawMail.getBody(), forEntity);
			}catch(Exception e) {
				
			}
		}
		else if (decodedMail.contains("OpenTaste")) { 
			System.err.println("calling ::::::::: OpenTaste" );
			try {
//			  mailData = readDataFromOpenTaste(decodedMail, mailID, value, new
//			  Date(rawMail.getBody().getInternalDate()), fromMail , deviceId);
				openTasteService.readDataFromOpenTaste( mailID, value, new  Date(rawMail.getBody().getInternalDate()), fromMail, deviceId, rawMail.getBody(), forEntity);
			  
			}catch(Exception e) {
			}
		}else 
 
		if (decodedMail.contains("flipkart") && fromMail.contains("no-reply@flipkart.com") ) {
			System.err.println("calling ::::::::: flipkart " +mailID);
 			try {
//			  mailData = readDataFromFlipkart(decodedMail, mailID, value, new
//			  Date(rawMail.getBody().getInternalDate()), fromMail, deviceId, rawMail.getBody(), forEntity); 
 				flipkartService.readDataFromFlipkart( mailID, value, new  Date(rawMail.getBody().getInternalDate()), fromMail, deviceId, rawMail.getBody(), forEntity);
			}catch(Exception e) {
			}
	    }
		else 
		if (decodedMail.contains("ZALORA")) {  
			System.err.println("calling ::::::::: ZALORA" );
			 try {
				 zaloraService.readDataFromZalora(decodedMail, mailID, value, new
						  Date(rawMail.getBody().getInternalDate()), fromMail, deviceId, rawMail.getBody(), forEntity);
			 }catch(Exception e) {
			 }
		}
		
		else 
			if (decodedMail.contains("myntra")) {  
				System.err.println("calling ::::::::: MYNTRA" );
				 try {
					 myntraService.readDataFromMyntra(decodedMail, mailID, value, new
							  Date(rawMail.getBody().getInternalDate()), fromMail, deviceId, rawMail.getBody(), forEntity);
				 }catch(Exception e) {
				 }
			}
		
			else 
				if (decodedMail.contains("Jabong")) {  
					System.err.println("calling ::::::::: Jabong" );
					 try {
						 jabongService.readDataFromJabong(decodedMail, mailID, value, new
								  Date(rawMail.getBody().getInternalDate()), fromMail, deviceId, rawMail.getBody(), forEntity);
					 }catch(Exception e) {
					 }
				}
		
		
				else 
					if (decodedMail.contains("bigbasket")) {  
						System.err.println("calling ::::::::: bigbasket" );
						 try {
							 bigBasketService.readDataFromBigBasket(decodedMail, mailID, value, new
									  Date(rawMail.getBody().getInternalDate()), fromMail, deviceId, rawMail.getBody(), forEntity);
						 }catch(Exception e) {
						 }
					}
		 else 
		if (decodedMail.contains("lazada")) {
			System.err.println("calling ::::::::: lazada" );
			 try {
				 jythonService.execLazadaMailData(fromMail, id,accessToken,forEntity,new
						 Date(rawMail.getBody().getInternalDate()),deviceId);
			 }catch(Exception e) {
				 e.printStackTrace();
			 }
		}
 		return true;
	}

	 

	private OrderDetail readDataFromZalora(String decodedMail, String toMailId, String subject, Date date,
			String fromMail, String deviceId,  ResponseEntity<MessageResponseDTO> forEntity) {
		
		String mailDataBody= forEntity.getBody().getPayload().getBody().getData();  
		System.err.println("mailDataBody : "+mailDataBody); 
		
		String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));
		
		Document doc = Jsoup.parseBodyFragment(decodedMail1);
		System.err.println("SUBJECT IS..........."+subject);
		// Table Reading
		String orderNo = "";
		if (subject != null) {
			orderNo = fetchZaloraOrderNo(subject);
			System.err.println("ORDER NO IS........................." + orderNo);
		}
		
	/*	//orderNo
		String orderID = "";
		if(!subject.contains("cancelled")) {
//			Element tableOrderNo=doc.select("table").get(20).select("tr").get(1);
//			System.err.println("table1 is..........."+tableOrderNo.text().split(": ")[1]);
//			orderID=tableOrderNo.text().split(": ")[1];
			if (subject != null) {
				orderID = fetchZaloraOrderNo(subject);
				System.err.println("ORDER NO IS........................." + orderID);
			}
		}*/
			
					
					
		if (subject.contains("is on its way")) {
			System.err.println("INside is on its way ::: "+orderNo);
			try {
				MailData shippedDataDB = mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatus(orderNo,
						"zalora", "Shipped");
				if (shippedDataDB == null) {
					System.err.println("heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
					MailData mailDataDB = mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatus(orderNo,
							"zalora", "Ordered");
					System.err.println("ORDRED ZALORA ::::: " + mailDataDB.toString());
					if (mailDataDB != null) {
						MailData shippedData = new MailData();
						shippedData.setOrderNumber(orderNo);
						shippedData.setOrderItemsList(mailDataDB.getOrderItemsList());
						shippedData.setDate(date);
						shippedData.setFromMailId(fetchFromMail(fromMail));
						shippedData.setCurrentStatus("Shipped");
						shippedData.setItemsTotalDeliveryCharge(mailDataDB.getItemsTotalDeliveryCharge());
						shippedData.setArrivesOn(mailDataDB.getArrivesOn());
						shippedData.setDeliveryAddress(mailDataDB.getDeliveryAddress());
						shippedData.setCurrentStatusCode(3);
						shippedData.setGrandTotal(mailDataDB.getGrandTotal());
						shippedData.setGstTotal(mailDataDB.getGstTotal());
						shippedData.setVendor("zalora");
						shippedData.setToMaild(toMailId);
						mailDataRepo.save(shippedData);
						String msg = "Your order at zalora, Order Id : " + orderNo
								+ " status have changed to Shipped now."
								+ " You can now view your order details at ShopperApp.";
						notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId,orderNo);
					}
				}
			} catch (Exception ede) {
				ede.printStackTrace();
			}
		}
		else if (subject.contains("confirmed")) {
			try {
				logger.info("At readZaloraMails......................");
				
				Element table0 = doc.select("table").get(24);
				
				Elements dataTables = table0.select("tr").get(0).select("td").get(0).select("table");
				
				List<OrderItem> list = new ArrayList<>();
//				 System.err.println("table0table0table0table0table0table0 :::: \n "+table0.toString()+"\n\n");
				   
				MailData orderDetails = new MailData();
				List<OrderItem> orderItemList = new ArrayList<OrderItem>();
				
				for(int i=0;i< dataTables.size();i++) {
					OrderItem orderItem = new OrderItem();
					
					Elements dataColumns = dataTables.get(i).select("tr").get(0).select("td");
					
//					System.err.println("\n\n\n dataColumns --------------------> " +dataColumns.toString()+"\n\n\n ");
					if(dataColumns.text().contains("SOLD BY")) {
						i=i+1;
						dataColumns = dataTables.get(i).select("tr").get(0).select("td");
//						System.err.println("\n\n\n dataColumns --22222------------------> " +dataColumns.toString()+"\n\n\n ");
					}
					String itemName=dataColumns.get(1).select("b").get(0).text();
//					System.err.println("itemName : "+itemName);
					
					orderItem.setItem(itemName);
					String itemQty = dataColumns.get(1).text().split("Qty")[1];
//					System.err.println("itemQty : "+itemQty);
					
					orderItem.setQty(itemQty);
					
					String itemDeliveryDate = dataColumns.get(2).text();
					System.err.println("itemDeliveryDate : "+itemDeliveryDate);
					
					if(itemDeliveryDate.contains("Express Shipping")) {
						try {
							Date finalDate = dateComponent.addDays(new Date(), 1);
							System.err.println("DATE IS..........." + finalDate);
							orderDetails.setArrivesOn(finalDate);
							orderItem.setDeliveryDate(finalDate);
							
						}catch(Exception dateExp) {
							
						}
					}
					else if(itemDeliveryDate.contains("working days")) {
						
						itemDeliveryDate=itemDeliveryDate.split("-")[1].replaceAll("[^\\d.]", "");
						System.err.println("Item delivery date is........"+itemDeliveryDate);
						
						try {
							Date finalDate = dateComponent.addDays(new Date(), Integer.parseInt(itemDeliveryDate));
							System.err.println("DATE IS..........." + finalDate);
							orderDetails.setArrivesOn(finalDate);
							orderItem.setDeliveryDate(finalDate);
							
						}catch(Exception dateExp) {
							
						}
					}
					else {
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
							orderDetails.setArrivesOn(finalDate);
							orderItem.setDeliveryDate(finalDate);
							
						}catch(Exception dateExp) {
							
						}
					}
					String subTotal = dataColumns.get(3).text();
					System.err.println("subTotal : "+subTotal);  
					
					orderItem.setSubTotal("$ "+subTotal);
					
					
				
					System.err.println("orderItem :: ======>>>>>"+orderItem.toString());
					if(orderItem.getItem() != null || !orderItem.getItem().isEmpty()) {
						orderItemList.add(orderItem);
						System.err.println("ITEM ADDED");
					}
				}
				
				orderDetails.setOrderItemsList(orderItemList);
				 
				Element table1 = doc.select("table").get((24+dataTables.size()+1));
	//			System.err.println("table1table1table1table1table1table1 :::: \n "+table1.toString()+"\n\n");
				
				Elements tableRows = table1.select("tr");
				for(int i=0;i<tableRows.size();i++) {
					if(tableRows.get(i).select("table").text().contains("Shipping")) {
						String shipppingCharges=tableRows.get(i).select("td").select("table").select("tr").get(0).select("td").get(1).text();
						System.out.println("shipppingCharges :::: "+shipppingCharges);
						
						if(!shipppingCharges.contains("FREE")) {
							shipppingCharges="$ "+shipppingCharges;
						}
						
						orderDetails.setItemsTotalDeliveryCharge(shipppingCharges);
						
						
					}else if(tableRows.get(i).text().contains("Total S$ incl. GST")) {
	//					System.out.println("Total S$ incl. GST :::: "+tableRows.get(i).toString()+"\n\n\n");
						String grandTotalPrice=tableRows.get(i+1).select("tr").get(0).select("td").get(1).text();
						System.out.println("Total S$ incl. GST :::: "+grandTotalPrice);
						
						orderDetails.setGrandTotal("$ "+grandTotalPrice);
						
						
						String gstPrice=tableRows.get(i+2).select("tr").get(0).select("td").get(1).text();
						System.out.println("gstPrice :::: "+gstPrice);
						orderDetails.setGstTotal("$ "+gstPrice);
						break;
					}
				}
				
				Element table2 = doc.select("table").get((24+dataTables.size()+4));
//				System.err.println("table2table2table2table2table2table2 :::: \n "+table2.toString()+"\n\n");
				
				String deliveryDetails=table2.text();
				String deliveryDetailsArr[]=deliveryDetails.split("\\:");
//				for(int j=0; j<deliveryDetailsArr.length; j++) {
//					
//					System.out.println("J ::: "+j+"\t :::: "+deliveryDetailsArr[j]);
//				}
				
				String deliveyAddress=deliveryDetailsArr[1].replaceAll("Delivery Address", "")+", "+deliveryDetailsArr[2].replaceAll("Payment Method", "");
				System.out.println("deliveyAddress ::: "+deliveyAddress);
				 
				
				
				

				MailData mailDataDB = mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatus(orderNo, "zalora",
						"Ordered");

				System.err.println("Order Num : " + orderNo + "\t mailDataDB :: " + mailDataDB);
				if (mailDataDB == null) {
					System.err.println("REACHED...................................");

					orderDetails.setDate(date);
					orderDetails.setCurrentStatus("Ordered");
					orderDetails.setOrderNumber(orderNo);
					orderDetails.setFromMailId(fetchFromMail(fromMail));
					orderDetails.setVendor("zalora");
					orderDetails.setToMaild(toMailId);
 
					orderDetails.setCurrentStatusCode((Integer) 1);
					orderDetails.setDeliveryAddress(deliveyAddress); 
					System.err.println("orderDetails  :::::: "+orderDetails.toString());
					mailDataRepo.save(orderDetails);
					
					
 
					String msg = "Your order at zalora, Order Id : " + orderNo + " had placed successfully."
							+ " You can now view your order details at ShopperApp.";
					notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId,orderNo);
				}

				System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNo);
			
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
		
		else if(subject.contains("cancelled")) {
			System.err.println("REACHED HERE  IT IS...: \n ");
			
//			Element e1s =doc.select("Order Number").parents().first();
//			System.err.println("e1s :::::: \n  "+e1s.toString());
//			Element tableOrderNo=doc.select("table").get(20).select("tr").get(1);
//			System.err.println("table1 is..........."+tableOrderNo.text().split(": ")[1]);
			 String orderID=doc.select("table").get(20).select("tr").get(0).select("td").get(0).text().split(" ")[2];
			
			System.err.println("orderID IS...:  "+orderID);
			
			List<MailData> findByOrderNumber = mailDataRepo
					.findByOrderNumberAndCurrentStatus(orderID, "Ordered");

			System.err.println("findByOrderNumber ::: " + findByOrderNumber);
			if (findByOrderNumber != null && !findByOrderNumber.isEmpty()) {

				List<MailData> findByOrderCancelled = mailDataRepo.findByOrderNumberAndCurrentStatus(orderID, "Cancelled");
				System.err.println("findByOrderCancelled ::: " + findByOrderCancelled);
				if (findByOrderCancelled == null || findByOrderCancelled.isEmpty()) {

					System.err.println("date :: cancelledd: "+date);
					MailData mailDataDB = findByOrderNumber.get(0);
					MailData mailData = new MailData();
					mailData.setCurrentStatusCode(5);
					mailData.setDate(date);
					mailData.setDeliveryAddress(mailDataDB.getDeliveryAddress());
					mailData.setOrderItemsList(mailDataDB.getOrderItemsList());
					mailData.setOrderNumber(mailDataDB.getOrderNumber());
					mailData.setVendor(mailDataDB.getVendor());
					mailData.setGstTotal(mailDataDB.getGstTotal());
					mailData.setFromMailId(mailDataDB.getFromMailId());
					mailData.setToMaild(mailDataDB.getToMaild());
					mailData.setGrandTotal(mailDataDB.getGrandTotal());
					mailData.setItemsTotalDeliveryCharge(mailDataDB.getItemsTotalDeliveryCharge());
					mailData.setItemsTotalsCost(mailDataDB.getItemsTotalsCost());
					mailData.setCurrentStatus("Cancelled");
					mailData = mailDataRepo.save(mailData);
					String msg = "Your order at ZALORA, Order Id : " + mailData.getOrderNumber()
					+ " status have changed to Cancelled now."
					+ " You can now view your order details at ShopperApp.";
			notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, mailData.getOrderNumber());
			
		}
			}
		 
			
	}
		return null;
	}
 
	
	
     private OrderDetail readRedMartMails(String decodedMail, String toMailId, String subject, Date date,String fromMail , String deviceId,ResponseEntity<MessageResponseDTO> forEntity) {
 		 
    	logger.info("At readRedMartMails......................");
    	System.err.println("SUBJECT IS......"+subject);
 		
    	String mailDataBody= forEntity.getBody().getPayload().getParts().get(1).getBody().getData();  
 		System.err.println("mailDataBody : "+mailDataBody); 
 		
 		String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));
		
		String orderNo = "";
		if (subject != null) {
			orderNo = fetchOrderNumber(subject);
		}
		orderNo=orderNo.trim();
		System.err.println("...........*****************............."+orderNo);
		
		if(subject.contains("Your invoice")) {
			MailData  mailDataDB = mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatus(orderNo, "redmart","Ordered");
			if(mailDataDB != null) {
				MailData  mailDataDB1 = mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatus(orderNo, "redmart","Delivered");
				if(mailDataDB1 == null) {
					
					MailData mailData1 = new MailData(); //Shipped
					 
					mailData1.setCurrentStatus("Shipped");
					mailData1.setOrderNumber(orderNo.trim());
					mailData1.setVendor("redmart");
					mailData1.setFromMailId(mailDataDB.getFromMailId());
					mailData1.setToMaild(mailDataDB.getToMaild());
					mailData1.setOrderItemsList(mailDataDB.getOrderItemsList());
					mailData1.setArrivesOn(mailDataDB.getArrivesOn()); 
					mailData1.setGrandTotal(mailDataDB.getGrandTotal());  
					mailData1.setItemsTotalDeliveryCharge(mailDataDB.getItemsTotalDeliveryCharge());
					mailData1.setItemsTotalsCost(mailDataDB.getItemsTotalsCost());
					mailData1.setCurrentStatusCode((Integer)3);
					mailData1.setDeliveryAddress(mailDataDB.getDeliveryAddress());
					mailData1.setDate(date);
					
					mailDataRepo.save(mailData1);
					
					
					MailData mailData = new MailData(); //Delivered
					
					mailData.setDate(date);
					mailData.setCurrentStatus("Delivered");
					mailData.setOrderNumber(orderNo.trim());
					mailData.setVendor("redmart");
					mailData.setItemsTotalDeliveryCharge(mailDataDB.getItemsTotalDeliveryCharge());
					mailData.setItemsTotalsCost(mailDataDB.getItemsTotalsCost());
					mailData.setFromMailId(mailDataDB.getFromMailId());
					mailData.setToMaild(mailDataDB.getToMaild());
					mailData.setOrderItemsList(mailDataDB.getOrderItemsList());
					mailData.setArrivesOn(mailDataDB.getArrivesOn()); 
					mailData.setGrandTotal(mailDataDB.getGrandTotal());  
					mailData.setCurrentStatusCode((Integer)4);
					mailData.setDeliveryAddress(mailDataDB.getDeliveryAddress());
					
					mailDataRepo.save(mailData);
					
					String msg = "Your order at Redmart, Order Id : " + orderNo + " will be Delivered today."
							+ " You can now view your order details at ShopperApp.";
					notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId,orderNo);
					
				}
			}
		}else if(subject.contains("New Order")){
			 try {
			List<OrderItem> list = new ArrayList<>();
			// Table Reading
			Document doc = Jsoup.parseBodyFragment(decodedMail1);
			 
			String deliveryAddress="";
			Element addressData = doc.select("h3").get(2);
			
//			System.err.println("addressData 00000 \n :"+addressData.text().replaceAll(":", "")+"\n \n");
			 deliveryAddress=addressData.text();
			 
			 //deliverydate
			 Element addressData1 = doc.select("h3").get(1);
			 String deliveryDate=addressData1.text().split(",")[1].split(",")[0];
			 System.err.println("FIND IT........................."+deliveryDate);
			  
			DateComponents dates = new DateComponents();
			Date deliveryDate1 = dates.getStringToDate4(deliveryDate);
			System.err.println("DATE IS..........." + deliveryDate1);
				
			 
	//		 System.err.println(":::::::::::::::::::::"+deliveryAddress);
			Element table = doc.select("table").get(0);
			System.err.println("table ::::\n"+table+"\n\n");
	//		Element div=doc.select("div)
			String grandTotal="";
			String deliveryCharge="";
			Elements rows = table.select("tr");
//			System.err.println("REDMART ROWS IS>>>>>>>>>>>>>>>>>>."+rows);
//			Element row1=rows.select("tr").get(5);
//			String row2=row1.text();
//			System.err.println("FIND IT.............................."+row1.text());
			
			int i = 1; 
			
			
			MailData orderDetails = new MailData();
			
			for ( ; i < rows.size(); i++) { // first row is the col names so skip it.
				
				if(rows.get(i).text().contains("Subtotal")) {
					break;
				}
				
				OrderItem orderItem = new OrderItem();
				Element row = rows.get(i);
	
				Elements cols = row.select("td");
	
				if (cols.size() > 2) {
					orderItem = new OrderItem();
					
//					orderItem.setItem(cols.get(0).text().replaceAll("<= /td>", "").replaceAll("=", "").replaceAll("=E2=80=93", "-"));
					orderItem.setItem(cols.get(0).text());
	//				System.err.println("REDMART ITEMS ARE:::::::::::::::::::::::"+cols.get(0).text().replaceAll("<= /td>", "").replaceAll("=", ""));
					
//					orderItem.setQty(cols.get(1).text().replaceAll("[^\\d.]", ""));
					orderItem.setQty(cols.get(1).text());
	//				System.err.println("THE QUANTIES OF ITEMS ARE:::::::::::::::::::::"+cols.get(1).text().replaceAll("[^\\d.]", ""));
					
//					orderItem.setSubTotal("$ "+cols.get(2).ownText().replaceAll("[^\\d.]", ""));
					orderItem.setSubTotal("$"+cols.get(2).text());
	//				System.err.println("THE SUBTOTALS ARE::::::::::::::::::::::;"+cols.get(2).ownText().replaceAll("[^\\d.]", ""));
 
				}
				
				if (orderItem.getItem() != null && !orderItem.getItem().isEmpty()) {
					list.add(orderItem);
				}
			}
			Element subTotalRow = table.select("tr").get(i);
			if(subTotalRow.text().contains("Subtotal")) {
				Elements subTotalRowTableData = subTotalRow.select("table").get(0).select("tr");
//				System.err.println("subTotalRowTableData Size :::: "+subTotalRowTableData.size());
				String itemsTotalsCost=subTotalRowTableData.get(0).select("td").get(1).text();
				orderDetails.setItemsTotalsCost(itemsTotalsCost);
				
				//LiveUp Credits'
				if(subTotalRowTableData.get(1).select("td").get(0).text().contains("Delivery Fee")) {
					String itemsTotalsDeliveyCharge=subTotalRowTableData.get(1).select("td").get(1).text();
					
					orderDetails.setItemsTotalDeliveryCharge(itemsTotalsDeliveyCharge);
				}
				else if(subTotalRowTableData.get(2).select("td").get(0).text().contains("Delivery Fee")) {
					String itemsTotalsDeliveyCharge=subTotalRowTableData.get(2).select("td").get(1).text();
					
					orderDetails.setItemsTotalDeliveryCharge(itemsTotalsDeliveyCharge);
				}
				 
				
				//Delivery Fee
				
				
				
				i=i+subTotalRowTableData.size();
				
			}
			
			Element grandTotalRow = table.select("tr").get(i+1);
			
			System.err.println("grandTotalRow ::::::: \n"+grandTotalRow+"\n\n");
			if(grandTotalRow.text().contains("Grand")) {
				
				String grandTotalPrice = grandTotalRow.select("td").get(0).select("span").get(0).text();
				
				System.err.println("grandTotalPrice :::: "+grandTotalPrice);
				orderDetails.setGrandTotal(grandTotalPrice);
			} 
			
			
			MailData  mailDataDB = mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatus(orderNo, "redmart","Ordered");
			if(mailDataDB == null ) {
				orderDetails.setDate(date);
				orderDetails.setCurrentStatus("Ordered");
				orderDetails.setOrderNumber(orderNo.trim());
 				orderDetails.setVendor("redmart");
				orderDetails.setFromMailId(fetchFromMail(fromMail));
				orderDetails.setToMaild(toMailId);
				orderDetails.setOrderItemsList(list);
				orderDetails.setArrivesOn(deliveryDate1); 
				orderDetails.setCurrentStatusCode((Integer)1);
				orderDetails.setDeliveryAddress(deliveryAddress);
				mailDataRepo.save(orderDetails);
				
				String msg="Your have ordered at REDMART. Your order number is : "+orderNo+" . You can now view your order details at ShopperApp.";
				
				notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNo);
			}
			 }catch (Exception e) {
				// TODO: handle exception
			}
 		}
		return null;
	}

	private String fetchOrderNumber(String subject) {
		String orderId = "";
		if (subject.contains("#")) {
			System.err.println("HAI SUBJECT ISSSSSSSSSSSSSSSSSS");
			String[] split = subject.split("#");
			orderId = split[1];
		}
		return orderId;
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

	private String fetchOrderIdFromSubject(String subject) {
		// System.err.println("Subject Data -------->" + subject);
		String orderId = null;
		if (subject.contains("#")) {
			Pattern compile = Pattern.compile("#(.*?)\\)");
			Matcher matcher = compile.matcher(subject);
			while (matcher.find()) {
				orderId = matcher.group(1);
				// System.err.println("Order Id Data -------->" + orderId);
			}
		}
		return orderId;
	} 
	 

	@Override
	public String devFetchMessageId(String accessToken, String mailId) {

		// String accessToken = tokenComponent.getAccessTokenAndRefreshToken(authCode,
		// mailId);
		// if (accessToken.equalsIgnoreCase("Login Failed")) {
		// return accessToken;
		// }
		int i = 1;
		System.out.println("accessToken ::: " + accessToken);
		logger.info("At fetchMessageId");
		UserToken userToken = userTokenRepo.findByMailId(mailId);
		String deviceId = "";
		if (userToken == null) {
			userToken = new UserToken();
			deviceId = "";
		} else {
			deviceId = userToken.getDeviceId();
		}
		//

//		List<MailData> mailDataDbList = mailDataRepo.findByToMaildOrderByDateDesc(mailId);

		logger.info("At  devfetchMessageId . .. . . .");

		// String accessToken = tokenComponent.getAccessTokenAndRefreshToken(authCode,
		// mailId);
		// if (accessToken.equalsIgnoreCase("Login Failed")) {
		// return accessToken;
		// }
		Boolean stopMailRead = false;

		System.out.println("accessToken ::: " + accessToken);
		logger.info("At fetchMessageId");

		List<Messages> mailIdsList = new ArrayList<>();
		// if(!mailDataDbList.isEmpty() && mailDataDbList.get(0) != null) {
		// if( userToken != null && userToken.getLastMessageId() != null &&
		// userToken.getLastMessageId() != "" &&
		// !userToken.getLastMessageId().isEmpty()) {
		 
			Date currentDate = new Date();
			java.util.Date getMailsAfterDate = dateComponent.subDays(currentDate, 2);
			System.err.println(getMailsAfterDate);

			this.restTemplate = restTemplateBuilder.errorHandler(restTemplateErrorHandler).build();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", accessToken);
			HttpEntity<String> entity = new HttpEntity<>(headers);
//			 String
//			 api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=after:"+dateComponent.convertUtilDateToEpoch(getMailsAfterDate);
			 
//			String api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:info@e.zalora.sg";
			 
//			String  api="https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:campaigns@e.zalora.sg ";
//		     String api = "https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:style@e.zalora.sg";
//			String api = "https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:auto-confirm@amazon.in";
//			 String api = "https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:userinfo@id.jabong.com";
//			 String api = "https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:alerts@bigbasket.com";
			 String api = "https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:updates@myntra.com";
//			 String api = "https://www.googleapis.com/gmail/v1/users/userid/messages?q=from:no-reply@flipkart.com";
//			 String api="https://www.googleapis.com/gmail/v1/users/userid/messages";
			System.out.println("api ::::: " + api);
			proprtyComponents.setGoogleMessageIdUrl(api);
			String url = proprtyComponents.getGoogleMessageIdUrl().replaceAll("userid", mailId);
			System.err.println(url);
			ResponseEntity<MessageIdDTO> forEntity = restTemplate.exchange(url, HttpMethod.GET, entity,
					MessageIdDTO.class);
			System.out.println(forEntity);
			System.err.println(
					"forEntity.getBody().getResultSizeEstimate() ::: " + forEntity.getBody().getResultSizeEstimate());
			if (forEntity.getBody().getResultSizeEstimate() != null
					&& forEntity.getBody().getResultSizeEstimate() > 0) {
				// Updating the Last Read Message Id
				// if(lastMessageId == "" || lastMessageId == null || lastMessageId.isEmpty()) {
				userToken.setLastMessageId(forEntity.getBody().getMessages().get(0).getId());
				userTokenRepo.save(userToken);
				// }
				// mailIdsList.addAll(forEntity.getBody().getMessages());

				// forEntity.getBody().getMessages().forEach(messages -> {
				for (int r = 0; r < forEntity.getBody().getMessages().size(); r++) {
					Messages messages = forEntity.getBody().getMessages().get(r);

//					 if( !lastMessageId.equalsIgnoreCase(messages.getId()) && !stopMailRead) {
					//if(messages.getId().equalsIgnoreCase("166f3588195d06ba")){
						mailIdsList.add(messages);
//					
					 //System.err.println("ADDEDDDD 1"+messages.getId()+"\t ");
					// }
//					else {
					// stopMailRead = true;
					// break;
					// }
				}
				// nextPage Mails Starts
				try {
					if (!stopMailRead) {
						while (forEntity.getBody().getNextPageToken() != null && !stopMailRead) {
							this.restTemplate = restTemplateBuilder.errorHandler(restTemplateErrorHandler).build();
							proprtyComponents.setGoogleMessageIdUrl(
									api + "&pageToken=" + forEntity.getBody().getNextPageToken());
							url = proprtyComponents.getGoogleMessageIdUrl().replaceAll("userid", mailId);
							forEntity = restTemplate.exchange(url, HttpMethod.GET, entity, MessageIdDTO.class);
							if (forEntity.getBody().getResultSizeEstimate() != null
									&& forEntity.getBody().getResultSizeEstimate() > 0) {
								// mailIdsList.addAll(forEntity.getBody().getMessages());
								for (int r = 0; r < forEntity.getBody().getMessages().size(); r++) {
									Messages message1 = forEntity.getBody().getMessages().get(r);
									//
									// if(lastMessageId != message1.getId() && !stopMailRead) {
									mailIdsList.add(message1);
									// System.err.println("ADDEDDDD");
									// }else {
									// stopMailRead = true;
									// break;
									// }
								}

							}
							// forEntity.getBody().getMessages().forEach(messages -> {
							// try {
							// getData(messages.getId(), accessToken, mailId);
							// } catch (MessagingException e) {
							// // TODO Auto-generated catch block
							// e.printStackTrace();
							// }
							// });
							// }
						}
					}
				} catch (Exception e) {
					System.err.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
					e.printStackTrace();
				}
				
				System.err.println("mailIdsList : lllllll  "+mailIdsList.size());
				ListIterator<Messages> li = mailIdsList.listIterator(mailIdsList.size());
				while (li.hasPrevious()) {
					Messages messages = (Messages) li.previous();
					try {
						getData(messages.getId(), accessToken, mailId, deviceId);
					} catch (MessagingException e) {
						
						e.printStackTrace();
					}
				}
			}
			return "Success";
		 

	 
	}

}