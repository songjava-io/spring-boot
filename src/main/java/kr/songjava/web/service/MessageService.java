package kr.songjava.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.songjava.web.domain.Message;
import kr.songjava.web.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

	private final MessageMapper messageMapper;

	public List<Message> getList() {
		return messageMapper.selectMessageList();
	}

	public void save(Message message) {
		messageMapper.insertMessage(message);
	}

	public void updateStateSuccess(int msgSeq) {
		messageMapper.updateMessageStateSuccess(msgSeq);
	}

	public void updateStateFail(int msgSeq) {
		messageMapper.updateMessageStateFail(msgSeq);
	}

}
