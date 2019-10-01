package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.CompanyAddressMapping;

/**
 * @author vivekjain
 * 
 */
public interface CompanyAddressMappingDAO {

	void update(CompanyAddressMapping companyAddressMapping);

	void delete(CompanyAddressMapping companyAddressMapping);

	void save(CompanyAddressMapping companyAddressMapping);

	CompanyAddressMapping findById(Long companyAddressMappingId);

	List<CompanyAddressMapping> findByCondition(Long companyId);

	void deleteByCondition(Long companyId);

}
