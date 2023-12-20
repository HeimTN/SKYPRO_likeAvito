package ru.skypro.homework.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * DTO and Entity fields are identical, no need to set up mapping via annotations
     * @param user UserEntity from DB
     * @return User DTO without password and Ads Collection
     */
    User UserEntityToDTO(UserEntity user);

    /**
     *
     * @param user UserDTO
     * @return User entity with null password, login and ads
     */
    UserEntity DtoToEntity(User user);




}