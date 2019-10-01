package com.payasia.logic.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.CodeDesc;
import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.EmployeeFilter;
import com.mind.payasia.xml.bean.EmployeeFilterTemplate;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.dto.CustomFieldReportDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DeviceDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.EmployeeDTO;
import com.payasia.common.dto.EmployeeFieldDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.ManageRolesConditionDTO;
import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.dto.NotificationAlertConditionDTO;
import com.payasia.common.dto.NotificationAlertDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.common.form.NotificationForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.EmployeeFilterXMLUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.ClaimApplicationReviewerDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyEmployeeShortListDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmpDataExportTemplateDAO;
import com.payasia.dao.EmployeeActivationCodeDAO;
import com.payasia.dao.EmployeeClaimReviewerDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.EmployeeMobileDetailsDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.EmployeeRoleSectionMappingDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.HRISChangeRequestDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.PrivilegeMasterDAO;
import com.payasia.dao.RoleSectionMappingDAO;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyEmployeeShortList;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.EmpDataExportTemplate;
import com.payasia.dao.bean.EmpDataExportTemplateFilter;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeActivationCode;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.EmployeeMobileDetails;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeShortList;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.NotificationAlert;
import com.payasia.dao.bean.PrivilegeMaster;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.FiltersInfoUtilsLogic;
import com.payasia.logic.GeneralFilterLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.logic.SecurityLogic;

@Component
public class GeneralLogicImpl implements GeneralLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(GeneralLogicImpl.class);

	@Resource
	MonthMasterDAO monthMasterDAO;

	@Resource
	CompanyEmployeeShortListDAO companyEmployeeShortListDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	FiltersInfoUtilsLogic filtersInfoUtilsLogic;

	@Resource
	GeneralDAO generalDAO;

	@Resource
	SecurityLogic securityLogic;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeLoginDetailDAO employeeLoginDetailDAO;

	@Resource
	GeneralFilterLogic generalFilterLogic;

	@Resource
	DataExportUtils dataExportUtils;

	@Resource
	EmployeeActivationCodeDAO employeeActivationCodeDAO;

	@Resource
	EmployeeMobileDetailsDAO employeeMobileDetailsDAO;
	@Resource
	HRISChangeRequestDAO hrisChangeRequestDAO;

	@Resource
	EmployeeLeaveReviewerDAO employeeLeaveReviewerDAO;

	@Resource
	LeaveApplicationReviewerDAO leaveApplicationReviewersDAO;

	@Resource
	MultilingualLogic multilingualLogic;
	@Resource
	EmployeeRoleSectionMappingDAO employeeRoleSectionMappingDAO;
	@Resource
	RoleSectionMappingDAO roleSectionMappingDAO;
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	@Resource
	EmployeeClaimReviewerDAO employeeClaimReviewerDAO;

	@Resource
	ClaimApplicationReviewerDAO claimApplicationReviewerDAO;

	@Resource
	NotificationAlertDAO notificationAlertDAO;
	@Resource
	EmpDataExportTemplateDAO empDataExportTemplateDAO;
	@Resource
	PrivilegeMasterDAO privilegeMasterDAO;
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;
	@Resource
	MessageSource messageSource;
	@Resource
	EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;

	/** The file name seperator. */
	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	/** The Company Based Employee Filter Combo Map. */
	private static ConcurrentHashMap<Long, HashMap<String, String>> companyDataDictComboCache = new ConcurrentHashMap<>();

	/** The Key data DictionaryId Form Id , version Map */
	private static ConcurrentHashMap<Long, String> dataDictFormVersionCache = new ConcurrentHashMap<>();

	@Override
	public List<MonthMasterDTO> getMonthList() {
		Locale locale = UserContext.getLocale();
		List<MonthMaster> monthMasterList = monthMasterDAO.findAll();
		List<MonthMasterDTO> monthList = new ArrayList<MonthMasterDTO>();

		for (MonthMaster monthMaster : monthMasterList) {
			MonthMasterDTO monthMasterDTO = new MonthMasterDTO();
			monthMasterDTO.setMonthId(monthMaster.getMonthId());

			if (StringUtils.isNotBlank(monthMaster.getLabelKey())) {
				String labelMsg = messageSource.getMessage(
						monthMaster.getLabelKey(), new Object[] {}, locale);
				if (StringUtils.isNotBlank(labelMsg)) {
					monthMasterDTO.setMonthName(labelMsg);
				}
			} else {
				monthMasterDTO.setMonthName(monthMaster.getMonthName());
			}
			monthList.add(monthMasterDTO);
		}
		return monthList;
	}

	@Override
	public EmployeeShortListDTO getShortListEmployeeIds(Long employeeId,
			Long companyId) {

		List<BigInteger> employeeIds = new ArrayList<>();
		EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();
		Map<String, String> paramValueMap = new HashMap<String, String>();
		List<CompanyEmployeeShortList> companyEmployeeShortList = companyEmployeeShortListDAO
				.findByCondition(employeeId, null, companyId);

		if (companyEmployeeShortList == null
				|| companyEmployeeShortList.size() == 0) {
			employeeShortListDTO.setShortListEmployeeIds(employeeIds);
			employeeShortListDTO.setEmployeeShortList(false);
		} else {
			employeeIds = new ArrayList<BigInteger>();
			Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
			List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
			List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
			setFilterInfo(companyEmployeeShortList, finalFilterList,
					tableNames, codeDescDTOs);

			EntityMaster entityMaster = entityMasterDAO
					.findById(PayAsiaConstants.EMPLOYEE_ENTITY_ID);

			List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
					entityMaster.getEntityId());

			String queryString = createShortListQuery(companyId, formIds,
					finalFilterList, tableNames, codeDescDTOs, paramValueMap);

			employeeIds.addAll(employeeDAO.checkForEmployeeDocuments(
					queryString, paramValueMap, null, companyId));
			employeeShortListDTO.setShortListEmployeeIds(employeeIds);
			employeeShortListDTO.setEmployeeShortList(true);
		}

		return employeeShortListDTO;
	}

	@Override
	public EmployeeShortListDTO getShortListEmployeeNumbers(Long employeeId,
			Long companyId) {

		List<String> employeeNumbers = new ArrayList<>();
		EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();

		List<CompanyEmployeeShortList> companyEmployeeShortList = companyEmployeeShortListDAO
				.findByCondition(employeeId, null, companyId);

		if (companyEmployeeShortList == null
				|| companyEmployeeShortList.size() == 0) {
			employeeShortListDTO.setShortListEmployeeNumbers(employeeNumbers);
			employeeShortListDTO.setEmployeeShortList(false);

		} else {
			employeeNumbers = new ArrayList<>();
			Map<String, String> paramValueMap = new HashMap<String, String>();
			Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
			List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
			List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
			setFilterInfo(companyEmployeeShortList, finalFilterList,
					tableNames, codeDescDTOs);

			EntityMaster entityMaster = entityMasterDAO
					.findById(PayAsiaConstants.EMPLOYEE_ENTITY_ID);

			List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
					entityMaster.getEntityId());

			String queryString = createShortListEmpNumQuery(companyId, formIds,
					finalFilterList, tableNames, codeDescDTOs, paramValueMap);

			employeeNumbers.addAll(employeeDAO.findShortListedEmployeeIds(
					queryString, paramValueMap, companyId));
			employeeShortListDTO.setShortListEmployeeNumbers(employeeNumbers);
			employeeShortListDTO.setEmployeeShortList(true);
		}

		return employeeShortListDTO;
	}

	private void setFilterInfo(
			List<CompanyEmployeeShortList> companyEmployeeShortList,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs) {

		for (CompanyEmployeeShortList companyEmployeeFliter : companyEmployeeShortList) {
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			if (companyEmployeeFliter.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

				filtersInfoUtilsLogic.getStaticDictionaryInfo(
						companyEmployeeFliter.getDataDictionary(),
						dataImportKeyValueDTO);

			} else {

				filtersInfoUtilsLogic.getDynamicDictionaryInfo(
						companyEmployeeFliter.getDataDictionary(),
						dataImportKeyValueDTO, tableNames, codeDescDTOs);

			}
			GeneralFilterDTO generalFilterDTO = new GeneralFilterDTO();

			if (companyEmployeeFliter.getCloseBracket() != null) {
				generalFilterDTO.setCloseBracket(companyEmployeeFliter
						.getCloseBracket());
			} else {
				generalFilterDTO.setCloseBracket("");
			}

			if (companyEmployeeFliter.getOpenBracket() != null) {
				generalFilterDTO.setOpenBracket(companyEmployeeFliter
						.getOpenBracket());
			} else {
				generalFilterDTO.setOpenBracket("");
			}

			generalFilterDTO.setDataDictionaryName(companyEmployeeFliter
					.getDataDictionary().getDataDictName());
			generalFilterDTO.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			generalFilterDTO.setDictionaryId(companyEmployeeFliter
					.getDataDictionary().getDataDictionaryId());
			generalFilterDTO.setEqualityOperator(companyEmployeeFliter
					.getEqualityOperator());
			generalFilterDTO
					.setFilterId(companyEmployeeFliter.getShortListId());
			generalFilterDTO.setLogicalOperator(companyEmployeeFliter
					.getLogicalOperator());
			generalFilterDTO.setValue(companyEmployeeFliter.getValue());

			finalFilterList.add(generalFilterDTO);
		}
	}

	private String createShortListQuery(Long companyId, List<Long> formIds,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			Map<String, String> paramValueMap) {
		// Map<String, String> paramValueMap = new HashMap<String, String>();
		String queryString = "";
		String select = "SELECT employee.EMPLOYEE_ID ";
		StringBuilder fromBuilder = new StringBuilder(
				" FROM Employee AS employee ");
		for (Long formId : formIds) {

			fromBuilder
					.append("LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord")
					.append(formId).append(" ")
					.append("ON (employee.Employee_ID = dynamicFormRecord")
					.append(formId)
					.append(".Entity_Key) AND (dynamicFormRecord")
					.append(formId).append(".Form_ID = ").append(formId)
					.append(" ) ");

		}
		String tableDateStr = "getDate()";
		for (DynamicTableDTO dynamicTableDTO : tableNames) {
			String dynamicFormTableRecord = new StringBuilder(
					"dynamicFormTableRecordT")
					.append(dynamicTableDTO.getFormId())
					.append(dynamicTableDTO.getTableName()).toString();

			fromBuilder
					.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS ")
					.append(dynamicFormTableRecord)
					.append(" ")
					.append("ON (dynamicFormRecord")
					.append(dynamicTableDTO.getFormId())
					.append(".Col_")
					.append(dynamicTableDTO.getTableName())
					.append(" = CAST(")
					.append(dynamicFormTableRecord)
					.append(".Dynamic_Form_Table_Record_ID AS varchar(255)) AND ISNULL(")
					.append(dynamicFormTableRecord)
					.append(".Company_ID,")
					.append(companyId)
					.append(")= ")
					.append(companyId)
					.append(" ) ")
					.append("and CONVERT(datetime," + dynamicFormTableRecord)
					.append(".Col_1) = (select top 1 max(CONVERT(datetime, dynamicFormTableRecord_max")
					.append(dynamicTableDTO.getFormId())
					.append(dynamicTableDTO.getTableName())
					.append(".Col_1)) from Dynamic_Form_Table_Record dynamicFormTableRecord_max")
					.append(dynamicTableDTO.getFormId())
					.append(dynamicTableDTO.getTableName())
					.append(" where dynamicFormTableRecord_max")
					.append(dynamicTableDTO.getFormId())
					.append(dynamicTableDTO.getTableName())
					.append(".Dynamic_Form_Table_Record_ID ="
							+ dynamicFormTableRecord)
					.append(".Dynamic_Form_Table_Record_ID  and CONVERT(date, dynamicFormTableRecord_max")
					.append(dynamicTableDTO.getFormId())
					.append(dynamicTableDTO.getTableName())
					.append(".Col_1) <= ").append(tableDateStr).append(" ) ");
		}

		List<String> codeAliasList = new ArrayList<>();

		for (CodeDescDTO codeDescDTO : codeDescDTOs) {

			if (codeDescDTO.isChildVal()) {
				for (DynamicTableDTO dynamicTableDTO : tableNames) {

					String dynamicFormTableRecord = new StringBuilder(
							"dynamicFormTableRecordT")
							.append(dynamicTableDTO.getFormId())
							.append(dynamicTableDTO.getTableName()).toString();

					String dynamicFormFieldRefValueT = new StringBuilder(
							"dynamicFormFieldRefValueT")
							.append(codeDescDTO.getFormId())
							.append(codeDescDTO.getMethodName()).toString();
					if (!codeAliasList.contains(dynamicFormFieldRefValueT)) {

						fromBuilder
								.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS ")
								.append(dynamicFormFieldRefValueT)
								.append(" ")
								.append("ON (")
								.append(dynamicFormTableRecord)
								.append(".")
								.append(codeDescDTO.getMethodName())
								.append(" = CAST(")
								.append(dynamicFormFieldRefValueT)
								.append(".Field_Ref_Value_ID AS varchar(255))) ");
						codeAliasList.add(dynamicFormFieldRefValueT);
					}

				}

			} else {

				String dynamicFormFieldRefValueT = new StringBuilder(
						"dynamicFormFieldRefValue")
						.append(codeDescDTO.getFormId())
						.append(codeDescDTO.getMethodName()).toString();

				if (!codeAliasList.contains(dynamicFormFieldRefValueT)) {
					fromBuilder
							.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS ")
							.append(dynamicFormFieldRefValueT).append(" ")
							.append("ON (dynamicFormRecord")
							.append(codeDescDTO.getFormId()).append(".")
							.append(codeDescDTO.getMethodName())
							.append(" = CAST(")
							.append(dynamicFormFieldRefValueT)
							.append(".Field_Ref_Value_ID AS varchar(255))) ");
					codeAliasList.add(dynamicFormFieldRefValueT);
				}

			}

		}

		String where = createShorlistWhere(companyId, finalFilterList,
				paramValueMap);
		queryString = select + fromBuilder.toString() + where;
		return queryString;
	}

	private String createShortListEmpNumQuery(Long companyId,
			List<Long> formIds, List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			Map<String, String> paramValueMap) {
		String queryString = "";
		String select = "SELECT employee.Employee_Number ";
		StringBuilder fromBuilder = new StringBuilder(
				" FROM Employee AS employee ");
		for (Long formId : formIds) {

			fromBuilder
					.append("LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord")
					.append(formId).append(" ");
			fromBuilder.append("ON (employee.Employee_ID = dynamicFormRecord")
					.append(formId)
					.append(".Entity_Key) AND (dynamicFormRecord")
					.append(formId).append(".Form_ID = ").append(formId)
					.append(" ) ");

		}

		for (DynamicTableDTO dynamicTableDTO : tableNames) {

			String dynamicFormTableRecord = "dynamicFormTableRecordT"
					+ dynamicTableDTO.getFormId()
					+ dynamicTableDTO.getTableName();

			fromBuilder
					.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS ")
					.append(dynamicFormTableRecord)
					.append(" ")
					.append("ON (dynamicFormRecord")
					.append(dynamicTableDTO.getFormId())
					.append(".Col_")
					.append(dynamicTableDTO.getTableName())
					.append(" = CAST(")
					.append(dynamicFormTableRecord)
					.append(".Dynamic_Form_Table_Record_ID AS varchar(255)) AND ISNULL(")
					.append(dynamicFormTableRecord).append(".Company_ID,")
					.append(companyId).append(")= ").append(companyId)
					.append(" ) ");
		}

		List<String> codeAliasList = new ArrayList<>();

		for (CodeDescDTO codeDescDTO : codeDescDTOs) {

			if (codeDescDTO.isChildVal()) {
				for (DynamicTableDTO dynamicTableDTO : tableNames) {

					String dynamicFormTableRecord = new StringBuilder(
							"dynamicFormTableRecordT")
							.append(dynamicTableDTO.getFormId())
							.append(dynamicTableDTO.getTableName()).toString();

					String dynamicFormFieldRefValueT = new StringBuilder(
							"dynamicFormFieldRefValueT")
							.append(codeDescDTO.getFormId())
							.append(codeDescDTO.getMethodName()).toString();
					if (!codeAliasList.contains(dynamicFormFieldRefValueT)) {

						fromBuilder
								.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS ")
								.append(dynamicFormFieldRefValueT)
								.append(" ")
								.append("ON (")
								.append(dynamicFormTableRecord)
								.append(".")
								.append(codeDescDTO.getMethodName())
								.append(" = CAST(")
								.append(dynamicFormFieldRefValueT)
								.append(".Field_Ref_Value_ID AS varchar(255))) ");
						codeAliasList.add(dynamicFormFieldRefValueT);
					}

				}

			} else {

				String dynamicFormFieldRefValueT = "dynamicFormFieldRefValue"
						+ codeDescDTO.getFormId() + codeDescDTO.getMethodName();

				if (!codeAliasList.contains(dynamicFormFieldRefValueT)) {
					fromBuilder
							.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS ")
							.append(dynamicFormFieldRefValueT).append(" ")
							.append("ON (dynamicFormRecord")
							.append(codeDescDTO.getFormId()).append(".")
							.append(codeDescDTO.getMethodName())
							.append(" = CAST(")
							.append(dynamicFormFieldRefValueT)
							.append(".Field_Ref_Value_ID AS varchar(255))) ");
					codeAliasList.add(dynamicFormFieldRefValueT);
				}

			}

		}

		String where = createShorlistWhere(companyId, finalFilterList,
				paramValueMap);
		queryString = select + fromBuilder.toString() + where;
		return queryString;
	}

	private String createShorlistWhere(Long companyId,
			List<GeneralFilterDTO> finalFilterList,
			Map<String, String> paramValueMap) {
		String where = " WHERE employee.COMPANY_ID =:companyId ";

		return generalFilterLogic.createSubWhere(where, finalFilterList,
				companyId, null, paramValueMap);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.GeneralLogic#generateEncryptedPasswords()
	 */
	@Override
	public void generateEncryptedPasswords() {

		List<Company> companies = companyDAO.findAllCmps();
		for (Company company : companies) {

			List<Employee> employeeVOs = employeeDAO.findAll(
					company.getCompanyId(), null, null);
			for (Employee employeeVO : employeeVOs) {
				EmployeeLoginDetail employeeLoginDetail = employeeVO
						.getEmployeeLoginDetail();
				String salt = securityLogic.generateSalt();
				String encrpytPass = securityLogic.encrypt(
						employeeLoginDetail.getPassword(), salt);
				employeeLoginDetail.setPassword(encrpytPass);
				employeeLoginDetail.setSalt(salt);
				if (employeeVO.getEmployeeLoginHistories().isEmpty()) {
					employeeLoginDetail.setPasswordReset(true);
				}
				employeeLoginDetailDAO.update(employeeLoginDetail);

			}

		}

	}

	private Tab getTabObject(String xml) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		} catch (SAXException sAXException) {
			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(),
					sAXException);
		}
		final StringReader xmlReader = new StringReader(xml);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
			throw new PayAsiaSystemException(jaxbException.getMessage(),
					jaxbException);
		}
		return tab;
	}

	@Override
	public HashMap<String, String> getEmployeeFilterComboList(Long companyId) {
		HashMap<String, String> comboListHashMap = null;

		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();

		EntityMaster entityMasterVO = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<DataDictionary> dataDictionaryCompanyList = getDynamicDataDictionaryList(
				companyId, entityMasterVO.getEntityId());
		if (companyDataDictComboCache.get(companyId) != null) {
			comboListHashMap = companyDataDictComboCache.get(companyId);
		}
		List<Long> dataDictionaryTempList = new ArrayList<>();
		if (comboListHashMap != null) {
			for (DataDictionary dataDictionaryVO : dataDictionaryCompanyList) {
				Long dataDictIdKey = dataDictionaryVO.getDataDictionaryId();
				String formIdVesionStr = dataDictFormVersionCache
						.get(dataDictIdKey);
				if (StringUtils.isBlank(formIdVesionStr)) {
					createEmployeeFilterDynamicCombo(companyId,
							comboListHashMap, formIdMap,
							entityMasterVO.getEntityId(), dataDictionaryVO);
				} else {
					String formId = formIdVesionStr.substring(0,
							formIdVesionStr.lastIndexOf("_"));
					int version = Integer.parseInt(formIdVesionStr.substring(
							formIdVesionStr.lastIndexOf("_") + 1,
							formIdVesionStr.length()));
					dataDictionaryTempList.add(dataDictIdKey);
					DynamicForm dynamicForm = getMaxVersionDynamicForm(
							companyId, formIdMap, entityMasterVO.getEntityId(),
							Long.parseLong(formId));
					if (dynamicForm != null) {
						if (dynamicForm.getId().getVersion() != version) {
							createEmployeeFilterDynamicCombo(companyId,
									comboListHashMap, formIdMap,
									entityMasterVO.getEntityId(),
									dataDictionaryVO);
						}
					}
				}
			}

			Set<String> keySet = comboListHashMap.keySet();
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				Long dataDictIdkey = Long.parseLong(iterator.next());
				if (!dataDictionaryTempList.contains(dataDictIdkey)) {
					iterator.remove();
				}
			}
		} else {
			synchronized (companyDataDictComboCache) {
				if (companyDataDictComboCache.get(companyId) == null) {
					comboListHashMap = new HashMap<String, String>();
					getStaticTypeFilterComboList(comboListHashMap,
							entityMasterVO.getEntityId());

					getDynamicTypeFilterComboList(companyId, comboListHashMap,
							formIdMap, dataDictionaryCompanyList,
							entityMasterVO.getEntityId());
				}
			}
		}

		companyDataDictComboCache.put(companyId, comboListHashMap);

		return comboListHashMap;

	}

	private void getDynamicTypeFilterComboList(Long companyId,
			HashMap<String, String> comboListHashMap,
			Map<Long, DynamicForm> formIdMap,
			List<DataDictionary> dataDictionaryCompanyList, Long entityId) {
		if (!dataDictionaryCompanyList.isEmpty()) {
			for (DataDictionary dataDictionary : dataDictionaryCompanyList) {
				createEmployeeFilterDynamicCombo(companyId, comboListHashMap,
						formIdMap, entityId, dataDictionary);
			}
		}
	}

	private void createEmployeeFilterDynamicCombo(Long companyId,
			HashMap<String, String> comboListHashMap,
			Map<Long, DynamicForm> formIdMap, Long entityId,
			DataDictionary dataDictionary) {
		DynamicForm dynamicForm = getMaxVersionDynamicForm(companyId,
				formIdMap, entityId, dataDictionary.getFormID());
		Tab tab = getTabObject(dynamicForm.getMetaData());
		List<Field> listOfFields = tab.getField();
		try {
			for (Field field : listOfFields) {
				if (!StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.TABLE_FIELD_TYPE)
						&& !StringUtils.equalsIgnoreCase(field.getType(),
								PayAsiaConstants.LABEL_FIELD_TYPE)
						&& !StringUtils.equalsIgnoreCase(field.getType(),
								PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
					if (new String(Base64.decodeBase64(field
							.getDictionaryName().getBytes()))
							.equals(dataDictionary.getDataDictName())
							|| field.getDictionaryId().equals(
									dataDictionary.getDataDictionaryId())) {
						if (field.getType().equalsIgnoreCase(
								PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
							getNonTableCodeDescCombo(comboListHashMap,
									dataDictionary.getDataDictionaryId());
							dataDictFormVersionCache.put(
									dataDictionary.getDataDictionaryId(),
									dynamicForm.getId().getFormId() + "_"
											+ dynamicForm.getId().getVersion());
						}
						if (field.getType().equalsIgnoreCase(
								PayAsiaConstants.FIELD_TYPE_COMBO)) {
							List<String> comboList = field.getOption();
							getNonTableDropDownCombo(comboListHashMap,
									dataDictionary.getDataDictionaryId(),
									comboList);
							dataDictFormVersionCache.put(
									dataDictionary.getDataDictionaryId(),
									dynamicForm.getId().getFormId() + "_"
											+ dynamicForm.getId().getVersion());
						}
					}
				} else if (field.getType().equals(
						PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						if (new String(Base64.decodeBase64(column
								.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())
								|| column.getDictionaryId().equals(
										dataDictionary.getDataDictionaryId())) {
							if (column.getType().equalsIgnoreCase(
									PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
								getTableCodeDescCombo(comboListHashMap,
										dataDictionary.getDataDictionaryId());
								dataDictFormVersionCache.put(
										dataDictionary.getDataDictionaryId(),
										dynamicForm.getId().getFormId()
												+ "_"
												+ dynamicForm.getId()
														.getVersion());
							}
							if (column.getType().equalsIgnoreCase(
									PayAsiaConstants.PAYASIA_FIELD_DROPDOWN)) {
								List<String> comboList = column.getOption();
								getTableDropDownCombo(comboListHashMap,
										dataDictionary.getDataDictionaryId(),
										comboList);
								dataDictFormVersionCache.put(
										dataDictionary.getDataDictionaryId(),
										dynamicForm.getId().getFormId()
												+ "_"
												+ dynamicForm.getId()
														.getVersion());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}
	}

	private List<DataDictionary> getDynamicDataDictionaryList(Long companyId,
			Long entityId) {
		List<DataDictionary> dataDictionaryCompanyList = dataDictionaryDAO
				.findByConditionEntityAndCompanyId(companyId, entityId,
						PayAsiaConstants.DYNAMIC_TYPE);
		return dataDictionaryCompanyList;
	}

	private void getTableDropDownCombo(
			HashMap<String, String> comboListHashMap, Long dataDictionaryId,
			List<String> comboList) throws UnsupportedEncodingException {
		StringBuilder dynamicComboStr = new StringBuilder();
		//dynamicComboStr.append("<option value=''>--select--</option>");

		for (String comboVal : comboList) {
			try {
				dynamicComboStr.append( URLDecoder.decode(StringEscapeUtils
								.unescapeHtml(new String(Base64
										.decodeBase64(comboVal.getBytes()))),
								"UTF-8") );
				dynamicComboStr.append("#");
				/*
				 * dynamicComboStr.append(URLDecoder.decode(StringEscapeUtils .unescapeHtml(new
				 * String(Base64.decodeBase64(comboVal .getBytes()))), "UTF-8"));
				 */
			} catch (java.lang.IllegalArgumentException iae) {
				dynamicComboStr.append(StringEscapeUtils.unescapeHtml(new String(Base64
								.decodeBase64(comboVal.getBytes()))));
				dynamicComboStr.append("#");
				/*
				 * dynamicComboStr.append(StringEscapeUtils .unescapeHtml(new
				 * String(Base64.decodeBase64(comboVal .getBytes()))));
				 */
			}

			//dynamicComboStr.append("</option>");
		}

		comboListHashMap.put(String.valueOf(dataDictionaryId),
				dynamicComboStr.toString());
	}

	private void getTableCodeDescCombo(
			HashMap<String, String> comboListHashMap, Long dataDictionaryId)
			throws UnsupportedEncodingException {
		List<CodeDesc> codeDescList = multilingualLogic
				.getCodeDescList(dataDictionaryId);
		StringBuilder dynamicCodeDescStr = new StringBuilder();
		//dynamicCodeDescStr.append("<option value=''>--select--</option>");
		for (CodeDesc codeDescVal : codeDescList) {

			String code = URLDecoder.decode(codeDescVal.getCode(), "UTF-8");

			dynamicCodeDescStr.append(code);
			//dynamicCodeDescStr.append(code);
			dynamicCodeDescStr.append("#");
			//dynamicCodeDescStr.append("</option>");
		}
		comboListHashMap.put(String.valueOf(dataDictionaryId),
				dynamicCodeDescStr.toString());
	}

	private void getNonTableDropDownCombo(
			HashMap<String, String> comboListHashMap, Long dataDictionaryId,
			List<String> comboList) throws UnsupportedEncodingException {
		StringBuilder dynamicComboStr = new StringBuilder();
		//dynamicComboStr.append("<option value=''>--select--</option>");
		for (String comboVal : comboList) {
			try {
				dynamicComboStr.append(URLDecoder.decode(StringEscapeUtils
								.unescapeHtml(new String(Base64
										.decodeBase64(comboVal.getBytes()))),
								"UTF-8"));
				dynamicComboStr.append("#");
				
				/*
				 * dynamicComboStr.append(URLDecoder.decode(StringEscapeUtils .unescapeHtml(new
				 * String(Base64.decodeBase64(comboVal .getBytes()))), "UTF-8"));
				 */
			} catch (java.lang.IllegalArgumentException iae) {
				dynamicComboStr.append(StringEscapeUtils.unescapeHtml(new String(Base64
								.decodeBase64(comboVal.getBytes()))));
				dynamicComboStr.append("#");
				/*
				 * dynamicComboStr.append(StringEscapeUtils .unescapeHtml(new
				 * String(Base64.decodeBase64(comboVal .getBytes()))));
				 */
			}
			//dynamicComboStr.append("</option>");
		}
		comboListHashMap.put(String.valueOf(dataDictionaryId),
				dynamicComboStr.toString());
	}

	private void getNonTableCodeDescCombo(
			HashMap<String, String> comboListHashMap, Long dataDictionaryId)
			throws UnsupportedEncodingException {
		List<CodeDesc> codeDescList = multilingualLogic
				.getCodeDescList(dataDictionaryId);
		StringBuilder dynamicCodeDescStr = new StringBuilder();
		//dynamicCodeDescStr.append("<option value=''>--select--</option>");
		for (CodeDesc codeDescVal : codeDescList) {

			String code = URLDecoder.decode(codeDescVal.getCode(), "UTF-8");
			dynamicCodeDescStr.append(code);
			dynamicCodeDescStr.append("#");
			//dynamicCodeDescStr.append("</option>");
		}
		comboListHashMap.put(String.valueOf(dataDictionaryId),
				dynamicCodeDescStr.toString());
	}

	private DynamicForm getMaxVersionDynamicForm(Long companyId,
			Map<Long, DynamicForm> formIdMap, Long entityId, Long formId) {
		DynamicForm dynamicForm;
		if (formIdMap.containsKey(formId)) {
			dynamicForm = formIdMap.get(formId);
		} else {
			dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
					entityId, formId);
			formIdMap.put(formId, dynamicForm);
		}
		return dynamicForm;
	}

	private void getStaticTypeFilterComboList(
			HashMap<String, String> comboListHashMap, Long entityId) {
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntity(entityId, PayAsiaConstants.STATIC_TYPE);
		if (dataDictionaryList != null) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				if (dataDictionary.getDataDictName()
						.equals("Employment Status")) {
					StringBuilder dynamicComboStr =  new StringBuilder();
					dynamicComboStr.append("Probation").append("#");
					dynamicComboStr.append("Confirmed");
					comboListHashMap.put(String.valueOf(dataDictionary .getDataDictionaryId()),dynamicComboStr.toString());
					 
				}
			}
		}
	}

	@Override
	public EmployeeShortListDTO getShortListEmployeeIdsForAdvanceFilter(
			Long companyId, String metaData) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();

		List<CompanyEmployeeShortList> companyEmployeeShortList = new ArrayList<>();
		setCompanyShortList(companyEmployeeShortList, metaData);

		Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
		List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
		setFilterInfo(companyEmployeeShortList, finalFilterList, tableNames,
				codeDescDTOs);

		EntityMaster entityMaster = entityMasterDAO
				.findById(PayAsiaConstants.EMPLOYEE_ENTITY_ID);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());

		String queryString = createShortListQuery(companyId, formIds,
				finalFilterList, tableNames, codeDescDTOs, paramValueMap);
		List<BigInteger> employeeIds = employeeDAO.checkForEmployeeDocuments(
				queryString, paramValueMap, null, companyId);
		employeeShortListDTO.setShortListEmployeeIds(employeeIds);
		employeeShortListDTO.setEmployeeShortList(true);

		return employeeShortListDTO;
	}

	@Override
	public EmployeeShortListDTO getShortListEmployeeIdsByCondition(
			CompanyEmployeeShortList companyEmployeeShortList, Long companyId) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();

		List<CompanyEmployeeShortList> companyEmpShortList = new ArrayList<>();
		companyEmpShortList.add(companyEmployeeShortList);

		Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
		List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
		setFilterInfo(companyEmpShortList, finalFilterList, tableNames,
				codeDescDTOs);

		EntityMaster entityMaster = entityMasterDAO
				.findById(PayAsiaConstants.EMPLOYEE_ENTITY_ID);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());

		String queryString = createShortListQuery(companyId, formIds,
				finalFilterList, tableNames, codeDescDTOs, paramValueMap);
		List<BigInteger> employeeIds = employeeDAO.checkForEmployeeDocuments(
				queryString, paramValueMap, null, companyId);
		employeeShortListDTO.setShortListEmployeeIds(employeeIds);
		employeeShortListDTO.setEmployeeShortList(true);

		return employeeShortListDTO;
	}

	private void setCompanyShortList(
			List<CompanyEmployeeShortList> companyEmployeeShortLists,
			String metaData) {
		String decodedMetaData=null;
		Unmarshaller unmarshaller = null;
		try {
			decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
			unmarshaller = EmployeeFilterXMLUtil.getDocumentUnmarshaller();
		} catch (UnsupportedEncodingException | JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (SAXException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}

		final StringReader xmlReader = new StringReader(decodedMetaData);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		EmployeeFilterTemplate empFilterTemplate = null;
		try {
			empFilterTemplate = (EmployeeFilterTemplate) unmarshaller
					.unmarshal(xmlSource);
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}

		List<EmployeeFilter> listOfFields = empFilterTemplate
				.getEmployeeFilter();
		for (EmployeeFilter field : listOfFields) {
			CompanyEmployeeShortList companyEmployeeShortList = new CompanyEmployeeShortList();

			if (field.getCloseBracket() != ""
					&& field.getCloseBracket() != null) {
				companyEmployeeShortList.setCloseBracket(field
						.getCloseBracket());
			}
			if (field.getOpenBracket() != "" && field.getOpenBracket() != null) {
				companyEmployeeShortList.setOpenBracket(field.getOpenBracket());

			}
			if (field.getDictionaryId() != 0) {
				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(field.getDictionaryId());
				companyEmployeeShortList.setDataDictionary(dataDictionary);
			}

			if (field.getEqualityOperator() != ""
					&& field.getEqualityOperator() != null) {
				try {
					String equalityOperator = URLDecoder.decode(
							field.getEqualityOperator(), "UTF-8");
					companyEmployeeShortList
							.setEqualityOperator(equalityOperator);
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}

			}
			if (field.getLogicalOperator() != ""
					&& field.getLogicalOperator() != null) {
				companyEmployeeShortList.setLogicalOperator(field
						.getLogicalOperator());
			}
			if (field.getValue() != "" && field.getValue() != null) {
				try {
					companyEmployeeShortList.setValue(URLDecoder.decode(
							field.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}

			}
			companyEmployeeShortLists.add(companyEmployeeShortList);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.GeneralLogic#getTypeOfApplication(com.payasia.dao.bean
	 * .LeaveApplication)
	 */
	@Override
	public String getTypeOfApplication(LeaveApplication leaveApplication) {

		if (leaveApplication.getLeaveCancelApplication() != null) {

			return PayAsiaConstants.LEAVE_APPLICATION_CANCEL_APPLICATION;

		} else {

			return PayAsiaConstants.LEAVE_APPLICATION_NEW_APPLICATION;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.GeneralLogic#getShortListEmployeeIds(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public EmployeeShortListDTO getShortListEmployeeIdsForReports(
			Long companyId, String metaData) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();

		List<CompanyEmployeeShortList> companyEmployeeShortList = new ArrayList<>();
		setCompanyShortList(companyEmployeeShortList, metaData);

		Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
		List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
		setFilterInfo(companyEmployeeShortList, finalFilterList, tableNames,
				codeDescDTOs);

		EntityMaster entityMaster = entityMasterDAO
				.findById(PayAsiaConstants.EMPLOYEE_ENTITY_ID);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());

		String queryString = createShortListQuery(companyId, formIds,
				finalFilterList, tableNames, codeDescDTOs, paramValueMap);
		List<BigInteger> employeeIds = new ArrayList<BigInteger>();
		employeeIds.addAll(employeeDAO.checkForEmployeeDocuments(queryString,
				paramValueMap, null, companyId));
		employeeShortListDTO.setShortListEmployeeIds(employeeIds);
		employeeShortListDTO.setEmployeeShortList(true);

		return employeeShortListDTO;
	}

	@Override
	public CustomFieldReportDTO getCustomFieldDataForLeaveReport(
			List<Long> dataDictionaryIdsList, List<Long> employeeIdsList,
			Long companyId, boolean showOnlyEmployeeDynFieldCode) {
		CustomFieldReportDTO customFieldReportDTO = new CustomFieldReportDTO();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		Company companyVO = companyDAO.findById(companyId);
		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());
		List<Object[]> tupleList = new ArrayList<Object[]>();
		List<String> dataDictNameList = new ArrayList<>();

		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		for (Long dataDictionaryId : dataDictionaryIdsList) {
			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(dataDictionaryId);//.findById(dataDictionaryId,companyId);
			dataDictNameList.add(dataDictionary.getLabel());
			if (dataDictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.STATIC_TYPE)) {
				setStaticDictionary(colMap, dataDictionary);
			} else {
				setDynamicDictionary(colMap, dataDictionary);

			}
			if (colMap.get(dataDictionary.getDataDictName()	+ dataDictionary.getDataDictionaryId())!=null && 
					colMap.get(dataDictionary.getDataDictName() + dataDictionary.getDataDictionaryId()).getTablePosition() != null) {
				String tableKey;
				tableKey = colMap.get(
						dataDictionary.getDataDictName()
								+ dataDictionary.getDataDictionaryId())
						.getFormId()
						+ colMap.get(
								dataDictionary.getDataDictName()
										+ dataDictionary.getDataDictionaryId())
								.getTablePosition();
				tableRecordInfoFrom.put(
						tableKey,
						colMap.get(dataDictionary.getDataDictName()
								+ dataDictionary.getDataDictionaryId()));

			}
		}
		if (!dataDictionaryIdsList.isEmpty()) {
			tupleList.addAll(employeeDAO.createQueryForCustomFieldReport(
					colMap, formIds, companyId, companyVO.getDateFormat(),
					tableRecordInfoFrom, employeeIdsList,
					showOnlyEmployeeDynFieldCode));
		}

		customFieldReportDTO.setCustomFieldObjList(tupleList);
		customFieldReportDTO.setDataDictNameList(dataDictNameList);
		return customFieldReportDTO;

	}

	@Override
	public CustomFieldReportDTO getCustomFieldDataForCompanyEntity(
			List<Long> dataDictionaryIdsList, Long companyId,
			boolean showOnlyCompanyDynFieldCode) {
		CustomFieldReportDTO customFieldReportDTO = new CustomFieldReportDTO();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);
		Company companyVO = companyDAO.findById(companyId);
		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
				entityMaster.getEntityId());
		List<Object[]> tupleList = new ArrayList<Object[]>();
		List<String> dataDictNameList = new ArrayList<>();

		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		for (Long dataDictionaryId : dataDictionaryIdsList) {
			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(dataDictionaryId);
			dataDictNameList.add(dataDictionary.getLabel());
			if (dataDictionary.getFieldType().equalsIgnoreCase(
					PayAsiaConstants.STATIC_TYPE)) {
				setStaticDictionary(colMap, dataDictionary);
			} else {
				setDynamicDictionary(colMap, dataDictionary);

			}
			if (colMap.get(
					dataDictionary.getDataDictName()
							+ dataDictionary.getDataDictionaryId())
					.getTablePosition() != null) {
				String tableKey;
				tableKey = colMap.get(
						dataDictionary.getDataDictName()
								+ dataDictionary.getDataDictionaryId())
						.getFormId()
						+ colMap.get(
								dataDictionary.getDataDictName()
										+ dataDictionary.getDataDictionaryId())
								.getTablePosition();
				tableRecordInfoFrom.put(
						tableKey,
						colMap.get(dataDictionary.getDataDictName()
								+ dataDictionary.getDataDictionaryId()));

			}
		}
		if (!dataDictionaryIdsList.isEmpty()) {
			tupleList.addAll(companyDAO.createQueryForCompanyCustomField(
					colMap, companyId, formIds, companyVO.getDateFormat(),
					tableRecordInfoFrom, showOnlyCompanyDynFieldCode));
		}

		customFieldReportDTO.setCustomFieldObjList(tupleList);
		customFieldReportDTO.setDataDictNameList(dataDictNameList);
		return customFieldReportDTO;

	}

	/**
	 * Gets the dynamic dictionary info.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 * @param dataImportKeyValueDTO
	 *            the data import key value dto
	 * @return the dynamic dictionary info
	 */
	public void setDynamicDictionary(Map<String, DataImportKeyValueDTO> colMap,
			DataDictionary dataDictionary) {

		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(dataDictionary
				.getCompany().getCompanyId(), dataDictionary.getEntityMaster()
				.getEntityId(), dataDictionary.getFormID());

		DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
				dataDictionary.getCompany().getCompanyId(), dataDictionary
						.getEntityMaster().getEntityId(), maxVersion,
				dataDictionary.getFormID());

		Tab tab = dataExportUtils.getTabObject(dynamicForm);

		List<Field> listOfFields = tab.getField();
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.LABEL_FIELD_TYPE)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
							.getColNumber(field.getName()));
					dataImportKeyValueDTO.setFormId(dynamicForm.getId()
							.getFormId());

					colMap.put(dataDictionary.getDataDictName()
							+ dataDictionary.getDataDictionaryId(),
							dataImportKeyValueDTO);
				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {

				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (new String(Base64.decodeBase64(column
							.getDictionaryName().getBytes()))
							.equals(dataDictionary.getDataDictName())) {
						dataImportKeyValueDTO.setStatic(false);
						dataImportKeyValueDTO.setChild(true);
						dataImportKeyValueDTO.setFieldType(column.getType());
						dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
								.getColNumber(column.getName()));
						dataImportKeyValueDTO.setFormId(dynamicForm.getId()
								.getFormId());
						dataImportKeyValueDTO
								.setTablePosition(PayAsiaStringUtils
										.getColNumber(field.getName()));
						colMap.put(dataDictionary.getDataDictName()
								+ dataDictionary.getDataDictionaryId(),
								dataImportKeyValueDTO);
					}
				}
			}
		}
	}

	/**
	 * Purpose: To get the static dictionary information.
	 * 
	 * @param dataDictionary
	 *            the data dictionary
	 * @param dataImportKeyValueDTO
	 *            the data import key value dto
	 * @return the static dictionary info
	 */
	private void setStaticDictionary(Map<String, DataImportKeyValueDTO> colMap,
			DataDictionary dataDictionary) {
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		try {
			ColumnPropertyDTO colProp = generalDAO.getColumnProperties(
					dataDictionary.getTableName(),
					dataDictionary.getColumnName());
			dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
			dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
					.getCamelCase(dataDictionary.getColumnName()));
			dataImportKeyValueDTO.setStatic(true);
			dataImportKeyValueDTO.setActualColName(dataDictionary
					.getColumnName());
			colMap.put(
					dataDictionary.getDataDictName()
							+ dataDictionary.getDataDictionaryId(),
					dataImportKeyValueDTO);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}
	}

	@Override
	public void saveDeviceDetails(DeviceDTO deviceDTO, EmployeeDTO employeeDTO) {
		EmployeeMobileDetails employeeMobileDetails = new EmployeeMobileDetails();
		employeeMobileDetails.setActive(true);
		employeeMobileDetails.setActivationDate(DateUtils
				.getCurrentTimestampWithTime());
		employeeMobileDetails.setDeviceID(deviceDTO.getDeviceId());
		employeeMobileDetails.setDeviceMacId(deviceDTO.getMacId());
		employeeMobileDetails.setDeviceOS(deviceDTO.getDeviceOS());
		employeeMobileDetails
				.setDeviceOSVersion(deviceDTO.getDeviceOSVersion());
		EmployeeActivationCode employeeActivationCode = employeeActivationCodeDAO
				.findByID(deviceDTO.getEmployeeActivationCodeId());
		employeeMobileDetails.setEmployeeActivationCode(employeeActivationCode);
		employeeMobileDetails.setPixelDensity(deviceDTO.getPixelDensity());
		employeeMobileDetails.setResolution(deviceDTO.getResolution());
		employeeMobileDetails.setDeviceToken(deviceDTO.getDeviceToken());

		EmployeeMobileDetails empMobileDetail = employeeMobileDetailsDAO
				.findByEmployeeActivationCode(deviceDTO
						.getEmployeeActivationCodeId());
		if (empMobileDetail == null) {
			employeeDTO.setUuid(deviceDTO.getDeviceId());
			employeeMobileDetailsDAO.save(employeeMobileDetails);
		} else {
			employeeDTO.setUuid(empMobileDetail.getDeviceID());
		}
	}

	@Override
	public Field getExistDynamicFormFieldInfo(String XML, Long dataDictionaryId) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(),
					sAXException);
		}
		final StringReader xmlReader = new StringReader(XML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				for (Column column : field.getColumn()) {
					if (column.getDictionaryId().equals(dataDictionaryId)) {
						return field;
					}
				}
			} else {
				if (field.getDictionaryId().equals(dataDictionaryId)) {
					return field;
				}
			}

		}
		return null;
	}

	@Override
	public EmployeeListFormPage getEmployeeHRISChangeRequestData(
			Long hrisChangeRequestId, Long companyId, String metadata,
			Long languageId) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		HRISChangeRequest hrisChangeRequest = hrisChangeRequestDAO
				.findById(hrisChangeRequestId);

		if(hrisChangeRequest!=null)
		{
		
		Field existingField = getExistDynamicFormFieldInfo(metadata,
				hrisChangeRequest.getDataDictionary().getDataDictionaryId());
		
		if (existingField !=null)
		{
		if (existingField.getType().equalsIgnoreCase(
				PayAsiaConstants.CODEDESC_FIELD_TYPE)) {

			List<CodeDesc> codeDescList = multilingualLogic
					.getCodeDescList(hrisChangeRequest.getDataDictionary()
							.getDataDictionaryId());
			for (CodeDesc codeDesc : codeDescList) {
				if (StringUtils.isNotBlank(hrisChangeRequest.getNewValue())) {
					if (codeDesc.getFieldRefId().equals(
							Long.parseLong(hrisChangeRequest.getNewValue()))) {
						try {
							employeeListFormPage.setNewValue(URLDecoder.decode(
									codeDesc.getCode(), "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}
					}
				}
				if (StringUtils.isNotBlank(hrisChangeRequest.getOldValue())) {
					if (codeDesc.getFieldRefId().equals(
							Long.parseLong(hrisChangeRequest.getOldValue()))) {
						try {
							employeeListFormPage.setOldValue(URLDecoder.decode(
									codeDesc.getCode(), "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}
					}
				}
			}

		} else if (existingField.getType().equalsIgnoreCase(
				PayAsiaConstants.TABLE_FIELD_TYPE)) {
			List<Column> columnList = existingField.getColumn();
			for (Column column : columnList) {
				if (column.getDictionaryId().equals(
						hrisChangeRequest.getDataDictionary()
								.getDataDictionaryId())) {
					if (column.getType().equalsIgnoreCase(
							PayAsiaConstants.CODEDESC_FIELD_TYPE)) {

						List<CodeDesc> codeDescList = multilingualLogic
								.getCodeDescList(hrisChangeRequest
										.getDataDictionary()
										.getDataDictionaryId());
						for (CodeDesc codeDesc : codeDescList) {
							if (StringUtils.isNotBlank(hrisChangeRequest
									.getNewValue())) {
								if (codeDesc.getFieldRefId().equals(
										Long.parseLong(hrisChangeRequest
												.getNewValue()))) {
									try {
										employeeListFormPage
												.setNewValue(URLDecoder.decode(
														codeDesc.getCode(),
														"UTF-8"));
									} catch (UnsupportedEncodingException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									}
								}
							}
							if (StringUtils.isNotBlank(hrisChangeRequest
									.getOldValue())) {
								if (codeDesc.getFieldRefId().equals(
										Long.parseLong(hrisChangeRequest
												.getOldValue()))) {
									try {
										employeeListFormPage
												.setOldValue(URLDecoder.decode(
														codeDesc.getCode(),
														"UTF-8"));
									} catch (UnsupportedEncodingException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(
												e.getMessage(), e);
									}
								}
							}
						}
					} else if (column.getType().equalsIgnoreCase(
							PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
						Employee newEmployeeVO = employeeDAO.findById(Long
								.parseLong(hrisChangeRequest.getNewValue()));

						Employee oldEmployeeVO = employeeDAO.findById(Long
								.parseLong(hrisChangeRequest.getOldValue()));
						employeeListFormPage
								.setNewValue(getEmployeeName(newEmployeeVO));
						employeeListFormPage
								.setEmpLstNewValue(hrisChangeRequest
										.getNewValue());
						employeeListFormPage
								.setEmpLstOldValue(hrisChangeRequest
										.getOldValue());
						employeeListFormPage
								.setOldValue(getEmployeeName(oldEmployeeVO));
					} else {
						if (column.getType().equalsIgnoreCase(
								PayAsiaConstants.FIELD_TYPE_DATE)) {
							String oldVal = "";
							if (StringUtils.isNotBlank(hrisChangeRequest
									.getOldValue())) {
								oldVal = DateUtils.convertDateToSpecificFormat(
										hrisChangeRequest.getOldValue(),
										hrisChangeRequest.getEmployee()
												.getCompany().getDateFormat());
							}

							String newVal = DateUtils
									.convertDateToSpecificFormat(
											hrisChangeRequest.getNewValue(),
											hrisChangeRequest.getEmployee()
													.getCompany()
													.getDateFormat());

							employeeListFormPage.setNewValue(newVal);
							employeeListFormPage.setOldValue(oldVal);
						} else {
							employeeListFormPage.setNewValue(hrisChangeRequest
									.getNewValue());
							employeeListFormPage.setOldValue(hrisChangeRequest
									.getOldValue());
						}

					}

				}

			}
		} else if (existingField.getType().equalsIgnoreCase(
				PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)) {
			Employee newEmployeeVO = employeeDAO.findById(Long
					.parseLong(hrisChangeRequest.getNewValue()));

			if(hrisChangeRequest.getOldValue()!=null || "".equals(hrisChangeRequest.getOldValue())){
			Employee oldEmployeeVO = employeeDAO.findById(Long
					.parseLong(hrisChangeRequest.getOldValue()));
			employeeListFormPage.setOldValue(getEmployeeName(oldEmployeeVO));
			}else{
				employeeListFormPage.setOldValue("");
			}
			employeeListFormPage.setNewValue(getEmployeeName(newEmployeeVO));
			
		} else {
			if (existingField.getType().equalsIgnoreCase(
					PayAsiaConstants.FIELD_TYPE_DATE)) {
				String oldVal = "";
				if (StringUtils.isNotBlank(hrisChangeRequest.getOldValue())) {
					oldVal = DateUtils
							.convertDateToSpecificFormat(
									hrisChangeRequest.getOldValue(),
									hrisChangeRequest.getEmployee()
											.getCompany().getDateFormat());
				}

				String newVal = DateUtils.convertDateToSpecificFormat(
						hrisChangeRequest.getNewValue(), hrisChangeRequest
								.getEmployee().getCompany().getDateFormat());

				employeeListFormPage.setNewValue(newVal);
				employeeListFormPage.setOldValue(oldVal);
			} else {
				employeeListFormPage.setNewValue(hrisChangeRequest
						.getNewValue());
				employeeListFormPage.setOldValue(hrisChangeRequest
						.getOldValue());
			}

		}
		String multilingualFieldLabel = multilingualLogic
				.convertFieldLabelToSpecificLanguage(languageId,
						hrisChangeRequest.getDataDictionary().getCompany()
								.getCompanyId(), hrisChangeRequest
								.getDataDictionary().getDataDictionaryId());
		if (StringUtils.isBlank(multilingualFieldLabel)) {
			multilingualFieldLabel = hrisChangeRequest.getDataDictionary()
					.getLabel();
		}
		
		employeeListFormPage.setFieldName(multilingualFieldLabel);
		return employeeListFormPage;
		}
		}
		return employeeListFormPage;

	}

	@Override
	public Boolean checkIsLeaveManager(Long employeeId) {

		Boolean isLeaveManager = false;

		List<EmployeeLeaveReviewer> leaveReviewersList = employeeLeaveReviewerDAO
				.checkEmployeeReviewer(employeeId);
		if (leaveReviewersList != null) {
			isLeaveManager = true;
		} else {
			List<String> leaveStatusList = new ArrayList<>();
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			List<LeaveApplicationReviewer> leaveAppRevsList = leaveApplicationReviewersDAO
					.checkEmployeeReviewer(employeeId, leaveStatusList);
			if (leaveAppRevsList != null) {
				isLeaveManager = true;
			}
		}
		return isLeaveManager;

	}

	/**
	 * Purpose : Get Authorized Section List.
	 * 
	 * @param EmployeeId
	 *            the employee Id
	 * @param CompanyId
	 *            the company id
	 * @param EntityId
	 *            the entityId
	 * @return the List<Long> List of Section Ids
	 */
	@Override
	public List<Long> getAdminAuthorizedSectionIdList(Long employeeId,
			Long companyId, Long entityId) {

		List<Long> emplRoleSecFormIds = employeeRoleSectionMappingDAO
				.findByCondition(employeeId, companyId);

		Set<Long> authorizedFormIds = new LinkedHashSet<>();
		authorizedFormIds.addAll(emplRoleSecFormIds);

		Long empBasicTabFormId;
		Integer employeeBasicFormMax = dynamicFormDAO.getMaxVersion(companyId,
				entityId, PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
		if (employeeBasicFormMax != null) {
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityId, employeeBasicFormMax,
					PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
			empBasicTabFormId = dynamicForm.getId().getFormId();
		} else {
			empBasicTabFormId = 0l;
		}

		List<Long> formIdList;
		if (!authorizedFormIds.isEmpty()) {
			formIdList = new ArrayList<>();
			if (!authorizedFormIds.contains(empBasicTabFormId)) {
				formIdList.add(empBasicTabFormId);
			}
			formIdList.addAll(authorizedFormIds);

		} else {
			formIdList = dynamicFormDAO.getDistinctFormId(companyId, entityId);
		}

		return formIdList;
	}

	@Override
	public List<Long> getEmployeeAuthorizedSectionIdList(Long employeeId,
			Long companyId, Long entityId) {

		List<Long> empRoleMapFormIds = new ArrayList<>();
		List<EmployeeRoleMapping> employeeRoleMappings = employeeRoleMappingDAO
				.findByConditionEmpIdAndCompanyId(companyId, employeeId);
		for (EmployeeRoleMapping employeeRoleMapping : employeeRoleMappings) {
			if (employeeRoleMapping.getRoleMaster().getRoleName()
					.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_DEFAULT_ROLE)) {
				empRoleMapFormIds.add(employeeRoleMapping.getRoleMaster()
						.getRoleId());
			}
		}
		List<Long> roleSecFormIds = null;
		if (!empRoleMapFormIds.isEmpty()) {
			roleSecFormIds = roleSectionMappingDAO
					.findByRoleIds(empRoleMapFormIds);
		}

		Long empBasicTabFormId;
		Integer employeeBasicFormMax = dynamicFormDAO.getMaxVersion(companyId,
				entityId, PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
		if (employeeBasicFormMax != null) {
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityId, employeeBasicFormMax,
					PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
			empBasicTabFormId = dynamicForm.getId().getFormId();
		} else {
			empBasicTabFormId = 0l;
		}

		List<Long> formIdList = null;
		if (roleSecFormIds != null) {
			if (!roleSecFormIds.isEmpty()) {
				formIdList = new ArrayList<>();
				if (!roleSecFormIds.contains(empBasicTabFormId)) {
					formIdList.add(empBasicTabFormId);
				}
				formIdList.addAll(roleSecFormIds);

			} else {
				formIdList = dynamicFormDAO.getDistinctFormId(companyId,
						entityId);
			}

		} else {
			formIdList = dynamicFormDAO.getDistinctFormId(companyId, entityId);

		}

		return formIdList;
	}

	@Override
	public Boolean checkIsClaimManager(Long employeeId) {

		Boolean isClaimManager = false;
		List<EmployeeClaimReviewer> claimReviewersList = employeeClaimReviewerDAO
				.checkEmployeeClaimReviewer(employeeId);
		if (claimReviewersList != null) {
			isClaimManager = true;
		} else {
			List<String> claimStatusList = new ArrayList<>();
			claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
			claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
			List<ClaimApplicationReviewer> claimAppRevsList = claimApplicationReviewerDAO
					.checkClaimEmployeeReviewer(employeeId, claimStatusList);
			if (claimAppRevsList != null) {
				isClaimManager = true;
			}
		}
		return isClaimManager;
	}

	@Override
	public NotificationForm getPendingNotifications(String mobileId) {
		NotificationForm notificationForm = new NotificationForm();
		EmployeeMobileDetails employeeMobileDetails = employeeMobileDetailsDAO
				.findByMobileId(mobileId);

		if (employeeMobileDetails == null) {
			return notificationForm;
		}

		NotificationAlertConditionDTO notificationAlertConditionDTO = new NotificationAlertConditionDTO();
		notificationAlertConditionDTO.setEmployeeId(employeeMobileDetails
				.getEmployeeActivationCode().getEmployee().getEmployeeId());
		notificationAlertConditionDTO.setShownStatus(false);

		List<Long> notificationAlertIds = new ArrayList<>();
		List<NotificationAlert> notificationAlerts = notificationAlertDAO
				.findByCondtion(notificationAlertConditionDTO);
		List<NotificationAlertDTO> notificationAlertDTOs = new ArrayList<>();
		for (NotificationAlert notificationAlert : notificationAlerts) {
			NotificationAlertDTO notificationAlertDTO = new NotificationAlertDTO();
			notificationAlertDTO.setNotificationId(notificationAlert
					.getNotificationAlertId());
			notificationAlertDTO.setNotificationText(notificationAlert
					.getMessage());
			notificationAlertDTO.setNotificationTitle(notificationAlert
					.getModuleMaster().getModuleName());
			notificationAlertDTOs.add(notificationAlertDTO);
			notificationAlertIds
					.add(notificationAlert.getNotificationAlertId());
			notificationAlert.setShownStatus(true);
			notificationAlertDAO.update(notificationAlert);

		}
		notificationForm.setNotificationAlertDTOs(notificationAlertDTOs);

		return notificationForm;
	}

	@Override
	public EmployeeShortListDTO getExcelExportShortListEmployeeIds(
			Long templateId, Long companyId) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		List<BigInteger> employeeIds = new ArrayList<>();
		EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();

		EmpDataExportTemplate empDataExportTemplate = empDataExportTemplateDAO
				.findById(templateId);

		List<EmpDataExportTemplateFilter> empDataExportTemplateFilterList = new ArrayList<EmpDataExportTemplateFilter>(
				empDataExportTemplate.getEmpDataExportTemplateFilters());

		if (empDataExportTemplateFilterList == null
				|| empDataExportTemplateFilterList.size() == 0) {
			employeeShortListDTO.setShortListEmployeeIds(employeeIds);
			employeeShortListDTO.setEmployeeShortList(false);

		} else {
			employeeIds = new ArrayList<BigInteger>();
			Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
			List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
			List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
			setExcelExportFilterInfo(empDataExportTemplateFilterList,
					finalFilterList, tableNames, codeDescDTOs);

			EntityMaster entityMaster = entityMasterDAO
					.findById(PayAsiaConstants.EMPLOYEE_ENTITY_ID);

			List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
					entityMaster.getEntityId());

			String queryString = createShortListQuery(companyId, formIds,
					finalFilterList, tableNames, codeDescDTOs, paramValueMap);

			employeeIds.addAll(employeeDAO.checkForEmployeeDocuments(
					queryString, paramValueMap, null, companyId));
			employeeShortListDTO.setShortListEmployeeIds(employeeIds);
			employeeShortListDTO.setEmployeeShortList(true);
		}

		return employeeShortListDTO;
	}

	private void setExcelExportFilterInfo(
			List<EmpDataExportTemplateFilter> empDataExportTemplateFilterList,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs) {

		for (EmpDataExportTemplateFilter empDataExportTemplateFilter : empDataExportTemplateFilterList) {
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			if (empDataExportTemplateFilter.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

				filtersInfoUtilsLogic.getStaticDictionaryInfo(
						empDataExportTemplateFilter.getDataDictionary(),
						dataImportKeyValueDTO);

			} else {

				filtersInfoUtilsLogic.getDynamicDictionaryInfo(
						empDataExportTemplateFilter.getDataDictionary(),
						dataImportKeyValueDTO, tableNames, codeDescDTOs);

			}
			GeneralFilterDTO generalFilterDTO = new GeneralFilterDTO();

			if (empDataExportTemplateFilter.getCloseBracket() != null) {
				generalFilterDTO.setCloseBracket(empDataExportTemplateFilter
						.getCloseBracket());
			} else {
				generalFilterDTO.setCloseBracket("");
			}

			if (empDataExportTemplateFilter.getOpenBracket() != null) {
				generalFilterDTO.setOpenBracket(empDataExportTemplateFilter
						.getOpenBracket());
			} else {
				generalFilterDTO.setOpenBracket("");
			}

			generalFilterDTO.setDataDictionaryName(empDataExportTemplateFilter
					.getDataDictionary().getDataDictName());
			generalFilterDTO.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			generalFilterDTO.setDictionaryId(empDataExportTemplateFilter
					.getDataDictionary().getDataDictionaryId());
			generalFilterDTO.setEqualityOperator(empDataExportTemplateFilter
					.getEqualityOperator());
			generalFilterDTO.setFilterId(empDataExportTemplateFilter
					.getExportFilterId());
			generalFilterDTO.setLogicalOperator(empDataExportTemplateFilter
					.getLogicalOperator());
			generalFilterDTO.setValue(empDataExportTemplateFilter.getValue());

			finalFilterList.add(generalFilterDTO);
		}
	}

	@Override
	public List<EmployeeFieldDTO> returnEmployeesList(Long employeeId,
			Long companyId) {

		List<EmployeeFieldDTO> employees = new ArrayList<>();
		List<Employee> employeeVOs = employeeDAO.findByCompany(companyId);
		for (Employee employee : employeeVOs) {
			EmployeeFieldDTO employeeFieldDTO = new EmployeeFieldDTO();
			employeeFieldDTO.setEmployeeId(employee.getEmployeeId());
			try {
				employeeFieldDTO.setEmployeeName(URLEncoder.encode(
						getEmployeeName(employee), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			employees.add(employeeFieldDTO);

		}
		return employees;

	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();

		if (StringUtils.isNotBlank(employee.getMiddleName())) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " [" + employee.getEmployeeNumber() + "]";
		return employeeName;
	}

	@Override
	public String getValidEmployeeNumberFromFileName(String fileName,
			List<CompanyDocumentLogDTO> documentLogs, Long companyId,
			boolean isFileNameContainsCompName, List<String> invalidZipFileNames) {
		CompanyDocumentLogDTO companyDocumentLogDTO = new CompanyDocumentLogDTO();
		String[] fileNameArr;
		String employeeNumber;
		String ZipFileEmployeeNumber;
		try {
			fileNameArr = fileName.split(fileNameSeperator);
			Employee emp = null;
			if (isFileNameContainsCompName) {
				ZipFileEmployeeNumber = fileNameArr[1];
				emp = employeeDAO.findByNumber(fileNameArr[1], companyId);
			} else {
				ZipFileEmployeeNumber = fileNameArr[0];
				emp = employeeDAO.findByNumber(fileNameArr[0], companyId);
			}

			if (emp != null) {
				employeeNumber = emp.getEmployeeNumber();
			} else {

				companyDocumentLogDTO.setName(fileName);
				companyDocumentLogDTO
						.setMessage("No Employee exists with given employee code");
				documentLogs.add(companyDocumentLogDTO);
				invalidZipFileNames.add(ZipFileEmployeeNumber);
				employeeNumber = PayAsiaConstants.PAYASIA_ERROR;
			}

		} catch (Exception exception) {

			companyDocumentLogDTO.setName(fileName);
			companyDocumentLogDTO.setMessage("Invalid fileName");
			documentLogs.add(companyDocumentLogDTO);
			employeeNumber = PayAsiaConstants.PAYASIA_ERROR;
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);

		}
		return employeeNumber;
	}

	@Override
	public boolean isValidFileType(String fileExt) {
		boolean isFileExtValid = false;
		List<String> fileExtList = new ArrayList<>();
		fileExtList.add(PayAsiaConstants.FILE_TYPE_ZIP);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_BMP);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_DOC);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_DOCX);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_GIF);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_JPEG);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_JPG);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_PNG);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_TXT);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_XLS);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_XLSX);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_PDF);
		fileExtList.add(PayAsiaConstants.FILE_TYPE_PPT);

		if (fileExtList.contains(fileExt.toLowerCase())) {
			isFileExtValid = true;
		}
		return isFileExtValid;
	}

	public Boolean isEmployeeClaimSummaryEnabled(Long employeeId) {
		int empClaimSummPrivCount = 0;
		Employee employeeVO = employeeDAO.findById(employeeId);
		List<EmployeeRoleMapping> employeeRoleMappingVOList = employeeRoleMappingDAO
				.findByConditionEmpIdAndCompanyId(employeeVO.getCompany()
						.getCompanyId(), employeeId);
		ManageRolesConditionDTO conditionDTO = new ManageRolesConditionDTO();
		conditionDTO
				.setPrivilege(PayAsiaConstants.PAYASIA_PRIVILEGE_EMPLOYEE_CLAIM_SUMMARY_DESC);
		if (employeeRoleMappingVOList != null
				&& !employeeRoleMappingVOList.isEmpty()) {
			for (EmployeeRoleMapping employeeRoleMappingVO : employeeRoleMappingVOList) {
				List<PrivilegeMaster> privilegeList = privilegeMasterDAO
						.findByRole(employeeRoleMappingVO.getRoleMaster()
								.getRoleId(), conditionDTO);
				if (!privilegeList.isEmpty()) {
					empClaimSummPrivCount++;
				}
			}
		}
		if (empClaimSummPrivCount > 0) {
			return true;
		}
		return false;
	}

	@Override
	public String getEmpoyeeDynamicFormFieldValue(
			DataDictionary dataDictionary, Long employeeId, Long companyId) {
		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
				companyId, dataDictionary.getEntityMaster().getEntityId(),
				dataDictionary.getFormID());

		DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO
				.getEmpRecords(employeeId, dynamicForm.getId().getVersion(),
						dynamicForm.getId().getFormId(), dynamicForm.getId()
								.getEntity_ID(), companyId);
		String fieldValue = "";

		if (dynamicFormRecord == null) {
			return fieldValue;
		}

		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
		Tab tab = dataExportUtils.getTabObject(dynamicForm);
		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (field.getDictionaryId().equals(
					dataDictionary.getDataDictionaryId())) {
				String fieldName = field.getName();
				String fieldtype = field.getType();

				String getMethodName = "getCol"
						+ fieldName.substring(fieldName.lastIndexOf('_') + 1,
								fieldName.length());

				try {
					Method dynamicFormRecordMethod = dynamicFormRecordClass
							.getMethod(getMethodName);
					try {
						fieldValue = (String) dynamicFormRecordMethod
								.invoke(dynamicFormRecord);
					} catch (IllegalAccessException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					} catch (IllegalArgumentException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					} catch (InvocationTargetException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					}
				} catch (NoSuchMethodException e) {
					LOGGER.error(e.getMessage(), e);
					throw new PayAsiaSystemException(e.getMessage(), e);
				} catch (SecurityException e) {
					LOGGER.error(e.getMessage(), e);
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

				if (fieldtype
						.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
						&& !"".equalsIgnoreCase(fieldValue)) {
					fieldValue = DateUtils.convertDateToSpecificFormat(
							fieldValue, dataDictionary.getCompany()
									.getDateFormat());
				}
				if (fieldtype
						.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
						&& StringUtils.isNotBlank(fieldValue)) {
					DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
							.findById(Long.parseLong(fieldValue));
					fieldValue = dynamicFormFieldRefValue.getCode();
				}

			}
			if (field.getType().equalsIgnoreCase(
					PayAsiaConstants.TABLE_FIELD_TYPE)) {
				List<Column> columnList = field.getColumn();
				for (Column column : columnList) {
					String tableId;
					if (!column.getDictionaryId().equals(
							dataDictionary.getDataDictionaryId())) {
						continue;
					}
					String getMethodName = "getCol"
							+ field.getName().substring(
									field.getName().lastIndexOf('_') + 1,
									field.getName().length());

					try {
						Method dynamicFormRecordMethod = dynamicFormRecordClass
								.getMethod(getMethodName);
						try {
							tableId = (String) dynamicFormRecordMethod
									.invoke(dynamicFormRecord);
						} catch (IllegalAccessException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						} catch (IllegalArgumentException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						} catch (InvocationTargetException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}
					} catch (NoSuchMethodException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					} catch (SecurityException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					}

					if (StringUtils.isBlank(tableId)) {
						continue;
					}

					List<DynamicFormTableRecord> dynamicFormTableRecordList = dynamicFormTableRecordDAO
							.getTableRecords(Long.parseLong(tableId), null,
									null);
					List<Date> effdateList = new ArrayList<Date>();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy/MM/dd");
					for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecordList) {
						String effDateString = dynamicFormTableRecord.getCol1()
								.substring(0, 10);
						Date effDate;
						try {
							effDate = formatter.parse(effDateString);
							effdateList.add(effDate);
						} catch (ParseException e) {
							LOGGER.error(e.getMessage(), e);
						}
					}
					Comparator<Date> comparator = Collections.reverseOrder();
					Collections.sort(effdateList, comparator);
					Date currentDate = DateUtils.stringToDate(
							DateUtils.getCurrentDateWithFormat("yyyy/MM/dd"),
							"yyyy/MM/dd");
					Date requiredEffdate = null;
					for (Date effectiveDate : effdateList) {
						if (!currentDate.before(effectiveDate)) {
							requiredEffdate = effectiveDate;
							break;
						}
					}
					DynamicFormTableRecord dynamicFormTabRecord = null;
					for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecordList) {
						String effDateString = dynamicFormTableRecord.getCol1()
								.substring(0, 10);
						Date effDate = null;
						try {
							effDate = formatter.parse(effDateString);
						} catch (ParseException e) {
							LOGGER.error(e.getMessage(), e);
						}
						if (requiredEffdate != null) {
							if (requiredEffdate.equals(effDate)) {
								dynamicFormTabRecord = dynamicFormTableRecord;
							}
						}
					}

					if (dynamicFormTabRecord == null) {
						continue;
					}

					Class<?> dynamicFormTableRecordClass = dynamicFormTabRecord
							.getClass();
					String colName = column.getName();
					String colNameCount = colName.substring(
							colName.lastIndexOf('_') + 1, colName.length());

					String dynamicFormTableRecordMethodName = PayAsiaConstants.GET_COL
							+ colNameCount;

					Method dynamicFormTableRecordMethod = null;
					try {
						dynamicFormTableRecordMethod = dynamicFormTableRecordClass
								.getMethod(dynamicFormTableRecordMethodName);
					} catch (NoSuchMethodException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					} catch (SecurityException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					}

					try {
						fieldValue = (String) dynamicFormTableRecordMethod
								.invoke(dynamicFormTabRecord);
					} catch (IllegalAccessException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					} catch (IllegalArgumentException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					} catch (InvocationTargetException e) {
						LOGGER.error(e.getMessage(), e);
						throw new PayAsiaSystemException(e.getMessage(), e);
					}

					if (column.getType().equalsIgnoreCase(
							PayAsiaConstants.FIELD_TYPE_DATE)
							&& StringUtils.isNotBlank(fieldValue)) {

						fieldValue = DateUtils.convertDateToSpecificFormat(
								fieldValue, dataDictionary.getCompany()
										.getDateFormat());

					} else if (column.getType().equalsIgnoreCase(
							PayAsiaConstants.CODEDESC_FIELD_TYPE)
							&& StringUtils.isNotBlank(fieldValue)) {

						DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
								.findById(Long.parseLong(fieldValue));
						try {
							fieldValue = dynamicFormFieldRefValue.getCode();
						} catch (Exception exception) {
							LOGGER.error(exception.getMessage(), exception);
							fieldValue = "";
						}

					}
				}
			}
		}
		return fieldValue;
	}

	@Override
	public String getEmployeeNameWithNumber(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}

	@Override
	public String getChildCareLeaveTypeInfo(Long companyId, Long employeeId) {
		String leaveSchemeTypeIds = "";
		List<Long> validSchemeTypes = new ArrayList<>();
		Company companyVO = companyDAO.findById(companyId);

		EmployeeLeaveScheme employeeLeaveSchemeVO = employeeLeaveSchemeDAO
				.getActiveLeaveSchemeByDate(employeeId, DateUtils
						.timeStampToString(
								DateUtils.getCurrentTimestampWithTime(),
								companyVO.getDateFormat()), companyVO
						.getDateFormat());
		if (employeeLeaveSchemeVO != null
				&& employeeLeaveSchemeVO.getLeaveScheme().getLeaveSchemeTypes() != null) {
			HashMap<Long, Tab> tabMap = new HashMap<>();
			EntityMaster entityMaster = entityMasterDAO
					.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

			List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId,
					entityMaster.getEntityId());
			for (LeaveSchemeType leaveSchemeType : employeeLeaveSchemeVO
					.getLeaveScheme().getLeaveSchemeTypes()) {
				if (leaveSchemeType.getLeaveTypeMaster().getLeaveType() != null
						&& (leaveSchemeType
								.getLeaveTypeMaster()
								.getLeaveType()
								.getCodeDesc()
								.equalsIgnoreCase(
										PayAsiaConstants.APP_CODE_LEAVE_TYPE_CHILD_CARE_LEAVE) || leaveSchemeType
								.getLeaveTypeMaster()
								.getLeaveType()
								.getCodeDesc()
								.equalsIgnoreCase(
										PayAsiaConstants.APP_CODE_LEAVE_TYPE_EXTENDED_CHILD_CARE_LEAVE))) {
					Map<String, String> paramValueMap = new HashMap<String, String>();
					Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
					List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
					List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
					List<LeaveSchemeTypeShortList> leaveSchemeTypeShortList = new ArrayList<>(
							leaveSchemeType.getLeaveSchemeTypeShortLists());
					setFilterInfo(leaveSchemeTypeShortList, finalFilterList,
							tableNames, codeDescDTOs, tabMap);

					String query = filtersInfoUtilsLogic.createQuery(
							employeeId, formIds, finalFilterList, tableNames,
							codeDescDTOs, companyId, paramValueMap);

					List<BigInteger> resultList = employeeDAO
							.checkForEmployeeDocuments(query, paramValueMap,
									employeeId, null);

					if (!resultList.isEmpty()) {
						validSchemeTypes.add(leaveSchemeType
								.getLeaveSchemeTypeId());
					}
				}
			}
		}

		StringBuilder leaveSchemeTypeIdsBuilder = new StringBuilder();
		for (Long leaveSchemeTypeId : validSchemeTypes) {
			leaveSchemeTypeIdsBuilder = leaveSchemeTypeIdsBuilder
					.append(leaveSchemeTypeId);
			leaveSchemeTypeIdsBuilder = leaveSchemeTypeIdsBuilder.append(",");
		}
		leaveSchemeTypeIds = leaveSchemeTypeIdsBuilder.toString();
		leaveSchemeTypeIds = StringUtils.removeEnd(
				leaveSchemeTypeIdsBuilder.toString(), ",");

		return leaveSchemeTypeIds;
	}

	private void setFilterInfo(
			List<LeaveSchemeTypeShortList> leaveSchemeTypeShortList,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			HashMap<Long, Tab> tabMap) {
		for (LeaveSchemeTypeShortList schemeTypeShortList : leaveSchemeTypeShortList) {
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			if (schemeTypeShortList.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

				filtersInfoUtilsLogic.getStaticDictionaryInfo(
						schemeTypeShortList.getDataDictionary(),
						dataImportKeyValueDTO);

			} else {

				filtersInfoUtilsLogic
						.getDynamicDictionaryInfo(
								schemeTypeShortList.getDataDictionary(),
								dataImportKeyValueDTO, tableNames,
								codeDescDTOs, tabMap);

			}
			GeneralFilterDTO generalFilterDTO = new GeneralFilterDTO();

			if (schemeTypeShortList.getCloseBracket() != null) {
				generalFilterDTO.setCloseBracket(schemeTypeShortList
						.getCloseBracket());
			} else {
				generalFilterDTO.setCloseBracket("");
			}

			if (schemeTypeShortList.getOpenBracket() != null) {
				generalFilterDTO.setOpenBracket(schemeTypeShortList
						.getOpenBracket());
			} else {
				generalFilterDTO.setOpenBracket("");
			}

			generalFilterDTO.setDataDictionaryName(schemeTypeShortList
					.getDataDictionary().getDataDictName());
			generalFilterDTO.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			generalFilterDTO.setDictionaryId(schemeTypeShortList
					.getDataDictionary().getDataDictionaryId());
			generalFilterDTO.setEqualityOperator(schemeTypeShortList
					.getEqualityOperator());
			generalFilterDTO.setFilterId(schemeTypeShortList.getShortListId());
			generalFilterDTO.setLogicalOperator(schemeTypeShortList
					.getLogicalOperator());
			generalFilterDTO.setValue(schemeTypeShortList.getValue());

			finalFilterList.add(generalFilterDTO);
		}
	}

	@Override
	public void setStaticDictionary(Map<String, DataImportKeyValueDTO> colMap,
			String colKey, DataDictionary dataDictionary) {
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		try {
			ColumnPropertyDTO colProp = generalDAO.getColumnProperties(
					dataDictionary.getTableName(),
					dataDictionary.getColumnName());
			dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
			dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
					.getCamelCase(dataDictionary.getColumnName()));
			dataImportKeyValueDTO.setStatic(true);
			dataImportKeyValueDTO.setActualColName(dataDictionary
					.getColumnName());
			colMap.put(colKey, dataImportKeyValueDTO);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}
	}

	@Override
	public void getColMap(DataDictionary dataDictionary,
			Map<String, DataImportKeyValueDTO> colMap) {
		Long formId = dataDictionary.getFormID();
		Tab tab = null;
		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(dataDictionary
				.getCompany().getCompanyId(), dataDictionary.getEntityMaster()
				.getEntityId(), dataDictionary.getFormID());

		DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
				dataDictionary.getCompany().getCompanyId(), dataDictionary
						.getEntityMaster().getEntityId(), maxVersion,
				dataDictionary.getFormID());

		tab = dataExportUtils.getTabObject(dynamicForm);
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {

				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (column.getDictionaryId().equals(
							dataDictionary.getDataDictionaryId())) {
						dataImportKeyValueDTO.setStatic(false);
						dataImportKeyValueDTO.setChild(true);
						dataImportKeyValueDTO.setFieldType(column.getType());
						String colNumber = PayAsiaStringUtils
								.getColNumber(column.getName());
						dataImportKeyValueDTO.setMethodName(colNumber);
						dataImportKeyValueDTO.setFormId(formId);
						dataImportKeyValueDTO
								.setActualColName(new String(Base64
										.decodeBase64(field.getLabel()
												.getBytes())));
						String tablePosition = PayAsiaStringUtils
								.getColNumber(field.getName());
						dataImportKeyValueDTO.setTablePosition(tablePosition);
						if (dataDictionary
								.getEntityMaster()
								.getEntityName()
								.equalsIgnoreCase(
										PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
							colMap.put(dataDictionary.getLabel()
									+ dataDictionary.getDataDictionaryId(),
									dataImportKeyValueDTO);
						}

					}
				}
			}
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {
				if (field.getDictionaryId().equals(
						dataDictionary.getDataDictionaryId())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(true);
					dataImportKeyValueDTO.setFieldType(field.getType());
					String colNumber = PayAsiaStringUtils.getColNumber(field
							.getName());
					dataImportKeyValueDTO.setMethodName(colNumber);
					dataImportKeyValueDTO.setFormId(formId);
					dataImportKeyValueDTO.setActualColName(new String(Base64
							.decodeBase64(field.getLabel().getBytes())));
					String tablePosition = PayAsiaStringUtils
							.getColNumber(field.getName());
					dataImportKeyValueDTO.setTablePosition(tablePosition);
					if (dataDictionary
							.getEntityMaster()
							.getEntityName()
							.equalsIgnoreCase(
									PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
						colMap.put(
								dataDictionary.getLabel()
										+ dataDictionary.getDataDictionaryId(),
								dataImportKeyValueDTO);
					}

				}
			}
		}
	}
}
