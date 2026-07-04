package me.smmukesh.ecommerceproject.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileService {

    public String uploadImage(String path, MultipartFile file) throws IOException {

        // 1. File name of the original file;
        String originalFileName = file.getOriginalFilename();

        // 2. Generate a unique file name (to avoid name conflict)
        String fileName = createUniqueId(originalFileName);
        String filePath = path + File.separator + fileName;

        // 3. Check if the path exists and creates.
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.mkdir();
        }

        // 4. upload the file to the server.
        Files.copy(file.getInputStream(), Path.of(filePath));

        // 5. Return the file name.
        return fileName;
    }

    public String createUniqueId(String originalFileName) {
        String id = UUID.randomUUID().toString();
        return id.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
    }
}
