package ru.skypro.homework.service;

import org.junit.jupiter.api.*;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;

import java.util.List;


class UserMapperTest {
    private static UserMapper mapper;

    private static UserEntity source = new UserEntity(1, "lolka@bolka.ru", "ya.ru/kartinka", "Lolek", "Bolek", "88005553535", Role.USER, "Qwerty123", List.of(new AdEntity(), new AdEntity()));
    private static User sourceDTO = new User(1, "lolka@bolka.ru", "Lolek", "Bolek", "88005553535", Role.USER.name(), "ya.ru/kartinka");


    @BeforeAll
    static void setUp() {
       mapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void userEntityConvertsToDTO() {


        User DTO = mapper.UserEntityToDTO(source);

        Assertions.assertNotNull(DTO);
        Assertions.assertEquals(1, DTO.getId());
        Assertions.assertEquals("ya.ru/kartinka", DTO.getImage());
        Assertions.assertEquals("lolka@bolka.ru", DTO.getEmail());
        Assertions.assertEquals("Lolek", DTO.getFirstName());
        Assertions.assertEquals("Bolek", DTO.getLastName());
        Assertions.assertEquals("88005553535", DTO.getPhone());
        Assertions.assertEquals(Role.USER.name(), DTO.getRole());

    }
}