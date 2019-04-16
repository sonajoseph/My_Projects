package com.shopperapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class Messages {
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	private String id;
	private  String threadId;

}
