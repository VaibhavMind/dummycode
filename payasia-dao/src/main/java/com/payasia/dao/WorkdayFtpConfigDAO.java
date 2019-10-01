package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.WorkdayFtpConfig;

public interface WorkdayFtpConfigDAO {
	
	WorkdayFtpConfig saveReturn(WorkdayFtpConfig ftpConfig);

	void save(WorkdayFtpConfig ftpConfig);

	WorkdayFtpConfig findByCompanyId(Long companyId);
	
	WorkdayFtpConfig findActiveFtpConfigByCompanyId(Long companyId);

	void update(WorkdayFtpConfig ftpConfig);

	List<WorkdayFtpConfig> findAll();
	
}
