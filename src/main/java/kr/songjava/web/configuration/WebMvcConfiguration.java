package kr.songjava.web.configuration;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import kr.songjava.web.error.CustomErrorAttributes;
import kr.songjava.web.interceptor.MemberAuthInterceptor;
import kr.songjava.web.interceptor.MemberRealnameCheckInterceptor;
import kr.songjava.web.interceptor.RequestLoggingInterceptor;

@Configuration
@EnableAspectJAutoProxy
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Bean
	public MappingJackson2JsonView jsonView() {
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		return jsonView;
	}
	
	@Bean
	public MessageSource messageSource() {
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
	public CustomErrorAttributes customErrorAttributes() {
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
	
	
}
