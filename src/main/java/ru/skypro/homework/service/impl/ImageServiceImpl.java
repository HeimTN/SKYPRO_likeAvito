package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Image;
import ru.skypro.homework.model.ImageEntity;
import ru.skypro.homework.repo.ImgaeRepo;
import ru.skypro.homework.repo.UserRepo;
import ru.skypro.homework.service.ImageMapper;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.util.exceptions.NotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private UserDetails user;
    private final UserRepo userRepo;
    private final ImgaeRepo imgaeRepo;
    private final ImageMapper imageMapper;

    @Override
    public void uploadImage(MultipartFile image, Path imagePath) throws IOException {

        if (image == null || imagePath == null) {
            return;
        }

        Files.createDirectories(imagePath.getParent());
        Files.deleteIfExists(imagePath);
        try (InputStream is = image.getInputStream();
             OutputStream os = Files.newOutputStream(imagePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }
        user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ImageEntity entityToSave = new ImageEntity();
        entityToSave.setFilePath(imagePath.toString());
        entityToSave.setFileSize(image.getBytes().length);
        entityToSave.setMediaType(image.getContentType());
        entityToSave.setOwner(userRepo.findByLogin(user.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User is not registered")));

        imgaeRepo.save(entityToSave);
    }

    @Override
    public byte[] getImageFromDisk(String path) throws IOException {
        ImageEntity image = imgaeRepo.findById(path).orElseThrow(() -> new NotFoundException("No image found with path " + path));

        byte[] resultImage = new byte[image.getFileSize()];

        InputStream is = Files.newInputStream(Path.of(path));
        IOUtils.readFully(is, resultImage);

        return resultImage;
    }

    @Override
    public Image getImageObject(String path) {
        return imageMapper.imageEntityToDTO(imgaeRepo.findById(path).orElseThrow(() -> new NotFoundException("No image found with path " + path)));
    }
}
