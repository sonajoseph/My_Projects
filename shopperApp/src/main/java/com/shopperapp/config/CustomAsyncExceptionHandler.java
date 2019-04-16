package com.shopperapp.config;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(CustomAsyncExceptionHandler.class);

	@Override
	public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
		logger.info("CustomAsyncExceptionHandler", throwable);
//		log.error("CustomAsyncExceptionHandler", throwable);
	}
}
