package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.RoleSectionMapping;

/**
 * @author vivekjain
 * 
 */
public interface RoleSectionMappingDAO {

	RoleSectionMapping findById(Long roleSectionMappingId);

	void delete(RoleSectionMapping roleSectionMapping);

	void save(RoleSectionMapping roleSectionMapping);

	void update(RoleSectionMapping roleSectionMapping);

	void deleteByCondition(long roleId);

	List<RoleSectionMapping> findByRoleId(Long roleId);

	List<Long> findByRoleIds(List<Long> roleIdList);

	void deleteByFormIdAndCompanyId(Long formId, Long companyId);
	
	void deleteByCondition(long roleId,Long companyId);
	
	

}
