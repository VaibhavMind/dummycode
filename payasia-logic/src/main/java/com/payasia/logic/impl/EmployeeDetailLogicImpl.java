/**
 * 
 * 
 * @author ragulapraveen
 */
package com.payasia.logic.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Row;
import com.mind.payasia.xml.bean.RowValue;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AccessControlConditionDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeDocumentHistoryDTO;
import com.payasia.common.dto.EmployeeDynamicFormDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DynamicFormTableDocumentDTO;
import com.payasia.common.form.EmployeeForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.EntityListViewFieldForm;
import com.payasia.common.form.EntityListViewForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.FormulaUtils;
import com.payasia.common.util.HRISUtils;
import com.payasia.common.util.ImageUtils;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.common.util.ValidationUtils;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.AppConfigMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyEmployeeShortListDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.EmployeeActivationCodeDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeDocumentHistoryDAO;
import com.payasia.dao.EmployeeHRISReviewerDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.EmployeeLoginHistoryDAO;
import com.payasia.dao.EmployeeMobileDetailsDAO;
import com.payasia.dao.EmployeePhotoDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.EmployeeRoleSectionMappingDAO;
import com.payasia.dao.EntityListViewDAO;
import com.payasia.dao.EntityListViewFieldDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.HRISChangeRequestDAO;
import com.payasia.dao.HRISChangeRequestReviewerDAO;
import com.payasia.dao.HRISChangeRequestWorkflowDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.HRISStatusMasterDAO;
import com.payasia.dao.LanguageMasterDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.MultiLingualDataDAO;
import com.payasia.dao.PasswordPolicyConfigMasterDAO;
import com.payasia.dao.RoleMasterDAO;
import com.payasia.dao.RoleSectionMappingDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.AppConfigMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyModuleMapping;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.DynamicFormTableRecordPK;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeActivationCode;
import com.payasia.dao.bean.EmployeeDocumentHistory;
import com.payasia.dao.bean.EmployeeHRISReviewer;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.EmployeeMobileDetails;
import com.payasia.dao.bean.EmployeePhoto;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.dao.bean.EmployeeRoleMappingPK;
import com.payasia.dao.bean.EntityListView;
import com.payasia.dao.bean.EntityListViewField;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISChangeRequestReviewer;
import com.payasia.dao.bean.HRISChangeRequestWorkflow;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.HRISStatusMaster;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.FiltersInfoUtilsLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.HRISMailLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.logic.SecurityLogic;

import net.sf.json.JSONObject;

/**
 * The Class EmployeeDetailLogicImpl.
 */
@Component
public class EmployeeDetailLogicImpl implements EmployeeDetailLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(EmployeeDetailLogicImpl.class);

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The dynamic form record dao. */
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The dynamic form table record dao. */
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The entity list view field dao. */
	@Resource
	EntityListViewFieldDAO entityListViewFieldDAO;

	/** The entity list view dao. */
	@Resource
	EntityListViewDAO entityListViewDAO;

	/** The employee photo dao. */
	@Resource
	EmployeePhotoDAO employeePhotoDAO;

	/** The role master dao. */
	@Resource
	RoleMasterDAO roleMasterDAO;

	/** The employee role mapping dao. */
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	/** The app config master dao. */
	@Resource
	AppConfigMasterDAO appConfigMasterDAO;

	/** The multilingual logic. */
	@Resource
	MultilingualLogic multilingualLogic;

	/** The email preference master dao. */
	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	/** The email template sub category master dao. */
	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;

	/** The email template dao. */
	@Resource
	EmailTemplateDAO emailTemplateDAO;

	/** The pay asia mail utils. */
	@Resource
	PayAsiaMailUtils payAsiaMailUtils;

	/** The password policy config master dao. */
	@Resource
	PasswordPolicyConfigMasterDAO passwordPolicyConfigMasterDAO;

	/** The language master dao. */
	@Resource
	LanguageMasterDAO languageMasterDAO;

	/** The company employee short list dao. */
	@Resource
	CompanyEmployeeShortListDAO companyEmployeeShortListDAO;

	/** The filters info utils logic. */
	@Resource
	FiltersInfoUtilsLogic filtersInfoUtilsLogic;

	/** The multi lingual data dao. */
	@Resource
	MultiLingualDataDAO multiLingualDataDAO;

	/** The employee login history dao. */
	@Resource
	EmployeeLoginHistoryDAO employeeLoginHistoryDAO;

	/** The dynamic form field ref value dao. */
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	SecurityLogic securityLogic;

	@Resource
	EmployeeLoginDetailDAO employeeLoginDetailDAO;
	@Resource
	HRISChangeRequestDAO hrisChangeRequestDAO;
	@Resource
	HRISStatusMasterDAO hrisStatusMasterDAO;
	@Resource
	EmployeeHRISReviewerDAO employeeHRISReviewerDAO;

	@Resource
	HRISChangeRequestReviewerDAO hrisChangeRequestReviewerDAO;
	@Resource
	HRISChangeRequestWorkflowDAO hrisChangeRequestWorkflowDAO;

	@Resource
	HRISMailLogic hrisMailLogic;
	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	EmployeeMobileDetailsDAO employeeMobileDetailsDAO;

	@Resource
	EmployeeActivationCodeDAO employeeActivationCodeDAO;
	@Resource
	EmployeeRoleSectionMappingDAO employeeRoleSectionMappingDAO;
	@Resource
	RoleSectionMappingDAO roleSectionMappingDAO;

	@Resource
	EmployeeDocumentHistoryDAO employeeDocumentHistoryDAO;
	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;
	@Resource
	GeneralDAO generalDAO;
	@Resource
	DataExportUtils dataExportUtils;

	@Resource
	MessageSource messageSource;
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Resource
	AWSS3Logic awss3LogicImpl;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Resource
	GeneralMailLogic generalMailLogic;
	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;

	@Resource
	FileUtils fileUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#getEmployeeList(java.lang.String,
	 * java.lang.String, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long, java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage getEmployeeList(String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long employeeId, Long languageId) throws UnsupportedEncodingException

	{

		AccessControlConditionDTO conditionDTO = new AccessControlConditionDTO();

		int recordSize = 0;
		try {
			searchText = URLDecoder.decode(searchText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		if (searchCondition.equals(PayAsiaConstants.EMPLOYEE_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.FIRST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setFirstName("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.LAST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLastName("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.EMAIL)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmail("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.DATE_OF_BIRTH)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setDob(DateUtils.stringToTimestamp(searchText.trim()));
			}

		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		List<Employee> empList;
		recordSize = (employeeDAO.getCountForCondition(conditionDTO, companyId)).intValue();
		empList = employeeDAO.findByCondition(conditionDTO, pageDTO, sortDTO, companyId);
		List<EmployeeListForm> employeeList = new ArrayList<EmployeeListForm>();
		for (Employee employee : empList) {
			EmployeeListForm empForm = new EmployeeListForm();
			/* ID ENCRYPT */
			empForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			empForm.setEmployeeNumber(URLEncoder.encode(employee.getEmployeeNumber(), "UTF-8"));

			empForm.setFirstName(URLEncoder.encode(employee.getFirstName(), "UTF-8"));

			if (employee.getLastName() != null) {
				empForm.setLastName(URLEncoder.encode(employee.getLastName(), "UTF-8"));

			}
			if (employee.getEmail() != null) {
				empForm.setEmail(employee.getEmail());
			}

			if (employee.isStatus() == true) {
				empForm.setStatusMsg(PayAsiaConstants.EMPLOYEE_ENABLED);

			} else {
				empForm.setStatusMsg(PayAsiaConstants.EMPLOYEE_DISABLED);
			}

			if (employee.getOriginalHireDate() != null) {
				empForm.setOriginalHireDate(DateUtils.timeStampToString(employee.getOriginalHireDate()));
			}
			if (employee.getHireDate() != null) {
				empForm.setHireDate(DateUtils.timeStampToString(employee.getHireDate()));
			}
			if (employee.getResignationDate() != null) {
				empForm.setResignationDate(DateUtils.timeStampToString(employee.getResignationDate()));
			}
			if (employee.getConfirmationDate() != null) {
				empForm.setConfirmationDate(DateUtils.timeStampToString(employee.getConfirmationDate()));
			}

			if (StringUtils.isNotBlank(employee.getMiddleName())) {
				empForm.setMiddleName(employee.getMiddleName());
			}

			if (employee.getEmploymentStatus() != null) {
				empForm.setEmploymentStatus(employee.getEmploymentStatus());
			}

			employeeList.add(empForm);
		}

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			employeeListFormPage.setPage(pageDTO.getPageNumber());
			employeeListFormPage.setTotal(totalPages);

			employeeListFormPage.setRecords(recordSize);
		}
		employeeListFormPage.setEmployeeListFrom(employeeList);

		return employeeListFormPage;

	}

	/**
	 * Gets the employee ids for general.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 * @return the employee ids for general
	 */

	/**
	 * Sets the filter info.
	 * 
	 * @param companyEmployeeShortList
	 *            the company employee short list
	 * @param finalFilterList
	 *            the final filter list
	 * @param tableNames
	 *            the table names
	 * @param codeDescDTOs
	 *            the code desc dt os
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#getEmployeeListPwd(java.lang.Long,
	 * java.lang.String, java.lang.String, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public EmployeeListFormPage getEmployeeListPwd(Long companyId, String fromDate, String toDate, PageRequest pageDTO,
			SortCondition sortDTO)

	{

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		int recordSize = employeeDAO.getCountForCondition(conditionDTO);

		if (StringUtils.isNotBlank(fromDate)) {

			conditionDTO.setFromDate(fromDate);

		}
		if (StringUtils.isNotBlank(toDate)) {

			conditionDTO.setToDate(toDate);

		}

		List<Employee> empList;

		empList = employeeDAO.findByCondition(conditionDTO, pageDTO, sortDTO);

		List<EmployeeListForm> employeeList = new ArrayList<EmployeeListForm>();
		for (Employee employee : empList) {
			EmployeeListForm empForm = new EmployeeListForm();
			empForm.setEmployeeID((int) employee.getEmployeeId());
			empForm.setEmployeeNumber(employee.getEmployeeNumber());
			empForm.setEmployeeName(employee.getFirstName());
			empForm.setStatus(employee.isStatus());
			employeeList.add(empForm);
		}

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			employeeListFormPage.setPage(pageDTO.getPageNumber());
			employeeListFormPage.setTotal(totalPages);

			employeeListFormPage.setRecords(recordSize);
		}
		employeeListFormPage.setEmployeeListFrom(employeeList);

		return employeeListFormPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#filterEmployeeList()
	 */
	@Override
	public void filterEmployeeList() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#addEmployee(com.payasia.common.
	 * form.EmployeeListForm, java.lang.Long)
	 */
	@Override
	public Long addEmployee(EmployeeListForm employeeListForm, Long companyId) {

		Employee emp = new Employee();

		try {
			emp.setEmployeeNumber(URLDecoder.decode(employeeListForm.getEmployeeNumber(), "UTF-8"));
			emp.setFirstName(URLDecoder.decode(employeeListForm.getFirstName(), "UTF-8"));
			if (StringUtils.isNotBlank(employeeListForm.getMiddleName())) {
				emp.setMiddleName(URLDecoder.decode(employeeListForm.getMiddleName(), "UTF-8"));
			}

			if (StringUtils.isNotBlank(employeeListForm.getLastName())) {
				emp.setLastName(URLDecoder.decode(employeeListForm.getLastName(), "UTF-8"));
			}

			if (StringUtils.isNotBlank(employeeListForm.getEmail())) {
				emp.setEmail(URLDecoder.decode(employeeListForm.getEmail(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		Date todayDate = new Date();
		if (StringUtils.isNotBlank(employeeListForm.getResignationDate())) {
			Date resignationDate = DateUtils.stringToDate(employeeListForm.getResignationDate());
			if (resignationDate.before(todayDate) || resignationDate.equals(todayDate)) {
				emp.setStatus(false);
			} else {

				emp.setStatus(true);
			}

		} else {

			emp.setStatus(true);
		}

		if (StringUtils.isNotBlank(employeeListForm.getEmploymentStatus())) {
			emp.setEmploymentStatus(employeeListForm.getEmploymentStatus());
		}

		Company cmp = companyDAO.findById(companyId);
		emp.setCompany(cmp);

		if (StringUtils.isNotBlank(employeeListForm.getHireDate())) {
			emp.setHireDate(DateUtils.stringToTimestamp(employeeListForm.getHireDate()));

		}
		if (StringUtils.isNotBlank(employeeListForm.getConfirmationDate())) {
			emp.setConfirmationDate(DateUtils.stringToTimestamp(employeeListForm.getConfirmationDate()));

		}
		if (StringUtils.isNotBlank(employeeListForm.getOriginalHireDate())) {
			emp.setOriginalHireDate(DateUtils.stringToTimestamp(employeeListForm.getOriginalHireDate()));

		}
		if (StringUtils.isNotBlank(employeeListForm.getResignationDate())) {
			emp.setResignationDate(DateUtils.stringToTimestamp(employeeListForm.getResignationDate()));

		}

		Employee newEmployee = employeeDAO.save(emp);

		EmployeeLoginDetail employeeLoginDetail = new EmployeeLoginDetail();
		String salt = securityLogic.generateSalt();
		String encryptPassWord = securityLogic.encrypt(PasswordUtils.getRandomPassword(), salt);
		try {
			employeeLoginDetail.setLoginName(URLDecoder.decode(employeeListForm.getEmployeeNumber().trim(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		employeeLoginDetail.setPassword(encryptPassWord);
		employeeLoginDetail.setSalt(salt);
		employeeLoginDetail.setEmployee(newEmployee);
		employeeLoginDetailDAO.save(employeeLoginDetail);

		RoleMaster roleMaster = roleMasterDAO.findByRoleName(PayAsiaConstants.EMPLOYEE_DEFAULT_ROLE, companyId);

		if (roleMaster != null) {
			EmployeeRoleMapping empRoleMapping = new EmployeeRoleMapping();
			EmployeeRoleMappingPK employeeRoleMappingPK = new EmployeeRoleMappingPK();
			employeeRoleMappingPK.setCompanyId(cmp.getCompanyId());
			employeeRoleMappingPK.setEmployeeId(newEmployee.getEmployeeId());
			employeeRoleMappingPK.setRoleId(roleMaster.getRoleId());
			empRoleMapping.setId(employeeRoleMappingPK);
			employeeRoleMappingDAO.save(empRoleMapping);
		}

		return newEmployee.getEmployeeId();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#deleteEmployee(java.lang.Long)
	 */
	@Override
	public boolean deleteEmployee(Long employeeId) {
		EmployeeListForm employeeListForm = employeeDAO.deleteEmployeeProc(employeeId);
		return employeeListForm.isStatus();
	}

	/**
	 * Delete employee records.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 */
	private void deleteEmployeeRecords(Long employeeId, Long companyId) {
		EntityMaster entityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		List<DynamicFormRecord> dynamicFormRecords = dynamicFormRecordDAO.findByEntityKey(employeeId,
				entityMaster.getEntityId(), companyId);

		for (DynamicFormRecord dynamicFormRecord : dynamicFormRecords) {
			List<Long> tableRecordIds = getTableRecordIds(dynamicFormRecord);

			for (Long tableId : tableRecordIds) {
				dynamicFormTableRecordDAO.deleteByCondition(tableId);
			}

			dynamicFormRecordDAO.delete(dynamicFormRecord);
		}

	}

	/**
	 * Gets the table record ids.
	 * 
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @return the table record ids
	 */
	private List<Long> getTableRecordIds(DynamicFormRecord dynamicFormRecord) {
		int maxVersion = 0;
		List<Long> tableRecordIds = new ArrayList<>();
		synchronized (this) {
			maxVersion = dynamicFormDAO.getMaxVersionByFormId(dynamicFormRecord.getCompany_ID(),
					dynamicFormRecord.getEntity_ID(), dynamicFormRecord.getForm_ID());
		}

		DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(dynamicFormRecord.getCompany_ID(),
				dynamicFormRecord.getEntity_ID(), maxVersion, dynamicFormRecord.getForm_ID());

		Unmarshaller unmarshaller = null;

		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
			throw new PayAsiaSystemException(jaxbException.getMessage(), jaxbException);
		} catch (SAXException saxException) {
			LOGGER.error(saxException.getMessage(), saxException);
			throw new PayAsiaSystemException(saxException.getMessage(), saxException);
		}
		Tab tab = null;
		final StringReader xmlReader = new StringReader(dynamicForm.getMetaData());
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
			throw new PayAsiaSystemException(jaxbException.getMessage(), jaxbException);
		}

		List<Field> listOfFields = tab.getField();

		for (Field field : listOfFields) {
			if (StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.TABLE_FIELD_TYPE)) {
				String colValue = getColValueFile(PayAsiaStringUtils.getColNumber(field.getName()), dynamicFormRecord);
				if (StringUtils.isNotBlank(colValue)) {
					tableRecordIds.add(Long.parseLong(colValue));
				}

			}
		}
		return tableRecordIds;
	}

	/**
	 * Gets the col value file.
	 * 
	 * @param colNumber
	 *            the col number
	 * @param existingFormRecord
	 *            the existing form record
	 * @return the col value file
	 */
	private String getColValueFile(String colNumber, DynamicFormRecord existingFormRecord) {

		String tableRecordId = null;
		Class<?> dynamicFormRecordClass = existingFormRecord.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormRecordMethod;
		try {

			dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormRecordMethod.invoke(existingFormRecord);
		} catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return tableRecordId;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#deleteRoleAndEmployee(java.lang
	 * .Long)
	 */
	@Override
	public void deleteRoleAndEmployee(Long empID) {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		employeeRoleMappingDAO.deleteByCondition(empID, companyID);
		Employee emp = employeeDAO.findById(empID);
		employeeDAO.delete(emp);
		deleteEmployeeRecords(empID, emp.getCompany().getCompanyId());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#updateEmployee(com.payasia.common
	 * .form.EmployeeListForm, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void updateEmployee(EmployeeListForm employeeListForm, Long empId, Long companyId) {
		Employee emp = employeeDAO.findById(empId);

		Date todayDate = new Date();
		if (StringUtils.isNotBlank(employeeListForm.getResignationDate())) {
			Date resignationDate = DateUtils.stringToDate(employeeListForm.getResignationDate());
			if (resignationDate.before(todayDate) || resignationDate.equals(todayDate)) {
				emp.setStatus(false);
			} else {
				emp.setStatus(true);
			}

		} else {
			emp.setStatus(true);
		}

		try {
			if (StringUtils.isNotBlank(employeeListForm.getEmployeeNumber())) {
				emp.setEmployeeNumber(URLDecoder.decode(employeeListForm.getEmployeeNumber(), "UTF-8"));
			}
			if (StringUtils.isNotBlank(employeeListForm.getFirstName())) {
				emp.setFirstName(URLDecoder.decode(employeeListForm.getFirstName(), "UTF-8"));
			}
			if (employeeListForm.getMiddleName() != null) {
				emp.setMiddleName(URLDecoder.decode(employeeListForm.getMiddleName(), "UTF-8"));
			}
			if (employeeListForm.getLastName() != null) {
				emp.setLastName(URLDecoder.decode(employeeListForm.getLastName(), "UTF-8"));
			}
			if (employeeListForm.getEmail() != null) {
				emp.setEmail(URLDecoder.decode(employeeListForm.getEmail(), "UTF-8"));
			}

		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		emp.setEmploymentStatus(employeeListForm.getEmploymentStatus());

		if (StringUtils.isNotBlank(employeeListForm.getHireDate())) {
			emp.setHireDate(DateUtils.stringToTimestamp(employeeListForm.getHireDate()));

		} else {
			emp.setHireDate(null);
		}
		if (StringUtils.isNotBlank(employeeListForm.getConfirmationDate())) {
			emp.setConfirmationDate(DateUtils.stringToTimestamp(employeeListForm.getConfirmationDate()));

		} else {
			emp.setConfirmationDate(null);
		}
		if (StringUtils.isNotBlank(employeeListForm.getOriginalHireDate())) {
			emp.setOriginalHireDate(DateUtils.stringToTimestamp(employeeListForm.getOriginalHireDate()));

		} else {
			emp.setOriginalHireDate(null);
		}
		if (StringUtils.isNotBlank(employeeListForm.getResignationDate())) {
			emp.setResignationDate(DateUtils.stringToTimestamp(employeeListForm.getResignationDate()));

		} else {
			emp.setResignationDate(null);
		}

		employeeDAO.update(emp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#getUploadedDoc(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage getUploadedDoc(Long companyId, Long languageId) {

		Long entityId = null;
		Company company = companyDAO.findById(companyId);
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		EmployeeListForm employeeListForm = null;
		List<EmployeeListForm> tabDataList = new ArrayList<EmployeeListForm>();
		List<Long> formIdList = dynamicFormDAO.getDistinctFormId(companyId, entityId);
		for (int count = 0; count < formIdList.size(); count++) {
			employeeListForm = new EmployeeListForm();
			EmployeeDynamicFormDTO employeeDynamicFormDTO = new EmployeeDynamicFormDTO();
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId, entityId, formIdList.get(count));
			employeeListForm.setCmpID(dynamicForm.getId().getCompany_ID());
			employeeDynamicFormDTO.setEntityID(dynamicForm.getId().getEntity_ID());
			employeeDynamicFormDTO.setFormId(dynamicForm.getId().getFormId());
			employeeDynamicFormDTO.setVersion(dynamicForm.getId().getVersion());
			employeeDynamicFormDTO.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
					updateXMLWithDefaultValue(dynamicForm.getMetaData(), company.getDateFormat(), "addmode", companyId),
					languageId, companyId, entityId, dynamicForm.getId().getFormId()));
			employeeDynamicFormDTO.setTabName(multilingualLogic.convertSectionNameToSpecificLanguage(
					dynamicForm.getTabName(), languageId, companyId, entityId, dynamicForm.getId().getFormId()));
			employeeListForm.setEmployeeDynamicFormDTO(employeeDynamicFormDTO);
			tabDataList.add(employeeListForm);

		}

		employeeListFormPage.setEmployeeListFrom(tabDataList);
		// employeeListFormPage.setEmployees(generalLogic.returnEmployeesList(null,
		// companyId));
		return employeeListFormPage;
	}

	@Override
	public EmployeeListFormPage saveAddEmployee(String xml, Long companyID, Long entityID, Long formID, Integer version,
			Long empid) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		Company cmp = companyDAO.findById(companyID);
		String companyDateFormat = cmp.getDateFormat();

		DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
		DynamicFormTableRecord dynamicFormTableRecord;
		DynamicFormTableRecordPK dynamicFormTableRecordPK;

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}

		final StringReader xmlReader = new StringReader(xml);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}
		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();
			String fieldValue = field.getValue();

			String fieldtype = field.getType();
			if ("undefined".equalsIgnoreCase(fieldtype)) {
				continue;
			}

			if (!fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF8");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE) && !"".equalsIgnoreCase(fieldValue)) {
				fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldValue, companyDateFormat);
				int isValid = ValidationUtils.validateDate(fieldValue, PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
				if (isValid == 1) {
					throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
				}
			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)
					|| fieldtype.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				synchronized (this) {

					Long maxTableRecordId = dynamicFormTableRecordDAO.getMaxTableRecordId() + 1;
					int seqNo = 1;
					List<Row> listOfRows = field.getRow();
					for (Row row : listOfRows) {
						dynamicFormTableRecord = new DynamicFormTableRecord();
						dynamicFormTableRecordPK = new DynamicFormTableRecordPK();

						Class<?> dynamicFormTableRecordClass = dynamicFormTableRecord.getClass();

						List<RowValue> listOfRowvalue = row.getRowValue();

						for (RowValue rowValue : listOfRowvalue) {
							String colName = rowValue.getName();
							String colValue;
							try {
								colValue = URLDecoder.decode(rowValue.getValue(), "UTF8");
							} catch (UnsupportedEncodingException unsupportedEncodingException) {
								LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
								throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
										unsupportedEncodingException);
							}
							;
							String colType = rowValue.getType();

							if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
								colValue = DateUtils.appendTodayTime(colValue);

							} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !"".equalsIgnoreCase(colValue)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
							}

							String tableRecordMethodName = PayAsiaConstants.SET_COL
									+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
							Method dynamicFormTableRecordMethod;
							try {
								dynamicFormTableRecordMethod = dynamicFormTableRecordClass
										.getMethod(tableRecordMethodName, String.class);
								if (StringUtils.isNotBlank(colValue)) {
									dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);
								}

							} catch (SecurityException securityException) {
								LOGGER.error(securityException.getMessage(), securityException);
								throw new PayAsiaSystemException(securityException.getMessage(), securityException);
							} catch (NoSuchMethodException e) {
								LOGGER.error(e.getMessage(), e);
							} catch (IllegalArgumentException illegalArgumentException) {
								LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
								throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
										illegalArgumentException);
							} catch (IllegalAccessException illegalAccessException) {
								LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
								throw new PayAsiaSystemException(illegalAccessException.getMessage(),
										illegalAccessException);
							} catch (InvocationTargetException invocationTargetException) {
								LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
								throw new PayAsiaSystemException(invocationTargetException.getMessage(),
										invocationTargetException);
							}

						}

						dynamicFormTableRecordPK.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecordPK.setSequence(seqNo);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);
						seqNo++;
						fieldValue = maxTableRecordId.toString();
					}
				}

			}

			try {
				String methodName = PayAsiaConstants.SET_COL
						+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
				Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
				try {
					if (StringUtils.isNotBlank(fieldValue)) {
						dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
					}

				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
					throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
					throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
					throw new PayAsiaSystemException(invocationTargetException.getMessage(), invocationTargetException);
				}
			} catch (SecurityException securityException) {
				LOGGER.error(securityException.getMessage(), securityException);
				throw new PayAsiaSystemException(securityException.getMessage(), securityException);
			} catch (NoSuchMethodException noSuchMethodException) {

				LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
				throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
			}

		}

		dynamicFormRecord.setCompany_ID(companyID);
		dynamicFormRecord.setEntity_ID(entityID);
		dynamicFormRecord.setForm_ID(formID);
		dynamicFormRecord.setVersion(version);
		dynamicFormRecord.setEntityKey(empid);

		DynamicFormRecord dynamicRecordVO = dynamicFormRecordDAO.saveReturn(dynamicFormRecord);
		employeeListFormPage.setDynamicFormRecordId(dynamicRecordVO.getRecordId());

		return employeeListFormPage;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#saveEmployee(java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Integer,
	 * java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage saveEmployee(String xml, Long companyID, Long entityID, Long formID, Integer version,
			Long empid) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		Company cmp = companyDAO.findById(companyID);
		String companyDateFormat = cmp.getDateFormat();

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
		DynamicFormTableRecord dynamicFormTableRecord;
		DynamicFormTableRecordPK dynamicFormTableRecordPK;

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyID, entityID, formID);
		String dynamicFormXML = dynamicForm.getMetaData();

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}

		final StringReader xmlReader = new StringReader(xml);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}
		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();
			String fieldValue = field.getValue();
			String fieldtype = field.getType();

			Field dynamicFormField = getExistingDynamicFormFieldInfo(dynamicFormXML, field.getDictionaryId());

			if (!fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF8");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE) && !"".equalsIgnoreCase(fieldValue)) {
				fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldValue, companyDateFormat);
				int isValid = ValidationUtils.validateDate(fieldValue, PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
				if (isValid == 1) {
					throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
				}

			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				synchronized (this) {

					Long maxTableRecordId = dynamicFormTableRecordDAO.getMaxTableRecordId() + 1;
					int seqNo = 1;
					List<Row> listOfRows = field.getRow();
					for (Row row : listOfRows) {
						dynamicFormTableRecord = new DynamicFormTableRecord();
						dynamicFormTableRecordPK = new DynamicFormTableRecordPK();

						List<RowValue> listOfRowvalue = row.getRowValue();

						for (RowValue rowValue : listOfRowvalue) {
							String colName = rowValue.getName();
							String colValue;

							DataDictionary fieldDataDictionary = dataDictionaryDAO.findById(rowValue.getDictionaryId());
							if (fieldDataDictionary == null
									&& rowValue.getName().equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								continue;
							}

							Column existingColumn = getExistingDynamicFormColumnInfo(dynamicForm.getMetaData(),
									rowValue.getDictionaryId());

							try {
								colValue = URLDecoder.decode(rowValue.getValue(), "UTF8");
							} catch (UnsupportedEncodingException unsupportedEncodingException) {
								LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
								throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
										unsupportedEncodingException);
							}
							String colType = rowValue.getType();

							if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
								colValue = DateUtils.appendTodayTime(colValue);

							} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !"".equalsIgnoreCase(colValue)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
							}

							if (existingColumn.isApprovalRequired() != null && existingColumn.isApprovalRequired()) {
								HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO.findByConditionTableSeq(
										existingColumn.getDictionaryId(), empid, hrisStatusList, seqNo);
								if (hrisChangeRequestVO == null) {

									String tableRecordMethodName = PayAsiaConstants.SET_COL
											+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
									Method dynamicFormTableRecordMethod;
									try {
										dynamicFormTableRecordMethod = DynamicFormTableRecord.class
												.getMethod(tableRecordMethodName, String.class);

										dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

									} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
											| IllegalAccessException | InvocationTargetException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(e.getMessage(), e);
									}

								}

							} else {

								String tableRecordMethodName = PayAsiaConstants.SET_COL
										+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
								Method dynamicFormTableRecordMethod;
								try {
									dynamicFormTableRecordMethod = DynamicFormTableRecord.class
											.getMethod(tableRecordMethodName, String.class);

									dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

								} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
										| IllegalAccessException | InvocationTargetException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								}

							}

						}

						dynamicFormTableRecordPK.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecordPK.setSequence(seqNo);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);
						seqNo++;
						fieldValue = maxTableRecordId.toString();
					}
				}

			}

			if (dynamicFormField.isApprovalRequired() != null && dynamicFormField.isApprovalRequired()) {
				HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO
						.findByCondition(dynamicFormField.getDictionaryId(), empid, hrisStatusList);
				if (hrisChangeRequestVO == null) {

					try {
						String methodName = PayAsiaConstants.SET_COL
								+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
						Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
						try {
							if (StringUtils.isNotBlank(fieldValue)) {
								dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
							}

						} catch (IllegalArgumentException illegalArgumentException) {
							LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
							throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
									illegalArgumentException);
						} catch (IllegalAccessException illegalAccessException) {
							LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
							throw new PayAsiaSystemException(illegalAccessException.getMessage(),
									illegalAccessException);
						} catch (InvocationTargetException invocationTargetException) {
							LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
							throw new PayAsiaSystemException(invocationTargetException.getMessage(),
									invocationTargetException);
						}
					} catch (SecurityException securityException) {
						LOGGER.error(securityException.getMessage(), securityException);
						throw new PayAsiaSystemException(securityException.getMessage(), securityException);
					} catch (NoSuchMethodException noSuchMethodException) {

						LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
						throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
					}

				}

			} else {

				try {
					String methodName = PayAsiaConstants.SET_COL
							+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
					Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
					try {
						if (StringUtils.isNotBlank(fieldValue)) {
							dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
						}

					} catch (IllegalArgumentException illegalArgumentException) {
						LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
						throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
								illegalArgumentException);
					} catch (IllegalAccessException illegalAccessException) {
						LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
						throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
					} catch (InvocationTargetException invocationTargetException) {
						LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
						throw new PayAsiaSystemException(invocationTargetException.getMessage(),
								invocationTargetException);
					}
				} catch (SecurityException securityException) {
					LOGGER.error(securityException.getMessage(), securityException);
					throw new PayAsiaSystemException(securityException.getMessage(), securityException);
				} catch (NoSuchMethodException noSuchMethodException) {

					LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
					throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
				}

			}

		}

		dynamicFormRecord.setCompany_ID(companyID);
		dynamicFormRecord.setEntity_ID(entityID);
		dynamicFormRecord.setForm_ID(formID);
		dynamicFormRecord.setVersion(version);
		dynamicFormRecord.setEntityKey(empid);

		DynamicFormRecord dynamicRecordVO = dynamicFormRecordDAO.saveReturn(dynamicFormRecord);
		employeeListFormPage.setDynamicFormRecordId(dynamicRecordVO.getRecordId());

		return employeeListFormPage;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#updateEmployeeDynamicFormRecord
	 * (java.lang.String, java.lang.Long, java.lang.Long, java.lang.Long,
	 * java.lang.Integer, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void updateEmployeeDynamicFormRecord(String xml, Long companyID, Long entityID, Long formID, Integer version,
			Long empid, Long tabId) {
		Company cmp = companyDAO.findById(companyID);
		String companyDateFormat = cmp.getDateFormat();

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO.findById(tabId);

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyID, dynamicFormRecord.getEntity_ID(),
				dynamicFormRecord.getForm_ID());
		String dynamicFormXML = dynamicForm.getMetaData();

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}
		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

		final StringReader xmlReader = new StringReader(xml);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();
			String fieldValue = field.getValue();
			String fieldtype = field.getType();

			Field dynamicFormField = getExistingDynamicFormFieldInfo(dynamicFormXML, field.getDictionaryId());

			if (!fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {

				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE) && !"".equalsIgnoreCase(fieldValue)) {
				fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldValue, companyDateFormat);
				int isValid = ValidationUtils.validateDate(fieldValue, PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
				if (isValid == 1) {
					throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
				}
			}

			if (dynamicFormField.isApprovalRequired() != null && dynamicFormField.isApprovalRequired()) {
				HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO
						.findByCondition(dynamicFormField.getDictionaryId(), empid, hrisStatusList);
				if (hrisChangeRequestVO == null) {
					try {
						String methodName = "setCol"
								+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
						Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
						try {

							dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);

						} catch (IllegalArgumentException illegalArgumentException) {
							LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
							throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
									illegalArgumentException);
						} catch (IllegalAccessException illegalAccessException) {
							LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
							throw new PayAsiaSystemException(illegalAccessException.getMessage(),
									illegalAccessException);
						} catch (InvocationTargetException invocationTargetException) {
							LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
							throw new PayAsiaSystemException(invocationTargetException.getMessage(),
									invocationTargetException);
						}
					} catch (SecurityException securityException) {
						LOGGER.error(securityException.getMessage(), securityException);
						throw new PayAsiaSystemException(securityException.getMessage(), securityException);
					} catch (NoSuchMethodException noSuchMethodException) {

						LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
						throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
					}
				}

			} else {

				try {
					String methodName = "setCol"
							+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
					Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
					try {

						dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);

					} catch (IllegalArgumentException illegalArgumentException) {
						LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
						throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
								illegalArgumentException);
					} catch (IllegalAccessException illegalAccessException) {
						LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
						throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
					} catch (InvocationTargetException invocationTargetException) {
						LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
						throw new PayAsiaSystemException(invocationTargetException.getMessage(),
								invocationTargetException);
					}
				} catch (SecurityException securityException) {
					LOGGER.error(securityException.getMessage(), securityException);
					throw new PayAsiaSystemException(securityException.getMessage(), securityException);
				} catch (NoSuchMethodException noSuchMethodException) {

					LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
					throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
				}

			}

		}

		dynamicFormRecordDAO.update(dynamicFormRecord);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#getUpdatedXmls(long,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage getUpdatedXmls(Long loggedInEmployeeId, long empID, Long companyId, Long languageId,
			String mode) {

		Long entityId = null;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		Company cmp = companyDAO.findById(companyId);

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		EmployeeListForm employeeListForm = null;
		List<EmployeeListForm> tabDataList = new ArrayList<EmployeeListForm>();

		List<Long> formIdList = generalLogic.getAdminAuthorizedSectionIdList(loggedInEmployeeId, companyId, entityId);

		int dynamicFormCount = 0;
		int tabNo = 1;
		for (Long dynamicFormId : formIdList) {
			employeeListForm = new EmployeeListForm();
			EmployeeDynamicFormDTO employeeDynamicFormDTO = new EmployeeDynamicFormDTO();
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId, entityId,
					formIdList.get(dynamicFormCount));
			employeeListForm.setCmpID(dynamicForm.getId().getCompany_ID());
			employeeDynamicFormDTO.setEntityID(dynamicForm.getId().getEntity_ID());
			/* ID ENCRYPT */
			employeeDynamicFormDTO.setFormId(FormatPreserveCryptoUtil.encrypt(dynamicForm.getId().getFormId()));
			employeeDynamicFormDTO.setVersion(dynamicForm.getId().getVersion());

			if (tabNo == 1) {
				DynamicFormRecord empRecord = dynamicFormRecordDAO.getEmpRecords(empID,
						dynamicForm.getId().getVersion(), dynamicFormId, entityId, null);
				if (empRecord != null) {
					/* ID ENCRYPT */
					employeeDynamicFormDTO.setTabID(FormatPreserveCryptoUtil.encrypt(empRecord.getRecordId()));
					employeeDynamicFormDTO.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
							updateXML(empRecord, dynamicForm.getMetaData(), cmp.getDateFormat(), "editmode", companyId),
							languageId, companyId, entityId, dynamicForm.getId().getFormId()));
				} else {
					employeeDynamicFormDTO
							.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
									updateXMLWithDefaultValue(dynamicForm.getMetaData(), cmp.getDateFormat(),
											"editmode", companyId),
									languageId, companyId, entityId, dynamicForm.getId().getFormId()));
				}
				employeeDynamicFormDTO.setXmlString(addChangeRequestIdInExistingDynXML(empRecord,
						employeeDynamicFormDTO.getXmlString(), empID, companyId, cmp.getDateFormat(), "editMode"));
				employeeDynamicFormDTO.setXmlString(
						addFormulaValueInExistingDynXML(employeeDynamicFormDTO.getXmlString(), empID, companyId));
				employeeDynamicFormDTO.setXmlString(addReferenceValueInExistingDynXML(
						employeeDynamicFormDTO.getXmlString(), empID, companyId, cmp.getDateFormat()));

			}

			employeeDynamicFormDTO.setTabName(multilingualLogic.convertSectionNameToSpecificLanguage(
					dynamicForm.getTabName(), languageId, companyId, entityId, dynamicForm.getId().getFormId()));
			employeeListForm.setEmployeeDynamicFormDTO(employeeDynamicFormDTO);
			tabDataList.add(employeeListForm);
			dynamicFormCount++;
			tabNo++;
		}

		employeeListFormPage.setEmployeeListFrom(tabDataList);
		Employee emp = employeeDAO.findById(empID);
		try {
			employeeListFormPage.setEmpId(URLEncoder.encode(emp.getEmployeeNumber(), "UTF8"));
			employeeListFormPage.setFirstName(URLEncoder.encode(emp.getFirstName(), "UTF8"));
			employeeListFormPage
					.setMiddleName(emp.getMiddleName() == null ? "" : URLEncoder.encode(emp.getMiddleName(), "UTF8"));

			employeeListFormPage
					.setLastName(emp.getLastName() == null ? "" : URLEncoder.encode(emp.getLastName(), "UTF8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		employeeListFormPage.setEmploymentStatus(emp.getEmploymentStatus() == null ? "" : emp.getEmploymentStatus());
		employeeListFormPage
				.setHireDate(emp.getHireDate() == null ? "" : DateUtils.timeStampToString(emp.getHireDate()));
		employeeListFormPage.setConfirmationDate(
				emp.getConfirmationDate() == null ? "" : DateUtils.timeStampToString(emp.getConfirmationDate()));
		employeeListFormPage.setOriginalHireDate(
				emp.getOriginalHireDate() == null ? "" : DateUtils.timeStampToString(emp.getOriginalHireDate()));
		employeeListFormPage.setResignationDate(
				emp.getResignationDate() == null ? "" : DateUtils.timeStampToString(emp.getResignationDate()));

		employeeListFormPage.setEmail(emp.getEmail());

		// employeeListFormPage.setEmployees(generalLogic.returnEmployeesList(empID,
		// companyId));
		return employeeListFormPage;

	}

	/**
	 * Update xml.
	 * 
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @param metaData
	 *            the meta data
	 * @param companyDateFormat
	 *            the company date format
	 * @param mode
	 *            the mode
	 * @return the string
	 */
	private String updateXML(DynamicFormRecord dynamicFormRecord, String metaData, String companyDateFormat,
			String mode, Long companyId) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		boolean isEnableEmployeeChangeWorkflow = false;
		if (hrisPreferenceVO == null) {
			isEnableEmployeeChangeWorkflow = false;
		} else if (hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = true;
		} else if (!hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = false;
		}
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}
		Class<?> c = dynamicFormRecord.getClass();

		final StringReader xmlReader = new StringReader(metaData);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XMLStreamWriter streamWriter = null;
		try {
			streamWriter = outputFactory.createXMLStreamWriter(byteArrayOutputStream);
		} catch (XMLStreamException xMLStreamException) {
			LOGGER.error(xMLStreamException.getMessage(), xMLStreamException);
			throw new PayAsiaSystemException(xMLStreamException.getMessage(), xMLStreamException);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();
			String fieldType = field.getType();
			String value;

			try {
				String methodName = PayAsiaConstants.GET_COL
						+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
				Method m = c.getMethod(methodName);
				try {
					value = (String) m.invoke(dynamicFormRecord);

					if (value != null) {

						if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
								&& !"".equalsIgnoreCase(value)) {
							value = DateUtils.convertDateToSpecificFormat(value, companyDateFormat);
						}
						if (fieldType.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)
								&& !"".equalsIgnoreCase(value)) {

							Employee employee = employeeDAO.findById(Long.parseLong(value));
							if ("editmode".equalsIgnoreCase(mode)) {
								value = String.valueOf(employee.getEmployeeId());
							} else {
								if (!isEnableEmployeeChangeWorkflow) {
									value = getEmployeeName(employee);
								} else {
									if (field.isReadOnly()) {
										value = getEmployeeName(employee);
									} else {
										value = String.valueOf(employee.getEmployeeId());
									}
								}
							}

						}

						if (fieldType.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
								&& !"".equalsIgnoreCase(value)
								&& PayAsiaConstants.EMPLOYEE_VIEW_MODE.equalsIgnoreCase(mode)) {
							DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
									.findById(Long.parseLong(value));

							try {
								String codeDescription = dynamicFormFieldRefValue.getDescription() + "["
										+ dynamicFormFieldRefValue.getCode() + "]";
								value = codeDescription;
							} catch (Exception exception) {
								LOGGER.error(exception.getMessage(), exception);
								value = "";
							}
						}

						try {
							value = URLEncoder.encode(value, "UTF8");
						} catch (UnsupportedEncodingException unsupportedEncodingException) {
							LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
							throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
									unsupportedEncodingException);
						}

					}

					field.setValue(value);
				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
					throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
					throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
					throw new PayAsiaSystemException(invocationTargetException.getMessage(), invocationTargetException);
				}
			} catch (SecurityException securityException) {
				LOGGER.error(securityException.getMessage(), securityException);
				throw new PayAsiaSystemException(securityException.getMessage(), securityException);
			} catch (NoSuchMethodException noSuchMethodException) {

				LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
				throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
			}

		}

		try {
			Marshaller m = XMLUtil.getDocumentMarshaller();
			m.marshal(tab, streamWriter);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}

		return byteArrayOutputStream.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#tableRecordList(java.lang.Long,
	 * int, java.lang.String[], java.lang.String[], java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage tableRecordList(Long tid, int columnCount, String[] fieldNames, String[] fieldTypes,
			String[] fieldDictIds, Long companyId, Long loggedInemployeeId, Long empID, String tableType,
			String sortOrder, String sortBy, Long languageId) {

		Company cmp = companyDAO.findById(companyId);
		String companyDateFormat = cmp.getDateFormat();

		if (StringUtils.isNotBlank(sortBy) && !sortBy.equalsIgnoreCase("null")) {
			String sortByNameCount = sortBy.substring(sortBy.lastIndexOf('_') + 1, sortBy.length());
			sortBy = "col" + sortByNameCount;
		} else {
			sortBy = "";
		}

		List<DynamicFormTableRecord> tableRecordList = dynamicFormTableRecordDAO.getTableRecords(tid, sortOrder,
				sortBy);
		List<EmployeeListForm> tableRecords = new ArrayList<EmployeeListForm>();

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();
		HashMap<Long, Tab> dataTabMap = new HashMap<>();
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();

		int count;
		String fieldNameCount;
		String fieldName;
		String fieldType;
		String fieldDictId;
		String dynamicFormTableRecordValue = null;
		EmployeeListForm empListForm = null;
		for (DynamicFormTableRecord dynamicFormTableRecord : tableRecordList) {
			empListForm = new EmployeeListForm();
			EmployeeDynamicFormDTO employeeDynamicFormDTO = new EmployeeDynamicFormDTO();
			for (count = 1; count <= columnCount; count++) {
				fieldName = fieldNames[count - 1];
				fieldType = fieldTypes[count - 1];
				fieldDictId = fieldDictIds[count - 1];
				fieldNameCount = fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());

				Class<?> dynamicTableClass = dynamicFormTableRecord.getClass();
				Class<?> employeeDynamicFormDTOClass = employeeDynamicFormDTO.getClass();
				String dynamicFormTableRecordMethodName = PayAsiaConstants.GET_COL + fieldNameCount;
				String empListFormMethodName = PayAsiaConstants.SET_COL + count;
				try {
					Method dynamicFormTableRecordMethod = dynamicTableClass.getMethod(dynamicFormTableRecordMethodName);
					Method empListFormMethod = employeeDynamicFormDTOClass.getMethod(empListFormMethodName,
							String.class);

					dynamicFormTableRecordValue = (String) dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord);
					if (fieldName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1) && "table".equalsIgnoreCase(tableType)) {
						dynamicFormTableRecordValue = DateUtils.convertDateFormat(dynamicFormTableRecordValue,
								companyDateFormat);

					} else if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
							&& dynamicFormTableRecordValue != null
							&& !"".equalsIgnoreCase(dynamicFormTableRecordValue)) {

						dynamicFormTableRecordValue = DateUtils.convertDateToSpecificFormat(dynamicFormTableRecordValue,
								companyDateFormat);

					} else if (fieldType.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
							&& dynamicFormTableRecordValue != null
							&& !"".equalsIgnoreCase(dynamicFormTableRecordValue)) {

						DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
								.findById(Long.parseLong(dynamicFormTableRecordValue));
						if (dynamicFormFieldRefValue != null) {
							dynamicFormTableRecordValue = dynamicFormFieldRefValue.getCode();
						} else {
							dynamicFormTableRecordValue = "";
						}

					} else if ("file".equalsIgnoreCase(fieldType) && dynamicFormTableRecordValue != null
							&& !"".equalsIgnoreCase(dynamicFormTableRecordValue)) {
						String fileNameStr = getFileNameWOTimeStamp(dynamicFormTableRecordValue);
						StringBuilder payasiaShortListYesNo = new StringBuilder();
						payasiaShortListYesNo
								.append("<a class='alink' style='text-decoration: underline;' href='#' onClick = 'downLoadEmpInfoDoc("
										+ dynamicFormTableRecord.getId().getDynamicFormTableRecordId() + ','
										+ dynamicFormTableRecord.getId().getSequence() + ")'>" + fileNameStr + "</a>");
						dynamicFormTableRecordValue = payasiaShortListYesNo.toString();
					} else if (fieldType.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)
							&& dynamicFormTableRecordValue != null
							&& !"".equalsIgnoreCase(dynamicFormTableRecordValue)) {

						Employee employee = employeeDAO.findById(Long.parseLong(dynamicFormTableRecordValue));
						dynamicFormTableRecordValue = getEmployeeName(employee);
					}

					if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
						DataDictionary dataDictionary = dataDictionaryDAO.findById(Long.parseLong(fieldDictId));
						DynamicForm dynamicForm = null;
						if (formIdMap.containsKey(dataDictionary.getFormID())) {
							dynamicForm = formIdMap.get(dataDictionary.getFormID());
						} else {
							dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
									dataDictionary.getEntityMaster().getEntityId(), dataDictionary.getFormID());
							formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
							dynamicFormMap.put(dynamicForm.getId().getFormId(), dynamicForm);
						}
						if (dynamicForm != null) {
							try {
								dynamicFormTableRecordValue = addTableFormulaValueInExistingDynXML(dynamicFormMap,
										dataTabMap, staticPropMap, dynamicForm.getMetaData(), empID, companyId, true,
										dynamicFormTableRecord.getId().getDynamicFormTableRecordId(),
										dynamicFormTableRecord.getId().getSequence(),
										dataDictionary.getDataDictionaryId());
							} catch (XMLStreamException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(e.getMessage(), e);
							}
						}

					}

					if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
						DataDictionary dataDictionary = dataDictionaryDAO.findById(Long.parseLong(fieldDictId));
						DynamicForm dynamicForm = null;
						if (formIdMap.containsKey(dataDictionary.getFormID())) {
							dynamicForm = formIdMap.get(dataDictionary.getFormID());
						} else {
							dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
									dataDictionary.getEntityMaster().getEntityId(), dataDictionary.getFormID());
							formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
							dynamicFormMap.put(dynamicForm.getId().getFormId(), dynamicForm);
						}
						if (dynamicForm != null) {
							try {
								try {
									dynamicFormTableRecordValue = addTableReferenceValueInExistingDynXML(dynamicFormMap,
											dynamicForm.getMetaData(), empID, companyId, companyDateFormat,
											dynamicFormTableRecord.getId().getDynamicFormTableRecordId(),
											dynamicFormTableRecord.getId().getSequence(),
											dataDictionary.getDataDictionaryId());
								} catch (SAXException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								}
							} catch (XMLStreamException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(e.getMessage(), e);
							}
						}

					}

					if ("table".equalsIgnoreCase(tableType)
							&& !fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)
							&& !fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
						if (StringUtils.isNotBlank(fieldDictId)) {
							HRISChangeRequest hrisChangeRequest = hrisChangeRequestDAO.findByConditionTableSeq(
									Long.parseLong(fieldDictId), empID, hrisStatusList,
									dynamicFormTableRecord.getId().getSequence());
							if (hrisChangeRequest != null) {
								DynamicForm dynamicForm = null;
								if (formIdMap.containsKey(hrisChangeRequest.getDataDictionary().getFormID())) {
									dynamicForm = formIdMap.get(hrisChangeRequest.getDataDictionary().getFormID());
								} else {
									dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
											hrisChangeRequest.getDataDictionary().getEntityMaster().getEntityId(),
											hrisChangeRequest.getDataDictionary().getFormID());
									formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
								}

								EmployeeListFormPage employeeListFormPage = generalLogic
										.getEmployeeHRISChangeRequestData(hrisChangeRequest.getHrisChangeRequestId(),
												companyId, dynamicForm.getMetaData(), languageId);

								StringBuilder tableFieldDynStr = new StringBuilder();
								if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CHECK)) {
									if (employeeListFormPage.getNewValue().equalsIgnoreCase("true")) {
										tableFieldDynStr.append("<div class='tableCellBGColor'><input type='checkbox'"
												+ "checked='checked' value='" + employeeListFormPage.getNewValue()
												+ "' offval='no' disabled='disabled'></div>");
									} else {
										tableFieldDynStr.append("<div class='tableCellBGColor'><input type='checkbox'"
												+ " value='" + employeeListFormPage.getNewValue()
												+ "' offval='no' disabled='disabled'></div>");
									}
								} else {
									tableFieldDynStr.append("<div  class='tableCellBGColor' >"
											+ employeeListFormPage.getNewValue() + "</div>");
								}

								dynamicFormTableRecordValue = tableFieldDynStr.toString();
							}
						}
					}

					if (StringUtils.isNotBlank(dynamicFormTableRecordValue)) {
						empListFormMethod.invoke(employeeDynamicFormDTO, dynamicFormTableRecordValue);
					}
				} catch (SecurityException securityException) {
					LOGGER.error(securityException.getMessage(), securityException);
					throw new PayAsiaSystemException(securityException.getMessage(), securityException);
				} catch (NoSuchMethodException noSuchMethodException) {

					LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
					throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
					throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
					throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
					throw new PayAsiaSystemException(invocationTargetException.getMessage(), invocationTargetException);
				}
			}
			empListForm.setTableRecordId(dynamicFormTableRecord.getId().getSequence());
			empListForm.setEmployeeDynamicFormDTO(employeeDynamicFormDTO);
			tableRecords.add(empListForm);
		}

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		employeeListFormPage.setTableDataList(tableRecords);

		return employeeListFormPage;
	}

	private String getFileNameWOTimeStamp(String fileName) {
		String ext = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		fileName = fileName.substring(0, fileName.lastIndexOf('_'));
		return fileName + ext;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#checkEmployeeNumberEmail(java.lang
	 * .String, java.lang.Long, java.lang.String)
	 */
	@Override
	public EmployeeListFormPage checkEmployeeNumberEmail(String employeeNumber, Long companyId, String emailId) {

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			employeeNumber = URLDecoder.decode(employeeNumber, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		Employee empNumber = employeeDAO.findEmployee(employeeNumber, companyId, null);

		if (empNumber != null) {
			employeeListFormPage.setEmployeeNumberStatus(PayAsiaConstants.AVAILABLE);
			employeeListFormPage.setUniqueEmployeeId(empNumber.getEmployeeId());
			employeeListFormPage.setMessage("payasia.employee.id.already.exists");

		} else {
			employeeListFormPage.setEmployeeNumberStatus(PayAsiaConstants.NOTAVAILABLE);

		}

		return employeeListFormPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#editView(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public List<EntityListViewFieldForm> editView(Long companyId, Long viewId) {
		Long entityMasterId = null;
		String fieldType = PayAsiaConstants.STATIC_TYPE;

		List<EntityListViewFieldForm> entityListViewFieldList = new ArrayList<EntityListViewFieldForm>();
		EntityListViewFieldForm entityListViewField;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityMasterId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO.findByConditionEntity(entityMasterId, fieldType);
		List<EntityListViewField> viewIds = entityListViewFieldDAO.findByEntityListViewId(viewId, companyId);

		for (DataDictionary dataDictionary : dataDictionaryList) {

			entityListViewField = new EntityListViewFieldForm();
			entityListViewField.setFieldName(dataDictionary.getDataDictName());
			entityListViewField.setDataDictionaryId(dataDictionary.getDataDictionaryId());
			for (EntityListViewField viewID : viewIds) {
				if (viewID.getDataDictionary().getDataDictionaryId() == dataDictionary.getDataDictionaryId()) {
					entityListViewField.setStatus(PayAsiaConstants.CHECKED);
					entityListViewField.setViewName(viewID.getEntityListView().getViewName());
					entityListViewField.setRecords(viewID.getEntityListView().getRecordsPerPage());
					entityListViewField.setSequence(viewID.getSequence());
				}

			}
			if (entityListViewField.getStatus() != PayAsiaConstants.CHECKED) {
				entityListViewField.setStatus(PayAsiaConstants.UNCHECKED);
			}
			entityListViewFieldList.add(entityListViewField);

		}

		return entityListViewFieldList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#listEditView(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public List<EntityListViewFieldForm> listEditView(Long companyId, Long viewId) {
		Locale locale = UserContext.getLocale();
		Long entityMasterId = null;
		String fieldType = PayAsiaConstants.STATIC_TYPE;
		boolean status = false;

		List<EntityListViewFieldForm> entityListViewFieldList = new ArrayList<EntityListViewFieldForm>();
		EntityListViewFieldForm entityListViewField;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityMasterId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO.findByConditionEntity(entityMasterId, fieldType);
		List<EntityListViewField> viewIds = entityListViewFieldDAO.findByEntityListViewId(viewId, companyId);

		for (EntityListViewField viewID : viewIds) {

			entityListViewField = new EntityListViewFieldForm();

			if (StringUtils.isNotBlank(viewID.getDataDictionary().getDescription())) {
				String staticLabelMsg = messageSource.getMessage(viewID.getDataDictionary().getDescription(),
						new Object[] {}, locale);
				if (StringUtils.isNotBlank(staticLabelMsg)) {
					entityListViewField.setFieldName(staticLabelMsg);
				} else {
					entityListViewField.setFieldName(viewID.getDataDictionary().getDataDictName());
				}
			} else {
				entityListViewField.setFieldName(viewID.getDataDictionary().getDataDictName());
			}
			entityListViewField.setDataDictionaryId(viewID.getDataDictionary().getDataDictionaryId());
			entityListViewFieldList.add(entityListViewField);

		}

		for (DataDictionary dataDictionary : dataDictionaryList) {

			for (EntityListViewField viewID : viewIds) {

				if (viewID.getDataDictionary().getDataDictionaryId() == dataDictionary.getDataDictionaryId()) {
					status = true;

				}
			}

			if (status == false) {
				entityListViewField = new EntityListViewFieldForm();
				if (StringUtils.isNotBlank(dataDictionary.getDescription())) {
					String staticLabelMsg = messageSource.getMessage(dataDictionary.getDescription(), new Object[] {},
							locale);
					if (StringUtils.isNotBlank(staticLabelMsg)) {
						entityListViewField.setFieldName(staticLabelMsg);
					} else {
						entityListViewField.setFieldName(dataDictionary.getDataDictName());
					}
				} else {
					entityListViewField.setFieldName(dataDictionary.getDataDictName());
				}

				entityListViewField.setDataDictionaryId(dataDictionary.getDataDictionaryId());
				entityListViewFieldList.add(entityListViewField);
			}
			status = false;
		}

		return entityListViewFieldList;
	}

	/**
	 * Gets the entity master id.
	 * 
	 * @param entityName
	 *            the entity name
	 * @param entityMasterList
	 *            the entity master list
	 * @return the entity master id
	 */
	public Long getEntityMasterId(String entityName, List<EntityMaster> entityMasterList) {
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityName.equalsIgnoreCase(entityMaster.getEntityName())) {
				return entityMaster.getEntityId();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#saveCustomView(java.lang.Long,
	 * java.lang.String, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void saveCustomView(Long companyId, String viewName, int recordsPerPage, String[] dataDictionaryIdArr,
			String[] rowIndexs) {
		Long entityMasterId = 1l;

		EntityListView entityListView = new EntityListView();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().toUpperCase().equals(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityMasterId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}
		EntityMaster entityMaster = entityMasterDAO.findById(entityMasterId);
		entityListView.setEntityMaster(entityMaster);
		Company company = companyDAO.findById(companyId);
		entityListView.setCompany(company);
		entityListView.setViewName(viewName);
		entityListView.setRecordsPerPage(recordsPerPage);

		EntityListView persistObj = entityListViewDAO.save(entityListView);

		EntityListViewField entityListViewField = new EntityListViewField();
		entityListViewField.setEntityListView(persistObj);
		DataDictionary dataDictionary = null;
		for (int count = 0; count < dataDictionaryIdArr.length; count++) {
			if (!"".equalsIgnoreCase(dataDictionaryIdArr[count])) {
				dataDictionary = dataDictionaryDAO.findById(Long.parseLong(dataDictionaryIdArr[count]));
				entityListViewField.setDataDictionary(dataDictionary);
				entityListViewField.setSequence(Integer.parseInt(rowIndexs[count]));
				entityListViewFieldDAO.save(entityListViewField);
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#updateCustomView(java.lang.Long,
	 * java.lang.String, int, java.lang.String[], java.lang.String[],
	 * java.lang.Long)
	 */
	@Override
	public void updateCustomView(Long companyId, String viewName, int recordsPerPage, String[] dataDictionaryIdArr,
			String[] rowIndexs, Long viewId) {

		EntityListView entityListView = entityListViewDAO.findById(viewId);

		entityListView.setViewName(viewName);
		entityListView.setRecordsPerPage(recordsPerPage);

		entityListViewDAO.update(entityListView);

		entityListViewFieldDAO.deleteByCondition(viewId);

		EntityListViewField entityListViewField = new EntityListViewField();
		entityListViewField.setEntityListView(entityListView);

		for (int count = 0; count < dataDictionaryIdArr.length; count++) {
			if (!"".equalsIgnoreCase(dataDictionaryIdArr[count])) {
				DataDictionary dataDictionary = dataDictionaryDAO.findById(Long.parseLong(dataDictionaryIdArr[count]));
				entityListViewField.setDataDictionary(dataDictionary);
				entityListViewField.setSequence(Integer.parseInt(rowIndexs[count]));
				entityListViewFieldDAO.save(entityListViewField);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#getViewName(java.lang.Long)
	 */
	@Override
	public List<EntityListViewForm> getViewName(Long companyId) {
		Long entityMasterId = 1l;

		List<EntityListViewForm> EntityListViewFormList = new ArrayList<EntityListViewForm>();

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().toUpperCase().equals(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityMasterId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}
		List<EntityListView> entityViewList = entityListViewDAO.findByConditionEntityAndCompanyId(companyId,
				entityMasterId);

		for (EntityListView entityListView : entityViewList) {
			EntityListViewForm entityListViewForm = new EntityListViewForm();
			entityListViewForm.setViewNameId(entityListView.getEntityListViewId());
			try {
				entityListViewForm.setViewName(URLEncoder.encode(entityListView.getViewName(), "UTF-8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
				throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			EntityListViewFormList.add(entityListViewForm);
		}

		return EntityListViewFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#deleteView(java.lang.Long)
	 */
	@Override
	public void deleteView(Long viewId) {

		EntityListView entityListView = entityListViewDAO.findById(viewId);
		entityListViewDAO.delete(entityListView);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#getCustomColumnName(java.lang.Long)
	 */
	@Override
	public List<EntityListViewFieldForm> getCustomColumnName(Long viewID) {

		List<EntityListViewFieldForm> entityListViewFieldFormList = new ArrayList<EntityListViewFieldForm>();

		List<EntityListViewField> entityListViewFieldList = entityListViewFieldDAO.findByEntityListViewId(viewID);
		for (EntityListViewField entityListViewField : entityListViewFieldList) {
			EntityListViewFieldForm entityListViewFieldForm = new EntityListViewFieldForm();
			DataDictionary dataDictionary = entityListViewField.getDataDictionary();
			entityListViewFieldForm.setFieldName(dataDictionary.getColumnName());
			entityListViewFieldForm.setFieldLabel(dataDictionary.getDataDictName().replace("_", " "));
			entityListViewFieldForm.setRecords(entityListViewField.getEntityListView().getRecordsPerPage());
			entityListViewFieldFormList.add(entityListViewFieldForm);
		}

		return entityListViewFieldFormList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#updateEmployeeImage(com.payasia
	 * .common.form.EmployeeListForm, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void updateEmployeeImage(EmployeeListForm employeeListForm, Long companyId, Long empID) {

		EmployeePhoto employeePhoto = new EmployeePhoto();
		employeePhoto.setEmployee_ID(empID);
		employeePhoto.setUploadedDate(DateUtils.getCurrentTimestamp());

		String fileName = employeeListForm.getEmployeeImage().getOriginalFilename();
		String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		employeePhoto.setFileType(fileExtension);
		employeePhoto.setFileName(fileName);
		EmployeePhoto employeePhotoVO = employeePhotoDAO.findByEmployeeId(empID);

		if (employeePhotoVO == null) {
			EmployeePhoto saveReturn = employeePhotoDAO.saveReturn(employeePhoto);

			// save Employee Photo to file directory
			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyId, PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME, null, null, null, null,
					PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			/*
			 * String filePath = downloadPath + "/company/" + companyId + "/" +
			 * PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME + "/";
			 */

			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				awss3LogicImpl.uploadCommonMultipartFile(employeeListForm.getEmployeeImage(),
						filePath + saveReturn.getEmployee_ID());
			} else {
				FileUtils.uploadFileWOExt(employeeListForm.getEmployeeImage(), filePath, fileNameSeperator,
						saveReturn.getEmployee_ID());
			}

		} else {
			boolean success = true;
			try {

				FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
						companyId, PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME, null, null,
						String.valueOf(employeePhotoVO.getEmployee_ID()), null,
						PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);
				String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
				/*
				 * String filePath = "/company/" + companyId + "/" +
				 * PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME + "/" +
				 * employeePhotoVO.getEmployee_ID();
				 */
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
					List<String> fileList = new ArrayList<String>();
					fileList.add(filePath);
					awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
				} else {
					FileUtils.deletefile(filePath);
				}
				FileUtils.deletefile(filePath);
			} catch (Exception exception) {
				success = false;
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(), exception);
			}
			if (success) {
				// save Employee Photo to file directory
				FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
						companyId, PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME, null, null,
						String.valueOf(employeePhotoVO.getEmployee_ID()), null,
						PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
				String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
				/*
				 * String filePath = downloadPath + "/company/" + companyId +
				 * "/" + PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME + "/";
				 */

				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
					awss3LogicImpl.uploadCommonMultipartFile(employeeListForm.getEmployeeImage(),
							filePath + employeePhotoVO.getEmployee_ID());
				} else {
					FileUtils.uploadFileWOExt(employeeListForm.getEmployeeImage(), filePath, fileNameSeperator,
							employeePhotoVO.getEmployee_ID());
				}
				employeePhotoDAO.update(employeePhoto);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#getEmployeeImage(long,
	 * java.lang.String, int, int)
	 */
	@Override
	public byte[] getEmployeeImage(long empID, String imagePath, int imageWidth, int imageHeight) throws IOException {

		Employee emp = employeeDAO.findById(empID);
		byte[] originalByteFile = null;

		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				emp.getCompany().getCompanyId(), PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME, null, null,
				String.valueOf(emp.getEmployeeId()), null, PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		/*
		 * String filePath = "company/" + emp.getCompany().getCompanyId() + "/"
		 * + PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME + "/" +
		 * emp.getEmployeeId();
		 */
		File file = new File(filePath);
		String defaultUserImage = "company/defaultUserImage/default-user.png";
		
		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			try {
				originalByteFile = org.apache.commons.io.IOUtils
						.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
		
			  if (originalByteFile == null || originalByteFile.length == 0) {
					originalByteFile = org.apache.commons.io.IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(defaultUserImage));
					return originalByteFile;
				}
				
			} catch (Exception e) {
				originalByteFile = org.apache.commons.io.IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(defaultUserImage));
				return originalByteFile;
			}
		

		} else {
			if (file.exists()) {
				originalByteFile = Files.readAllBytes(file.toPath());
			} else {
				originalByteFile = org.apache.commons.io.IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(defaultUserImage));
				return originalByteFile;
			}
		}
		byte[] resizedImageInByte = null;
		InputStream inputStream = new ByteArrayInputStream(originalByteFile);

		try {
			BufferedImage originalBufferedImage = ImageIO.read(inputStream);

			BufferedImage convertedBufferdImage = ImageUtils.resize(originalBufferedImage, imageWidth, imageHeight,
					true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ImageIO.write(convertedBufferdImage, "png", baos);
			baos.flush();
			resizedImageInByte = baos.toByteArray();
			baos.close();
		} catch (IOException iOException) {
			LOGGER.error(iOException.getMessage(), iOException);
			throw new PayAsiaSystemException(iOException.getMessage(), iOException);
		}

		return resizedImageInByte;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#getEmployeeName(java.lang.Long)
	 */
	@Override
	public String getEmployeeName(Long employeeId) {

		Employee employeeVO = employeeDAO.findById(employeeId);
		if (employeeVO != null) {
			String employeeName = employeeVO.getFirstName();
			if (StringUtils.isNotBlank(employeeVO.getLastName())) {
				employeeName += " " + employeeVO.getLastName();
			}

			return employeeName;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#getEmployeeName(java.lang.Long)
	 */
	@Override
	public String getEmployeeNameWithNumber(Long employeeId) {

		Employee employeeVO = employeeDAO.findById(employeeId);
		if (employeeVO != null) {
			String employeeName = employeeVO.getFirstName();
			if (StringUtils.isNotBlank(employeeVO.getLastName())) {
				employeeName += " " + employeeVO.getLastName();
			}

			return employeeName + "[" + employeeVO.getEmployeeNumber() + "]";
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#getEmployeeImageSize()
	 */
	@Override
	public int getEmployeeImageSize() {

		AppConfigMaster appConfigMaster = appConfigMasterDAO.findByName(PayAsiaConstants.Employee_Image_Size);
		int fileSize = Integer.parseInt(appConfigMaster.getParamValue()) * 1024;
		return fileSize;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#getEmpPasswordAndSendMail(long,
	 * java.lang.String)
	 */
	@Override
	public String getEmpPasswordAndSendMail(long employeeId, String password) {
		Employee employeeVO = employeeDAO.findById(employeeId);

		if (employeeVO == null) {
			return "employee.profile.is.not.defined";
		} else {
			if (!StringUtils.isNotBlank(employeeVO.getEmail())) {
				return "email.id.not.found";
			}
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(employeeVO.getCompany().getCompanyId());
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		HRISPreference hrisPreference = hrisPreferenceDAO.findByCompanyId(employeeVO.getCompany().getCompanyId());
		boolean isHideGetPassword = false;
		if (hrisPreference == null) {
			isHideGetPassword = false;
		} else {
			if (hrisPreference.isHideGetPassword()) {
				isHideGetPassword = true;
			} else {
				isHideGetPassword = false;
			}
		}

		if (!isHideGetPassword) {
			String salt = securityLogic.generateSalt();
			if (password.equals(PayAsiaConstants.OTHER_COMPANY_NAME)) {
				password = PasswordUtils.getRandomPassword();
			}
			EmployeeLoginDetail employeeLoginDetail = employeeVO.getEmployeeLoginDetail();
			String encryptPassWord = securityLogic.encrypt(password, salt);
			employeeLoginDetail.setPassword(encryptPassWord);
			employeeLoginDetail.setSalt(salt);
			employeeLoginDetail.setInvalidLoginAttempt(null);
			employeeLoginDetail.setPasswordReset(true);
			employeeLoginDetailDAO.update(employeeLoginDetail);

			File emailTemplate = null;
			FileOutputStream fos = null;
			File emailSubjectTemplate = null;
			FileOutputStream fosSubject = null;

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

			List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
					.findAll();

			Long subCategoryId = getSubCategoryId(PayAsiaConstants.PAYASIA_SUB_CATEGORY_PASSWORD,
					EmailTemplateSubCategoryMasterList);

			EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(
					PayAsiaConstants.PAYASIA_GET_PASSWORD_NAME, subCategoryId, employeeVO.getCompany().getCompanyId());
			if (emailTemplateVO == null) {
				return "get.password.template.is.not.defined";
			}

			String mailBody = emailTemplateVO.getBody();
			String mailSubject = emailTemplateVO.getSubject();
			Map<String, Object> modelMap = new HashMap<String, Object>();

			modelMap.put("firstName", employeeVO.getFirstName());
			modelMap.put("First_Name", employeeVO.getFirstName());
			modelMap.put("employeeId", employeeVO.getEmployeeNumber());
			modelMap.put("Employee_Number", employeeVO.getEmployeeNumber());
			modelMap.put("userName", employeeVO.getEmployeeLoginDetail().getLoginName());
			modelMap.put("Username", employeeVO.getEmployeeLoginDetail().getLoginName());
			modelMap.put("companyName", employeeVO.getCompany().getCompanyName());
			modelMap.put("Company_Name", employeeVO.getCompany().getCompanyName());

			if (StringUtils.isNotBlank(employeeVO.getEmployeeLoginDetail().getPassword())) {

				modelMap.put("password", password);
				modelMap.put("Password", password);
			}
			if (!StringUtils.isNotBlank(employeeVO.getEmployeeLoginDetail().getPassword())) {
				modelMap.put("password", "");
				modelMap.put("Password", "");
			}
			if (StringUtils.isNotBlank(employeeVO.getMiddleName())) {
				modelMap.put("middleName", employeeVO.getMiddleName());
				modelMap.put("Middle_Name", employeeVO.getMiddleName());
			}
			if (!StringUtils.isNotBlank(employeeVO.getMiddleName())) {
				modelMap.put("middleName", "");
				modelMap.put("Middle_Name", "");
			}
			if (StringUtils.isNotBlank(employeeVO.getLastName())) {
				modelMap.put("lastName", employeeVO.getLastName());
				modelMap.put("Last_Name", employeeVO.getLastName());
			}
			if (!StringUtils.isNotBlank(employeeVO.getLastName())) {
				modelMap.put("lastName", "");
				modelMap.put("Last_Name", "");
			}
			if (StringUtils.isNotBlank(employeeVO.getEmail())) {
				modelMap.put("email", employeeVO.getEmail());
				modelMap.put("Email", employeeVO.getEmail());
			}
			if (!StringUtils.isNotBlank(employeeVO.getEmail())) {
				modelMap.put("email", "");
				modelMap.put("Email", "");
			}
			modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME, generalMailLogic.getEmployeeName(employeeVO));

			if (emailPreferenceMasterVO.getCompanyUrl() != null) {
				if (StringUtils.isNotBlank(emailPreferenceMasterVO.getCompanyUrl())) {
					String link = "<a href='" + emailPreferenceMasterVO.getCompanyUrl() + "'>"
							+ emailPreferenceMasterVO.getCompanyUrl() + "</a>";
					modelMap.put(PayAsiaConstants.URL, link);
				} else {
					modelMap.put(PayAsiaConstants.URL, "");
				}
			} else {
				modelMap.put(PayAsiaConstants.URL, "");
			}

			try {
				byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
				byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
				String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
				emailTemplate = new File(PAYASIA_TEMP_PATH + "//" + "getPasswordTemplate" + uniqueId + ".vm");
				emailTemplate.deleteOnExit();
				emailSubjectTemplate = new File(
						PAYASIA_TEMP_PATH + "//" + "getPasswordSubjectTemplate" + "_" + uniqueId + ".vm");
				emailSubjectTemplate.deleteOnExit();

				try {
					fos = new FileOutputStream(emailTemplate);
					fos.write(mailBodyBytes);
					fos.flush();
					fos.close();
					fosSubject = new FileOutputStream(emailSubjectTemplate);
					fosSubject.write(mailSubjectBytes);
					fosSubject.flush();
					fosSubject.close();
				} catch (FileNotFoundException fileNotFoundException) {
					LOGGER.error(fileNotFoundException.getMessage(), fileNotFoundException);
					throw new PayAsiaSystemException(fileNotFoundException.getMessage(), fileNotFoundException);
				} catch (IOException iOException) {
					LOGGER.error(iOException.getMessage(), iOException);
					throw new PayAsiaSystemException(iOException.getMessage(), iOException);
				}

				if (StringUtils.isNotBlank(employeeVO.getEmail())) {
					payAsiaEmailTO.addMailTo(employeeVO.getEmail());
				}
				if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
					payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
				}
				if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
					payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
				}
				if (StringUtils.isNotBlank(emailPreferenceMasterVO.getContactEmail())) {
					payAsiaEmailTO.setMailFrom(emailPreferenceMasterVO.getContactEmail());
				}

				payAsiaMailUtils.sendEmail(modelMap,
						emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
						emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true,
						payAsiaEmailTO);
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(), exception);

			} finally {
				if (emailTemplate != null) {
					emailTemplate.delete();

				}

			}
			return "password.is.sent.to.this.employee";
		} else {
			return PayAsiaConstants.PAYASIA_ERROR;
		}

	}

	/**
	 * Gets the sub category id.
	 * 
	 * @param subCategoryName
	 *            the sub category name
	 * @param EmailTemplateSubCategoryMasterList
	 *            the email template sub category master list
	 * @return the sub category id
	 */
	public Long getSubCategoryId(String subCategoryName,
			List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList) {
		for (EmailTemplateSubCategoryMaster emailTemplateSubCategoryMaster : EmailTemplateSubCategoryMasterList) {
			if ((subCategoryName.toUpperCase())
					.equals(emailTemplateSubCategoryMaster.getSubCategoryName().toUpperCase())) {
				return emailTemplateSubCategoryMaster.getEmailTemplateSubCategoryId();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#getPasswordPolicy(java.lang.Long)
	 */
	@Override
	public PasswordPolicyPreferenceForm getPasswordPolicy(Long companyId) {
		PasswordPolicyPreferenceForm passwordPolicyPreferenceForm = new PasswordPolicyPreferenceForm();

		PasswordPolicyConfigMaster passwordPolicyConfigMaster = passwordPolicyConfigMasterDAO
				.findByConditionCompany(companyId);
		if (passwordPolicyConfigMaster != null) {
			passwordPolicyPreferenceForm.setMinPasswordLength(passwordPolicyConfigMaster.getMinPwdLength());
			passwordPolicyPreferenceForm.setSpecialCharacters(passwordPolicyConfigMaster.isIncludeSpecialCharacter());

			return passwordPolicyPreferenceForm;
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#checkView(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage checkView(String viewName, Long companyId) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		Long entityMasterID = null;
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityMasterID = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		EntityListView entityListView = entityListViewDAO.findByEntityIdCompanyIdAndViewName(companyId, entityMasterID,
				viewName);

		if (entityListView != null) {
			employeeListFormPage.setStatus(PayAsiaConstants.AVAILABLE);
		} else {
			employeeListFormPage.setStatus(PayAsiaConstants.NOTAVAILABLE);
		}

		return employeeListFormPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#checkViewUpdate(java.lang.String,
	 * java.lang.Long, java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage checkViewUpdate(String viewName, Long companyId, Long viewId) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		Long entityMasterID = null;
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityMasterID = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		EntityListView entityListView = entityListViewDAO.findByEntityIdCompanyIdAndViewNameViewId(companyId,
				entityMasterID, viewName, viewId);

		if (entityListView != null) {
			employeeListFormPage.setStatus(PayAsiaConstants.AVAILABLE);
		} else {
			employeeListFormPage.setStatus(PayAsiaConstants.NOTAVAILABLE);
		}

		return employeeListFormPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#checkEmployeeEmail(java.lang.String
	 * , java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage checkEmployeeEmail(String email, Long employeeID) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		Employee empEmail = employeeDAO.findByEmailId(employeeID, email);

		if (empEmail != null) {
			employeeListFormPage.setEmployeeNumberStatus(PayAsiaConstants.AVAILABLE);
			employeeListFormPage.setMessage("payasia.employee.email.already.exists");

		} else {
			employeeListFormPage.setEmployeeNumberStatus(PayAsiaConstants.NOTAVAILABLE);

		}

		return employeeListFormPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#getResetPassword(java.lang.Long)
	 */
	@Override
	public String getResetPassword(Long companyId, Long loggedInEmployeeId) {
		String resetPwd = "";
		Employee loggedInEmployee = employeeDAO.findById(loggedInEmployeeId);
		if (loggedInEmployee.getCompany().getCompanyName().toUpperCase()
				.equals(PayAsiaConstants.PAYASIA_COMPANY_NAME)) {
			resetPwd = PasswordUtils.getRandomPassword();
		} else {
			resetPwd = PayAsiaConstants.OTHER_COMPANY_NAME;
		}

		return resetPwd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#getEmployeeLoginHistory(java.lang
	 * .Long)
	 */
	@Override
	public String getEmployeeLoginHistory(Long companyId) {

		List<Object[]> tupleList = employeeLoginHistoryDAO.findEmployeeLoginHistory(companyId);
		String line = "[";
		String jan = "";
		String feb = "";
		String mar = "";
		String apr = "";
		String may = "";
		String june = "";
		String july = "";
		String aug = "";
		String sept = "";
		String oct = "";
		String nov = "";
		String dec = "";

		for (Object[] tuple : tupleList) {

			switch ((Integer) tuple[0]) {

			case 1:
				jan += "[\'Jan\'," + tuple[1] + "],";
				break;
			case 2:
				feb += "[\'Feb\'," + tuple[1] + "],";
				break;
			case 3:
				mar += "[\'Mar\'," + tuple[1] + "],";
				break;
			case 4:
				apr += "[\'Apr\'," + tuple[1] + "],";
				break;
			case 5:
				may += "[\'May\'," + tuple[1] + "],";
				break;
			case 6:
				june += "[\'Jun\'," + tuple[1] + "],";
				break;
			case 7:
				july += "[\'Jul\'," + tuple[1] + "],";
				break;
			case 8:
				aug += "[\'Aug\'," + tuple[1] + "],";
				break;
			case 9:
				sept += "[\'Sep\'," + tuple[1] + "],";
				break;
			case 10:
				oct += "[\'Oct\'," + tuple[1] + "],";
				break;
			case 11:
				nov += "[\'Nov\'," + tuple[1] + "],";
				break;
			case 12:
				dec += "[\'Dec\'," + tuple[1] + "]";
				break;

			}

		}
		line += jan + feb + mar + apr + may + june + july + aug + sept + oct + nov + dec;
		line += "]";

		return line;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeDetailLogic#getXML(java.lang.Long,
	 * java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage getXML(Long empID, Long companyId, Long languageId, Long formId) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		Long entityId = null;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		Company cmp = companyDAO.findById(companyId);

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId, entityId, formId);

		DynamicFormRecord empRecord = dynamicFormRecordDAO.getEmpRecords(empID, dynamicForm.getId().getVersion(),
				formId, entityId, null);

		if (empRecord != null) {
			/* ID ENCRYPT */
			employeeListFormPage.setTabId(FormatPreserveCryptoUtil.encrypt(empRecord.getRecordId()));

			employeeListFormPage.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
					updateXML(empRecord, dynamicForm.getMetaData(), cmp.getDateFormat(), "editMode", companyId),
					languageId, companyId, entityId, dynamicForm.getId().getFormId()));
		} else {
			employeeListFormPage.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
					updateXMLWithDefaultValue(dynamicForm.getMetaData(), cmp.getDateFormat(), "editMode", companyId),
					languageId, companyId, entityId, dynamicForm.getId().getFormId()));
		}

		employeeListFormPage.setXmlString(addChangeRequestIdInExistingDynXML(empRecord,
				employeeListFormPage.getXmlString(), empID, companyId, cmp.getDateFormat(), "editMode"));

		employeeListFormPage
				.setXmlString(addFormulaValueInExistingDynXML(employeeListFormPage.getXmlString(), empID, companyId));

		employeeListFormPage.setXmlString(addReferenceValueInExistingDynXML(employeeListFormPage.getXmlString(), empID,
				companyId, cmp.getDateFormat()));
		// employeeListFormPage.setEmployees(generalLogic.returnEmployeesList(empID,
		// companyId));

		return employeeListFormPage;
	}

	@Override
	public EmployeeListFormPage fetchEmpsForCalendarTemplate(String searchCondition, String searchText,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId, Long employeeId) {

		AccessControlConditionDTO conditionDTO = new AccessControlConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.EMPLOYEE_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setEmployeeNumber("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.FIRST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setFirstName("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.LAST_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLastName("%" + searchText.trim() + "%");
			}

		}

		List<Employee> empList;
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		empList = employeeDAO.findEmployeesCalendarTemplate(conditionDTO, pageDTO, sortDTO, companyId);
		List<EmployeeListForm> employeeList = new ArrayList<EmployeeListForm>();
		for (Employee employee : empList) {
			EmployeeListForm empForm = new EmployeeListForm();
			empForm.setEmployeeID((int) employee.getEmployeeId());
			empForm.setEmployeeNumber(employee.getEmployeeNumber());

			empForm.setFirstName(employee.getFirstName());

			if (employee.getLastName() != null) {
				empForm.setLastName(employee.getLastName());

			}
			if (employee.getEmail() != null) {
				empForm.setEmail(employee.getEmail());
			}

			if (employee.isStatus() == true) {
				empForm.setStatusMsg(PayAsiaConstants.EMPLOYEE_ENABLED);

			} else {
				empForm.setStatusMsg(PayAsiaConstants.EMPLOYEE_DISABLED);
			}
			employeeList.add(empForm);
		}

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		employeeListFormPage.setEmployeeListFrom(employeeList);

		return employeeListFormPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#saveTableRecord(java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Integer,
	 * java.lang.Long)
	 */
	@Override
	public EmployeeListFormPage saveTableRecord(String tableXML, Long tabId, Long companyId, Long employeeId,
			Long formId, Integer version, Long entityKey) {

		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		boolean isEnableEmployeeChangeWorkflow = false;
		if (hrisPreferenceVO == null) {
			isEnableEmployeeChangeWorkflow = false;
		} else if (hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = true;
		} else if (!hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = false;
		}
		boolean isDependentFieldUpdated = false;

		Integer seqNo;
		Long entityId = null;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();

		Company cmp = companyDAO.findById(companyId);
		String companyDateFormat = cmp.getDateFormat();
		DynamicFormRecord dynamicFormRecord = null;
		String fieldValue = null;

		if (tabId == 0) {
			dynamicFormRecord = new DynamicFormRecord();
			dynamicFormRecord.setForm_ID(formId);
			dynamicFormRecord.setVersion(version);
			dynamicFormRecord.setEntityKey(entityKey);
			dynamicFormRecord.setCompany_ID(companyId);
			dynamicFormRecord.setEntity_ID(entityId);

		} else {
			dynamicFormRecord = dynamicFormRecordDAO.findById(tabId);
		}

		DynamicFormTableRecord dynamicFormTableRecord;
		DynamicFormTableRecordPK dynamicFormTableRecordPK;

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException | SAXException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}
		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

		final StringReader xmlReader = new StringReader(tableXML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();
			fieldValue = field.getValue();
			String fieldtype = field.getType();
			String fieldLabel = field.getLabel();

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				synchronized (this) {

					Long maxTableRecordId;
					if ("".equalsIgnoreCase(fieldValue) || fieldValue.equalsIgnoreCase("0")) {

						maxTableRecordId = dynamicFormTableRecordDAO.getMaxTableRecordId() + 1;
						seqNo = 1;
					} else {
						maxTableRecordId = Long.parseLong(fieldValue);
						Integer maxSeqNo = dynamicFormTableRecordDAO.getMaxSequenceNumber(maxTableRecordId);
						if (maxSeqNo != 0) {
							seqNo = maxSeqNo + 1;
						} else {
							seqNo = 1;
						}
					}

					fieldValue = null;

					List<Row> listOfRows = field.getRow();
					for (Row row : listOfRows) {
						dynamicFormTableRecord = new DynamicFormTableRecord();
						dynamicFormTableRecordPK = new DynamicFormTableRecordPK();

						List<RowValue> listOfRowvalue = row.getRowValue();

						for (RowValue rowValue : listOfRowvalue) {
							if (isEnableEmployeeChangeWorkflow) {
								DataDictionary fieldDataDictionary = dataDictionaryDAO
										.findById(rowValue.getDictionaryId());
								if (fieldDataDictionary == null
										&& rowValue.getName().equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
									continue;
								}
								DynamicForm dynamicForm = null;
								if (fieldDataDictionary != null
										&& formIdMap.containsKey(fieldDataDictionary.getFormID())) {
									dynamicForm = formIdMap.get(fieldDataDictionary.getFormID());
								} else {
									dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
											fieldDataDictionary.getEntityMaster().getEntityId(),
											fieldDataDictionary.getFormID());
									formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
								}

								Column existingColumn = getExistingDynamicFormColumnInfo(dynamicForm.getMetaData(),
										rowValue.getDictionaryId());
								String colName = rowValue.getName();
								String colValue;
								try {
									if (fieldLabel.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
											&& StringUtils.isBlank(rowValue.getValue())
											&& StringUtils.isNotBlank(existingColumn.getDefaultValue())) {
										colValue = URLDecoder.decode(existingColumn.getDefaultValue(), "UTF8");
									} else {
										colValue = URLDecoder.decode(rowValue.getValue(), "UTF8");
									}
								} catch (UnsupportedEncodingException unsupportedEncodingException) {
									LOGGER.error(unsupportedEncodingException.getMessage(),
											unsupportedEncodingException);
									throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
											unsupportedEncodingException);
								}

								String colType = rowValue.getType();

								if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
									colValue = DateUtils.appendTodayTime(colValue);
								} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
										&& !"".equalsIgnoreCase(colValue)) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
								}

								if (existingColumn.isApprovalRequired() != null
										&& existingColumn.isApprovalRequired()) {
									HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO
											.findByConditionTableSeq(existingColumn.getDictionaryId(), employeeId,
													hrisStatusList, seqNo);
									if (hrisChangeRequestVO == null) {
										String tableRecordMethodName = PayAsiaConstants.SET_COL
												+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
										Method dynamicFormTableRecordMethod;
										try {
											dynamicFormTableRecordMethod = DynamicFormTableRecord.class
													.getMethod(tableRecordMethodName, String.class);

											dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

										} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
												| IllegalAccessException | InvocationTargetException e) {
											LOGGER.error(e.getMessage(), e);
											throw new PayAsiaSystemException(e.getMessage(), e);
										}
									}

								} else {

									String tableRecordMethodName = PayAsiaConstants.SET_COL
											+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
									Method dynamicFormTableRecordMethod;
									try {
										dynamicFormTableRecordMethod = DynamicFormTableRecord.class
												.getMethod(tableRecordMethodName, String.class);

										dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

									} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
											| IllegalAccessException | InvocationTargetException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(e.getMessage(), e);
									}

								}
							} else {

								String colName = rowValue.getName();
								String colValue;
								try {
									colValue = URLDecoder.decode(rowValue.getValue(), "UTF8");
								} catch (UnsupportedEncodingException unsupportedEncodingException) {
									LOGGER.error(unsupportedEncodingException.getMessage(),
											unsupportedEncodingException);
									throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
											unsupportedEncodingException);
								}

								String colType = rowValue.getType();

								if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
									colValue = DateUtils.appendTodayTime(colValue);

								} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
										&& !"".equalsIgnoreCase(colValue)) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
								}

								String tableRecordMethodName = PayAsiaConstants.SET_COL
										+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
								Method dynamicFormTableRecordMethod;
								try {
									dynamicFormTableRecordMethod = DynamicFormTableRecord.class
											.getMethod(tableRecordMethodName, String.class);

									dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

								} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
										| IllegalAccessException | InvocationTargetException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								}
							}

						}

						fieldValue = maxTableRecordId.toString();
						dynamicFormTableRecordPK.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecordPK.setSequence(seqNo);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);

					}
				}

			}
			if (fieldLabel.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				isDependentFieldUpdated = true;
			}
			try {
				String methodName = "setCol" + fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
				Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
				try {
					if (StringUtils.isNotBlank(fieldValue)) {
						dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
					}
				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
					throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
					throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
					throw new PayAsiaSystemException(invocationTargetException.getMessage(), invocationTargetException);
				}
			} catch (SecurityException securityException) {
				LOGGER.error(securityException.getMessage(), securityException);
				throw new PayAsiaSystemException(securityException.getMessage(), securityException);
			} catch (NoSuchMethodException noSuchMethodException) {

				LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
				throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
			}

		}
		if (tabId == 0) {
			DynamicFormRecord dynamicRecordVO = dynamicFormRecordDAO.saveReturn(dynamicFormRecord);
			employeeListFormPage.setDynamicFormRecordId(dynamicRecordVO.getRecordId());
			employeeListFormPage.setDynamicFormTableRecordId(Long.parseLong(fieldValue));
			employeeListFormPage.setMode("SAVE");

		} else {
			employeeListFormPage.setDynamicFormTableRecordId(Long.parseLong(fieldValue));
			dynamicFormRecordDAO.update(dynamicFormRecord);
			employeeListFormPage.setMode("UPDATE");
		}

		// Update Leave Entitlement for 1. Child Care Leave
		// type ,
		// 2. Extended Child Care Leave type i.e. when any
		// dependents
		// details will inserted.
		if (isDependentFieldUpdated) {
			if (cmp.getCountryMaster().getCountryName().equalsIgnoreCase("Singapore")) {
				String leaveSchemeTypeIds = generalLogic.getChildCareLeaveTypeInfo(companyId, entityKey);

				leaveSchemeTypeDAO.childCareLeaveEntitlementProc(companyId, dynamicFormRecord.getEntityKey(),
						leaveSchemeTypeIds);
			}
		}

		return employeeListFormPage;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#updateTableRecord(java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public EmployeeListFormPage updateTableRecord(String tableXML, Long tabId, Long companyId, Long employeeId,
			Integer seqNo) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		boolean isEnableEmployeeChangeWorkflow = false;
		if (hrisPreferenceVO == null) {
			isEnableEmployeeChangeWorkflow = false;
		} else if (hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = true;
		} else if (!hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = false;
		}
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		Company cmp = companyDAO.findById(companyId);
		String companyDateFormat = cmp.getDateFormat();

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException | SAXException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}
		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();
		final StringReader xmlReader = new StringReader(tableXML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldValue = field.getValue();
			String fieldtype = field.getType();

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				synchronized (this) {

					Long maxTableRecordId = Long.parseLong(fieldValue);

					List<Row> listOfRows = field.getRow();
					for (Row row : listOfRows) {
						DynamicFormTableRecord dynamicFormTableRecord = dynamicFormTableRecordDAO
								.findByIdAndSeq(maxTableRecordId, seqNo);
						List<RowValue> listOfRowvalue = row.getRowValue();

						for (RowValue rowValue : listOfRowvalue) {
							if (isEnableEmployeeChangeWorkflow) {
								DataDictionary fieldDataDictionary = dataDictionaryDAO
										.findById(rowValue.getDictionaryId());
								if (fieldDataDictionary == null
										&& rowValue.getName().equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
									continue;
								}
								DynamicForm dynamicForm = null;
								if (fieldDataDictionary != null
										&& formIdMap.containsKey(fieldDataDictionary.getFormID())) {
									dynamicForm = formIdMap.get(fieldDataDictionary.getFormID());
								} else {
									dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
											fieldDataDictionary.getEntityMaster().getEntityId(),
											fieldDataDictionary.getFormID());
									formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
								}

								Column existingColumn = getExistingDynamicFormColumnInfo(dynamicForm.getMetaData(),
										rowValue.getDictionaryId());

								String colName = rowValue.getName();
								String colValue;
								try {
									colValue = URLDecoder.decode(rowValue.getValue(), "UTF8");
								} catch (UnsupportedEncodingException unsupportedEncodingException) {
									LOGGER.error(unsupportedEncodingException.getMessage(),
											unsupportedEncodingException);
									throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
											unsupportedEncodingException);
								}

								String colType = rowValue.getType();

								if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
									colValue = DateUtils.appendTodayTime(colValue);

								} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
										&& !"".equalsIgnoreCase(colValue)) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
								}

								if (existingColumn.isApprovalRequired() != null
										&& existingColumn.isApprovalRequired()) {
									HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO.findByCondition(
											existingColumn.getDictionaryId(), employeeId, hrisStatusList);
									if (hrisChangeRequestVO == null) {
										String tableRecordMethodName = PayAsiaConstants.SET_COL
												+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
										Method dynamicFormTableRecordMethod;
										try {
											dynamicFormTableRecordMethod = DynamicFormTableRecord.class
													.getMethod(tableRecordMethodName, String.class);

											dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

										} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
												| IllegalAccessException | InvocationTargetException e) {
											LOGGER.error(e.getMessage(), e);
											throw new PayAsiaSystemException(e.getMessage(), e);
										}
									}

								} else {

									String tableRecordMethodName = PayAsiaConstants.SET_COL
											+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
									Method dynamicFormTableRecordMethod;
									try {
										dynamicFormTableRecordMethod = DynamicFormTableRecord.class
												.getMethod(tableRecordMethodName, String.class);

										dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

									} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
											| IllegalAccessException | InvocationTargetException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(e.getMessage(), e);
									}

								}
							} else {

								String colName = rowValue.getName();
								String colValue;
								try {
									colValue = URLDecoder.decode(rowValue.getValue(), "UTF8");
								} catch (UnsupportedEncodingException unsupportedEncodingException) {
									LOGGER.error(unsupportedEncodingException.getMessage(),
											unsupportedEncodingException);
									throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
											unsupportedEncodingException);
								}

								String colType = rowValue.getType();

								if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
									colValue = DateUtils.appendTodayTime(colValue);
								} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
										&& !"".equalsIgnoreCase(colValue)) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
								}

								String tableRecordMethodName = PayAsiaConstants.SET_COL
										+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
								Method dynamicFormTableRecordMethod;
								try {
									dynamicFormTableRecordMethod = DynamicFormTableRecord.class
											.getMethod(tableRecordMethodName, String.class);

									dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

								} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
										| IllegalAccessException | InvocationTargetException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								}
							}

						}

						fieldValue = maxTableRecordId.toString();
						dynamicFormTableRecordDAO.update(dynamicFormTableRecord);

					}
				}

			}
		}
		return employeeListFormPage;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeDetailLogic#deleteTableRecord(java.lang.Long,
	 * java.lang.Long, java.lang.Integer)
	 */
	@Override
	public EmployeeListFormPage deleteTableRecord(Long tableId, Long companyId, Integer seqNo, String tableType,
			Long entityKey, String[] fieldDictIds, String fieldLabel, Long formId, Boolean fromAdmin) {

		if (!fromAdmin) {
			if (!isUserAuthorized(companyId, entityKey, formId, tableId)) {
				throw new PayAsiaSystemException("Authentication Exception");
			}
		}

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		if (tableType.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_FIELD_TYPE)) {
			try {
				FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
						companyId, PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME, null, null,
						String.valueOf(entityKey), null, PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);
				String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
					List<String> fileList = new ArrayList<String>();
					fileList.add(filePath);
					awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
				} else {
					FileUtils.deletefile(filePath);
				}
				employeeDocumentHistoryDAO.deleteByConditionEmployeeTableRecord(tableId, seqNo);
				dynamicFormTableRecordDAO.deleteByConditionEmployeeTableRecord(tableId, seqNo);
				employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}
		if (tableType.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
			List<String> hrisStatusList = new ArrayList<>();
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

			List<String> fieldDictIdsList = new ArrayList<>();
			for (int count = 0; count < fieldDictIds.length; count++) {
				if (StringUtils.isNotBlank(fieldDictIds[count])) {
					fieldDictIdsList.add(fieldDictIds[count]);
				}
			}

			List<HRISChangeRequest> hrisChangeRequestList = hrisChangeRequestDAO.findByDataDictIdAndSeq(companyId,
					entityKey, seqNo, hrisStatusList, fieldDictIdsList);
			if (hrisChangeRequestList == null) {
				dynamicFormTableRecordDAO.deleteByConditionEmployeeTableRecord(tableId, seqNo);
				employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_SUCCESS);
			} else {
				employeeListFormPage.setStatus(PayAsiaConstants.PAYASIA_DELETE);
			}
		}

		return employeeListFormPage;
	}

	@Override
	public EmployeeListFormPage getViewProfileXMLS(long empID, Long companyId, Long languageId, String mode) {

		Long entityId = null;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		boolean isCompanyGroupIFR = false;
		Company cmp = companyDAO.findById(companyId);
		if (cmp.getCompanyGroup().getGroupId() == PayAsiaConstants.IRF_COMPANY_GROUP_ID) {
			isCompanyGroupIFR = true;
		}

		List<String> tabListForCompanyIRFMY = new ArrayList<>();
		tabListForCompanyIRFMY.add(PayAsiaConstants.IRFMY_VIEW_PROFILE_TAB_BANK_DETAILS);
		tabListForCompanyIRFMY.add(PayAsiaConstants.IRFMY_VIEW_PROFILE_TAB_EDUCATION_QUALIFICATION);
		tabListForCompanyIRFMY.add(PayAsiaConstants.IRFMY_VIEW_PROFILE_TAB_ORGANISATIONAL_DETAILS);
		tabListForCompanyIRFMY.add(PayAsiaConstants.IRFMY_VIEW_PROFILE_TAB_SALARY_DETAILS);
		if (!"irfjp".equalsIgnoreCase(cmp.getCompanyCode())) {
			tabListForCompanyIRFMY.add(PayAsiaConstants.IRFMY_VIEW_PROFILE_TAB_DEPENDENT_DETAILS);
		}

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		EmployeeListForm employeeListForm = null;
		List<EmployeeListForm> tabDataList = new ArrayList<EmployeeListForm>();

		List<Long> formIdList = dynamicFormDAO.getDistinctFormIdForCompanyGroupIRF(companyId,
				PayAsiaConstants.EMPLOYEE_ENTITY_ID, isCompanyGroupIFR, tabListForCompanyIRFMY);
		int dynamicFormCount = 0;
		for (Long dynamicFormId : formIdList) {
			employeeListForm = new EmployeeListForm();
			EmployeeDynamicFormDTO employeeDynamicFormDTO = new EmployeeDynamicFormDTO();

			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId, entityId,
					formIdList.get(dynamicFormCount));
			if (dynamicForm != null) {

				employeeListForm.setCmpID(dynamicForm.getId().getCompany_ID());
				employeeDynamicFormDTO.setEntityID(dynamicForm.getId().getEntity_ID());
				employeeDynamicFormDTO.setFormId(dynamicForm.getId().getFormId());
				employeeDynamicFormDTO.setVersion(dynamicForm.getId().getVersion());

				DynamicFormRecord empRecord = dynamicFormRecordDAO.getEmpRecords(empID,
						dynamicForm.getId().getVersion(), dynamicFormId, entityId, null);

				if (empRecord != null) {
					employeeDynamicFormDTO.setTabID(empRecord.getRecordId());
					employeeDynamicFormDTO.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
							updateXML(empRecord, dynamicForm.getMetaData(), cmp.getDateFormat(), "viewMode", companyId),
							languageId, companyId, entityId, dynamicForm.getId().getFormId()));
				} else {
					employeeDynamicFormDTO
							.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
									updateXMLWithDefaultValue(dynamicForm.getMetaData(), cmp.getDateFormat(),
											"viewMode", companyId),
									languageId, companyId, entityId, dynamicForm.getId().getFormId()));
				}

				employeeDynamicFormDTO.setTabName(dynamicForm.getTabName());
				employeeListForm.setEmployeeDynamicFormDTO(employeeDynamicFormDTO);
				tabDataList.add(employeeListForm);
				dynamicFormCount++;
			}

		}

		employeeListFormPage.setEmployeeListFrom(tabDataList);
		Employee emp = employeeDAO.findById(empID);
		employeeListFormPage.setEmpId(emp.getEmployeeNumber());
		employeeListFormPage.setFirstName(emp.getFirstName());
		employeeListFormPage.setMiddleName(emp.getMiddleName() == null ? "" : emp.getMiddleName());
		employeeListFormPage.setEmploymentStatus(emp.getEmploymentStatus() == null ? "" : emp.getEmploymentStatus());
		employeeListFormPage
				.setHireDate(emp.getHireDate() == null ? "" : DateUtils.timeStampToString(emp.getHireDate()));
		employeeListFormPage.setConfirmationDate(
				emp.getConfirmationDate() == null ? "" : DateUtils.timeStampToString(emp.getConfirmationDate()));
		employeeListFormPage.setOriginalHireDate(
				emp.getOriginalHireDate() == null ? "" : DateUtils.timeStampToString(emp.getOriginalHireDate()));
		employeeListFormPage.setResignationDate(
				emp.getResignationDate() == null ? "" : DateUtils.timeStampToString(emp.getResignationDate()));

		employeeListFormPage.setLastName(emp.getLastName() == null ? "" : emp.getLastName());

		employeeListFormPage.setEmail(emp.getEmail());

		return employeeListFormPage;

	}

	@Override
	public String getEmployeeName(Employee employee) {
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
	public String deleteEmployeeImage(Long companyId, Long empID) {
		EmployeePhoto employeePhoto = employeePhotoDAO.findByEmployeeAndCompanyId(empID, companyId);
		boolean success = true;
		if (employeePhoto == null) {
			success = false;
			return PayAsiaConstants.PAYASIA_ERROR;
		}
		try {
			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyId, PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME, null, null,
					String.valueOf(employeePhoto.getEmployee_ID()), null, PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * String filePath = "/company/" + companyId + "/" +
			 * PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME + "/" +
			 * employeePhoto.getEmployee_ID();
			 */
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				List<String> fileList = new ArrayList<String>();
				fileList.add(filePath);
				awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
			} else {
				FileUtils.deletefile(filePath);
			}
		} catch (Exception exception) {
			success = false;
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}

		if (success == true) {
			// delete attachment
			employeePhotoDAO.delete(employeePhoto);
			return PayAsiaConstants.PAYASIA_SUCCESS;
		}
		return PayAsiaConstants.PAYASIA_ERROR;
	}

	@Override
	public EmployeeForm getUserDetails(String userId, String companyCode, Long employeeActivationCodeId) {

		EmployeeForm employeeForm = new EmployeeForm();
		Company companyVO = companyDAO.findByCompanyCode(companyCode, null);
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyVO.getCompanyId());

		Employee employeeVO = null;
		if (hrisPreferenceVO != null && hrisPreferenceVO.isUseEmailAndEmployeeNumberForLogin()) {
			employeeVO = employeeDAO.getEmpByLoginNameOrEmail(userId, companyCode);
		} else {
			employeeVO = employeeDAO.getEmployeeByLoginName(userId, companyCode);
		}

		employeeForm.setEmployeeId(employeeVO.getEmployeeId());
		employeeForm.setCompanyId(employeeVO.getCompany().getCompanyId());
		employeeForm.setWorkingCompanyTimeZoneOffset(employeeVO.getCompany().getTimeZoneMaster().getGmtOffset());
		employeeForm.setCompanyDateFormat(employeeVO.getCompany().getDateFormat());
		EmployeeActivationCode employeeActivationCode = employeeActivationCodeDAO.findByID(employeeActivationCodeId);
		EmployeeMobileDetails employeeMobileDetails = employeeMobileDetailsDAO
				.findByEmployeeActivationCode(employeeActivationCodeId);
		employeeForm.setUuid(employeeMobileDetails.getDeviceID());

		if (!employeeActivationCode.isActive()) {
			employeeActivationCode.setActive(true);
			employeeActivationCodeDAO.update(employeeActivationCode);

			employeeMobileDetailsDAO.updateByDeviceId(employeeMobileDetails.getDeviceID(),
					employeeActivationCode.getEmployeeActivationCodeId());
		}

		String employeeName = employeeVO.getFirstName();

		if (StringUtils.isNotBlank(employeeVO.getMiddleName())) {
			employeeName = employeeName + " " + employeeVO.getMiddleName();
		}
		if (StringUtils.isNotBlank(employeeVO.getLastName())) {
			employeeName = employeeName + " " + employeeVO.getLastName();
		}
		employeeForm.setName(employeeName);

		Set<CompanyModuleMapping> companyModuleList = employeeVO.getCompany().getCompanyModuleMappings();
		for (CompanyModuleMapping companyModuleMapping : companyModuleList) {

			if (companyModuleMapping.getModuleMaster().getModuleName().equals(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
				employeeForm.setClaimModule(true);
			}

			if (companyModuleMapping.getModuleMaster().getModuleName().equals(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
				employeeForm.setLeaveModule(true);
			}

			if (companyModuleMapping.getModuleMaster().getModuleName().equals(PayAsiaConstants.COMPANY_MODULE_HRIS)) {
				employeeForm.setPayslipModule(true);
			}

			if (companyModuleMapping.getModuleMaster().getModuleName().equals(PayAsiaConstants.COMPANY_MODULE_MOBILE)) {
				employeeForm.setMobileModule(true);
			}

		}

		if (!employeeForm.getMobileModule()) {
			throw new PayAsiaSystemException("Mobile Access Disabled");
		}

		return employeeForm;
	}

	private Employee getDelegatedEmployee(Long employeeId) {
		Employee emp = employeeDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterLeave = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_CATEGORY_HRIS);
		if (appCodeMasterLeave != null) {
			WorkflowDelegate workflowDelegateHris = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
					appCodeMasterLeave.getAppCodeID());

			if (workflowDelegateHris != null) {
				workflowDelegate = workflowDelegateHris;
			} else {

				AppCodeMaster appCodeMasterALL = appCodeMasterDAO.findByCategoryAndDesc(
						PayAsiaConstants.WORKFLOW_CATEGORY, PayAsiaConstants.WORKFLOW_CATEGORY_ALL);

				WorkflowDelegate workflowDelegateALL = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
						appCodeMasterALL.getAppCodeID());

				workflowDelegate = workflowDelegateALL;
			}

			if (workflowDelegate != null) {
				return workflowDelegate.getEmployee2();
			}
		}

		return emp;
	}

	private Field getExistingDynamicFormFieldInfo(String XML, Long dataDictionaryId) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}
		final StringReader xmlReader = new StringReader(XML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {

			if (field.getDictionaryId().equals(dataDictionaryId)) {
				return field;
			}

		}
		return null;
	}

	@Override
	public EmployeeListFormPage updateEmplViewProfileDynamicFormRecord(String xml, Long companyID, Long entityID,
			Long formID, Integer version, Long empid, Long tabId, Long languageId) {
		EmployeeListFormPage employeeListForm = new EmployeeListFormPage();
		Company cmp = companyDAO.findById(companyID);
		String companyDateFormat = cmp.getDateFormat();

		Employee employeeVO = employeeDAO.findById(empid);

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		HRISStatusMaster hrisStatusMasterVO = hrisStatusMasterDAO.findByName(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO.findById(tabId);

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyID, dynamicFormRecord.getEntity_ID(),
				dynamicFormRecord.getForm_ID());
		String dynamicFormXML = dynamicForm.getMetaData();

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}
		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

		final StringReader xmlReader = new StringReader(xml);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();
			String fieldValue = field.getValue();
			String fieldtype = field.getType();

			Field dynamicFormField = getExistingDynamicFormFieldInfo(dynamicFormXML, field.getDictionaryId());

			if (!fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE) && !"".equalsIgnoreCase(fieldValue)) {
				fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldValue, companyDateFormat);
				int isValid = ValidationUtils.validateDate(fieldValue, PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
				if (isValid == 1) {
					throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
				}
			}
			if (dynamicFormField.isApprovalRequired() != null && dynamicFormField.isReadOnly() != null
					&& dynamicFormField.isApprovalRequired() && !dynamicFormField.isReadOnly()) {
				List<EmployeeHRISReviewer> hrisReviewerList = employeeHRISReviewerDAO.findByCondition(empid, companyID);
				if (hrisReviewerList == null) {
					employeeListForm.setMessage("noreviewer");
					return employeeListForm;
				} else {
					HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO
							.findByCondition(dynamicFormField.getDictionaryId(), empid, hrisStatusList);
					if (hrisChangeRequestVO == null) {
						HRISChangeRequest persistChangeRequestVO = null;
						HRISChangeRequest hrisChangeRequest = new HRISChangeRequest();
						DataDictionary dataDictionary = dataDictionaryDAO.findById(field.getDictionaryId());
						hrisChangeRequest.setDataDictionary(dataDictionary);
						hrisChangeRequest.setEmployee(employeeVO);

						String getMethodName = "getCol"
								+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
						String oldValue;
						try {
							Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(getMethodName);
							try {
								oldValue = (String) dynamicFormRecordMethod.invoke(dynamicFormRecord);
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

						hrisChangeRequest.setOldValue(oldValue);
						hrisChangeRequest.setNewValue(fieldValue);
						hrisChangeRequest.setHrisStatusMaster(hrisStatusMasterVO);
						if (StringUtils.isNotBlank(oldValue)) {
							if (StringUtils.isNotBlank(fieldValue)) {
								if (!oldValue.equals(fieldValue)) {
									persistChangeRequestVO = hrisChangeRequestDAO.savePersist(hrisChangeRequest);
								}
							}
						} else {
							if (StringUtils.isNotBlank(fieldValue)) {
								persistChangeRequestVO = hrisChangeRequestDAO.savePersist(hrisChangeRequest);
							}
						}
						if (persistChangeRequestVO != null) {
							String forwardTo = "";
							Employee forwardToEmployee = null;

							List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeHRISReviewerDAO
									.findByCondition(empid, companyID);
							if (!employeeHRISReviewerList.isEmpty()) {
								for (EmployeeHRISReviewer employeeHRISReviewer : employeeHRISReviewerList) {
									HRISChangeRequestReviewer changeRequestReviewer = new HRISChangeRequestReviewer();
									changeRequestReviewer.setHrisChangeRequest(persistChangeRequestVO);
									changeRequestReviewer.setEmployeeReviewer(getDelegatedEmployee(
											employeeHRISReviewer.getEmployeeReviewer().getEmployeeId()));
									changeRequestReviewer
											.setWorkFlowRuleMaster(employeeHRISReviewer.getWorkFlowRuleMaster());
									if (employeeHRISReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
										forwardTo = employeeHRISReviewer.getEmployeeReviewer().getEmail();
										forwardToEmployee = employeeHRISReviewer.getEmployeeReviewer();
										changeRequestReviewer.setPending(true);
									} else {
										changeRequestReviewer.setPending(false);
									}

									hrisChangeRequestReviewerDAO.save(changeRequestReviewer);
								}

							}

							HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
							HRISChangeRequestWorkflow changeRequestWorkflow = new HRISChangeRequestWorkflow();
							changeRequestWorkflow.setHrisChangeRequest(persistChangeRequestVO);
							changeRequestWorkflow.setNewValue(fieldValue);
							changeRequestWorkflow.setForwardTo(forwardTo);
							changeRequestWorkflow.setHrisStatusMaster(hrisStatusMasterVO);
							changeRequestWorkflow.setEmployee(employeeVO);
							changeRequestWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
							hrisChangeRequestWorkflowPersistObj = hrisChangeRequestWorkflowDAO
									.savePersist(changeRequestWorkflow);

							EmployeeListFormPage employeeListFormPage = generalLogic.getEmployeeHRISChangeRequestData(
									persistChangeRequestVO.getHrisChangeRequestId(),
									persistChangeRequestVO.getEmployee().getCompany().getCompanyId(),
									dynamicForm.getMetaData(), languageId);

							hrisMailLogic.sendEMailForHRISDataChange(companyID, persistChangeRequestVO,
									hrisChangeRequestWorkflowPersistObj,
									PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_APPLY, employeeVO,
									forwardToEmployee, employeeListFormPage);

						}
					}
				}

			} else {
				if (dynamicFormField.isReadOnly() != null && !dynamicFormField.isReadOnly()) {
					try {
						String methodName = "setCol"
								+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
						Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
						try {

							dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);

						} catch (IllegalArgumentException illegalArgumentException) {
							LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
							throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
									illegalArgumentException);
						} catch (IllegalAccessException illegalAccessException) {
							LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
							throw new PayAsiaSystemException(illegalAccessException.getMessage(),
									illegalAccessException);
						} catch (InvocationTargetException invocationTargetException) {
							LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
							throw new PayAsiaSystemException(invocationTargetException.getMessage(),
									invocationTargetException);
						}
					} catch (SecurityException securityException) {
						LOGGER.error(securityException.getMessage(), securityException);
						throw new PayAsiaSystemException(securityException.getMessage(), securityException);
					} catch (NoSuchMethodException noSuchMethodException) {

						LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
						throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
					}
				}
			}

		}

		dynamicFormRecordDAO.update(dynamicFormRecord);
		return employeeListForm;
	}

	@Override
	public Map<String, Object> saveUserDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			DynamicForm dynamicForm) {
		Map<String, Object> errorMess = new HashMap<String, Object>();
		try {
			Company cmp = companyDAO.findById(dynamicFormDataForm.getCompanyId());
			String companyDateFormat = cmp.getDateFormat();

			Employee employeeVO = employeeDAO.findById(dynamicFormDataForm.getEmployeeId());

			List<String> hrisStatusList = new ArrayList<>();
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

			HRISStatusMaster hrisStatusMasterVO = hrisStatusMasterDAO
					.findByName(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
			DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();

			Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
			if (!formValue.isEmpty()) {

				Iterator<Map.Entry<String, Object>> itr = formValue.entrySet().iterator();
				
				Map<String, Field> fieldMap = new HashMap<String, Field>();

				Tab tab = (Tab) HRISUtils.unmarshal(dynamicForm.getMetaData());

				List<Field> fieldList = tab.getField();

				for (Field field : fieldList) {
					fieldMap.put(field.getName(), field);
				}

				while (itr.hasNext()) {
					Map.Entry<String, Object> entry = itr.next();

					if (fieldMap.containsKey(entry.getKey())) {

						Field field = fieldMap.get(entry.getKey());

						field.setValue((entry.getValue() == null || entry.getValue().equals("")
								|| "\"null\"".equalsIgnoreCase("" + entry.getValue())) ? null
										: entry.getValue().toString());

						String fieldName = field.getName();
						String fieldValue = field.getValue();
						if (fieldValue != null && fieldValue.length() > 99) {
							fieldValue = fieldValue.substring(0, 99);
						}

						String fieldtype = field.getType();

						if (!fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)
								|| !fieldtype.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
								|| !fieldtype.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_FIELD_TYPE)) {

							if (fieldtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE) && fieldValue != null) {
								fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(fieldValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									errorMess.put("error", PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									return errorMess;
								}
							}
							if (field.isApprovalRequired() != null && field.isReadOnly() != null
									&& field.isApprovalRequired() && !field.isReadOnly()) {
								List<EmployeeHRISReviewer> hrisReviewerList = employeeHRISReviewerDAO.findByCondition(
										dynamicFormDataForm.getEmployeeId(), dynamicFormDataForm.getCompanyId());
								if (hrisReviewerList == null || hrisReviewerList.isEmpty()) {
									errorMess.put("error", PayAsiaConstants.PAYASIA_REVIEWER_NOT_DEFINE);
									return errorMess;
								} else {
									HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO.findByCondition(
											field.getDictionaryId(), dynamicFormDataForm.getEmployeeId(),
											hrisStatusList);
									if (hrisChangeRequestVO == null) {
										HRISChangeRequest persistChangeRequestVO = null;
										HRISChangeRequest hrisChangeRequest = new HRISChangeRequest();
										DataDictionary dataDictionary = dataDictionaryDAO
												.findById(field.getDictionaryId());
										hrisChangeRequest.setDataDictionary(dataDictionary);
										hrisChangeRequest.setEmployee(employeeVO);

										String getMethodName = "getCol" + fieldName
												.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
										String oldValue;
										try {
											Method dynamicFormRecordMethod = dynamicFormRecordClass
													.getMethod(getMethodName);
											try {
												oldValue = (String) dynamicFormRecordMethod.invoke(dynamicFormRecord);
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

										hrisChangeRequest.setOldValue(oldValue);
										hrisChangeRequest.setNewValue(fieldValue);
										hrisChangeRequest.setHrisStatusMaster(hrisStatusMasterVO);
										if (StringUtils.isNotBlank(oldValue)) {
											if (StringUtils.isNotBlank(fieldValue)) {
												if (!oldValue.equals(fieldValue)) {
													persistChangeRequestVO = hrisChangeRequestDAO
															.savePersist(hrisChangeRequest);
												}
											}
										} else {
											if (StringUtils.isNotBlank(fieldValue)) {
												persistChangeRequestVO = hrisChangeRequestDAO
														.savePersist(hrisChangeRequest);
											}
										}
										if (persistChangeRequestVO != null) {
											String forwardTo = "";
											Employee forwardToEmployee = null;

											List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeHRISReviewerDAO
													.findByCondition(dynamicFormDataForm.getEmployeeId(),
															dynamicFormDataForm.getCompanyId());
											if (!employeeHRISReviewerList.isEmpty()) {
												for (EmployeeHRISReviewer employeeHRISReviewer : employeeHRISReviewerList) {
													HRISChangeRequestReviewer changeRequestReviewer = new HRISChangeRequestReviewer();
													changeRequestReviewer.setHrisChangeRequest(persistChangeRequestVO);
													changeRequestReviewer.setEmployeeReviewer(
															getDelegatedEmployee(employeeHRISReviewer
																	.getEmployeeReviewer().getEmployeeId()));
													changeRequestReviewer.setWorkFlowRuleMaster(
															employeeHRISReviewer.getWorkFlowRuleMaster());
													if (employeeHRISReviewer.getWorkFlowRuleMaster().getRuleValue()
															.equals("1")) {
														forwardTo = employeeHRISReviewer.getEmployeeReviewer()
																.getEmail();
														forwardToEmployee = employeeHRISReviewer.getEmployeeReviewer();
														changeRequestReviewer.setPending(true);
													} else {
														changeRequestReviewer.setPending(false);
													}
													hrisChangeRequestReviewerDAO.save(changeRequestReviewer);
												}
											}

											HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
											HRISChangeRequestWorkflow changeRequestWorkflow = new HRISChangeRequestWorkflow();
											changeRequestWorkflow.setHrisChangeRequest(persistChangeRequestVO);
											changeRequestWorkflow.setNewValue(fieldValue);
											changeRequestWorkflow.setForwardTo(forwardTo);
											changeRequestWorkflow.setHrisStatusMaster(hrisStatusMasterVO);
											changeRequestWorkflow.setEmployee(employeeVO);
											changeRequestWorkflow
													.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
											hrisChangeRequestWorkflowPersistObj = hrisChangeRequestWorkflowDAO
													.savePersist(changeRequestWorkflow);

											EmployeeListFormPage employeeListFormPage = generalLogic
													.getEmployeeHRISChangeRequestData(
															persistChangeRequestVO.getHrisChangeRequestId(),
															persistChangeRequestVO.getEmployee().getCompany()
																	.getCompanyId(),
															dynamicForm.getMetaData(),
															dynamicFormDataForm.getLanguageId());

											hrisMailLogic.sendEMailForHRISDataChange(dynamicFormDataForm.getCompanyId(),
													persistChangeRequestVO, hrisChangeRequestWorkflowPersistObj,
													PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_APPLY,
													employeeVO, forwardToEmployee, employeeListFormPage);
										}
									}
								}

							} else {
								if (field.isReadOnly() != null && !field.isReadOnly()) {
									try {
										String methodName = "setCol" + fieldName
												.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
										Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName,
												String.class);
										try {
											dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
										} catch (IllegalArgumentException illegalArgumentException) {
											LOGGER.error(illegalArgumentException.getMessage(),
													illegalArgumentException);
											throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
													illegalArgumentException);
										} catch (IllegalAccessException illegalAccessException) {
											LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
											throw new PayAsiaSystemException(illegalAccessException.getMessage(),
													illegalAccessException);
										} catch (InvocationTargetException invocationTargetException) {
											LOGGER.error(invocationTargetException.getMessage(),
													invocationTargetException);
											throw new PayAsiaSystemException(invocationTargetException.getMessage(),
													invocationTargetException);
										}
									} catch (SecurityException securityException) {
										LOGGER.error(securityException.getMessage(), securityException);
										throw new PayAsiaSystemException(securityException.getMessage(),
												securityException);
									} catch (NoSuchMethodException noSuchMethodException) {

										LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
										throw new PayAsiaSystemException(noSuchMethodException.getMessage(),
												noSuchMethodException);
									}
								}
							}
						}
					}
				}
			}

			dynamicFormRecord.setCompany_ID(dynamicFormDataForm.getCompanyId());
			dynamicFormRecord.setEntity_ID(dynamicForm.getEntityMaster().getEntityId());
			dynamicFormRecord.setForm_ID(dynamicForm.getId().getFormId());
			dynamicFormRecord.setVersion(dynamicForm.getId().getVersion());
			dynamicFormRecord.setEntityKey(dynamicFormDataForm.getEmployeeId());

			dynamicFormRecordDAO.saveReturn(dynamicFormRecord);

		} catch (Exception e) {
			e.printStackTrace();
			errorMess.put("error", "Data not saved");
			return errorMess;
		}
		return errorMess;
	}

	@Override
	public Map<String, Object> updateUserDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			DynamicForm dynamicForm) {
		Map<String, Object> errorMess = new HashMap<String, Object>();

		try {
			Company cmp = companyDAO.findById(dynamicFormDataForm.getCompanyId());
			String companyDateFormat = cmp.getDateFormat();

			Employee employeeVO = employeeDAO.findById(dynamicFormDataForm.getEmployeeId());

			List<String> hrisStatusList = new ArrayList<>();
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

			HRISStatusMaster hrisStatusMasterVO = hrisStatusMasterDAO
					.findByName(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
			DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO
					.findById(Long.parseLong(dynamicFormDataForm.getFormRecordId()));

			Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
			if (!formValue.isEmpty()) {

				Iterator<Map.Entry<String, Object>> itr = formValue.entrySet().iterator();
				
				Map<String, Field> fieldMap = new HashMap<String, Field>();

				Tab tab = (Tab) HRISUtils.unmarshal(dynamicForm.getMetaData());

				List<Field> fieldList = tab.getField();

				for (Field field : fieldList) {
					fieldMap.put(field.getName(), field);
				}

				while (itr.hasNext()) {
					Map.Entry<String, Object> entry = itr.next();					

					if (fieldMap.containsKey(entry.getKey())) {

						Field field = fieldMap.get(entry.getKey());

						field.setValue((entry.getValue() == null || entry.getValue().equals("")
								|| "\"null\"".equalsIgnoreCase("" + entry.getValue())) ? null
										: entry.getValue().toString());

						String fieldName = field.getName();
						String fieldValue = field.getValue();
						if (fieldValue != null && fieldValue.length() > 99) {
							fieldValue = fieldValue.substring(0, 99);
						}

						String fieldtype = field.getType();
						if (!fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)
								|| !fieldtype.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
								|| !fieldtype.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_FIELD_TYPE)) {

							if (fieldtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE) && fieldValue != null) {
								fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(fieldValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									errorMess.put("error", PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									return errorMess;
								}
							}
							if (field.isApprovalRequired() != null && field.isReadOnly() != null
									&& field.isApprovalRequired() && !field.isReadOnly()) {
								List<EmployeeHRISReviewer> hrisReviewerList = employeeHRISReviewerDAO.findByCondition(
										dynamicFormDataForm.getEmployeeId(), dynamicFormDataForm.getCompanyId());
								if (hrisReviewerList == null || hrisReviewerList.isEmpty()) {
									errorMess.put("error", PayAsiaConstants.PAYASIA_REVIEWER_NOT_DEFINE);
									return errorMess;
								} else {
									HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO.findByCondition(
											field.getDictionaryId(), dynamicFormDataForm.getEmployeeId(),
											hrisStatusList);
									if (hrisChangeRequestVO == null) {
										HRISChangeRequest persistChangeRequestVO = null;
										HRISChangeRequest hrisChangeRequest = new HRISChangeRequest();
										DataDictionary dataDictionary = dataDictionaryDAO
												.findById(field.getDictionaryId());
										hrisChangeRequest.setDataDictionary(dataDictionary);
										hrisChangeRequest.setEmployee(employeeVO);

										String getMethodName = "getCol" + fieldName
												.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
										String oldValue;
										try {
											Method dynamicFormRecordMethod = dynamicFormRecordClass
													.getMethod(getMethodName);
											try {
												oldValue = (String) dynamicFormRecordMethod.invoke(dynamicFormRecord);
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

										hrisChangeRequest.setOldValue(oldValue);
										hrisChangeRequest.setNewValue(fieldValue);
										hrisChangeRequest.setHrisStatusMaster(hrisStatusMasterVO);
										if (StringUtils.isNotBlank(oldValue)) {
											if (StringUtils.isNotBlank(fieldValue)) {
												if (!oldValue.equals(fieldValue)) {
													persistChangeRequestVO = hrisChangeRequestDAO
															.savePersist(hrisChangeRequest);
												}
											}
										} else {
											if (StringUtils.isNotBlank(fieldValue)) {
												persistChangeRequestVO = hrisChangeRequestDAO
														.savePersist(hrisChangeRequest);
											}
										}
										if (persistChangeRequestVO != null) {
											String forwardTo = "";
											Employee forwardToEmployee = null;

											List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeHRISReviewerDAO
													.findByCondition(dynamicFormDataForm.getEmployeeId(),
															dynamicFormDataForm.getCompanyId());
											if (!employeeHRISReviewerList.isEmpty()) {
												for (EmployeeHRISReviewer employeeHRISReviewer : employeeHRISReviewerList) {
													HRISChangeRequestReviewer changeRequestReviewer = new HRISChangeRequestReviewer();
													changeRequestReviewer.setHrisChangeRequest(persistChangeRequestVO);
													changeRequestReviewer.setEmployeeReviewer(
															getDelegatedEmployee(employeeHRISReviewer
																	.getEmployeeReviewer().getEmployeeId()));
													changeRequestReviewer.setWorkFlowRuleMaster(
															employeeHRISReviewer.getWorkFlowRuleMaster());
													if (employeeHRISReviewer.getWorkFlowRuleMaster().getRuleValue()
															.equals("1")) {
														forwardTo = employeeHRISReviewer.getEmployeeReviewer()
																.getEmail();
														forwardToEmployee = employeeHRISReviewer.getEmployeeReviewer();
														changeRequestReviewer.setPending(true);
													} else {
														changeRequestReviewer.setPending(false);
													}
													hrisChangeRequestReviewerDAO.save(changeRequestReviewer);
												}
											}

											HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
											HRISChangeRequestWorkflow changeRequestWorkflow = new HRISChangeRequestWorkflow();
											changeRequestWorkflow.setHrisChangeRequest(persistChangeRequestVO);
											changeRequestWorkflow.setNewValue(fieldValue);
											changeRequestWorkflow.setForwardTo(forwardTo);
											changeRequestWorkflow.setHrisStatusMaster(hrisStatusMasterVO);
											changeRequestWorkflow.setEmployee(employeeVO);
											changeRequestWorkflow
													.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
											hrisChangeRequestWorkflowPersistObj = hrisChangeRequestWorkflowDAO
													.savePersist(changeRequestWorkflow);

											EmployeeListFormPage employeeListFormPage = generalLogic
													.getEmployeeHRISChangeRequestData(
															persistChangeRequestVO.getHrisChangeRequestId(),
															persistChangeRequestVO.getEmployee().getCompany()
																	.getCompanyId(),
															dynamicForm.getMetaData(),
															dynamicFormDataForm.getLanguageId());

											hrisMailLogic.sendEMailForHRISDataChange(dynamicFormDataForm.getCompanyId(),
													persistChangeRequestVO, hrisChangeRequestWorkflowPersistObj,
													PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_APPLY,
													employeeVO, forwardToEmployee, employeeListFormPage);
										}
									}
								}

							} else {
								if (field.isReadOnly() != null && !field.isReadOnly()) {
									try {
										String methodName = "setCol" + fieldName
												.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
										Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName,
												String.class);
										try {
											dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);

										} catch (IllegalArgumentException illegalArgumentException) {
											LOGGER.error(illegalArgumentException.getMessage(),
													illegalArgumentException);
											throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
													illegalArgumentException);
										} catch (IllegalAccessException illegalAccessException) {
											LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
											throw new PayAsiaSystemException(illegalAccessException.getMessage(),
													illegalAccessException);
										} catch (InvocationTargetException invocationTargetException) {
											LOGGER.error(invocationTargetException.getMessage(),
													invocationTargetException);
											throw new PayAsiaSystemException(invocationTargetException.getMessage(),
													invocationTargetException);
										}
									} catch (SecurityException securityException) {
										LOGGER.error(securityException.getMessage(), securityException);
										throw new PayAsiaSystemException(securityException.getMessage(),
												securityException);
									} catch (NoSuchMethodException noSuchMethodException) {
										LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
										throw new PayAsiaSystemException(noSuchMethodException.getMessage(),
												noSuchMethodException);
									}
								}
							}
						}
					}
				}
			}
			dynamicFormRecordDAO.update(dynamicFormRecord);
		} catch (Exception e) {
			errorMess.put("error", "Data not saved");
			return errorMess;
		}
		return errorMess;
	}

	@Override
	public Map<String, Object> saveTableDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			DynamicForm dynamicForm) {

		Map<String, Object> errorMess = new HashMap<String, Object>();
		Employee employeeVO = employeeDAO.findById(dynamicFormDataForm.getEmployeeId());

		HRISStatusMaster hrisStatusMasterVO = hrisStatusMasterDAO.findByName(PayAsiaConstants.HRIS_STATUS_SUBMITTED);

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		Company cmp = companyDAO.findById(dynamicFormDataForm.getCompanyId());
		String companyDateFormat = cmp.getDateFormat();
		DynamicFormRecord dynamicFormRecord = null;
		String fieldValue = null;

		if (dynamicFormDataForm.getFormRecordId() != null && dynamicFormDataForm.getFormRecordId().equals("0")) {
			dynamicFormRecord = new DynamicFormRecord();
			dynamicFormRecord.setForm_ID(Long.parseLong(dynamicFormDataForm.getFormId()));
			dynamicFormRecord.setVersion(dynamicForm.getId().getVersion());
			dynamicFormRecord.setEntityKey(dynamicFormDataForm.getEmployeeId());
			dynamicFormRecord.setCompany_ID(dynamicFormDataForm.getCompanyId());
			dynamicFormRecord.setEntity_ID(dynamicForm.getId().getEntity_ID());
		} else {
			dynamicFormRecord = dynamicFormRecordDAO.findById(Long.parseLong(dynamicFormDataForm.getFormRecordId()));
		}

		DynamicFormTableRecord dynamicFormTableRecord = null;
		DynamicFormTableRecordPK dynamicFormTableRecordPK = null;

		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

		Field field = getExistingDynamicFormFieldInfo(dynamicForm.getMetaData(),
				Long.parseLong(dynamicFormDataForm.getDataDictionaryId()));

		if (field != null) {
			String fieldName = field.getName();
			fieldValue = dynamicFormDataForm.getFormTableRecordId();
			String fieldtype = field.getType();
			Integer seqNo = (dynamicFormDataForm.getSeqNo() == null
					|| dynamicFormDataForm.getSeqNo().equals("")) == true ? 0
							: Integer.parseInt(dynamicFormDataForm.getSeqNo());

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)
					|| fieldtype.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				synchronized (this) {

					Long maxTableRecordId = null;

					if ((fieldValue != null || "".equals(fieldValue)) && seqNo != 0) {
						dynamicFormTableRecord = dynamicFormTableRecordDAO.findByIdAndSeq(Long.parseLong(fieldValue),
								seqNo);
					} else if (fieldValue == null || "".equals(fieldValue)) {
						maxTableRecordId = dynamicFormTableRecordDAO.getMaxTableRecordId() + 1;
						fieldValue = "" + maxTableRecordId;
						dynamicFormDataForm.setFormTableRecordId("" + maxTableRecordId);
						seqNo = 1;
					} else {
						maxTableRecordId = Long.parseLong(fieldValue);
						Integer maxSeqNo = dynamicFormTableRecordDAO.getMaxSequenceNumber(maxTableRecordId);
						if (maxSeqNo != 0) {
							seqNo = maxSeqNo + 1;
						} else {
							seqNo = 1;
						}
					}

					if (!formValue.isEmpty()) {
						Iterator<Map.Entry<String, Object>> itr = formValue.entrySet().iterator();

						boolean newRecord = false;
						if (dynamicFormTableRecord == null) {
							newRecord = true;
							dynamicFormTableRecord = new DynamicFormTableRecord();
							dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
						}

						Class<?> dynamicFormTableRecordClass = dynamicFormTableRecord.getClass();
						
						Map<String, Column> columnMap = new HashMap<String, Column>();
						List<Column> columnList = field.getColumn();
						for (Column column : columnList) {
							columnMap.put(column.getName(), column);
						}
						
						while (itr.hasNext()) {
							Map.Entry<String, Object> entry = itr.next();							

							if (columnMap.containsKey(entry.getKey())) {

								Column column = columnMap.get(entry.getKey());

								DataDictionary fieldDataDictionary = dataDictionaryDAO
										.findById(column.getDictionaryId());
								if (fieldDataDictionary == null
										&& column.getName().equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
									continue;
								}

								String colName = column.getName();
								String colValue = (entry.getValue() == null || entry.getValue().equals("")
										|| "\"null\"".equalsIgnoreCase("" + entry.getValue())) ? null
												: entry.getValue().toString();
								if (colValue != null && colValue.length() > 99) {
									colValue = colValue.substring(0, 99);
								}

								String colType = column.getType();

								if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
									colValue = DateUtils.appendTodayTime(colValue);

								} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
										&& colValue != null) {
									colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
									int isValid = ValidationUtils.validateDate(colValue,
											PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
									if (isValid == 1) {
										throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
									}
								}

								if (column.isApprovalRequired() != null && column.isReadOnly() != null
										&& column.isApprovalRequired() && !column.isReadOnly()) {
									List<EmployeeHRISReviewer> hrisReviewerList = employeeHRISReviewerDAO
											.findByCondition(dynamicFormDataForm.getEmployeeId(),
													dynamicFormDataForm.getCompanyId());
									if (hrisReviewerList == null || hrisReviewerList.isEmpty()) {
										errorMess.put("error", PayAsiaConstants.PAYASIA_REVIEWER_NOT_DEFINE);
										return errorMess;
									} else {
										HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO
												.findByConditionTableSeq(column.getDictionaryId(),
														dynamicFormDataForm.getEmployeeId(), hrisStatusList, seqNo);
										if (hrisChangeRequestVO == null) {
											HRISChangeRequest persistChangeRequestVO = null;
											HRISChangeRequest hrisChangeRequest = new HRISChangeRequest();
											DataDictionary dataDictionary = dataDictionaryDAO
													.findById(column.getDictionaryId());
											hrisChangeRequest.setDataDictionary(dataDictionary);
											hrisChangeRequest.setEmployee(employeeVO);

											String tableRecordGetMethodName = PayAsiaConstants.GET_COL
													+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
											String oldValue;
											Method dynamicFormTableRecordMethod;
											try {
												dynamicFormTableRecordMethod = dynamicFormTableRecordClass
														.getMethod(tableRecordGetMethodName);
												try {
													oldValue = (String) dynamicFormTableRecordMethod
															.invoke(dynamicFormTableRecord);
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

											hrisChangeRequest.setTableRecordSequence(seqNo);
											hrisChangeRequest.setOldValue(oldValue);
											hrisChangeRequest.setNewValue(colValue);
											hrisChangeRequest.setHrisStatusMaster(hrisStatusMasterVO);
											if (StringUtils.isNotBlank(oldValue)) {
												if (StringUtils.isNotBlank(colValue)) {
													if (!oldValue.equals(colValue)) {
														persistChangeRequestVO = hrisChangeRequestDAO
																.savePersist(hrisChangeRequest);
													}
												}
											} else {
												if (StringUtils.isNotBlank(colValue)) {
													persistChangeRequestVO = hrisChangeRequestDAO
															.savePersist(hrisChangeRequest);
												}
											}
											if (persistChangeRequestVO != null) {
												String forwardTo = "";

												Employee forwardToEmployee = null;

												List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeHRISReviewerDAO
														.findByCondition(dynamicFormDataForm.getEmployeeId(),
																dynamicFormDataForm.getCompanyId());
												if (!employeeHRISReviewerList.isEmpty()) {
													for (EmployeeHRISReviewer employeeHRISReviewer : employeeHRISReviewerList) {
														HRISChangeRequestReviewer changeRequestReviewer = new HRISChangeRequestReviewer();
														changeRequestReviewer
																.setHrisChangeRequest(persistChangeRequestVO);
														changeRequestReviewer.setEmployeeReviewer(
																getDelegatedEmployee(employeeHRISReviewer
																		.getEmployeeReviewer().getEmployeeId()));
														changeRequestReviewer.setWorkFlowRuleMaster(
																employeeHRISReviewer.getWorkFlowRuleMaster());
														if (employeeHRISReviewer.getWorkFlowRuleMaster().getRuleValue()
																.equals("1")) {
															forwardTo = employeeHRISReviewer.getEmployeeReviewer()
																	.getEmail();
															forwardToEmployee = employeeHRISReviewer
																	.getEmployeeReviewer();
															changeRequestReviewer.setPending(true);
														} else {
															changeRequestReviewer.setPending(false);
														}
														hrisChangeRequestReviewerDAO.save(changeRequestReviewer);
													}
												}

												HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
												HRISChangeRequestWorkflow changeRequestWorkflow = new HRISChangeRequestWorkflow();
												changeRequestWorkflow.setHrisChangeRequest(persistChangeRequestVO);
												changeRequestWorkflow.setNewValue(colValue);
												changeRequestWorkflow.setForwardTo(forwardTo);
												changeRequestWorkflow.setHrisStatusMaster(hrisStatusMasterVO);
												changeRequestWorkflow
														.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
												changeRequestWorkflow.setEmployee(employeeVO);
												hrisChangeRequestWorkflowPersistObj = hrisChangeRequestWorkflowDAO
														.savePersist(changeRequestWorkflow);

												EmployeeListFormPage employeeListForm = generalLogic
														.getEmployeeHRISChangeRequestData(
																persistChangeRequestVO.getHrisChangeRequestId(),
																persistChangeRequestVO.getEmployee().getCompany()
																		.getCompanyId(),
																dynamicForm.getMetaData(),
																dynamicFormDataForm.getLanguageId());

												hrisMailLogic.sendEMailForHRISDataChange(
														dynamicFormDataForm.getCompanyId(), persistChangeRequestVO,
														hrisChangeRequestWorkflowPersistObj,
														PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_APPLY,
														employeeVO, forwardToEmployee, employeeListForm);
											}
										}
									}
								} else {
									if (!column.isReadOnly()) {
										String tableRecordMethodName = PayAsiaConstants.SET_COL
												+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
										Method dynamicFormTableRecordMethod;
										try {
											dynamicFormTableRecordMethod = DynamicFormTableRecord.class
													.getMethod(tableRecordMethodName, String.class);
											dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

										} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
												| IllegalAccessException | InvocationTargetException e) {
											LOGGER.error(e.getMessage(), e);
											throw new PayAsiaSystemException(e.getMessage(), e);
										}
									}
								}
							}
						}

						try {
							String methodName = "setCol"
									+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
							Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
							try {
								dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);

							} catch (IllegalArgumentException illegalArgumentException) {
								LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
								throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
										illegalArgumentException);
							} catch (IllegalAccessException illegalAccessException) {
								LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
								throw new PayAsiaSystemException(illegalAccessException.getMessage(),
										illegalAccessException);
							} catch (InvocationTargetException invocationTargetException) {
								LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
								throw new PayAsiaSystemException(invocationTargetException.getMessage(),
										invocationTargetException);
							}
						} catch (SecurityException securityException) {
							LOGGER.error(securityException.getMessage(), securityException);
							throw new PayAsiaSystemException(securityException.getMessage(), securityException);
						} catch (NoSuchMethodException noSuchMethodException) {
							LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
							throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
						}

						if (dynamicFormDataForm.getFormRecordId() != null
								&& dynamicFormDataForm.getFormRecordId().equals("0")) {
							dynamicFormRecord = dynamicFormRecordDAO.saveReturn(dynamicFormRecord);
							dynamicFormDataForm.setFormRecordId("" + dynamicFormRecord.getRecordId());
						} else {
							dynamicFormRecordDAO.update(dynamicFormRecord);
						}

						if (newRecord) {
							dynamicFormTableRecordPK.setDynamicFormTableRecordId(maxTableRecordId);
							dynamicFormTableRecordPK.setSequence(seqNo);
							dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
							dynamicFormTableRecord.setDfrColName(fieldName);
							dynamicFormTableRecord.setDfrRecordId(dynamicFormRecord.getRecordId());
							dynamicFormTableRecordDAO.save(dynamicFormTableRecord);
						} else {
							dynamicFormTableRecord.setDfrColName(fieldName);
							dynamicFormTableRecord.setDfrRecordId(dynamicFormRecord.getRecordId());
							dynamicFormTableRecordDAO.update(dynamicFormTableRecord);
						}
					}
				}
			}
			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				// Update Leave Entitlement for 1. Child Care Leave
				// type ,
				// 2. Extended Child Care Leave type i.e. when any
				// dependents
				// details will inserted.
				if (cmp.getCountryMaster().getCountryName().equalsIgnoreCase("Singapore")) {
					String leaveSchemeTypeIds = generalLogic.getChildCareLeaveTypeInfo(
							dynamicFormDataForm.getCompanyId(), dynamicFormDataForm.getEmployeeId());
					leaveSchemeTypeDAO.childCareLeaveEntitlementProc(dynamicFormDataForm.getCompanyId(),
							dynamicFormDataForm.getEmployeeId(), leaveSchemeTypeIds);
				}
			}
		}
		return errorMess;

	}

	@Override
	public Map<String, Object> saveDocumentDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			DynamicForm dynamicForm, MultipartFile files) {

		Map<String, Object> errorMess = new HashMap<String, Object>();

		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				dynamicFormDataForm.getCompanyId(), PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_INFO_DOCUMENT, null,
				null, String.valueOf(dynamicFormDataForm.getEmployeeId()), null,
				PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		String fileOrgName = "";
		String fileName = "";
		if (files != null) {
			fileOrgName = files.getOriginalFilename();
			fileName = uploadEmpDocFile(files, filePath, "_");
		}

		Employee employeeVO = employeeDAO.findById(dynamicFormDataForm.getEmployeeId());

		DynamicFormRecord dynamicFormRecord = null;

		if (dynamicFormDataForm.getFormRecordId() != null && dynamicFormDataForm.getFormRecordId().equals("0")) {
			dynamicFormRecord = new DynamicFormRecord();
			dynamicFormRecord.setForm_ID(Long.parseLong(dynamicFormDataForm.getFormId()));
			dynamicFormRecord.setVersion(dynamicForm.getId().getVersion());
			dynamicFormRecord.setEntityKey(dynamicFormDataForm.getEmployeeId());
			dynamicFormRecord.setCompany_ID(dynamicFormDataForm.getCompanyId());
			dynamicFormRecord.setEntity_ID(dynamicForm.getId().getEntity_ID());
		} else {
			dynamicFormRecord = dynamicFormRecordDAO.findById(Long.parseLong(dynamicFormDataForm.getFormRecordId()));
		}

		Field field = getExistingDynamicFormFieldInfo(dynamicForm.getMetaData(),
				Long.parseLong(dynamicFormDataForm.getDataDictionaryId()));

		DynamicFormTableRecord dynamicFormTableRecord = null;
		DynamicFormTableRecordPK dynamicFormTableRecordPK = null;

		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

		if (field != null) {
			String fieldName = field.getName();
			String fieldValue = dynamicFormDataForm.getFormTableRecordId();
			String fieldtype = field.getType();
			Integer seqNo = (dynamicFormDataForm.getSeqNo() == null
					|| dynamicFormDataForm.getSeqNo().equals("")) == true ? 0
							: Integer.parseInt(dynamicFormDataForm.getSeqNo());
			if (PayAsiaConstants.DOCUMENT_FIELD_TYPE.equalsIgnoreCase(fieldtype)) {
				synchronized (this) {
					Long maxTableRecordId = null;

					if ((fieldValue != null || "".equals(fieldValue)) && seqNo != 0) {
						dynamicFormTableRecord = dynamicFormTableRecordDAO.findByIdAndSeq(Long.parseLong(fieldValue),
								seqNo);
					} else if (fieldValue == null || "".equals(fieldValue)) {
						maxTableRecordId = dynamicFormTableRecordDAO.getMaxTableRecordId() + 1;
						fieldValue = "" + maxTableRecordId;
						dynamicFormDataForm.setFormTableRecordId("" + maxTableRecordId);
						seqNo = 1;
					} else {
						maxTableRecordId = Long.parseLong(fieldValue);
						Integer maxSeqNo = dynamicFormTableRecordDAO.getMaxSequenceNumber(maxTableRecordId);
						if (maxSeqNo != 0) {
							seqNo = maxSeqNo + 1;
						} else {
							seqNo = 1;
						}
					}

					String documentName = "";
					String documentType = "";
					String documentDescription = "";

					if (!formValue.isEmpty()) {
						documentName = (String) (formValue.containsKey("custcol_9") == true
								? (formValue.get("custcol_9") == null || formValue.get("custcol_9").equals("")) ? ""
										: formValue.get("custcol_9")
								: "");
						documentType = (String) (formValue.containsKey("custcol_2") == true
								? (formValue.get("custcol_2") == null || formValue.get("custcol_2").equals("")) ? ""
										: formValue.get("custcol_2")
								: "");
						documentDescription = (String) (formValue.containsKey("custcol_3") == true
								? (formValue.get("custcol_3") == null || formValue.get("custcol_3").equals("")) ? ""
										: formValue.get("custcol_3")
								: "");
					}

					try {
						String methodName = "setCol"
								+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
						Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
						try {
							if (fieldValue != null && !fieldValue.equalsIgnoreCase("null")
									&& !fieldValue.equalsIgnoreCase("")) {
								dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
							}
						} catch (IllegalArgumentException illegalArgumentException) {
							LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
							throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
									illegalArgumentException);
						} catch (IllegalAccessException illegalAccessException) {
							LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
							throw new PayAsiaSystemException(illegalAccessException.getMessage(),
									illegalAccessException);
						} catch (InvocationTargetException invocationTargetException) {
							LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
							throw new PayAsiaSystemException(invocationTargetException.getMessage(),
									invocationTargetException);
						}
					} catch (SecurityException securityException) {
						LOGGER.error(securityException.getMessage(), securityException);
						throw new PayAsiaSystemException(securityException.getMessage(), securityException);
					} catch (NoSuchMethodException noSuchMethodException) {
						LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
						throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
					}

					if (dynamicFormDataForm.getFormRecordId() != null
							&& dynamicFormDataForm.getFormRecordId().equals("0")) {
						dynamicFormRecord = dynamicFormRecordDAO.saveReturn(dynamicFormRecord);
						dynamicFormDataForm.setFormRecordId("" + dynamicFormRecord.getRecordId());
					} else {
						dynamicFormRecordDAO.update(dynamicFormRecord);
					}

					if (dynamicFormTableRecord == null) {
						if (files == null) {
							errorMess.put("error", "Please choose the file before upload");
							return errorMess;
						}
						dynamicFormTableRecord = new DynamicFormTableRecord();
						dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
						dynamicFormTableRecordPK.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecordPK.setSequence(seqNo);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);

						if (StringUtils.isBlank(documentName)) {
							documentName = fileOrgName.substring(0, fileOrgName.lastIndexOf("."));
						}
						if (StringUtils.isBlank(documentType)) {
							documentType = fileOrgName.substring(fileOrgName.lastIndexOf(".") + 1,
									fileOrgName.length());
						}

						setTableDocCustomCol(2, documentType, dynamicFormTableRecord);
						setTableDocCustomCol(3, documentDescription, dynamicFormTableRecord);

						setTableDocCustomCol(4, fileName, dynamicFormTableRecord);
						setTableDocCustomCol(5, DateUtils.timeStampToString(DateUtils.getCurrentTimestamp()),
								dynamicFormTableRecord);
						setTableDocCustomCol(6, getEmployeeName(employeeVO), dynamicFormTableRecord);
						setTableDocCustomCol(7, "Employee", dynamicFormTableRecord);
						setTableDocCustomCol(8, "true", dynamicFormTableRecord);
						setTableDocCustomCol(9, documentName, dynamicFormTableRecord);

						dynamicFormTableRecord.setDfrColName(fieldName);
						dynamicFormTableRecord.setDfrRecordId(dynamicFormRecord.getRecordId());

						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);
					} else {
						if (!documentName.equalsIgnoreCase(dynamicFormTableRecord.getCol9())) {
							saveEmployeeDocumentHistory("Document Name", dynamicFormTableRecord.getCol9(), documentName,
									getEmployeeName(employeeVO), dynamicFormTableRecord);
						}
						if (!documentType.equalsIgnoreCase(dynamicFormTableRecord.getCol2())) {
							saveEmployeeDocumentHistory("Document Type", dynamicFormTableRecord.getCol2(), documentType,
									getEmployeeName(employeeVO), dynamicFormTableRecord);
						}
						if (!documentDescription.equalsIgnoreCase(dynamicFormTableRecord.getCol3())) {
							saveEmployeeDocumentHistory("Document Description", dynamicFormTableRecord.getCol3(),
									documentDescription, getEmployeeName(employeeVO), dynamicFormTableRecord);
						}
						if (files != null) {
							if (!fileName.equalsIgnoreCase(dynamicFormTableRecord.getCol4())) {
								saveEmployeeDocumentHistory("Document File", dynamicFormTableRecord.getCol4(), fileName,
										getEmployeeName(employeeVO), dynamicFormTableRecord);
							}
						}
						setTableDocCustomCol(2, documentType, dynamicFormTableRecord);
						setTableDocCustomCol(3, documentDescription, dynamicFormTableRecord);
						setTableDocCustomCol(4, fileName, dynamicFormTableRecord);
						setTableDocCustomCol(9, documentName, dynamicFormTableRecord);

						dynamicFormTableRecord.setDfrColName(fieldName);
						dynamicFormTableRecord.setDfrRecordId(dynamicFormRecord.getRecordId());

						dynamicFormTableRecordDAO.update(dynamicFormTableRecord);
					}
				}
			}
		}
		return errorMess;
	}

	@Override
	public Map<String, Object> deleteTableRecord(DynamicForm dynamicForm, DynamicFormDataForm dynamicFormDataForm) {

		Map<String, Object> errorMess = new HashMap<String, Object>();

		DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO
				.findById(Long.parseLong(dynamicFormDataForm.getFormRecordId()));
		
		if(!String.valueOf(dynamicFormDataForm.getEmployeeId()).equals(""+dynamicFormRecord.getEntityKey())){
			errorMess.put("error", "Wrong data found try again");
			return errorMess;
		}

		Tab tab = (Tab) HRISUtils.unmarshal(dynamicForm.getMetaData());

		List<Field> listOfFields = tab.getField();
		Field fieldObj = null;
		for (Field field : listOfFields) {
			if ((StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.TABLE_FIELD_TYPE))
					|| (StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DEPENDENTS))
					|| (StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DOCUMENT))) {
				String colValue = getColValueFile(PayAsiaStringUtils.getColNumber(field.getName()), dynamicFormRecord);
				if (StringUtils.isNotBlank(colValue)
						&& StringUtils.equals(colValue, String.valueOf(dynamicFormDataForm.getFormTableRecordId()))) {
					fieldObj = field;
				}
			}
		}

		if (fieldObj != null) {
			if (fieldObj.getType().equalsIgnoreCase(PayAsiaConstants.DOCUMENT_FIELD_TYPE)) {
				try {
					FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath,
							rootDirectoryName, dynamicFormDataForm.getCompanyId(),
							PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME, null, null,
							String.valueOf(dynamicFormDataForm.getEmployeeId()), null,
							PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);
					String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
					if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
						List<String> fileList = new ArrayList<String>();
						fileList.add(filePath);
						awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
					} else {
						FileUtils.deletefile(filePath);
					}
					employeeDocumentHistoryDAO.deleteByConditionEmployeeTableRecord(
							Long.parseLong(dynamicFormDataForm.getFormTableRecordId()),
							Integer.parseInt(dynamicFormDataForm.getSeqNo()));

					dynamicFormTableRecordDAO.deleteByConditionEmployeeTableRecord(
							Long.parseLong(dynamicFormDataForm.getFormTableRecordId()),
							Integer.parseInt(dynamicFormDataForm.getSeqNo()));
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
			}
			if (fieldObj.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)
					|| fieldObj.getType().equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				List<String> hrisStatusList = new ArrayList<>();
				hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
				hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

				List<String> fieldDictIdsList = new ArrayList<>();
				for (int count = 0; count < fieldObj.getColumn().size(); count++) {

					if (StringUtils.isNotBlank("" + fieldObj.getColumn().get(count).getDictionaryId())) {
						fieldDictIdsList.add("" + fieldObj.getColumn().get(count).getDictionaryId());
					}
				}

				List<HRISChangeRequest> hrisChangeRequestList = hrisChangeRequestDAO.findByDataDictIdAndSeq(
						dynamicFormDataForm.getCompanyId(), dynamicFormDataForm.getEmployeeId(),
						Integer.parseInt(dynamicFormDataForm.getSeqNo()), hrisStatusList, fieldDictIdsList);
				if (hrisChangeRequestList == null) {
					dynamicFormTableRecordDAO.deleteByConditionEmployeeTableRecord(
							Long.parseLong(dynamicFormDataForm.getFormTableRecordId()),
							Integer.parseInt(dynamicFormDataForm.getSeqNo()));
				} else {
					errorMess.put("error", "Unable to delete while request in workflow");
				}
			}
		}
		return errorMess;
	}

	@Override
	public DynamicFormTableDocumentDTO downloadEmpDoc(Long companyId, Long employeeId, String fileName) {
		DynamicFormTableDocumentDTO documentDTO = new DynamicFormTableDocumentDTO();
		documentDTO.setFileName(fileName);

		InputStream pdfIn = null;

		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_INFO_DOCUMENT, null, null, String.valueOf(employeeId), null,
				PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				pdfIn = awss3LogicImpl.readS3ObjectAsStream(filePath + fileName);
			} else {
				File file = new File(filePath + fileName);
				pdfIn = new FileInputStream(file);
			}
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}
		try {
			documentDTO.setAttachmentBytes(IOUtils.toByteArray(pdfIn));
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}
		return documentDTO;
	}

	private Column getExistingDynamicFormColumnInfo(String XML, Long dataDictionaryId) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}
		final StringReader xmlReader = new StringReader(XML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			for (Column column : field.getColumn()) {
				if (column.getDictionaryId().equals(dataDictionaryId)) {
					return column;
				}
			}

		}
		return null;

	}

	
	@Override
	public EmployeeListFormPage saveEmployeeViewProfile(String xml, Long companyID, Long entityID, Long formID,
			Integer version, Long empid, Long languageId) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		Company cmp = companyDAO.findById(companyID);
		String companyDateFormat = cmp.getDateFormat();

		Employee employeeVO = employeeDAO.findById(empid);

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		HRISStatusMaster hrisStatusMasterVO = hrisStatusMasterDAO.findByName(PayAsiaConstants.HRIS_STATUS_SUBMITTED);

		DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
		DynamicFormTableRecord dynamicFormTableRecord;
		DynamicFormTableRecordPK dynamicFormTableRecordPK;

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyID, entityID, formID);
		String dynamicFormXML = dynamicForm.getMetaData();

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}

		final StringReader xmlReader = new StringReader(xml);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}
		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();
			String fieldValue = field.getValue();
			String fieldtype = field.getType();

			Field dynamicFormField = getExistingDynamicFormFieldInfo(dynamicFormXML, field.getDictionaryId());

			if (!fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF8");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE) && !"".equalsIgnoreCase(fieldValue)) {
				fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldValue, companyDateFormat);
				int isValid = ValidationUtils.validateDate(fieldValue, PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
				if (isValid == 1) {
					throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
				}
			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				synchronized (this) {

					Long maxTableRecordId = dynamicFormTableRecordDAO.getMaxTableRecordId() + 1;
					int seqNo = 1;
					List<Row> listOfRows = field.getRow();
					for (Row row : listOfRows) {
						dynamicFormTableRecord = new DynamicFormTableRecord();
						dynamicFormTableRecordPK = new DynamicFormTableRecordPK();

						Class<?> dynamicFormTableRecordClass = dynamicFormTableRecord.getClass();

						List<RowValue> listOfRowvalue = row.getRowValue();

						for (RowValue rowValue : listOfRowvalue) {
							String colName = rowValue.getName();
							String colValue;

							DataDictionary fieldDataDictionary = dataDictionaryDAO.findById(rowValue.getDictionaryId());
							if (fieldDataDictionary == null
									&& rowValue.getName().equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								continue;
							}

							Column existingColumn = getExistingDynamicFormColumnInfo(dynamicForm.getMetaData(),
									rowValue.getDictionaryId());

							try {
								colValue = URLDecoder.decode(rowValue.getValue(), "UTF8");
							} catch (UnsupportedEncodingException unsupportedEncodingException) {
								LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
								throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
										unsupportedEncodingException);
							}
							String colType = rowValue.getType();

							if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);

								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
								colValue = DateUtils.appendTodayTime(colValue);

							} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !"".equalsIgnoreCase(colValue)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
							}

							if (existingColumn.isApprovalRequired() != null && existingColumn.isReadOnly() != null
									&& existingColumn.isApprovalRequired() && !existingColumn.isReadOnly()) {
								List<EmployeeHRISReviewer> hrisReviewerList = employeeHRISReviewerDAO
										.findByCondition(empid, companyID);
								if (hrisReviewerList == null) {
									employeeListFormPage.setMessage("noreviewer");
									return employeeListFormPage;
								} else {
									HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO
											.findByConditionTableSeq(existingColumn.getDictionaryId(), empid,
													hrisStatusList, seqNo);
									if (hrisChangeRequestVO == null) {
										HRISChangeRequest persistChangeRequestVO = null;
										HRISChangeRequest hrisChangeRequest = new HRISChangeRequest();
										DataDictionary dataDictionary = dataDictionaryDAO
												.findById(rowValue.getDictionaryId());
										hrisChangeRequest.setDataDictionary(dataDictionary);
										hrisChangeRequest.setEmployee(employeeVO);

										String tableRecordGetMethodName = PayAsiaConstants.GET_COL
												+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
										String oldValue;
										Method dynamicFormTableRecordMethod;
										try {
											dynamicFormTableRecordMethod = dynamicFormTableRecordClass
													.getMethod(tableRecordGetMethodName);
											try {
												oldValue = (String) dynamicFormTableRecordMethod
														.invoke(dynamicFormTableRecord);
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
										hrisChangeRequest.setTableRecordSequence(seqNo);
										hrisChangeRequest.setOldValue(oldValue);
										hrisChangeRequest.setNewValue(colValue);
										hrisChangeRequest.setHrisStatusMaster(hrisStatusMasterVO);
										if (StringUtils.isNotBlank(oldValue)) {
											if (StringUtils.isNotBlank(colValue)) {
												if (!oldValue.equals(colValue)) {
													persistChangeRequestVO = hrisChangeRequestDAO
															.savePersist(hrisChangeRequest);
												}
											}
										} else {
											if (StringUtils.isNotBlank(colValue)) {
												persistChangeRequestVO = hrisChangeRequestDAO
														.savePersist(hrisChangeRequest);
											}
										}
										if (persistChangeRequestVO != null) {
											String forwardTo = "";

											Employee forwardToEmployee = null;

											List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeHRISReviewerDAO
													.findByCondition(empid, companyID);
											if (!employeeHRISReviewerList.isEmpty()) {
												for (EmployeeHRISReviewer employeeHRISReviewer : employeeHRISReviewerList) {
													HRISChangeRequestReviewer changeRequestReviewer = new HRISChangeRequestReviewer();
													changeRequestReviewer.setHrisChangeRequest(persistChangeRequestVO);
													changeRequestReviewer.setEmployeeReviewer(
															getDelegatedEmployee(employeeHRISReviewer
																	.getEmployeeReviewer().getEmployeeId()));
													changeRequestReviewer.setWorkFlowRuleMaster(
															employeeHRISReviewer.getWorkFlowRuleMaster());
													if (employeeHRISReviewer.getWorkFlowRuleMaster().getRuleValue()
															.equals("1")) {
														forwardTo = employeeHRISReviewer.getEmployeeReviewer()
																.getEmail();
														forwardToEmployee = employeeHRISReviewer.getEmployeeReviewer();
														changeRequestReviewer.setPending(true);
													} else {
														changeRequestReviewer.setPending(false);
													}

													hrisChangeRequestReviewerDAO.save(changeRequestReviewer);
												}

											}

											HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
											HRISChangeRequestWorkflow changeRequestWorkflow = new HRISChangeRequestWorkflow();
											changeRequestWorkflow.setHrisChangeRequest(persistChangeRequestVO);
											changeRequestWorkflow.setNewValue(colValue);
											changeRequestWorkflow.setForwardTo(forwardTo);
											changeRequestWorkflow.setHrisStatusMaster(hrisStatusMasterVO);
											changeRequestWorkflow
													.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
											changeRequestWorkflow.setEmployee(employeeVO);
											hrisChangeRequestWorkflowPersistObj = hrisChangeRequestWorkflowDAO
													.savePersist(changeRequestWorkflow);

											EmployeeListFormPage employeeListForm = generalLogic
													.getEmployeeHRISChangeRequestData(
															persistChangeRequestVO.getHrisChangeRequestId(),
															persistChangeRequestVO.getEmployee().getCompany()
																	.getCompanyId(),
															dynamicForm.getMetaData(), languageId);

											hrisMailLogic.sendEMailForHRISDataChange(companyID, persistChangeRequestVO,
													hrisChangeRequestWorkflowPersistObj,
													PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_APPLY,
													employeeVO, forwardToEmployee, employeeListForm);

										}

									}
								}

							} else {
								if (!existingColumn.isReadOnly()) {
									String tableRecordMethodName = PayAsiaConstants.SET_COL
											+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
									Method dynamicFormTableRecordMethod;
									try {
										dynamicFormTableRecordMethod = DynamicFormTableRecord.class
												.getMethod(tableRecordMethodName, String.class);

										dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

									} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
											| IllegalAccessException | InvocationTargetException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(e.getMessage(), e);
									}
								}

							}

						}

						dynamicFormTableRecordPK.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecordPK.setSequence(seqNo);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);
						seqNo++;
						fieldValue = maxTableRecordId.toString();
					}
				}

			}

			if (dynamicFormField.isApprovalRequired() != null && dynamicFormField.isReadOnly() != null
					&& dynamicFormField.isApprovalRequired() && !dynamicFormField.isReadOnly()) {
				HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO
						.findByCondition(dynamicFormField.getDictionaryId(), empid, hrisStatusList);
				if (hrisChangeRequestVO == null) {
					HRISChangeRequest persistChangeRequestVO = null;
					HRISChangeRequest hrisChangeRequest = new HRISChangeRequest();
					DataDictionary dataDictionary = dataDictionaryDAO.findById(field.getDictionaryId());
					hrisChangeRequest.setDataDictionary(dataDictionary);
					hrisChangeRequest.setEmployee(employeeVO);

					String getMethodName = "getCol"
							+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
					String oldValue;
					try {
						Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(getMethodName);
						try {
							oldValue = (String) dynamicFormRecordMethod.invoke(dynamicFormRecord);
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

					hrisChangeRequest.setOldValue(oldValue);
					hrisChangeRequest.setNewValue(fieldValue);
					hrisChangeRequest.setHrisStatusMaster(hrisStatusMasterVO);
					if (StringUtils.isNotBlank(oldValue)) {
						if (StringUtils.isNotBlank(fieldValue)) {
							if (!oldValue.equals(fieldValue)) {
								persistChangeRequestVO = hrisChangeRequestDAO.savePersist(hrisChangeRequest);
							}
						}
					} else {
						if (StringUtils.isNotBlank(fieldValue)) {
							persistChangeRequestVO = hrisChangeRequestDAO.savePersist(hrisChangeRequest);
						}
					}
					if (persistChangeRequestVO != null) {
						String forwardTo = "";
						Employee forwardToEmployee = null;

						List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeHRISReviewerDAO
								.findByCondition(empid, companyID);
						if (!employeeHRISReviewerList.isEmpty()) {
							for (EmployeeHRISReviewer employeeHRISReviewer : employeeHRISReviewerList) {
								HRISChangeRequestReviewer changeRequestReviewer = new HRISChangeRequestReviewer();
								changeRequestReviewer.setHrisChangeRequest(persistChangeRequestVO);
								changeRequestReviewer.setEmployeeReviewer(getDelegatedEmployee(
										employeeHRISReviewer.getEmployeeReviewer().getEmployeeId()));
								changeRequestReviewer
										.setWorkFlowRuleMaster(employeeHRISReviewer.getWorkFlowRuleMaster());
								if (employeeHRISReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
									forwardTo = employeeHRISReviewer.getEmployeeReviewer().getEmail();
									forwardToEmployee = employeeHRISReviewer.getEmployeeReviewer();
									changeRequestReviewer.setPending(true);
								} else {
									changeRequestReviewer.setPending(false);
								}

								hrisChangeRequestReviewerDAO.save(changeRequestReviewer);
							}

						}

						HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
						HRISChangeRequestWorkflow changeRequestWorkflow = new HRISChangeRequestWorkflow();
						changeRequestWorkflow.setHrisChangeRequest(persistChangeRequestVO);
						changeRequestWorkflow.setNewValue(fieldValue);
						changeRequestWorkflow.setForwardTo(forwardTo);
						changeRequestWorkflow.setHrisStatusMaster(hrisStatusMasterVO);
						changeRequestWorkflow.setEmployee(employeeVO);
						changeRequestWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
						hrisChangeRequestWorkflowPersistObj = hrisChangeRequestWorkflowDAO
								.savePersist(changeRequestWorkflow);

						EmployeeListFormPage employeeListFrmPage = generalLogic.getEmployeeHRISChangeRequestData(
								persistChangeRequestVO.getHrisChangeRequestId(),
								persistChangeRequestVO.getEmployee().getCompany().getCompanyId(),
								dynamicForm.getMetaData(), languageId);

						hrisMailLogic.sendEMailForHRISDataChange(companyID, persistChangeRequestVO,
								hrisChangeRequestWorkflowPersistObj,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_APPLY, employeeVO,
								forwardToEmployee, employeeListFrmPage);

					}
				}

			} else {

				/* isReadOnly noy null added by manoj */
				if (dynamicFormField.isReadOnly() != null && !dynamicFormField.isReadOnly()) {

					try {
						String methodName = PayAsiaConstants.SET_COL
								+ fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
						Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
						try {
							if (StringUtils.isNotBlank(fieldValue)) {
								dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
							}

						} catch (IllegalArgumentException illegalArgumentException) {
							LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
							throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
									illegalArgumentException);
						} catch (IllegalAccessException illegalAccessException) {
							LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
							throw new PayAsiaSystemException(illegalAccessException.getMessage(),
									illegalAccessException);
						} catch (InvocationTargetException invocationTargetException) {
							LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
							throw new PayAsiaSystemException(invocationTargetException.getMessage(),
									invocationTargetException);
						}
					} catch (SecurityException securityException) {
						LOGGER.error(securityException.getMessage(), securityException);
						throw new PayAsiaSystemException(securityException.getMessage(), securityException);
					} catch (NoSuchMethodException noSuchMethodException) {

						LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
						throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
					}
				}

			}

		}

		dynamicFormRecord.setCompany_ID(companyID);
		dynamicFormRecord.setEntity_ID(entityID);
		dynamicFormRecord.setForm_ID(formID);
		dynamicFormRecord.setVersion(version);
		dynamicFormRecord.setEntityKey(empid);

		DynamicFormRecord dynamicRecordVO = dynamicFormRecordDAO.saveReturn(dynamicFormRecord);
		employeeListFormPage.setDynamicFormRecordId(dynamicRecordVO.getRecordId());

		return employeeListFormPage;

	}

	@Override
	public EmployeeListFormPage saveViewProfileTableRecord(String tableXML, Long tabId, Long companyId, Long employeeId,
			Long formId, Integer version, Long entityKey, Long languageId) {

		Integer seqNo;
		Long entityId = null;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		Employee employeeVO = employeeDAO.findById(employeeId);

		HRISStatusMaster hrisStatusMasterVO = hrisStatusMasterDAO.findByName(PayAsiaConstants.HRIS_STATUS_SUBMITTED);

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();

		Company cmp = companyDAO.findById(companyId);
		String companyDateFormat = cmp.getDateFormat();
		DynamicFormRecord dynamicFormRecord = null;
		String fieldValue = null;

		if (tabId == 0) {
			dynamicFormRecord = new DynamicFormRecord();
			dynamicFormRecord.setForm_ID(formId);
			dynamicFormRecord.setVersion(version);
			dynamicFormRecord.setEntityKey(entityKey);
			dynamicFormRecord.setCompany_ID(companyId);
			dynamicFormRecord.setEntity_ID(entityId);

		} else {
			dynamicFormRecord = dynamicFormRecordDAO.findById(tabId);
		}

		DynamicFormTableRecord dynamicFormTableRecord;
		DynamicFormTableRecordPK dynamicFormTableRecordPK;

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException | SAXException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}
		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

		final StringReader xmlReader = new StringReader(tableXML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}
		boolean isDependentFieldUpdated = false;
		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();
			fieldValue = field.getValue();
			String fieldtype = field.getType();
			String fieldLabel = field.getLabel();

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				synchronized (this) {

					Long maxTableRecordId;
					if ("".equalsIgnoreCase(fieldValue)) {

						maxTableRecordId = dynamicFormTableRecordDAO.getMaxTableRecordId() + 1;
						seqNo = 1;
					} else {
						maxTableRecordId = Long.parseLong(fieldValue);
						Integer maxSeqNo = dynamicFormTableRecordDAO.getMaxSequenceNumber(maxTableRecordId);
						if (maxSeqNo != 0) {
							seqNo = maxSeqNo + 1;
						} else {
							seqNo = 1;
						}
					}

					fieldValue = null;

					List<Row> listOfRows = field.getRow();
					for (Row row : listOfRows) {
						dynamicFormTableRecord = new DynamicFormTableRecord();
						dynamicFormTableRecordPK = new DynamicFormTableRecordPK();

						Class<?> dynamicFormTableRecordClass = dynamicFormTableRecord.getClass();

						List<RowValue> listOfRowvalue = row.getRowValue();

						for (RowValue rowValue : listOfRowvalue) {
							DataDictionary fieldDataDictionary = dataDictionaryDAO.findById(rowValue.getDictionaryId());
							if (fieldDataDictionary == null
									&& rowValue.getName().equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								continue;
							}
							DynamicForm dynamicForm = null;
							if (formIdMap.containsKey(fieldDataDictionary.getFormID())) {
								dynamicForm = formIdMap.get(fieldDataDictionary.getFormID());
							} else {
								dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
										fieldDataDictionary.getEntityMaster().getEntityId(),
										fieldDataDictionary.getFormID());
								formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
							}

							String colName = rowValue.getName();
							String colValue;

							Column existingColumn = getExistingDynamicFormColumnInfo(dynamicForm.getMetaData(),
									rowValue.getDictionaryId());

							try {
								colValue = URLDecoder.decode(rowValue.getValue(), "UTF8");
							} catch (UnsupportedEncodingException unsupportedEncodingException) {
								LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
								throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
										unsupportedEncodingException);
							}

							String colType = rowValue.getType();

							if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
								colValue = DateUtils.appendTodayTime(colValue);

							} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !"".equalsIgnoreCase(colValue)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
							}

							if (existingColumn.isApprovalRequired() != null && existingColumn.isReadOnly() != null
									&& existingColumn.isApprovalRequired() && !existingColumn.isReadOnly()) {
								List<EmployeeHRISReviewer> hrisReviewerList = employeeHRISReviewerDAO
										.findByCondition(employeeId, companyId);
								if (hrisReviewerList == null) {
									employeeListFormPage.setMessage("noreviewer");
									return employeeListFormPage;
								} else {
									HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO
											.findByConditionTableSeq(existingColumn.getDictionaryId(), employeeId,
													hrisStatusList, seqNo);
									if (hrisChangeRequestVO == null) {
										HRISChangeRequest persistChangeRequestVO = null;
										HRISChangeRequest hrisChangeRequest = new HRISChangeRequest();
										DataDictionary dataDictionary = dataDictionaryDAO
												.findById(rowValue.getDictionaryId());
										hrisChangeRequest.setDataDictionary(dataDictionary);
										hrisChangeRequest.setEmployee(employeeVO);

										String tableRecordGetMethodName = PayAsiaConstants.GET_COL
												+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
										String oldValue;
										Method dynamicFormTableRecordMethod;
										try {
											dynamicFormTableRecordMethod = dynamicFormTableRecordClass
													.getMethod(tableRecordGetMethodName);
											try {
												oldValue = (String) dynamicFormTableRecordMethod
														.invoke(dynamicFormTableRecord);
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
										hrisChangeRequest.setTableRecordSequence(seqNo);
										hrisChangeRequest.setOldValue(oldValue);
										hrisChangeRequest.setNewValue(colValue);
										hrisChangeRequest.setHrisStatusMaster(hrisStatusMasterVO);
										if (StringUtils.isNotBlank(oldValue)) {
											if (StringUtils.isNotBlank(colValue)) {
												if (!oldValue.equals(colValue)) {
													persistChangeRequestVO = hrisChangeRequestDAO
															.savePersist(hrisChangeRequest);
												}
											}
										} else {
											if (StringUtils.isNotBlank(colValue)) {
												persistChangeRequestVO = hrisChangeRequestDAO
														.savePersist(hrisChangeRequest);
											}
										}
										if (persistChangeRequestVO != null) {
											String forwardTo = "";

											Employee forwardToEmployee = null;

											List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeHRISReviewerDAO
													.findByCondition(employeeId, companyId);
											if (!employeeHRISReviewerList.isEmpty()) {
												for (EmployeeHRISReviewer employeeHRISReviewer : employeeHRISReviewerList) {
													HRISChangeRequestReviewer changeRequestReviewer = new HRISChangeRequestReviewer();
													changeRequestReviewer.setHrisChangeRequest(persistChangeRequestVO);
													changeRequestReviewer.setEmployeeReviewer(
															getDelegatedEmployee(employeeHRISReviewer
																	.getEmployeeReviewer().getEmployeeId()));
													changeRequestReviewer.setWorkFlowRuleMaster(
															employeeHRISReviewer.getWorkFlowRuleMaster());
													if (employeeHRISReviewer.getWorkFlowRuleMaster().getRuleValue()
															.equals("1")) {
														forwardTo = employeeHRISReviewer.getEmployeeReviewer()
																.getEmail();
														forwardToEmployee = employeeHRISReviewer.getEmployeeReviewer();
														changeRequestReviewer.setPending(true);
													} else {
														changeRequestReviewer.setPending(false);
													}

													hrisChangeRequestReviewerDAO.save(changeRequestReviewer);
												}

											}

											HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
											HRISChangeRequestWorkflow changeRequestWorkflow = new HRISChangeRequestWorkflow();
											changeRequestWorkflow.setHrisChangeRequest(persistChangeRequestVO);
											changeRequestWorkflow.setNewValue(colValue);
											changeRequestWorkflow.setForwardTo(forwardTo);
											changeRequestWorkflow.setHrisStatusMaster(hrisStatusMasterVO);
											changeRequestWorkflow
													.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
											changeRequestWorkflow.setEmployee(employeeVO);
											hrisChangeRequestWorkflowPersistObj = hrisChangeRequestWorkflowDAO
													.savePersist(changeRequestWorkflow);

											EmployeeListFormPage employeeListForm = generalLogic
													.getEmployeeHRISChangeRequestData(
															persistChangeRequestVO.getHrisChangeRequestId(),
															persistChangeRequestVO.getEmployee().getCompany()
																	.getCompanyId(),
															dynamicForm.getMetaData(), languageId);

											hrisMailLogic.sendEMailForHRISDataChange(companyId, persistChangeRequestVO,
													hrisChangeRequestWorkflowPersistObj,
													PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_APPLY,
													employeeVO, forwardToEmployee, employeeListForm);

										}

									}
								}

							} else {
								if (!existingColumn.isReadOnly()) {
									String tableRecordMethodName = PayAsiaConstants.SET_COL
											+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
									Method dynamicFormTableRecordMethod;
									try {
										dynamicFormTableRecordMethod = DynamicFormTableRecord.class
												.getMethod(tableRecordMethodName, String.class);

										dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

									} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
											| IllegalAccessException | InvocationTargetException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(e.getMessage(), e);
									}
								}

							}

						}

						fieldValue = maxTableRecordId.toString();
						dynamicFormTableRecordPK.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecordPK.setSequence(seqNo);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);

					}
				}

			}
			if (fieldLabel.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				isDependentFieldUpdated = true;
			}
			try {
				String methodName = "setCol" + fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());
				Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
				try {
					if (StringUtils.isNotBlank(fieldValue)) {
						dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
					}
				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
					throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
					throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
					throw new PayAsiaSystemException(invocationTargetException.getMessage(), invocationTargetException);
				}
			} catch (SecurityException securityException) {
				LOGGER.error(securityException.getMessage(), securityException);
				throw new PayAsiaSystemException(securityException.getMessage(), securityException);
			} catch (NoSuchMethodException noSuchMethodException) {

				LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
				throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
			}

		}
		if (tabId == 0) {
			DynamicFormRecord dynamicRecordVO = dynamicFormRecordDAO.saveReturn(dynamicFormRecord);
			employeeListFormPage.setDynamicFormRecordId(dynamicRecordVO.getRecordId());
			employeeListFormPage.setDynamicFormTableRecordId(Long.parseLong(fieldValue));
			employeeListFormPage.setMode("SAVE");

		} else {
			employeeListFormPage.setDynamicFormTableRecordId(Long.parseLong(fieldValue));
			dynamicFormRecordDAO.update(dynamicFormRecord);
			employeeListFormPage.setMode("UPDATE");
		}

		// Update Leave Entitlement for 1. Child Care Leave
		// type ,
		// 2. Extended Child Care Leave type i.e. when any
		// dependents
		// details will inserted.
		if (isDependentFieldUpdated) {
			if (cmp.getCountryMaster().getCountryName().equalsIgnoreCase("Singapore")) {
				String leaveSchemeTypeIds = generalLogic.getChildCareLeaveTypeInfo(companyId, entityKey);

				leaveSchemeTypeDAO.childCareLeaveEntitlementProc(companyId, entityKey, leaveSchemeTypeIds);
			}

		}

		return employeeListFormPage;

	}

	@Override
	public EmployeeListFormPage updateViewProfileTableRecord(String tableXML, Long tabId, Long companyId,
			Long employeeId, Integer seqNo, Long languageId) {

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		Company cmp = companyDAO.findById(companyId);
		String companyDateFormat = cmp.getDateFormat();

		Employee employeeVO = employeeDAO.findById(employeeId);

		HRISStatusMaster hrisStatusMasterVO = hrisStatusMasterDAO.findByName(PayAsiaConstants.HRIS_STATUS_SUBMITTED);

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException | SAXException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		final StringReader xmlReader = new StringReader(tableXML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldValue = field.getValue();
			String fieldtype = field.getType();

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				synchronized (this) {

					Long maxTableRecordId = Long.parseLong(fieldValue);

					List<Row> listOfRows = field.getRow();
					for (Row row : listOfRows) {
						DynamicFormTableRecord dynamicFormTableRecord = dynamicFormTableRecordDAO
								.findByIdAndSeq(maxTableRecordId, seqNo);
						Class<?> dynamicFormTableRecordClass = dynamicFormTableRecord.getClass();
						List<RowValue> listOfRowvalue = row.getRowValue();

						for (RowValue rowValue : listOfRowvalue) {

							DataDictionary fieldDataDictionary = dataDictionaryDAO.findById(rowValue.getDictionaryId());
							if (fieldDataDictionary == null
									&& rowValue.getName().equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								continue;
							}
							DynamicForm dynamicForm = null;
							if (fieldDataDictionary != null && formIdMap.containsKey(fieldDataDictionary.getFormID())) {
								dynamicForm = formIdMap.get(fieldDataDictionary.getFormID());
							} else {
								dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
										fieldDataDictionary.getEntityMaster().getEntityId(),
										fieldDataDictionary.getFormID());
								formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
							}

							String colName = rowValue.getName();
							String colValue;

							Column existingColumn = getExistingDynamicFormColumnInfo(dynamicForm.getMetaData(),
									rowValue.getDictionaryId());
							try {
								colValue = URLDecoder.decode(rowValue.getValue(), "UTF8");
							} catch (UnsupportedEncodingException unsupportedEncodingException) {
								LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
								throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
										unsupportedEncodingException);
							}

							String colType = rowValue.getType();

							if (colName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
								colValue = DateUtils.appendTodayTime(colValue);

							} else if (colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !"".equalsIgnoreCase(colValue)) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(colValue, companyDateFormat);
								int isValid = ValidationUtils.validateDate(colValue,
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								if (isValid == 1) {
									throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_DATE_RANGE_NOT_VALID);
								}
							}
							if (existingColumn.isApprovalRequired() != null && existingColumn.isReadOnly() != null
									&& existingColumn.isApprovalRequired() && !existingColumn.isReadOnly()) {
								List<EmployeeHRISReviewer> hrisReviewerList = employeeHRISReviewerDAO
										.findByCondition(employeeId, companyId);
								if (hrisReviewerList == null) {
									employeeListFormPage.setMessage("noreviewer");
									return employeeListFormPage;
								} else {
									HRISChangeRequest hrisChangeRequestVO = hrisChangeRequestDAO
											.findByConditionTableSeq(existingColumn.getDictionaryId(), employeeId,
													hrisStatusList, seqNo);
									if (hrisChangeRequestVO == null) {
										HRISChangeRequest persistChangeRequestVO = null;
										HRISChangeRequest hrisChangeRequest = new HRISChangeRequest();
										DataDictionary dataDictionary = dataDictionaryDAO
												.findById(rowValue.getDictionaryId());
										hrisChangeRequest.setDataDictionary(dataDictionary);
										hrisChangeRequest.setEmployee(employeeVO);

										String tableRecordGetMethodName = PayAsiaConstants.GET_COL
												+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
										String oldValue;
										Method dynamicFormTableRecordMethod;
										try {
											dynamicFormTableRecordMethod = dynamicFormTableRecordClass
													.getMethod(tableRecordGetMethodName);
											try {
												oldValue = (String) dynamicFormTableRecordMethod
														.invoke(dynamicFormTableRecord);
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
										hrisChangeRequest.setTableRecordSequence(seqNo);
										hrisChangeRequest.setOldValue(oldValue);
										hrisChangeRequest.setNewValue(colValue);
										hrisChangeRequest.setHrisStatusMaster(hrisStatusMasterVO);
										if (StringUtils.isNotBlank(oldValue)) {
											if (StringUtils.isNotBlank(colValue)) {
												if (!oldValue.equals(colValue)) {
													persistChangeRequestVO = hrisChangeRequestDAO
															.savePersist(hrisChangeRequest);
												}
											}
										} else {
											if (StringUtils.isNotBlank(colValue)) {
												persistChangeRequestVO = hrisChangeRequestDAO
														.savePersist(hrisChangeRequest);
											}
										}
										if (persistChangeRequestVO != null) {
											String forwardTo = "";

											Employee forwardToEmployee = null;

											List<EmployeeHRISReviewer> employeeHRISReviewerList = employeeHRISReviewerDAO
													.findByCondition(employeeId, companyId);
											if (!employeeHRISReviewerList.isEmpty()) {
												for (EmployeeHRISReviewer employeeHRISReviewer : employeeHRISReviewerList) {
													HRISChangeRequestReviewer changeRequestReviewer = new HRISChangeRequestReviewer();
													changeRequestReviewer.setHrisChangeRequest(persistChangeRequestVO);
													changeRequestReviewer.setEmployeeReviewer(
															getDelegatedEmployee(employeeHRISReviewer
																	.getEmployeeReviewer().getEmployeeId()));
													changeRequestReviewer.setWorkFlowRuleMaster(
															employeeHRISReviewer.getWorkFlowRuleMaster());
													if (employeeHRISReviewer.getWorkFlowRuleMaster().getRuleValue()
															.equals("1")) {
														forwardTo = employeeHRISReviewer.getEmployeeReviewer()
																.getEmail();
														forwardToEmployee = employeeHRISReviewer.getEmployeeReviewer();
														changeRequestReviewer.setPending(true);
													} else {
														changeRequestReviewer.setPending(false);
													}

													hrisChangeRequestReviewerDAO.save(changeRequestReviewer);
												}

											}

											HRISChangeRequestWorkflow hrisChangeRequestWorkflowPersistObj;
											HRISChangeRequestWorkflow changeRequestWorkflow = new HRISChangeRequestWorkflow();
											changeRequestWorkflow.setHrisChangeRequest(persistChangeRequestVO);
											changeRequestWorkflow.setNewValue(colValue);
											changeRequestWorkflow.setForwardTo(forwardTo);
											changeRequestWorkflow.setHrisStatusMaster(hrisStatusMasterVO);
											changeRequestWorkflow
													.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
											changeRequestWorkflow.setEmployee(employeeVO);
											hrisChangeRequestWorkflowPersistObj = hrisChangeRequestWorkflowDAO
													.savePersist(changeRequestWorkflow);

											EmployeeListFormPage employeeListForm = generalLogic
													.getEmployeeHRISChangeRequestData(
															persistChangeRequestVO.getHrisChangeRequestId(),
															persistChangeRequestVO.getEmployee().getCompany()
																	.getCompanyId(),
															dynamicForm.getMetaData(), languageId);

											hrisMailLogic.sendEMailForHRISDataChange(companyId, persistChangeRequestVO,
													hrisChangeRequestWorkflowPersistObj,
													PayAsiaConstants.PAYASIA_SUB_CATEGORY_HRIS_DATA_CHANGE_APPLY,
													employeeVO, forwardToEmployee, employeeListForm);

										}

									}
								}

							} else {
								if (!existingColumn.isReadOnly()) {
									String tableRecordMethodName = PayAsiaConstants.SET_COL
											+ colName.substring(colName.lastIndexOf('_') + 1, colName.length());
									Method dynamicFormTableRecordMethod;
									try {
										dynamicFormTableRecordMethod = DynamicFormTableRecord.class
												.getMethod(tableRecordMethodName, String.class);

										dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);

									} catch (SecurityException | NoSuchMethodException | IllegalArgumentException
											| IllegalAccessException | InvocationTargetException e) {
										LOGGER.error(e.getMessage(), e);
										throw new PayAsiaSystemException(e.getMessage(), e);
									}
								}

							}

						}

						fieldValue = maxTableRecordId.toString();
						dynamicFormTableRecordDAO.update(dynamicFormTableRecord);

					}
				}

			}
		}

		return employeeListFormPage;

	}

	private String addChangeRequestIdInExistingDynXML(DynamicFormRecord dynamicFormRecord, String XML, Long employeeId,
			Long companyId, String companyDateFormat, String mode) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		boolean isEnableEmployeeChangeWorkflow = false;
		if (hrisPreferenceVO == null) {
			isEnableEmployeeChangeWorkflow = false;
		} else if (hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = true;
		} else if (!hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = false;
		}

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = XMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jAXBException) {
				LOGGER.error(jAXBException.getMessage(), jAXBException);
				throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
			} catch (SAXException sAXException) {

				LOGGER.error(sAXException.getMessage(), sAXException);
				throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
			}
			final StringReader xmlReader = new StringReader(XML);
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(), e1);
			}

			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			streamWriter = outputFactory.createXMLStreamWriter(byteArrayOutputStream);

			Tab tab = null;
			try {
				tab = (Tab) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jAXBException) {
				LOGGER.error(jAXBException.getMessage(), jAXBException);
				throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
			}

			List<Field> listOfFields = tab.getField();
			for (Field field : listOfFields) {
				String fieldType = field.getType();
				String value = field.getValue();

				if (!isEnableEmployeeChangeWorkflow) {
					field.setReadOnly(true);
					field.setChangeRequestId(0l);

				} else {
					HRISChangeRequest hrisChangeRequest = hrisChangeRequestDAO.findByCondition(field.getDictionaryId(),
							employeeId, hrisStatusList);
					if (hrisChangeRequest != null) {
						field.setChangeRequestId(hrisChangeRequest.getHrisChangeRequestId());

						value = hrisChangeRequest.getNewValue();
						if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
								&& !"".equalsIgnoreCase(value)) {
							value = DateUtils.convertDateToSpecificFormat(value, companyDateFormat);
						}

						try {
							value = URLEncoder.encode(value, "UTF8");
						} catch (UnsupportedEncodingException unsupportedEncodingException) {
							LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
							throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
									unsupportedEncodingException);
						}
						if (!fieldType.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
							field.setValue(value);
						}

					} else {
						field.setChangeRequestId(0l);
					}
				}

				List<Column> columnList = field.getColumn();
				for (Column column : columnList) {

					if (!isEnableEmployeeChangeWorkflow) {
						column.setReadOnly(true);
						column.setChangeRequestId(0l);

					} else {
						HRISChangeRequest hrisChangeRequestForColumn = hrisChangeRequestDAO
								.findByCondition(column.getDictionaryId(), employeeId, hrisStatusList);
						if (hrisChangeRequestForColumn != null) {
							column.setChangeRequestId(hrisChangeRequestForColumn.getHrisChangeRequestId());

							value = hrisChangeRequestForColumn.getNewValue();
							if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !"".equalsIgnoreCase(value)) {
								value = DateUtils.convertDateToSpecificFormat(value, companyDateFormat);
							}

							if (fieldType.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
									&& !"".equalsIgnoreCase(value)) {
								DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
										.findById(Long.parseLong(value));

								try {
									String codeDescription = dynamicFormFieldRefValue.getDescription() + "["
											+ dynamicFormFieldRefValue.getCode() + "]";
									value = codeDescription;
								} catch (Exception exception) {
									LOGGER.error(exception.getMessage(), exception);
									value = "";
								}
							}

							try {
								value = URLEncoder.encode(value, "UTF8");
							} catch (UnsupportedEncodingException unsupportedEncodingException) {
								LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
								throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
										unsupportedEncodingException);
							}

						} else {
							column.setChangeRequestId(0l);
						}
					}
				}

			}
			Marshaller m = XMLUtil.getDocumentMarshaller();
			m.marshal(tab, streamWriter);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (XMLStreamException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return byteArrayOutputStream.toString();
	}

	@Override
	public EmployeeListFormPage getViewProfileUpdatedXmls(Long loggedInEmployeeId, long empID, Long companyId,
			Long languageId, String mode) {

		Long entityId = null;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		Company cmp = companyDAO.findById(companyId);

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		EmployeeListForm employeeListForm = null;
		List<EmployeeListForm> tabDataList = new ArrayList<EmployeeListForm>();

		List<Long> formIdList = generalLogic.getEmployeeAuthorizedSectionIdList(loggedInEmployeeId, companyId,
				entityId);

		int dynamicFormCount = 0;
		int tabNo = 1;
		for (Long dynamicFormId : formIdList) {
			employeeListForm = new EmployeeListForm();
			EmployeeDynamicFormDTO employeeDynamicFormDTO = new EmployeeDynamicFormDTO();
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId, entityId,
					formIdList.get(dynamicFormCount));
			employeeListForm.setCmpID(dynamicForm.getId().getCompany_ID());
			employeeDynamicFormDTO.setEntityID(dynamicForm.getId().getEntity_ID());

			/* ID ENCRYPT */
			employeeDynamicFormDTO.setFormId(FormatPreserveCryptoUtil.encrypt(dynamicForm.getId().getFormId()));

			employeeDynamicFormDTO.setVersion(dynamicForm.getId().getVersion());

			if (tabNo == 1) {
				DynamicFormRecord empRecord = dynamicFormRecordDAO.getEmpRecords(empID,
						dynamicForm.getId().getVersion(), dynamicFormId, entityId, null);
				if (empRecord != null) {

					/* ID ENCRYPT */
					employeeDynamicFormDTO.setTabID(FormatPreserveCryptoUtil.encrypt(empRecord.getRecordId()));

					employeeDynamicFormDTO.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
							updateXML(empRecord, dynamicForm.getMetaData(), cmp.getDateFormat(), "mode", companyId),
							languageId, companyId, entityId, dynamicForm.getId().getFormId()));
				} else {
					employeeDynamicFormDTO
							.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
									updateXMLWithDefaultValue(dynamicForm.getMetaData(), cmp.getDateFormat(),
											"viewMode", companyId),
									languageId, companyId, entityId, dynamicForm.getId().getFormId()));
				}
				employeeDynamicFormDTO.setXmlString(
						addChangeRequestIdInExistingDynXML(empRecord, employeeDynamicFormDTO.getXmlString(),
								loggedInEmployeeId, companyId, cmp.getDateFormat(), "notEditMode"));
				employeeDynamicFormDTO.setXmlString(addFormulaValueInExistingDynXML(
						employeeDynamicFormDTO.getXmlString(), loggedInEmployeeId, companyId));
				employeeDynamicFormDTO.setXmlString(addReferenceValueInExistingDynXML(
						employeeDynamicFormDTO.getXmlString(), empID, companyId, cmp.getDateFormat()));

			}

			employeeDynamicFormDTO.setTabName(multilingualLogic.convertSectionNameToSpecificLanguage(
					dynamicForm.getTabName(), languageId, companyId, entityId, dynamicForm.getId().getFormId()));
			employeeListForm.setEmployeeDynamicFormDTO(employeeDynamicFormDTO);
			tabDataList.add(employeeListForm);
			dynamicFormCount++;
			tabNo++;
		}

		employeeListFormPage.setEmployeeListFrom(tabDataList);
		Employee emp = employeeDAO.findById(empID);
		try {
			employeeListFormPage.setEmpId(URLEncoder.encode(emp.getEmployeeNumber(), "UTF8"));
			employeeListFormPage.setFirstName(URLEncoder.encode(emp.getFirstName(), "UTF8"));
			employeeListFormPage
					.setMiddleName(emp.getMiddleName() == null ? "" : URLEncoder.encode(emp.getMiddleName(), "UTF8"));

			employeeListFormPage
					.setLastName(emp.getLastName() == null ? "" : URLEncoder.encode(emp.getLastName(), "UTF8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		employeeListFormPage.setEmploymentStatus(emp.getEmploymentStatus() == null ? "" : emp.getEmploymentStatus());

		employeeListFormPage
				.setHireDate(emp.getHireDate() == null ? "" : DateUtils.timeStampToString(emp.getHireDate()));
		employeeListFormPage.setConfirmationDate(
				emp.getConfirmationDate() == null ? "" : DateUtils.timeStampToString(emp.getConfirmationDate()));
		employeeListFormPage.setOriginalHireDate(
				emp.getOriginalHireDate() == null ? "" : DateUtils.timeStampToString(emp.getOriginalHireDate()));
		employeeListFormPage.setResignationDate(
				emp.getResignationDate() == null ? "" : DateUtils.timeStampToString(emp.getResignationDate()));

		employeeListFormPage.setEmail(emp.getEmail());
		// employeeListFormPage.setEmployees(generalLogic.returnEmployeesList(loggedInEmployeeId,
		// companyId));

		return employeeListFormPage;

	}

	@Override
	public EmployeeListFormPage getViewProfileXML(Long empID, Long companyId, Long languageId, Long formId) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		Long entityId = null;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}

		Company cmp = companyDAO.findById(companyId);

		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId, entityId, formId);

		if (dynamicForm != null) {

			DynamicFormRecord empRecord = dynamicFormRecordDAO.getEmpRecords(empID, dynamicForm.getId().getVersion(),
					formId, entityId, null);

			if (empRecord != null) {

				/* ID ENCRYPT */
				employeeListFormPage.setTabId(FormatPreserveCryptoUtil.encrypt(empRecord.getRecordId()));
				employeeListFormPage.setXmlString(multilingualLogic.convertLabelsToSpecificLanguage(
						updateXML(empRecord, dynamicForm.getMetaData(), cmp.getDateFormat(), "mode", companyId),
						languageId, companyId, entityId, dynamicForm.getId().getFormId()));
			} else {
				employeeListFormPage
						.setXmlString(
								multilingualLogic.convertLabelsToSpecificLanguage(
										updateXMLWithDefaultValue(dynamicForm.getMetaData(), cmp.getDateFormat(),
												"viewMode", companyId),
										languageId, companyId, entityId, dynamicForm.getId().getFormId()));
			}

			employeeListFormPage.setXmlString(addChangeRequestIdInExistingDynXML(empRecord,
					employeeListFormPage.getXmlString(), empID, companyId, cmp.getDateFormat(), "notEditMode"));
			employeeListFormPage.setXmlString(
					addFormulaValueInExistingDynXML(employeeListFormPage.getXmlString(), empID, companyId));
			employeeListFormPage.setXmlString(addReferenceValueInExistingDynXML(employeeListFormPage.getXmlString(),
					empID, companyId, cmp.getDateFormat()));
		}
		return employeeListFormPage;
	}

	@Override
	public EmployeeListFormPage getEmpChangeRequestData(Long loggedInEmployeeId, Long companyId,
			Long hrisChangeRequestId, Long languageId) {
		HRISChangeRequest hrisChangeRequest = hrisChangeRequestDAO.findById(hrisChangeRequestId);
		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
				hrisChangeRequest.getDataDictionary().getEntityMaster().getEntityId(),
				hrisChangeRequest.getDataDictionary().getFormID());

		EmployeeListFormPage employeeListFormPage = generalLogic.getEmployeeHRISChangeRequestData(hrisChangeRequestId,
				companyId, dynamicForm.getMetaData(), languageId);
		return employeeListFormPage;

	}

	@Override
	public void updateEmpNewValueInExistingXML(Long hrisChangeRequestId, Long companyId) {
		HRISChangeRequest hrisChangeRequest = hrisChangeRequestDAO.findById(hrisChangeRequestId);
		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
				hrisChangeRequest.getDataDictionary().getEntityMaster().getEntityId(),
				hrisChangeRequest.getDataDictionary().getFormID());

		Field existingField = generalLogic.getExistDynamicFormFieldInfo(dynamicForm.getMetaData(),
				hrisChangeRequest.getDataDictionary().getDataDictionaryId());

		DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO.getEmpRecords(
				hrisChangeRequest.getEmployee().getEmployeeId(), dynamicForm.getId().getVersion(),
				dynamicForm.getId().getFormId(), dynamicForm.getId().getEntity_ID(), companyId);

		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
		if (!existingField.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
			String fieldValue = hrisChangeRequest.getNewValue();

			String methodName = "setCol" + existingField.getName()
					.substring(existingField.getName().lastIndexOf('_') + 1, existingField.getName().length());
			Method dynamicFormRecordMethod = null;
			try {
				dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
			} catch (NoSuchMethodException | SecurityException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}

			try {
				dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
			dynamicFormRecordDAO.update(dynamicFormRecord);
		}

		if (existingField.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
			synchronized (this) {
				String getMethodName = "getCol" + existingField.getName()
						.substring(existingField.getName().lastIndexOf('_') + 1, existingField.getName().length());
				String recordId = "";

				Method dynamicFormRecordMethod = null;
				try {
					dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(getMethodName);
				} catch (NoSuchMethodException | SecurityException e) {
					LOGGER.error(e.getMessage(), e);
					throw new PayAsiaSystemException(e.getMessage(), e);
				}

				try {
					recordId = (String) dynamicFormRecordMethod.invoke(dynamicFormRecord);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					LOGGER.error(e.getMessage(), e);
					throw new PayAsiaSystemException(e.getMessage(), e);
				}
				Long maxTableRecordId = Long.parseLong(recordId);

				DynamicFormTableRecord dynamicFormTableRecord = dynamicFormTableRecordDAO
						.findByIdAndSeqForHrisManager(maxTableRecordId, hrisChangeRequest.getTableRecordSequence());

				List<Column> listOfCols = existingField.getColumn();
				for (Column column : listOfCols) {
					if (column.getDictionaryId().equals(hrisChangeRequest.getDataDictionary().getDataDictionaryId())) {
						String colValue = hrisChangeRequest.getNewValue();

						String tableRecordMethodName = PayAsiaConstants.SET_COL + column.getName()
								.substring(column.getName().lastIndexOf('_') + 1, column.getName().length());
						Method dynamicFormTableRecordMethod = null;

						try {
							dynamicFormTableRecordMethod = DynamicFormTableRecord.class.getMethod(tableRecordMethodName,
									String.class);
						} catch (NoSuchMethodException | SecurityException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}

						try {
							dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}
						dynamicFormTableRecordDAO.update(dynamicFormTableRecord);
					}
				}

			}
		}
	}

	@Override
	public EmployeeListFormPage saveDocTableRecord(DynamicFormTableDocumentDTO dynamicFormDocumentDTO, Long companyId,
			Long loggedInEmpId) {

		/*
		 * String filePath = downloadPath + "/company" + "/" + companyId +
		 * PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_INFO_DOCUMENT + "/" +
		 * dynamicFormDocumentDTO.getEntityKey() + "/";
		 */
		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_INFO_DOCUMENT, null, null,
				String.valueOf(dynamicFormDocumentDTO.getEntityKey()), null, PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT,
				0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		String fileName = uploadEmpDocFile(dynamicFormDocumentDTO.getUploadFile(), filePath, "_");
		String fileNameTemp = dynamicFormDocumentDTO.getUploadFile().getOriginalFilename();
		String ext = fileNameTemp.substring(fileNameTemp.lastIndexOf(".") + 1, fileNameTemp.length());
		String fileNameStr = fileNameTemp.substring(0, fileNameTemp.lastIndexOf("."));

		Integer seqNo;
		Long entityId = null;
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
				entityId = getEntityMasterId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME, entityMasterList);
			}
		}
		Employee employeeVO = employeeDAO.findById(loggedInEmpId);
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		DynamicFormRecord dynamicFormRecord = null;
		String fieldValue = null;

		if (dynamicFormDocumentDTO.getTabId() == 0 || dynamicFormDocumentDTO.getTabId() == null) {
			dynamicFormRecord = new DynamicFormRecord();
			dynamicFormRecord.setForm_ID(dynamicFormDocumentDTO.getFormId());
			dynamicFormRecord.setVersion(dynamicFormDocumentDTO.getVersion());
			dynamicFormRecord.setEntityKey(Long.parseLong(dynamicFormDocumentDTO.getEntityKey()));
			dynamicFormRecord.setCompany_ID(companyId);
			dynamicFormRecord.setEntity_ID(entityId);

		} else {
			dynamicFormRecord = dynamicFormRecordDAO.findById(dynamicFormDocumentDTO.getTabId());
		}

		DynamicFormTableRecord dynamicFormTableRecord;
		DynamicFormTableRecordPK dynamicFormTableRecordPK;

		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

		synchronized (this) {
			Long maxTableRecordId;
			if (dynamicFormDocumentDTO.getTableRecordId() == null) {
				maxTableRecordId = dynamicFormTableRecordDAO.getMaxTableRecordId() + 1;
				seqNo = 1;
			} else {
				maxTableRecordId = dynamicFormDocumentDTO.getTableRecordId();
				Integer maxSeqNo = dynamicFormTableRecordDAO.getMaxSequenceNumber(maxTableRecordId);
				if (maxSeqNo != 0) {
					seqNo = maxSeqNo + 1;
				} else {
					seqNo = 1;
				}
			}

			fieldValue = null;

			dynamicFormTableRecord = new DynamicFormTableRecord();
			dynamicFormTableRecordPK = new DynamicFormTableRecordPK();

			String documentName = "";
			try {
				documentName = URLDecoder.decode(dynamicFormDocumentDTO.getDocumentName(), "UTF8");
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
				throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			if (StringUtils.isBlank(dynamicFormDocumentDTO.getDocumentName())) {
				documentName = fileNameStr;
			}
			setTableDocCustomCol(9, documentName, dynamicFormTableRecord);

			String documentType = "";
			try {
				documentType = URLDecoder.decode(dynamicFormDocumentDTO.getDocumentType(), "UTF8");
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
				throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			if (StringUtils.isBlank(dynamicFormDocumentDTO.getDocumentType())) {
				documentType = ext;
			}
			setTableDocCustomCol(2, documentType, dynamicFormTableRecord);

			String documentDescription = "";
			try {
				documentDescription = URLDecoder.decode(dynamicFormDocumentDTO.getDocumentDescription(), "UTF8");
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
				throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			setTableDocCustomCol(3, documentDescription, dynamicFormTableRecord);

			setTableDocCustomCol(4, fileName, dynamicFormTableRecord);

			String uploadDate = DateUtils.timeStampToString(DateUtils.getCurrentTimestamp());
			setTableDocCustomCol(5, uploadDate, dynamicFormTableRecord);

			String uploadBy = getEmployeeName(employeeVO);
			setTableDocCustomCol(6, uploadBy, dynamicFormTableRecord);
			String owner = dynamicFormDocumentDTO.getOwner();
			setTableDocCustomCol(7, owner, dynamicFormTableRecord);
			String visibleToEmp;
			if ("Employee".equalsIgnoreCase(dynamicFormDocumentDTO.getOwner())) {
				visibleToEmp = "true";
			} else {
				if (dynamicFormDocumentDTO.isVisibleToEmployee() == true) {
					visibleToEmp = "true";
				} else {
					visibleToEmp = "false";
				}
			}

			setTableDocCustomCol(8, visibleToEmp, dynamicFormTableRecord);

			fieldValue = maxTableRecordId.toString();
			dynamicFormTableRecordPK.setDynamicFormTableRecordId(maxTableRecordId);
			dynamicFormTableRecordPK.setSequence(seqNo);
			dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
			dynamicFormTableRecordDAO.save(dynamicFormTableRecord);

		}

		try {
			String methodName = "setCol" + dynamicFormDocumentDTO.getTableName().substring(
					dynamicFormDocumentDTO.getTableName().lastIndexOf("_") + 1,
					dynamicFormDocumentDTO.getTableName().length());
			Method dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(methodName, String.class);
			try {
				if (StringUtils.isNotBlank(fieldValue)) {
					dynamicFormRecordMethod.invoke(dynamicFormRecord, fieldValue);
				}
			} catch (IllegalArgumentException illegalArgumentException) {
				LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
				throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
			} catch (IllegalAccessException illegalAccessException) {
				LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
				throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
			} catch (InvocationTargetException invocationTargetException) {
				LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
				throw new PayAsiaSystemException(invocationTargetException.getMessage(), invocationTargetException);
			}
		} catch (SecurityException securityException) {
			LOGGER.error(securityException.getMessage(), securityException);
			throw new PayAsiaSystemException(securityException.getMessage(), securityException);
		} catch (NoSuchMethodException noSuchMethodException) {

			LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
			throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
		}

		if (dynamicFormDocumentDTO.getTabId() == 0 || dynamicFormDocumentDTO.getTabId() == null) {
			DynamicFormRecord dynamicRecordVO = dynamicFormRecordDAO.saveReturn(dynamicFormRecord);
			employeeListFormPage.setDynamicFormRecordId(dynamicRecordVO.getRecordId());
			employeeListFormPage.setDynamicFormTableRecordId(Long.parseLong(fieldValue));
			employeeListFormPage.setMode("SAVE");

		} else {
			employeeListFormPage.setDynamicFormTableRecordId(Long.parseLong(fieldValue));
			dynamicFormRecordDAO.update(dynamicFormRecord);
			employeeListFormPage.setMode("UPDATE");
		}

		return employeeListFormPage;

	}

	@Override
	public EmployeeListFormPage updateDocTableRecord(DynamicFormTableDocumentDTO dynamicFormDocumentDTO, Long companyId,
			Long loggedInEmpId) {

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();

		Employee employeeVO = employeeDAO.findById(loggedInEmpId);

		synchronized (this) {

			Long maxTableRecordId = dynamicFormDocumentDTO.getTableRecordId();

			DynamicFormTableRecord dynamicFormTableRecord = dynamicFormTableRecordDAO.findByIdAndSeq(maxTableRecordId,
					dynamicFormDocumentDTO.getSeqNo());
			String fileName = "";
			String fileNameTemp = "";
			String ext = "";
			String fileNameStr = "";
			if (dynamicFormDocumentDTO.getUploadFile().getSize() != 0
					&& dynamicFormDocumentDTO.getUploadFile() != null) {
				/*
				 * String filePath = downloadPath + "/company" + "/" + companyId
				 * + PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_INFO_DOCUMENT +
				 * "/" + dynamicFormDocumentDTO.getEntityKey() + "/";
				 */
				FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
						companyId, PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_INFO_DOCUMENT, null, null,
						String.valueOf(dynamicFormDocumentDTO.getEntityKey()), null,
						PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
				String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
				fileName = uploadEmpDocFile(dynamicFormDocumentDTO.getUploadFile(), filePath, "_");

				fileNameTemp = dynamicFormDocumentDTO.getUploadFile().getOriginalFilename();
				ext = fileNameTemp.substring(fileNameTemp.lastIndexOf(".") + 1, fileNameTemp.length());
				fileNameStr = fileNameTemp.substring(0, fileNameTemp.lastIndexOf("."));
			} else {

				fileNameTemp = getFileNameWOTimeStamp(dynamicFormTableRecord.getCol4());
				ext = fileNameTemp.substring(fileNameTemp.lastIndexOf(".") + 1, fileNameTemp.length());
				fileNameStr = fileNameTemp.substring(0, fileNameTemp.lastIndexOf("."));
			}

			String documentName = "";
			try {
				documentName = URLDecoder.decode(dynamicFormDocumentDTO.getDocumentName(), "UTF8");
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
				throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}

			if (StringUtils.isBlank(dynamicFormDocumentDTO.getDocumentName())) {
				documentName = fileNameStr;
			}
			if (!documentName.equalsIgnoreCase(dynamicFormTableRecord.getCol9())) {
				saveEmployeeDocumentHistory("Document Name", dynamicFormTableRecord.getCol9(), documentName,
						getEmployeeName(employeeVO), dynamicFormTableRecord);
			}
			setTableDocCustomCol(9, documentName, dynamicFormTableRecord);

			String documentType = "";
			try {
				documentType = URLDecoder.decode(dynamicFormDocumentDTO.getDocumentType(), "UTF8");
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
				throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			if (StringUtils.isBlank(dynamicFormDocumentDTO.getDocumentType())) {
				documentType = ext;
			}
			if (!documentType.equalsIgnoreCase(dynamicFormTableRecord.getCol2())) {
				saveEmployeeDocumentHistory("Document Type", dynamicFormTableRecord.getCol2(), documentType,
						getEmployeeName(employeeVO), dynamicFormTableRecord);
			}
			setTableDocCustomCol(2, documentType, dynamicFormTableRecord);

			String documentDescription = "";
			try {
				documentDescription = URLDecoder.decode(dynamicFormDocumentDTO.getDocumentDescription(), "UTF8");
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
				throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			if (StringUtils.isNotBlank(dynamicFormTableRecord.getCol3())
					&& StringUtils.isNotBlank(documentDescription)) {
				if (!documentDescription.equalsIgnoreCase(dynamicFormTableRecord.getCol3())) {
					saveEmployeeDocumentHistory("Document Description", dynamicFormTableRecord.getCol3(),
							documentDescription, getEmployeeName(employeeVO), dynamicFormTableRecord);
				}
			}

			setTableDocCustomCol(3, documentDescription, dynamicFormTableRecord);

			if (StringUtils.isNotBlank(fileName)) {
				if (!fileName.equalsIgnoreCase(dynamicFormTableRecord.getCol4())) {
					saveEmployeeDocumentHistory("Document File", dynamicFormTableRecord.getCol4(), fileName,
							getEmployeeName(employeeVO), dynamicFormTableRecord);
				}
				setTableDocCustomCol(4, fileName, dynamicFormTableRecord);
				String uploadDate = DateUtils.timeStampToString(DateUtils.getCurrentTimestamp());

				setTableDocCustomCol(5, uploadDate, dynamicFormTableRecord);
			}

			String uploadBy = getEmployeeName(employeeVO);
			setTableDocCustomCol(6, uploadBy, dynamicFormTableRecord);
			String owner = dynamicFormDocumentDTO.getOwner();
			setTableDocCustomCol(7, owner, dynamicFormTableRecord);
			String visibleToEmp;
			if ("Employee".equalsIgnoreCase(dynamicFormDocumentDTO.getOwner())) {
				visibleToEmp = "true";
			} else {

				if (dynamicFormDocumentDTO.isVisibleToEmployee() == true) {
					visibleToEmp = "true";
				} else {
					visibleToEmp = "false";
				}
			}
			if (!visibleToEmp.equalsIgnoreCase(dynamicFormTableRecord.getCol8())) {
				saveEmployeeDocumentHistory("visible To Employee", dynamicFormTableRecord.getCol8(), visibleToEmp,
						getEmployeeName(employeeVO), dynamicFormTableRecord);
			}
			setTableDocCustomCol(8, visibleToEmp, dynamicFormTableRecord);

			dynamicFormTableRecordDAO.update(dynamicFormTableRecord);
		}

		return employeeListFormPage;
	}

	private void saveEmployeeDocumentHistory(String fieldName, String oldValue, String newValue, String employeeName,
			DynamicFormTableRecord dynamicFormTableRecord) {
		EmployeeDocumentHistory employeeDocumentHistory = new EmployeeDocumentHistory();
		employeeDocumentHistory.setDynamicFormTableRecord(dynamicFormTableRecord);
		employeeDocumentHistory.setFieldChanged(fieldName);
		employeeDocumentHistory.setNewValue(newValue);
		employeeDocumentHistory.setOldValue(oldValue == null ? "" : oldValue);
		employeeDocumentHistory.setChangedDate(DateUtils.getCurrentTimestamp());
		employeeDocumentHistory.setChangedBy(employeeName);
		employeeDocumentHistoryDAO.save(employeeDocumentHistory);
	}

	private void setTableDocCustomCol(int colNum, String colValue, DynamicFormTableRecord dynamicFormTableRecord) {
		String tableRecordMethodName = PayAsiaConstants.SET_COL + colNum;
		Method dynamicFormTableRecordMethod;
		try {
			dynamicFormTableRecordMethod = DynamicFormTableRecord.class.getMethod(tableRecordMethodName, String.class);
			if (StringUtils.isNotBlank(colValue)) {
				dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord, colValue);
			}
		} catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}
	}

	@Override
	public DynamicFormTableDocumentDTO downloadEmpDoc(Long recordId, int seqNo, Long companyId, Long entityKey) {
		DynamicFormTableDocumentDTO documentDTO = new DynamicFormTableDocumentDTO();
		DynamicFormTableRecord tableRecord = dynamicFormTableRecordDAO.findByIdAndSeq(recordId, seqNo);
		documentDTO.setFileName(tableRecord.getCol4());

		InputStream pdfIn = null;

		/*
		 * String filePath = downloadPath + "/company" + "/" + companyId +
		 * PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_INFO_DOCUMENT + "/" +
		 * entityKey + "/";
		 */
		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_INFO_DOCUMENT, null, null, String.valueOf(entityKey), null,
				PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		String fileName = tableRecord.getCol4();

		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				pdfIn = awss3LogicImpl.readS3ObjectAsStream(filePath + fileName);
			} else {
				File file = new File(filePath + fileName);
				pdfIn = new FileInputStream(file);
			}
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}
		try {
			documentDTO.setAttachmentBytes(IOUtils.toByteArray(pdfIn));
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}
		return documentDTO;
	}

	@Override
	public EmployeeListFormPage empTakeOwnership(Long tableId, Long companyId, Integer seqNo, String ownership) {
		DynamicFormTableRecord tableRecord = dynamicFormTableRecordDAO.findByIdAndSeq(tableId, seqNo);
		if (StringUtils.isBlank(ownership)) {
			return null;
		}
		if ("admin".equalsIgnoreCase(ownership)) {
			tableRecord.setCol7("Admin");
			dynamicFormTableRecordDAO.update(tableRecord);
		}
		if ("employee".equalsIgnoreCase(ownership)) {
			tableRecord.setCol7("Employee");
			dynamicFormTableRecordDAO.update(tableRecord);
		}

		return null;
	}

	@Override
	public EmployeeListFormPage empTableRecordList(Long tid, Long formId, int ColumnCount, String[] fieldNames,
			String[] fieldTypes, String[] fieldDictIds, Long companyId, Long employeeId, String tableType,
			String sortOrder, String sortBy, Long languageId) {

		Company cmp = companyDAO.findById(companyId);
		String companyDateFormat = cmp.getDateFormat();

		if (StringUtils.isNotBlank(sortBy) && !sortBy.equalsIgnoreCase("null")) {
			String sortByNameCount = sortBy.substring(sortBy.lastIndexOf('_') + 1, sortBy.length());
			sortBy = "col" + sortByNameCount;
		} else {
			sortBy = "";
		}

		if (!isUserAuthorized(companyId, employeeId, formId, tid)) {
			throw new PayAsiaSystemException("Unauthorized Access");
		}

		List<DynamicFormTableRecord> tableRecordList = dynamicFormTableRecordDAO.getTableRecords(tid, sortOrder,
				sortBy);
		List<EmployeeListForm> tableRecords = new ArrayList<EmployeeListForm>();

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();
		HashMap<Long, Tab> dataTabMap = new HashMap<>();
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();

		int count;
		String fieldNameCount;
		String fieldName;
		String fieldType;
		String fieldDictId;
		String dynamicFormTableRecordValue = null;
		EmployeeListForm empListForm = null;
		for (DynamicFormTableRecord dynamicFormTableRecord : tableRecordList) {
			if ("document".equalsIgnoreCase(tableType)) {
				if ("false".equalsIgnoreCase(dynamicFormTableRecord.getCol8())) {
					continue;
				}
			}

			empListForm = new EmployeeListForm();
			EmployeeDynamicFormDTO employeeDynamicFormDTO = new EmployeeDynamicFormDTO();
			for (count = 1; count <= ColumnCount; count++) {
				fieldName = fieldNames[count - 1];
				fieldType = fieldTypes[count - 1];
				fieldDictId = fieldDictIds[count - 1];
				fieldNameCount = fieldName.substring(fieldName.lastIndexOf('_') + 1, fieldName.length());

				Class<?> dynamicTableClass = dynamicFormTableRecord.getClass();
				Class<?> employeeDynamicFormDTOClass = employeeDynamicFormDTO.getClass();
				String dynamicFormTableRecordMethodName = PayAsiaConstants.GET_COL + fieldNameCount;
				String empListFormMethodName = PayAsiaConstants.SET_COL + count;
				try {
					Method dynamicFormTableRecordMethod = dynamicTableClass.getMethod(dynamicFormTableRecordMethodName);
					Method empListFormMethod = employeeDynamicFormDTOClass.getMethod(empListFormMethodName,
							String.class);

					dynamicFormTableRecordValue = (String) dynamicFormTableRecordMethod.invoke(dynamicFormTableRecord);

					if (fieldName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1) && "table".equalsIgnoreCase(tableType)) {
						dynamicFormTableRecordValue = DateUtils.convertDateFormat(dynamicFormTableRecordValue,
								companyDateFormat);

					} else if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
							&& dynamicFormTableRecordValue != null
							&& !"".equalsIgnoreCase(dynamicFormTableRecordValue)) {

						dynamicFormTableRecordValue = DateUtils.convertDateToSpecificFormat(dynamicFormTableRecordValue,
								companyDateFormat);

					} else if (fieldType.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
							&& dynamicFormTableRecordValue != null
							&& !"".equalsIgnoreCase(dynamicFormTableRecordValue)) {

						DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
								.findById(Long.parseLong(dynamicFormTableRecordValue));
						if (dynamicFormFieldRefValue != null) {
							dynamicFormTableRecordValue = dynamicFormFieldRefValue.getCode();
						} else {
							dynamicFormTableRecordValue = "";
						}

					} else if ("file".equalsIgnoreCase(fieldType) && dynamicFormTableRecordValue != null
							&& !"".equalsIgnoreCase(dynamicFormTableRecordValue)) {
						String fileNameStr = getFileNameWOTimeStamp(dynamicFormTableRecordValue);
						StringBuilder payasiaShortListYesNo = new StringBuilder();
						payasiaShortListYesNo
								.append("<a class='alink' style='text-decoration: underline;' href='#' onClick = 'downLoadEmpInfoDoc("
										+ dynamicFormTableRecord.getId().getDynamicFormTableRecordId() + ','
										+ dynamicFormTableRecord.getId().getSequence() + ")'>" + fileNameStr + "</a>");
						dynamicFormTableRecordValue = payasiaShortListYesNo.toString();
					} else if (fieldType.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)
							&& dynamicFormTableRecordValue != null
							&& !"".equalsIgnoreCase(dynamicFormTableRecordValue)) {

						Employee employee = employeeDAO.findById(Long.parseLong(dynamicFormTableRecordValue));
						dynamicFormTableRecordValue = getEmployeeName(employee);
					}
					if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
						DataDictionary dataDictionary = dataDictionaryDAO.findById(Long.parseLong(fieldDictId));
						DynamicForm dynamicForm = null;
						if (formIdMap.containsKey(dataDictionary.getFormID())) {
							dynamicForm = formIdMap.get(dataDictionary.getFormID());
						} else {
							dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
									dataDictionary.getEntityMaster().getEntityId(), dataDictionary.getFormID());
							formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
							dynamicFormMap.put(dynamicForm.getId().getFormId(), dynamicForm);
						}
						if (dynamicForm != null) {
							try {
								dynamicFormTableRecordValue = addTableFormulaValueInExistingDynXML(dynamicFormMap,
										dataTabMap, staticPropMap, dynamicForm.getMetaData(), employeeId, companyId,
										true, dynamicFormTableRecord.getId().getDynamicFormTableRecordId(),
										dynamicFormTableRecord.getId().getSequence(),
										dataDictionary.getDataDictionaryId());
							} catch (XMLStreamException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(e.getMessage(), e);
							}
						}

					}
					if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
						DataDictionary dataDictionary = dataDictionaryDAO.findById(Long.parseLong(fieldDictId));
						DynamicForm dynamicForm = null;
						if (formIdMap.containsKey(dataDictionary.getFormID())) {
							dynamicForm = formIdMap.get(dataDictionary.getFormID());
						} else {
							dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
									dataDictionary.getEntityMaster().getEntityId(), dataDictionary.getFormID());
							formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
							dynamicFormMap.put(dynamicForm.getId().getFormId(), dynamicForm);
						}
						if (dynamicForm != null) {
							try {
								try {
									dynamicFormTableRecordValue = addTableReferenceValueInExistingDynXML(dynamicFormMap,
											dynamicForm.getMetaData(), employeeId, companyId, companyDateFormat,
											dynamicFormTableRecord.getId().getDynamicFormTableRecordId(),
											dynamicFormTableRecord.getId().getSequence(),
											dataDictionary.getDataDictionaryId());
								} catch (SAXException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								}
							} catch (XMLStreamException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(e.getMessage(), e);
							}
						}

					}

					if ("table".equalsIgnoreCase(tableType)
							&& !fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)
							&& !fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
						if (StringUtils.isNotBlank(fieldDictId)) {
							HRISChangeRequest hrisChangeRequest = hrisChangeRequestDAO.findByConditionTableSeq(
									Long.parseLong(fieldDictId), employeeId, hrisStatusList,
									dynamicFormTableRecord.getId().getSequence());
							if (hrisChangeRequest != null) {
								DynamicForm dynamicForm = null;
								if (formIdMap.containsKey(hrisChangeRequest.getDataDictionary().getFormID())) {
									dynamicForm = formIdMap.get(hrisChangeRequest.getDataDictionary().getFormID());
								} else {
									dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
											hrisChangeRequest.getDataDictionary().getEntityMaster().getEntityId(),
											hrisChangeRequest.getDataDictionary().getFormID());
									formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
								}

								EmployeeListFormPage employeeListFormPage = generalLogic
										.getEmployeeHRISChangeRequestData(hrisChangeRequest.getHrisChangeRequestId(),
												companyId, dynamicForm.getMetaData(), languageId);

								StringBuilder tableFieldDynStr = new StringBuilder();
								if (fieldType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CHECK)) {
									if (employeeListFormPage.getNewValue().equalsIgnoreCase("true")) {
										tableFieldDynStr.append("<div class='tableCellBGColor'><input type='checkbox'"
												+ "checked='checked' value='" + employeeListFormPage.getNewValue()
												+ "' offval='no' disabled='disabled'></div>");
									} else {
										tableFieldDynStr.append("<div class='tableCellBGColor'><input type='checkbox'"
												+ " value='" + employeeListFormPage.getNewValue()
												+ "' offval='no' disabled='disabled'></div>");
									}

								} else if (fieldType.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)
										&& dynamicFormTableRecordValue != null
										&& !"".equalsIgnoreCase(dynamicFormTableRecordValue)) {

									Employee employee = employeeDAO
											.findById(Long.parseLong(employeeListFormPage.getEmpLstNewValue()));
									employeeListFormPage.setEmpLstNewValue(getEmployeeName(employee));
									tableFieldDynStr.append("<div  class='tableCellBGColor' >"
											+ employeeListFormPage.getEmpLstNewValue() + "</div>");

								} else {
									tableFieldDynStr.append("<div  class='tableCellBGColor' >"
											+ employeeListFormPage.getNewValue() + "</div>");
								}

								dynamicFormTableRecordValue = tableFieldDynStr.toString();

							}

						}

					}

					if (StringUtils.isNotBlank(dynamicFormTableRecordValue)) {
						empListFormMethod.invoke(employeeDynamicFormDTO, dynamicFormTableRecordValue);
					}
				} catch (SecurityException securityException) {
					LOGGER.error(securityException.getMessage(), securityException);
					throw new PayAsiaSystemException(securityException.getMessage(), securityException);
				} catch (NoSuchMethodException noSuchMethodException) {

					LOGGER.error(noSuchMethodException.getMessage(), noSuchMethodException);
					throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
					throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(), illegalAccessException);
					throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(), invocationTargetException);
					throw new PayAsiaSystemException(invocationTargetException.getMessage(), invocationTargetException);
				}
			}
			empListForm.setTableRecordId(dynamicFormTableRecord.getId().getSequence());
			empListForm.setEmployeeDynamicFormDTO(employeeDynamicFormDTO);
			tableRecords.add(empListForm);
		}

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		employeeListFormPage.setTableDataList(tableRecords);

		return employeeListFormPage;
	}

	@Override
	public EmployeeListFormPage getEmployeeDocHistory(Long companyId, Long entityKey, Long tableId, Integer seqNo,
			String tableType, Long formId, boolean isAdmin) {

		if (!isAdmin) {
			if (!isUserAuthorized(companyId, entityKey, formId, tableId)) {
				throw new PayAsiaSystemException("Authentication Exception");
			}
		}
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		List<EmployeeDocumentHistory> employeeDocumentHistoryList = employeeDocumentHistoryDAO.findByCondition(tableId,
				seqNo);
		List<EmployeeDocumentHistoryDTO> empDocHistoryList = new ArrayList<EmployeeDocumentHistoryDTO>();

		for (EmployeeDocumentHistory employeeDocumentHistory : employeeDocumentHistoryList) {
			EmployeeDocumentHistoryDTO empDocHistory = new EmployeeDocumentHistoryDTO();
			empDocHistory.setFieldName(employeeDocumentHistory.getFieldChanged());
			empDocHistory.setNewValue(employeeDocumentHistory.getNewValue());
			empDocHistory.setOldValue(employeeDocumentHistory.getOldValue());
			empDocHistory.setChangedDate(DateUtils.timeStampToString(employeeDocumentHistory.getChangedDate()));
			empDocHistory.setChangedBy(employeeDocumentHistory.getChangedBy());
			empDocHistoryList.add(empDocHistory);
		}

		employeeListFormPage.setEmpDocHistoryList(empDocHistoryList);
		return employeeListFormPage;
	}

	@Override
	public EmployeeListFormPage getTableEmpChangeRequestData(Long loggedInEmployeeId, Long companyId, Long employeeId,
			Long tableFieldDictionaryId, int seqNum, Long languageId) {

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);
		HRISChangeRequest hrisChangeRequest = hrisChangeRequestDAO.findByDataDictAndSeq(tableFieldDictionaryId,
				employeeId, seqNum, hrisStatusList, companyId);
		EmployeeListFormPage employeeListFormPage = null;
		if (hrisChangeRequest != null) {
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
					hrisChangeRequest.getDataDictionary().getEntityMaster().getEntityId(),
					hrisChangeRequest.getDataDictionary().getFormID());

			employeeListFormPage = generalLogic.getEmployeeHRISChangeRequestData(
					hrisChangeRequest.getHrisChangeRequestId(), companyId, dynamicForm.getMetaData(), languageId);
		}

		return employeeListFormPage;
	}

	@Override
	public String isEnableEmployeeChangeWorkflow(Long companyId) {

		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO == null) {
			return PayAsiaConstants.FALSE;
		} else if (hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			return PayAsiaConstants.TRUE;
		} else if (!hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			return PayAsiaConstants.FALSE;
		}
		return PayAsiaConstants.FALSE;

	}

	@Override
	public String isAllowEmployeetouploaddocument(Long companyId) {

		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO == null) {
			return PayAsiaConstants.FALSE;
		} else if (hrisPreferenceVO.isAllowEmployeeUploadDoc()) {
			return PayAsiaConstants.TRUE;
		} else if (!hrisPreferenceVO.isAllowEmployeeUploadDoc()) {
			return PayAsiaConstants.FALSE;
		}
		return PayAsiaConstants.FALSE;

	}

	private String addTableFormulaValueInExistingDynXML(HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<Long, Tab> dataTabMap, HashMap<String, ColumnPropertyDTO> staticPropMap, String XML,
			Long employeeId, Long companyId, boolean isTableField, Long tableId, Integer seqNo, Long fieldDictId)
			throws XMLStreamException {

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}
		final StringReader xmlReader = new StringReader(XML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}
		String calculatedValue = "";
		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {

			if (field.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE) && isTableField) {
				List<Column> columnList = field.getColumn();
				for (Column column : columnList) {
					if (column.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
						if (fieldDictId.equals(column.getDictionaryId())) {
							DataDictionary dataDictionary = dataDictionaryDAO.findById(column.getDictionaryId());
							if (dataDictionary != null) {
								try {
									calculatedValue = getFormulaFieldCalculatedValue(dynamicFormMap, dataTabMap,
											staticPropMap, column.getFormula(), column.getFormulaType(), dataDictionary,
											employeeId, companyId, tableId, seqNo);
								} catch (NoSuchMethodException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								} catch (IllegalAccessException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								} catch (InvocationTargetException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								}

							}
						}

					}

				}
			}

		}

		return calculatedValue;
	}

	private String addFormulaValueInExistingDynXML(String XML, Long employeeId, Long companyId) {
		HashMap<Long, Tab> dataTabMap = new HashMap<>();
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();

		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = XMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jAXBException) {
				LOGGER.error(jAXBException.getMessage(), jAXBException);
				throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
			} catch (SAXException sAXException) {

				LOGGER.error(sAXException.getMessage(), sAXException);
				throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
			}
			final StringReader xmlReader = new StringReader(XML);
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(), e1);
			}

			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			streamWriter = outputFactory.createXMLStreamWriter(byteArrayOutputStream);

			Tab tab = null;
			try {
				tab = (Tab) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jAXBException) {
				LOGGER.error(jAXBException.getMessage(), jAXBException);
				throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
			}

			List<Field> listOfFields = tab.getField();
			for (Field field : listOfFields) {
				if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
					DataDictionary dataDictionary = dataDictionaryDAO.findById(field.getDictionaryId());
					if (dataDictionary != null) {
						String calculatedValue = "";
						try {
							calculatedValue = getFormulaFieldCalculatedValue(dynamicFormMap, dataTabMap, staticPropMap,
									field.getFormula(), field.getFormulaType(), dataDictionary, employeeId, companyId,
									null, 0);
						} catch (NoSuchMethodException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						} catch (IllegalAccessException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						} catch (InvocationTargetException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}
						if (StringUtils.isNotBlank(calculatedValue)) {
							field.setValue(calculatedValue);
						} else {
							field.setValue("");
						}

					}

				}

			}
			Marshaller m = XMLUtil.getDocumentMarshaller();
			m.marshal(tab, streamWriter);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (XMLStreamException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return byteArrayOutputStream.toString();
	}

	@Override
	public String getFormulaFieldCalculatedValue(HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<Long, Tab> dataTabMap, HashMap<String, ColumnPropertyDTO> staticPropMap, String formula,
			String formulaType, DataDictionary dataDictionary, Long employeeId, Long companyId, Long tableId,
			Integer seqNo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		List<Long> dataDictionaries = new ArrayList<Long>();
		String formulaEx = formula;

		if (StringUtils.isBlank(formulaType) || "undefined".equalsIgnoreCase(formulaType)) {
			formulaType = PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC;
		}
		if (formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC)) {
			while (!formulaEx.equals("")) {
				if (formulaEx.indexOf('{') != -1) {
					String fex = formulaEx.substring(formulaEx.indexOf('{') + 1, formulaEx.indexOf('}'));
					dataDictionaries.add(Long.parseLong(fex));
					formulaEx = formulaEx.substring(formulaEx.indexOf('}') + 1);
				} else {
					break;
				}
			}
		} else if (formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_STRING)
				|| formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_DATE)) {
			while (!formulaEx.equals("")) {
				if (formulaEx.indexOf('{') != -1) {
					String fex = formulaEx.substring(formulaEx.indexOf('(') + 1, formulaEx.indexOf(')'));
					dataDictionaries.add(Long.parseLong(fex.substring(fex.indexOf('{') + 1, fex.indexOf('}'))));
					formulaEx = formulaEx.substring(formulaEx.indexOf(')') + 1);
				} else {
					break;
				}
			}
		}
		String calculatedValue = "";
		List<String> valueList = getValueForDictionary(employeeId, dataDictionary, dataDictionaries, dataTabMap,
				dynamicFormMap, staticPropMap, companyId, tableId, seqNo);
		if (!valueList.isEmpty()) {
			if (formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_STRING)) {
				calculatedValue = FormulaUtils.getStringTypeFormulaCalulatedValue(formula, valueList);
			} else if (formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC)) {
				calculatedValue = FormulaUtils.getNumericTypeFormulaCalculatedValue(formula, valueList);
			} else if (formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_DATE)) {
				Company companyVO = companyDAO.findById(companyId);
				calculatedValue = FormulaUtils.getDateTypeFormulaCalulatedValue(formula, valueList,
						companyVO.getDateFormat());
			}
		}

		return calculatedValue;
	}

	private List<String> getValueForDictionary(Long employeeId, DataDictionary dataDictionary,
			List<Long> dataDictionaries, HashMap<Long, Tab> dataTabMap, HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap, Long companyId, Long tableId, Integer seqNo)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Map<Long, DataImportKeyValueDTO> formulaMap = new LinkedHashMap<Long, DataImportKeyValueDTO>();
		for (Long dataDicId : dataDictionaries) {
			DataDictionary dictionary = dataDictionaryDAO.findById(dataDicId);

			if (dictionary.getFieldType().equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
				setStaticDictionary(formulaMap, dictionary, staticPropMap);
			} else {
				setDynamicDictionary(formulaMap, dictionary, dataTabMap, dynamicFormMap);
			}
		}

		List<String> valueList = new ArrayList<String>();
		for (Long dicID : dataDictionaries) {
			DataImportKeyValueDTO formulaValueDTO = formulaMap.get(dicID);
			if (formulaValueDTO.isStatic()) {
				Employee employeeVO = employeeDAO.findById(employeeId);
				if (formulaValueDTO.getMethodName().equalsIgnoreCase("EmployeeNumber")) {

					String value = employeeVO.getEmployeeNumber();
					valueList.add(value);
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("FirstName")) {

					String value = employeeVO.getFirstName();
					valueList.add(value);
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("LastName")) {

					String value = employeeVO.getLastName();
					valueList.add(value);
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("HireDate")) {
					Timestamp value = employeeVO.getHireDate();
					if (value != null) {
						Date date = new Date(value.getTime());
						SimpleDateFormat dateFormat = new SimpleDateFormat(PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
						valueList.add(dateFormat.format(date));
					} else {
						valueList.add("");
					}

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("OriginalHireDate")) {
					Timestamp value = employeeVO.getOriginalHireDate();
					if (value != null) {
						Date date = new Date(value.getTime());
						SimpleDateFormat dateFormat = new SimpleDateFormat(PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
						valueList.add(dateFormat.format(date));
					} else {
						valueList.add("");
					}
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("ResignationDate")) {
					Timestamp value = employeeVO.getResignationDate();
					if (value != null) {
						Date date = new Date(value.getTime());
						SimpleDateFormat dateFormat = new SimpleDateFormat(PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
						valueList.add(dateFormat.format(date));
					} else {
						valueList.add("");
					}
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("ConfirmationDate")) {
					Timestamp value = employeeVO.getConfirmationDate();
					if (value != null) {
						Date date = new Date(value.getTime());
						SimpleDateFormat dateFormat = new SimpleDateFormat(PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
						valueList.add(dateFormat.format(date));
					} else {
						valueList.add("");
					}
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("Email")) {

					String value = employeeVO.getEmail();
					valueList.add(value);
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("Status")) {

					boolean value = employeeVO.isStatus();
					valueList.add(String.valueOf(value));
				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("CompanyName")) {
					String value = employeeVO.getCompany().getCompanyName();
					valueList.add(value);

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("CompanyCode")) {
					String value = employeeVO.getCompany().getCompanyCode();
					valueList.add(value);

				} else if (formulaValueDTO.getMethodName().equalsIgnoreCase("GroupName")) {
					String value = employeeVO.getCompany().getCompanyGroup().getGroupName();
					valueList.add(value);

				}

			} else {
				if (formulaValueDTO.isChild()) {
					DynamicFormTableRecord formulaDynFormTableRecord = dynamicFormTableRecordDAO.findByIdAndSeq(tableId,
							seqNo);
					boolean isRecordFound = false;
					if (formulaDynFormTableRecord != null) {
						isRecordFound = true;
						String value = getColValueOfDynamicTableRecord(formulaValueDTO.getMethodName(),
								formulaDynFormTableRecord);
						if (value != null) {
							if (formulaValueDTO.isCodeDescField() && StringUtils.isNotBlank(value)) {
								DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
										.findById(Long.parseLong(value));

								try {
									String code = dynamicFormFieldRefValue.getCode();
									value = code;
								} catch (Exception exception) {
									LOGGER.error(exception.getMessage(), exception);
									value = "";
								}

							}
							valueList.add(value);
						} else {
							valueList.add("");
						}

					} else {
						valueList.add("");
					}

					if (!isRecordFound) {
						valueList.add("");
					}
				} else {
					List<DynamicFormRecord> formulaDynFormRecordList = dynamicFormRecordDAO.findByEntityKey(employeeId,
							dataDictionary.getEntityMaster().getEntityId(), companyId);
					boolean isRecordFound = false;
					if (!formulaDynFormRecordList.isEmpty() && formulaDynFormRecordList != null) {
						for (DynamicFormRecord formulaDynFormRecord : formulaDynFormRecordList) {
							if (formulaValueDTO.getFormId() == formulaDynFormRecord.getForm_ID()) {
								isRecordFound = true;
								String value = getColValueFile(formulaValueDTO.getMethodName(), formulaDynFormRecord);
								if (value != null) {
									if (formulaValueDTO.isCodeDescField() && StringUtils.isNotBlank(value)) {
										DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
												.findById(Long.parseLong(value));

										try {
											String code = dynamicFormFieldRefValue.getCode();
											value = code;
										} catch (Exception exception) {
											LOGGER.error(exception.getMessage(), exception);
											value = "";
										}

									}

									valueList.add(value);
								} else {
									valueList.add("");
								}
							}
						}
					} else {
						valueList.add("");
					}

					if (!isRecordFound) {
						valueList.add("");
					}
				}

			}
		}
		return valueList;
	}

	private void setStaticDictionary(Map<Long, DataImportKeyValueDTO> colMap, DataDictionary dataDictionary,
			HashMap<String, ColumnPropertyDTO> staticPropMap) {
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		ColumnPropertyDTO colProp = staticPropMap.get(dataDictionary.getTableName() + dataDictionary.getColumnName());
		if (colProp == null) {
			colProp = generalDAO.getColumnProperties(dataDictionary.getTableName(), dataDictionary.getColumnName());
			staticPropMap.put(dataDictionary.getTableName() + dataDictionary.getColumnName(), colProp);
		}

		dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
		dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils.getCamelCase(dataDictionary.getColumnName()));
		dataImportKeyValueDTO.setStatic(true);
		colMap.put(dataDictionary.getDataDictionaryId(), dataImportKeyValueDTO);

	}

	private void setDynamicDictionary(Map<Long, DataImportKeyValueDTO> colMap, DataDictionary dataDictionary,
			HashMap<Long, Tab> dataTabMap, HashMap<Long, DynamicForm> dynamicFormMap) {
		DynamicForm dynamicForm = null;

		Tab tab = dataTabMap.get(dataDictionary.getFormID());
		if (tab == null) {
			if (dataDictionary.getEntityMaster().getEntityName()
					.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {

				int maxVersion = dynamicFormDAO.getMaxVersionByFormId(dataDictionary.getCompany().getCompanyId(),
						dataDictionary.getEntityMaster().getEntityId(), dataDictionary.getFormID());

				dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(dataDictionary.getCompany().getCompanyId(),
						dataDictionary.getEntityMaster().getEntityId(), maxVersion, dataDictionary.getFormID());

			}

			tab = dataExportUtils.getTabObject(dynamicForm);
			dataTabMap.put(dataDictionary.getFormID(), tab);
			dynamicFormMap.put(dataDictionary.getFormID(), dynamicForm);
		} else {
			dynamicForm = dynamicFormMap.get(dataDictionary.getFormID());
		}

		List<Field> listOfFields = tab.getField();
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		for (Field field : listOfFields) {
			if (StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName().getBytes()))
						.equals(dataDictionary.getDataDictName())
						|| field.getDictionaryId().longValue() == dataDictionary.getDataDictionaryId()) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils.getColNumber(field.getName()));
					dataImportKeyValueDTO.setFormula(true);
					dataImportKeyValueDTO.setFormId(dynamicForm.getId().getFormId());
					dataImportKeyValueDTO.setFormula(field.getFormula());

				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.FIELD_TYPE_DATE)
					|| StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.FIELD_TYPE_TEXT)
					|| StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.FIELD_TYPE_COMBO)
					|| StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName().getBytes()))
						.equals(dataDictionary.getDataDictName())
						|| field.getDictionaryId().longValue() == dataDictionary.getDataDictionaryId()) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils.getColNumber(field.getName()));
					dataImportKeyValueDTO.setFormula(true);
					dataImportKeyValueDTO.setFormId(dynamicForm.getId().getFormId());
					if (StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
						dataImportKeyValueDTO.setCodeDescField(true);
					} else {
						dataImportKeyValueDTO.setCodeDescField(false);
					}

				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(), "table")) {

				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (StringUtils.equalsIgnoreCase(column.getType(), PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
						if (new String(Base64.decodeBase64(column.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())
								|| column.getDictionaryId().longValue() == dataDictionary.getDataDictionaryId()) {
							dataImportKeyValueDTO.setStatic(false);
							dataImportKeyValueDTO.setChild(true);
							dataImportKeyValueDTO.setFieldType(column.getType());
							dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils.getColNumber(column.getName()));
							dataImportKeyValueDTO.setFormId(dynamicForm.getId().getFormId());
							dataImportKeyValueDTO.setTablePosition(PayAsiaStringUtils.getColNumber(field.getName()));
						}
					} else if (StringUtils.equalsIgnoreCase(column.getType(), PayAsiaConstants.FIELD_TYPE_TEXT)
							|| StringUtils.equalsIgnoreCase(column.getType(), PayAsiaConstants.FIELD_TYPE_DATE)
							|| StringUtils.equalsIgnoreCase(column.getType(), PayAsiaConstants.FIELD_TYPE_DROPDOWN)
							|| StringUtils.equalsIgnoreCase(column.getType(), PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
						if (new String(Base64.decodeBase64(column.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())
								|| column.getDictionaryId().longValue() == dataDictionary.getDataDictionaryId()) {
							dataImportKeyValueDTO.setStatic(false);
							dataImportKeyValueDTO.setChild(true);
							dataImportKeyValueDTO.setFieldType(column.getType());
							dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils.getColNumber(column.getName()));
							dataImportKeyValueDTO.setFormId(dynamicForm.getId().getFormId());
							dataImportKeyValueDTO.setTablePosition(PayAsiaStringUtils.getColNumber(field.getName()));
							if (StringUtils.equalsIgnoreCase(column.getType(), PayAsiaConstants.CODEDESC_FIELD_TYPE)) {
								dataImportKeyValueDTO.setCodeDescField(true);
							} else {
								dataImportKeyValueDTO.setCodeDescField(false);
							}
						}
					}

				}
			}

			colMap.put(dataDictionary.getDataDictionaryId(), dataImportKeyValueDTO);
		}

	}

	private String getColValueOfDynamicTableRecord(String colNumber, DynamicFormTableRecord existingFormRecord) {

		String tableRecordId = null;
		Class<?> dynamicFormTableRecordClass = existingFormRecord.getClass();
		String colMehtodName = "getCol" + colNumber;
		Method dynamicFormTableRecordMethod;
		try {

			dynamicFormTableRecordMethod = dynamicFormTableRecordClass.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormTableRecordMethod.invoke(existingFormRecord);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return tableRecordId;

	}

	private String addReferenceValueInExistingDynXML(String XML, Long employeeId, Long companyId,
			String companyDateFormat) {
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();

		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = XMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jAXBException) {
				LOGGER.error(jAXBException.getMessage(), jAXBException);
				throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
			} catch (SAXException sAXException) {

				LOGGER.error(sAXException.getMessage(), sAXException);
				throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
			}
			final StringReader xmlReader = new StringReader(XML);
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(), e1);
			}

			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			streamWriter = outputFactory.createXMLStreamWriter(byteArrayOutputStream);

			Tab tab = null;
			try {
				tab = (Tab) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jAXBException) {
				LOGGER.error(jAXBException.getMessage(), jAXBException);
				throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
			}
			Employee employeeVO = employeeDAO.findById(employeeId);

			List<Field> listOfFields = tab.getField();
			for (Field field : listOfFields) {
				if (field.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
					Long fieldReferencedId = field.getReferenced();

					DataDictionary refDataDictionary = dataDictionaryDAO.findById(fieldReferencedId);

					if (refDataDictionary.getFieldType().equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
						Class<?> employeeClass = employeeVO.getClass();

						String getMethodName = "get" + refDataDictionary.getColumnName().replace("_", "");

						if (refDataDictionary.getColumnName()
								.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_STATIC_FIELD_STATUS)) {
							getMethodName = "is" + refDataDictionary.getColumnName();
						}

						Method employeeMethod = null;
						try {
							employeeMethod = employeeClass.getMethod(getMethodName);
						} catch (NoSuchMethodException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						} catch (SecurityException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage(), e);
						}
						String fieldRValue;
						try {
							Object obj = employeeMethod.invoke(employeeVO);
							if (obj instanceof Timestamp) {
								fieldRValue = String.valueOf(DateUtils.timeStampToString(
										(Timestamp) employeeMethod.invoke(employeeVO), companyDateFormat));
							} else {
								fieldRValue = String.valueOf(employeeMethod.invoke(employeeVO));
							}

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
						if (StringUtils.isNotBlank(fieldRValue) && !"null".equalsIgnoreCase(fieldRValue)) {
							try {
								field.setValue(URLEncoder.encode(fieldRValue, "UTF-8"));
							} catch (UnsupportedEncodingException e) {
								LOGGER.error(e.getMessage(), e);
							}
						} else {
							field.setValue("");
						}

					} else {
						DynamicForm dynamicForm = null;
						if (dynamicFormMap.containsKey(refDataDictionary.getFormID())) {
							dynamicForm = dynamicFormMap.get(refDataDictionary.getFormID());
						} else {
							dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
									refDataDictionary.getEntityMaster().getEntityId(), refDataDictionary.getFormID());
							dynamicFormMap.put(dynamicForm.getId().getFormId(), dynamicForm);
						}

						if (dynamicForm != null) {
							Unmarshaller unmarshallerRef = null;
							try {
								unmarshallerRef = XMLUtil.getDocumentUnmarshaller();
							} catch (JAXBException jAXBException) {
								LOGGER.error(jAXBException.getMessage(), jAXBException);
								throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
							} catch (SAXException sAXException) {

								LOGGER.error(sAXException.getMessage(), sAXException);
								throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
							}
							final StringReader xmlRefReader = new StringReader(dynamicForm.getMetaData());
							Source xmlRefSource = null;
							try {
								xmlSource = XMLUtil.getSAXSource(xmlRefReader);
							} catch (SAXException | ParserConfigurationException e1) {
								LOGGER.error(e1.getMessage(), e1);
								throw new PayAsiaSystemException(e1.getMessage(), e1);
							}

							Tab tabRef = null;
							try {
								tabRef = (Tab) unmarshallerRef.unmarshal(xmlRefSource);
							} catch (JAXBException jAXBException) {
								LOGGER.error(jAXBException.getMessage(), jAXBException);
								throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
							}

							DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO.getEmpRecords(employeeId,
									dynamicForm.getId().getVersion(), dynamicForm.getId().getFormId(),
									dynamicForm.getEntityMaster().getEntityId(), companyId);
							if (dynamicFormRecord != null) {
								Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

								List<Field> listOfRefFields = tabRef.getField();
								for (Field fieldR : listOfRefFields) {
									if (fieldR.getDictionaryId().equals(refDataDictionary.getDataDictionaryId())) {
										String fieldRName = fieldR.getName();
										String fieldRtype = fieldR.getType();

										String getMethodName = "getCol" + fieldRName
												.substring(fieldRName.lastIndexOf('_') + 1, fieldRName.length());
										String fieldRValue;
										try {
											Method dynamicFormRecordMethod = dynamicFormRecordClass
													.getMethod(getMethodName);
											try {
												fieldRValue = (String) dynamicFormRecordMethod
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

										if (fieldRtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
												&& !"".equalsIgnoreCase(fieldRValue)) {
											fieldRValue = DateUtils.convertDateToSpecificFormat(fieldRValue,
													companyDateFormat);
										}
										if (fieldRtype.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
												&& StringUtils.isNotBlank(fieldRValue)) {
											DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
													.findById(Long.parseLong(fieldRValue));

											try {
												String codeDescription = dynamicFormFieldRefValue.getDescription() + "["
														+ dynamicFormFieldRefValue.getCode() + "]";
												fieldRValue = codeDescription;
											} catch (Exception exception) {
												LOGGER.error(exception.getMessage(), exception);
												fieldRValue = "";
											}

										}
										if (StringUtils.isNotBlank(fieldRValue)
												&& !fieldRValue.equalsIgnoreCase("null")) {
											try {
												field.setValue(URLEncoder.encode(fieldRValue, "UTF-8"));
											} catch (UnsupportedEncodingException e) {
												LOGGER.error(e.getMessage(), e);
											}
										} else {
											field.setValue("");
										}
									}
								}
							}

						}

					}

				}
			}
			Marshaller m = XMLUtil.getDocumentMarshaller();
			m.marshal(tab, streamWriter);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (SAXException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (XMLStreamException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return byteArrayOutputStream.toString();
	}

	private String addTableReferenceValueInExistingDynXML(HashMap<Long, DynamicForm> dynamicFormMap, String XML,
			Long employeeId, Long companyId, String companyDateFormat, Long tableId, Integer seqNo, Long fieldDictId)
			throws SAXException, XMLStreamException {

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}
		final StringReader xmlReader = new StringReader(XML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}
		Employee employeeVO = employeeDAO.findById(employeeId);
		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}
		String fieldRValue = "";
		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (field.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				List<Column> columnList = field.getColumn();
				for (Column column : columnList) {
					if (column.getType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
						if (fieldDictId.equals(column.getDictionaryId())) {
							Long fieldReferencedId = column.getReferenced();

							DataDictionary refTableDataDictionary = dataDictionaryDAO.findById(fieldReferencedId);
							if (refTableDataDictionary.getFieldType().equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
								Class<?> employeeClass = employeeVO.getClass();

								String getMethodName = "get" + refTableDataDictionary.getColumnName().replace("_", "");
								if (refTableDataDictionary.getColumnName()
										.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_STATIC_FIELD_STATUS)) {
									getMethodName = "is" + refTableDataDictionary.getColumnName();
								}

								Method employeeMethod = null;
								try {
									employeeMethod = employeeClass.getMethod(getMethodName);
								} catch (NoSuchMethodException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								} catch (SecurityException e) {
									LOGGER.error(e.getMessage(), e);
									throw new PayAsiaSystemException(e.getMessage(), e);
								}
								try {
									Object obj = employeeMethod.invoke(employeeVO);
									if (obj instanceof Timestamp) {
										fieldRValue = String.valueOf(DateUtils.timeStampToString(
												(Timestamp) employeeMethod.invoke(employeeVO), companyDateFormat));
									} else {
										fieldRValue = String.valueOf(employeeMethod.invoke(employeeVO));
									}

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
								if (StringUtils.isNotBlank(fieldRValue) && !fieldRValue.equalsIgnoreCase("null")) {

									field.setValue(fieldRValue);

								} else {
									field.setValue("");
								}

							} else {
								DynamicForm dynamicForm = null;
								if (dynamicFormMap.containsKey(refTableDataDictionary.getFormID())) {
									dynamicForm = dynamicFormMap.get(refTableDataDictionary.getFormID());
								} else {
									dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId,
											refTableDataDictionary.getEntityMaster().getEntityId(),
											refTableDataDictionary.getFormID());
									dynamicFormMap.put(dynamicForm.getId().getFormId(), dynamicForm);
								}

								if (dynamicForm != null) {
									Unmarshaller unmarshallerRef = null;
									try {
										unmarshallerRef = XMLUtil.getDocumentUnmarshaller();
									} catch (JAXBException jAXBException) {
										LOGGER.error(jAXBException.getMessage(), jAXBException);
										throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
									} catch (SAXException sAXException) {

										LOGGER.error(sAXException.getMessage(), sAXException);
										throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
									}
									final StringReader xmlRefReader = new StringReader(dynamicForm.getMetaData());
									Source xmlRefSource = null;
									try {
										xmlRefSource = XMLUtil.getSAXSource(xmlRefReader);
									} catch (SAXException | ParserConfigurationException e1) {
										LOGGER.error(e1.getMessage(), e1);
										throw new PayAsiaSystemException(e1.getMessage(), e1);
									}

									Tab tabRef = null;
									try {
										tabRef = (Tab) unmarshallerRef.unmarshal(xmlRefSource);
									} catch (JAXBException jAXBException) {
										LOGGER.error(jAXBException.getMessage(), jAXBException);
										throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
									}
									DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO.getEmpRecords(employeeId,
											dynamicForm.getId().getVersion(), dynamicForm.getId().getFormId(),
											dynamicForm.getEntityMaster().getEntityId(), companyId);

									List<Field> listOfRefFields = tabRef.getField();
									for (Field fieldR : listOfRefFields) {
										if (!fieldR.getType().equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
											if (dynamicFormRecord != null) {
												Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();
												if (fieldR.getDictionaryId()
														.equals(refTableDataDictionary.getDataDictionaryId())) {
													String fieldRName = fieldR.getName();
													String fieldRtype = fieldR.getType();

													String getMethodName = "getCol" + fieldRName.substring(
															fieldRName.lastIndexOf('_') + 1, fieldRName.length());

													try {
														Method dynamicFormRecordMethod = dynamicFormRecordClass
																.getMethod(getMethodName);
														try {
															fieldRValue = (String) dynamicFormRecordMethod
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

													if (fieldRtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
															&& !"".equalsIgnoreCase(fieldRValue)) {
														fieldRValue = DateUtils.convertDateToSpecificFormat(fieldRValue,
																companyDateFormat);
													}
													if (fieldRtype
															.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
															&& StringUtils.isNotBlank(fieldRValue)) {
														DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
																.findById(Long.parseLong(fieldRValue));

														try {
															String codeDescription = dynamicFormFieldRefValue
																	.getDescription() + "["
																	+ dynamicFormFieldRefValue.getCode() + "]";
															fieldRValue = codeDescription;
														} catch (Exception exception) {
															LOGGER.error(exception.getMessage(), exception);
															fieldRValue = "";
														}

													}
												}
											}

										} else {

											DynamicFormTableRecord refDynFormTableRecord = dynamicFormTableRecordDAO
													.findByIdAndSeq(tableId, seqNo);
											Class<?> dynamicTableClass = refDynFormTableRecord.getClass();
											List<Column> columnRefList = field.getColumn();
											for (Column columnRef : columnRefList) {
												if (columnRef.getDictionaryId()
														.equals(refTableDataDictionary.getDataDictionaryId())) {
													String colRName = columnRef.getName();

													String colNameCount = colRName.substring(
															colRName.lastIndexOf('_') + 1, colRName.length());

													String dynamicFormTableRecordMethodName = PayAsiaConstants.GET_COL
															+ colNameCount;

													Method dynamicFormTableRecordMethod = null;
													try {
														dynamicFormTableRecordMethod = dynamicTableClass
																.getMethod(dynamicFormTableRecordMethodName);
													} catch (NoSuchMethodException e) {
														LOGGER.error(e.getMessage(), e);
														throw new PayAsiaSystemException(e.getMessage(), e);
													} catch (SecurityException e) {
														LOGGER.error(e.getMessage(), e);
														throw new PayAsiaSystemException(e.getMessage(), e);
													}

													try {
														fieldRValue = (String) dynamicFormTableRecordMethod
																.invoke(refDynFormTableRecord);
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

													if (columnRef.getType()
															.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
															&& StringUtils.isNotBlank(fieldRValue)) {

														fieldRValue = DateUtils.convertDateToSpecificFormat(fieldRValue,
																companyDateFormat);

													} else if (columnRef.getType()
															.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
															&& StringUtils.isNotBlank(fieldRValue)) {

														DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
																.findById(Long.parseLong(fieldRValue));
														try {
															String codeDescription = dynamicFormFieldRefValue
																	.getDescription() + "["
																	+ dynamicFormFieldRefValue.getCode() + "]";
															fieldRValue = codeDescription;
														} catch (Exception exception) {
															LOGGER.error(exception.getMessage(), exception);
															fieldRValue = "";
														}

													}

												}
											}
										}

									}

								}
							}

						}

					}

				}
			}

		}
		if (StringUtils.isBlank(fieldRValue) && !"null".equalsIgnoreCase(fieldRValue)) {
			fieldRValue = "";
		}
		return fieldRValue;
	}

	@Override
	public String empGetPassword(Long companyId, Long loggedInEmployeeId, Long employeeId) {
		Employee loggedInEmployeeVO = employeeDAO.findById(loggedInEmployeeId);
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		String password = "";
		if (hrisPreferenceVO != null) {
			if (hrisPreferenceVO.isHideGetPassword()) {
				password = PayAsiaConstants.OTHER_COMPANY_NAME;
			} else {

				if (companyId == 1l) {
					ArrayList<String> roleList = new ArrayList<>();
					SecurityContext securityContext = SecurityContextHolder.getContext();
					Authentication authentication = securityContext.getAuthentication();
					for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
						if (grantedAuthority.getAuthority().equals("ROLE_SUPER ADMIN")) {
							roleList.add("ROLE_ADMIN");
						}
					}
					if (roleList.contains("ROLE_ADMIN")) {
						password = getDecryptedPassword(employeeId);
					} else {
						password = PayAsiaConstants.OTHER_COMPANY_NAME;
					}
				} else {
					if (loggedInEmployeeVO.getCompany().getCompanyName().toUpperCase()
							.equals(PayAsiaConstants.PAYASIA_COMPANY_NAME)) {
						password = getDecryptedPassword(employeeId);
					} else {
						if (hrisPreferenceVO.isShowPasswordAsPlainText()) {
							password = getDecryptedPassword(employeeId);

						} else {
							password = PayAsiaConstants.OTHER_COMPANY_NAME;
						}
					}
				}
			}

		} else {
			if (companyId == 1l) {
				ArrayList<String> roleList = new ArrayList<>();
				SecurityContext securityContext = SecurityContextHolder.getContext();
				Authentication authentication = securityContext.getAuthentication();
				for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
					if (grantedAuthority.getAuthority().equals("ROLE_SUPER ADMIN")) {
						roleList.add("ROLE_ADMIN");
					}
				}
				if (roleList.contains("ROLE_ADMIN")) {
					password = getDecryptedPassword(employeeId);
				} else {
					password = PayAsiaConstants.OTHER_COMPANY_NAME;
				}
			} else {
				if (loggedInEmployeeVO.getCompany().getCompanyName().toUpperCase()
						.equals(PayAsiaConstants.PAYASIA_COMPANY_NAME)) {
					password = getDecryptedPassword(employeeId);
				} else {
					password = PayAsiaConstants.OTHER_COMPANY_NAME;
				}
			}
		}
		return password;
	}

	private String getDecryptedPassword(Long employeeId) {
		Employee employeeVO = employeeDAO.findById(employeeId);
		String password = securityLogic.decrypt(employeeVO.getEmployeeLoginDetail().getPassword(),
				employeeVO.getEmployeeLoginDetail().getSalt());
		return password;
	}

	@Override
	public String isHideGetPassword(Long companyId) {

		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO == null) {
			return PayAsiaConstants.FALSE;
		} else if (hrisPreferenceVO.isHideGetPassword()) {
			return PayAsiaConstants.TRUE;
		} else if (!hrisPreferenceVO.isHideGetPassword()) {
			return PayAsiaConstants.FALSE;
		}
		return PayAsiaConstants.FALSE;

	}

	@Override
	public String getClientAdminEditDeleteEmpStatus(Long companyId) {

		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO == null) {
			return PayAsiaConstants.TRUE;
		} else if (hrisPreferenceVO.isClientAdminEditDeleteEmployee()) {
			return PayAsiaConstants.TRUE;
		} else if (!hrisPreferenceVO.isClientAdminEditDeleteEmployee()) {
			return PayAsiaConstants.FALSE;
		}
		return PayAsiaConstants.TRUE;

	}

	/**
	 * Update xml.
	 * 
	 * @param dynamicFormRecord
	 *            the dynamic form record
	 * @param metaData
	 *            the meta data
	 * @param companyDateFormat
	 *            the company date format
	 * @param mode
	 *            the mode
	 * @return the string
	 */
	private String updateXMLWithDefaultValue(String metaData, String companyDateFormat, String mode, Long companyId) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		boolean isEnableEmployeeChangeWorkflow = false;
		if (hrisPreferenceVO == null) {
			isEnableEmployeeChangeWorkflow = false;
		} else if (hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = true;
		} else if (!hrisPreferenceVO.isEnableEmployeeChangeWorkflow()) {
			isEnableEmployeeChangeWorkflow = false;
		}

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}

		final StringReader xmlReader = new StringReader(metaData);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XMLStreamWriter streamWriter = null;
		try {
			streamWriter = outputFactory.createXMLStreamWriter(byteArrayOutputStream);
		} catch (XMLStreamException xMLStreamException) {
			LOGGER.error(xMLStreamException.getMessage(), xMLStreamException);
			throw new PayAsiaSystemException(xMLStreamException.getMessage(), xMLStreamException);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldType = field.getType();
			String defaultValue = field.getDefaultValue();

			try {
				if (fieldType.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
					List<Column> columnList = field.getColumn();
					for (Column column : columnList) {
						String colType = column.getType();
						String colDefaultValue = column.getDefaultValue();
						if (colType.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)
								&& !"".equalsIgnoreCase(colDefaultValue)) {
							Employee employee = employeeDAO.findById(Long.parseLong(colDefaultValue));
							colDefaultValue = String.valueOf(employee.getEmployeeId());
						}

						try {
							if (StringUtils.isNotBlank(colDefaultValue)) {
								colDefaultValue = URLEncoder.encode(colDefaultValue, "UTF8");
							} else {
								colDefaultValue = "";
							}

						} catch (UnsupportedEncodingException unsupportedEncodingException) {
							LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
							throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
									unsupportedEncodingException);
						}
						column.setDefaultValue(colDefaultValue);
					}

				} else {
					if (StringUtils.isNotBlank(defaultValue)) {

						if (fieldType.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_lIST_REFERENCE)
								&& !"".equalsIgnoreCase(defaultValue)) {
							Employee employee = employeeDAO.findById(Long.parseLong(defaultValue));
							if ("viewmode".equalsIgnoreCase(mode)) {
								if (!isEnableEmployeeChangeWorkflow) {
									defaultValue = getEmployeeName(employee);
								} else {
									if (field.isReadOnly()) {
										defaultValue = getEmployeeName(employee);
									} else {
										defaultValue = String.valueOf(employee.getEmployeeId());
									}
								}
							} else {
								defaultValue = String.valueOf(employee.getEmployeeId());
							}

						}
						if (fieldType.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
								&& !"".equalsIgnoreCase(defaultValue)) {
							List<DynamicFormFieldRefValue> dynamicFormFieldRefValueList = dynamicFormFieldRefValueDAO
									.findByDataDictionayId(field.getDictionaryId());
							for (DynamicFormFieldRefValue fieldRefValue : dynamicFormFieldRefValueList) {
								if (fieldRefValue.getCode().equalsIgnoreCase(defaultValue)) {
									defaultValue = String.valueOf(fieldRefValue.getFieldRefValueId());
								}
							}

						}

						try {
							defaultValue = URLEncoder.encode(defaultValue, "UTF8");
						} catch (UnsupportedEncodingException unsupportedEncodingException) {
							LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
							throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(),
									unsupportedEncodingException);
						}
						field.setValue(defaultValue);

					} else {
						field.setValue("");
					}
				}

			} catch (SecurityException securityException) {
				LOGGER.error(securityException.getMessage(), securityException);
				throw new PayAsiaSystemException(securityException.getMessage(), securityException);
			} catch (IllegalArgumentException illegalArgumentException) {
				LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
				throw new PayAsiaSystemException(illegalArgumentException.getMessage(), illegalArgumentException);
			}

		}

		try {
			Marshaller m = XMLUtil.getDocumentMarshaller();
			m.marshal(tab, streamWriter);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}

		return byteArrayOutputStream.toString();

	}

	private String uploadEmpDocFile(CommonsMultipartFile file, String filePath, String fileNameSeperator) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = file.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

		if (!("").equals(fileName)) {
			UUID uuid = UUID.randomUUID();
			String fileNameWOExt = fileName.substring(0, fileName.indexOf('.'));
			if (fileNameWOExt.length() > 60) {
				fileNameWOExt = fileNameWOExt.substring(0, 55);
			}
			fileName = fileNameWOExt + fileNameSeperator + uuid + "." + ext;
			File newFile = new File(filePath, fileName);
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				awss3LogicImpl.uploadCommonMultipartFile(file, filePath + fileName);
			} else {
				try {
					FileOutputStream fos = new FileOutputStream(newFile);
					fos.write(file.getBytes());
					fos.flush();
					fos.close();
				} catch (IOException iOException) {
					LOGGER.error(iOException.getMessage(), iOException);
					throw new PayAsiaSystemException(iOException.getMessage(), iOException);
				}
			}
		}
		return fileName;

	}

	private String uploadEmpDocFile(MultipartFile file, String filePath, String fileNameSeperator) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = file.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

		if (!("").equals(fileName)) {
			UUID uuid = UUID.randomUUID();
			String fileNameWOExt = fileName.substring(0, fileName.indexOf('.'));
			if (fileNameWOExt.length() > 60) {
				fileNameWOExt = fileNameWOExt.substring(0, 55);
			}
			fileName = fileNameWOExt + fileNameSeperator + uuid + "." + ext;
			File newFile = new File(filePath, fileName);
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				awss3LogicImpl.uploadCommonMultipartFile(file, filePath + fileName);
			} else {
				try {
					FileOutputStream fos = new FileOutputStream(newFile);
					fos.write(file.getBytes());
					fos.flush();
					fos.close();
				} catch (IOException iOException) {
					LOGGER.error(iOException.getMessage(), iOException);
					throw new PayAsiaSystemException(iOException.getMessage(), iOException);
				}
			}
		}
		return fileName;

	}

	@Override
	public boolean areEmployeesOfSameCompanyGroup(String workingCompanyId, Long employeeId1, Long employeeId2) {

		if (employeeId1 == null || employeeId2 == null)
			return false;
		if (employeeId1.equals(employeeId2))
			return true;
		Employee employee1 = employeeDAO.findById(employeeId1);
		Employee employee2 = employeeDAO.findById(employeeId2);
		if (Long.valueOf(workingCompanyId).equals(employee1.getCompany().getCompanyId()) || employee1.getCompany()
				.getCompanyGroup().getGroupId() == employee2.getCompany().getCompanyGroup().getGroupId())
			return true;
		return false;
	}

	@Override
	public boolean isAdminAuthorizedForEmployee(Long empId, Long companyId, Long adminId) {
		if (empId == null || companyId == null)
			return false;
		boolean isEmpExist = employeeDAO.isEmployeeExistInCompany(empId, companyId);
		if (isEmpExist) {
			EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(adminId, companyId);
			if (employeeShortListDTO.getEmployeeShortList()) {
				isEmpExist = employeeShortListDTO.getShortListEmployeeIds().contains(BigInteger.valueOf(empId));
			}
			return isEmpExist;

		}

		else
			return isEmpExist;

	}

	@Override
	public boolean isAdminAuthorizedForViewGrid(Long viewId, Long companyId) {

		if (viewId == null || companyId == null)
			return false;
		return entityListViewDAO.isViewGridExistInCom(viewId, companyId);
	}

	public Long getEmpIdByEmpNoAndComId(String empNumber, Long companyId) {

		if (empNumber == null || companyId == null)
			return null;
		Long empId = employeeDAO.findByNumber(empNumber, companyId).getEmployeeId();

		return empId;
	}

	@Override
	public List<String> getAuthorizedEmployeeList(List<String> empNoList, Long companyId, Long adminId) {
		if (empNoList == null || empNoList.isEmpty()) {
			return null;
		}
		List<Long> empoyeeIdList1 = employeeDAO.findAllEmpIdByCompany(companyId);
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(adminId, companyId);
		if (employeeShortListDTO.getEmployeeShortList()) {

			List<Long> shortList = new ArrayList<Long>();
			for (BigInteger item : employeeShortListDTO.getShortListEmployeeIds()) {
				shortList.add(item.longValue());
			}

			empoyeeIdList1.retainAll(shortList);
		}
		List<Long> empNoLists = new ArrayList<Long>();
		for (String item : empNoList) {
			empNoLists.add(Long.parseLong(item));
		}

		empoyeeIdList1.retainAll(empNoLists);

		List<String> empoyeeIdList = new ArrayList<String>();
		for (Long empId : empoyeeIdList1) {
			empoyeeIdList.add(String.valueOf(empId));
		}
		return empoyeeIdList;

	}

	private boolean isUserAuthorized(Long companyId, Long employeeId, Long formId, Long tid) {

		DynamicForm dynamicFormForAuth = dynamicFormDAO.findMaxVersionByFormId(companyId, 1, formId);
		DynamicFormRecord empRecord = dynamicFormRecordDAO.getEmpRecords(employeeId,
				dynamicFormForAuth.getId().getVersion(), formId, 1L, null);

		String xmlMetaData = dynamicFormForAuth.getMetaData();

		Unmarshaller unmarshaller = null;

		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
			throw new PayAsiaSystemException(jaxbException.getMessage(), jaxbException);
		} catch (SAXException saxException) {
			LOGGER.error(saxException.getMessage(), saxException);
			throw new PayAsiaSystemException(saxException.getMessage(), saxException);
		}
		Tab tab = null;
		final StringReader xmlReader = new StringReader(xmlMetaData);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
			throw new PayAsiaSystemException(jaxbException.getMessage(), jaxbException);
		}

		List<Field> listOfFields = tab.getField();

		for (Field field : listOfFields) {
			if ((StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.TABLE_FIELD_TYPE))
					|| (StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DEPENDENTS))
					|| (StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_DOCUMENT))) {
				String colValue = getColValueFile(PayAsiaStringUtils.getColNumber(field.getName()), empRecord);
				if (StringUtils.isNotBlank(colValue) && StringUtils.equals(colValue, String.valueOf(tid))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public EmployeeListFormPage checkEmployeeNumberIsSame(String employeeNumber, Long companyId, String emailId,
			long empID) {

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {
			employeeNumber = URLDecoder.decode(employeeNumber, "UTF-8");
			Employee empObj = employeeDAO.findEmployee(employeeNumber, companyId, null);
			Employee empOriginalNo = employeeDAO.findById(empID);

			if (empObj != null && empOriginalNo != null
					&& empObj.getEmployeeNumber().equals(empOriginalNo.getEmployeeNumber())) {

				employeeListFormPage.setEmployeeNumberStatus(PayAsiaConstants.AVAILABLE);

			} else {
				employeeListFormPage.setEmployeeNumberStatus(PayAsiaConstants.NOTAVAILABLE);
				employeeListFormPage.setMessage("payasia.employee.id.cannot.update");
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return employeeListFormPage;
	}
}
