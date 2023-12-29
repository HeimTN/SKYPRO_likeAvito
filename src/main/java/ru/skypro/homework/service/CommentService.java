package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.User;

public interface CommentService {
    Comments getComments(Integer id);
    Comment createComment(CreateOrUpdateComment createOrUpdateComment, Integer id, Authentication authentication);
    void deleteComment(Integer adId, Integer commentId);
    Comment updateComment(Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment, Authentication authentication);
}
