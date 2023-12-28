package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repo.UserRepo;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserMapper;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.util.Constants;
import ru.skypro.homework.util.exceptions.NotFoundException;

import java.util.Objects;

import static com.datical.liquibase.ext.init.InitProjectUtil.getExtension;
@Service
@RequiredArgsConstructor
public class  UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final ImageService imageService;

    private final UserDetailsManager userDetailsManager;

    public UserEntity getMe() {

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null && user.getUsername() != null) {
            return userRepo.findByLogin(user.getUsername()).orElseThrow(() -> new UsernameNotFoundException("No user in database found: " + user.getUsername()));
        }
        throw new UsernameNotFoundException("No such user");
    }

    @Override
    public User getMeDTO() {
        UserEntity me = getMe();
        me.setImage(Constants.IMAGES_URL + me.getImage()); //Для того, что бы достать картинку по ссылке нужно к ней приклеить префикс (URL).
        return userMapper.UserEntityToDTO(me);
    }

    @Override
    public void updPass(NewPassword newPassword) {
        logger.info("Hi in UserService in updPass()");
        userDetailsManager.changePassword(newPassword.getCurrentPassword(), newPassword.getNewPassword());
    }
    
    @Override
    public UpdateUser updUsr(UpdateUser updateUser) {
        int id = getMe().getId();
        UserEntity user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("No user with id " + id));

        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());

        userRepo.save(user);

        return updateUser;
    }

    @Override
    public void updPhoto(MultipartFile photo) {
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
     * @see ImageServiceImpl
     */
    public String savePhoto(MultipartFile photo){
        if(photo == null){
            return null;
        }

        UserEntity user = getMe();
        if (user == null) {
            return null;
        }

        String filePath = "user_" + user.getId() + "_avatar" + "." + getExtension(Objects.requireNonNull(photo.getOriginalFilename()));

        imageService.uploadImage(photo, filePath);
        return filePath;
    }

}

