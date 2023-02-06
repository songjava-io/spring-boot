package kr.songjava.web.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {

	private int memberSeq;
	private String account;
	private String password;
	private String nickname;
	private String profileImagePath;
	private String profileImageName;
	private Date joinDate;
	
}
