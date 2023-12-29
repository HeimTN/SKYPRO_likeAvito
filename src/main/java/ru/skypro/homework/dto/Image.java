package ru.skypro.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;

@Data
@AllArgsConstructor
public class Image {
    private int id;
    private String filePath;
    private int fileSize;
    private String mediaType;
}
