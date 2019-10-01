package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.EmployeeCalendarConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeCalendarConfig;

public interface EmployeeCalendarConfigDAO {

	EmployeeCalendarConfig findByID(Long employeeCalendarConfigId);

	void save(EmployeeCalendarConfig employeeCalendarConfig);

	void delete(EmployeeCalendarConfig employeeCalendarConfig);

	void update(EmployeeCalendarConfig employeeCalendarConfig);

	List<EmployeeCalendarConfig> findByCondition(
			EmployeeCalendarConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	int getCountForCondition(EmployeeCalendarConditionDTO conditionDTO,
			Long companyId);

	List<EmployeeCalendarConfig> findByCalTempId(Long companyCalTempId);

	List<EmployeeCalendarConfig> findByCalPatternId(Long calPatternId);

	EmployeeCalendarConfig checkEmpCalTempByDate(Long empCalConfigId,
			Long employeeId, String date, String dateFormat);

	EmployeeCalendarConfig findByEmpIdAndEndDate(Long employeeId);

}
