package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Image;

import java.io.IOException;

public interface ImageService {


    void uploadImage(MultipartFile image, String imagePath);

    byte[] getImageFromDisk(String path) throws IOException;

    Image getImageObject(String path);
}
