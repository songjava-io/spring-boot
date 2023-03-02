package kr.songjava.web.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

//@Profile(value = "prod")
@Configuration
@EnableScheduling
public class SchedulerConfiguration {

	@Bean
	EmailScheduler emailScheduler(EmailService emailService) {
		return new EmailScheduler(emailService);
	}
	
	@Bean(name = "emailService")
	@ConditionalOnProperty(name = "email.service", havingValue = "test")
	EmailService defaultEmailService() {
		return new DefaultEmailService();
	}
	
	@Bean(name = "emailService")
	@ConditionalOnProperty(name = "email.service", havingValue = "real")
	EmailService realEmailService() {
		return new RealEmailService();
	}
	
}
