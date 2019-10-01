package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.WorkdayFieldMappingDataTransformation;

public interface WorkdayFieldMappingDataTransformationDAO {
	
	List<WorkdayFieldMappingDataTransformation> getDataTransformationByFieldMappingId(
			Long fieldMappingId);

	WorkdayFieldMappingDataTransformation findById(Long dataTransformationId);
	
	void deleteByCondition(Long fieldMappingId);
	
	void save(WorkdayFieldMappingDataTransformation workdayFieldMappingDataTransformation);

}
