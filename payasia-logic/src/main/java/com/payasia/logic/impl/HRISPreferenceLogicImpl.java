package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.HRISPreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.HRISPreferenceLogic;

@Component
public class HRISPreferenceLogicImpl extends BaseLogic implements HRISPreferenceLogic {

	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	@Override
	public HRISPreferenceForm getHRISPreference(Long companyId) {
		HRISPreferenceForm hrisPreferenceForm = new HRISPreferenceForm();
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO != null) {
			hrisPreferenceForm.setSaveHrLetterInDocumentCenter(hrisPreferenceVO.isSaveHrLetterInDocumentCenter());
			hrisPreferenceForm.setEnableEmployeeChangeWorkflow(hrisPreferenceVO.isEnableEmployeeChangeWorkflow());
			hrisPreferenceForm.setPasswordProtect(hrisPreferenceVO.isPasswordProtect());
			hrisPreferenceForm.setAllowEmployeetouploaddocument(hrisPreferenceVO.isAllowEmployeeUploadDoc());
			hrisPreferenceForm.setHideGetPassword(hrisPreferenceVO.isHideGetPassword());
			hrisPreferenceForm.setClientAdminEditDeleteEmployee(hrisPreferenceVO.isClientAdminEditDeleteEmployee());
			hrisPreferenceForm.setUseSystemMailAsFromAddress(hrisPreferenceVO.isUseSystemMailAsFromAddress());
			hrisPreferenceForm.setShowPasswordAsPlainText(hrisPreferenceVO.isShowPasswordAsPlainText());
			hrisPreferenceForm
					.setUseEmailAndEmployeeNumberForLogin(hrisPreferenceVO.isUseEmailAndEmployeeNumberForLogin());
			hrisPreferenceForm.setEnableVisibility(
					hrisPreferenceVO.isEnableVisibility() == null ? false : hrisPreferenceVO.isEnableVisibility());
			hrisPreferenceForm.setSendPayslipReleaseMail(hrisPreferenceVO.isSendPayslipReleaseMail());
			if (StringUtils.isNotBlank(hrisPreferenceVO.getDiscussionBoardDefaultEmailTo())) {
				hrisPreferenceForm
						.setDiscussionBoardDefaultEmailTo(hrisPreferenceVO.getDiscussionBoardDefaultEmailTo());
			}
			if (StringUtils.isNotBlank(hrisPreferenceVO.getDiscussionBoardDefaultEmailCC())) {
				hrisPreferenceForm
						.setDiscussionBoardDefaultEmailCC(hrisPreferenceVO.getDiscussionBoardDefaultEmailCC());
			}

			if (hrisPreferenceVO.getExternalId() != null) {
				hrisPreferenceForm.setExternalDataDictId(hrisPreferenceVO.getExternalId().getDataDictionaryId());
			}

			List<EmployeeConditionDTO> employeeInfoList = new ArrayList<EmployeeConditionDTO>();
			if (StringUtils.isNotBlank(hrisPreferenceVO.getDiscussionBoardPayAsiaUsers())) {
				String[] empIdArray = hrisPreferenceVO.getDiscussionBoardPayAsiaUsers().split(";");
				for (int count = 0; count < empIdArray.length; count++) {
					if (StringUtils.isNotBlank(empIdArray[count])) {
						EmployeeConditionDTO ConditionDTO = new EmployeeConditionDTO();
						Employee employeeVO = employeeDAO.findById(Long.valueOf(empIdArray[count]));
						ConditionDTO.setEmployeeId(String.valueOf(employeeVO.getEmployeeId()));
						ConditionDTO.setEmployeeName(getEmployeeName(employeeVO));
						employeeInfoList.add(ConditionDTO);
					}
				}
			}
			hrisPreferenceForm.setEmployeeInfoList(employeeInfoList);
		} else {
			hrisPreferenceForm.setSaveHrLetterInDocumentCenter(false);
			hrisPreferenceForm.setEnableEmployeeChangeWorkflow(false);
			hrisPreferenceForm.setPasswordProtect(true);
			hrisPreferenceForm.setAllowEmployeetouploaddocument(false);
			hrisPreferenceForm.setHideGetPassword(false);
			hrisPreferenceForm.setClientAdminEditDeleteEmployee(true);
			hrisPreferenceForm.setUseSystemMailAsFromAddress(false);
			hrisPreferenceForm.setShowPasswordAsPlainText(false);
			hrisPreferenceForm.setUseEmailAndEmployeeNumberForLogin(false);
			hrisPreferenceForm.setSendPayslipReleaseMail(false);
		}

		return hrisPreferenceForm;

	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (StringUtils.isNotBlank(employee.getMiddleName())) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + "[" + employee.getEmployeeNumber() + "]";
		return employeeName;
	}

	@Override
	public void saveHRISPreference(HRISPreferenceForm hrisPreferenceForm, Long companyId) {
		Company companyVO = new Company();
		companyVO.setCompanyId(companyId);
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO == null) {
			HRISPreference hrisPreference = new HRISPreference();
			hrisPreference.setCompany(companyVO);
			hrisPreference.setSaveHrLetterInDocumentCenter(hrisPreferenceForm.getSaveHrLetterInDocumentCenter());
			hrisPreference.setEnableEmployeeChangeWorkflow(hrisPreferenceForm.getEnableEmployeeChangeWorkflow());
			hrisPreference.setPasswordProtect(hrisPreferenceForm.getPasswordProtect());
			hrisPreference.setAllowEmployeeUploadDoc(hrisPreferenceForm.getAllowEmployeetouploaddocument());
			hrisPreference.setHideGetPassword(hrisPreferenceForm.getHideGetPassword());
			hrisPreference.setClientAdminEditDeleteEmployee(hrisPreferenceForm.getClientAdminEditDeleteEmployee());
			hrisPreference.setUseSystemMailAsFromAddress(hrisPreferenceForm.getUseSystemMailAsFromAddress());
			hrisPreference.setShowPasswordAsPlainText(hrisPreferenceForm.getShowPasswordAsPlainText());
			hrisPreference
					.setUseEmailAndEmployeeNumberForLogin(hrisPreferenceForm.getUseEmailAndEmployeeNumberForLogin());
			hrisPreference.setSendPayslipReleaseMail(hrisPreferenceForm.getSendPayslipReleaseMail());
			hrisPreference.setDiscussionBoardDefaultEmailTo(hrisPreferenceForm.getDiscussionBoardDefaultEmailTo());
			hrisPreference.setDiscussionBoardDefaultEmailCC(hrisPreferenceForm.getDiscussionBoardDefaultEmailCC());
			hrisPreference.setEnableVisibility(hrisPreferenceForm.getEnableVisibility());
			// Discussion Board PayAsia Users Email To
			String empIdsList = "";
			String[] empIdArray = hrisPreferenceForm.getDiscussionBoardPayAsiaUsers().split(";");
			Set<String> empsIdSet = new LinkedHashSet<String>();
			for (int count = 0; count < empIdArray.length; count++) {
				if (StringUtils.isNotBlank(empIdArray[count])) {
					empsIdSet.add(empIdArray[count]);
				}
			}
			for (String empId : empsIdSet) {
				if (StringUtils.isNotBlank(empId)) {
					empIdsList += empId + ";";
				}
			}
			hrisPreference.setDiscussionBoardPayAsiaUsers(empIdsList);

			if (hrisPreferenceForm.getExternalDataDictId() != null) {
				DataDictionary externalIdDict = dataDictionaryDAO.findById(hrisPreferenceForm.getExternalDataDictId());
				hrisPreference.setExternalId(externalIdDict);
			} else {
				hrisPreference.setExternalId(null);
			}
			hrisPreferenceDAO.save(hrisPreference);
		} else {
			hrisPreferenceVO.setSaveHrLetterInDocumentCenter(hrisPreferenceForm.getSaveHrLetterInDocumentCenter());
			hrisPreferenceVO.setEnableEmployeeChangeWorkflow(hrisPreferenceForm.getEnableEmployeeChangeWorkflow());
			hrisPreferenceVO.setHrisPreferenceId(hrisPreferenceVO.getHrisPreferenceId());
			hrisPreferenceVO.setPasswordProtect(hrisPreferenceForm.getPasswordProtect());
			hrisPreferenceVO.setAllowEmployeeUploadDoc(hrisPreferenceForm.getAllowEmployeetouploaddocument());
			hrisPreferenceVO.setHideGetPassword(hrisPreferenceForm.getHideGetPassword());
			hrisPreferenceVO.setClientAdminEditDeleteEmployee(hrisPreferenceForm.getClientAdminEditDeleteEmployee());
			hrisPreferenceVO.setUseSystemMailAsFromAddress(hrisPreferenceForm.getUseSystemMailAsFromAddress());
			hrisPreferenceVO.setShowPasswordAsPlainText(hrisPreferenceForm.getShowPasswordAsPlainText());
			hrisPreferenceVO
					.setUseEmailAndEmployeeNumberForLogin(hrisPreferenceForm.getUseEmailAndEmployeeNumberForLogin());
			hrisPreferenceVO.setSendPayslipReleaseMail(hrisPreferenceForm.getSendPayslipReleaseMail());
			hrisPreferenceVO.setDiscussionBoardDefaultEmailTo(hrisPreferenceForm.getDiscussionBoardDefaultEmailTo());
			hrisPreferenceVO.setDiscussionBoardDefaultEmailCC(hrisPreferenceForm.getDiscussionBoardDefaultEmailCC());
			hrisPreferenceVO.setEnableVisibility(hrisPreferenceForm.getEnableVisibility());
			// Discussion Board PayAsia Users Email To
			String empIdsList = "";
			String[] empIdArray = hrisPreferenceForm.getDiscussionBoardPayAsiaUsers().split(";");
			Set<String> empsIdSet = new LinkedHashSet<String>();
			for (int count = 0; count < empIdArray.length; count++) {
				if (StringUtils.isNotBlank(empIdArray[count])) {
					empsIdSet.add(empIdArray[count]);
				}
			}
			for (String empId : empsIdSet) {
				if (StringUtils.isNotBlank(empId)) {
					empIdsList += empId + ";";
				}
			}
			hrisPreferenceVO.setDiscussionBoardPayAsiaUsers(empIdsList);

			if (hrisPreferenceForm.getExternalDataDictId() != null) {
				DataDictionary externalIdDict = dataDictionaryDAO.findById(hrisPreferenceForm.getExternalDataDictId());
				hrisPreferenceVO.setExternalId(externalIdDict);
			} else {
				hrisPreferenceVO.setExternalId(null);
			}

			hrisPreferenceDAO.update(hrisPreferenceVO);
		}
	}

	@Override
	public HRISPreferenceForm searchEmployee(PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			String empName, String empNumber, Long companyId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		Company companyVO = companyDAO.findById(companyId);
		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		conditionDTO.setGroupId(companyVO.getCompanyGroup().getGroupId());

		List<Tuple> finalList = employeeRoleMappingDAO.findPayAsiaUsersByCond(conditionDTO, 1l, companyId);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Tuple employeeRoleMapping : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += (String) employeeRoleMapping.get(getAlias(Employee_.firstName), String.class) + " ";
			if (StringUtils.isNotBlank((String) employeeRoleMapping.get(getAlias(Employee_.lastName), String.class))) {
				employeeName += (String) employeeRoleMapping.get(getAlias(Employee_.lastName), String.class);
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(
					(String) employeeRoleMapping.get(getAlias(Employee_.employeeNumber), String.class));
			employeeForm.setEmail((String) employeeRoleMapping.get(getAlias(Employee_.email), String.class));
			employeeForm.setCompanyName((String) employeeRoleMapping.get(getAlias(Company_.companyName), String.class));
			employeeForm.setEmployeeID((Long) employeeRoleMapping.get(getAlias(Employee_.employeeId), Long.class));
			employeeListFormList.add(employeeForm);
		}

		HRISPreferenceForm response = new HRISPreferenceForm();
		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}
}
