package kr.songjava.web.service;

import kr.songjava.web.domain.EventExcel;
import kr.songjava.web.mapper.EventExcelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

	private final EventExcelMapper eventExcelMapper;

	public List<EventExcel> getExcelList() {
		return eventExcelMapper.getEventExcelList();
	}

	public void saveExcels(MultipartFile multipartFile) {
		var eventExcels = new ArrayList<EventExcel>();
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(multipartFile.getInputStream());
			var sheet = workbook.getSheetAt(0);

			var rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getRowNum() == 0) {
					continue;
				}
				/*
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
						case NUMERIC:
							System.out.print(cell.getNumericCellValue() + "t");
							break;
						case STRING:
							System.out.print(cell.getStringCellValue() + "t");
							break;
					}
				}
				 */
				var cell1 = row.getCell(0);
				var cell2 = row.getCell(1);
				var cell3 = row.getCell(2);
				var cell4 = row.getCell(3);
				var cell5 = row.getCell(4);
				var cell6 = row.getCell(5);
				var cell7 = row.getCell(6);

				eventExcels.add(new EventExcel(
					cell1.getStringCellValue(),
					cell2.getStringCellValue(),
					(int) cell3.getNumericCellValue(),
					cell4.getDateCellValue(),
					cell5.getStringCellValue(),
					cell6.getStringCellValue(),
					String.valueOf(cell7.getNumericCellValue())
				));

			}
		} catch (Exception e) {
			log.error("save error", e);
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException ex) {
					log.error("close error", ex);
				}
			}
			throw new RuntimeException("엑셀 업로드 오류가 발생하였습니다.");
		}
		// 한번에 다건 등록처리
		eventExcelMapper.saveEventExcels(eventExcels);
	}

}
