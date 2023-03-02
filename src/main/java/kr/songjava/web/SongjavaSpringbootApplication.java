package kr.songjava.web;

import java.util.Arrays;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("kr.songjava.web.mapper")
@Slf4j
@RequiredArgsConstructor
public class SongjavaSpringbootApplication extends SpringBootServletInitializer
  implements CommandLineRunner {
	
	private final Environment environment;

	public static void main(String[] args) {
		log.debug("기본 main 메소드로 서버 실행... (내장톰캣)");
		SpringApplication.run(SongjavaSpringbootApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		log.debug("외장톰캣으로 서버 실행...");
		return builder.sources(SongjavaSpringbootApplication.class);
	}

	@Override
	public void run(String... args) throws Exception {
		log.debug("서버실행 후 메소드 호출됨..");
		log.info("myservice : {}", System.getProperty("myservice"));
		log.info("rootPath : {}", environment.getProperty("file.root-path"));
		log.info("rootPath : {}", environment.getProperty("file.rootPath"));
		log.info("actvieProfiles : {}", Arrays.toString(environment.getActiveProfiles()));
	}

}
