package kr.songjava.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {

	private int boardSeq;
	private String title;
	private String contents;
	private int memberSeq;
	private String regDate;
	
}
