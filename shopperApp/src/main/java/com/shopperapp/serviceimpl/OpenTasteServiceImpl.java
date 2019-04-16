package com.shopperapp.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.shopperapp.components.DateComponents;
import com.shopperapp.components.NotificationMessageFCM;
import com.shopperapp.dto.MessageResponseDTO;
import com.shopperapp.dto.OrderDetail;
import com.shopperapp.dto.OrderItem;
import com.shopperapp.dto.RawMessageResponseDTO;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.models.Order;
import com.shopperapp.mongo.models.OrderItemStatus;
import com.shopperapp.mongo.models.OrderItems;
import com.shopperapp.mongo.repo.MailDataRepo;
import com.shopperapp.mongo.repo.OrderRepo;
import com.shopperapp.service.OpenTasteService;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author uvionics Tech. 
 * Developer 2 :sona Joseph 
 * Purpose :To take all details of Opentaste.
 *               
 *
 */
@Slf4j
@Service
public class OpenTasteServiceImpl implements OpenTasteService {

	private static final Logger logger = LoggerFactory.getLogger(OpenTasteServiceImpl.class);
	@Autowired
	NotificationMessageFCM notificationMessageFCM;

	@Autowired
	MailDataRepo mailDataRepo;

	@Autowired
	DateComponents dateComponent;

	@Autowired
	OrderRepo orderRepo;
	
	/**
	 * Reading the entire opentaste mail and saving the required datas from mail to database
	  
	 */


	@Override
	public void readDataFromOpenTaste(String toMailId, String subjectData, Date date, String fromMail, String deviceId,
			RawMessageResponseDTO body, ResponseEntity<MessageResponseDTO> forEntity) {

		logger.info("At readDataFromMail ---------->>>>>>>>>    OpenTaste +++++++++");
		System.err.println("SUBJECT IS......" + subjectData);

		String orderNo = "";
		if (subjectData != null) {
			log.info("subjectData.....1" + subjectData);
			orderNo = fetchOpentasteOrderNo(subjectData);
			System.err.println("ORDER NO IS........................." + orderNo);
		}

		
		// Data is stored in parts
		//checking delivered mails.
		if (subjectData.contains("delivered")) { 
			System.err.println("Now at is delivered!\n\n");
			String mailDataBody1 = forEntity.getBody().getPayload().getParts().get(0).getBody().getData();
			log.info("mailDataBody :::: " + mailDataBody1);

			String decodedMail2 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody1));
            //checking the order mails.
			List<MailData> dataList = mailDataRepo.findByOrderNumberAndCurrentStatus(fetchOpentasteOrderNo(subjectData),
					"Ordered");
			if (dataList != null && !dataList.isEmpty()) {
				List<MailData> deliveredDataList = mailDataRepo
						.findByOrderNumberAndCurrentStatus(fetchOpentasteOrderNo(subjectData), "Delivered");
				if (deliveredDataList == null || deliveredDataList.isEmpty()) {
					MailData mailData = dataList.get(0);
					System.err.println("Deliverd ---------> " + mailData);
					MailData updateMailData = new MailData();
					if (mailData != null) {
						updateMailData.setArrivesOn(mailData.getArrivesOn());
						updateMailData.setCurrentStatus("Delivered");
						updateMailData.setDate(date);
						updateMailData.setDeliveryAddress(mailData.getDeliveryAddress());
						updateMailData.setFromMailId(mailData.getFromMailId());
						updateMailData.setGrandTotal(mailData.getGrandTotal());
						updateMailData.setOrderItemsList(mailData.getOrderItemsList());
						updateMailData.setOrderNumber(mailData.getOrderNumber());
						updateMailData.setToMaild(mailData.getToMaild());
						updateMailData.setVendor(mailData.getVendor());
						updateMailData.setCurrentStatusCode((Integer) 4);
						mailDataRepo.save(updateMailData);
						//notification message of delivery is sent.
						String msg = "Your order at Opentast, Order Id : " + orderNo
								+ " status have changed to Delivered now."
								+ " You can now view your order details at ShopperApp.";
						notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNo);
					}
				}
			}
		} else if (subjectData.contains("Your purchase at") || subjectData.contains("is on the way")) { 
																										
			System.err.println("DATA GOING TO READ NOW....");
			// since data is stored not in parts here.
			String mailDataBody = forEntity.getBody().getPayload().getBody().getData();
			String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));
		    Document doc = Jsoup.parseBodyFragment(decodedMail1);
			try {
				if (subjectData.contains("Your purchase at")) {
					
					// deliveryAddress
					Element table1 = doc.select("table").get(14);

					String deliveryAddress = table1.text().split("Address:")[1];

					// DeliveryDate
					Element table2 = doc.select("table").get(13).select("tr").get(0).select("td").get(0);

					String deliveryDate = table2.text().split("Delivery Date:")[1].split("Delivery Time:")[0];

					Date deliveryDates = dateComponent.getStringToDate3(deliveryDate);

					Element table0 = doc.select("table").get(17);

					Elements items = table0.select("tr");

					int i = 1;
					List<OrderItems> list = new ArrayList<>();
					for (; i < items.size(); i++) {
						OrderItems orderItem = new OrderItems();
						if (items.get(i).select("td").get(0).text().contains("Delivery instructions")) {
							break;
						}

						String itemName = "";
						String itemQty = "";
						String itemSubTotal = "";
						itemName = items.get(i).select("td").get(1).text();
						orderItem.setProductName(itemName);
						itemQty = items.get(i).select("td").get(2).text();
						orderItem.setQty(Integer.parseInt(itemQty.trim()));

						itemSubTotal = items.get(i).select("td").get(3).select("table").get(0).select("tr").get(0)
								.select("td").get(1).text();
						orderItem.setPrice(itemSubTotal);

						i++;

						if (orderItem.getProductName() != null || !orderItem.getProductName().isEmpty()) {

							System.err.println("ITEM ADDED");
							List<OrderItemStatus> status = new ArrayList<>();

							OrderItemStatus orderItemStatus = new OrderItemStatus();
							orderItemStatus.setCrDate(date);
							orderItemStatus.setCurrentStatus("Ordered");
							orderItemStatus.setCurrentStatusCode(1);
							orderItemStatus.setOrderNumber(orderNo);

							status.add(orderItemStatus);

							orderItem.setStatus(status);

							list.add(orderItem);
						}
					}

					i++;
					String deliveyCharges = items.get(i).select("td").get(0).text();
					deliveyCharges = deliveyCharges.split("\\:")[1];

					i++;
					String grandTotal = items.get(i).select("td").get(0).text();
					grandTotal = grandTotal.split("\\:")[1];

					Order order = new Order();
					//checking order mails.

					Order orderDataDb = orderRepo.findOneByOrderNumAndVendorName(orderNo, "Opentaste");

					if (orderDataDb == null) {
						order.setCreatedDate(date);
						order.setDeliveryAddress(deliveryAddress);
						order.setFromMailId(fromMail);
						order.setGrandTotal(grandTotal);
						order.setItemsTotalDeliveryCharge(deliveyCharges);

						order.setOrderItems(list);

						order.setOrderNum(orderNo);
						order.setUserEmailId(toMailId);
						order.setVendorName("Opentaste");

						orderRepo.save(order);
						//notification message is sent. 

						String msg = "Your order at Opentaste, Order Id : " + orderNo + " had placed successfully."
								+ " You can now view your order details at ShopperApp.";
						notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNo);
					}
                   //checking shipped mails.
				} else if (subjectData.contains("is on the way!")) {

					List<MailData> dataList = mailDataRepo
							.findByOrderNumberAndCurrentStatus(fetchOpentasteOrderNo(subjectData), "Ordered");
					if (dataList != null && !dataList.isEmpty()) {
						List<MailData> shippedDataList = mailDataRepo
								.findByOrderNumberAndCurrentStatus(fetchOpentasteOrderNo(subjectData), "Shipped");
						if (shippedDataList == null || shippedDataList.isEmpty()) {
							MailData mailData = dataList.get(0);
							System.err.println("Shipped ---------> " + mailData);
							MailData updateMailData = new MailData();
							if (mailData != null) {
								updateMailData.setArrivesOn(mailData.getArrivesOn());
								updateMailData.setCurrentStatus("Shipped");
								updateMailData.setDate(date);
								updateMailData.setDeliveryAddress(mailData.getDeliveryAddress());
								updateMailData.setFromMailId(mailData.getFromMailId());
								updateMailData.setGrandTotal(mailData.getGrandTotal());
								updateMailData.setOrderItemsList(mailData.getOrderItemsList());
								updateMailData.setOrderNumber(mailData.getOrderNumber());
								updateMailData.setToMaild(mailData.getToMaild());
								updateMailData.setVendor(mailData.getVendor());
								updateMailData.setCurrentStatusCode((Integer) 3);
								mailDataRepo.save(updateMailData);

								String msg = "Your order at Opentast, Order Id : " + orderNo
										+ " status have changed to Shipped now."
										+ " You can now view your order details at ShopperApp.";
								notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNo);

							}
						}
					}
					System.err.println("End SUBJECT IS......" + subjectData);
					
				}
			} catch (Exception e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}

	}
/**
 * 
 * @param subjectData
 * @return orderno.
 */
	private String fetchOpentasteOrderNo(String subjectData) {
        System.err.println("Subject Data -------->" + subjectData);
		String orderId = null;
		if (subjectData.contains("#")) {
			Pattern compile = Pattern.compile("#(.*?)\\)");
			Matcher matcher = compile.matcher(subjectData);
			while (matcher.find()) {
				orderId = matcher.group(1);
				System.err.println("Order Id Data -------->" + orderId);
			}
		}
		return orderId;
	}

}
