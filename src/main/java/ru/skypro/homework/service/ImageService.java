package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Image;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageService {


    void uploadImage(MultipartFile image, Path imagePath) throws IOException;

    byte[] getImageFromDisk(String path) throws IOException;

    Image getImageObject(String path);
}
