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
import com.shopperapp.components.TokenComponent;
import com.shopperapp.dto.MessageResponseDTO;
import com.shopperapp.dto.OrderItem;
import com.shopperapp.dto.RawMessageResponseDTO;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.models.Order;
import com.shopperapp.mongo.models.OrderItemStatus;
import com.shopperapp.mongo.models.OrderItems;
import com.shopperapp.mongo.repo.MailDataRepo;
import com.shopperapp.mongo.repo.OrderRepo;
import com.shopperapp.mysql.repo.UserTokenRepo;
import com.shopperapp.service.JabongService;


/**
 * 
 * @author uvionics Tech.
 * Developer 2 :sona Joseph
 * Purpose :To take all details of Jabong.
 *
 */
@Service
public class JabongAPIServiceImpl implements JabongService {

	@Autowired
	private MailDataRepo mailDataRepo;
	@Autowired
	DateComponents dateComponent;

	@Autowired
	NotificationMessageFCM notificationMessageFCM;

	@Autowired
	private OrderRepo orderRepo;
	
 
	
	/**
	 * Reading the entire Jabong mail and saving the required datas of mail to database.
	 */
	@Override
	public void readDataFromJabong(String decodedMail, String mailID, String subject, Date date, String fromMail,
			String deviceId, RawMessageResponseDTO body, ResponseEntity<MessageResponseDTO> forEntity) {
		
        // Through full data format ,entire mail is in under the payload.
		String mailDataBody = forEntity.getBody().getPayload().getParts().get(0).getBody().getData();
		System.err.println("mailDataBody : " + mailDataBody);

		String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));
		
		//for viewing the mail in html format.Taking details from mail by examining table,row & coloumn.
		Document doc = Jsoup.parseBodyFragment(decodedMail1);
        //subject of mail
		System.err.println("SUBJECT IS..........." + subject);
		List<OrderItems> orderItemsList = new ArrayList<>();
		try {
			if (subject.contains("Confirmation")) {
				//checking the order mails.

				List<OrderItem> orderItemList = new ArrayList<OrderItem>();
				System.err.println(("At readJabongMails......................"));
				// orderNo
				//html is in div format,not in table format.
				String orderNo = "";
				Element orderNumAndAddress = doc.select("div").get(14);
				

				String deliveryAddress = "";

				if (orderNumAndAddress.select("div").get(0).select("ul").get(0).select("li").get(0).text()
						.contains("Order number")) {
					orderNo = orderNumAndAddress.select("div").get(0).select("ul").get(0).select("li").get(0)
							.select("p").get(1).text();
				}

				deliveryAddress = orderNumAndAddress.select("address").get(0).text();

				System.err.println("Delivey Address::: " + deliveryAddress);
				
				//tracking id
				Element trackIt=doc.select("div").get(10).select("a").get(0);
				System.err.println("trackIt.............."+trackIt.attr("href"));
			

				Elements product_details = doc.select(".product-details");
				// System.err.println("THE PRODUCT DETAILS ARE......."+product_details.text());
                //multiple saving to database in each rum is avoided.
				Order order = orderRepo.findOneByOrderNumAndVendorName(orderNo, "Jabong");
				if (order == null) {
					order = new Order();
					order.setOrderNum(orderNo);
					order.setVendorName("Jabong");
					order.setDeliveryAddress(deliveryAddress);
					order.setCreatedDate(new Date());
					order.setTrackIt(""+trackIt.attr("href"));
					order.setUserEmailId(mailID);
					order.setFromMailId(fromMail);
                    //taking items .
					for (int i = 0; i < product_details.size(); i++) {

					
						Element itemDetails = product_details.get(i).select("ul").get(0);
						Element itemPriceDetails = product_details.get(i).select("ul").get(1);

						String itemName = "";
						String size = "";
						String qty = "";
						String seller = "";

						itemName = itemDetails.select("li").get(0).text().trim();
						String moreDetails = itemDetails.select("li").get(1).text();
						String moreDetailsArr[] = moreDetails.split("\\|");
						size = moreDetailsArr[0].split("\\:")[1];
						qty = moreDetailsArr[1].split("\\:")[1].trim();
						System.err.println("QTY IS..............." + qty);
						seller = itemDetails.select("li").get(2).text().split("Sold by:")[1];
						System.err.println("..........." + seller);

						String itemPrice = itemPriceDetails.select("li").get(0).select("span").get(1).text();
						String discount = itemPriceDetails.select("li").get(1).select("span").get(1).text();
						String total = itemPriceDetails.select("li").get(2).select("span").get(1).text();

						
                        //saving to database.
						OrderItems orderItem = new OrderItems();
						orderItem.setOrderId(orderNo);
						orderItem.setCreatedAt(new Date());
						orderItem.setProductName(itemName);
						orderItem.setPrice(total);
						orderItem.setQty(Integer.parseInt(qty));
						orderItem.setSize(size);
//						orderItem.setDeliveryDate(deliveryDate);

						/*
						 * if(orderItem.getItem() != null || !orderItem.getItem().isEmpty()) {
						 * orderItemList.add(orderItem); System.err.println("ITEM ADDED"); }
						 */

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

					Element finalBill_details = doc.select("#pricing-summary-area").get(0).select("ul").get(0);

					String packageValue = finalBill_details.select("li").get(0).select("span").get(1).text();
					String tax = finalBill_details.select("li").get(1).select("span").get(1).text();
					String shippingCharge = finalBill_details.select("li").get(2).select("span").get(1).text();
					String grandTotal = finalBill_details.select("li").get(3).select("span").get(1).text();
                    order.setItemsTotalsCost(packageValue);
					order.setItemsTotalDeliveryCharge(shippingCharge);
					order.setGrandTotal(grandTotal);
					order.setOrderItems(orderItemsList);
					
					// multiple saving will be avoided in each run.since it is checking with database.
					
					Order orderDB = orderRepo.findOneByOrderNumAndVendorName(orderNo, "Jabong");
					if (orderDB == null) {
						orderRepo.save(order);
					}
					//order notification message is sent.
					String msg = "Your order at jabong, Order Id : " + orderNo + " had placed successfully."
							+ " You can now view your order details at ShopperApp.";
					notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNo);

					System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" + orderNo);
				}
				

			} else if (subject.contains("cancelled")) {

				String orderNumber = doc.select("div").get(9).select("li").get(0).text().split("Order number ")[1];
				String productName=doc.select("div").get(17).select("ul").get(0).select("li").get(0).text();
				System.err.println("PRODUCTNAME IS............"+productName);
              //checking whether there is an order exist.if exists then only  take the details from cancel mail.
				Order order = orderRepo.findOneByOrderNumAndVendorName(orderNumber, "Jabong");

				System.err.println("order ::: " + order);
				if (order != null) {
              //taking order items from mail.
					for (int i = 0; i < order.getOrderItems().size(); i++) {
						if(order.getOrderItems().get(i).getProductName().equalsIgnoreCase(productName)){

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
					
               //passing notification message of cancellation.
				String msg = "Your order at Jabong, Order Id : " + orderNumber + "is  cancelled successfully."
						+ " You can now view your cancel details at ShopperApp.";
				notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderNumber);
				}


				System.err.println("ORDER SACVEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDdd" );

			}
		 catch (Exception e) {
			e.printStackTrace();
		}

	}

}
