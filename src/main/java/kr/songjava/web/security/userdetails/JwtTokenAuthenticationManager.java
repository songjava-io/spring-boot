package kr.songjava.web.security.userdetails;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import kr.songjava.web.domain.Member;
import kr.songjava.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenAuthenticationManager
	implements AuthenticationManager {
	
	private final JwtDecoder decoder;
	private final MemberService memberService;

	@Override
	public Authentication authenticate(Authentication authentication) 
		throws AuthenticationException {
		String tokenValue = authentication.getPrincipal().toString();
		Jwt jwt = decoder.decode(tokenValue);
		String username = jwt.getSubject();
		// 토큰에 username이 실제 DB에 존재하는지 체크와 회원 정보를 인증에 사용하기 위한.
		Member member = memberService.selectMemberAccount(username);
		if (member == null) {
			throw new UsernameNotFoundException("회원이 존재하지 않습니다.");
		}
		log.info("member : {}", member);		
		
		List<String> roles = jwt.getClaimAsStringList("roles");
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		log.debug("roles : {}", roles);
		log.debug("username : {}", username);
		return new UsernamePasswordAuthenticationToken(
			SecurityUserDetails.builder()
				.username(username)
				.password(member.getPassword())
				.authorities(authorities)
				.build(), 
				member.getPassword(),
				authorities
		);
	}

}
