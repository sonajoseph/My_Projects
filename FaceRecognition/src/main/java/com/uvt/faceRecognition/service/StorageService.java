package com.uvt.faceRecognition.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	String store(MultipartFile file, String id);

	String delete(String fileName);
}
