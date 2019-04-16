package com.shopperapp.mysql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; 
import com.shopperapp.mysql.models.CompanyMoreDetails;

public interface CompanyMoreDetailsRepo extends JpaRepository<CompanyMoreDetails, Long> { 
	public Optional<CompanyMoreDetails> findOneByEmailId(String emailId);  
	
	public CompanyMoreDetails findByEmailId(String emailId);  
	
}
