package kr.songjava.web.domain;

import java.util.Date;

public record EventExcel(String account,
												 String username,
												 int age,
												 Date birthday,
												 String phone,
												 String email,
												 String result) {
}
