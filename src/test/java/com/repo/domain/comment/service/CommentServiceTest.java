package com.repo.domain.comment.service;

import com.repo.domain.comment.dto.CommentMapResponseDto;
import com.repo.domain.comment.dto.CommentRequestDto;
import com.repo.domain.comment.dto.CommentResponseDto;
import com.repo.domain.comment.entity.Comment;
import com.repo.domain.comment.repository.CommentRepository;
import com.repo.domain.post.entity.Post;
import com.repo.domain.post.repository.PostRepository;
import com.repo.domain.user.entity.User;
import com.repo.domain.user.service.UserService;
import com.repo.exception.BusinessException;
import com.repo.exception.ErrorCode;
import com.repo.security.principal.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Post post;
    private Comment comment;
    private UserPrincipal userPrincipal;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testUser", "태스터", "password", User.Authorities.USER, User.Status.NORMAL);
        post = new Post(1L, "Test Post Title", "Test Post Content", user, 0L);
        comment = Comment.builder().id(1L).post(post).user(user).contents("태스트 지문").build();
        userPrincipal = new UserPrincipal(user);
        commentRequestDto = new CommentRequestDto(null, "태스트 지문");
        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .userId(1L)
                .nickname("태스터")
                .contents("태스트 지문")
                .build();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createCommentTest() {
        // Given
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        CommentResponseDto response = commentService.create(1L, userPrincipal, commentRequestDto);

        // Then
        assertThat(response.getContents()).isEqualTo(commentRequestDto.getContents());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateCommentTest() {
        // Given
        when(userService.getUser(anyLong())).thenReturn(user);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // When
        CommentResponseDto response = commentService.update(1L, 1L, 1L, commentRequestDto);

        // Then
        assertThat(response.getContents()).isEqualTo(commentRequestDto.getContents());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteCommentTest() {
        // Given
        when(userService.getUser(anyLong())).thenReturn(user);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // When
        commentService.delete(1L, 1L, 1L);

        // Then
        verify(commentRepository, times(1)).delete(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 조회 테스트")
    void readCommentTest() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // When
        CommentResponseDto response = commentService.read(1L, 1L, 1L);

        // Then
        assertThat(response.getId()).isEqualTo(comment.getId());
    }

    @Test
    @DisplayName("댓글 목록 조회 테스트")
    void readCommentsTest() {
        // Given
        List<Comment> comments = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(comments);
        when(commentRepository.findAllByPostIdAndParentIsNull(anyLong(), anyInt(), anyInt())).thenReturn(commentPage);

        // When
        CommentMapResponseDto response = commentService.reads(1L, 0, 10);

        // Then
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getResponseDto().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 조회 시 예외 발생 테스트")
    void readNonExistingCommentTest() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () -> commentService.read(1L, 1L, 1L), ErrorCode.COMMENT_NOT_FOUND.getMessage());
    }
}
