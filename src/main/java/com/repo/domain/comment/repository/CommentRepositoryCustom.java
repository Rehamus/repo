package com.repo.domain.comment.repository;


import com.repo.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;

public interface CommentRepositoryCustom {
    Page<Comment> findAllByPostIdAndParentIsNull(Long postId, int page, int pageSize);

}
