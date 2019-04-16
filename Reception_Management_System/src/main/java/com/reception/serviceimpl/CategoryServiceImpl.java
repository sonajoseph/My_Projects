package com.reception.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reception.model.Category;
import com.reception.repository.CategoryRepository;
import com.reception.service.CategoryService;
@Service
public class CategoryServiceImpl implements CategoryService{
 @Autowired 
 private CategoryRepository categoryRepo;

@Override
public List<Category> findAll() {
	
	return categoryRepo.findAll();
}
	
	

}
