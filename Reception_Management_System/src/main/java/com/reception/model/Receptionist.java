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

@Table(name = "receptionist")

public class Receptionist {
	
@Id
@GeneratedValue(strategy=GenerationType.AUTO)
private int receptionistid;
private String name;

@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "user_id", nullable = false)
 private Users user;
public int getReceptionistid() {
	return receptionistid;
}
public void setReceptionistid(int receptionistid) {
	this.receptionistid = receptionistid;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public Users getUser() {
	return user;
}
public void setUser(Users user) {
	this.user = user;
}




}








