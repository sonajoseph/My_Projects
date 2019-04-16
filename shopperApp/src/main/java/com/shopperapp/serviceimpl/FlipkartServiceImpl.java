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
import com.shopperapp.service.FlipkartService;

@Service
public class FlipkartServiceImpl implements FlipkartService {
	private static final Logger logger = LoggerFactory.getLogger(FlipkartServiceImpl.class);

	@Autowired
	NotificationMessageFCM notificationMessageFCM;

	@Autowired
	MailDataRepo mailDataRepo;

	@Autowired
	DateComponents dateComponent;

	@Autowired
	OrderRepo orderRepo;

	// Reading the entire flipkart mail and saving the required datas of mail to database.
	@Override
	public void readDataFromFlipkart(String userMailId, String subjectData, Date date, String sellerMailId,
			String deviceId, RawMessageResponseDTO rawMessageResponseDTO,
			ResponseEntity<MessageResponseDTO> forEntity) {
		logger.info("Now at readDataFromFlipkart . . . ");

		Date crDate = new Date();

		String mailDataBody = forEntity.getBody().getPayload().getParts().get(1).getBody().getData();
		if (mailDataBody == null && subjectData.contains("has been delivered")) {
			mailDataBody = forEntity.getBody().getPayload().getParts().get(0).getParts().get(1).getBody().getData();

		}

		System.out.println("Subject Data ------------> \n" + subjectData + "\n");

		String decodedMail1 = StringUtils.newStringUtf8(Base64.decodeBase64(mailDataBody));

		try {
			List<OrderItems> orderList = new ArrayList<>();
			if (!subjectData.contains("Return") && !subjectData.contains("return") && !subjectData.contains("refund")) {
				// for viewing the mail in html format.
				Document doc = Jsoup.parseBodyFragment(decodedMail1);
				Element table = doc.select("table").get(0);
				Elements rows = table.select("tr");
				String orderStatus = "";
				String orderID = "";

				try {
					Element rowTable1 = rows.get(0).select("table").get(0).select("tr").get(0).select("table").get(0)
							.select("tr").get(0).select("td").get(1);
					orderStatus = rowTable1.text();

				} catch (IndexOutOfBoundsException indEx) {
					try {
						Element rowTable1 = rows.get(0).select("table").get(0).select("tr").get(0).select("td").get(1);
						orderStatus = rowTable1.text();
					} catch (Exception e) {

					}
				}
				try {
					Element table6 = doc.select("table").get(6);

					if ((orderStatus.contains("Item Cancelled")) || (orderStatus.contains("Items Cancelled"))) {
						table6 = doc.select("table").get(5);
					}

					System.err.println("table6 :::: \n  " + table6.toString() + "\n\n\n");
					Element orderDetailsElement = table6.select("tr").get(0).select("td").get(0);
					Elements table6PList = orderDetailsElement.select("p");
					try {
						orderID = table6PList.get(1).select("span").get(0).text();
						// orderID=orderID.replaceAll("[^a-zA-Z0-9]+", "");
						System.err.println("Order ID aaaa :: " + orderID);
					} catch (IndexOutOfBoundsException es) {
						orderID = table6PList.text();
						orderID = orderID.replaceAll("[^a-zA-Z0-9]+", "");
						orderID = orderID.split("OrderID")[1];
						// System.err.println("Order ID ODDDD:: "+orderID);
					}
				} catch (Exception e) {
					System.err.println("Order Id Exception...");
					e.printStackTrace();

				}
				logger.info("ORDER IDDDDD ::: " + orderID);

				if (orderStatus.contains("Order") && orderStatus.contains("Placed")) {

					// Delivery by
					try {
						Element table7 = doc.select("table").get(7).select("tr").get(0).select("td").get(0).select("p")
								.get(0).select("span").get(1);

						String deliveyDate[] = table7.text().split(" ");
						System.err.println("deliveyDate ::: " + deliveyDate[2] + " " + deliveyDate[3] + " "
								+ deliveyDate[4].replaceAll("[^\\d.]", ""));
						String arriveOn = deliveyDate[2] + " " + deliveyDate[3] + " "
								+ deliveyDate[4].replaceAll("[^\\d.]", "");
						System.err.println("arriveOn ::::" + arriveOn);

					} catch (Exception ed) {
						System.err.println("ARRIVES ON ERRROR");
						ed.printStackTrace();

					}

					String deliveryAddress = "";
					try {
						Element table7 = doc.select("table").get(8);

						Element table7List = table7.select("tr").get(0).select("td").get(0).select("div").get(0)
								.select("p").get(1);
						System.err.println("Delivey Address ::: 1374=== " + table7List.text());
						deliveryAddress = table7List.text();
						deliveryAddress = deliveryAddress.replaceAll("\\<.*?>", "").replaceAll("=20", "");
						deliveryAddress = deliveryAddress.replaceAll("\\<.*?>", "").replaceAll("= ", "");
						System.err.println("=====================>>>>> " + deliveryAddress);
					} catch (IndexOutOfBoundsException e1) {
						e1.printStackTrace();
					}
					deliveryAddress = deliveryAddress.replaceAll("<= /span>", "");

					try {
						int dataIndex = 11;
						Element table11 = doc.select("table").get(dataIndex);
						Elements table13s = table11.select("tr");
						System.err.println("table11 1362 ========\n " + table11.toString() + "\n");
						System.err.println("here................................." + table13s.text());
						System.err.println(table13s.size() + "--Products Details 11 : \n" + table11.toString());
						if (table13s.size() <= 1) {
							Element table13 = doc.select("table").get(13);
							table13s = table13.select("tr");
							System.err.println("table10 ========\n " + table13.toString() + "\n");
						}

						if (table13s.text().contains("coin") || table13s.text().contains("Free Delivery")
								|| table13s.text().contains("Plus Members")) {

							System.err.println("coin Exist  11");
							dataIndex++;

							table11 = doc.select("table").get(dataIndex);
							table13s = table11.select("tr");
							System.err.println("THE table13s is..................." + table13s + "\n\n");
							System.err.println(table13s.size() + "--Products Details 11 : \n" + table11.toString());
							if (table13s.size() <= 1) {
								Element table13 = doc.select("table").get(13);
								table13s = table13.select("tr");
								System.err.println("table10 ========\n " + table13.toString() + "\n");

							}
							// Free Delivery

							if (table13s.text().contains("coin") || table13s.text().contains("Free Delivery")
									|| table13s.text().contains("Plus Members")) {

								System.err.println("coin Exist  22");
								dataIndex++;

								table11 = doc.select("table").get(dataIndex);
								table13s = table11.select("tr");
								System.err.println(table13s.size() + "--Products Details 11 : \n" + table11.toString());
								if (table13s.size() <= 1) {
									Element table13 = doc.select("table").get(13);
									table13s = table13.select("tr");
									System.err.println("table10 ========\n " + table13.toString() + "\n");
								}
								// Plus Members
								if (table13s.text().contains("coin") || table13s.text().contains("Free Delivery")
										|| table13s.text().contains("Plus Members")) {

									System.err.println("coin Exist  33");
									dataIndex++;

									table11 = doc.select("table").get(dataIndex);
									table13s = table11.select("tr");
									System.err.println(
											table13s.size() + "--Products Details 11 : \n" + table11.toString());
									if (table13s.size() <= 1) {
										Element table13 = doc.select("table").get(13);
										table13s = table13.select("tr");
										System.err.println("table10 ========\n " + table13.toString() + "\n");
									}
								}
							}
						}
						System.err.println("IS Coin 0000000000000000000000000000000 : " + orderID);

						System.err.println("1222222222222222222222222222222222222222222 else");
						Element table13 = doc.select("table").get(dataIndex + 1);
						// Element table13 = doc.select("table").get(14);
						table13s = table13.select("table");
						System.err.println(table13s.size() + "--Products Details 13 : \n" + table13.toString());

						System.err.println("122222222222222222222222222222222222222222");
						Order order = new Order();
						order.setOrderNum(orderID);
						order.setCreatedDate(crDate);
						order.setFromMailId(sellerMailId);
						order.setUserEmailId(userMailId);
						order.setDeliveryAddress(deliveryAddress);

						order.setVendorName("Flipkart");

						try {
							for (int i = 1; i < table13s.size(); i++) {

								OrderItems orderItem = new OrderItems();

								Element table13T = table13s.get(i);
								// System.err.println(i + "::: iiii^^^^^^^^^^^^^^^^^^^^^^^" + orderID
								// + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n " + table13T.toString());
								try {
									Elements productDatas = table13T.select("td").get(1).select("p");
									Element productDetail = productDatas.get(0);
									String itemName = productDetail.select("a").get(0).text().trim();
									System.err.println("THE ITEM NAME IS............." + itemName);
									orderItem.setProductName(itemName);

									String itemPrice = productDetail.select("span").get(0).text();
									orderItem.setPrice(itemPrice);
									String deliveyDate = "";
									String deliveryCharges = "";
									String itemQty = "";
									String sellerName = "";

									System.err.println("productDatas.size() :: " + productDatas.size());
									for (int p = 1; p < productDatas.size(); p++) {
										Element deliveryDetail = productDatas.get(p);
										System.err.println("deliveryDetail : " + deliveryDetail.text());
										if (deliveryDetail.text().contains("Delivery")
												&& deliveryDetail.text().contains("by")) {
											deliveyDate = deliveryDetail.select("span").get(0).text()
													.replaceAll("by", "").trim();
											String deliveyDate1[] = deliveyDate.split(",");
											deliveyDate = deliveyDate1[1] + "," + deliveyDate1[2];

											// System.err.println("deliveyDate 1424 : "+deliveyDate.toString());
											// System.err.println("arriveOn ::::" + deliveyDate);
											try {
												orderItem.setDeliveryDate(
														dateComponent.getStringToDate(deliveyDate.trim()));
											} catch (Exception date1) {

											}
										} else if (deliveryDetail.text().contains("Seller")
												&& deliveryDetail.text().contains("Delivery charges")) {
											String sellerDetails = deliveryDetail.text();
											sellerDetails = sellerDetails.split("Seller")[1];

											sellerName = sellerDetails.split("Delivery charges")[0].replaceAll("\\:",
													"");
											orderItem.setSellerName(sellerName);

											deliveryCharges = sellerDetails.split("Delivery charges")[1];
											orderItem.setDeliveryCharge(deliveryCharges.trim());
										} else if (deliveryDetail.text().contains("Qty")) {
											System.err.println("deliveryDetail.text() ::: " + deliveryDetail.text());
											itemQty = deliveryDetail.text().split(" ")[1];
											itemQty = itemQty.replaceAll("[^0-9]+", "");
											logger.info("itemQty>>>>>>" + itemQty);// output 1
											logger.info("Integer.parseInt(itemQty.trim())>>>>>>"
													+ Integer.parseInt(itemQty.trim()));// output 1
											orderItem.setQty(Integer.parseInt(itemQty.trim()));

											if (deliveryDetail.text().contains("Delivery discount")) {
												System.err.println("Delivery discount  ::: " + deliveryDetail.text());
												String discountDeliveryCharge = deliveryDetail.text()
														.split("Delivery discount")[1];
												orderItem.setDeliveryChargeDiscount(discountDeliveryCharge.trim());
											}

										}

										List<OrderItemStatus> status = new ArrayList<OrderItemStatus>();
										OrderItemStatus orderItemStatus = new OrderItemStatus();
										orderItemStatus.setCrDate(crDate);
										orderItemStatus.setCurrentStatus("Ordered");
										orderItemStatus.setCurrentStatusCode(1);
										orderItemStatus.setOrderNumber(orderID);

										status.add(orderItemStatus);

										orderItem.setCreatedAt(crDate);
										orderItem.setStatus(status);
									}
									orderList.add(orderItem);
								} catch (Exception e) {
									System.err.println("EXPEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
									e.printStackTrace();
									System.err.println("0000000000000000000000000000000");
								}
							}
						} catch (Exception e1) {
							System.err.println("1562 ERROR");
						}

						System.err.println("orderList :::::: 150000 " + orderList);
						order.setOrderItems(orderList);

						int tableNo = table13s.size() + 15;
						Element table16 = doc.select("table").get(tableNo);
						String amoutToBePaid = "";

						// order.setGstTotal(gstTotal);

						try {
							// System.err.println("table16 ::"+orderID+":: " + table16.text());
							// System.err.println("table16 :::: " + table16.toString());
							String totalPriceDetails = table16.text();
							String totalPrices[] = totalPriceDetails.split("₨.");
							for (int t = 0; t < totalPrices.length; t++) {
								if (totalPrices[t].contains("Item(s) total")) {
									String itemsTotalsCost = totalPrices[t + 1].replaceAll("[^0-9]+", "");
									// System.out.println("Item(s) total :: ₨. :"+ itemsTotalsCost);
									order.setItemsTotalsCostCal(Double.parseDouble(itemsTotalsCost));

									order.setItemsTotalsCost("₨." + itemsTotalsCost);
								} else if (totalPrices[t].contains("Delivery charges")) {
									String itemsTotalDeliveryCharge = totalPrices[t + 1].replaceAll("[^0-9]+", "");
									// System.out.println("Delivery charges :: ₨. :"+itemsTotalDeliveryCharge);
									order.setItemsTotalDeliveryCharge("₨." + itemsTotalDeliveryCharge);
									order.setItemsTotalDeliveryChargeCal(Double.parseDouble(itemsTotalDeliveryCharge));

								} else if (totalPrices[t].contains("Amount Payable on Delivery")
										|| (totalPrices[t].contains("Amount Paid"))) {
									String grandTotal = totalPrices[t + 1].replaceAll("[^0-9]+", "");
									// System.out.println("Amount Payable on Delivery / Amount Paid :: ₨.
									// :"+grandTotal);
									order.setGrandTotal("₨." + grandTotal);
									order.setGrandTotalCal(Double.parseDouble(grandTotal));

									order.setCurrencySymbol("₨.");
								}
							}
							// System.err.println("data ::::1649: " + data.toString());
							System.err.println("order :: ::: " + order);
							Order oderDB = orderRepo.findOneByOrderNumAndVendorName(orderID, "Flipkart");

							if (oderDB == null) {
								orderRepo.save(order);
								String msg = "You have ordered at FLIPKART. Your order number is : "
										+ order.getOrderNum() + " . You can now view your order details at ShopperApp.";
								notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, order.getOrderNum());
							}
						} catch (Exception e1687) {
							System.err.println("E1687");
						}
						System.err.println("orderList SIZZZZZ : " + orderList.size());

					} catch (IndexOutOfBoundsException e1) {
						System.err.println("E1111111111111111111111111111 ." + e1.getMessage());
					}
				} else if (orderStatus.contains("Item") && orderStatus.contains("Shipped")) {
					System.err.println("Item Shipped");
					Order order = orderRepo.findOneByOrderNumAndVendorName(orderID, "Flipkart");

					Element table14 = doc.select("table").get(14);

					System.err.println("table14 :::: \n\n\n" + table14.toString());

					if (order != null) {

						String productName = table14.select("tr").get(0).select("td").get(0).select("p").get(0).text()
								.trim();

						System.err.println("productName ::: " + productName);
						try {
							System.err.println("order : " + order);
							for (int i = 0; i < order.getOrderItems().size(); i++) {
								if (order.getOrderItems().get(i).getProductName().equalsIgnoreCase(productName)) {
									List<OrderItemStatus> statusList = order.getOrderItems().get(i).getStatus();
									Boolean packedData = false;
									for (int j = 0; j < statusList.size(); j++) {
										if (statusList.get(j).getCurrentStatus().equalsIgnoreCase("Shipped")) {
											packedData = true;
										}
									}
									if (!packedData) {
										OrderItemStatus orderItemsStatus = new OrderItemStatus();
										orderItemsStatus.setCrDate(new Date());
										orderItemsStatus.setCurrentStatusCode(3);
										orderItemsStatus.setCurrentStatus("Shipped");
										orderItemsStatus.setOrderNumber(orderID);
										statusList.add(orderItemsStatus);
										order.getOrderItems().get(i).setStatus(statusList);
										orderRepo.save(order);

										String msg = "Your order at FLIPKART, Order Id : " + orderID
												+ " status have changed to Shipped now."
												+ " You can now view your order details at ShopperApp.";

										notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, orderID);

									}
								}
							}
						} catch (Exception e2) {
							e2.getMessage();
							e2.printStackTrace();
						}
					}
				} else if (orderStatus.contains("Item") && orderStatus.contains("Delivered")) {
					System.err.println("Item Delivered");

					Elements productTable16 = doc.select("table").get(16).select("tr");

					for (int j = 0; j < productTable16.size(); j++) {
						String productName = productTable16.get(j).select("td").get(1).select("p").get(0).text();
						System.err.println("=========== productName  :::: " + productName);

						try {
							Order order = orderRepo.findOneByOrderNumAndVendorName(orderID, "Flipkart");
							if (order != null) {
								for (int i = 0; i < order.getOrderItems().size(); i++) {
									if (order.getOrderItems().get(i).getProductName().equalsIgnoreCase(productName)
											|| productName.contains(order.getOrderItems().get(i).getProductName())) {
										List<OrderItemStatus> statusList = order.getOrderItems().get(i).getStatus();
										Boolean packedData = false;
										for (int k = 0; k < statusList.size(); k++) {
											if (statusList.get(j).getCurrentStatus().equalsIgnoreCase("Delivered")) {
												packedData = true;
											}
										}
										if (!packedData) {
											OrderItemStatus orderItemsStatus = new OrderItemStatus();
											orderItemsStatus.setCrDate(new Date());
											orderItemsStatus.setCurrentStatusCode(4);
											orderItemsStatus.setCurrentStatus("Delivered");
											orderItemsStatus.setOrderNumber(orderID);
											statusList.add(orderItemsStatus);
											order.getOrderItems().get(i).setStatus(statusList);
											orderRepo.save(order);
										}
									}
								}
							}
						} catch (Exception e2) {
							e2.getMessage();
							e2.printStackTrace();
						}
					}

				} else if (orderStatus.contains("Replacement") && orderStatus.contains("Delivered")) {
					System.err.println("Replacement Delivered");
				} else if (orderStatus.contains("Return") && orderStatus.contains("Accepted")) {
					System.err.println("Return Accepted");
				}

				else if (orderStatus.contains("Refund") && orderStatus.contains("Approved")) {
					System.err.println("Refund Approved");
				} else if (orderStatus.contains("Cancelled")) {

					/*
					 * if (rawMessageResponseDTO != null) { String snippet =
					 * rawMessageResponseDTO.getSnippet(); System.err.println("-----Start");
					 * System.out.println(snippet); String orderNumber =
					 * snippet.replaceAll("[^0-9]", ""); List<MailData> findByOrderNumber =
					 * mailDataRepo .findByOrderNumberAndCurrentStatus(orderID, "Ordered");
					 */

					System.err.println("Cancelled findByOrderNumber ::: " + orderID);

					Element dataTable = doc.select("table").get(6);

					System.out.println("dataTable....2\n" + dataTable.toString());

					Elements itemsTable = doc.select("table").get(9).select("tbody").get(0).select("table");

					if ((dataTable.text().contains("qty") || dataTable.text().contains("Qty"))
							&& dataTable.text().contains("Seller")) {
						itemsTable = dataTable.select("tbody").get(0).select("table");
					}

					System.err.println("itemsTable.size() ::::::: " + itemsTable.size());

					for (int jj = 1; jj < itemsTable.size(); jj = jj + 2) {

						System.out.println("itemsTable.get(jj)\n" + itemsTable.get(jj));
						String productName = itemsTable.get(jj).select("tr").get(0).select("td").get(0).select("p")
								.get(0).text().split("₨.")[0].trim();

						String qty = itemsTable.get(jj).select("tr").get(0).select("td").get(0).select("p").get(2)
								.text().split("Qty:")[1].trim();

						String itemPrice = itemsTable.get(jj).select("tr").get(0).select("td").get(0).select("p").get(0)
								.text().split("₨.")[1].trim();

						logger.info("\n productName : " + productName + "\n\n");
						logger.info("\n Qty : " + qty + "\n\n");
						logger.info("\n itemPrice : " + itemPrice + "\n\n");

						Order order = orderRepo.findOneByOrderNumAndVendorName(orderID, "Flipkart");

						if (order != null) {
							System.out.println("order----" + order);

							for (int i = 0; i < order.getOrderItems().size(); i++) {
								System.err.println("order.getOrderItems().get(i).getProductName() : "
										+ order.getOrderItems().get(i).getProductName());

								if (order.getOrderItems().get(i).getProductName().equalsIgnoreCase(productName)) {

									List<OrderItemStatus> statusList = order.getOrderItems().get(i).getStatus();
									Boolean packedData = false;

									for (int j = 0; j < statusList.size(); j++) {
										if (statusList.get(j).getCurrentStatus().equalsIgnoreCase("Cancelled")) {
											packedData = true;
										}

									}
									// if packedData!=true.
									if (!packedData) {

										if (order.getOrderItems().get(i).getQty() == Integer.parseInt(qty)) {
											OrderItemStatus orderItemsStatus = new OrderItemStatus();
											orderItemsStatus.setCrDate(new Date());
											orderItemsStatus.setCurrentStatusCode(5);
											orderItemsStatus.setCurrentStatus("Cancelled");
											orderItemsStatus.setOrderNumber(orderID);
											statusList.add(orderItemsStatus);
											order.getOrderItems().get(i).setStatus(statusList);
											orderRepo.save(order);
										} else {
											order.getOrderItems().get(i).setQty(Integer.parseInt(qty));
											order.getOrderItems().get(i).setPrice(itemPrice);

											double grandTotal = order.getGrandTotalCal();
											grandTotal = grandTotal - Double.parseDouble(itemPrice);

											order.setGrandTotal(order.getCurrencySymbol() + grandTotal);
											order.setGrandTotalCal(grandTotal);

											order.setItemsTotalsCost(order.getCurrencySymbol() + grandTotal);
											order.setItemsTotalsCostCal(grandTotal);

											OrderItemStatus orderItemsStatus = new OrderItemStatus();

											orderItemsStatus.setCrDate(new Date());
											orderItemsStatus.setCurrentStatusCode(6);
											orderItemsStatus.setCurrentStatus("Updated");
											orderItemsStatus.setOrderNumber(orderID);
											statusList.add(orderItemsStatus);
											order.getOrderItems().get(i).setStatus(statusList);
											orderRepo.save(order);

										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {

		}

	}

}