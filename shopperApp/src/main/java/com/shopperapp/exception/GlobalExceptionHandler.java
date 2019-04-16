package com.shopperapp.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.shopperapp.dto.ResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(FailedToSave.class)
	public @ResponseBody ResponseDTO<String> invalidAuthData(HttpServletRequest request, FailedToSave ex) {
		logger.info("At invalidAuthData...");
		
		ResponseDTO<String> responseDTO = new ResponseDTO<>();
		responseDTO.setMessage(ex.getMessage());
		responseDTO.setData(ex.getMessage());
		responseDTO.setStatus(false);
		return responseDTO;
	}

}
