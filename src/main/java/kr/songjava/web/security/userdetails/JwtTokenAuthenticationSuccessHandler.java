package kr.songjava.web.security.userdetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Jwt Token 인증 성공 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenAuthenticationSuccessHandler 
	implements AuthenticationSuccessHandler {
	
	private static final int EXPIRED = (60 * 60) * 24; // JWT 토큰 만료시간
	
	private final JwtEncoder encoder; // Jwt Encoder
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		log.info("onAuthenticationSuccess");
		SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		List<String> roles = new ArrayList<String>();
		for (GrantedAuthority authoritie : authorities) {
			roles.add(authoritie.getAuthority());
		}
		// jwt에 사용할 정보 set
		JwtClaimsSet claimsSet = JwtClaimsSet.builder()
			.subject(userDetails.getUsername())
			.claim("roles", roles)
			.expiresAt(Instant.now().plusSeconds(EXPIRED))
			.build();
		// jwt 생성
		Jwt jwt = encoder.encode(
			JwtEncoderParameters.from(
				JwsHeader.with(MacAlgorithm.HS256).build(), 
				claimsSet
			)
		);
		// 암호화된 토큰
		String tokenValue = jwt.getTokenValue();
		log.info("tokenValue : {}", tokenValue);
		
		PrintWriter writer = response.getWriter();
		
		// 응답에 Jwt 토큰을 출력
		writer.write(tokenValue);
		writer.flush();
		writer.close();
	}

}
