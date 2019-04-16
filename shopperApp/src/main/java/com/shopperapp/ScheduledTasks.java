package com.shopperapp;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shopperapp.components.DateComponents;
import com.shopperapp.components.NotificationMessageFCM;
import com.shopperapp.components.TokenComponent;
import com.shopperapp.mongo.models.MailData;
import com.shopperapp.mongo.repo.MailDataRepo;
import com.shopperapp.mysql.models.UserToken;
import com.shopperapp.mysql.repo.UserTokenRepo;
import com.shopperapp.service.GoogleAPIService;

/**
 * Created by PR HARISH
 */
@Component
public class ScheduledTasks {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	@Autowired
	UserTokenRepo userTokenRepo;
	 
	@Autowired
	GoogleAPIService googleAPIService;
	
	@Autowired
	TokenComponent tokenComponent;
	
	@Autowired
	NotificationMessageFCM notificationMessageFCM; 
	
	
	@Autowired
	DateComponents dateComponents;
	
	@Autowired
	MailDataRepo mailDataRepo;
	

//    @Scheduled(fixedDelay = 6000) //1Min
//	@Scheduled(fixedDelay = 12000) //2Min
//    @Scheduled(fixedDelay = 18000) //3Min
//  @Scheduled(fixedDelay = 300000) //05Min
//  @Scheduled(fixedDelay = 600000) //10Min 
    public void schedulerForFetchingNewMailsForFCM() {
        logger.info(" AT  schedulerForFetchingNewMailsForFCM.......");
        try {
        	List<UserToken> usersList = userTokenRepo.findAll();   
        	usersList.forEach(userToken ->{
        		Date lastDataTakenTime=userToken.getUpdatedAt(); 
        		if(userToken.getDeviceId() != null && !userToken.getDeviceId().isEmpty()) {
        			String accessToken = tokenComponent.getNewAccessTokenUsingRefreshTokenByEmailId(userToken.getMailId());
            		if (accessToken.equalsIgnoreCase("Login Failed") || accessToken == "") {
            			logger.info("Device Id Not Available..... Please Login. with email Id : "+userToken.getMailId());
            		}else {
            			userToken.setAccessToken(accessToken);
            			userToken.setUpdatedAt(new Date());
            			userTokenRepo.save(userToken); 
            			if(userToken.getDeviceId().length()>1 && userToken.getDeviceId() != null && userToken.getDeviceId() != "") {
            				googleAPIService.fetchMessageId(accessToken, userToken.getMailId());
            			}
            		}
        		}
        	});
        } catch(Exception e) {
        	logger.info("Excepion in Mail Checking ");
        	logger.error(e.getMessage());
        	e.printStackTrace();
        }
    }
//    @Scheduled(fixedDelay = 666600) //10Min
    public void shedulerForArrivingSoonNotification() {
    	logger.info("At sheduler For Arriving Soon Notification.....");
    	Date fromDate=new Date();
    	try {
    		// checking the mails with comming arriving date
    		List<MailData> ordersList= mailDataRepo.findByCurrentStatusAndArrivesOnNotificationStatusAndArrivesOnGreaterThan("Ordered", false, fromDate);
	    	ordersList.forEach(mailData->{
	    		// if difference between current date and arrives on == 1..send one day before delivery notifications.
	    		long days = dateComponents.getDaysDifference(mailData.getArrivesOn());
	    		if(days == 1 ) {
	    			
	    			// if items delivered before expected date.dont send  notifications.One day before ,it checks if there is any delivered mail.if it is not there send the notifications that it will be delivered.and if it is cancelled before one day before delivery dont send notification.
	    			MailData cancelDetails= mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatusCode(mailData.getOrderNumber(), mailData.getVendor(),5);
	    			MailData deliveryDetails = mailDataRepo.findOneByOrderNumberAndVendorAndCurrentStatusCode(mailData.getOrderNumber(), mailData.getVendor(), 4);
	    			  if(deliveryDetails == null && cancelDetails==null) {
	    				String msg="Your order at "+mailData.getVendor()+", Order Id : "+mailData.getOrderNumber()+" going to deliver by end of day tomorrow.";
		    			UserToken userToken = userTokenRepo.findByMailId(mailData.getToMaild());
		    			if(userToken != null) {
			    			mailData.setArrivesOnNotificationStatus(true);
			    			mailDataRepo.save(mailData);
							notificationMessageFCM.fcmMailNotificationMessage(msg, userToken.getDeviceId(),mailData.getOrderNumber());
		    			}
	    			}
		    			
	    		}
	    	});
		 } catch(Exception e) {
	     	logger.info("Excepion in Mail Checking ");
	     	logger.error(e.getMessage());
	     	e.printStackTrace();
	     }
    }
}
	 
  