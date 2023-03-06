package kr.songjava.web.security.userdetails;

import java.util.Arrays;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.songjava.web.domain.Member;
import kr.songjava.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenAuthenticationManager implements AuthenticationManager {

	private final MemberService memberService;
	private final JwtTokenManager tokenManager;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public Authentication authenticate(Authentication authentication) 
		throws AuthenticationException {
		JwtTokenClaimLogin claimLogin = tokenManager.decodeLogin(authentication);
		if (claimLogin != null) {
			// 토큰에 username이 실제 DB에 존재하는지 체크와 회원 정보를 인증에 사용하기 위한.
			Member member = memberService.selectMemberAccount(claimLogin.username());
			if (member == null) {
				throw new UsernameNotFoundException("회원이 존재하지 않습니다.");
			}
			log.info("member : {}", member);		
			log.debug("authorities : {}", claimLogin.authorities());
			log.debug("claim.username() : {}", claimLogin.username());
			return new UsernamePasswordAuthenticationToken(
				SecurityUserDetails.builder()
					.username(claimLogin.username())
					.password(member.getPassword())
					.authorities(claimLogin.authorities())
					.build(), 
					member.getPassword(),
					claimLogin.authorities()
			);
		}
		String id = tokenManager.decodeOAuth(authentication);
		Object data = redisTemplate.opsForValue().get("OAUTH2_USER_KAKAO:" + id);
		log.info("id : {}", id);
		log.info("data : {}", data);
		if (data != null) {
			try {
				OAuth2KakaoAccount account = objectMapper.readValue(data.toString(), OAuth2KakaoAccount.class);
				log.info("account : {}", account);
				if (account != null) {
					return new AnonymousAuthenticationToken(id, account, Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
				}
			} catch (Exception e) {
				log.error("e", e);
			}
		}
		throw new BadCredentialsException("토큰이 잘못되었습니다.");
	}

}
