package kr.songjava.web.scheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestEmailService implements EmailService {

	@Override
	public void send() {
		log.info("로컬 테스트 이메일 전송이 되었다고 가정 !!!");
	}

}
