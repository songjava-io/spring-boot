package kr.songjava.web.controller;

import kr.songjava.web.service.EventService;
import kr.songjava.web.validation.annotation.NotEmptyMultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/event")
@Validated
@Slf4j
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;

	@PostMapping("/upload")
	public ResponseEntity<Boolean> upload(@NotEmptyMultipartFile MultipartFile excelFile) {
		eventService.saveExcels(excelFile);
		return ResponseEntity.ok(true);
	}
}
