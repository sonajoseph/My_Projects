package com.shopperapp.components;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		System.err.println("++++++++++++++++++++++++");
		System.err.println(response);
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return false;
	}
}