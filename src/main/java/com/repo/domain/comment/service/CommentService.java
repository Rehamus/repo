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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public CommentResponseDto create(Long postId, UserPrincipal userPrincipal, CommentRequestDto commentRequestDto) {

        Post post = postRepository.findById(postId).orElse(null);

        User user = userPrincipal.getUser();
        Comment parentComment = null;

        if (commentRequestDto.getParentId() != null) {
            parentComment = commentRepository.findById(commentRequestDto.getParentId()).orElseThrow(
                    () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
            );
        }

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .contents(commentRequestDto.getContents())
                .parent(parentComment)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    public CommentResponseDto update(Long postId, Long commentId, Long userId, CommentRequestDto commentRequestDto) {

        User user = getUser(userId);

        Comment comment = getComment(postId, commentId, user);

        comment.updateContent(commentRequestDto.getContents());

        commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }


    @Transactional
    public void delete(Long postId, Long commentId, Long userId) {

        User user = getUser(userId);

        Comment comment = getComment(postId, commentId, user);
        if (comment.getParent() != null) {
            Comment parentcomment = comment.getParent();
            parentcomment.deleteChildren(comment);
        }

        commentRepository.delete(comment);
    }

    public CommentResponseDto read(Long boardId, Long postId, Long commentId) {
        return new CommentResponseDto(getComment(commentId));
    }

    public CommentMapResponseDto reads(Long postId, int page, int pageSize) {
        Page<Comment> comments = commentRepository.findAllByPostIdAndParentIsNull(postId, page, pageSize);

        return new CommentMapResponseDto(comments.getTotalPages(), comments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList()));
    }

    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://


    private Comment getComment(Long postId, Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        );

        if (!comment.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return comment;
    }


    private CommentResponseDto convertToResponseDto(Comment comment) {
        List<CommentResponseDto> childrenDto = comment.getChildren().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return CommentResponseDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .postId(comment.getPost().getId())
                .nickname(comment.getUser().getNickname())
                .contents(comment.getContents())
                .childrenDto(childrenDto)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private User getUser(Long userId) {
        return userService.getUser(userId);
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }


}
