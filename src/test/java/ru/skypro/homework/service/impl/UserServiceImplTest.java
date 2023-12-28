package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repo.UserRepo;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepo userRepo;
    @Mock
    ImageService imageService;
    @Mock
    UserMapper userMapper;
    @Mock
    UserDetailsManager userDetailsManager;


    UserEntity exampleEntity = new UserEntity(1, "lolka@bolka.ru", "ya.ru/kartinka", "Lolek", "Bolek", "88005553535", Role.USER, "Qwerty123", List.of(new AdEntity(), new AdEntity()));
    User exampleDTO = new User(1, "lolka@bolka.ru", "Lolek", "Bolek", "88005553535", Role.USER.name(), "ya.ru/kartinka");

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepo, userMapper, imageService, userDetailsManager);
    }

    @Test
    @Order(0)
    void mocksAreCreated() {
        Assertions.assertTrue(mockingDetails(userRepo).isMock());
    }

    @Test
    @WithMockUser
    @Order(1)
    void getMeReturnsUserEntity() {

        when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(exampleEntity));

        UserEntity user = userService.getMe();

        System.err.println(userService);
        System.err.println(user);

        Assertions.assertInstanceOf(UserEntity.class, user);
    }

    @Test
    void getMeDTO() {
    }

    @Test
    void updPass() {
    }

    @Test
    void updUsr() {
    }

    @Test
    void updPhoto() {
    }

    @Test
    void savePhoto() {
    }
}