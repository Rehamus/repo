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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardService boardService;
    private final UserService userService;

    @Transactional
    public PostResponseDto create(Long boardId, Long userId, PostRequestDto requestDto) {
        Board board = boardService.getBoard(userId, boardId);
        Post post = Post.builder()
                .user(userService.getUser(userId))
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .viewCount(0L)
                .build();

        board.addPost(post);

        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto update(Long boardId, Long userId, Long postId, PostRequestDto requestDto) {
        Post post = getPost(postId, userId);

        post.update(requestDto);

        return new PostResponseDto(post);
    }

    @Transactional
    public void delete(Long boardId, Long userId, Long postId) {
        Post post = getPost(postId, userId);

        postRepository.delete(post);
    }

    public PostResponseDto read(Long boardId, Long postId) {
        Post post = getPost(postId);

        post.incrementViewCount();

        return new PostResponseDto(post);
    }

    public PostResponseMapDto reads(Long boardId, int page, int pageSize, boolean asc) {

        Page<Post> posts = postRepository.reads(boardId, page, pageSize, asc);

        return getPostResponseMapDto(posts);
    }

    public PostResponseMapDto search(Long boardId, String keyword, int page, int pageSize, boolean asc) {

        Page<Post> posts = postRepository.search(boardId, keyword, page, pageSize, asc);

        return getPostResponseMapDto(posts);
    }

    //:::::::::::::::::// create //::::::::::::::::://

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ErrorCode.POST_NOT_FOUND)
        );
    }

    public Post getPost(Long postId, Long userId) {
        Post post = getPost(postId);
        User user = userService.getUser(userId);

        if (!user.getId().equals(post.getUser().getId())) {
            throw new BusinessException(ErrorCode.NO_BOARD_PERMISSION);
        }

        return post;
    }

    private PostResponseMapDto getPostResponseMapDto(Page<Post> posts) {
        return new PostResponseMapDto(posts.getTotalPages(), posts.getContent().stream()
                .map(PostResponseDto::new)
                .toList());
    }


}
