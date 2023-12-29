package ru.skypro.homework.service;

import org.mapstruct.Mapper;
import ru.skypro.homework.dto.Image;
import ru.skypro.homework.model.ImageEntity;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    Image imageEntityToDTO(ImageEntity imageEntity);
}
