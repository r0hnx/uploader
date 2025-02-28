package com.example.fileapp.controller;

import com.example.fileapp.dto.DTOMapper;
import com.example.fileapp.dto.FileDTO;
import com.example.fileapp.dto.FolderDTO;
import com.example.fileapp.model.FolderEntity;
import com.example.fileapp.repository.FileRepository;
import com.example.fileapp.repository.FolderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/folders")
public class FolderController {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final DTOMapper dtoMapper;

    public FolderController(FolderRepository folderRepository, FileRepository fileRepository, DTOMapper dtoMapper) {
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<FolderDTO> createFolder(@RequestBody Map<String, String> requestData) {
        String folderName = requestData.get("name");
        String parentFolderIdStr = requestData.get("parentFolderId");

        UUID parentFolderId = null;
        if (parentFolderIdStr != null && !parentFolderIdStr.isEmpty()) {
            parentFolderId = UUID.fromString(parentFolderIdStr);
        }

        FolderEntity parentFolder = null;
        if (parentFolderId != null) {
            parentFolder = folderRepository.findById(parentFolderId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent folder not found"));
        }

        FolderEntity newFolder = new FolderEntity();
        newFolder.setName(folderName);
        newFolder.setParentFolder(parentFolder);

        FolderEntity savedFolder = folderRepository.save(newFolder);
        return ResponseEntity.ok(dtoMapper.convert(savedFolder, FolderDTO.class));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listRootContents() {
        List<FolderDTO> rootFolders = folderRepository.findByParentFolderId(null).stream()
                .map(folder -> dtoMapper.convert(folder, FolderDTO.class))
                .toList();

        List<FileDTO> rootFiles = fileRepository.findByFolderId(null).stream()
                .map(file -> dtoMapper.convert(file, FileDTO.class))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("folders", rootFolders);
        response.put("files", rootFiles);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFolderContents(@PathVariable UUID id) {
        FolderEntity folder = folderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        List<FolderDTO> folders = folderRepository.findByParentFolderId(id).stream()
                .map(f -> dtoMapper.convert(f, FolderDTO.class))
                .toList();

        List<FileDTO> files = fileRepository.findByFolderId(id).stream()
                .map(file -> dtoMapper.convert(file, FileDTO.class))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("path", getFolderPath(folder));
        response.put("name", folder.getName());
        response.put("folders", folders);
        response.put("files", files);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/breadcrumb")
    public ResponseEntity<Deque<FolderDTO>> getFolderBreadcrumb(@PathVariable UUID id) {
        Deque<FolderDTO> breadcrumb = new ArrayDeque<>();
        FolderEntity folder = folderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));

        while (folder != null) {
            breadcrumb.addFirst(dtoMapper.convert(folder, FolderDTO.class));
            folder = folder.getParentFolder();
        }

        return ResponseEntity.ok(breadcrumb);
    }

    private String getFolderPath(FolderEntity folder) {
        String root = "/";
        if (folder.getParentFolder() == null)
            return root + folder.getName();
        return getFolderPath(folder.getParentFolder()) + "/" + folder.getName();
    }

}
