package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repo.CommentRepository;
import ru.skypro.homework.service.impl.CommentServiceImpl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentServiceImpl commentService;
    Comment comment = new Comment();
    Integer id;
    LocalDateTime now = LocalDateTime.now();
    long milliseconds = now.toInstant(ZoneOffset.UTC).toEpochMilli();
    CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
    private CommentEntity commentEntity = new CommentEntity();

    UserEntity userEntity;
    AdEntity adEntity;
    Collection<AdEntity> ads;
    CommentMapper commentMapper = new CommentMapperImpl();
    Collection<CommentEntity> collection = new ArrayList<>();

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        commentEntity.setId(id);
        commentEntity.setCreatedAt(milliseconds);
        commentEntity.setText("Test for comment");
        adEntity = new AdEntity(1,"image_path",40, "title", "description",userEntity, collection);
        userEntity = new UserEntity(1,"anna@mail.ru","image_path", "Anna", "Bel", "+71234567889",Role.USER,"asdfgh",ads);
        commentEntity.setAuthor(userEntity);

    }
    @Test
    public void shouldCheckCommentNotNullWhenCreated() {
        when(commentRepository.save(commentEntity)).thenReturn(commentEntity);
        assertNotNull(comment);
    }
    @Test
    public void testCreateComment() {
        when(commentRepository.save(commentEntity)).thenReturn(commentEntity);
        comment = commentMapper.commentEntityToCommentDTO(commentEntity);
        assertEquals("Anna", comment.getAuthorFirstName());
        assertEquals("Test for comment", comment.getText());
    }
    @Test
    public void testUpdateComment() {
        commentEntity.setAuthor(userEntity);
        commentEntity.setText("new text");
        comment = commentMapper.commentEntityToCommentDTO(commentEntity);
        assertEquals("new text", comment.getText());
    }
}
