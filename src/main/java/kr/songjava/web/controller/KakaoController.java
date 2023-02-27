package kr.songjava.web.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/kakao")
@Slf4j
@Controller
public class KakaoController {
	
	String REDIRECT_URI = "http://localhost:8080/kakao/callback";

	@RequestMapping("/login")
	public String login() {
		return "redirect:https://kauth.kakao.com/oauth/authorize?client_id=1b02488664d45d68db38d679f6395f09&redirect_uri=" + REDIRECT_URI + "&response_type=code";
	}
	
	
	@RequestMapping("/callback")
	@ResponseBody
	public String callback(@RequestParam String code) throws URISyntaxException {
		
		MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
		param.add("grant_type", "authorization_code");
		param.add("client_id", "1b02488664d45d68db38d679f6395f09");
		param.add("redirect_uri", REDIRECT_URI);
		param.add("code", code);
		
		RequestEntity<MultiValueMap<String, String>> entity = new RequestEntity<>(param, HttpMethod.POST,
				new URI("https://kauth.kakao.com/oauth/token"));
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(entity, String.class);
		
		log.debug("response : {}", response);
		
		return response.getBody();
	}
}
