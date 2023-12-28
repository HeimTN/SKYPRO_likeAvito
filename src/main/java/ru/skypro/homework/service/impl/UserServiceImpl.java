package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;

import ru.skypro.homework.model.ImageEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repo.UserRepo;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserMapper;
import ru.skypro.homework.service.UserService;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.datical.liquibase.ext.init.InitProjectUtil.getExtension;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
@Service
@RequiredArgsConstructor
public class  UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final ImageService imageService;

    private final UserDetailsManager userDetailsManager;

    @Value("${path.to.image.folder}")
    private String pathFolder;


    public UserEntity getMe() {

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null && user.getUsername() != null) {
            return userRepo.findByLogin(user.getUsername()).orElseThrow(() -> new UsernameNotFoundException("No user in database found: " + user.getUsername()));
        }
        throw new UsernameNotFoundException("No such user");
    }

    @Override
    public User getMeDTO() {
        return userMapper.UserEntityToDTO(getMe());
    }

    @Override
    public void updPass(NewPassword newPassword) {
        logger.info("Hi in UserService in updPass()");
        userDetailsManager.changePassword(newPassword.getCurrentPassword(), newPassword.getNewPassword());
    }
    
    @Override
    public UpdateUser updUsr(UpdateUser updateUser) {
        int id = getMe().getId();
        UserEntity user = userRepo.getById(id);

        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());

        userRepo.save(user);

        return updateUser;
    }

    @Override
    public void updPhoto(MultipartFile photo) throws IOException {
        UserEntity user = getMe();
        user.setImage(savePhoto(photo));

        if (user.getImage() == null) {
            throw new IllegalArgumentException("No new image provided. User is not saved");
        }
        userRepo.save(user);
    }

    /**
     * Generates unique path for user picture and saves it via ImageService
     * @param photo photo file
     * @return path to photo at the HDD
     * @throws IOException
     * @see ImageServiceImpl
     */
    public ImageEntity savePhoto(MultipartFile photo) throws IOException{
        if(photo == null){
            return null;
        }

        UserEntity user = getMe();
        if (user == null) {
            return null;
        }

        Path filePath = Path.of(pathFolder, "user_" + user.getId() + "_avatar" + "." + getExtension(photo.getOriginalFilename()));

        return imageService.uploadImage(photo, filePath);
    }

    public byte[] getPhoto() {
        return null;
    }
}

