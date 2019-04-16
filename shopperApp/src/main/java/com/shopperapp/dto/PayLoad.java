package com.shopperapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class PayLoad {
	private String mimeType;
	private List<Headers> headers;
	private List<PartData> parts;
	private BodyDto  body;
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public List<Headers> getHeaders() {
		return headers;
	}
	public void setHeaders(List<Headers> headers) {
		this.headers = headers;
	}
	
}
