package kr.songjava.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@MapperScan("kr.songjava.web.mapper")
@Slf4j
public class SongjavaSpringbootApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		log.debug("기본 main 메소드로 서버 실행... (내장톰캣)");
		SpringApplication.run(SongjavaSpringbootApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		log.debug("외장톰캣으로 서버 실행...");
		return builder.sources(SongjavaSpringbootApplication.class);
	}

}
