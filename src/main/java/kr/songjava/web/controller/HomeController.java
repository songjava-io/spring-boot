package kr.songjava.web.controller;

import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.songjava.web.interceptor.RequestConfig;
import kr.songjava.web.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
	
	private final HomeService homeService;
	
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
}
