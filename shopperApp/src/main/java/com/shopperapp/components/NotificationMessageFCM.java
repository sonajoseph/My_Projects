package com.shopperapp.components;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.shopperapp.mongo.models.NotificationsData;
import com.shopperapp.mongo.repo.NotificationsDataRepo;
 

@Component
public class NotificationMessageFCM {
	private static final Logger logger = LoggerFactory.getLogger(NotificationMessageFCM.class);

	
	@Autowired
	NotificationsDataRepo notificationDataRepo;
	
	 
	
	public String fcmMailNotificationMessage(String msg, String deviceId, String orderId) { 
		logger.info("At  fcmMailNotificationMessage....");
		
		JSONObject json = new JSONObject();
		 
		
		String url = "https://fcm.googleapis.com/fcm/send"; 
		 HttpHeaders httpHeaders = new HttpHeaders();
 
		httpHeaders.set("Authorization",
					"key=AAAAjLhLcZg:APA91bHfk0J8DL-hIoSoz6lgYFQM3kM1Asmx1-rXQB6LEzIZXGOrQbepb4D_i8BGSfYjdce0L7jCnlyRATQkOZjXnzUuyEImioBNiBfnpEOIrwcHNhXXawKeeawzjZT5215r5DSB3I-6");
//		 httpHeaders.set("Authorization",
//					"key= AAAAnp2FKRQ:APA91bEXFiznIWq7od4y8vhJZ_0p1jfREgLd96vaGBPBSQe1DIjyW52LXGUheujp2jxh_a9v72F_kJwlpGeIJsx1kzM8Cg9ZZkLvZtwZuLBnAG0MfBLADUVnR4Mq1EspzxI80Z2URQNO");

		httpHeaders.set("Content-Type", "application/json"); 
		   
		   
		RestTemplate restTemplate = new RestTemplate(); 
		
		   JSONObject notification = new JSONObject();
		   notification.put("body", msg);
		   notification.put("title", "Shopper App");
		   
		   JSONObject data = new JSONObject();
		   data.put("orderId", orderId);
		   
		   JSONObject body = new JSONObject();
		   body.put("notification", notification);
		   body.put("to", deviceId);
		   body.put("data", data);
		   
		   System.err.println("++++++++++++++++++++" + body);
		HttpEntity<String> httpEntity = new HttpEntity<String>(body.toString(), httpHeaders);
		String response = restTemplate.postForObject(url, httpEntity, String.class);
		 
		System.out.println(response);
		   
		NotificationsData notificationData =new NotificationsData();
//		notificationData.setEmailId(emailId);
		notificationData.setMessage(msg);
		notificationData.setOrderId(orderId);
		notificationData.setUserDeviceId(deviceId);
//		notificationData.setType(type);
//		notificationData.setVendorName(vendorName);
		
		notificationDataRepo.save(notificationData);
		
		return response;
	}
	
//	public static void main(String [] args) {
//		String msg = "Your have ordered at FLIPKART. Your order number is :  3ret76g-bqgi-dbucn" +
//				". You can now view your order details at ShopperApp.";
//		
//		String deviceId="eP9DkIS3xbA:APA91bH0pm0flCrVZHFlVBJtHtXjndC520fr9L3JlaMPEbX2kcz1hEWgwCHzC_eykTs_KLxGD6Y62aMawW2kmIZptv8vEef2ryB58Sf8s8zWbhyCnyaccpCFW0Ylpa1aFJzJEueT0xzW";
//		NotificationMessageFCM obj=new NotificationMessageFCM();
//		obj.fcmMailNotificationMessage(msg, deviceId,"8755949309006");
//	}

}
