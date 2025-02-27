package com.example.fileapp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "folders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FolderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_folder_id", referencedColumnName = "id")
    private FolderEntity parentFolder;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<FileEntity> files;
}
