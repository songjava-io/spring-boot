package kr.songjava.web.controller;

import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.songjava.web.interceptor.RequestConfig;
import kr.songjava.web.security.userdetails.SecurityUserDetails;
import kr.songjava.web.service.HomeService;
import kr.songjava.web.service.SecurityOauth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
	
	private final HomeService homeService;
	private final SecurityOauth2Service securityOauth2Service;
	
	@GetMapping(value = { "/", "/home", "/main " })
	@RequestConfig(menu = "HOME")
	public String home(Model model, Authentication authentication) {
		log.info("authentication : {}", authentication);
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			log.info("principal : {}", principal);
		}
		homeService.print();
		
		model.addAttribute("age", 38);
		model.addAttribute("display", false);
		model.addAttribute("frameworks", Arrays.asList("Spring", 
				"Vue.js", "React"));
		return "home";
	}
	
	@GetMapping(value = { "/userinfo" })
	@RequestConfig(menu = "HOME")
	@ResponseBody
	public SecurityUserDetails userinfo(@AuthenticationPrincipal SecurityUserDetails userDetails) {
		return userDetails;
	}
	
	@GetMapping("/authorized-client")
	@ResponseBody
	public OAuth2User authorizedClient(
			@RegisteredOAuth2AuthorizedClient("kakao") OAuth2AuthorizedClient authorizedClient) {
		log.info("authorizedClient : {}", authorizedClient);
		OAuth2User loadUser = securityOauth2Service.loadUser(
			new OAuth2UserRequest(authorizedClient.getClientRegistration(), 
			authorizedClient.getAccessToken()));
		log.info("loadUser : {}", loadUser);
		return loadUser;
	}
	
}
