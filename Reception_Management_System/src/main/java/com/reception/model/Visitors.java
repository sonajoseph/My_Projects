package com.reception.model;




import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "visitor")
public class Visitors {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int visitorid;
	private String visitorname;
	private String mobileno;
	private int category_id;
	private Integer eventid;
    private Integer hostid;
    private String category;
    private String events;
    private String host;
    

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
     private Users user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEvents() {
		return events;
	}

	public void setEvents(String events) {
		this.events = events;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getVisitorid() {
		return visitorid;
	}

	public void setVisitorid(int visitorid) {
		this.visitorid = visitorid;
	}

	public String getVisitorname() {
		return visitorname;
	}

	public void setVisitorname(String visitorname) {
		this.visitorname = visitorname;
	}

	public String getMobileno() {
		return mobileno;
	}

	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	

	public Integer getEventid() {
		return eventid;
	}

	public void setEventid(Integer eventid) {
		this.eventid = eventid;
	}

	public Integer getHostid() {
		return hostid;
	}

	public void setHostid(Integer hostid) {
		this.hostid = hostid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Visitors [visitorid=" + visitorid + ", visitorname=" + visitorname + ", mobileno=" + mobileno
				+ ", category_id=" + category_id + ", eventid=" + eventid + ", hostid=" + hostid + ", date=" + date
				+ ", user=" + user + "]";
	}
    
    


	
	
}