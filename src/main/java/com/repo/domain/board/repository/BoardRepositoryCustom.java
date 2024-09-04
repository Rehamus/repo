package com.repo.domain.board.repository;


import com.repo.domain.board.entity.Board;
import org.springframework.data.domain.Page;

public interface BoardRepositoryCustom {

    Page<Board> reads(int page, int pageSize, boolean asc);

    Page<Board> search(String keyword, int page, int pageSize, boolean asc);

}


