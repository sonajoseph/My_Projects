package com.shopperapp.dto;

import lombok.Data;

@Data
public class ReponseDTO<T> {
	private String message;
	private T data;
	private Boolean status;
}
