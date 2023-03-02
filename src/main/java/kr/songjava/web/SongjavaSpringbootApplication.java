package kr.songjava.web;

import java.util.Arrays;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("kr.songjava.web.mapper")
@Slf4j
@RequiredArgsConstructor
public class SongjavaSpringbootApplication implements CommandLineRunner {
	
	private final Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(SongjavaSpringbootApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.debug("args : {}", Arrays.toString(args));
		log.debug("getActiveProfiles : {}", Arrays.toString(environment.getActiveProfiles()));
		log.debug("getDefaultProfiles : {}", Arrays.toString(environment.getDefaultProfiles()));
	}

}
