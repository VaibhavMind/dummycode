package com.payasia.dao;

import com.payasia.dao.bean.ClaimTemplateItemCustomFieldDropDown;

public interface ClaimTemplateItemCustomFieldDropDownDAO {

	void update(
			ClaimTemplateItemCustomFieldDropDown claimTemplateItemCustomFieldDropDown);

	void save(
			ClaimTemplateItemCustomFieldDropDown claimTemplateItemCustomFieldDropDown);

	void delete(
			ClaimTemplateItemCustomFieldDropDown claimTemplateItemCustomFieldDropDown);

	ClaimTemplateItemCustomFieldDropDown findByID(
			long claimTemplateItemCustomFieldDropDownId);

	void deleteByCondition(Long customFieldId);

}
