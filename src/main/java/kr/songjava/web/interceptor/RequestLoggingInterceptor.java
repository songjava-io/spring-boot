package kr.songjava.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

	private static final SimpleGrantedAuthority ANONYMOUS = new SimpleGrantedAuthority("ROLE_ANONYMOUS");
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("requestURI : {}", request.getRequestURI());
		log.info("handler : {}", handler);
		// 어디서든 Spring Security 인증 정보를 가져올 수 있는 클래스로 가져옴.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("authentication : {}", authentication);
		log.info("authentication.getAuthorities() : {}", authentication.getAuthorities());
		boolean anonymous = authentication.getAuthorities().contains(ANONYMOUS);
		// if (authentication instanceof AnonymousAuthenticationToken) {
		if (anonymous) {
			log.info("익명사용자가 접근한 Request");
		} else {
			log.info("username : {} 계정으로 로그인한 사용자가 접근한 Request", authentication.getName());
		}		
		return true;
	}
	
	
}
