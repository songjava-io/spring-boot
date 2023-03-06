package kr.songjava.web.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.songjava.web.configuration.properties.FileProperties;
import kr.songjava.web.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class FileController {

	private static final String ATTACHMENT_FORMAT = "attachment; filename=\"%s\";";

	private final FileProperties fileProperties;

	@GetMapping("/download")
	public ResponseEntity<ByteArrayResource> download(HttpServletRequest request, @RequestParam String filepath,
			@RequestParam String originalName) {
		try {
			String filename = getEncodeFilename(request, originalName);
			Path pathname = Paths.get(fileProperties.rootPath() + filepath);
			log.info("pathname : {}", pathname.toAbsolutePath().toString());
			byte[] data = FileCopyUtils.copyToByteArray(new File(pathname.toAbsolutePath().toString()));
			String disposition = String.format(ATTACHMENT_FORMAT, filename);
			log.info("disposition : {}", disposition);
			return ResponseEntity.ok().contentLength(data.length).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, disposition).body(new ByteArrayResource(data));
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
