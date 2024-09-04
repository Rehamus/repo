package com.repo.domain.board.service;

import com.repo.domain.board.dto.BoardRequestDto;
import com.repo.domain.board.dto.BoardResponseDto;
import com.repo.domain.board.dto.BoardResponseMapDto;
import com.repo.domain.board.entity.Board;
import com.repo.domain.board.repository.BoardRepository;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;

    public BoardResponseDto create(Long id, BoardRequestDto requestDto) {
        User user = userService.getUser(id);

        Board board = Board.builder()
                .title(requestDto.getTitle())
                .postCount(0L)
                .user(user)
                .build();

        boardRepository.save(board);

        return new BoardResponseDto(board,false);
    }

    @Transactional
    public BoardResponseDto update(Long id, Long boardId, BoardRequestDto requestDto) {
        Board board = getBoard(id, boardId);

        board.update(requestDto);

        return new BoardResponseDto(board,false);
    }

    public void delete(Long id, Long boardId) {
        Board board = getBoard(id, boardId);

        boardRepository.delete(board);
    }

    public BoardResponseDto read(Long boardId) {
        return new BoardResponseDto(getBoard(boardId),false);
    }

    public BoardResponseMapDto reads(int page, int pageSize, boolean asc) {

        Page<Board> boards = boardRepository.reads( page, pageSize, asc);

        return getBoardResponseMapDto(boards);
    }

    public BoardResponseMapDto search(String keyword, int page, int pageSize, boolean asc) {

        Page<Board> boards = boardRepository.search(keyword, page, pageSize, asc);

        return getBoardResponseMapDto(boards);
    }

    //:::::::::::::::::// TOOL BOX //::::::::::::::::://

    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new BusinessException(ErrorCode.BOARD_NOT_FOUND)
        );
    }

    public Board getBoard(Long userId, Long boardId) {
        User user = userService.getUser(userId);
        Board board = getBoard(boardId);

        if(!user.getId().equals(board.getUser().getId())) {
            throw new BusinessException(ErrorCode.NO_BOARD_PERMISSION);
        }

        return board;
    }

    private BoardResponseMapDto getBoardResponseMapDto(Page<Board> boards) {
        return new BoardResponseMapDto(
                boards.getTotalPages(),
                boards.getContent().stream()
                        .map(board -> new BoardResponseDto(board, false))
                        .toList()
        );
    }


}
