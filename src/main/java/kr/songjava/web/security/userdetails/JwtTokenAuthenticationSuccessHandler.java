package kr.songjava.web.security.userdetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
public class JwtTokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtTokenManager tokenManager;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();

		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		List<String> roles = new ArrayList<String>();
		for (GrantedAuthority authoritie : authorities) {
			roles.add(authoritie.getAuthority());
		}
		// 암호화된 토큰
		String tokenValue = tokenManager
				.encodeLogin(new JwtTokenClaimLogin(userDetails.getUsername(), userDetails.getAuthorities()));
		log.info("tokenValue : {}", tokenValue);

		PrintWriter writer = response.getWriter();

		// 응답에 Jwt 토큰을 출력
		writer.write(tokenValue);
		writer.flush();
		writer.close();
	}

}
