package com.example.fileapp.controller;

import com.example.fileapp.dto.DTOMapper;
import com.example.fileapp.dto.FileDTO;
import com.example.fileapp.dto.FolderDTO;
import com.example.fileapp.model.FolderEntity;
import com.example.fileapp.repository.FileRepository;
import com.example.fileapp.repository.FolderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<FolderDTO> createFolder(@RequestParam String name,
            @RequestParam(required = false) UUID parentFolderId) {
        FolderEntity folder = new FolderEntity();
        folder.setName(name);

        if (parentFolderId != null) {
            folder.setParentFolder(folderRepository.findById(parentFolderId).orElse(null));
        }

        FolderEntity savedFolder = folderRepository.save(folder);
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
        List<FolderDTO> folders = folderRepository.findByParentFolderId(id).stream()
        .map(folder -> dtoMapper.convert(folder, FolderDTO.class))
        .toList();

        List<FileDTO> files = fileRepository.findByFolderId(id).stream()
                .map(file -> dtoMapper.convert(file, FileDTO.class))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("folders", folders);
        response.put("files", files);

        return ResponseEntity.ok(response);
    }

}
