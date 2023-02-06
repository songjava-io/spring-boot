package kr.songjava.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void print() {
		logger.info("메인 서비스 호출...");
	}
}
