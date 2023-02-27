package kr.songjava.web.security.userdetails;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final MappingJackson2JsonView jsonView;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		OAuth2User user = (OAuth2User) authentication.getPrincipal();
		log.info("user : {}", user);
		try {
			jsonView.render(user.getAttributes(), request, response);
		} catch (Exception e) {
			log.error("render", e);
		}
	}

}
