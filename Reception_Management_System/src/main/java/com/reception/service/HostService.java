package com.reception.service;

import java.util.Date;
import java.util.List;

import com.reception.dto.HostDTO;
import com.reception.model.Hosts;
import com.reception.model.Visitors;


public interface HostService {
	
	Hosts save(Hosts host);
	public List<Hosts> findAll();
	void saveHost(HostDTO host);
	
    public  List<Hosts> findByHostname(String hostname);
//    public List<Hosts> findByEmpid(String empid);
//	 public List<Hosts> findByDesignation(String designation);
//	 public List<Hosts> findByDepartment(String department);
//	 public List<Hosts> findByMobileno(String mobileno);
//	List<Hosts> findByDate(Integer i, Date date11, Date date12);
//	List<Hosts> findByHosts(Date date11, Date date12);
	
	//List<Hosts> allHost();
//	 List<String> findHostByName();

}
