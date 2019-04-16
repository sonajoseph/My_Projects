package com.shopperapp.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import com.shopperapp.service.BigBasketService;

import ch.qos.logback.classic.Logger;
/**
 * 
 * @author uvionics Tech.
 * Developer 2 :sona Joseph
 * Purpose :To handle all details of bigbasket.
 *
 */
@Service
public class BigBasketAPIServiceImpl implements BigBasketService {

	@Autowired
	OrderRepo orderRepo;

	@Autowired
	NotificationMessageFCM notificationMessageFCM;

	@Autowired
	DateComponents dateComponent;
	
	/**
	 *  Reading the entire bigbasket mail and saving the required datas from mail to database
	 */
	 
	@Override
	public void readDataFromBigBasket(String decodedMail, String mailID, String subject, Date date, String fromMail,
			String deviceId, RawMessageResponseDTO body, ResponseEntity<MessageResponseDTO> forEntity) {
		//Through full data format ,entire mail is in under the payload.
		String mailDataBody = forEntity.getBody().getPayload().getParts().get(1).getBody().getData();

		String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));
		
		//for viewing the mail in html format.Taking details from mail by examining table,row & coloumn.
		Document doc = Jsoup.parseBodyFragment(decodedMail1);

		System.err.println("SUBJECT IS..........." + subject);

		try {

			List<OrderItems> orderItemsList = new ArrayList<>();
			String orderNumber = "";
			if (subject.indexOf("(") > 0 && subject.indexOf(")") > 0) {
				orderNumber = subject.substring(subject.indexOf("(") + 1, subject.indexOf(")")).trim();
			}
			System.err.println("orderNumber :::: " + orderNumber);

			if (subject.contains("confirmation")) {
				//multiple saving to database in each rum is avoided in each run.

				Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "BigBasket");
				if (order == null) {
					order = new Order();

					System.err.println(("At readBigBasketMails......................"));

					// deliveryADdress
					String deliveryAddress = "";
					String deliveryDate = "";
					String subTotal = "";

					Element table1 = doc.select("table").get(2).select("table").get(3);

					Element trackIt = table1.select("tr").get(0).select("a").get(0);

					deliveryAddress = table1.select("tr").get(0).select("td").get(2).text().split("address:")[1];

					// deliveryDate
					Element table10 = doc.select("table").get(2).select("table").get(3);

					String[] deliveryDateArr = table1.select("tr").get(1).select("td").get(1).text().split("between")[0]
							.split(" ");

					deliveryDate = deliveryDateArr[1] + " " + deliveryDateArr[2] + " " + deliveryDateArr[3];

					order.setOrderNum(orderNumber);
					order.setVendorName("BigBasket");
					order.setDeliveryAddress(deliveryAddress);
					order.setCreatedDate(new Date());
					order.setTrackIt("" + trackIt.attr("href"));
					order.setUserEmailId(mailID);
					order.setFromMailId(fromMail);

					// getting items

					Element table6 = doc.select("table").get(6);
					System.err.println("table6......" + table6);

					Elements table6s = doc.select("table").get(6).select("tr");

					for (int i = 1; i < table6s.size(); i++) {
						OrderItems orderItem = new OrderItems();
						orderItem.setOrderId(orderNumber);
						orderItem.setCreatedAt(new Date());

						Elements itemsDatas = table6s.get(i).select("td");

						if (itemsDatas.size() > 1 && itemsDatas.size() == 8
								&& (!itemsDatas.get(2).text().contains("Promotion used"))

						) {

							String itemName = itemsDatas.get(2).text();

							String qty = itemsDatas.get(3).text();
							String unitPrice = itemsDatas.get(4).text();
							subTotal = itemsDatas.get(5).text();
							String saving = itemsDatas.get(6).text();
							orderItem.setProductName(itemName);
							orderItem.setQty(Integer.parseInt(qty));
							orderItem.setPrice(subTotal);
							orderItem.setUnitPrice(unitPrice);
							orderItem.setSavings(saving);
							// orderItem.setDeliveryDate(deliveryDate);
 
							OrderItemStatus orderItemStatus = new OrderItemStatus();
							orderItemStatus.setOrderNumber(orderNumber);
							orderItemStatus.setCurrentStatus("Ordered");
							orderItemStatus.setCurrentStatusCode(1);
							orderItemStatus.setCrDate(new Date());

							List<OrderItemStatus> status = new ArrayList<OrderItemStatus>();
							status.add(orderItemStatus);

							orderItem.setStatus(status);
							orderItemsList.add(orderItem);

						} else if (itemsDatas.size() == 4) {
							if (itemsDatas.get(1).text().contains("Sub Total")) {
								String subTotals = itemsDatas.get(2).text();
								order.setItemsTotalsCost(subTotals);
								System.err.println("subTotals is........" + subTotals);
							} else if (itemsDatas.get(1).text().contains("Delivery Charges")) {
								String deliveryCharges = itemsDatas.get(2).text();
								order.setItemsTotalDeliveryCharge(deliveryCharges);
								System.err.println("deliveryCharges is........" + deliveryCharges);
							} else if (itemsDatas.get(1).text().contains("Total Savings")) {
								String totalSavings = itemsDatas.get(2).text();

								System.err.println("totalSavings is........" + totalSavings);
							} else if (itemsDatas.get(1).text().contains("Final Total")) {
								String grandTotal = itemsDatas.get(2).text();
								order.setGrandTotal(grandTotal);
								System.err.println("grandTotal is........" + grandTotal);
							}
						}

					}

					order.setOrderItems(orderItemsList);
					orderRepo.save(order);

					String msg = "Your order at BigBasket, Order Id : " + orderNumber + " had placed successfully."
							+ " You can now view your order details at ShopperApp.";
					notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNumber);

					System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNumber);
				}
				
				//Reading the cancellation mails and saving the details to database.

			} else if (subject.contains("Cancellation")) {
				orderNumber = doc.select("table").get(5).select("tr").get(0).select("td").get(1).text();

				Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "BigBasket");
//checking whether there is order.if it is there , items are fetched from mail.
				if (order != null) {

					for (int i = 0; i < order.getOrderItems().size(); i++) {

						List<OrderItemStatus> statusList = order.getOrderItems().get(i).getStatus();

						Boolean packedData = false;
						for (int j = 0; j < statusList.size(); j++) {
							if (statusList.get(j).getCurrentStatus().equalsIgnoreCase("Cancelled")) {
								packedData = true;
							}
						}
						// if packedData!=true.
						if (!packedData) {
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
				//a notification message is sent.

				String msg = "Your order at BigBasket, Order Id : " + orderNumber + "is  cancelled successfully."
						+ " You can now view your cancel details at ShopperApp.";
				notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNumber);

				System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNumber);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
