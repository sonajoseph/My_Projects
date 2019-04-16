package com.reception.service;



import java.util.List;

import com.reception.model.Events;

public interface EventService {
	
	
	public Events save (Events event);
	public  List<Events>findAll();
	public List< Events> findByName(String event_name);
	public List<Events> findByDate(String date);
	public List<Events> findByRoomno(String roomno);
	
	

}
