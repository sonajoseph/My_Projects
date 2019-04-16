package com.reception.serviceimpl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.reception.dto.VisitorDto;
//import com.reception.enums.Role;
import com.reception.model.Users;
import com.reception.model.Visitors;
import com.reception.repository.UserRepository;
import com.reception.repository.VisitorRepository;
//import com.reception.service.JSONObject;
//import com.reception.service.JSONObject;
//import com.reception.service.JSONObject;
import com.reception.service.VisitorService;

@Service
public class VisitorServiceImpl implements VisitorService {
	@Autowired
	private VisitorRepository visitorRepo;
	@Autowired
	private BCryptPasswordEncoder encoder;
	@Autowired
	private UserRepository userRepo;
	private String jsonString;

	@Transactional
	@Override
	public void saveVisitor(VisitorDto visitor) {
		Users user = new Users();
		Visitors visitors = new Visitors();
		try {
			Timestamp timestamp = new Timestamp(new Date().getTime());

			String pattern = "dd-MM-yyyy hh:mm:ss";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());
			Date parse = simpleDateFormat.parse(date);

			BeanUtils.copyProperties(visitor, user);// convert visitor to user
//			user.setRole(Role.ROLE_VISITOR.toString());
			user.setRoleid("4");
			user.setPassword(encoder.encode(user.getPassword()));
			userRepo.save(user);
			BeanUtils.copyProperties(visitor, visitors);// convert visitor to visitors

			visitors.setDate(parse);
			System.err.println("------------------->" + visitors);
			visitors.setUser(user);
			Visitors save = visitorRepo.save(visitors);
			System.err.println("------------------->" + save);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Visitors save(Visitors visitor) {

		return visitorRepo.save(visitor);
	}


	

	

	@SuppressWarnings("unchecked")
	@Override
	public List<Visitors> findByDate(Date date1, Date date2) {
		
		List<Visitors> findByDate = visitorRepo.findByDate(date1, date2);
		return findByDate;
	}
	
//	
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<Visitors> findByDate(Integer i,Date date1, Date date2) {
//		
//		List<Visitors> findByDate = visitorRepo.findByDate(i,date1, date2);
//		return findByDate;
//	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Visitors> findByEvents(Date date1, Date date2) {
		List<Visitors> findByEvents = visitorRepo.findByEvents(date1, date2);
		return findByEvents;
		
	}

	@Override
	public List<Visitors> findByHosts(Date date1, Date date2) {
		List<Visitors> findByHosts = visitorRepo.findByHosts(date1, date2);
		return findByHosts;
		
		
	}

	


	
}

	

	

	

	
	


