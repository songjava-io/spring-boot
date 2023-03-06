package kr.songjava.web.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class CustomErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String ERROR_INTERNAL_ATTRIBUTE = CustomErrorAttributes.class.getName() + ".ERROR";

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		storeErrorAttributes(request, ex);
		return null;
	}

	public void storeErrorAttributes(HttpServletRequest request, Exception ex) {
		request.setAttribute(ERROR_INTERNAL_ATTRIBUTE, ex);
	}

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
		Map<String, Object> errorAttributes = getErrorAttributes(webRequest, options.isIncluded(Include.STACK_TRACE));
		errorAttributes.remove("exception");
		errorAttributes.remove("trace");
		errorAttributes.remove("errors");
		return errorAttributes;
	}

	public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
		Map<String, Object> errorAttributes = new LinkedHashMap<>();
		errorAttributes.put("timestamp", new Date());
		addStatus(errorAttributes, webRequest);
		addErrorDetails(errorAttributes, webRequest, includeStackTrace);
		addPath(errorAttributes, webRequest);
		return errorAttributes;
	}

	private void addStatus(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
		Integer status = getAttribute(requestAttributes, RequestDispatcher.ERROR_STATUS_CODE);
		if (status == null) {
			errorAttributes.put("status", 999);
			errorAttributes.put("error", "None");
			return;
		}
		errorAttributes.put("status", status);
		try {
			errorAttributes.put("error", HttpStatus.valueOf(status).getReasonPhrase());
		}
		catch (Exception ex) {
			// Unable to obtain a reason
			errorAttributes.put("error", "Http Status " + status);
		}
	}

	private void addErrorDetails(Map<String, Object> errorAttributes, WebRequest webRequest,
			boolean includeStackTrace) {
		Throwable error = getError(webRequest);
		if (error != null) {
			while (error instanceof ServletException && error.getCause() != null) {
				error = error.getCause();
			}
			errorAttributes.put("exception", error.getClass().getName());
			if (includeStackTrace) {
				addStackTrace(errorAttributes, error);
			}
		}
		addErrorMessage(errorAttributes, webRequest, error);
	}

	private void addErrorMessage(Map<String, Object> errorAttributes, WebRequest webRequest, Throwable error) {
		BindingResult result = extractBindingResult(error);
		if (result == null) {
			addExceptionErrorMessage(errorAttributes, webRequest, error);
		}
		else {
			addBindingResultErrorMessage(errorAttributes, result);
		}
	}

	private void addExceptionErrorMessage(Map<String, Object> errorAttributes, WebRequest webRequest, Throwable error) {
		errorAttributes.put("message", getMessage(webRequest, error));
	}

	protected String getMessage(WebRequest webRequest, Throwable error) {
		Object message = getAttribute(webRequest, RequestDispatcher.ERROR_MESSAGE);
		if (!ObjectUtils.isEmpty(message)) {
			return message.toString();
		}
		if (error != null && StringUtils.hasLength(error.getMessage())) {
			return error.getMessage();
		}
		return "No message available";
	}

	private void addBindingResultErrorMessage(Map<String, Object> errorAttributes, BindingResult result) {
		FieldError fieldError = result.getFieldError();
		errorAttributes.put("message", fieldError.getDefaultMessage());
		errorAttributes.put("field", fieldError.getField());
		errorAttributes.put("errors", result.getAllErrors());
	}

	private BindingResult extractBindingResult(Throwable error) {
		if (error instanceof BindingResult) {
			return (BindingResult) error;
		}
		return null;
	}

	private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
		StringWriter stackTrace = new StringWriter();
		error.printStackTrace(new PrintWriter(stackTrace));
		stackTrace.flush();
		errorAttributes.put("trace", stackTrace.toString());
	}

	private void addPath(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
		String path = getAttribute(requestAttributes, RequestDispatcher.ERROR_REQUEST_URI);
		if (path != null) {
			errorAttributes.put("path", path);
		}
	}

	@Override
	public Throwable getError(WebRequest webRequest) {
		Throwable exception = getAttribute(webRequest, ERROR_INTERNAL_ATTRIBUTE);
		if (exception == null) {
			exception = getAttribute(webRequest, RequestDispatcher.ERROR_EXCEPTION);
		}
		logger.info("getError exception : {}", exception);
		// store the exception in a well-known attribute to make it available to metrics
		// instrumentation.
		webRequest.setAttribute(ErrorAttributes.ERROR_ATTRIBUTE, exception, WebRequest.SCOPE_REQUEST);
		return exception;
	}

	@SuppressWarnings("unchecked")
	private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
		return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

}