package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
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

    private final UserRepo userRepo;
    private final ImgaeRepo imgaeRepo;
    private final ImageMapper imageMapper;
    @Value("${path.to.image.folder}")
    private String pathFolder;

    @Override
    public void uploadImage(MultipartFile image, String pt) {

        if (image == null || pt == null) {
            return;
        }

        Path imagePath = Path.of(pathFolder, pt);

        try {
            Files.createDirectories(imagePath.getParent());
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file");
        }

        try (InputStream is = image.getInputStream();
             OutputStream os = Files.newOutputStream(imagePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {


            bis.transferTo(bos);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file");
        }

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ImageEntity entityToSave = new ImageEntity();
        entityToSave.setFilePath(pt);
        entityToSave.setFileSize((int) image.getSize());
        entityToSave.setMediaType(image.getContentType());
        entityToSave.setOwner(userRepo.findByLogin(user.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User is not registered")));

        imgaeRepo.save(entityToSave);
    }

    @Override
    public byte[] getImageFromDisk(String path) {
        String pathPrefixed = pathFolder + path;
        ImageEntity image = imgaeRepo.findById(path).orElseThrow(() -> new NotFoundException("No image found with path " + path));

        byte[] resultImage = new byte[image.getFileSize()];

        try (InputStream is = Files.newInputStream(Path.of(pathPrefixed))) {
            IOUtils.readFully(is, resultImage);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file");
        }
        return resultImage;
    }

    @Override
    public Image getImageObject(String path) {
        return imageMapper.imageEntityToDTO(imgaeRepo.findById(path).orElseThrow(() -> new NotFoundException("No image found with path " + path)));
    }
}
