package kr.songjava.web.configuration;

import java.nio.file.Paths;
import java.util.Locale;

import org.springframework.boot.info.OsInfo;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import kr.songjava.web.error.CustomErrorAttributes;
import kr.songjava.web.interceptor.MemberAuthInterceptor;
import kr.songjava.web.interceptor.RequestLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {
	
	private final FileProperties fileProperties;

	@Bean
	MappingJackson2JsonView jsonView() {
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		return jsonView;
	}
	
	@Bean
	MessageSource messageSource() {
		ReloadableResourceBundleMessageSource source = 
			new ReloadableResourceBundleMessageSource();
		source.setBasename("classpath:config/messages/message");
		source.setDefaultEncoding("UTF-8");
		source.setDefaultLocale(Locale.ENGLISH);
		return source;
	}
	
	@Bean
	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean factory =
			new LocalValidatorFactoryBean();
		factory.setValidationMessageSource(messageSource());
		return factory;
	}
	
	@Bean
	CustomErrorAttributes customErrorAttributes() {
		return new CustomErrorAttributes();
	}
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
			// 사용할 인터셉터를 set
			.addInterceptor(new RequestLoggingInterceptor())
			// 추가한 인터셉터가 동작해야할 URL 패턴 추가
			.addPathPatterns("/**")
			// 로그인 제외
			.excludePathPatterns("/member/login")
			.order(1);
		registry
			// 사용할 인터셉터를 set
			.addInterceptor(new MemberAuthInterceptor())
			// 추가한 인터셉터가 동작해야할 URL 패턴 추가
			.addPathPatterns("/**")
			// 로그인 제외
			.excludePathPatterns("/member/login")
			.order(2);
		/*
		registry
		// 사용할 인터셉터를 set
		.addInterceptor(new MemberRealnameCheckInterceptor())
		// 추가한 인터셉터가 동작해야할 URL 패턴 추가
		.addPathPatterns("/member/save", "/member/save-upload")
		// 로그인 제외
		.excludePathPatterns("/member/login")
		.order(2);
		*/		
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String rootPath = Paths.get(fileProperties.rootPath()).toAbsolutePath().toString() + "/";
		log.info("rootPath : {}", rootPath);
		OsInfo osInfo = new OsInfo();
		// os 비교하여 환경에 맞게 업로드 파일 접근시 실제 파일 경로로 매핑되게 설정
		if (osInfo.getName().toLowerCase().contains("windows")) {
			// 윈도우 환경
			registry.addResourceHandler(fileProperties.resourcePath())
				.addResourceLocations("file:///" + rootPath);
		} else {
			// 유닉스, 리눅스 환경
			registry.addResourceHandler(fileProperties.resourcePath())
			.addResourceLocations("file://" + rootPath);
		}
	}
	
	
}
