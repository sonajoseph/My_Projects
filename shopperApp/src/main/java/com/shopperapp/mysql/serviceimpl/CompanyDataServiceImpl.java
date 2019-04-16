package com.shopperapp.mysql.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopperapp.dto.ResponseDTO;
import com.shopperapp.exception.FailedToSave;
import com.shopperapp.mysql.models.CompanyData;
import com.shopperapp.mysql.models.CompanyMoreDetails;
import com.shopperapp.mysql.repo.CompanyDataRepo;
import com.shopperapp.mysql.repo.CompanyMoreDetailsRepo;
import com.shopperapp.mysql.service.CompanyDataService;
@Service
public class CompanyDataServiceImpl implements CompanyDataService {
	@Autowired
	private CompanyDataRepo companyDataRepo;
	
	@Autowired
	private CompanyMoreDetailsRepo companyMoreDetailsRepo;
	
//	@Override
//	public ResponseDTO<CompanyData> storeData(CompanyData companyData) {
//		CompanyData companyDatas= companyDataRepo.save(companyData);
//		System.err.println("after saving" + companyData);
//		if (companyDatas  != null) {
//			ResponseDTO<CompanyData> responseDTO = new ResponseDTO<>();
//			responseDTO.setData(companyDatas);
//			responseDTO.setStatus(true);
//			responseDTO.setMessage("Successfully Created");
//			return responseDTO;
//		}
//		throw new FailedToSave("Failed to save company data");
//	}
	@Override
	public String storeData(CompanyData companyData, CompanyMoreDetails companyMoreDetails) {
		  
		CompanyData companyDataDB =  companyDataRepo.findByCompanyName(companyData.getCompanyName());
		if(companyDataDB == null) {
			companyDataDB= companyDataRepo.save(companyData);
		}
		
		CompanyMoreDetails companyMoreDetailsDB = companyMoreDetailsRepo.findByEmailId(companyMoreDetails.getEmailId());
		
		if(companyMoreDetailsDB == null) {
			companyMoreDetails.setCompanyData(companyDataDB); 
			companyMoreDetailsDB=companyMoreDetailsRepo.save(companyMoreDetails);
		}

		if (companyMoreDetailsDB  != null) {
			return "success";
		}else {
			return "failed";
		}
	}

}
