package com.shopperapp.mysql.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class CompanyMoreDetails {
	@Id 
	@GeneratedValue( strategy=GenerationType.AUTO )
	
	private int id;
	
	private String address1; 
	private String location; 
	private String emailType;
	private String emailId;
	
	@ManyToOne
	CompanyData  companyData;
	
	


}
