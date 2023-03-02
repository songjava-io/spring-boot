package kr.songjava.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.songjava.web.domain.Member;
import kr.songjava.web.exception.ApiException;
import kr.songjava.web.form.MemberSaveForm;
import kr.songjava.web.form.MemberSaveUploadForm;
import kr.songjava.web.interceptor.RequestConfig;
import kr.songjava.web.service.FileCopyResult;
import kr.songjava.web.service.FileService;
import kr.songjava.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회원 컨트롤러
 */
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Validated
@Slf4j
public class MemberController {

	private final MemberService memberService;
	private final FileService fileService;
	
	@Value("${file.root-path}")
	private String rootPath;
	
	/**
	 * 회원 로그인 화면
	 */
	@GetMapping("/login")
	@RequestConfig(menu = "MEMBER")
	public void login() {
	}
	
	/**
	 * 회원 정보 입력 화면
	 */
	@GetMapping("/form")
	@RequestConfig(menu = "MEMBER")
	public void form() {
		
	}
	
	/**
	 * 회원 정보 입력 화면 (파일첨부 포함)
	 */
	@GetMapping("/form-upload")
	@RequestConfig(menu = "MEMBER")
	public void formUpload() {
	}
	
	/**
	 * 회원 가입 완료 화면
	 */
	@GetMapping("/join-complete")
	@RequestConfig(menu = "MEMBER")
	public void joinComplete() {
	}
	
	/**
	 * 본인인증 성공 후 콜백 (데이터를 받는 역활)
	 */
	@GetMapping("/realname-callback")
	@RequestConfig(menu = "MEMBER")
	@ResponseBody
	public String realnameCallback(HttpServletRequest request) {
		request.getSession().setAttribute("realnameCheck", true);
		return "ok";
	}
	
	/**
	 * 회원 가입 처리.
	 * @param form
	 * @return
	 */
	@PostMapping("/save")
	@RequestConfig(menu = "MEMBER", realnameCheck = true)
	@ResponseBody
	public HttpEntity<Boolean> save(HttpServletRequest request, @Validated MemberSaveForm form) {
		log.info("form : {}", form);
		log.info("nickname : {}", form.getNickname());
		// 사용이 불가능 상태인경우
		
		boolean useAccount = memberService.isUseAccount(form.getAccount());
		log.info("useAccount : {}", useAccount);
		if (useAccount) {
			throw new ApiException("아이디는 중복으로 사용이 불가능 합니다.");
		}
		// form -> member 로 변환
		Member member = Member.builder()
			.account(form.getAccount())
			.password(form.getPassword())
			.nickname(form.getNickname())
			.build();
		// 등록 처리
		memberService.save(member);
		// 본인인증 값이 세션에 필요가 없으므로 제거
		request.getSession().removeAttribute("realnameCheck");
		return ResponseEntity.ok(true);
	}
	
	
	/**
	 * 회원 가입 처리. (파일첨부 포함)
	 * @param form
	 * @return
	 * @throws IOException 
	 */
	@PostMapping("/save-upload")
	@RequestConfig(menu = "MEMBER", realnameCheck = true)
	@ResponseBody
	public HttpEntity<Boolean> saveUpload(@Validated MemberSaveUploadForm form) throws IOException {
		log.info("form : {}", form);
		log.info("nickname : {}", form.getNickname());
		// 사용이 불가능 상태인경우
		
		boolean useAccount = memberService.isUseAccount(form.getAccount());
		log.info("useAccount : {}", useAccount);
		if (useAccount) {
			throw new ApiException("아이디는 중복으로 사용이 불가능 합니다.");
		}
		// 파일첨부 객체
		MultipartFile profileImage = form.getProfileImage();
		FileCopyResult result = fileService.copy(profileImage.getInputStream(), profileImage.getOriginalFilename());
		// form -> member 로 변환
		Member member = Member.builder()
			.account(form.getAccount())
			.password(form.getPassword())
			.nickname(form.getNickname())
			.profileImagePath(result.imagePath())
			.profileImageName(result.originalFilename())
			.build();
		// 등록 처리
		memberService.save(member);
		return ResponseEntity.ok(true);
	}
	
	
}
