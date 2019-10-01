package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.WorkdayFieldMaster;

public interface WorkdayFieldMasterDAO {
	
	WorkdayFieldMaster findById(long workdayFieldID);
	
	List<WorkdayFieldMaster> findByEntityName(String entityName);

	WorkdayFieldMaster findBySectionAndFieldName(String sectionName, String fieldName);
}
