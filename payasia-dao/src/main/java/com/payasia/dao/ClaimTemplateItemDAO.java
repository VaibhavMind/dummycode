package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.AssignClaimTemplateConditionDTO;
import com.payasia.dao.bean.ClaimTemplateItem;

public interface ClaimTemplateItemDAO {

	void save(ClaimTemplateItem claimTemplateItem);

	List<ClaimTemplateItem> findByCondition(Long claimTemplateId, Long companyId);

	void update(ClaimTemplateItem claimTemplateItem);

	ClaimTemplateItem findById(Long claimTemplateTypeId);

	List<ClaimTemplateItem> findByTemplateIdCompanyId(Long claimTemplateId,
			Long companyId);

	Long findByConditionCount(Long claimTemplateId, Long companyId);

	void delete(ClaimTemplateItem claimTemplateItem);

	ClaimTemplateItem saveReturn(ClaimTemplateItem claimTemplateItem);

	List<ClaimTemplateItem> findByCondition(
			AssignClaimTemplateConditionDTO assignClaimTemplateConditionDTO);

	List<ClaimTemplateItem> findByCompanyId(Long companyId);

	List<ClaimTemplateItem> findByCondition(
			List<Long> multipleClaimTemplateIdList, Long companyId);

	ClaimTemplateItem findByClaimTemplateItemId(Long claimTemplateItemId, Long companyId);
}
