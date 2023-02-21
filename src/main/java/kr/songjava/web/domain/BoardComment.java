package kr.songjava.web.domain;

import lombok.Data;

@Data
public class BoardComment {
	
	private int boardCommentSeq; 
	private int boardSeq;
	private int memberSeq; 
	private String comment;
	private String regDate; 
	private String nickname; 
	private String account;
	
}
