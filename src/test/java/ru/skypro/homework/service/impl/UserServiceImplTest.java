package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repo.UserRepo;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserMapper;
import ru.skypro.homework.util.Constants;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    @Mock
    UserEntity exampleMockEntity = new UserEntity(1, "lolka@bolka.ru", "ya.ru/kartinka", "Lolek", "Bolek", "88005553535", Role.USER, "Qwerty123", List.of(new AdEntity(), new AdEntity()));
    UserEntity exampleEntity = new UserEntity(1, "lolka@bolka.ru", "ya.ru/kartinka", "Lolek", "Bolek", "88005553535", Role.USER, "Qwerty123", List.of(new AdEntity(), new AdEntity()));
    User exampleDTO = new User(1, "lolka@bolka.ru", "Lolek", "Bolek", "88005553535", Role.USER.name(), "ya.ru/kartinka");

    @Mock
    MultipartFile photoMock;


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
        Assertions.assertInstanceOf(UserEntity.class, userService.getMe());
    }

    @Test
    @WithMockUser
    void getMeThrowsExceptionIfNotFound() {

        when(userRepo.findByLogin(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.getMe());
    }
    @Test
    @WithMockUser
    void getMeDTOReturnsDTO() {

        when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(exampleEntity));
        when(userMapper.UserEntityToDTO(any(UserEntity.class))).thenReturn(exampleDTO);
        Assertions.assertInstanceOf(User.class, userService.getMeDTO());
    }

    @Test
    @WithMockUser
    void getMeDTOChangesURL() {

        when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(exampleMockEntity));
        when(userMapper.UserEntityToDTO(any(UserEntity.class))).thenReturn(exampleDTO);
        Assertions.assertEquals(exampleDTO, userService.getMeDTO());
        verify(exampleMockEntity).setImage(contains(Constants.IMAGES_URL));
    }

    @Test
    void updPassCallsUserDetailsManagerToUpdatePassword() {
        NewPassword examplePasswordDTO = new NewPassword();
        examplePasswordDTO.setNewPassword("ASdASd");
        examplePasswordDTO.setCurrentPassword("DSDSDSDSDDS");
        userService.updPass(examplePasswordDTO);
        verify(userDetailsManager).changePassword(examplePasswordDTO.getCurrentPassword(), examplePasswordDTO.getNewPassword());

    }

    @Test
    @WithMockUser
    void updUsrReturnsUpdate() {
        when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(exampleEntity));
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(exampleEntity));
        UpdateUser exampleUpdUser = new UpdateUser();
        exampleUpdUser.setFirstName("Lolka");
        exampleUpdUser.setLastName("Bolka");
        exampleUpdUser.setPhone("+78005553535");
        Assertions.assertEquals(exampleUpdUser, userService.updUsr(exampleUpdUser));
    }

    @Test
    @WithMockUser
    void updUsrSevesUpdatedUser() {
        when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(exampleEntity));
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(exampleEntity));
        UpdateUser exampleUpdUser = new UpdateUser();
        exampleUpdUser.setFirstName("Lolka");
        exampleUpdUser.setLastName("Bolka");
        exampleUpdUser.setPhone("+78005553535");

        userService.updUsr(exampleUpdUser);
        verify(userRepo).save(exampleEntity);
        Assertions.assertEquals(exampleEntity.getFirstName(), exampleUpdUser.getFirstName());
        Assertions.assertEquals(exampleEntity.getLastName(), exampleUpdUser.getLastName());
        Assertions.assertEquals(exampleEntity.getPhone(), exampleUpdUser.getPhone());
    }

    @Test
    @WithMockUser
    void updPhotoSavesNewPhoto() {
        when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(exampleEntity));
        when(photoMock.getOriginalFilename()).thenReturn("Kartinochka.jpeg");

        userService.updPhoto(photoMock);
        verify(userRepo).save(exampleEntity);
        Assertions.assertTrue(exampleEntity.getImage().contains("user_" + exampleEntity.getId()) && exampleEntity.getImage().contains("_avatar.jpeg"));
    }

    @Test
    @WithMockUser
    void updPhotoThrosExceptionWhenIncorrectPhotoProvided() {
        when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(exampleEntity));
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.updPhoto(any()));
    }

    @Test
    @WithMockUser
    void savePhotoSavesImageWithCorrectName() {
        when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(exampleEntity));
        when(photoMock.getOriginalFilename()).thenReturn("Kartinochka.jpeg");
        String path = "user_" + exampleEntity.getId() + "_avatar.jpeg";
        Assertions.assertEquals(path, userService.savePhoto(photoMock));
        verify(imageService).uploadImage(photoMock, path);

    }
}