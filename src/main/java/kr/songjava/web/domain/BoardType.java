package kr.songjava.web.domain;

import org.springframework.util.StringUtils;

public enum BoardType {

	community("COMMUNITY", "커뮤니티"), 
	notice("NOTICE", "공지사항"), 
	inquiry("INQUIRY", "1:1 문의"),
	;

	private String code;
	private String label;

	BoardType(String code, String label) {
		this.code = code;
		this.label = label;
	}

	public String code() {
		return code;
	}

	public String label() {
		return label;
	}

	public static BoardType valueOfCode(String code) {
		if (!StringUtils.hasLength(code))
			return null;
		BoardType[] values = BoardType.values();
		for (BoardType value : values) {
			if (value.code().equals(code)) {
				return value;
			}
		}
		return null;
	}
	
	
}
