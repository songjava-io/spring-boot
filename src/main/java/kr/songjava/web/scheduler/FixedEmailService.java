package kr.songjava.web.scheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FixedEmailService implements EmailService {

	@Override
	public void send() {
		log.info("이메일 받는사람 고정 전송 !!!");
	}

}
