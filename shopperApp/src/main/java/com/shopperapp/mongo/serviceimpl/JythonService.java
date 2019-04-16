package com.shopperapp.mongo.serviceimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.python.core.PyClass;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopperapp.components.DateComponents;
import com.shopperapp.components.NotificationMessageFCM;
import com.shopperapp.dto.Headers;
import com.shopperapp.dto.JythonResponseOfMailsDTO;
import com.shopperapp.dto.MessageResponseDTO;
import com.shopperapp.dto.OrderItem;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.repo.MailDataRepo;

@Component
public class JythonService {

	PythonInterpreter pythonInterpreter;
	
    @Autowired
    MailDataRepo mailDataRepo;
    
    @Autowired
    DateComponents dateComponents;
    
    @Autowired
    NotificationMessageFCM notificationMessageFCM;
    
    public JythonService() {
        pythonInterpreter = new PythonInterpreter();
    }

    public void execScriptAsInputStream (InputStream inputStream) {  
    	PythonInterpreter pythonInterpreter = new PythonInterpreter();
		pythonInterpreter.execfile(inputStream);
//		pythonInterpreter.set("variable", 5);
		PyObject eval = pythonInterpreter.eval("amazonMailRead()");
		System.err.println(eval.toString());
    }
    
    public void execAmazonMailData(String userId,  String  id,  String  tokenValue, ResponseEntity<MessageResponseDTO> forEntity, Date date, String deviceId) {
    	System.err.println("REACHED");
    	try {
    		
    	
        InputStream initInputStream = this.getClass().getClassLoader().getResourceAsStream("python/gmail_amazon_18_01_19.py");
        String[] arguments = {"myscript.py", "arg1", "arg2", "arg3"};

    	PythonInterpreter pythonInterpreter = new PythonInterpreter();
//    	pythonInterpreter.initialize(System.getProperties(), System.getProperties(), forEntity);
    	
		pythonInterpreter.execfile(initInputStream);
//		pythonInterpreter.set("variable", 5);
		//def amazonMails(userId, id, tokenValue):

		String funcName = "msg";
		System.err.println("function name is");
		
//		PyObject eval = pythonInterpreter.eval("amazonMailRead("+forEntity+")");
		 PyObject someFunc = pythonInterpreter.get(funcName); //functionName
		 
		 
		 List<Headers> collect = forEntity.getBody().getPayload().getHeaders().stream()
					.filter(dats -> dats.getName().equalsIgnoreCase("subject")).collect(Collectors.toList());
			Headers subject = collect.get(0);
			String subjectValue = subject.getValue();
			
			List<Headers> fromMail = forEntity.getBody().getPayload().getHeaders().stream()
					.filter(dats -> dats.getName().equalsIgnoreCase("From")).collect(Collectors.toList());
			Headers from = fromMail.get(0);
			System.err.println(from.getValue());
			
			List<Headers> toMail = forEntity.getBody().getPayload().getHeaders().stream()
					.filter(dats -> dats.getName().equalsIgnoreCase("To")).collect(Collectors.toList());
			Headers to = toMail.get(0);
			System.err.println(to.getValue());
			
			
			List<Headers> dateData= forEntity.getBody().getPayload().getHeaders().stream()
					.filter(dats -> dats.getName().equalsIgnoreCase("Date")).collect(Collectors.toList());
			Headers dateH = dateData.get(0); 
			
			String mailDataBody= forEntity.getBody().getPayload().getParts().get(0).getBody().getData();  
			System.err.println(mailDataBody);  

	     try {
	    	 PyObject returnData = someFunc.__call__(new PyString(dateH.getValue()), new PyString(subjectValue) , new PyString(from.getValue()), new PyString(mailDataBody) ); 
	    	 String mailDataS =(String) returnData.__tojava__(String.class);
	    	 System.err.println("mailData :::::: "+mailDataS);
	    	 if(!mailDataS.contains("No Data")) {
	    		 List<JythonResponseOfMailsDTO>  mailDataList = new ObjectMapper().readValue(mailDataS,  new TypeReference<List<JythonResponseOfMailsDTO>>(){});
	    		 System.err.println("mailDataList  :"+mailDataList);
	    		 
	    		 
		    	 for(int i=0;i< mailDataList.size();i++) {
		    		 JythonResponseOfMailsDTO obj= mailDataList.get(i);
		    		 if(obj.getCurrentStatus().equalsIgnoreCase("Ordered")) {
		    			 String arrivesOn=obj.getArrivesOn();
		    			 if(arrivesOn.contains("-")) {
		    				 arrivesOn=arrivesOn.split("-")[1];
		    			 }
		    			 arrivesOn=arrivesOn.split(",")[1];
		    			 
		    			 Date deliveryDate=dateComponents.getStringToDate5(arrivesOn+" "+(new Date().getYear()+1900));
		    			 
		    			 System.err.println("deliveryDate ::::: "+deliveryDate);
		    			 
		    			 System.err.println("OrderedOrderedOrderedOrdered :: "+obj.getOrderNumber());
		    			 List<MailData> mailDataDb=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Ordered");
			    		 if(mailDataDb == null || mailDataDb.isEmpty() ) {
			    			 MailData mailData = new MailData();
			    			 mailData.setDate(date);
			    			 mailData.setCurrentStatus(obj.getCurrentStatus());
			    			 mailData.setCurrentStatusCode(1);
			    			 mailData.setVendor("Amazon"); 
			    			 if(deliveryDate != null) {
			    				 mailData.setArrivesOn(deliveryDate);
			    			 }
			    			 mailData.setDeliveryAddress(obj.getDeliveryAddress());
			    			 mailData.setGrandTotal(obj.getGrandTotal());
			    			 mailData.setFromMailId(obj.getFromMailId());
			    			 mailData.setToMaild(to.getValue());
			    			 List<OrderItem> orderList=new ArrayList();
			    			 
			    			 for(int j=0;j<obj.getOrderItemsList().size();j++) {
			    				 OrderItem orderItem= obj.getOrderItemsList().get(j);
			    				 orderItem.setItem(orderItem.getItem().trim());
			    				 
			    				 orderList.add(orderItem);
			    			 }
			    			 
			    			 mailData.setOrderItemsList(orderList);
			    			 mailData.setOrderNumber(obj.getOrderNumber());
			    			 mailDataRepo.save(mailData);
			    			 String msg = "Your order at Amazon, Order Id : " + obj.getOrderNumber() + " had placed successfully."
										+ " You can now view your order details at ShopperApp.";
								notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId,obj.getOrderNumber());
			    		 }
		    		 }else if(obj.getCurrentStatus().equalsIgnoreCase("Shipped")) {
		    			 List<MailData> mailDataDb=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Ordered");
			    		 if(mailDataDb != null  ) {
			    			 List<MailData> mailDataDbShipped=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Shipped");
			    			 if(mailDataDbShipped == null || mailDataDbShipped.isEmpty() ) {
				    			 MailData mailDataDB = mailDataDb.get(0);
				    			 MailData mailData =new MailData();
				    			 mailData.setDate(date);
				    			 mailData.setCurrentStatus(obj.getCurrentStatus());
				    			 mailData.setCurrentStatusCode(2);
				    			 mailData.setVendor("Amazon");
				    			 mailData.setDeliveryAddress(mailDataDB.getDeliveryAddress());
				    			 mailData.setGrandTotal(mailDataDB.getGrandTotal());
				    			 mailData.setFromMailId(mailDataDB.getFromMailId());
				    			 mailData.setArrivesOn(mailDataDB.getArrivesOn());
				    			 mailData.setToMaild(to.getValue());
				    			 mailData.setOrderItemsList(mailDataDB.getOrderItemsList());
				    			 mailData.setOrderNumber(mailDataDB.getOrderNumber());
				    			 mailDataRepo.save(mailData);
				    			 String msg="Your order at Amazon, Order Id : "+obj.getOrderNumber()+" status have changed to Shipped now."
											+ " You can now view your order details at ShopperApp.";
									notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, obj.getOrderNumber());
			    			 }
			    		 }
			    		 
		    		 }else if(obj.getCurrentStatus().equalsIgnoreCase("Delivered")) {
		    			 List<MailData> mailDataDb=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Ordered");
		    			 if(mailDataDb != null  ) {
			    			 List<MailData> mailDataDbDelivered=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Delivered");
			    			 if(mailDataDbDelivered == null || mailDataDbDelivered.isEmpty() ) {
				    			 MailData mailDataDB = mailDataDb.get(0);
				    			 MailData mailData =new MailData();
				    			 mailData.setDate(date);
				    			 mailData.setCurrentStatus(obj.getCurrentStatus());
				    			 mailData.setCurrentStatusCode(3);
				    			 mailData.setVendor("Amazon");
				    			 mailData.setArrivesOn(mailDataDB.getArrivesOn());
				    			 mailData.setDeliveryAddress(mailDataDB.getDeliveryAddress());
				    			 mailData.setGrandTotal(mailDataDB.getGrandTotal());
				    			 mailData.setFromMailId(mailDataDB.getFromMailId());
				    			 mailData.setToMaild(to.getValue());
				    			 mailData.setOrderItemsList(mailDataDB.getOrderItemsList());
				    			 mailData.setOrderNumber(mailDataDB.getOrderNumber());
				    			 mailDataRepo.save(mailData);
				    			 String msg="Your order at Amazon, Order Id : "+obj.getOrderNumber()+" status have changed to Delivered now."
											+ " You can now view your order details at ShopperApp.";
								notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, obj.getOrderNumber());
			    			 }
		    			 }
		    		 }
		    		 else if(obj.getCurrentStatus().equalsIgnoreCase("Cancelled")) {
		    			 
		    			 System.err.println("CancelledCancelledCancelled :: "+obj.getOrderNumber());
		    			 List<MailData> mailDataDb=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Ordered");
		    			 if(mailDataDb != null  ) {
			    			 List<MailData> mailDataDbDelivered=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Cancelled");
			    			 if(mailDataDbDelivered == null || mailDataDbDelivered.isEmpty() ) {
				    			 MailData mailDataDB = mailDataDb.get(0);
				    			 MailData mailData =new MailData();
				    			 mailData.setDate(date);
				    			 mailData.setCurrentStatus(obj.getCurrentStatus());
				    			 mailData.setCurrentStatusCode(5);
				    			 mailData.setVendor("Amazon");
				    			 mailData.setArrivesOn(mailDataDB.getArrivesOn());
				    			 mailData.setDeliveryAddress(mailDataDB.getDeliveryAddress());
				    			 mailData.setGrandTotal(mailDataDB.getGrandTotal());
				    			 mailData.setFromMailId(mailDataDB.getFromMailId());
				    			 mailData.setToMaild(to.getValue());
				    			 mailData.setOrderItemsList(mailDataDB.getOrderItemsList());
				    			 mailData.setOrderNumber(mailDataDB.getOrderNumber());
				    			 mailDataRepo.save(mailData);
				    			 String msg="Your order at Amazon, Order Id : "+obj.getOrderNumber()+" status have changed to Cancelled now."
											+ " You can now view your order details at ShopperApp.";
								notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, obj.getOrderNumber());
			    			 }
		    			 }
		    		 }
		    	 }
	    	 }
	     } catch (PyException e) {
	          e.printStackTrace();
	     }
	     catch (Exception e1) {
	          e1.printStackTrace();
	     }
    	}catch(Exception en) {
    		en.printStackTrace();
    		
    	}
    }

    
    public void execLazadaMailData(String userId,  String  id,  String  tokenValue, ResponseEntity<MessageResponseDTO> forEntity, Date date, String deviceId) {  
        InputStream initInputStream = this.getClass().getClassLoader().getResourceAsStream("python/lazada_gmail_java_22_1_19.py");
        String[] arguments = {"myscript.py", "arg1", "arg2", "arg3"};

    	PythonInterpreter pythonInterpreter = new PythonInterpreter();
//    	pythonInterpreter.initialize(System.getProperties(), System.getProperties(), forEntity);
    	
		pythonInterpreter.execfile(initInputStream);
//		pythonInterpreter.set("variable", 5);
		//def amazonMails(userId, id, tokenValue):

		String funcName = "lazada";
//		PyObject eval = pythonInterpreter.eval("amazonMailRead("+forEntity+")");
		 PyObject someFunc = pythonInterpreter.get(funcName); //functionName
		 
		 
		 List<Headers> collect = forEntity.getBody().getPayload().getHeaders().stream()
					.filter(dats -> dats.getName().equalsIgnoreCase("subject")).collect(Collectors.toList());
			Headers subject = collect.get(0);
			String subjectValue = subject.getValue();
			
			List<Headers> fromMail = forEntity.getBody().getPayload().getHeaders().stream()
					.filter(dats -> dats.getName().equalsIgnoreCase("From")).collect(Collectors.toList());
			Headers from = fromMail.get(0);
			System.err.println(from.getValue());
			
			List<Headers> toMail = forEntity.getBody().getPayload().getHeaders().stream()
					.filter(dats -> dats.getName().equalsIgnoreCase("To")).collect(Collectors.toList());
			Headers to = toMail.get(0);
			System.err.println(to.getValue());
			
			
			List<Headers> dateData= forEntity.getBody().getPayload().getHeaders().stream()
					.filter(dats -> dats.getName().equalsIgnoreCase("Date")).collect(Collectors.toList());
			Headers dateH = dateData.get(0); 
			
			String mailDataBody= forEntity.getBody().getPayload().getBody().getData();  
			System.err.println("mailDataBody : "+mailDataBody);  
			 if(mailDataBody != null) {
	     try {
	    	
	    	 PyObject returnData = someFunc.__call__(new PyString(dateH.getValue()), new PyString(subjectValue) , new PyString(from.getValue()), new PyString(mailDataBody) ); 
	    	 String mailDataS =(String) returnData.__tojava__(String.class);
	    	 System.err.println("mailData :::::: "+mailDataS);
	    	 if(!mailDataS.contains("No Data")) {
	    		 List<JythonResponseOfMailsDTO>  mailDataList = new ObjectMapper().readValue(mailDataS,  new TypeReference<List<JythonResponseOfMailsDTO>>(){});
	    		 System.err.println("mailDataList  :"+mailDataList);
		    	 for(int i=0;i< mailDataList.size();i++) {
		    		 JythonResponseOfMailsDTO obj= mailDataList.get(i);
		    		 if(obj.getCurrentStatus().equalsIgnoreCase("Ordered")) {
		    			 List<MailData> mailDataDb=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Ordered");
			    		 if(mailDataDb == null || mailDataDb.isEmpty() ) {
			    			 MailData mailData = new MailData();
			    			 mailData.setDate(date);
			    			 mailData.setCurrentStatus(obj.getCurrentStatus());
			    			 mailData.setCurrentStatusCode(1);
			    			 mailData.setVendor("Lazada");
			    			 
			    			 String arrivesOn=obj.getArrivesOn();
			    			 Date arriveDate = dateComponents.getStringToDate(arrivesOn.split(" - ")[1]);
			    			 mailData.setArrivesOn(arriveDate);
			    			 mailData.setDeliveryAddress(obj.getDeliveryAddress());
			    			 mailData.setGrandTotal(obj.getGrandTotal());
			    			 mailData.setFromMailId(obj.getFromMailId());
			    			 mailData.setToMaild(to.getValue());
			    			 mailData.setOrderItemsList(obj.getOrderItemsList());
			    			 mailData.setOrderNumber(obj.getOrderNumber());
			    			 mailDataRepo.save(mailData);
			    			 String msg = "Your order at Lazada, Order Id : " + obj.getOrderNumber() + " had placed successfully."
										+ " You can now view your order details at ShopperApp.";
								notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, obj.getOrderNumber());
			    		 }
		    		 }else if(obj.getCurrentStatus().equalsIgnoreCase("Shipped")) {
		    			 List<MailData> mailDataDb=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Ordered");
			    		 if(mailDataDb != null  ) {
			    			 List<MailData> mailDataDbShipped=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Shipped");
			    			 if(mailDataDbShipped == null || mailDataDbShipped.isEmpty() ) {
				    			 MailData mailDataDB = mailDataDb.get(0);
				    			 MailData mailData =new MailData();
				    			 mailData.setDate(date);
				    			 mailData.setCurrentStatus(obj.getCurrentStatus());
				    			 mailData.setCurrentStatusCode(2);
				    			 mailData.setVendor("Lazada");
				    			 mailData.setArrivesOn(mailDataDB.getArrivesOn());
				    			 mailData.setDeliveryAddress(mailDataDB.getDeliveryAddress());
				    			 mailData.setGrandTotal(mailDataDB.getGrandTotal());
				    			 mailData.setFromMailId(mailDataDB.getFromMailId());
				    			 mailData.setToMaild(to.getValue());
				    			 mailData.setOrderItemsList(mailDataDB.getOrderItemsList());
				    			 mailData.setOrderNumber(mailDataDB.getOrderNumber());
				    			 mailDataRepo.save(mailData);
				    			 String msg="Your order at Lazada, Order Id : "+obj.getOrderNumber()+" status have changed to Shipped now."
											+ " You can now view your order details at ShopperApp.";
									notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, obj.getOrderNumber());
			    			 }
			    		 }
			    		 
		    		 }else if(obj.getCurrentStatus().equalsIgnoreCase("Delivered")) {
		    			 List<MailData> mailDataDb=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Ordered");
		    			 if(mailDataDb != null  ) {
			    			 List<MailData> mailDataDbDelivered=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Delivered");
			    			 if(mailDataDbDelivered == null || mailDataDbDelivered.isEmpty() ) {
				    			 MailData mailDataDB = mailDataDb.get(0);
				    			 MailData mailData =new MailData();
				    			 mailData.setDate(date);
				    			 mailData.setCurrentStatus(obj.getCurrentStatus());
				    			 mailData.setCurrentStatusCode(3);
				    			 mailData.setVendor("Lazada");
				    			 mailData.setArrivesOn(mailDataDB.getArrivesOn());
				    			 mailData.setDeliveryAddress(mailDataDB.getDeliveryAddress());
				    			 mailData.setGrandTotal(mailDataDB.getGrandTotal());
				    			 mailData.setFromMailId(mailDataDB.getFromMailId());
				    			 mailData.setToMaild(to.getValue());
				    			 mailData.setOrderItemsList(mailDataDB.getOrderItemsList());
				    			 mailData.setOrderNumber(mailDataDB.getOrderNumber());
				    			 mailDataRepo.save(mailData);
				    			 String msg="Your order at Lazada, Order Id : "+obj.getOrderNumber()+" status have changed to Delivered now."
											+ " You can now view your order details at ShopperApp.";
								notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, obj.getOrderNumber());
			    			 }
		    			 }
		    		 }else if(obj.getCurrentStatus().equalsIgnoreCase("Cancelled")) {
		    			 List<MailData> mailDataDb=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Ordered");
		    			 if(mailDataDb != null  ) {
			    			 List<MailData> mailDataDbDelivered=   mailDataRepo.findByOrderNumberAndCurrentStatus(obj.getOrderNumber(), "Cancelled");
			    			 if(mailDataDbDelivered == null || mailDataDbDelivered.isEmpty() ) {
				    			 MailData mailDataDB = mailDataDb.get(0);
				    			 MailData mailData =new MailData();
				    			 mailData.setDate(date);
				    			 mailData.setCurrentStatus(obj.getCurrentStatus());
				    			 mailData.setCurrentStatusCode(5);
				    			 mailData.setVendor("Lazada");
				    			 mailData.setArrivesOn(mailDataDB.getArrivesOn());
				    			 mailData.setDeliveryAddress(mailDataDB.getDeliveryAddress());
				    			 mailData.setGrandTotal(mailDataDB.getGrandTotal());
				    			 mailData.setFromMailId(mailDataDB.getFromMailId());
				    			 mailData.setToMaild(to.getValue());
				    			 mailData.setOrderItemsList(mailDataDB.getOrderItemsList());
				    			 mailData.setOrderNumber(mailDataDB.getOrderNumber());
				    			 mailDataRepo.save(mailData);
				    			 String msg="Your order at Lazada, Order Id : "+obj.getOrderNumber()+" status have changed to Cancelled now."
											+ " You can now view your order details at ShopperApp.";
								notificationMessageFCM.fcmMailNotificationMessage(msg, deviceId, obj.getOrderNumber());
			    			 }
		    			 }
		    		 }
		    	 }
	    	 }
	     } catch (PyException e) {
	          e.printStackTrace();
	     }
	     catch (Exception e1) {
	          e1.printStackTrace();
	     }
			 }
    }
    
    public String execMethodInPyClass(String fileName, String className, String methodName) {
        pythonInterpreter.exec("from main import Main");
        PyClass mainClass = (PyClass) pythonInterpreter.get("Main");
        PyObject main = mainClass.__call__();
        PyObject pyObject = main.invoke("launchAndDebug", new PyString("svn://192.168.37.135/test"));
        return pyObject.toString();
    }

}