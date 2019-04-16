package com.shopperapp.dto;

import lombok.Data;

@Data
public class ResponseDTO<T> {
	private T data;
	private boolean status;
	private String message;
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
