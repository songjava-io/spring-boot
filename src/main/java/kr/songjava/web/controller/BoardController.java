package kr.songjava.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.songjava.web.domain.Board;
import kr.songjava.web.domain.BoardType;
import kr.songjava.web.form.BoardSaveForm;
import kr.songjava.web.security.userdetails.SecurityUserDetails;
import kr.songjava.web.service.BoardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Validated
public class BoardController {
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	private final BoardService boardService;
	
	@GetMapping("/{boardType}")
	public List<Board> list(Model model, @PathVariable BoardType boardType,
			@RequestParam(required = false) String query) 
			throws Exception {
		logger.info("BoardController list 실행...");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("query", query);
		paramMap.put("boardType", boardType);
		return boardService.selectBoardList(paramMap);
	}
	
	/**
	 * 상세정보
	 * @param model
	 * @param boardType
	 * @param boardSeq
	 * @return
	 */
	@GetMapping("/{boardType}/{boardSeq}")
	public Board detail(Model model, @PathVariable BoardType boardType, @PathVariable int boardSeq) {
		Board board = boardService.selectBoard(boardSeq);
		Assert.notNull(board, "게시글 정보가 없습니다.");
		return board;
	}
	
	/**
	 * 게시물 저장기능
	 */
	@PostMapping("/save")
	public Board save(@Validated BoardSaveForm form, 
			@AuthenticationPrincipal SecurityUserDetails user) {
		Board board = Board.builder()
			.boardType(form.getBoardType())
			.title(form.getTitle())
			.contents(form.getContents())
			.memberSeq(user.getMemberSeq())
			.build();
		// 게시물 저장
		boardService.save(board);
		return board;
	}
	
	/**
	 * 게시물 업데이트 기능 처리.
	 */
	@PostMapping("/update")
	public Board update(@Validated BoardSaveForm form) {
		// form -> Board 도메인으로 데이터 set 후 build
		Board board = Board.builder()
			.title(form.getTitle())
			.contents(form.getContents())
			.boardSeq(form.getBoardSeq())
			.build();
		
		// 게시물 저장
		boardService.save(board);
		return board;
	}
	
	/**
	 * 게시물 삭제 기능 처리.
	 */
	@PostMapping("/delete")
	public HttpEntity<Boolean> delete(@RequestParam int boardSeq) {
		boardService.delete(boardSeq);
		return ResponseEntity.ok(true);
	}

}
