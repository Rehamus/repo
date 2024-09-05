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
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BoardService boardService;

    private User user;
    private Board board;
    private BoardRequestDto boardRequestDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testUser", "태스터", "password", User.Authorities.USER, User.Status.NORMAL);
        board = new Board(1L, "Test Board", user);
        boardRequestDto = new BoardRequestDto("Updated Title");
    }

    @Test
    @DisplayName("게시판 생성 테스트")
    void createBoardTest() {
        // Given
        when(userService.getUser(anyLong())).thenReturn(user);
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        // When
        BoardResponseDto response = boardService.create(1L, boardRequestDto);

        // Then
        assertThat(response.getTitle()).isEqualTo(boardRequestDto.getTitle());
        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    @DisplayName("게시판 수정 테스트")
    void updateBoardTest() {
        // Given
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(userService.getUser(anyLong())).thenReturn(user);

        // When
        BoardResponseDto response = boardService.update(1L, 1L, boardRequestDto);

        // Then
        assertThat(response.getTitle()).isEqualTo(boardRequestDto.getTitle());
        verify(boardRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("게시판 삭제 테스트")
    void deleteBoardTest() {
        // Given
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(userService.getUser(anyLong())).thenReturn(user);

        // When
        boardService.delete(1L, 1L);

        // Then
        verify(boardRepository, times(1)).delete(any(Board.class));
    }

    @Test
    @DisplayName("게시판 조회 테스트")
    void readBoardTest() {
        // Given
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));

        // When
        BoardResponseDto response = boardService.read(1L);

        // Then
        assertThat(response.getId()).isEqualTo(board.getId());
        verify(boardRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("게시판 목록 조회 테스트")
    void readBoardsTest() {
        // Given
        List<Board> boardList = List.of(board);
        Page<Board> boardPage = new PageImpl<>(boardList);
        when(boardRepository.reads(anyInt(), anyInt(), anyBoolean())).thenReturn(boardPage);

        // When
        BoardResponseMapDto response = boardService.reads(0, 10, true);

        // Then
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getResponseDtoList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시판 검색 테스트")
    void searchBoardsTest() {
        // Given
        List<Board> boardList = List.of(board);
        Page<Board> boardPage = new PageImpl<>(boardList);
        when(boardRepository.search(anyString(), anyInt(), anyInt(), anyBoolean())).thenReturn(boardPage);

        // When
        BoardResponseMapDto response = boardService.search("keyword", 0, 10, true);

        // Then
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getResponseDtoList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시판이 존재하지 않을 때 예외 발생 테스트")
    void getBoardNotFoundTest() {
        // Given
        when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () -> boardService.getBoard(1L), ErrorCode.BOARD_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("게시판 권한이 없을 때 예외 발생 테스트")
    void getBoardNoPermissionTest() {
        // Given
        User otherUser = new User(2L, "otherUser", "테스터", "password", User.Authorities.USER, User.Status.NORMAL);
        Board otherUserBoard = new Board(2L, "Other Board", otherUser);
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(otherUserBoard));
        when(userService.getUser(anyLong())).thenReturn(user);

        // When & Then
        assertThrows(BusinessException.class, () -> boardService.getBoard(1L, 2L), ErrorCode.NO_BOARD_PERMISSION.getMessage());
    }
}
