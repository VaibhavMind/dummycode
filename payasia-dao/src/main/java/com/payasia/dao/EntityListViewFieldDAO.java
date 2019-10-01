package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EntityListViewField;

public interface EntityListViewFieldDAO {
	List<EntityListViewField> findAll();

	List<EntityListViewField> findByEntityListViewId(long entityListViewId);

	void save(EntityListViewField entityListViewField);

	List<Long> findViewIDs();

	void deleteByCondition(Long viewId);
	
	List<EntityListViewField> findByEntityListViewId(long entityListViewId,Long companyId);
}
