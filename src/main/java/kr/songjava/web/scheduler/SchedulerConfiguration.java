package kr.songjava.web.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile(value = { "dev", "stg", "prod" }) // 동작해야할 프로필 조건을 설정 가능..
@Configuration
@EnableScheduling
public class SchedulerConfiguration {

	@Bean
	EmailScheduler emailScheduler(EmailService emailService) {
		return new EmailScheduler(emailService);
	}
	
	@Bean(name = "emailService")
	@ConditionalOnProperty(name = "email.service", havingValue = "test")
	EmailService testEmailService() {
		return new TestEmailService();
	}
	
	@Bean(name = "emailService")
	@ConditionalOnProperty(name = "email.service", havingValue = "fixed")
	EmailService fixedEmailService() {
		return new FixedEmailService();
	}
	
	@Bean(name = "emailService")
	@ConditionalOnProperty(name = "email.service", havingValue = "real")
	EmailService realEmailService(JavaMailSender mailSender) {
		return new RealEmailService(mailSender);
	}
}
