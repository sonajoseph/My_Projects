package com.shopperapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class MessageIdDTO {
	private List<Messages> messages;
	private String nextPageToken;
	private Integer resultSizeEstimate;
	public List<Messages> getMessages() {
		return messages;
	}
	public void setMessages(List<Messages> messages) {
		this.messages = messages;
	}
	public String getNextPageToken() {
		return nextPageToken;
	}
	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}
	public Integer getResultSizeEstimate() {
		return resultSizeEstimate;
	}
	public void setResultSizeEstimate(Integer resultSizeEstimate) {
		this.resultSizeEstimate = resultSizeEstimate;
	}
	
	
	
}
