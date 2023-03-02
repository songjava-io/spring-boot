package kr.songjava.web.scheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealEmailService implements EmailService {

	@Override
	public void send() {
		log.info("실제 이메일 발송처리...");
	}

}
