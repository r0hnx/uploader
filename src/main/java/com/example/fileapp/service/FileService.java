package com.example.fileapp.service;

import com.example.fileapp.model.FileEntity;
import com.example.fileapp.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        new File(uploadDir).mkdirs(); // Ensure upload directory exists
    }

    public FileEntity saveFile(MultipartFile file) throws IOException {
        String filePath = uploadDir + "/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFilename(file.getOriginalFilename());
        fileEntity.setFilepath(filePath);
        fileEntity.setSize(file.getSize());
        fileEntity.setUploadDate(LocalDateTime.now());

        return fileRepository.save(fileEntity);
    }

    public List<FileEntity> listFiles() {
        return fileRepository.findAll();
    }

    public Optional<FileEntity> getFile(Long id) {
        return fileRepository.findById(id);
    }
}
