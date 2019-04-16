package com.stressApp.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.stressApp.model.StressData;

public interface DataService {

	String storeData(List<String> stressData);
	public  List<StressData>findAll();
	public void generateCSV(List<StressData> resultData, HttpServletResponse servletResponse) throws IOException;	
//	public void getCsv(List<StressData> resultData, HttpServletResponse servletResponse) throws IOException;
}
