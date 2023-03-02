package kr.songjava.web.scheduler;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RealEmailService implements EmailService {

	private final JavaMailSender javaMailSender;

	@Override
	public void send() {
		log.info("실제 이메일을 받는사람 전송 !!! 주의 SMTP로 실제 전송이 됨.");
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
			helper.setTo("master@songjava-springboot.com");
			helper.setFrom("user@songjava-springboot.com");
			helper.setSubject("Spring Boot 이메일 전송");
			helper.setText("이메일 내용..", true);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			log.error("send error", e);
		}
	}

}
