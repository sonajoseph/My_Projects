package com.reception.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "category")
public class Category {

    @Id
	
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int categoryId;
	private String CategoryName;
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return CategoryName;
	}
	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}
	public Category() {
		super();
	}
	public Category(int categoryId, String categoryName) {
		super();
		this.categoryId = categoryId;
		this.CategoryName = categoryName;
	}
	
	
	
	

}
