package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ClaimTemplateItemShortlist;

public interface ClaimTemplateItemShortlistDAO {

	void save(ClaimTemplateItemShortlist claimTemplateItemShortlist);

	void deleteByCondition(Long claimTemplateItemId);

	ClaimTemplateItemShortlist findByID(long claimTemplateItemId);

	void delete(ClaimTemplateItemShortlist claimTemplateItemShortlist);

	ClaimTemplateItemShortlist findByClaimTemplateItemShortlistID(Long filterId, Long companyId);

	List<ClaimTemplateItemShortlist> findByCondition(Long claimTemplateItemId, Long companyId);

}
