package kr.songjava.web.mapper;

import java.util.List;
import java.util.Map;

import kr.songjava.web.domain.Board;

/**
 * 게시판 Dao
 * 
 * @author youtube
 *
 */
public interface BoardMapper {

	/**
	 * 게시물 목록 조회 리턴
	 * 
	 * @return @
	 */
	// public List<Board> selectBoardList2(@Param("boardType") BoardType boardType, @Param("query") String query);
	public List<Board> selectBoardList(Map<String, Object> paramMap);

	/**
	 * 게시물 조회
	 * 
	 * @param boardSeq
	 * @return @
	 */
	public Board selectBoard(int boardSeq);

	/**
	 * 게시물 등록
	 * 
	 * @param board @
	 */
	public void insertBoard(Board board);

	/**
	 * 게시물 업데이트
	 * 
	 * @param board @
	 */
	public void updateBoard(Board board);

	/**
	 * 게시물 삭제
	 * 
	 * @param board @
	 */
	public void deleteBoard(int boardSeq);

}
