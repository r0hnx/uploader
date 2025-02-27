package com.example.fileapp.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FileDTO {
    private UUID id;
    private String filename;
    private Long size;
    private LocalDateTime uploadDate;
    private String mimeType;
}