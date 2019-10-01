package com.payasia.dao;

import java.util.Date;
import java.util.List;

import com.payasia.common.dto.PayslipReleaseConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CompanyPayslipRelease;

public interface CompanyPayslipReleaseDAO {

	CompanyPayslipRelease findById(long companyPayslipReleaseId);

	void save(CompanyPayslipRelease companyPayslipRelease);

	void delete(CompanyPayslipRelease companyPayslipRelease);

	void update(CompanyPayslipRelease companyPayslipRelease);

	List<CompanyPayslipRelease> findByCondition(
			PayslipReleaseConditionDTO conditionDTO, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	Integer getCountByCondition(PayslipReleaseConditionDTO conditionDTO,
			Long companyId);

	CompanyPayslipRelease findByCondition(Long companyPayslipReleaseId,
			Long monthId, int year, Integer part, Long companyId);

	List<CompanyPayslipRelease> findByCompany(Long companyId);

	CompanyPayslipRelease findMaxPayslipRelease(Long companyId);

	CompanyPayslipRelease findLastPayslipReleased(Long companyId);

	boolean isFuturePaySlipReleased(Long monthId, int year, Long companyId, int part);
	
	public CompanyPayslipRelease findByCompanyPayslipReleaseId(Long companyPayslipReleaseId,Long companyId);

	List<CompanyPayslipRelease> findByCondition(Long companyId, boolean isReleaseStatus, Date currntDate);

}
