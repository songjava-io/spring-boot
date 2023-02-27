package kr.songjava.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import kr.songjava.web.domain.Board;
import kr.songjava.web.domain.BoardType;
import kr.songjava.web.exception.ApiException;
import kr.songjava.web.form.BoardSaveForm;
import kr.songjava.web.interceptor.RequestConfig;
import kr.songjava.web.security.userdetails.SecurityUserDetails;
import kr.songjava.web.service.BoardService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Validated
public class BoardController {
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	private final BoardService boardService;
	
	@ExceptionHandler(ApiException.class)
	public ModelAndView handleApiException(ApiException e) {
		logger.error("BoardController.handleApiException", e);
		ModelAndView model = new ModelAndView("/board/api-error.html");
		model.addObject("message", e.getMessage());
		return model;
	}
	
	@GetMapping("/{boardType}")
	@RequestConfig(menu = "BOARD")
	public String list(Model model, @PathVariable BoardType boardType,
			@RequestParam(required = false) String query) 
			throws Exception {
		logger.info("BoardController list 실행...");
		// 검색조건 파라메터
		if (query != null && query.equals("test")) {
			throw new RuntimeException("테스트 문자열을 허용이 되지 않습니다.");
		}
		if (query != null && query.equals("api")) {
			throw new ApiException("API 문자열은 허용이 되지 않습니다.");
		}
		if (query != null && query.equals("spring")) {
			throw new Exception("spring 문자열은 허용이 되지 않습니다.");
		}
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put("query", query);
		paramMap.put("boardType", boardType);
		
		// 서비스를 호출해서 게시물 목록을 리턴 받
		List<Board> boardList = boardService.selectBoardList(paramMap);
		model.addAttribute("boardList", boardList);
		// 등록화면에 이동할 때 사용
		model.addAttribute("boardType", boardType);
		model.addAttribute("boardTypes", BoardType.values());
		return "board/list";
	}
	
	
	/**
	 * 상세화면 
	 */
	@GetMapping("/{boardType}/{boardSeq}")
	@RequestConfig(menu = "BOARD")
	public String detail(Model model, @PathVariable BoardType boardType, @PathVariable int boardSeq) {
		Board board = boardService.selectBoard(boardSeq);
		Assert.notNull(board, "게시글 정보가 없습니다.");
		model.addAttribute("board", board);
		model.addAttribute("boardTypes", BoardType.values());
		// 게시글의 댓글 목록
		model.addAttribute("boardComments", boardService.selectBoardCommentList(boardSeq));
		return "board/detail";
	}
	
	/**
	 * 등록화면 
	 */
	@GetMapping("/{boardType}/form")
	@RequestConfig(menu = "BOARD")
	public String form(Model model, @PathVariable BoardType boardType) {
		// 추후 서버에 전송 할 때 필요하기 때문에 model에 추가
		model.addAttribute("boardType", boardType);
		model.addAttribute("boardTypes", BoardType.values());
		return "board/form";
	}
	
	/**
	 * 수정화면 
	 */
	@GetMapping("{boardType}/edit/{boardSeq}")
	@RequestConfig(menu = "BOARD")
	public String edit(Model model, @PathVariable BoardType boardType, @PathVariable int boardSeq) {
		Board board = boardService.selectBoard(boardSeq);
		model.addAttribute("boardType", boardType);
		model.addAttribute("board", board);
		model.addAttribute("boardTypes", BoardType.values());
		return "board/form";
	}
	
	/**
	 * 게시물 저장기능
	 */
	@PostMapping("/save")
	@RequestConfig(menu = "BOARD")
	public String save(@Validated BoardSaveForm form, 
			@AuthenticationPrincipal SecurityUserDetails user) {
		/*
		Assert.hasLength(board.getTitle(), "제목은 필수 입니다.");
		Assert.hasLength(board.getContents(), "내용은 필수 입니다.");
		*/
		// form -> Board 도메인으로 데이터 set 후 build
		
		//SecurityUserDetails user = (SecurityUserDetails) authentication.getPrincipal();
		
		Board board = Board.builder()
			.boardType(form.getBoardType())
			.title(form.getTitle())
			.contents(form.getContents())
			.memberSeq(user.getMemberSeq())
			.build();
		// 게시물 저장
		boardService.save(board);
		// 목록 페이지로 이동
		return "redirect:/board/" + form.getBoardType();
	}
	
	/**
	 * 게시물 업데이트 기능 처리.
	 */
	@PostMapping("/update")
	@RequestConfig(menu = "BOARD")
	public String update(@Validated BoardSaveForm form) {
		/*
		Assert.hasLength(form.getTitle(), "제목은 필수 입니다.");
		Assert.hasLength(form.getContents(), "내용은 필수 입니다.");
		*/
		// form -> Board 도메인으로 데이터 set 후 build
		Board board = Board.builder()
			.title(form.getTitle())
			.contents(form.getContents())
			.boardSeq(form.getBoardSeq())
			.build();
		
		// 게시물 저장
		boardService.save(board);
		// 목록 페이지로 이동
		return "redirect:/board/" + form.getBoardType();
	}
	
	/**
	 * 게시물 삭제 기능 처리.
	 */
	@PostMapping("/delete")
	@RequestConfig(menu = "BOARD")
	@ResponseBody
	public HttpEntity<Boolean> delete(@RequestParam int boardSeq) {
		boardService.delete(boardSeq);
		return ResponseEntity.ok(true);
	}

}
