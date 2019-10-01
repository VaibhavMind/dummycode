package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.WorkdayFtpFieldMapping;

public interface WorkdayFtpFieldMappingDAO {
	
	WorkdayFtpFieldMapping findById(Long fieldMappingId);
	
	void save(WorkdayFtpFieldMapping workdayFTPFieldMapping);

	void update(WorkdayFtpFieldMapping workdayFTPFieldMapping);
	
	void delete(WorkdayFtpFieldMapping workdayFTPFieldMapping);

	WorkdayFtpFieldMapping saveReturn(WorkdayFtpFieldMapping workdayFTPFieldMapping);
	
	List<WorkdayFtpFieldMapping> findAllWorkdayFTPFieldMappingByCompanyId(Long companyId);

	List<WorkdayFtpFieldMapping> findAllWorkdayFTPFieldMappingByCompanyId(Long companyId, boolean isEmployeeData);

	WorkdayFtpFieldMapping findByCompanyIdAndWorkdayFieldId(Long companyId,
			Long fieldMappingId);

	void deleteById(Long fieldMappingId);

	WorkdayFtpFieldMapping getEmployeeNumberMapping(Long companyId);

}
