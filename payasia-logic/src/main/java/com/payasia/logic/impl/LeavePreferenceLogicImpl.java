package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.LeavePreferenceLogic;

@Component
public class LeavePreferenceLogicImpl implements LeavePreferenceLogic {

	private static final Logger LOGGER = Logger.getLogger(LeavePreferenceLogicImpl.class);

	@Resource
	CompanyDAO companyDAO;
	@Resource
	LeavePreferenceDAO leavePreferenceDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	MonthMasterDAO monthMasterDAO;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	EntityMasterDAO entityMasterDAO;
	@Resource
	DynamicFormDAO dynamicFormDAO;
	@Resource
	GeneralDAO generalDAO;
	@Resource
	DataExportUtils dataExportUtils;

	@Override
	public LeavePreferenceForm getLeavePreference(Long companyId) {
		LeavePreferenceForm leavePreferenceForm = new LeavePreferenceForm();
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO != null) {
			leavePreferenceForm.setAllowManagerSelfApproveLeave(leavePreferenceVO.isAllowManagerSelfApproveLeave());
			leavePreferenceForm.setConsiderOffInLeaveConjunction(leavePreferenceVO.isConsiderOffInLeaveConjunction());
			leavePreferenceForm.setAllowYEPIfLeaveApplicationsArePending(
					leavePreferenceVO.isAllowYEPIfLeaveApplicationsArePending());
			if (leavePreferenceVO.getCancelLeaveValidationDays() == null) {
				leavePreferenceForm.setCancelLeaveValidationDays("");
			} else {
				leavePreferenceForm
						.setCancelLeaveValidationDays(String.valueOf(leavePreferenceVO.getCancelLeaveValidationDays()));
			}

			leavePreferenceForm
					.setHideLeaveTypeInLeaveCalendarDetail(leavePreferenceVO.isHideLeaveTypeInLeaveCalendarDetail());
			leavePreferenceForm.setLeaveEndMonth(leavePreferenceVO.getEndMonth().getMonthId());
			leavePreferenceForm.setLeaveStartMonth(leavePreferenceVO.getStartMonth().getMonthId());
			leavePreferenceForm
					.setShowLeaveCalendarBasedOnCustomField(leavePreferenceVO.isShowLeaveCalendarBasedOnCustomField());
			leavePreferenceForm.setDefaultEmailCC(leavePreferenceVO.isDefaultEmailCC());
			if (leavePreferenceVO.getLeaveCalendarBasedOnCustomField() != null) {
				leavePreferenceForm.setLeaveCalendarBasedOnCustomField(
						leavePreferenceVO.getLeaveCalendarBasedOnCustomField().getDataDictionaryId());
			}

			if (leavePreferenceVO.getShowEncashed() == null) {
				leavePreferenceForm.setShowEncashed(true);
			} else {
				leavePreferenceForm.setShowEncashed(leavePreferenceVO.getShowEncashed());
			}
			if (leavePreferenceVO.getDashboardSameAsAdmin() == null) {
				leavePreferenceForm.setDashboardSameAsAdmin(false);
			} else {
				leavePreferenceForm.setDashboardSameAsAdmin(leavePreferenceVO.getDashboardSameAsAdmin());
			}
			if (leavePreferenceVO.getShowFullEntitlement() == null) {
				leavePreferenceForm.setShowFullEntitlement(false);
			} else {
				leavePreferenceForm.setShowFullEntitlement(leavePreferenceVO.getShowFullEntitlement());
			}

			leavePreferenceForm.setPreApprovalReq(leavePreferenceVO.isPreApprovalRequired());
			leavePreferenceForm.setPreApprovalReqRemark(leavePreferenceVO.getPreApprovalReqRemark());
			leavePreferenceForm.setLeaveExtensionReq(leavePreferenceVO.isLeaveExtensionRequired());

			leavePreferenceForm.setLeaveTransactionToShow(leavePreferenceVO.getLeaveTransactionToShow().getAppCodeID());
			leavePreferenceForm.setUseSystemMailAsFromAddress(leavePreferenceVO.isUseSystemMailAsFromAddress());
			leavePreferenceForm.setShowLeaveBalanceCustomReport(leavePreferenceVO.isShowLeaveBalanceCustomReport());
			if (leavePreferenceVO.getLeaveUnit() != null) {
				leavePreferenceForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
				if (leavePreferenceVO.getLeaveUnit().getCodeDesc()
						.equalsIgnoreCase(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_HOURS)
						&& leavePreferenceVO.getWorkingHoursInADay() != null) {
					leavePreferenceForm.setWorkingHoursInADay(
							Math.round(leavePreferenceVO.getWorkingHoursInADay() * 1000) / 1000.0);
				}
			}
			leavePreferenceForm.setLeaveWorkflowNotRequired(leavePreferenceVO.isLeaveWorkflowNotRequired());
			leavePreferenceForm.setLeavehideAddColumn(leavePreferenceVO.isLeaveHideAddColumn());
			leavePreferenceForm.setKeypayPayRunParameter(leavePreferenceVO.getKeypayPayRunParameter());
		}
		return leavePreferenceForm;
	}

	@Override
	public void saveLeavePreference(LeavePreferenceForm leavePreferenceForm, Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		LeavePreference leavePreference = new LeavePreference();
		leavePreference.setCompany(companyVO);

		MonthMaster startMonth = monthMasterDAO.findById(leavePreferenceForm.getLeaveStartMonth());
		leavePreference.setStartMonth(startMonth);
		MonthMaster endMonth = monthMasterDAO.findById(leavePreferenceForm.getLeaveEndMonth());
		leavePreference.setEndMonth(endMonth);
		leavePreference.setConsiderOffInLeaveConjunction(leavePreferenceForm.isConsiderOffInLeaveConjunction());
		AppCodeMaster appCodeMaster = appCodeMasterDAO.findById(leavePreferenceForm.getLeaveTransactionToShow());
		leavePreference.setLeaveTransactionToShow(appCodeMaster);
		leavePreference.setAllowManagerSelfApproveLeave(leavePreferenceForm.isAllowManagerSelfApproveLeave());
		leavePreference.setAllowYEPIfLeaveApplicationsArePending(
				leavePreferenceForm.isAllowYEPIfLeaveApplicationsArePending());
		if (StringUtils.isNotBlank(leavePreferenceForm.getCancelLeaveValidationDays())) {
			leavePreference
					.setCancelLeaveValidationDays(Integer.parseInt(leavePreferenceForm.getCancelLeaveValidationDays()));
		} else {
			leavePreference.setCancelLeaveValidationDays(null);
		}

		leavePreference
				.setHideLeaveTypeInLeaveCalendarDetail(leavePreferenceForm.isHideLeaveTypeInLeaveCalendarDetail());
		leavePreference.setUseSystemMailAsFromAddress(leavePreferenceForm.isUseSystemMailAsFromAddress());
		leavePreference.setShowEncashed(leavePreferenceForm.getShowEncashed());
		leavePreference.setDashboardSameAsAdmin(leavePreferenceForm.getDashboardSameAsAdmin());
		leavePreference.setShowFullEntitlement(leavePreferenceForm.getShowFullEntitlement());
		leavePreference.setDefaultEmailCC(leavePreferenceForm.isDefaultEmailCC());

		if (leavePreferenceForm.isShowLeaveCalendarBasedOnCustomField()
				&& leavePreferenceForm.getLeaveCalendarBasedOnCustomField() != null) {
			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(leavePreferenceForm.getLeaveCalendarBasedOnCustomField());
			leavePreference.setLeaveCalendarBasedOnCustomField(dataDictionary);
			leavePreference.setShowLeaveCalendarBasedOnCustomField(
					leavePreferenceForm.isShowLeaveCalendarBasedOnCustomField());
		} else {
			leavePreference.setShowLeaveCalendarBasedOnCustomField(false);
		}
		leavePreference.setShowLeaveBalanceCustomReport(leavePreferenceForm.isShowLeaveBalanceCustomReport());
		leavePreference.setPreApprovalRequired(leavePreferenceForm.isPreApprovalReq());
		leavePreference.setLeaveExtensionRequired(leavePreferenceForm.isLeaveExtensionReq());

		if (StringUtils.isNotBlank(leavePreferenceForm.getLeaveUnit())) {
			AppCodeMaster leaveUnitAppCode = appCodeMasterDAO.findByCategoryAndDesc(
					PayAsiaConstants.PAYASIA_LEAVE_UNIT_CATEGORY, leavePreferenceForm.getLeaveUnit());
			leavePreference.setLeaveUnit(leaveUnitAppCode);
			if (leavePreferenceForm.getLeaveUnit().equalsIgnoreCase(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_HOURS)) {
				leavePreference.setWorkingHoursInADay(leavePreferenceForm.getWorkingHoursInADay());
			} else {
				leavePreference.setWorkingHoursInADay(null);
			}
		}
		leavePreference.setKeypayPayRunParameter(leavePreferenceForm.getKeypayPayRunParameter());
		leavePreference.setLeaveWorkflowNotRequired(leavePreferenceForm.isLeaveWorkflowNotRequired());
		leavePreference.setLeaveHideAddColumn(leavePreferenceForm.isLeavehideAddColumn());
		leavePreference.setPreApprovalReqRemark(leavePreferenceForm.getPreApprovalReqRemark());
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO == null) {
			leavePreferenceDAO.save(leavePreference);
		} else {
			leavePreference.setLeavePreferenceId(leavePreferenceVO.getLeavePreferenceId());
			leavePreferenceDAO.update(leavePreference);
		}

	}

	@Override
	public List<LeavePreferenceForm> getMonthList() {
		List<LeavePreferenceForm> leavePreferenceFormList = new ArrayList<>();
		List<MonthMaster> monthMasterList = monthMasterDAO.findAll();
		for (MonthMaster monthMasterVO : monthMasterList) {
			LeavePreferenceForm leavePreferenceForm = new LeavePreferenceForm();
			leavePreferenceForm.setMonthMasterId(monthMasterVO.getMonthId());
			leavePreferenceForm.setMonthName(monthMasterVO.getMonthName());
			leavePreferenceFormList.add(leavePreferenceForm);
		}
		return leavePreferenceFormList;
	}

	@Override
	public List<LeavePreferenceForm> getLeaveTransactionList() {
		List<LeavePreferenceForm> leavePreferenceFormList = new ArrayList<>();
		List<AppCodeMaster> leaveTransList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANSACTION);
		for (AppCodeMaster appCodeVO : leaveTransList) {
			LeavePreferenceForm leavePreferenceForm = new LeavePreferenceForm();
			leavePreferenceForm.setLeaveTransAppCodeId(appCodeVO.getAppCodeID());
			leavePreferenceForm.setLeaveTransName(appCodeVO.getCodeDesc());
			leavePreferenceForm.setLeaveTransAppCodeValue(appCodeVO.getCodeValue());
			leavePreferenceFormList.add(leavePreferenceForm);
		}
		return leavePreferenceFormList;
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
				entityId, PayAsiaConstants.DYNAMIC_TYPE);
		if (dataDictionaryCompanyList != null) {
			for (DataDictionary dataDictionary : dataDictionaryCompanyList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setDataDictionaryId(dataDictionary.getDataDictionaryId());
				employeeFilterListForm.setFieldName(dataDictionary.getLabel());
				Tab tab = tabMap.get(dataDictionary.getFormID());

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
									PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
						if (new String(Base64.decodeBase64(field.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())) {
							employeeFilterListForm.setDataType(field.getType());

						}

					} else if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (new String(Base64.decodeBase64(column.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {
								employeeFilterListForm.setDataType(column.getType());
							}
						}
					}
				}

				employeeFilterList.add(employeeFilterListForm);

			}
		}
		return employeeFilterList;

	}
}
