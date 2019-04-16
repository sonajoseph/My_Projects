package com.shopperapp.exception;

public class FailedToSave extends RuntimeException {
	public FailedToSave(String message) {
		super(message);
	}
}
