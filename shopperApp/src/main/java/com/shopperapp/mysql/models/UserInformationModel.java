package com.shopperapp.mysql.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class UserInformationModel {
	@Id
	private String userId;
	private String accessToken;
	private String refreshToken;
}
