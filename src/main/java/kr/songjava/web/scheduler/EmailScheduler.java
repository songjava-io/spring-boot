package kr.songjava.web.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import kr.songjava.web.domain.Message;
import kr.songjava.web.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class EmailScheduler {

	private final MessageService messageService;
	private final EmailService emailService;
	
	@Scheduled(cron = "0/1 * * * * ?")
	public void send() {
		List<Message> list = messageService.getList();
		if (list.size() == 0) {
			log.info("이메일 보낼 대상이 없습니다...");
			return;
		}
		Message message = list.get(0);
		// 이메일 전송 대상 반복
		try {
			emailService.send(new EmailInfo(
				message.getReceiveEmail(),
				message.getSendEmail(), 
				message.getSubject(),
				message.getContents()
			));
			// 해당 이메일 전송은 성공처리.. 다시 전송안되게..
			messageService.updateStateSuccess(message.getMsgSeq());
			log.info("이메일 전송 성공...");
		} catch (Exception e) {
			// 해당 이메일 전송은 실패처리.. 나중에 다시 전송하는 대상으로..
			messageService.updateStateFail(message.getMsgSeq());
			log.info("이메일 전송 실패...");
		}
	}
}
