package com.reception.config;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reception.model.Category;
import com.reception.model.Role;
import com.reception.repository.CategoryRepository;
import com.reception.repository.RoleRepository;

@Component
public class DataInsertComponent {
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private CategoryRepository categoryRepo;
	@PostConstruct
	public void checkDataInRoleTable() {
		ArrayList<Role> list = new ArrayList<>();
		if(roleRepo.findAll().isEmpty()){
			list.add(new Role(1, "ADMIN"));
			list.add(new Role(2, "RECEPTIONIST"));
			list.add(new Role(3, "HOST"));
			list.add(new Role(4, "VISITOR"));
			roleRepo.save(list);
		}
	}
		
		@PostConstruct
		public void checkDataInCategoryTable() {
			ArrayList<Category> list = new ArrayList<>();
			if(categoryRepo.findAll().isEmpty()){
				list.add(new Category(1,"MEETSTAFF"));
				list.add(new Category(2,"EVENTS"));
				list.add(new Category(3,"COURIER"));
				categoryRepo.save(list);
				
				
			}
		
		
	}
}
