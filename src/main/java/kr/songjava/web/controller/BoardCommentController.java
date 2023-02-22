package kr.songjava.web.controller;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.songjava.web.domain.BoardComment;
import kr.songjava.web.form.BoardCommentSaveForm;
import kr.songjava.web.security.userdetails.SecurityUserDetails;
import kr.songjava.web.service.BoardService;
import lombok.RequiredArgsConstructor;

/**
 * 게시물 댓글 Restapi 컨트롤러
 */
@RestController
@RequestMapping("/board/comment")
@RequiredArgsConstructor
public class BoardCommentController {

	private final BoardService boardService;
	
	/**
	 * 댓글 목록 리턴
	 * @param comment
	 * @return
	 */
	@GetMapping("/{boardSeq}")
	public List<BoardComment> list(@PathVariable int boardSeq) {
		return boardService.selectBoardCommentList(boardSeq);
	}

	/**
	 * 댓글 저장 처리.
	 * @param comment
	 * @return
	 */
	@PostMapping("/save")
	public HttpEntity<List<BoardComment>> save(@Validated BoardCommentSaveForm form, 
			@AuthenticationPrincipal SecurityUserDetails userDetails) {
		// form --> domain 변경
		BoardComment comment = BoardComment.builder()
			.comment(form.getComment())
			.boardSeq(form.getBoardSeq())
			.memberSeq(userDetails.getMemberSeq())
			.build();
		// 저장
		boardService.saveComment(comment);
		// 댓글 목록을 리턴
		return ResponseEntity.ok(
			boardService.selectBoardCommentList(comment.getBoardSeq())
		);
	}
	
	/**
	 * 댓글 삭제 처리.
	 * @param comment
	 * @return
	 */
	@PostMapping("/delete")
	public HttpEntity<List<BoardComment>> delete(@RequestParam int boardCommentSeq,
			@AuthenticationPrincipal SecurityUserDetails userDetails) {
		BoardComment boardComment = boardService.selectBoardComment(boardCommentSeq);
		Assert.notNull(boardComment, "댓글이 없습니다.");
		Assert.isTrue(boardComment.getMemberSeq() == userDetails.getMemberSeq(), 
				"댓글을 작성자만 삭제가 가능합니다.");
		// 저장
		boardService.deleteComment(boardCommentSeq);
		// 댓글 목록을 리턴
		return ResponseEntity.ok(
			boardService.selectBoardCommentList(boardCommentSeq)
		);
	}
	
}
