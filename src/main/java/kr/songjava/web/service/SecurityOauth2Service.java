package kr.songjava.web.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import kr.songjava.web.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityOauth2Service extends DefaultOAuth2UserService {
	
	private final MemberService memberService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("userRequest : {}", userRequest.getClientRegistration());
		final String clientName = userRequest.getClientRegistration().getClientName();		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info("oAuth2User : {}", oAuth2User);
		log.info("oAuth2User.getName() : {}", oAuth2User.getName());
		
		log.info("clientName : {}", clientName);
		/*
		Member member = memberService.selectMemberOAuth2(clientName, oAuth2User.getName());
		if (member == null) {
			throw new OAuth2AuthenticationException(new OAuth2Error("MEMBER_NOT_FOUND"));
		}
		*/
		Map<String, Object> attributes = oAuth2User.getAttributes();
		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_OAUTH2_KAKAO")), attributes,
				"id");
	}

}