package com.example.fileapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String filename;
    private String filepath;
    private Long size;
    private LocalDateTime uploadDate;
    private String hash;
    private Boolean deleted = false;
    private String mimeType;

    @ManyToOne
    @JoinColumn(name = "folder_id", referencedColumnName = "id")
    @JsonBackReference
    private FolderEntity folder;
}
