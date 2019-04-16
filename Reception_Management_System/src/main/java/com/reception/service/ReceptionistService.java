package com.reception.service;

import com.reception.dto.ReceptionistDTO;
import com.reception.dto.VisitorDto;
import com.reception.model.Receptionist;


public interface ReceptionistService {
	
	Receptionist save(Receptionist receptionist);
	void saveReceptionist(ReceptionistDTO receptionist);

}
