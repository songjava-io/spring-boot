package kr.songjava.web.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import kr.songjava.web.configuration.properties.FileProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

	private final FileProperties fileProperties;
	
	public FileCopyResult copy(InputStream inputStream, String originalFilename) {
		// 원본파일에서 확장자를 가져옴
		String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1, 
				originalFilename.length());
		// 첨부파일을 실제 저장할 때 저장될 파일명 (중복안되는)
		String randomFilename = UUID.randomUUID().toString() + "." + ext;
		// 파일 저장 시 폴더를 현재날짜로 구분하기 위한 경로를 추가
		String addPath = "/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		// 실제로 파일이 저장경로
		String savePath = new StringBuilder(fileProperties.rootPath()).append(addPath).toString();
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
			FileCopyUtils.copy(inputStream, new FileOutputStream(out));
		} catch (IOException e) {
			log.error("fileCopy", e);
			throw new RuntimeException("파일을 저장하는 과정에 오류가 발생하였습니다.");
		}				
		return new FileCopyResult(imagePath, originalFilename);
	}
	
}
