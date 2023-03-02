package kr.songjava.web.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailScheduler {

	private final EmailService emailService;
	
	@Scheduled(cron = "0/1 * * * * ?")
	public void send() {
		emailService.send();
	}
}
