package com.shopperapp.service;

import java.util.Date;

import org.springframework.http.ResponseEntity;

import com.shopperapp.dto.MessageResponseDTO;
import com.shopperapp.dto.RawMessageResponseDTO;


	public interface  JabongService {
		

		void readDataFromJabong(String decodedMail, String mailID, String value, Date date, String fromMail,
				String deviceId, RawMessageResponseDTO body, ResponseEntity<MessageResponseDTO> forEntity);
	}


