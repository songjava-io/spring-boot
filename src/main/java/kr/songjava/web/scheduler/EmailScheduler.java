package kr.songjava.web.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EmailScheduler {

	private final EmailService emailService;
	
	@Scheduled(cron = "0/1 * * * * ?")
	public void send() {
		log.info("emailService : {}", emailService);
		emailService.send();
	}
}
