package com.shopperapp.components;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
@Data
@Component
@ConfigurationProperties(prefix = "google")

public class PropertyComponents {
	private String googleMessageIdUrl;

	public String getGoogleMessageIdUrl() {
		return googleMessageIdUrl;
	}

	public void setGoogleMessageIdUrl(String googleMessageIdUrl) {
		this.googleMessageIdUrl = googleMessageIdUrl;
	}
	
}
