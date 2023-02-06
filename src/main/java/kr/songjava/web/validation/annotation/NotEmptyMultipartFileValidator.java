package kr.songjava.web.validation.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class NotEmptyMultipartFileValidator

		implements ConstraintValidator<NotEmptyMultipartFile, MultipartFile> {

	@Override
	public void initialize(NotEmptyMultipartFile annotation) {
	}

	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
		return multipartFile != null && !multipartFile.isEmpty();
	}

}