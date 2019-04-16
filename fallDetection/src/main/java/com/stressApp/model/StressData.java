package com.stressApp.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "data")
@lombok.Data
@Getter
@Setter
public class StressData {
	@Id
	public String id;
	public String userId;
	public Date timeStamp;
	public String accelerometer_x;
	public String accelerometer_y;

	public String accelerometer_z;

	public String heartRate;

	

	
		
	}



