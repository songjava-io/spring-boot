package kr.songjava.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;


//@Controller
@RequestMapping("/error-custom")
@Slf4j
public class DefaultErrorController implements ErrorController {

	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	public String errorHtml(Model model, HttpServletRequest request, HttpServletResponse response) {
		
		Throwable error = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		log.error("errorHtml", error);
		log.info("status : {}", status);
		model.addAttribute("timestamp", new Date());
		model.addAttribute("message", error.getMessage());
		model.addAttribute("path", request.getRequestURI());
		model.addAttribute("status", status);
		return "error/custom";
	}
	
	@RequestMapping
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Throwable error = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		HttpStatus httpStatus = null;
		if (status != null) {
			httpStatus = HttpStatus.resolve(Integer.parseInt(status.toString()));
		} else {
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		log.error("error", error);
		log.debug("httpStatus : {}", httpStatus);
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", new Date());
		body.put("message", error.getMessage());
		body.put("path", request.getRequestURI());
		body.put("status", httpStatus.value());
		return new ResponseEntity<>(body, httpStatus);
	}
}
