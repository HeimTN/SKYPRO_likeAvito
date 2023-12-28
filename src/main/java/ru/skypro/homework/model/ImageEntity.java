package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "images")
public class ImageEntity {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "path")
    private String filePath;
    @Column(name = "size")
    private int fileSize;
    @Column(name = "media_type")
    private String mediaType;
    @ManyToOne
    @JoinColumn(name = "owner")
    private UserEntity owner;
}
