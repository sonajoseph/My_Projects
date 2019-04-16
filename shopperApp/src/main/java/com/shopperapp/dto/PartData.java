package com.shopperapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class PartData {
	
	private String partId;
    private String mimeType;
    private String filename;
    private List<Headers> headers;
    private BodyDto  body; 
    private List<PartData> parts;
}
