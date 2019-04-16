package com.shopperapp.mysql.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class CompanyData {
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO ) 
	private Long id;
	private String companyName;
//	@ElementCollection
//	private List<String> emails; 
	private String website;
 
}