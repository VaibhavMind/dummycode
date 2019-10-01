package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ClaimApplicationItemCustomField;

public interface ClaimApplicationItemCustomFieldDAO {

	void update(ClaimApplicationItemCustomField claimApplicationItemCustomField);

	void save(ClaimApplicationItemCustomField claimApplicationItemCustomField);

	void delete(ClaimApplicationItemCustomField claimApplicationItemCustomField);

	ClaimApplicationItemCustomField findByID(long claimApplicationItemCustomFieldId);

	ClaimApplicationItemCustomField saveReturn(ClaimApplicationItemCustomField claimApplicationItemCustomField);

	List<ClaimApplicationItemCustomField> findByClaimApplicationItemID(Long claimApplicationItem);

}
