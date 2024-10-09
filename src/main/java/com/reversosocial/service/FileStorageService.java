package com.reversosocial.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageService(
        @Value("${cloudinary.cloud_name}") String cloudName,
        @Value("${cloudinary.api_key}") String apiKey,
        @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret,
            "secure", true
        ));
    }

    public String storeFile(MultipartFile file) {
        try {
            HashMap<Object, Object> options = new HashMap<>();
            options.put("file", "test");
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            String errorMessage = "Error cloudinary: " + e.getMessage();
            throw new RuntimeException(errorMessage, e);
        }
    }
    
}
