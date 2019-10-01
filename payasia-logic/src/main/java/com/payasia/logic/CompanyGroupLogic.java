package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.CompanyGroupForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface CompanyGroupLogic {

	CompanyGroupForm viewGroup(PageRequest pageDTO, SortCondition sortDTO,
			Long companyID);

	void addCompanyGroup(CompanyGroupForm companyGroupForm);

	CompanyGroupForm getCompanyGroupById(Long groupId);

	void upadateCompanyGroup(CompanyGroupForm companyGroupForm);

	void deleteClaimCategory(Long groupId);

	CompanyGroupForm checkGroup(CompanyGroupForm companyGroupForm);

	CompanyGroupForm checkGroupUpdate(CompanyGroupForm companyGroupForm);

}
