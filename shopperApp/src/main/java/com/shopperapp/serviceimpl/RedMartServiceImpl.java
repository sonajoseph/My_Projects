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
import com.shopperapp.dto.OrderItem;
import com.shopperapp.dto.RawMessageResponseDTO;
import com.shopperapp.mongo.models.Order;
import com.shopperapp.mongo.models.OrderItemStatus;
import com.shopperapp.mongo.models.OrderItems;
import com.shopperapp.mongo.repo.OrderRepo;
import com.shopperapp.service.RedmartService;
/**
 * 
 * @author uvionics Tech. 
 * Developer 2 :sona Joseph 
 * Purpose :To take all details of Redmart.
 *               
 *
 */
@Service
public class RedMartServiceImpl implements RedmartService {
	private static final Logger logger = LoggerFactory.getLogger(RedMartServiceImpl.class);

	@Autowired
	NotificationMessageFCM notificationMessageFCM;

	@Autowired
	DateComponents dateComponent;

	@Autowired
	OrderRepo orderRepo;
	/**
	 * Reading the entire opentaste mail and saving the required datas from mail to database
	  */

	@Override
	public void readDataFromRedmart(String decodedMail, String toMailId, String subjectData, Date date, String fromMail,
			String deviceId, RawMessageResponseDTO rawMessageResponseDTO,
			ResponseEntity<MessageResponseDTO> forEntity) {
		//Through full data format ,entire mail is in under the payload.
		String mailDataBody = forEntity.getBody().getPayload().getParts().get(1).getBody().getData();
		logger.info("mailDataBody : " + mailDataBody);

		String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));
		// for viewing the mail in html format.Taking details from mail by examining table,row & coloumn.
		Document doc = Jsoup.parseBodyFragment(decodedMail1);
		logger.info("SUBJECT IS..........." + subjectData);

		String orderNo = "";
		if (subjectData != null) {
			orderNo = fetchOrderNumber(subjectData);
		}
		//trim will avoid space.
		orderNo = orderNo.trim();
		logger.info("...........*****************............." + orderNo);

		try {
			//multiple saving to database in each rum is avoided in each run.
			Order order = orderRepo.findOneByOrderNumAndVendorName(orderNo, "Redmart");
			if (order == null) {
				order = new Order();
				logger.info("Reading RedmartMails..............................");

				List<OrderItems> orderItemsList = new ArrayList<>();
                 //checking order mails.
				if (subjectData.contains("New Order")) {
					List<OrderItem> list = new ArrayList<>();
					// Table Reading

					String deliveryAddress = "";
					Element addressData = doc.select("h3").get(2);

					deliveryAddress = addressData.text();

					// deliverydate
					Element addressData1 = doc.select("h3").get(1);
					String deliveryDate = addressData1.text().split(",")[1].split(",")[0];
					logger.info("FIND IT........................." + deliveryDate);

					DateComponents dates = new DateComponents();
					Date deliveryDate1 = dates.getStringToDate4(deliveryDate);
					logger.info("DATE IS..........." + deliveryDate1);
					Element table = doc.select("table").get(0);
					logger.info("table ::::\n" + table + "\n\n");
					// Element div=doc.select("div)
					String grandTotal = "";
					String deliveryCharge = "";
					Elements rows = table.select("tr");

					int i = 1;

					for (; i < rows.size(); i++) { // first row is the coloumn  names so skip it.

						if (rows.get(i).text().contains("Subtotal")) {
							break;
						}

						OrderItems orderItem = new OrderItems();
						orderItem.setOrderId(orderNo);
						orderItem.setCreatedAt(new Date());
						Element row = rows.get(i);

						Elements cols = row.select("td");

						if (cols.size() > 2) {
							orderItem = new OrderItems();

							// orderItem.setItem(cols.get(0).text().replaceAll("<= /td>",
							// "").replaceAll("=", "").replaceAll("=E2=80=93", "-"));
							// orderItems.setItem(cols.get(0).text());
							// logger.info("REDMART ITEMS
							// ARE:::::::::::::::::::::::"+cols.get(0).text().replaceAll("<= /td>",
							// "").replaceAll("=", ""));

							// orderItem.setQty(cols.get(1).text().replaceAll("[^\\d.]", ""));
							// orderItems.setQty(cols.get(1).text());
							// logger.info("THE QUANTIES OF ITEMS
							// ARE:::::::::::::::::::::"+cols.get(1).text().replaceAll("[^\\d.]", ""));

							// orderItem.setSubTotal("$ "+cols.get(2).ownText().replaceAll("[^\\d.]", ""));
							// orderItems.setSubTotals("$"+cols.get(2).text());
							// logger.info("THE SUBTOTALS
							// ARE::::::::::::::::::::::;"+cols.get(2).ownText().replaceAll("[^\\d.]", ""));

						}

						orderItem.setProductName(cols.get(0).text());
						orderItem.setQty(Integer.parseInt(cols.get(1).text()));
						orderItem.setPrice("$ " + cols.get(2).text());

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
					Element subTotalRow = table.select("tr").get(i);
					if (subTotalRow.text().contains("Subtotal")) {
						Elements subTotalRowTableData = subTotalRow.select("table").get(0).select("tr");
						
						String itemsTotalsCost = subTotalRowTableData.get(0).select("td").get(1).text();
						order.setItemsTotalsCost(itemsTotalsCost);

						// LiveUp Credits'
						if (subTotalRowTableData.get(1).select("td").get(0).text().contains("Delivery Fee")) {
							String itemsTotalsDeliveyCharge = subTotalRowTableData.get(1).select("td").get(1).text();

							order.setItemsTotalDeliveryCharge(itemsTotalsDeliveyCharge);
						} else if (subTotalRowTableData.get(2).select("td").get(0).text().contains("Delivery Fee")) {
							String itemsTotalsDeliveyCharge = subTotalRowTableData.get(2).select("td").get(1).text();

							order.setItemsTotalDeliveryCharge(itemsTotalsDeliveyCharge);
						}

						// Delivery Fee

						i = i + subTotalRowTableData.size();

					}

					Element grandTotalRow = table.select("tr").get(i + 1);

					logger.info("grandTotalRow ::::::: \n" + grandTotalRow + "\n\n");
					if (grandTotalRow.text().contains("Grand")) {

						String grandTotalPrice = grandTotalRow.select("td").get(0).select("span").get(0).text();

						logger.info("grandTotalPrice :::: " + grandTotalPrice);
						order.setGrandTotal(grandTotalPrice);
					}

					// faq

					Element div = doc.select("div").get(8).select("a").get(0);
					logger.info("div is................" + div.attr("href"));

					Order orderDataDB = orderRepo.findOneByOrderNumAndVendorName(orderNo, "Redmart");
					logger.info("Order Num : " + orderNo + "\t mailDataDB :: " + orderDataDB);
					if (orderDataDB == null) {
						logger.info("REACHED...................................");
                        //saving to database.
						order.setOrderNum(orderNo);
						order.setVendorName("Redmart");
						order.setDeliveryAddress(deliveryAddress);
						order.setCreatedDate(new Date());
						order.setUserEmailId(toMailId);
						order.setFromMailId(fromMail);

						order.setOrderItems(orderItemsList);

						orderRepo.save(order);
						//notification message is sent.
						
						String msg = "You have ordered at Redmart. Your order number is : " + order.getOrderNum()
								+ " . You can now view your order details at ShopperApp.";
						notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, order.getOrderNum());
					}

				}

				logger.info("ORDER SACVE : " + orderNo);
			}

		} catch (Exception e) {

		}

	}
/**
 * 
 * @param subject
 * @return orderno.
 */
	private String fetchOrderNumber(String subject) {
		String orderId = "";
		if (subject.contains("#")) {

			String[] split = subject.split("#");
			orderId = split[1];
		}
		return orderId;
	}

}
