package kr.songjava.web.security.userdetails;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import kr.songjava.web.error.CustomErrorAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class UsernamePasswordAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final CustomErrorAttributes customErrorAttributes;
	private final MappingJackson2JsonView jsonView;
	
	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		log.error("failure error", exception);
		customErrorAttributes.storeErrorAttributes(request, exception);
		
		Map<String, Object> error = customErrorAttributes.getErrorAttributes(new ServletWebRequest(request), false);
		error.put("message", "로그인을 실패하였습니다.");
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		log.info("error : {}", error);
		try {
			jsonView.render(error, request, response);
		} catch (Exception e) {
			log.error("render error", e);
		}
	}

}
