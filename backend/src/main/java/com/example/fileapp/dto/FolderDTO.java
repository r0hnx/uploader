package com.example.fileapp.dto;

import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FolderDTO {
    private UUID id;
    private String name;
}