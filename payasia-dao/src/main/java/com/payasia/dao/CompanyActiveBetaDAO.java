package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.CompanyActiveBeta;

public interface CompanyActiveBetaDAO {

	CompanyActiveBeta findByID(long id);
	
	CompanyActiveBeta findByCompanyId(long companyId);
	
	List<CompanyActiveBeta> findAllActive();

}
