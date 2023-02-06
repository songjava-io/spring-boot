package kr.songjava.web.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestConfig {

	/**
	 * 현재 메뉴 코드를 리턴
	 * @return
	 */
	String menu() default "";
	
	/**
	 * 본인인증 체크가 필요한지 리턴
	 * @return
	 */
	boolean realnameCheck() default false;

}