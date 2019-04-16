package com.shopperapp.components;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

	 @Component
	public class DateComponents {

		
	private static final Logger logger = LoggerFactory.getLogger(DateComponents.class);
		
		public long getCurrentAge(Date dob){
//			logger.info("At  getCurrentAge");
			Date dateOfBirth = dob;
			ZoneId defaultZoneId = ZoneId.systemDefault();
			Instant instant = dateOfBirth.toInstant();
			java.time.LocalDate date = instant.atZone(defaultZoneId).toLocalDate();

			
			Date today = new Date(); 
			Instant toDayinstant = Instant.ofEpochMilli(today.getTime()); 
			LocalDateTime localToDateTime = LocalDateTime.ofInstant(toDayinstant, ZoneId.systemDefault());
			LocalDate now = localToDateTime.toLocalDate(); 
//			logger.info(""+ChronoUnit.YEARS.between(date, now));
			
			long age=ChronoUnit.YEARS.between(date, now);
			
//			logger.info("AGE : "+age);
			
			return age;
			
			
		}
		
		public static Date addDays(Date date, int days) {
			logger.info("At addDays -  date: "+date+"\t Days : "+days);
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.add(Calendar.DATE, days); //minus number would decrement the days 
//	        logger.info("Date : "+cal.getTime()); 
	        return cal.getTime();
	    }
		public static Date subDays(Date date, int days) {
//			logger.info("At subDays -  date: "+date+"\t Days : "+days);
			
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.add(Calendar.DATE, - (days-1));
//	        logger.info("Date : "+cal.getTime()); 
	        return cal.getTime();
	    }
		
		
		public static Date addMonths(Date date, int months){
//			logger.info("At addMonths -- Date : "+date+"\t months : "+months); 
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date); 
	        cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH)+months));
//	        logger.info("Date : "+cal.getTime()); 
	        return cal.getTime();
	    }
		
		public static Date subMonths(Date date, int months){
//			logger.info("At subMonths -- Date : "+date+"\t months : "+months); 
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date); 
	        cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH)-months));
//	        logger.info("Date : "+cal.getTime()); 
	        return cal.getTime();
	    }
		
		public Timestamp getCurrentDateTime(){
			Date now= new Date();
			
			return new Timestamp(now.getTime());
			
		}
		
		public int getDayOfWeekFromMonday(Date date){
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        //System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
	        return calendar.get(Calendar.DAY_OF_WEEK)-1;
		}
		
		public int getDayOfWeekFromSunday(Date date){
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        //System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
	        return calendar.get(Calendar.DAY_OF_WEEK);
		}
		public int getDayOfMonth(Date date){
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        //System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
	        return calendar.get(Calendar.DAY_OF_MONTH);
		}
		
		public Date getYearStartDate(int year){
			
			//int year =1900+date.getYear();
			
			Calendar calendarStart=Calendar.getInstance();
		    calendarStart.set(Calendar.YEAR,year);
		    calendarStart.set(Calendar.MONTH,0);
		    calendarStart.set(Calendar.DAY_OF_MONTH,1);
		    
		    Date startDate=calendarStart.getTime();
//		    System.err.println("Year startDate  :"+startDate);
		    return startDate;

		}
		
		public static long convertUtilDateToEpoch(Date date) {
//			logger.info("At convertUtilDateToEpoch -  date: "+date+"\t Days : "+days);
			
	          
	        return date.getTime()/1000;
	    }
		
		
		public Date getStringToDate(String sdate) {
			try {
//				String string = "January 2, 2010";
				DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
				Date date = format.parse(sdate);
				System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
				return date;
			}catch(Exception e){
			 e.printStackTrace();	
			 try {
				 String string = "2, January 2010";
					DateFormat format = new SimpleDateFormat("d, MMMM yyyy", Locale.ENGLISH);
					Date date = format.parse(sdate);
					System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
					return date;
			 }catch (Exception e2) {
				// TODO: handle exception
				 e2.printStackTrace();	
				 try {
					 String string = "2 January, 2010";
						DateFormat format = new SimpleDateFormat("d MMMM, yyyy", Locale.ENGLISH);
						Date date = format.parse(sdate);
						System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
						return date;
				 }catch (Exception e3) {
					// TODO: handle exception
					 e3.printStackTrace();	
				}
			}
			}
			return null;
			
		}
		
		public Date getStringToDate2(String sdate) {
			try {
//				String string = "January 2, 2010";
				DateFormat format = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);
				Date date = format.parse(sdate);
				System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
				return date;
			}catch(Exception e){
			 e.printStackTrace();	
			}
			return null;
		}
		
		public Date getStringToDate3(String arrivesOnDate) {
			try {
//				String string = "2019-01-19";
				DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
				Date date = format.parse(arrivesOnDate);
				System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
				return date;
			}catch(Exception e){
			 e.printStackTrace();	
			}
			return null;
		}
		
		
		
		
		public Date getStringToDate4(String deliveryDate) {
			
			
			DateFormat format = new SimpleDateFormat("dd MMMM yyyy");
			Date date12 = null;
			try {
				date12 = format.parse(deliveryDate);
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			
			
			return date12;
			
		}
		public Date getStringToDate5(String sdate) {
			try {
//				String string = "January 2, 2010";
				DateFormat format = new SimpleDateFormat("MMMMd yyyy", Locale.ENGLISH);
				Date date = format.parse(sdate);
				System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
				return date;
			}catch(Exception e){
			 e.printStackTrace();	
			  
			}
			return null;
			
		}
		
		public long getDaysDifference(Date arrivesOn){
			logger.info("At  getDaysDifference"); 
			
			ZoneId defaultZoneId = ZoneId.systemDefault();
			Instant instant = arrivesOn.toInstant();
			java.time.LocalDate date = instant.atZone(defaultZoneId).toLocalDate();

			Date today = new Date(); 
			Instant toDayinstant = Instant.ofEpochMilli(today.getTime()); 
			LocalDateTime localToDateTime = LocalDateTime.ofInstant(toDayinstant, ZoneId.systemDefault());
			LocalDate now = localToDateTime.toLocalDate(); 
			long days=ChronoUnit.DAYS.between( now,date);
			
			logger.info("days : "+days);
			return days;
		}
		
//		public static void main(String[] args) {
//			Date date = new DateComponents().getStringToDate("Jan 16, 2019");
//			System.out.println(date);
//			
//			Date date = new DateComponents().getStringToDate2("24/01/2019");
//			System.out.println(date);
			
//			long days = new DateComponents().getDaysDifference(date);
			
			 
//		}
	}
 