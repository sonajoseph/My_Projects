package com.shopperapp.mysql.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopperapp.mysql.models.UserInformationModel;

@Repository
public interface UserInformationRepo extends JpaRepository<UserInformationModel, Long> {
}
