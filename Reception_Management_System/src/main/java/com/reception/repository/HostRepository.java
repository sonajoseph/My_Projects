package com.reception.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.reception.model.Hosts;
import com.reception.model.Visitors;


@Repository
public interface HostRepository extends JpaRepository<Hosts,Integer>{
	
   public Hosts save(Hosts host);
	 public List<Hosts> findAll();
	
	 public List<Hosts> findByHostname(String hostname);
//	 @Query(value="SELECT host,COUNT(*) as totalvisitors FROM Visitors WHERE date BETWEEN ?1 AND ?2 GROUP BY host")
//	 List<Hosts>findByHosts(Date date11,Date date12);
//	 @Query(value="SELECT COUNT(*) AS totalvisitors FROM Visitors WHERE hostid=?1 AND date BETWEEN ?2 AND ?3")
//	 List<Hosts> findByDate(Integer i,Date date1,Date date2);
	 
//	List<Hosts> findByEmpid(String empid);
//	List<Hosts>findByDesignation(String designation);
//	List<Hosts>findByDepartment(String department);
//	List<Hosts> findByMobileno(String mobileno);
	
//     List<String> findHostByName();
//	

}
