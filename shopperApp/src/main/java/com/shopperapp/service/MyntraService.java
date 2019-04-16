package com.shopperapp.service;

import java.util.Date;

import org.springframework.http.ResponseEntity;

import com.shopperapp.dto.MessageResponseDTO;
import com.shopperapp.dto.OrderDetail;
import com.shopperapp.dto.RawMessageResponseDTO;

public interface  MyntraService {
	 void readDataFromMyntra(String decodedMail, String toMailId, String subjectData, Date date,
			String fromMail, String deviceId, RawMessageResponseDTO rawMessageResponseDTO,ResponseEntity<MessageResponseDTO> forEntity) ;
}
