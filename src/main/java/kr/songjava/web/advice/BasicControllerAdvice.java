package kr.songjava.web.advice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import kr.songjava.web.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class BasicControllerAdvice {
	
	private final MappingJackson2JsonView jsonView;
	
	private ModelAndView handleExceptionMessage(Exception e, HttpServletRequest request) {
		String message = null;
		if (e instanceof BindException) {
			BindException bind = (BindException) e;
			FieldError fieldError = bind.getFieldError();
			message = fieldError.getDefaultMessage();
		} else {
			message = e.getMessage();
		}
		
		String requested = request.getHeader("X-Requested-With");
		
		if (requested != null && requested.equals("XMLHttpRequest")) {
			//ModelAndView model = new ModelAndView(jsonView);
			ModelAndView model = new ModelAndView("jsonView", HttpStatus.BAD_REQUEST);
			model.addObject("message", message);
			return model;
		}
		ModelAndView model = new ModelAndView("/error/message.html");
		model.addObject("message",  message);
		return model;
	}

	@ExceptionHandler(BindException.class)
	public ModelAndView handleBindException(BindException e, HttpServletRequest request) {
		log.error("handleBindException", e);
		// bindException 예외에서 추가로직을 구현
		// 에러메세지 페이지나 에러메세지를 아래 메소드에서 처리
		return handleExceptionMessage(e, request);		
	}
	
	
	@ExceptionHandler(RuntimeException.class)
	public ModelAndView handleRuntimeException(RuntimeException e, HttpServletRequest request) {
		log.error("handleRuntimeException", e);
		String requested = request.getHeader("X-Requested-With");
	
		if (requested != null && requested.equals("XMLHttpRequest")) {
			//ModelAndView model = new ModelAndView(jsonView);
			ModelAndView model = new ModelAndView("jsonView", HttpStatus.BAD_REQUEST);
			model.addObject("message", e.getMessage());
			return model;
		}
		
		ModelAndView model = new ModelAndView("/error/message.html");
		model.addObject("message", e.getMessage());
		
		return model;
	}
	
	@ExceptionHandler(ApiException.class)
	public ModelAndView handleApiException(ApiException e, HttpServletRequest request) {
		log.error("handleApiException", e);
		// api 예외에서 추가로직을 구현
		// 에러메세지 페이지나 에러메세지를 아래 메소드에서 처리
		return handleExceptionMessage(e, request);
	}
	
}
