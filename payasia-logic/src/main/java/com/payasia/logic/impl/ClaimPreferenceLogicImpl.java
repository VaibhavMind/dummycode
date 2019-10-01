package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.logic.ClaimPreferenceLogic;
import com.payasia.logic.DataExportUtils;

@Component
public class ClaimPreferenceLogicImpl implements ClaimPreferenceLogic {

	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;
	@Resource
	DynamicFormDAO dynamicFormDAO;
	@Resource
	DataExportUtils dataExportUtils;

	@Override
	public ClaimPreferenceForm getClaimPreference(Long companyId) {
		ClaimPreferenceForm claimPreferenceForm = new ClaimPreferenceForm();
		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreferenceVO != null) {
			String startDate = DateUtils.timeStampToString(claimPreferenceVO.getStartDayMonth(),
					PayAsiaConstants.DEFAULT_DATE_FORMAT);
			startDate = startDate.substring(0, startDate.length() - 5);
			String endDate = DateUtils.timeStampToString(claimPreferenceVO.getEndDayMonth(),
					PayAsiaConstants.DEFAULT_DATE_FORMAT);
			endDate = endDate.substring(0, endDate.length() - 5);
			claimPreferenceForm.setPayasiaAdminCanApprove(claimPreferenceVO.isPayAsiaAdminCanApprove());
			claimPreferenceForm.setUseSystemMailAsFromAddress(claimPreferenceVO.isUseSystemMailAsFromAddress());
			claimPreferenceForm.setStartDayMonth(startDate);
			claimPreferenceForm.setEndDayMonth(endDate);
			claimPreferenceForm.setShowPaidStatusForClaimBatch(claimPreferenceVO.isShowPaidStatusForClaimBatch());
			claimPreferenceForm.setShowMonthlyConsFinReportGroupingByEmp(
					claimPreferenceVO.isShowMonthlyConsFinReportGroupingByEmp());
			claimPreferenceForm.setDefaultEmailCC(claimPreferenceVO.isDefaultEmailCC());
			claimPreferenceForm.setCreatedDateSortOrder(claimPreferenceVO.getGridCreatedDateSortOrder() == null ? ""
					: claimPreferenceVO.getGridCreatedDateSortOrder());
			claimPreferenceForm.setClaimNumberSortOrder(claimPreferenceVO.getGridClaimNumberSortOrder() == null ? ""
					: claimPreferenceVO.getGridClaimNumberSortOrder());
			claimPreferenceForm.setClaimItemNameSortOrder(claimPreferenceVO.getClaimItemNameSortOrder() == null ? ""
					: claimPreferenceVO.getClaimItemNameSortOrder());
			claimPreferenceForm.setClaimItemDateSortOrder(claimPreferenceVO.getClaimItemDateSortOrder() == null ? ""
					: claimPreferenceVO.getClaimItemDateSortOrder());
		    claimPreferenceForm.setClaimNumberSortOrder(claimPreferenceVO.getGridClaimNumberSortOrder() == null ? ""
					: claimPreferenceVO.getGridClaimNumberSortOrder());
			if (claimPreferenceVO.getBankAccountNameDictionary() != null) {
				claimPreferenceForm.setBankAccountNameDictId(
						claimPreferenceVO.getBankAccountNameDictionary().getDataDictionaryId());
			}
			if (claimPreferenceVO.getBankAccountNumDictionary() != null) {
				claimPreferenceForm
						.setBankAccountNumDictId(claimPreferenceVO.getBankAccountNumDictionary().getDataDictionaryId());
			}
			if (claimPreferenceVO.getBankNameDictionary() != null) {
				claimPreferenceForm.setBankNameDictId(claimPreferenceVO.getBankNameDictionary().getDataDictionaryId());
			}

			if (claimPreferenceVO.getPwdForAttachmentMail() != null) {
				claimPreferenceForm.setAttachmentMailBasedOnCustomField(
						claimPreferenceVO.getPwdForAttachmentMail().getDataDictionaryId());
			}
			claimPreferenceForm.setShowAttachmentForMail(claimPreferenceVO.getAttachmentForMail());
			claimPreferenceForm.setAdditionalBalanceFrom(claimPreferenceVO.getAdditionalBalanceFrom());
			claimPreferenceForm.setAdminCanAmendDataBeforeApproval(claimPreferenceVO.isAdminCanAmendDataBeforeApproval()); 
		}

		return claimPreferenceForm;

	}

	@Override
	public void saveClaimPreference(ClaimPreferenceForm claimPreferenceForm, Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		ClaimPreference claimPreference = new ClaimPreference();
		claimPreference.setCompany(companyVO);
		String year = " 2013";
		String startDayMonth = claimPreferenceForm.getStartDayMonth() + year;
		String endDayMonth = claimPreferenceForm.getEndDayMonth() + year;

		claimPreference
				.setStartDayMonth(DateUtils.stringToTimestamp(startDayMonth, PayAsiaConstants.DEFAULT_DATE_FORMAT));
		claimPreference.setEndDayMonth(DateUtils.stringToTimestamp(endDayMonth, PayAsiaConstants.DEFAULT_DATE_FORMAT));
		claimPreference.setPayAsiaAdminCanApprove(claimPreferenceForm.getPayasiaAdminCanApprove());
		claimPreference.setUseSystemMailAsFromAddress(claimPreferenceForm.getUseSystemMailAsFromAddress());
		claimPreference.setDefaultEmailCC(claimPreferenceForm.isDefaultEmailCC());
		claimPreference.setAdminCanAmendDataBeforeApproval(claimPreferenceForm.isAdminCanAmendDataBeforeApproval());
		claimPreference.setShowPaidStatusForClaimBatch(claimPreferenceForm.isShowPaidStatusForClaimBatch());
		if ((claimPreferenceForm.getShowAttachmentForMail() != null)
				&& claimPreferenceForm.getAttachmentMailBasedOnCustomField() != null) {
			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(claimPreferenceForm.getAttachmentMailBasedOnCustomField());
			claimPreference.setPwdForAttachmentMail(dataDictionary);
			claimPreference.setAttachmentForMail(claimPreferenceForm.getShowAttachmentForMail());
		} else {
			claimPreference.setAttachmentForMail(false);
		}
		claimPreference.setAdditionalBalanceFrom(claimPreferenceForm.getAdditionalBalanceFrom());

		claimPreference.setShowMonthlyConsFinReportGroupingByEmp(
				claimPreferenceForm.isShowMonthlyConsFinReportGroupingByEmp());
		if (StringUtils.isNotBlank(claimPreferenceForm.getCreatedDateSortOrder())
				&& !claimPreferenceForm.getCreatedDateSortOrder().equalsIgnoreCase("0")) {
			claimPreference.setGridCreatedDateSortOrder(claimPreferenceForm.getCreatedDateSortOrder());
		} else {
			claimPreference.setGridCreatedDateSortOrder(null);
		}

		if (StringUtils.isNotBlank(claimPreferenceForm.getClaimNumberSortOrder())
				&& !claimPreferenceForm.getClaimNumberSortOrder().equalsIgnoreCase("0")) {
			claimPreference.setGridClaimNumberSortOrder(claimPreferenceForm.getClaimNumberSortOrder());
		} else {
			claimPreference.setGridClaimNumberSortOrder(null);
		}
		if (StringUtils.isNotBlank(claimPreferenceForm.getClaimItemNameSortOrder())
				&& !claimPreferenceForm.getClaimItemNameSortOrder().equalsIgnoreCase("0")) {
			claimPreference.setClaimItemNameSortOrder(claimPreferenceForm.getClaimItemNameSortOrder());
		} else {
			claimPreference.setClaimItemNameSortOrder(null);
		}

		if (StringUtils.isNotBlank(claimPreferenceForm.getClaimItemDateSortOrder())
				&& !claimPreferenceForm.getClaimItemDateSortOrder().equalsIgnoreCase("0")) {
			claimPreference.setClaimItemDateSortOrder(claimPreferenceForm.getClaimItemDateSortOrder());
		} else {
			claimPreference.setClaimItemDateSortOrder(null);
		}

		if (claimPreferenceForm.getBankAccountNameDictId() != null) {
			DataDictionary bankAccountNameDict = dataDictionaryDAO
					.findById(claimPreferenceForm.getBankAccountNameDictId());
			claimPreference.setBankAccountNameDictionary(bankAccountNameDict);
		} else {
			claimPreference.setBankAccountNameDictionary(null);
		}

		if (claimPreferenceForm.getBankAccountNumDictId() != null) {
			DataDictionary bankAccountNumDict = dataDictionaryDAO
					.findById(claimPreferenceForm.getBankAccountNumDictId());
			claimPreference.setBankAccountNumDictionary(bankAccountNumDict);
		} else {
			claimPreference.setBankAccountNumDictionary(null);
		}

		if (claimPreferenceForm.getBankNameDictId() != null) {
			DataDictionary bankNameDict = dataDictionaryDAO.findById(claimPreferenceForm.getBankNameDictId());
			claimPreference.setBankNameDictionary(bankNameDict);
		} else {
			claimPreference.setBankNameDictionary(null);
		}

		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		if (claimPreferenceVO == null) {
			claimPreferenceDAO.saveReturn(claimPreference);
		} else {
			claimPreference.setClaimPreferenceId(claimPreferenceVO.getClaimPreferenceId());
			claimPreferenceDAO.update(claimPreference);
		}

	}

	@Override
	public boolean getAdditionalValueOfClaimPreference(Long companyId) {
		boolean flag = false;

		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);

		if (claimPreferenceVO == null || claimPreferenceVO.getAdditionalBalanceFrom() == null
				|| !claimPreferenceVO.getAdditionalBalanceFrom()) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}

	/**
	 * get Employee FilterList.
	 * 
	 * @param companyId
	 *            the company id
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	@Override
	public List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();

		EntityMaster entityMasterVO = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		Long entityId = entityMasterVO.getEntityId();
		HashMap<Long, Tab> tabMap = new HashMap<>();

		List<DataDictionary> dataDictionaryCompanyList = dataDictionaryDAO.findByConditionEntityAndCompanyId(companyId,
				entityId, null);
		if (dataDictionaryCompanyList != null) {

			for (DataDictionary dataDictionary : dataDictionaryCompanyList) {

				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();

				if (dataDictionary.getFieldType()
						.equalsIgnoreCase(PayAsiaConstants.DATA_DICTIONARY_FIELD_TYPE_STATIC)) {
					employeeFilterListForm.setDataDictionaryId(dataDictionary.getDataDictionaryId());
					employeeFilterListForm.setFieldName(dataDictionary.getLabel());
				}

				Tab tab = tabMap.get(dataDictionary.getFormID());
				if (dataDictionary.getFieldType()
						.equalsIgnoreCase(PayAsiaConstants.DATA_DICTIONARY_FIELD_TYPE_DYNAMIC)) {
					if (tab == null) {
						DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId, entityId,
								dataDictionary.getFormID());
						tab = dataExportUtils.getTabObject(dynamicForm);
						tabMap.put(dataDictionary.getFormID(), tab);
					}

					List<Field> listOfFields = tab.getField();

					for (Field field : listOfFields) {
						if (!StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.TABLE_FIELD_TYPE)
								&& !StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.LABEL_FIELD_TYPE)
								&& !StringUtils.equalsIgnoreCase(field.getType(),
										PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)
								&& !StringUtils.equalsIgnoreCase(field.getType(),
										PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
							employeeFilterListForm.setDataDictionaryId(dataDictionary.getDataDictionaryId());
							employeeFilterListForm.setFieldName(dataDictionary.getLabel());
							if (new String(Base64.decodeBase64(field.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {
								employeeFilterListForm.setDataType(field.getType());

							}

						}

					}

				}
				if (employeeFilterListForm.getFieldName() != null) {
					employeeFilterList.add(employeeFilterListForm);
				}
			}
		}
		return employeeFilterList;

	}

}
