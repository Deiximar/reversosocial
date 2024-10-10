package com.reversosocial.service;

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
            @Value("${cloudinary.api_secret}") String apiSecret) {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));

        System.out.println("name: " + cloudName);
        System.out.println("key: " + apiKey);
        System.out.println("secret: " + apiSecret);
    }

    public String storeFile(MultipartFile file) {

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "raw"));
            String publicUrl = uploadResult.get("secure_url").toString();
            return publicUrl;
        } catch (Exception e) {
            throw new RuntimeException("Error al subir el archivo a Cloudinary", e);
        }
    }

}
