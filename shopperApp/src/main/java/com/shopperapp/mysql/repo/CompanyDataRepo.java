package com.shopperapp.mysql.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopperapp.mysql.models.CompanyData;
@Repository
public interface CompanyDataRepo extends JpaRepository<CompanyData, Long> {
	
	public Optional<CompanyData> findById(Long id); 
	public CompanyData findByCompanyName(String companyName); 
//	public List<CompanyData> findDistinctByEmails();

}
