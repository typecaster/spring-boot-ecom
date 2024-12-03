package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        //File names of the current / original file.
        String originalFilename = file.getOriginalFilename();

        //Generate a unique file name
        String randomUUID = UUID.randomUUID().toString();
        assert originalFilename != null;
        String fileName = randomUUID.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));
        String filePath = path + File.separator + fileName;

        //Check if path exists or not. If not, create a new directory.
        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdir();
        }

        //Upload the file to the server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        //return the file name of the uploaded file
        return fileName;
    }
}
