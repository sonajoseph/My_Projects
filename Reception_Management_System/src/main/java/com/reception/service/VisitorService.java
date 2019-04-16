package com.reception.service;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;

import com.reception.dto.VisitorDto;
import com.reception.model.Events;
import com.reception.model.Hosts;
import com.reception.model.Visitors;

public interface  VisitorService {
	Visitors  save(Visitors visitor);
	void saveVisitor(VisitorDto visitor);
	List<Visitors> findByDate( Date date1,Date date2);
//	List<Visitors> findByDate( Integer i,Date date1,Date date2);
	List<Visitors> findByEvents(Date date1,Date date2);
	List<Visitors> findByHosts(Date date1,Date date2);
//	List<Visitors> findByDate(String date1,String date2);
	
	

}
