package kr.songjava.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
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
	
	@Value("${file.root-path}")
	private String rootPath;
	
	/**
	 * 회원 로그인 화면
	 */
	@GetMapping("/login")
	@RequestConfig(menu = "MEMBER")
	public void login() {
	}
	
	
	@GetMapping("/form")
	@RequestConfig(menu = "MEMBER")
	public String form(@AuthenticationPrincipal DefaultOAuth2User auth2User, Model model) {
		log.debug("auth2User : {}", auth2User);
		log.debug("auth2User.getAttributes() : {}", auth2User.getAttributes());
		model.addAttribute("kakaoProperties", auth2User.getAttributes().get("properties"));
		return "member/form";
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
	@RequestConfig(menu = "MEMBER")
	@ResponseBody
	public HttpEntity<Boolean> save(HttpServletRequest request, @Validated MemberSaveForm form, 
			OAuth2AuthenticationToken auth2User ) {
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
			.oauth2ClientName(auth2User.getAuthorizedClientRegistrationId())
			.oauth2Id(auth2User.getName())
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
	 */
	@PostMapping("/save-upload")
	@RequestConfig(menu = "MEMBER", realnameCheck = true)
	@ResponseBody
	public HttpEntity<Boolean> saveUpload(@Validated MemberSaveUploadForm form,
			@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
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
		// 원본파일명
		String originalFilename = profileImage.getOriginalFilename();
		// 원본파일에서 확장자를 가져옴
		String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1, 
				originalFilename.length());
		// 첨부파일을 실제 저장할 때 저장될 파일명 (중복안되는)
		String randomFilename = UUID.randomUUID().toString() + "." + ext;
		// 파일 저장 시 폴더를 현재날짜로 구분하기 위한 경로를 추가
		String addPath = "/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		// 실제로 파일이 저장경로
		String savePath = new StringBuilder(rootPath).append(addPath).toString();
		log.info("savePath : {}", savePath);
		// 나중에 실제 웹에서 접근시 링크거는 용도 (이미지/동영상)
		String imagePath = addPath + "/" + randomFilename;
		// 저장될 폴더를 File로 변환
		File saveDir = new File(savePath);
		log.info("imagePath : {}", imagePath);
		log.info("originalFilename : {}", originalFilename);
		log.info("ext : {}", ext);
		log.info("randomFilename : {}", randomFilename);
		// 폴더가 없는경우 
		if (!saveDir.isDirectory()) {
			// 폴더 생성
			saveDir.mkdirs();
		}
		// 실제로 저장될 파일 객체
		File out = new File(saveDir, randomFilename);
		try {
			log.info("out : {}", out.getAbsolutePath());
			// 실제 파일을 저장
			FileCopyUtils.copy(profileImage.getInputStream(), new FileOutputStream(out));
		} catch (IOException e) {
			log.error("fileCopy", e);
			throw new RuntimeException("파일을 저장하는 과정에 오류가 발생하였습니다.");
		}		
		// form -> member 로 변환
		Member member = Member.builder()
			.account(form.getAccount())
			.password(form.getPassword())
			.nickname(form.getNickname())
			.profileImagePath(imagePath)
			.profileImageName(originalFilename)
			.build();
		// 등록 처리
		memberService.save(member);
		return ResponseEntity.ok(true);
	}
	
	
}
