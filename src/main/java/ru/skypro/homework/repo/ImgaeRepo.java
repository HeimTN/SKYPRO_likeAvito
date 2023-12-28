package ru.skypro.homework.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.model.ImageEntity;
import ru.skypro.homework.service.ImageService;

public interface ImgaeRepo extends JpaRepository<ImageEntity, String> {
}
