package kr.songjava.web.mapper;

import java.util.List;

import kr.songjava.web.domain.Message;

public interface MessageMapper {

	List<Message> selectMessageList();
	
	void insertMessage(Message message);
	
	void updateMessageStateSuccess(int msgSeq);
	void updateMessageStateFail(int msgSeq);
	
}
