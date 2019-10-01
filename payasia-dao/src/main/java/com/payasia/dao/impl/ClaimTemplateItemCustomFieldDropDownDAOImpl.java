package com.payasia.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimTemplateItemCustomFieldDropDownDAO;
import com.payasia.dao.bean.ClaimTemplateItemCustomFieldDropDown;

@Repository
public class ClaimTemplateItemCustomFieldDropDownDAOImpl extends BaseDAO
		implements ClaimTemplateItemCustomFieldDropDownDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimTemplateItemCustomFieldDropDown claimTemplateItemCustomFieldDropDown = new ClaimTemplateItemCustomFieldDropDown();
		return claimTemplateItemCustomFieldDropDown;
	}

	@Override
	public void update(
			ClaimTemplateItemCustomFieldDropDown claimTemplateItemCustomFieldDropDown) {
		super.update(claimTemplateItemCustomFieldDropDown);

	}

	@Override
	public void save(
			ClaimTemplateItemCustomFieldDropDown claimTemplateItemCustomFieldDropDown) {
		super.save(claimTemplateItemCustomFieldDropDown);
	}

	@Override
	public void delete(
			ClaimTemplateItemCustomFieldDropDown claimTemplateItemCustomFieldDropDown) {
		super.delete(claimTemplateItemCustomFieldDropDown);

	}

	@Override
	public ClaimTemplateItemCustomFieldDropDown findByID(
			long claimTemplateItemCustomFieldDropDownId) {
		return super.findById(ClaimTemplateItemCustomFieldDropDown.class,
				claimTemplateItemCustomFieldDropDownId);
	}

	@Override
	public void deleteByCondition(Long customFieldId) {

		String queryString = "DELETE FROM ClaimTemplateItemCustomFieldDropDown e WHERE e.claimTemplateItemCustomField.customFieldId = :customFieldId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("customFieldId", customFieldId);
		q.executeUpdate();

	}

}
