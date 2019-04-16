package com.shopperapp.mysql.service;

import com.shopperapp.dto.ResponseDTO;
import com.shopperapp.mysql.models.CompanyData;
import com.shopperapp.mysql.models.CompanyMoreDetails;

public interface CompanyDataService {

//	ResponseDTO<CompanyData> storeData(CompanyData companyData);
	
	String storeData(CompanyData companyData, CompanyMoreDetails companyMoreDetails);

}
