package kr.songjava.web.mapper;

import kr.songjava.web.domain.EventExcel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EventExcelMapper {

	List<EventExcel> getEventExcelList();

	void saveEventExcels(@Param("eventExcels") List<EventExcel> eventExcels);

}
