package com.shopperapp.mysql.controller;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.shopperapp.dto.ResponseDTO;
import com.shopperapp.mysql.models.CompanyData;
import com.shopperapp.mysql.models.CompanyMoreDetails;
import com.shopperapp.mysql.repo.CompanyDataRepo;
import com.shopperapp.mysql.service.CompanyDataService;

@RestController
@RequestMapping("company-data")
public class CompanyDataController {
	private static final Logger logger = LoggerFactory.getLogger(CompanyDataController.class);

	@Autowired
	private CompanyDataService companyDataService;
	
	@Autowired
	private CompanyDataRepo companyDataRepo;
	
//	@PostMapping
//	public ResponseDTO<CompanyData> storeCompanyData(@RequestBody CompanyData companyData){
//		logger.info("At storeCompanyData...");
//		
//		return    companyDataService.storeData(companyData);
//	}
	
	@PostMapping
	public ResponseEntity<?> storeCompanyData(
			@RequestParam ("companyData") String   companyData, 
			@RequestParam ("CompanyMoreDetails") String companyMoreDetails
			){
		logger.info("At storeCompanyData..."); 
		
		Type CompanycataType = new TypeToken<CompanyData>() {}.getType(); 
		CompanyData companyDataOBJ = new Gson().fromJson(companyData, CompanycataType); 
		
		
		Type companyMoreDetailsType = new TypeToken<CompanyMoreDetails>() {}.getType(); 
		CompanyMoreDetails companyMoreDetailsObj = new Gson().fromJson(companyMoreDetails, companyMoreDetailsType); 
		
		return    new ResponseEntity<String>(companyDataService.storeData(companyDataOBJ,companyMoreDetailsObj), HttpStatus.OK);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<?> storeCompanyData(@PathVariable   Long id){
		logger.info("At storeCompanyData..."+id);
		Optional<CompanyData> companyData=companyDataRepo.findById(id); 
		System.err.println(companyData.toString());
//		CompanyData data= companyData.get();
		if(companyData.isPresent()) {
			ResponseDTO returnDto = new ResponseDTO();
			returnDto.setMessage("Success");
			returnDto.setStatus(true);
			returnDto.setData(companyData.get());  
			return new ResponseEntity<ResponseDTO>(returnDto, HttpStatus.OK);
			
		}
		else {
			ResponseDTO returnDto = new ResponseDTO();
			returnDto.setMessage("Unknown Id.");
			returnDto.setStatus(false);
			returnDto.setData(null);
			return new ResponseEntity<ResponseDTO>(returnDto, HttpStatus.OK);
		}
		
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllData(){
		logger.info("At storeCompanyData...");
		List<CompanyData> companyData=companyDataRepo.findAll();
		 
		return new ResponseEntity<List<CompanyData>>(companyData, HttpStatus.OK);
	}
	
//	@GetMapping("/allMailIds")
//	public ResponseEntity<?> getAllMailIds(){
//		logger.info("At getAllMailIds...");
//		List<CompanyData> companyData=companyDataRepo.findDistinctByEmails();
//		 
//		return new ResponseEntity<List<CompanyData>>(companyData, HttpStatus.OK);
//	}
}
