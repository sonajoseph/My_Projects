package com.shopperapp.mysql.serviceimpl;

import com.shopperapp.mysql.models.UserInformationModel;
import com.shopperapp.mysql.repo.UserInformationRepo;
import com.shopperapp.mysql.service.UserInformationService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class UserInformationServiceImpl implements UserInformationService {
	private UserInformationRepo userInformationRepo;
	@Override
	public void storeUserData(UserInformationModel informationModel) {
		UserInformationModel save = userInformationRepo.save(informationModel);
		
		
	}

}
