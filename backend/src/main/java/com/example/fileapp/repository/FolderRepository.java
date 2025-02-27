package com.example.fileapp.repository;

import com.example.fileapp.model.FolderEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, UUID> {
    List<FolderEntity> findByParentFolderId(UUID parentFolderId);
}
