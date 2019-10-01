package com.payasia.dao;

import com.payasia.dao.bean.ClaimTemplateItemClaimType;

public interface ClaimTemplateItemClaimTypeDAO {

	void update(ClaimTemplateItemClaimType claimTemplateItemClaimType);

	void save(ClaimTemplateItemClaimType claimTemplateItemClaimType);

	void delete(ClaimTemplateItemClaimType claimTemplateItemClaimType);

	ClaimTemplateItemClaimType findByID(long claimTemplateItemClaimTypeId);

	ClaimTemplateItemClaimType saveReturn(
			ClaimTemplateItemClaimType claimTemplateItemClaimType);

}
