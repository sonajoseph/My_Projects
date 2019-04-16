package com.shopperapp.mysql.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Data
public class UserToken {
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO ) 
	private Long id;
	private String accessToken;
	private String refreshToken;
	private String mailId;
	private String deviceId;
	private String lastMessageId;
	private Date updatedAt;
}
