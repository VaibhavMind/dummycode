package com.payasia.logic;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PaySlipReleaseForm;
import com.payasia.common.form.PaySlipReleaseFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CompanyPayslipRelease;

public interface PaySlipReleaseLogic {

	PaySlipReleaseFormResponse viewPayslipReleaseList(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);

	String savePaySlipRelease(PaySlipReleaseForm paySlipReleaseForm,
			Long companyId);

	String deletePaySlipRelease(Long companyPayslipReleaseId, Long companyId);

	PaySlipReleaseForm getPaySlipReleaseDetails(long companyPayslipReleaseId,long companyId);

	Integer getPaySlipPart(Long companyId);

	String editPaySlipRelease(PaySlipReleaseForm paySlipReleaseForm,
			Long companyId, long companyPayslipReleaseId);

	String getPayslipSendMailDetails(Long companyId);

	void sendPayslipReleaseEmailTO(Long companyId, CompanyPayslipRelease companyPayslipRelease);

	String getPaySlipEmailTo(Long companyId);

	

}
