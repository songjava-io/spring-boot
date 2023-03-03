package kr.songjava.web.scheduler;

import java.io.File;
import java.nio.file.Paths;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import kr.songjava.web.configuration.properties.FileProperties;
import kr.songjava.web.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RealEmailService implements EmailService {
	
	private final JavaMailSender javaMailSender;
	private final FileProperties fileProperties;

	@Override
	public void send(EmailInfo info) {
		log.info("실제 이메일을 받는사람 전송 !!! 주의 SMTP로 실제 전송이 됨.");
		try {
			
			String rootPath = Paths.get(fileProperties.rootPath()).toAbsolutePath().toString() + "/";
			log.info("rootPath : {}", rootPath);
			
			String filepath = rootPath + "/20230127/0a42dcbf-1bc6-4d7e-87ce-6ad4e897d5a6.jpg";
			
			log.info("filepath : {}", filepath);
			
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			helper.addAttachment("test.png", new File(filepath));
			
			helper.setTo(info.to());
			helper.setFrom(info.from());
			helper.setSubject(info.subject());
			helper.setText(info.text(), true);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			log.error("send error", e);
			throw new ApiException("이메일 발송을 실패하였습니다.");
		}
	}

}
