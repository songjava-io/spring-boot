package kr.songjava.web.form;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import kr.songjava.web.validation.ValidationSteps;
import lombok.Data;

@Data
@GroupSequence({
	BoardCommentSaveForm.class,
	ValidationSteps.Step1.class,
	ValidationSteps.Step2.class,
})
public class BoardCommentSaveForm {

	private int boardSeq;
	
	@NotEmpty(groups = ValidationSteps.Step1.class, message = "{BoardCommentSaveForm.comment.NotEmpty}")
	@Length(min = 5, max = 100, groups = ValidationSteps.Step2.class, message = "{BoardCommentSaveForm.comment.Length}")
	private String comment;

}
