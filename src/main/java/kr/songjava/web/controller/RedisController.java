package kr.songjava.web.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RedisController {

	private final RedisTemplate<String, Object> redisTemplate;

	@GetMapping("/redis/set")
	@ResponseBody
	public String setValue() {
		redisTemplate.opsForValue().set("USER_NAME", "송영문");
		return "Test";
	}
	
	@GetMapping("/redis/setHash")
	@ResponseBody
	public String setHash() {
		redisTemplate.opsForHash().put(	"USER_LOGIN", "11", "테스트, 111");
		redisTemplate.opsForHash().put("USER_LOGIN", "22", "홍길동, 222");
		return "Test";
	}
	

	@GetMapping("/redis/getHash")
	@ResponseBody
	public String getHash() {
		redisTemplate.opsForHash().get("USER_NAME", "11");
		return "Test";
	}
	

	@GetMapping("/redis/setList")
	@ResponseBody
	public String setList() {
		redisTemplate.opsForList().leftPush("YEAR", "2023");
		redisTemplate.opsForList().leftPush("YEAR", "2022");
		redisTemplate.opsForList().leftPush("YEAR", "2021");
		redisTemplate.opsForList().leftPush("YEAR", "2020");
		return "setList";
	}

	@GetMapping("/redis/get")
	@ResponseBody
	public String getValue() {
		return (String) redisTemplate.opsForValue().get("USER_NAME");
	}
	
	@GetMapping("/redis/getList")
	@ResponseBody
	public Object getList() {
		return redisTemplate.opsForList().leftPop("YEAR");
	}
	
	
}
