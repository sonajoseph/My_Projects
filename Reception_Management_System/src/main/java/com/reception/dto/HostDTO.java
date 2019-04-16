package com.reception.dto;

public class HostDTO {
	private String hostname;
	private String empid;
	private String designation;
	private String department;
	private String mobileno;
	private String username;
	private String password;
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "HostDTO [hostname=" + hostname + ", empid=" + empid + ", designation=" + designation + ", department="
				+ department + ", mobileno=" + mobileno + ", username=" + username + ", password=" + password + "]";
	}
	
	
	
}
