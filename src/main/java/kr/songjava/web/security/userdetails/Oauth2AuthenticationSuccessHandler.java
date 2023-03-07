package kr.songjava.web.security.userdetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
	private final FrontendProperties frontendProperties;
	
	private final JwtTokenManager tokenManager;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// OAuth2 인증토큰으로 변환
		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
		OAuth2User user = token.getPrincipal();
		Map<String, Object> attributes = user.getAttributes();
		Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) account.get("profile");
		log.info("profile : {}", profile);
		String id = attributes.get("id").toString();
		OAuth2KakaoAccount kakaoAccount = new OAuth2KakaoAccount(
			id, 
			profile.get("nickname").toString(), 
			profile.get("profile_image_url").toString(), 
			account.get("email") != null ? account.get("email").toString() : null, 
			account.get("gender") != null ? account.get("gender").toString() : null
		);
		Map<String, Object> result = new HashMap<String, Object>();
		
		// Oauth2 인증된 회원이 실제 DB에 등록이되어있는지 조회
		Member member = memberService.selectMemberOauth2(
			token.getAuthorizedClientRegistrationId(), 
			token.getName());
		if (member == null) {
			// 추후에 클라이언트에서 서버로 요청이 오는경우 다시 재사용하기 위하여 Redis에 저장
			// Key는 다시 조회할 때 Key값으로 사용해야하므로 중복되지 않게 set
			// 다른 사용자와 겹치지 않게 HashKey을 중복되지 않는 값으로 set 한다.
			// redis에 저장을 할때는 string 변환해서 보내는게 좋다. (Object, class, Map 으로 보내는경우 직렬화 관련 이슈가 있음)
			String body = objectMapper.writeValueAsString(kakaoAccount);
			log.info("id : {}", id);
			log.info("body : {}", body);
			//redisTemplate.expire("OAUTH2_USER_KAKAO", 10, TimeUnit.MINUTES);
			redisTemplate.opsForValue().set("OAUTH2_USER_KAKAO:" + id, body);
			
			result.put("user", kakaoAccount);
			// oAuth 인증 유지용 토큰 생성
			result.put("token", tokenManager.encodeOAuth(id));
			// 회원가입 필요
			result.put("signup", true);
		} else {
			// oAuth 인증 유지용 토큰 생성
			result.put("token", tokenManager.encodeLogin(new JwtTokenClaimLogin(member.getAccount(),
					Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))));
			// 이미 가입되어 있음..
			result.put("signup", false);
		}
		
		// Token을 다시 Client에서 Server와 인증용도로 사용하기 위해 Header에 내려준다. 응답 Body 데이터와 분리
		response.setContentType(MediaType.TEXT_HTML_VALUE);
		response.setCharacterEncoding(StandardCharset.UTF_8.name());
		
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
