package com.repo.domain.post.service;

import com.repo.domain.board.entity.Board;
import com.repo.domain.board.service.BoardService;
import com.repo.domain.post.dto.PostRequestDto;
import com.repo.domain.post.dto.PostResponseDto;
import com.repo.domain.post.dto.PostResponseMapDto;
import com.repo.domain.post.entity.Post;
import com.repo.domain.post.repository.PostRepository;
import com.repo.domain.user.entity.User;
import com.repo.domain.user.service.UserService;
import com.repo.exception.BusinessException;
import com.repo.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardService boardService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private User user;
    private Board board;
    private Post post;
    private PostRequestDto postRequestDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testUser", "태스터", "password", User.Authorities.USER, User.Status.NORMAL);
        board = new Board(1L, "Test Board", user);
        post = new Post(1L, "Test Title", "Test Content", user, 0L);
        postRequestDto = new PostRequestDto("Updated Title", "Updated Content");
    }

    @Test
    @DisplayName("게시글 생성 테스트")
    void createPostTest() {
        // Given
        when(boardService.getBoard(anyLong(), anyLong())).thenReturn(board);
        when(userService.getUser(anyLong())).thenReturn(user);

        // When
        PostResponseDto response = postService.create(1L, 1L, postRequestDto);

        // Then
        assertThat(response.getTitle()).isEqualTo(postRequestDto.getTitle());
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void updatePostTest() {
        // Given
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userService.getUser(anyLong())).thenReturn(user);

        // When
        PostResponseDto response = postService.update(1L, 1L, 1L, postRequestDto);

        // Then
        assertThat(response.getTitle()).isEqualTo(postRequestDto.getTitle());
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deletePostTest() {
        // Given
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userService.getUser(anyLong())).thenReturn(user);

        // When
        postService.delete(1L, 1L, 1L);

        // Then
        verify(postRepository, times(1)).delete(any(Post.class));
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void readPostTest() {
        // Given
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // When
        PostResponseDto response = postService.read(1L, 1L);

        // Then
        assertThat(response.getId()).isEqualTo(post.getId());
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("게시글 목록 조회 테스트")
    void readPostsTest() {
        // Given
        List<Post> postList = List.of(post);
        Page<Post> postPage = new PageImpl<>(postList);
        when(postRepository.reads(anyLong(), anyInt(), anyInt(), anyBoolean())).thenReturn(postPage);

        // When
        PostResponseMapDto response = postService.reads(1L, 0, 10, true);

        // Then
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getResponseDtoList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 검색 테스트")
    void searchPostsTest() {
        // Given
        List<Post> postList = List.of(post);
        Page<Post> postPage = new PageImpl<>(postList);
        when(postRepository.search(anyLong(), anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(postPage);

        // When
        PostResponseMapDto response = postService.search(1L, "keyword", 0, 10, true);

        // Then
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getResponseDtoList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글이 존재하지 않을 때 예외 발생 테스트")
    void getPostNotFoundTest() {
        // Given
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () -> postService.getPost(1L), ErrorCode.POST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("게시글 권한이 없을 때 예외 발생 테스트")
    void getPostNoPermissionTest() {
        // Given
        User otherUser = new User(2L, "otherUser", "테스터", "password", User.Authorities.USER, User.Status.NORMAL);
        Post otherUserPost = new Post(2L, "Other Title", "Other Content", otherUser, 0L);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(otherUserPost));
        when(userService.getUser(anyLong())).thenReturn(user);

        // When & Then
        assertThrows(BusinessException.class, () -> postService.getPost(2L, 1L), ErrorCode.NO_BOARD_PERMISSION.getMessage());
    }
}
