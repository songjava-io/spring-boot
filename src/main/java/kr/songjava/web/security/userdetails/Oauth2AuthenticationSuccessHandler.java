package kr.songjava.web.security.userdetails;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import kr.songjava.web.domain.Member;
import kr.songjava.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final MemberService memberService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// OAuth2 인증토큰으로 변환
		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
		OAuth2User user = (OAuth2User) token.getPrincipal();
		log.info("token : {}", token);
		log.info("user : {}", user);
		// Oauth2 인증된 회원이 실제 DB에 등록이되어있는지 조회
		Member member = memberService.selectMemberOauth2(
			token.getAuthorizedClientRegistrationId(), 
			token.getName());
		if (member == null) {
			// 회원가입 필요.
			response.sendRedirect("/member/form");
		} else {
			// 인증이 완료된 회원
			response.sendRedirect("/home");
		}
	}

}
