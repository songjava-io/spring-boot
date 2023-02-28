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
		OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
		OAuth2User user = (OAuth2User) auth.getPrincipal();
		log.info("authentication : {}", authentication);
		Member member = memberService.selectMemberOAuth2(auth.getAuthorizedClientRegistrationId(), user.getName());
		// 회원가입
		if (member == null) {
			response.sendRedirect("/member/form");
		} else {
			response.sendRedirect("/");
		}
	}

}
