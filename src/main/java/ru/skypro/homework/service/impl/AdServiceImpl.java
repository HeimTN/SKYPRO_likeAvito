package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repo.AdRepository;
import ru.skypro.homework.repo.UserRepo;
import ru.skypro.homework.service.AdMapper;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.WebSecurityService;
import ru.skypro.homework.util.Constants;
import ru.skypro.homework.util.exceptions.FileNotFoundException;
import ru.skypro.homework.util.exceptions.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.datical.liquibase.ext.init.InitProjectUtil.getExtension;


/**
 * Класс {@code AdServiceImpl} предоставляет реализацию интерфейса {@link AdService},
 * предоставляя функциональность для управления рекламными объявлениями в системе.
 *
 * <p>Инъекции зависимостей:
 * <ul>
 *     <li>{@code repository} - репозиторий для взаимодействия с данными рекламных объявлений.</li>
 *     <li>{@code mapper} - маппер для преобразования между сущностями и DTO рекламных объявлений.</li>
 * </ul>
 *
 * <p>Методы сервиса:</p>
 * <ul>
 *     <li>{@code getAllAds} - возвращает все рекламные объявления в виде объекта {@link Ads}.</li>
 *     <li>{@code createAd} - создает новое рекламное объявление на основе данных из объекта
 *     {@link CreateOrUpdateAd}, изображения и идентификатора пользователя. Возвращает созданное объявление.</li>
 *     <li>{@code getExtAd} - возвращает расширенную информацию о рекламном объявлении по его идентификатору.</li>
 *     <li>{@code deleteAd} - удаляет рекламное объявление по его идентификатору. Возвращает удаленное объявление.</li>
 *     <li>{@code pathAd} - обновляет рекламное объявление с использованием данных из объекта {@link CreateOrUpdateAd}
 *     по его идентификатору. Возвращает обновленное объявление.</li>
 *     <li>{@code getAllAdsForUser} - возвращает все рекламные объявления для указанного пользователя
 *     в виде объекта {@link Ads}.</li>
 *     <li>{@code pathImageAd} - обновляет изображение рекламного объявления по его идентификатору.
 *     Возвращает путь к обновленному изображению.</li>
 * </ul>
 *
 * <p>Этот класс является компонентом Spring с аннотацией {@link org.springframework.stereotype.Service},
 * предоставляющим бизнес-логику для управления рекламными объявлениями в системе.</p>
 *
 * @author Michail Z. (GH: HeimTN)
 */
@Service
public class AdServiceImpl implements AdService {
    private final AdRepository repository;
    private final UserRepo userRepository;
    private final AdMapper mapper;
    private final WebSecurityService webSecurityService;
    private final ImageService imageService;

    @Value("${path.to.image.folder}")
    private String uploadDir;

    public AdServiceImpl(AdRepository repository, AdMapper mapper, UserRepo userRepository, WebSecurityServiceImpl webSecurityService,
                         ImageService imageService){
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.webSecurityService = webSecurityService;
        this.imageService = imageService;
    }


    @Override
    public Ads getAllAds() {
        Collection<AdEntity> result = repository.findAll();
        if(result.isEmpty()){
            throw new NotFoundException("Обьявления не найдены");
        }
        result.forEach(adEntity -> adEntity.setImage(Constants.IMAGES_URL + adEntity.getImage()));
        return mapper.adToDtoList(result);
    }

    @Override
    public Ad createAd(CreateOrUpdateAd ad, MultipartFile image) {
        AdEntity result;
        result = mapper.dtoToAd(ad);
        result.setAuthor(userRepository.findByLogin(getMe()).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")));
        result = repository.save(result);
        if(result == null){
            throw new RuntimeException("Обьявление не созданно");
        }
        result.setImage(savePhoto(image, result.getId()));

        return mapper.adToDto(repository.save(result));
    }

    @Override
    public ExtendedAd getExtAd(Integer id) {
        AdEntity result = repository.findById(id).orElse(null);
        if(result == null){
            return null;
        }
        result.setImage(Constants.IMAGES_URL + result.getImage());
        return mapper.adToExtDto(result);
    }

    @PreAuthorize("@webSecurityServiceImpl.canAccessInAd(#id, principal.username) or hasRole('ADMIN')")
    @Override
    public Ad deleteAd(@Param("id")Integer id) {
        AdEntity result = repository.findById(id).orElse(null);
        if(result == null){
            return null;
        }
        repository.deleteById(id);
        return mapper.adToDto(result);
    }

    @PreAuthorize("@webSecurityServiceImpl.canAccessInAd(#id, principal.username) or hasRole('ADMIN')")
    @Override
    public Ad pathAd(CreateOrUpdateAd ad, Integer id) {
        AdEntity result = repository.findById(id).orElse(null);
        if(result == null){
            return null;
        }
        result.setDescription(ad.getDescription());
        result.setTitle(ad.getTitle());
        result.setPrice(ad.getPrice());
        return mapper.adToDto(repository.save(result));
    }

    @Override
    public Ads getAllAdsForUser() {
        Collection<AdEntity> result = repository.findAdEntitiesByAuthor(userRepository.findByLogin(getMe()).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")));
        result.forEach(adEntity -> adEntity.setImage(Constants.IMAGES_URL + adEntity.getImage()));
        return mapper.adToDtoList(result);
    }

    @Override
    public void pathImageAd(Integer id, MultipartFile image) {
        AdEntity ad = repository.findById(id).orElseThrow(() -> new NotFoundException("Обьяфление не найдено"));
        ad.setImage(savePhoto(image, id));

        if (ad.getImage() == null) {
            throw new IllegalArgumentException("No new image provided. User is not saved");
        }
        repository.save(ad);
    }

    private String getMe(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    public String savePhoto(MultipartFile photo, Integer id){
        if(photo == null){
            return null;
        }
        AdEntity ad = repository.findById(id).orElseThrow(() -> new NotFoundException("Обьявление не найдено"));

        String filePath = "ad_" + ad.getId() + "_avatar" + "." + getExtension(Objects.requireNonNull(photo.getOriginalFilename()));

        imageService.uploadImage(photo, filePath);
        return filePath;
    }
}
