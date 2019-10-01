package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.WorkdayPaygroup;

public interface WorkdayPaygroupDAO {

	WorkdayPaygroup findById(long workdayPayGroupId);
	
	List<WorkdayPaygroup> findByCompanyId(long companyId);

	WorkdayPaygroup findByPaygroupName(String workdayPaygroupName, long companyId);

	WorkdayPaygroup findByPaygroupId(String workdayPaygroupId, long companyId);
}
