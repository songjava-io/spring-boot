package kr.songjava.web.form;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import kr.songjava.web.validation.ValidationSteps;
import lombok.Data;

@Data
@GroupSequence({
	MemberSaveForm.class,
	ValidationSteps.Step1.class,
	ValidationSteps.Step2.class,
	ValidationSteps.Step3.class,
	ValidationSteps.Step4.class,
	ValidationSteps.Step5.class,
	ValidationSteps.Step6.class,
})
public class MemberSaveForm {

	@NotEmpty(groups = ValidationSteps.Step1.class, 
		message = "{MemberSaveForm.account.NotEmpty}")
	@Email(groups = ValidationSteps.Step2.class, 
		message = "{MemberSaveForm.account.Email}")
	private String account;
	
	@NotEmpty(groups = ValidationSteps.Step3.class, 
			message = "{MemberSaveForm.password.NotEmpty}")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,12}$", groups = ValidationSteps.Step4.class, 
		message = "{MemberSaveForm.password.Pattern}")	
	private String password;
	
	@NotEmpty(groups = ValidationSteps.Step5.class, 
			message = "{MemberSaveForm.nickname.NotEmpty}")
	@Length(min = 2, max = 10, groups = ValidationSteps.Step6.class, 
		message = "{MemberSaveForm.nickname.Length}")		
	private String nickname;
	
	
}
