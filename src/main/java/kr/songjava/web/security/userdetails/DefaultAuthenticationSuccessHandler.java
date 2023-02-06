package kr.songjava.web.security.userdetails;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import kr.songjava.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 기본 인증 성공 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultAuthenticationSuccessHandler 
	implements AuthenticationSuccessHandler {
	
	private final MemberService memberService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		log.info("onAuthenticationSuccess");
		SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
		log.debug("userDetails : {}", userDetails);
		// 회원 로그인 일자를 업데이트
		memberService.updateLoginDate(userDetails.getMemberSeq());
		// 메인페이지로 이동
		response.sendRedirect("/home");
	}

}
