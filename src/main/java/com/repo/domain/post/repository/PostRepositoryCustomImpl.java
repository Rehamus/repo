package com.repo.domain.post.repository;


import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.repo.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.repo.domain.post.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static OrderSpecifier<?> getOrderSpecifier(boolean asc) {
        return new OrderSpecifier<>(
                asc ? Order.ASC : Order.DESC,
                post.createdAt
        );
    }

    @Override
    public Page<Post> reads(Long boardId, int page, int pageSize, boolean asc) {
        long totalCount = Optional.ofNullable(
                jpaQueryFactory
                        .select(post.count())
                        .from(post)
                        .where(post.board.id.eq(boardId))
                        .fetchFirst()
        ).orElse(0L);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .where(post.board.id.eq(boardId))
                .offset(pageable.getOffset())
                .limit(pageSize)
                .orderBy(getOrderSpecifier(asc))
                .fetch();

        return new PageImpl<>(posts, pageable, totalCount);
    }

    @Override
    public Page<Post> search(Long boardId, String keyword, int page, int pageSize, boolean asc) {
        long totalCount = Optional.ofNullable(
                jpaQueryFactory
                        .select(post.count())
                        .from(post)
                        .where(post.title.like("%" + keyword + "%"))
                        .fetchFirst()
        ).orElse(0L);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .where(post.title.like("%" + keyword + "%"))
                .offset(pageable.getOffset())
                .limit(pageSize)
                .orderBy(getOrderSpecifier(asc))
                .fetch();

        return new PageImpl<>(posts, pageable, totalCount);
    }
}