package com.reception.exception;

public class ResponseComponent<T> {
	private String message;
	private T response;
	private Boolean flag;
	public ResponseComponent(String message, T response, Boolean flag) {
		super();
		this.message = message;
		this.response = response;
		this.flag = flag;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public T getResponse() {
		return response;
	}
	public void setResponse(T response) {
		this.response = response;
	}
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	
	
}
