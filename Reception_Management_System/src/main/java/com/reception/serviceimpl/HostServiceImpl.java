package com.reception.serviceimpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reception.dto.HostDTO;

import com.reception.model.Hosts;
import com.reception.model.Users;
import com.reception.model.Visitors;
import com.reception.repository.HostRepository;
import com.reception.repository.UserRepository;
import com.reception.repository.VisitorRepository;
import com.reception.service.HostService;
@Service
public class HostServiceImpl implements HostService{
  @Autowired
  
  private HostRepository hostrepo;
@Autowired
  
  private VisitorRepository visitoRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private BCryptPasswordEncoder encoder;
  
	@Override
	public Hosts save(Hosts host) {
		
		
		return hostrepo.save(host);
	}
		
		
//	@Override
//	public List<Hosts> findAll() {
//		
//		return hostrepo.findAll();
//	}


	@Override
	public List<Hosts> findByHostname(String name) {
		
		return hostrepo.findByHostname(name);
	}
//
//
//
//	@Override
//	public List<Hosts> findByEmpid(String empid) {
//		return hostrepo.findByEmpid(empid);
//	}
//
//
//
//	@Override
//	public List<Hosts> findByDesignation(String designation) {
//		
//		return hostrepo.findByDesignation(designation);
//	}
//
//
//
//	@Override
//	public List<Hosts> findByDepartment(String department) {
//		
//		return hostrepo.findByDepartment(department);
//	}
//
//
//
//	@Override
//	public List<Hosts> findByMobileno(String mobileno) {
//		
//		return hostrepo.findByMobileno(mobileno);
//	}
//
//
//
//

	

	@Transactional 
	//transaction management for both table
	@Override
	public void saveHost(HostDTO host) { //HostDTO contains all the details needed for both table.
		Users user = new Users();
		Hosts hosts = new Hosts();
		BeanUtils.copyProperties(host, user);//convert host to user db.
//		user.setRole(Role.ROLE_HOST.toString());//instead of assigning role through postman.maually assigned.
	    user.setRoleid("3");
		user.setPassword(encoder.encode(user.getPassword()));
		userRepo.save(user);
		hosts.setUser(user);
		
		BeanUtils.copyProperties(host, hosts);//convert host to hosts db.
		hostrepo.save(hosts);
		
	}


//	@Override
//	public List<String> findHostByName() {
//		
//		return hostrepo.findHostByName();
//	}


//	@Override
//	public List<Hosts> findAll() {
//		return hostrepo.findAll();
//	}
//	
	@Override
	public List<Hosts> findAll() {
		System.err.println("+++++++++++++++++++++++++++++++++++");
		List<Hosts> findAll = hostrepo.findAll();
		System.err.println("+++++++++++++++++++++++++++++++++++++" + findAll.size());
	    return findAll;
	}


//@Override
//public List<Hosts> findByHosts(Date date11, Date date12) {
//	List<Hosts> findByHosts=hostrepo.findByHosts(date11, date12);
//	return findByHosts;
//}


	
//		@SuppressWarnings("unchecked")
//		@Override
//		public List<Hosts> findByDate(Integer i,Date date1, Date date2) {
//			
//			List<Hosts> findByDate = hostrepo.findByDate(i,date1, date2);
//			return findByDate;
//		
//		
//	}


		

	
		
	}




	

