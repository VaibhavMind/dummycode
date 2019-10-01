package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ClaimTemplateItemCustomField;

public interface ClaimTemplateItemCustomFieldDAO {

	void update(ClaimTemplateItemCustomField claimTemplateItemCustomField);

	void save(ClaimTemplateItemCustomField claimTemplateItemCustomField);

	void delete(ClaimTemplateItemCustomField claimTemplateItemCustomField);

	ClaimTemplateItemCustomField findByID(long claimTemplateItemCustomId);

	ClaimTemplateItemCustomField saveReturn(
			ClaimTemplateItemCustomField claimTemplateItemCustomField);

	List<ClaimTemplateItemCustomField> findByClaimTemplateItemId(
			Long claimTemplateItemId);

}
