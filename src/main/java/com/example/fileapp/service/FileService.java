package com.example.fileapp.service;

import com.example.fileapp.dto.DTOMapper;
import com.example.fileapp.dto.FileDTO;
import com.example.fileapp.model.FileEntity;
import com.example.fileapp.model.FolderEntity;
import com.example.fileapp.repository.FileRepository;
import com.example.fileapp.repository.FolderRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;

    private final FolderRepository folderRepository;

    @Value("${file.allowed-types}")
    private String[] allowedTypes;

    @Value("${file.max-size}")
    private long maxFileSize;

    private Path uploadPath;

    private final DTOMapper dtoMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileService(FileRepository fileRepository, FolderRepository folderRepository, DTOMapper dtoMapper) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.dtoMapper = dtoMapper;
    }

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        File directory = uploadPath.toFile();

        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Could not create upload directory: " + uploadPath);
        }
    }

    public FileDTO saveFile(MultipartFile file, UUID folderId) throws IOException {
        validateFile(file);

        Path targetPath = uploadPath;

        FolderEntity folder = null;
        if (folderId != null) {
            folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid folder ID"));
            targetPath = uploadPath.resolve(folderId.toString()); // Store inside folder
            Files.createDirectories(targetPath);
        }

        String fileHash = computeFileHash(file);

        Optional<FileEntity> existingFileOpt = fileRepository.findByFilenameAndFolderId(file.getOriginalFilename(),
                folderId);

        if (existingFileOpt.isPresent()) {
            FileEntity existingFile = existingFileOpt.get();

            // If the file content is the same, return existing file
            if (existingFile.getHash().equals(fileHash)) {
                return dtoMapper.convert(existingFile, FileDTO.class); // No need to overwrite if the file is identical
            }

            // If content changed, update the existing file record
            Path existingFilePath = Paths.get(existingFile.getFilepath());
            Files.deleteIfExists(existingFilePath); // Remove old file

            existingFile.setSize(file.getSize());
            existingFile.setUploadDate(LocalDateTime.now());
            existingFile.setHash(fileHash);

            Path filePath = targetPath.resolve(file.getOriginalFilename()).normalize();
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            existingFile.setFilepath(filePath.toString());

            return dtoMapper.convert(fileRepository.save(existingFile), FileDTO.class);
        }

        Path filePath = targetPath.resolve(file.getOriginalFilename()).normalize();
        file.transferTo(filePath.toFile());

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFilename(file.getOriginalFilename());
        fileEntity.setFilepath(filePath.toString());
        fileEntity.setSize(file.getSize());
        fileEntity.setHash(fileHash);
        fileEntity.setUploadDate(LocalDateTime.now());
        fileEntity.setMimeType(getMimeType(filePath));
        fileEntity.setFolder(folder);

        return dtoMapper.convert(fileRepository.save(fileEntity), FileDTO.class);
    }

    public List<FileEntity> listFiles() {
        return fileRepository.findAll();
    }

    public Optional<FileEntity> getFile(UUID id) {
        return fileRepository.findById(id);
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File exceeds maximum allowed size.");
        }

        // Get MIME type
        String mimeType = Files.probeContentType(Paths.get(file.getOriginalFilename()));
        if (mimeType == null || !Arrays.asList(allowedTypes).contains(mimeType)) {
            throw new IllegalArgumentException("Invalid file type: " + mimeType);
        }
    }

    private String computeFileHash(MultipartFile file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = file.getBytes();
            byte[] hashBytes = digest.digest(fileBytes);
            return HexFormat.of().formatHex(hashBytes); // Convert bytes to hex string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not generate hash", e);
        }
    }

    public void deleteFile(UUID id) throws IllegalArgumentException {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        
        file.setDeleted(true);
        fileRepository.save(file);
    }

    private String getMimeType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }    
}
