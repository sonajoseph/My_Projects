package com.shopperapp.service;

import java.util.Date;

import org.springframework.http.ResponseEntity;

import com.shopperapp.dto.MessageResponseDTO;
import com.shopperapp.dto.RawMessageResponseDTO;

public interface OpenTasteService {

	void readDataFromOpenTaste(String tomailID, String subjectData, Date date, String fromMail, String deviceId,
			RawMessageResponseDTO body, ResponseEntity<MessageResponseDTO> forEntity);

}
