package kr.songjava.web.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import kr.songjava.web.configuration.properties.FileProperties;
import kr.songjava.web.service.MessageService;

//@Profile(value = "prod")
@Configuration
//@EnableScheduling
public class SchedulerConfiguration {

	@Bean
	EmailScheduler emailScheduler(EmailService emailService, MessageService messageService) {
		return new EmailScheduler(messageService, emailService);
	}
	
	@Bean(name = "emailService")
	@ConditionalOnProperty(name = "email.service", havingValue = "test")
	EmailService defaultEmailService() {
		return new DefaultEmailService();
	}
	
	@Bean(name = "emailService")
	@ConditionalOnProperty(name = "email.service", havingValue = "real")
	EmailService realEmailService(JavaMailSender javaMailSender, FileProperties fileProperties) {
		return new RealEmailService(javaMailSender, fileProperties);
	}
	
}
