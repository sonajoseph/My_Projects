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
import com.shopperapp.mongo.models.Order;
import com.shopperapp.mongo.models.OrderItemStatus;
import com.shopperapp.mongo.models.OrderItems;
import com.shopperapp.mongo.repo.OrderRepo;
import com.shopperapp.service.MyntraService;

/**
 * 
 * @author uvionics Tech. 
 * Developer 2 :sona Joseph 
 * Purpose :To take all details of Myntra.
 *               
 *
 */
@Service
public class MyntraAPIServiceImpl implements MyntraService {

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	DateComponents dateComponent;

	@Autowired
	NotificationMessageFCM notificationMessageFCM;

	/**
	 * Reading the entire myntra mail and saving the required datas from mail to
	 * database
	 */

	@Override
	public void readDataFromMyntra(String decodedMail, String toMailId, String subject, Date date, String fromMail,
			String deviceId, RawMessageResponseDTO rawMessageResponseDTO,
			ResponseEntity<MessageResponseDTO> forEntity) {
		// Through full data format ,entire mail is in under the payload.
		String mailDataBody = forEntity.getBody().getPayload().getParts().get(0).getBody().getData();
		System.err.println("mailDataBody : " + mailDataBody);
		String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));
		// for viewing the mail in html format.Taking details from mail by examining table,row & coloumn.
		Document doc = Jsoup.parseBodyFragment(decodedMail1);
		System.err.println("SUBJECT IS..........." + subject);
		List<OrderItems> orderItemsList = new ArrayList<>();
		String items = "";
		try {
			if (subject.contains("Confirmation")) {

				List<OrderItem> orderItemList = new ArrayList<OrderItem>();
				System.err.println(("At readMyntraMails......................"));
				// orderNo
				String orderNumber = "";
				Element table1 = doc.select("table").get(1).select("tr").get(7);
				System.err.println(table1.text());
				orderNumber = table1.text().split("Order no.:")[1].trim();
				// trackit

				Element trackIt = doc.select("table").get(1).select("tr").get(5).select("a").get(0);
				System.err.println("trackIt..................." + trackIt.attr("href"));

				// contactSeller

				System.err.println("THE ORDERNO IS ............" + orderNumber);
				
				//multiple saving to database in each rum is avoided in each run.

				Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "myntra");
				if (order == null) {
					order = new Order();

					// deliveryADdress
					String deliveryAddress = "";
					Element table4 = doc.select("table").get(4);
					System.err.println("TABLE 3 IS....." + table4.text().split("Address")[1].split("Expect")[0]);
					deliveryAddress = table4.text().split("Address")[1].split("Expect")[0];

					// DeliveryDate
					String deliveryDate = "";

					System.err.println("TABLE 3 IS....." + table4.text().split("delivery by")[1]);
					deliveryDate = table4.text().split("delivery by")[1];
					//saving to database.

					order.setOrderNum(orderNumber);
					order.setVendorName("Myntra");
					order.setDeliveryAddress(deliveryAddress);
					order.setCreatedDate(new Date());
					order.setTrackIt("" + trackIt.attr("href"));
					order.setUserEmailId(toMailId);
					order.setFromMailId(fromMail);

					// items

					String qty = "";
					String subTotal = "";
					String discount = "";
					String size = "";
					String seller = "";

					Element table2 = doc.select("table").get(2);
					System.err.println(".................." + table2);
					items = table2.select("tr").get(0).text().split("Qty")[0].split("Size")[0].trim();
					System.err.println("ITEM IS ............." + items);
					size = table2.select("tr").get(0).text().split(": ")[1];
					System.err.println("size is.." + size);

					System.err.println("item is....." + items);

					qty = table2.select("tr").get(1).text().split("Qty: ")[1].split("Price")[0];

					System.err.println("qty is.......++++" + qty);

					subTotal = table2.select("tr").get(5).text().split("Total")[1].split("Sold by:")[0];

					System.err.println("subTotal is..." + subTotal);

					// billingDEtails
					// shipping charge
					String deliveryCharge = "";
					String grandTotal = "";
					String modeOfPayment = "";
					Element table3 = doc.select("table").get(3).select("table").get(2).select("table").get(0)
							.select("tr").get(4);
					System.err.println("deliverycahrge is****************" + table3);
					deliveryCharge = table3.text().split("Shipping charge")[1];
					System.err.println(".........." + deliveryCharge);

					// grandTotal
					Element table5 = doc.select("table").get(3).select("table").get(2).select("table").get(0)
							.select("tr").get(5);
					System.err.println("grandtOTal is................." + table5);
					grandTotal = table5.text().split("Total")[1];
					System.err.println(".........." + grandTotal);
					// mode oF PAYMENT
					Element table6 = doc.select("table").get(3).select("table").get(2).select("table").get(0)
							.select("tr").get(6);
					modeOfPayment = table6.text().split("Payment")[1];
					System.err.println("......." + modeOfPayment);

					// contactseller

					Element contactSellers = doc.select("table").get(1).select("tr").get(35).select("a").get(0);

					System.err.println(":::::::::::::::::::::" + contactSellers.attr("href"));
					order.setContactSeller(contactSellers.attr("href"));

					order.setItemsTotalsCost(subTotal);
					order.setItemsTotalDeliveryCharge(deliveryCharge);
					order.setGrandTotal(grandTotal);
                    
					OrderItems orderItem = new OrderItems();
					orderItem.setOrderId(orderNumber);
					orderItem.setCreatedAt(new Date());
					orderItem.setProductName(items);
					orderItem.setPrice(subTotal);
					orderItem.setQty(Integer.parseInt(qty));
					orderItem.setSize(size);
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
					order.setOrderItems(orderItemsList);

					// multiple saving will be avoided in each run.
					Order orderDB = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "Myntra");
					if (orderDB == null) {
						orderRepo.save(order);
					}
					String msg = "Your order at myntra, Order Id : " + orderNumber + " had placed successfully."
							+ " You can now view your order details at ShopperApp.";
					notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNumber);

					System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNumber);

				}

			}
           //checking whether cancel mails is exist.
			else if (subject.contains("cancelled")) {
				// trim will remove space.
				String orderNumber = doc.select("table").get(2).select("tr").get(0).text().split("Order number:")[1]
						.split("Refund amount:")[0].trim();
				System.err.println("table0 is.........." + orderNumber);
				String productName = doc.select("table").get(7).select("tr").get(0).select("td").get(0).text().trim();
				System.err.println("productName.*********************" + productName);

				try {
					Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "Myntra");
					System.err.println("order : " + order);
					if (order != null) {
						for (int i = 0; i < order.getOrderItems().size(); i++) {
							// checking product name in db vs the product name fetched from mail.
							if (order.getOrderItems().get(i).getProductName().contains(productName)) {

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

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//notifiaction message is sent.

				String msg = "Your order at myntra, Order Id : " + orderNumber + "is cancelled successfully."
						+ " You can now view your order details at ShopperApp.";
				notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNumber);

				System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNumber);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
