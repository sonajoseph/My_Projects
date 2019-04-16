package com.stressApp.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stressApp.model.StressData;
import com.stressApp.repository.DataRepo;
import com.stressApp.service.DataService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/stressdata")
public class DataController {
	private static final Logger logger = LoggerFactory.getLogger(DataController.class);
	@Autowired
	private DataService dataService;

	@Autowired
	DataRepo repo;

	@PostMapping("/store-data")

	public String storeStressData(@RequestBody List<String> stressData) throws ParseException {
		log.info("incoming Dataaaaaaaaaaaaaaaaaa--------------->   " + stressData);

		String stress = dataService.storeData(stressData);
		log.info("sress is  " + " " + stress);
		if (stress == null) {
			return "no content";
		} else {
			return "succesfully entered";
		}
	}
	
	
	
	
	@GetMapping("/get-data")
	public List<StressData> findAlldata() {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
		return dataService.findAll();
	}
	
	@GetMapping(value = "/csvdata", produces = "text/csv")
    public void generateCSV(HttpServletResponse response) throws IOException {

		System.err.println("for generating csv");
		dataService.generateCSV(dataService.findAll(), response);


    }

}
