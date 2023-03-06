package kr.songjava.web.security.userdetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.StandardCharset;

import kr.songjava.web.configuration.properties.FrontendProperties;
import kr.songjava.web.domain.Member;
import kr.songjava.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final MemberService memberService;
	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;
	private final JwtEncoder encoder;
	private final FrontendProperties frontendProperties;
	
	private static final int EXPIRED = (60 * 60) * 24;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// OAuth2 인증토큰으로 변환
		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
		OAuth2User user = (OAuth2User) token.getPrincipal();
		log.info("token : {}", token);
		log.info("user : {}", user);
		
		Map<String, Object> attributes = user.getAttributes();
		log.info("attributes : {}", attributes);
		Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) account.get("profile");
		log.info("profile : {}", profile);
		String hashKey = attributes.get("id").toString();
		OAuth2KakaoAccount kakaoAccount = new OAuth2KakaoAccount(
			Integer.parseInt(hashKey), 
			profile.get("nickname").toString(), 
			profile.get("profile_image_url").toString(), 
			account.get("email").toString(), 
			account.get("gender").toString()
		);
		// 추후에 클라이언트에서 서버로 요청이 오는경우 다시 재사용하기 위하여 Redis에 저장
		// Key는 다시 조회할 때 Key값으로 사용해야하므로 중복되지 않게 set
		// 다른 사용자와 겹치지 않게 HashKey을 중복되지 않는 값으로 set 한다.
		// redis에 저장을 할때는 string 변환해서 보내는게 좋다. (Object, class, Map 으로 보내는경우 직렬화 관련 이슈가 있음)
		String body = objectMapper.writeValueAsString(kakaoAccount);
		redisTemplate.opsForHash().put("OAUTH2_USER_KAKAO", hashKey, body);
		JwtClaimsSet claimsSet = JwtClaimsSet.builder()
			.subject("OAUTH2")
			.claim("OAUTH2_USER_HASH_KEY", hashKey)
			.expiresAt(Instant.now().plusSeconds(EXPIRED))
			.build();
		Jwt jwt = encoder.encode(
			JwtEncoderParameters.from(
				JwsHeader.with(MacAlgorithm.HS256).build(), 
				claimsSet
			)
		);
		String tokenValue = jwt.getTokenValue();
		// Token을 다시 Client에서 Server와 인증용도로 사용하기 위해 Header에 내려준다. 응답 Body 데이터와 분리
		response.addHeader(HttpHeaders.AUTHORIZATION, tokenValue);
		response.setContentType(MediaType.TEXT_HTML_VALUE);
		response.setCharacterEncoding(StandardCharset.UTF_8.name());
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("data", kakaoAccount);
		result.put("token", tokenValue);
		
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
		
		String callbackData = objectMapper.writeValueAsString(result);
		String data = String.format("""
			<script>
				// 특정 도메인만 허용
				opener.window.postMessage('%s', '%s');
				self.close();
			</script>
		""", callbackData, frontendProperties.domain());
		log.info("data : {}", data);
		PrintWriter writer = response.getWriter();
		writer.append(data);
		writer.flush();
		writer.close();		
	}

}
