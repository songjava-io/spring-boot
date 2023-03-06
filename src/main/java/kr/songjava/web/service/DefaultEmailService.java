package kr.songjava.web.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultEmailService implements EmailService {

	@Override
	public void send(EmailInfo info) {
		log.info("실제 이메일 발송처리 안함.. 스케줄러 동작한 테스트...");
	}

}
