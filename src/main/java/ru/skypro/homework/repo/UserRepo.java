package ru.skypro.homework.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.model.UserEntity;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByLogin(String login);

}
