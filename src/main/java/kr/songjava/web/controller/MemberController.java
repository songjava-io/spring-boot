package kr.songjava.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	public void form(@AuthenticationPrincipal DefaultOAuth2User user, Model model) {
		log.debug("user : {}", user);
		model.addAttribute("kakaoProperties", user.getAttributes().get("properties"));
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
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/save")
	@RequestConfig(menu = "MEMBER")
	@ResponseBody
	public HttpEntity<Boolean> save(HttpServletRequest request, @Validated MemberSaveForm form, 
			OAuth2AuthenticationToken token) throws MalformedURLException, IOException {
		log.info("form : {}", form);
		log.info("nickname : {}", form.getNickname());
		// 사용이 불가능 상태인경우
		
		boolean useAccount = memberService.isUseAccount(form.getAccount());
		log.info("useAccount : {}", useAccount);
		if (useAccount) {
			throw new ApiException("아이디는 중복으로 사용이 불가능 합니다.");
		}
		OAuth2User oauth2User = token.getPrincipal();
		Map<String, Object> properties = (Map<String, Object>) 
				oauth2User.getAttributes().get("properties");
		String profileImage = (String) properties.get("profile_image");
		log.info("profileImage : {}", profileImage);
		
		String image = (String) properties.get("profile_image");
		FileCopyResult copyResult = fileService.copy(new URL(image).openStream(), image);
		// 실제로 저장될 파일 객체
		// form -> member 로 변환
		Member member = Member.builder()
			.account(form.getAccount())
			.password(form.getPassword())
			.nickname(form.getNickname())
			.profileImagePath(copyResult.imagePath())
			.profileImageName(copyResult.originalFilename())
			.oauth2ClientName(token.getAuthorizedClientRegistrationId())
			.oauth2Id(token.getName())
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
		FileCopyResult copyResult = fileService.copy(profileImage.getInputStream(), 
			profileImage.getOriginalFilename());
		// form -> member 로 변환
		Member member = Member.builder()
			.account(form.getAccount())
			.password(form.getPassword())
			.nickname(form.getNickname())
			.profileImagePath(copyResult.imagePath())
			.profileImageName(copyResult.originalFilename())
			.build();
		// 등록 처리
		memberService.save(member);
		return ResponseEntity.ok(true);
	}
	
	
}
