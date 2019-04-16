package com.reception.exception;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
@ControllerAdvice
public class GlobalException {
//	@ResponseStatus(HttpStatus.CONFLICT)
//	@ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
//	public @ResponseBody ResponseComponent<Object> handleSQLException(HttpServletRequest request, Exception ex) {
//		String message = ex.getMessage();
////		ex.printStackTrace();
//		if (message.contains("duplicate key error collection")) {
//			
//			return new ResponseComponent<>("Username alredy taken", "", false); //response with a message response anfd flag
//		}
//		return new ResponseComponent<>("Someting went wrong.Please Refresh your webpage Or Contact Admin","",false);
//	}
	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
	public @ResponseBody ResponseComponent<Object> duplicate(HttpServletRequest request, Exception ex) {
		
		String message = ex.getMessage();
		System.err.println("-------------------------------------------------------------S" );
		ex.printStackTrace();
		
		return new ResponseComponent<>("This name is already taken","",false);
	}
	@ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(Exception.class)
	
	
	public @ResponseBody ResponseComponent<Object> exception(HttpServletRequest request, Exception ex) {
		
		String message = ex.getMessage();
		System.err.println("-------------------------------------------------------------B" + message);
		ex.printStackTrace();
		
		return new ResponseComponent<>(ex.getMessage(),"",false);
	}
	
	
}
