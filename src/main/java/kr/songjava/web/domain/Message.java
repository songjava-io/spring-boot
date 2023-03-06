package kr.songjava.web.domain;

import lombok.Data;

@Data
public class Message {

	private int msgSeq;
	private MessageType msgType;
	private String subject;
	private String contents;
	private String receiveEmail;
	private String sendEmail;
	private MessageState state;
	private String sendDate;
	private String failDate;
	private String receivePhone;
	
}
