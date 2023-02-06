package kr.songjava.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("kr.songjava.web.mapper")
public class SongjavaSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SongjavaSpringbootApplication.class, args);
	}

}
