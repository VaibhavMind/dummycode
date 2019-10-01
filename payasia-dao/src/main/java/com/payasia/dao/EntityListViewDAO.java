package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EntityListView;

public interface EntityListViewDAO {
	List<EntityListView> findByConditionEntityAndCompanyId(Long companyId,
			Long entityId);

	EntityListView findById(long entityListViewId);

	EntityListView save(EntityListView entityListView);

	void update(EntityListView entityListView);

	void delete(EntityListView entityListView);

	EntityListView findByEntityIdCompanyIdAndViewName(Long companyId,
			Long entityId, String viewName);

	EntityListView findByEntityIdCompanyIdAndViewNameViewId(Long companyId,
			Long entityMasterID, String viewName, Long viewId);
	
	boolean isViewGridExistInCom(Long viewId, Long companyId);
}
