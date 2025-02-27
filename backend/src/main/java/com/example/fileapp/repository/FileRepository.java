package com.example.fileapp.repository;

import com.example.fileapp.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, UUID> {
    Optional<FileEntity> findByHashAndFolderId(String hash, UUID folderId);
    Optional<FileEntity> findByFilenameAndFolderId(String filename, UUID folderId);
    List<FileEntity> findByFolderId(UUID folderId);
}