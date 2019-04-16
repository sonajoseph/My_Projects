package com.stressApp.serviceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import com.stressApp.model.StressData;
import com.stressApp.repository.DataRepo;
import com.stressApp.service.DataService;
@Service
public class DataServiceImpl implements DataService{
@Autowired
 private DataRepo dataRepo;
	
	@Override
	public String storeData(List<String> stressData) {

//		stressData.iterator().forEachRemaining(System.out::println);

		
		
		for (int i = 2; i < stressData.size(); ++i) {
			StressData data = new StressData();

			data.setUserId(stressData.get(1));
			//long to date;
			//string to long, long  to timestamp
			
			java.sql.Timestamp timeStamp = new Timestamp(Long.parseLong(stressData.get(i)));
			//timestamp to date
			java.util.Date date = new java.util.Date(timeStamp.getTime());
			
			data.setTimeStamp(date);

			data.setAccelerometer_x(stressData.get(++i));

			data.setAccelerometer_y(stressData.get(++i));

			data.setAccelerometer_z(stressData.get(++i));

			data.setHeartRate(stressData.get(++i));

		dataRepo.save(data);

		
	

}
		return "successfully entered";
	}

	

	

	@Override
	public List<StressData> findAll() {
		
		return dataRepo.findAll();
	}
	
	
	
	public void generateCSV(List<StressData> resultData, HttpServletResponse servletResponse) throws IOException {
		System.err.println("resulData is"+resultData);
		System.err.println("servlet response is"+servletResponse);
	
		
		servletResponse.setContentType("text/csv");
		servletResponse.setHeader("Content-Disposition", "attachment; filename=\"rawdata.csv\"");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(servletResponse.getWriter(), CsvPreference.EXCEL_PREFERENCE);
		String[] mainHeaders = { "id", "userId", "timeStamp", "accelerometer_x", "accelerometer_y",
				"accelerometer_z", "heartRate" };
		csvWriter.writeHeader(mainHeaders);
		System.err.println("result Data Size :" + resultData.size());
		resultData.forEach(data -> {
			PrintWriter writer;
			String[] headers = Arrays.asList(StressData.class.getDeclaredFields()).stream().map(Field::getName)
					.toArray(String[]::new);
			
			try {
				System.err.println("Discomfort Data -> " + data);
				
				csvWriter.write(data, headers);
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		try {
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	 
	

}

