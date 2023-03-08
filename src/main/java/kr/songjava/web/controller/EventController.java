package kr.songjava.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kr.songjava.web.domain.EventExcel;
import kr.songjava.web.exception.ApiException;
import kr.songjava.web.service.EventService;
import kr.songjava.web.validation.annotation.NotEmptyMultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/event")
@Validated
@Slf4j
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;

	private static final String ATTACHMENT_FORMAT = "attachment; filename=\"%s\";";

	@PostMapping("/upload")
	public ResponseEntity<Boolean> upload(@NotEmptyMultipartFile MultipartFile excelFile) {
		eventService.saveExcels(excelFile);
		return ResponseEntity.ok(true);
	}

	@GetMapping("/download")
	public void download(HttpServletRequest request, HttpServletResponse response) {
		List<EventExcel> excelList = eventService.getExcelList();

		try {
			Workbook workbook = new XSSFWorkbook();
			org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("데이터");

			int rowNo = 0;

			Row headerRow = sheet.createRow(rowNo);
			headerRow.createCell(0).setCellValue("계정");
			headerRow.createCell(1).setCellValue("이름");

			for (EventExcel event : excelList) {
				rowNo++;
				Row row = sheet.createRow(rowNo);
				row.createCell(0).setCellValue(event.account());
				row.createCell(1).setCellValue(event.username());
			}

			// 컨텐츠 타입과 파일명 지정

			String disposition = String.format(ATTACHMENT_FORMAT, getEncodeFilename(request, "회원목록.xlsx"));
			response.setContentType("ms-vnd/excel");
			response.setHeader("Content-Disposition", disposition);

			workbook.write(response.getOutputStream());
			workbook.close();

		} catch (Exception e) {
			log.error("download error", e);
			throw new ApiException("파일을 찾을 수가 없습니다.");
		}
	}

	private String getEncodeFilename(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
		String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
		log.info("userAgent : {}", userAgent);
		// 접속한 브라우저 정보
		if (userAgent.contains("MSIE") || userAgent.contains("Trident") || userAgent.contains("Edg")) {
			log.info("userAgent : {}", "MSIE!!!");
			// 인터넷 익스플로러
			return URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
		} else if (userAgent.contains("Firefox")) {
			log.info("userAgent : {}", "FireFox");
			// 파이어폭스
			return new String(filename.getBytes(StandardCharsets.UTF_8.name()), StandardCharsets.ISO_8859_1.name());
		} else if (userAgent.contains("Chrome")) {
			log.info("userAgent : {}", "Chrome");
			// 크롬
			return new String(filename.getBytes(StandardCharsets.UTF_8.name()), StandardCharsets.ISO_8859_1.name());
		}
		// 그외 브라우져
		return new String(filename.getBytes(StandardCharsets.UTF_8.name()), StandardCharsets.ISO_8859_1.name());
	}
}
