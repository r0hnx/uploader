package com.example.fileapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Use UUID as primary key
    private UUID id;

    private String filename;
    private String filepath;
    private Long size;
    private LocalDateTime uploadDate;
    private String hash;
}
