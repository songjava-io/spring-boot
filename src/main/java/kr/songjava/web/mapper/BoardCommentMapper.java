package kr.songjava.web.mapper;

import java.util.List;

import kr.songjava.web.domain.BoardComment;

public interface BoardCommentMapper {

	List<BoardComment> selectBoardCommentList(int boardSeq);
	
	BoardComment selectBoardComment(int boardCommentSeq);
	
	void insertBoardComment(BoardComment comment);
	
	void deleteBoardComment(int boardCommentSeq);
	void deleteBoardCommentByBoardSeq(int boardSeq);
	
}
