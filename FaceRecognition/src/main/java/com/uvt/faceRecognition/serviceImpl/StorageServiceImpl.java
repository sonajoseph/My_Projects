package com.uvt.faceRecognition.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.map.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.uvt.faceRecognition.exception.StorageException;
import com.uvt.faceRecognition.service.StorageService;

import io.micrometer.core.ipc.http.HttpSender.Response;
 
@Service
public class StorageServiceImpl implements StorageService {

	private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

	@Autowired
	RestTemplate restTemplate;
	@Value("${videopath}")
	String videoPath;

	@Override
	public String store(MultipartFile file , String id) {
		logger.info("At store");
		
		File dir = new File(videoPath);
		if (!dir.exists())
			dir.mkdirs();

		System.out.println(videoPath);
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);

			}
			if (filename.contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				System.err.println("file.getOriginalFilename() ::: "+file.getOriginalFilename());
				System.err.println("+++++"+FilenameUtils.getExtension(file.getOriginalFilename()));
				filename=id+"."+FilenameUtils.getExtension(file.getOriginalFilename());
				String path = videoPath + filename;
				deleteFileIfExist(path);
				
				Path realPath = Paths.get(path);
				Files.copy(inputStream, realPath, StandardCopyOption.REPLACE_EXISTING);
				System.out.println(realPath);
				
				
				
				try {
//					String fileName = storageService.store(file);
		//
//					System.out.println(fileName);
		//
					// this.restTemplate =
					// restTemplateBuilder.errorHandler(restTemplateErrorHandler).build();
					// HttpHeaders headers = new HttpHeaders();
					// headers.add("Authorization","token");
					// HttpEntity<String> entity=new HttpEntity<>(headers);
					String api = "http://192.168.10.98:5000/Sachin_M.mp4/Sachin";
		
					ResponseEntity<Object> response = restTemplate.getForEntity(api, Object.class);
		
					// ResponseEntity<String> response = restTemplate.postForEntity( url, params,
					// String.class );
		
//					JSONPObject userDetails = (JSONPObject) response.getBody();
					
					System.out.println(response);
		
//					SigninResponse signinResponse = new SigninResponse("You successfully uploaded " + fileName);
		
//					System.err.println("********" + userDetails);
//					return new ResponseEntity<String>(null, HttpStatus.OK);
					 
				} catch (Exception se) {
					logger.error("storage error", se);
					System.err.println("storage error");
//					return new ResponseEntity<Response>(new Response("storage error"), HttpStatus.BAD_REQUEST);
					return "Failed";
				}
				
				
				
//				ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
//				newCachedThreadPool.submit(()->{
//					
//				});
			}
		} catch (IOException e) {
			logger.error("Failed to store file " + filename, e);
//			throw new StorageException("Failed to store file " + filename, e);
			return "Failed";
		}

		return filename;
	}

	public String deleteFileIfExist(String fileName) {
		try
        { 
            Files.deleteIfExists(Paths.get("fileName")); 
        } 
        catch(NoSuchFileException e) 
        { 
        	logger.info("No such file/directory exists"); 
        } 
        catch(DirectoryNotEmptyException e) 
        { 
        	logger.info("Directory is not empty."); 
        } 
        catch(IOException e) 
        { 
        	logger.info("Invalid permissions."); 
        } 
          
        logger.info("Deletion successful."); 
		
		return "success";
	}
	@Override
	public String delete(String fileName) {

		try {

			String path = videoPath + fileName;
			Path realPath = Paths.get(path);
			Files.deleteIfExists(realPath);
		} catch (NoSuchFileException e) {
			System.out.println("No such file/directory exists");
		} catch (DirectoryNotEmptyException e) {
			System.out.println("Directory is not empty.");
		} catch (IOException e) {
			System.out.println("Invalid permissions.");
		}

		System.out.println("Deletion successful.");
		return "Deletion successful.";
	}

}

 
