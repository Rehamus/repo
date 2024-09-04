package com.repo.domain.post.repository;


import com.repo.domain.post.entity.Post;
import org.springframework.data.domain.Page;

public interface PostRepositoryCustom {

    Page<Post> reads(Long boardId, int page, int pageSize, boolean asc);

    Page<Post> search(Long boardId, String keyword, int page, int pageSize, boolean asc);
}


