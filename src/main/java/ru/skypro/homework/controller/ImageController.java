package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.Image;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.util.Constants;

import java.io.IOException;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;


    @GetMapping(value = Constants.IMAGES_URL + "{path}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> getPhoto(@PathVariable(name = "path") String path) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        Image image = imageService.getImageObject(path);

        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
        headers.setContentLength(image.getFileSize());

        return ResponseEntity.status(200).headers(headers).body(imageService.getImageFromDisk(path));
    }

}
