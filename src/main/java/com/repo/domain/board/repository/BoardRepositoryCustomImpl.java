package com.repo.domain.board.repository;


import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.repo.domain.board.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.repo.domain.board.entity.QBoard.board;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static OrderSpecifier<?> getOrderSpecifier(boolean asc) {
        return new OrderSpecifier<>(
                asc ? Order.ASC : Order.DESC,
                board.createdAt
        );
    }

    @Override
    public Page<Board> reads(int page, int pageSize, boolean asc) {

        long totalCount = Optional.ofNullable(
                jpaQueryFactory
                        .select(board.count())
                        .from(board)
                        .fetchFirst()
        ).orElse(0L);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<Board> boards = jpaQueryFactory.selectFrom(board)
                .offset(pageable.getOffset())
                .limit(pageSize)
                .orderBy(getOrderSpecifier(asc))
                .fetch();

        return new PageImpl<>(boards, pageable, totalCount);
    }

    @Override
    public Page<Board> search(String keyword, int page, int pageSize, boolean asc) {

        long totalCount = Optional.ofNullable(
                jpaQueryFactory
                        .select(board.count())
                        .from(board)
                        .where(board.title.like("%" + keyword + "%"))
                        .fetchFirst()
        ).orElse(0L);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<Board> boards = jpaQueryFactory.selectFrom(board)
                .where(board.title.like("%" + keyword + "%"))
                .offset(pageable.getOffset())
                .limit(pageSize)
                .orderBy(getOrderSpecifier(asc))
                .fetch();

        return new PageImpl<>(boards, pageable, totalCount);
    }
}