package com.reception.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "hosts")

public class Hosts {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int hostid;
	private String hostname;
	private String empid;
	private String designation;
	private String department;
	private String mobileno;
	
	@OneToOne( cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
     private Users user;

	
	
	
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public int getHostid() {
		return hostid;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public void setHostid(int hostid) {
		this.hostid = hostid;
	}
	
	public String getEmpid() {
		return empid;
	}
	public void setEmpid(String empid) {
		this.empid = empid;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	
}
	
	
	
	
	
	
	
	


