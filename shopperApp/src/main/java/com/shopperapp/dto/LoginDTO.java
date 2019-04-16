package com.shopperapp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LoginDTO {

	String mailId;
	String authcode;
	String deviceId;
}
