package com.example.fileapp.controller;

import com.example.fileapp.dto.DTOMapper;
import com.example.fileapp.dto.FileDTO;
import com.example.fileapp.exception.GlobalExceptionHandler;
import com.example.fileapp.model.FileEntity;
import com.example.fileapp.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;
    private final DTOMapper dtoMapper;

    public FileController(FileService fileService, DTOMapper dtoMapper) {
        this.fileService = fileService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFileMetaData(@PathVariable UUID id) {
        Optional<FileEntity> fileEntity = fileService.getFile(id);
        if (!fileEntity.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GlobalExceptionHandler().handleIllegalArgumentException(new IllegalArgumentException(HttpStatus.BAD_REQUEST.getReasonPhrase())));
        }

        if(fileEntity.get().getDeleted()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GlobalExceptionHandler().handleIllegalArgumentException(new IllegalArgumentException(HttpStatus.NOT_FOUND.getReasonPhrase())));
        }

        return ResponseEntity.ok(dtoMapper.convert(fileEntity.get(), FileDTO.class));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam(required = false) UUID folderId) {
        try {
            FileDTO savedFile = fileService.saveFile(file, folderId);
            return ResponseEntity.ok(savedFile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GlobalExceptionHandler().handleIllegalArgumentException(e));
        } catch (IOException e) {
            System.out.print(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GlobalExceptionHandler().handleIOException(e));
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable UUID id) throws IOException {
        return fileService.getFile(id)
                .map(fileEntity -> {
                    if(fileEntity.getDeleted()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GlobalExceptionHandler().handleIllegalArgumentException(new IllegalArgumentException(HttpStatus.NOT_FOUND.getReasonPhrase())));
                    }
                    try {
                        File file = new File(fileEntity.getFilepath());
                        byte[] content = Files.readAllBytes(file.toPath());

                        HttpHeaders headers = new HttpHeaders();
                        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileEntity.getFilename());

                        return new ResponseEntity<>(content, headers, HttpStatus.OK);
                    } catch (IOException e) {
                        return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable UUID id) {
        try {
            fileService.deleteFile(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(Map.of("message", "File moved to trash"));
    }

}
