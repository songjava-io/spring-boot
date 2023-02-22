package kr.songjava.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardComment {
	
	private int boardCommentSeq; 
	private int boardSeq;
	private int memberSeq; 
	private String comment;
	private String regDate; 
	private String nickname; 
	private String account;
	
}
