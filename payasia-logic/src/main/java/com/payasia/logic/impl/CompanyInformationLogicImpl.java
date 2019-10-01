package com.payasia.logic.impl;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Row;
import com.mind.payasia.xml.bean.RowValue;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CompanyConditionDTO;
import com.payasia.common.dto.CompanyCopyDTO;
import com.payasia.common.dto.CompanyDynamicFormDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CompanyCopyForm;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.CompanyFormResponse;
import com.payasia.common.form.CompanyListForm;
import com.payasia.common.form.EntityListViewFieldForm;
import com.payasia.common.form.EntityListViewForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyGroupDAO;
import com.payasia.dao.CountryMasterDAO;
import com.payasia.dao.CurrencyMasterDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.EntityListViewDAO;
import com.payasia.dao.EntityListViewFieldDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.FinancialYearMasterDAO;
import com.payasia.dao.LanguageMasterDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.PayslipFrequencyDAO;
import com.payasia.dao.RoleMasterDAO;
import com.payasia.dao.StateMasterDAO;
import com.payasia.dao.TimeZoneMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.CurrencyMaster;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.DynamicFormTableRecordPK;
import com.payasia.dao.bean.EntityListView;
import com.payasia.dao.bean.EntityListViewField;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.FinancialYearMaster;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.StateMaster;
import com.payasia.dao.bean.TimeZoneMaster;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.logic.MultilingualLogic;

/**
 * The Class CompanyInformationLogicImpl.
 */
/**
 * @author ragulapraveen
 * 
 */
@Component
public class CompanyInformationLogicImpl implements CompanyInformationLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CompanyInformationLogicImpl.class);

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The company group dao. */
	@Resource
	CompanyGroupDAO companyGroupDAO;

	/** The country master dao. */
	@Resource
	CountryMasterDAO countryMasterDAO;

	/** The state master dao. */
	@Resource
	StateMasterDAO stateMasterDAO;

	/** The financial year master dao. */
	@Resource
	FinancialYearMasterDAO financialYearMasterDAO;

	/** The entity list view dao. */
	@Resource
	EntityListViewDAO entityListViewDAO;

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The entity list view field dao. */
	@Resource
	EntityListViewFieldDAO entityListViewFieldDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The dynamic form record dao. */
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	/** The dynamic form table record dao. */
	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	/** The month master dao. */
	@Resource
	MonthMasterDAO monthMasterDAO;

	/** The payslip frequency dao. */
	@Resource
	PayslipFrequencyDAO payslipFrequencyDAO;

	/** The app code master dao. */
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	/** The currency master dao. */
	@Resource
	CurrencyMasterDAO currencyMasterDAO;

	/** The multilingual logic. */
	@Resource
	MultilingualLogic multilingualLogic;

	/** The language master dao. */
	@Resource
	LanguageMasterDAO languageMasterDAO;

	/** The employee role mapping dao. */
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	/** The time zone master dao. */
	@Resource
	TimeZoneMasterDAO timeZoneMasterDAO;

	/** The dynamic form field ref value dao. */
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;

	/** The role master dao. */
	@Resource
	RoleMasterDAO roleMasterDAO;

	@Resource
	MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#getCompanies(java.lang.String,
	 * java.lang.String, java.lang.Long, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public CompanyFormResponse getCompanies(String searchCondition,
			String searchText, Long empId, PageRequest pageDTO,
			SortCondition sortDTO) throws UnsupportedEncodingException

	{
		List<CompanyForm> companyListDTO = new ArrayList<CompanyForm>();
		try {
			searchText = URLDecoder.decode(searchText, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		CompanyConditionDTO conditionDTO = new CompanyConditionDTO();

		if (searchCondition.equalsIgnoreCase(PayAsiaConstants.COMPANY_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCompanyName("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equalsIgnoreCase(PayAsiaConstants.COMPANY_CODE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCompanyCode("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equalsIgnoreCase(PayAsiaConstants.GROUP_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setGroup(searchText);

			}

		}
		if (searchCondition.equalsIgnoreCase(PayAsiaConstants.GROUP_CODE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setGroup(searchText);

			}

		}
		if (searchCondition.equalsIgnoreCase(PayAsiaConstants.FINANCIAL_YEAR)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setFinancialYear(searchText);
			}

		}
		if (searchCondition.equalsIgnoreCase(PayAsiaConstants.COUNTRY)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setCountry(searchText);
			}

		}
		if (searchCondition
				.equalsIgnoreCase(PayAsiaConstants.PAYSLIP_FREQUENCY)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setPaySlipFrequency(searchText);
			}

		}

		List<Company> companyListVO = companyDAO.findByConditionAndEmployeeId(
				conditionDTO, empId, pageDTO, sortDTO);

		for (Company cmp : companyListVO) {
			CompanyForm company = new CompanyForm();
			/*ID ENCRYPT*/
			company.setCompanyId(FormatPreserveCryptoUtil.encrypt(cmp.getCompanyId()));
			company.setCompanyName(cmp.getCompanyName());
			company.setGroupName(cmp.getCompanyGroup().getGroupName());
			if (cmp.getFinancialYearMaster() != null) {
				company.setFinYear(cmp.getFinancialYearMaster()
						.getMonthMaster1().getMonthAbbr()
						+ "-"
						+ cmp.getFinancialYearMaster().getMonthMaster2()
								.getMonthAbbr());
			}
			company.setCountryName(cmp.getCountryMaster().getCountryName());
			company.setCompanyCode(cmp.getCompanyCode());
			company.setDateFormat(cmp.getDateFormat());
			company.setPaySlipfrequnecyValue(cmp.getPayslipFrequency()
					.getFrequency_Desc());
			company.setGroupCode(cmp.getCompanyGroup().getGroupCode());
			companyListDTO.add(company);

		}
		CompanyFormResponse CompanyFormResponse = new CompanyFormResponse();
		int recordSize = companyDAO.getCountForConditionAndEmployeeId(
				conditionDTO, empId);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			CompanyFormResponse.setPage(pageDTO.getPageNumber());
			CompanyFormResponse.setTotal(totalPages);

		}

		CompanyFormResponse.setRecords(recordSize);

		CompanyFormResponse.setRows(companyListDTO);
		return CompanyFormResponse;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#addCompany(com.payasia.common
	 * .form.CompanyForm)
	 */
	@Override
	public Long addCompany(CompanyForm companyForm) {

		Company company = new Company();
		company.setCompanyCode(companyForm.getCompanyCode().toLowerCase());
		try {
			company.setCompanyName(URLDecoder.decode(
					companyForm.getCompanyName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		if (companyForm.getFinancialYearId() != -1) {
			FinancialYearMaster financialYearMaster = financialYearMasterDAO
					.findById(companyForm.getFinancialYearId());
			company.setFinancialYearMaster(financialYearMaster);
		}

		CompanyGroup companyGroup = companyGroupDAO.findById(companyForm
				.getGroupId());
		company.setCompanyGroup(companyGroup);
		CountryMaster countryMaster = countryMasterDAO.findById(companyForm
				.getCountryID());
		company.setCountryMaster(countryMaster);
		company.setCompanyCode(companyForm.getCompanyCode());
		company.setDateFormat(companyForm.getDateFormatId());

		CurrencyMaster currencyMaster = currencyMasterDAO.findById(companyForm
				.getCurrencyId());
		company.setCurrencyMaster(currencyMaster);

		PayslipFrequency payslipFrequency = payslipFrequencyDAO
				.findByFrequency(PayAsiaConstants.PARTLY);
		company.setPayslipFrequency(payslipFrequency);
		company.setPart(companyForm.getPart());
		company.setPayslipImportType(companyForm.getPayslipImportType());
		TimeZoneMaster timeZoneMaster = timeZoneMasterDAO.findById(companyForm
				.getTimeZoneId());
		company.setTimeZoneMaster(timeZoneMaster);
		company.setAuditEnable(companyForm.getAudit());
		company.setActive(companyForm.isActive());
		company.setDemoCompany(companyForm.isDemoCompany());
		Company cmp = companyDAO.save(company);

		RoleMaster roleMasterAdmin = new RoleMaster();

		roleMasterAdmin.setRoleName(PayAsiaConstants.EMPLOYEE_ENTITY_ADMIN);
		roleMasterAdmin.setRoleDesc(PayAsiaConstants.EMPLOYEE_ENTITY_ADMIN);
		roleMasterAdmin.setCompany(cmp);
		roleMasterAdmin.setDeletable(false);
		roleMasterDAO.save(roleMasterAdmin);

		RoleMaster roleMasterEmployee = new RoleMaster();

		roleMasterEmployee.setRoleName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		roleMasterEmployee.setRoleDesc(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		roleMasterEmployee.setCompany(cmp);
		roleMasterEmployee.setDeletable(false);
		roleMasterDAO.save(roleMasterEmployee);

		return cmp.getCompanyId();

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
	public Long getEntityMasterId(String entityName,
			List<EntityMaster> entityMasterList) {
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
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#saveCustomView(java.lang.Long,
	 * java.lang.String, int, java.lang.String[], java.lang.String[])
	 */
	@Override
	public void saveCustomView(Long companyId, String viewName,
			int recordsPerPage, String[] dataDictionaryIdArr, String[] rowIndexs) {
		Long entityMasterId = 3l;

		EntityListView entityListView = new EntityListView();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().toUpperCase()
					.equals(PayAsiaConstants.COMPANY_ENTITY_NAME)) {
				entityMasterId = getEntityMasterId(
						PayAsiaConstants.COMPANY_ENTITY_NAME, entityMasterList);
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

		for (int count = 0; count < dataDictionaryIdArr.length; count++) {
			if (!dataDictionaryIdArr[count].equalsIgnoreCase("")) {
				DataDictionary dataDictionary = dataDictionaryDAO.findById(Long
						.parseLong(dataDictionaryIdArr[count]));
				entityListViewField.setDataDictionary(dataDictionary);
				entityListViewField.setSequence(Integer
						.parseInt(rowIndexs[count]));
				entityListViewFieldDAO.save(entityListViewField);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#updateCustomView(java.lang.
	 * Long, java.lang.String, int, java.lang.String[], java.lang.String[],
	 * java.lang.Long)
	 */
	@Override
	public void updateCustomView(Long companyId, String viewName,
			int recordsPerPage, String[] dataDictionaryIdArr,
			String[] rowIndexs, Long viewId) {

		EntityListView entityListView = entityListViewDAO.findById(viewId);

		entityListView.setViewName(viewName);
		entityListView.setRecordsPerPage(recordsPerPage);

		entityListViewDAO.update(entityListView);
		entityListViewFieldDAO.deleteByCondition(viewId);

		EntityListViewField entityListViewField = new EntityListViewField();
		entityListViewField.setEntityListView(entityListView);

		for (int count = 0; count < dataDictionaryIdArr.length; count++) {
			if (!dataDictionaryIdArr[count].equalsIgnoreCase("")) {
				DataDictionary dataDictionary = dataDictionaryDAO.findById(Long
						.parseLong(dataDictionaryIdArr[count]));
				entityListViewField.setDataDictionary(dataDictionary);
				entityListViewField.setSequence(Integer
						.parseInt(rowIndexs[count]));
				entityListViewFieldDAO.save(entityListViewField);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#getCustomColumnName(java.lang
	 * .Long)
	 */
	@Override
	public List<EntityListViewFieldForm> getCustomColumnName(Long viewID) {
		List<EntityListViewFieldForm> entityListViewFieldFormList = new ArrayList<EntityListViewFieldForm>();

		List<EntityListViewField> entityListViewFieldList = entityListViewFieldDAO
				.findByEntityListViewId(viewID);
		for (EntityListViewField entityListViewField : entityListViewFieldList) {
			EntityListViewFieldForm entityListViewFieldForm = new EntityListViewFieldForm();
			DataDictionary dataDictionary = entityListViewField
					.getDataDictionary();
			entityListViewFieldForm
					.setFieldName(dataDictionary.getColumnName());
			entityListViewFieldForm.setFieldLabel(dataDictionary
					.getDataDictName().replace("_", " "));

			entityListViewFieldForm.setRecords(entityListViewField
					.getEntityListView().getRecordsPerPage());
			entityListViewFieldFormList.add(entityListViewFieldForm);
		}

		return entityListViewFieldFormList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyInformationLogic#getCompanyGroup()
	 */
	@Override
	public List<CompanyForm> getCompanyGroup() {

		Set<CompanyForm> companySet = new HashSet<CompanyForm>();
		List<CompanyGroup> companyGroupList = companyGroupDAO.findAll();
		for (CompanyGroup companyGroup : companyGroupList) {
			CompanyForm companyForm = new CompanyForm();
			try {
				companyForm.setGroupName(URLEncoder.encode(
						companyGroup.getGroupName(), "UTF-8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
				throw new PayAsiaSystemException(
						unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			companyForm.setGroupId(companyGroup.getGroupId());
			companyForm.setGroupCode(companyGroup.getGroupCode());
			companySet.add(companyForm);
		}
		List<CompanyForm> companyList = new ArrayList<CompanyForm>(companySet);
		return companyList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyInformationLogic#getCountryList()
	 */
	@Override
	public List<CompanyForm> getCountryList() {
		List<CompanyForm> companyListDTO = new ArrayList<CompanyForm>();
		List<CountryMaster> countryListVO = countryMasterDAO.findAll();
		for (CountryMaster countryMaster : countryListVO) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setCountryID(countryMaster.getCountryId());
			companyForm.setCountryValue(countryMaster.getCountryName());
			companyListDTO.add(companyForm);
		}
		return companyListDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#getStateList(java.lang.Long)
	 */
	@Override
	public List<CompanyListForm> getStateList(Long countryId) {
		List<CompanyListForm> companyList = new ArrayList<CompanyListForm>();
		List<StateMaster> stateMasterList = stateMasterDAO
				.findByCountry(countryId);
		for (StateMaster stateMaster : stateMasterList) {
			CompanyListForm companyForm = new CompanyListForm();
			companyForm.setState(stateMaster.getStateName());
			companyForm.setStateId(stateMaster.getStateId());
			companyList.add(companyForm);
		}
		return companyList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#getViewName(java.lang.Long)
	 */
	@Override
	public List<EntityListViewForm> getViewName(Long companyId) {
		Long entityMasterId = 0l;

		List<EntityListViewForm> EntityListViewFormList = new ArrayList<EntityListViewForm>();

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().toUpperCase()
					.equals(PayAsiaConstants.COMPANY_ENTITY_NAME)) {
				entityMasterId = getEntityMasterId(
						PayAsiaConstants.COMPANY_ENTITY_NAME, entityMasterList);
			}
		}
		List<EntityListView> entityViewList = entityListViewDAO
				.findByConditionEntityAndCompanyId(companyId, entityMasterId);

		for (EntityListView entityListView : entityViewList) {
			EntityListViewForm entityListViewForm = new EntityListViewForm();
			entityListViewForm.setViewNameId(entityListView
					.getEntityListViewId());
			try {
				entityListViewForm.setViewName(URLEncoder.encode(
						entityListView.getViewName(), "UTF-8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
				throw new PayAsiaSystemException(
						unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			EntityListViewFormList.add(entityListViewForm);
		}

		return EntityListViewFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyInformationLogic#getFinancialYearList()
	 */
	@Override
	public List<CompanyForm> getFinancialYearList() {
		List<CompanyForm> companyFinancialYearListDTO = new ArrayList<CompanyForm>();
		List<FinancialYearMaster> financialYearListVO = financialYearMasterDAO
				.findAll();
		for (FinancialYearMaster financialYearMaster : financialYearListVO) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setFinancialYearId(financialYearMaster.getFinYearId());
			companyForm.setFinancialYearValue(financialYearMaster
					.getMonthMaster1().getMonthAbbr()
					+ " - "
					+ financialYearMaster.getMonthMaster2().getMonthAbbr());
			companyFinancialYearListDTO.add(companyForm);
		}
		return companyFinancialYearListDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyInformationLogic#getPaySlipFrequencyList()
	 */
	@Override
	public List<CompanyForm> getPaySlipFrequencyList() {
		List<CompanyForm> paySlipFrequencyListDTO = new ArrayList<CompanyForm>();
		List<PayslipFrequency> paySlipFrequencyListVO = payslipFrequencyDAO
				.findAll();
		for (PayslipFrequency payslipFrequency : paySlipFrequencyListVO) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setPaySlipFrequencyId(payslipFrequency
					.getPayslipFrequencyID());
			companyForm.setPaySlipfrequnecyValue(payslipFrequency
					.getFrequency_Desc());
			paySlipFrequencyListDTO.add(companyForm);
		}
		return paySlipFrequencyListDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyInformationLogic#getDateFormatList()
	 */
	@Override
	public List<CompanyForm> getDateFormatList() {

		List<CompanyForm> dateFormatListDTO = new ArrayList<CompanyForm>();
		List<AppCodeMaster> dateFormatListVO = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.DATE_FORMAT);
		for (AppCodeMaster appCodeMaster : dateFormatListVO) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setDateFormatId(appCodeMaster.getCodeValue());
			companyForm.setDateFormatValue(appCodeMaster.getCodeDesc());
			dateFormatListDTO.add(companyForm);
		}
		return dateFormatListDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#saveCompany(java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Integer,
	 * java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public CompanyForm saveCompany(String xml, Long companyID, Long entityID,
			Long formID, Integer version, Long entityKey,
			String companyDateFormat, String existingCompanyDateFormat) {
		CompanyForm cmpForm = new CompanyForm();

		DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
		DynamicFormTableRecord dynamicFormTableRecord;
		DynamicFormTableRecordPK dynamicFormTableRecordPK;

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
		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

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
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();
			String fieldValue = field.getValue();
			String fieldtype = field.getType();

			if (!fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF8");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
					&& !fieldValue.equalsIgnoreCase("")) {
				fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldValue,
						companyDateFormat);
			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				synchronized (this) {
					dynamicFormTableRecord = new DynamicFormTableRecord();
					dynamicFormTableRecordPK = new DynamicFormTableRecordPK();
					Class<?> dynamicFormTableRecordClass = dynamicFormTableRecord
							.getClass();

					Long maxTableRecordId = dynamicFormTableRecordDAO
							.getMaxTableRecordId() + 1;
					int seqNo = 1;
					List<Row> listOfRows = field.getRow();
					for (Row row : listOfRows) {
						List<RowValue> listOfRowvalue = row.getRowValue();

						for (RowValue rowValue : listOfRowvalue) {
							String colName = rowValue.getName();
							String colValue = null;
							try {
								colValue = URLDecoder.decode(
										rowValue.getValue(), "UTF8");
							} catch (UnsupportedEncodingException unsupportedEncodingException) {
								LOGGER.error(unsupportedEncodingException
										.getMessage(),
										unsupportedEncodingException);
								throw new PayAsiaSystemException(
										unsupportedEncodingException
												.getMessage(),
										unsupportedEncodingException);
							}

							String colType = rowValue.getType();

							if (colName
									.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)
									&& !colValue.equalsIgnoreCase("")) {
								colValue = DateUtils.appendTodayTime(DateUtils
										.convertDateFormatyyyyMMdd(colValue,
												companyDateFormat));

							} else if (colType
									.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !colValue.equalsIgnoreCase("")) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(
										colValue, companyDateFormat);
							}

							String tableRecordMethodName = PayAsiaConstants.SET_COL
									+ colName.substring(
											colName.lastIndexOf('_') + 1,
											colName.length());
							Method dynamicFormTableRecordMethod;
							try {
								dynamicFormTableRecordMethod = dynamicFormTableRecordClass
										.getMethod(tableRecordMethodName,
												String.class);
								if (StringUtils.isNotBlank(colValue)) {
									dynamicFormTableRecordMethod.invoke(
											dynamicFormTableRecord, colValue);
								}
							} catch (SecurityException securityException) {
								LOGGER.error(securityException.getMessage(),
										securityException);
								throw new PayAsiaSystemException(
										securityException.getMessage(),
										securityException);
							} catch (NoSuchMethodException noSuchMethodException) {

								LOGGER.error(
										noSuchMethodException.getMessage(),
										noSuchMethodException);
								throw new PayAsiaSystemException(
										noSuchMethodException.getMessage(),
										noSuchMethodException);
							} catch (IllegalArgumentException illegalArgumentException) {
								LOGGER.error(
										illegalArgumentException.getMessage(),
										illegalArgumentException);
								throw new PayAsiaSystemException(
										illegalArgumentException.getMessage(),
										illegalArgumentException);
							} catch (IllegalAccessException illegalAccessException) {
								LOGGER.error(
										illegalAccessException.getMessage(),
										illegalAccessException);
								throw new PayAsiaSystemException(
										illegalAccessException.getMessage(),
										illegalAccessException);
							} catch (InvocationTargetException invocationTargetException) {
								LOGGER.error(
										invocationTargetException.getMessage(),
										invocationTargetException);
								throw new PayAsiaSystemException(
										invocationTargetException.getMessage(),
										invocationTargetException);
							}

						}

						dynamicFormTableRecordPK
								.setDynamicFormTableRecordId(maxTableRecordId);
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
						+ fieldName.substring(fieldName.lastIndexOf('_') + 1,
								fieldName.length());
				Method dynamicFormRecordMethod = dynamicFormRecordClass
						.getMethod(methodName, String.class);
				try {
					if (StringUtils.isNotBlank(fieldValue)) {
						dynamicFormRecordMethod.invoke(dynamicFormRecord,
								fieldValue);
					}
				} catch (IllegalArgumentException | IllegalAccessException
						| InvocationTargetException exception) {
					LOGGER.error(exception.getMessage(), exception);
					throw new PayAsiaSystemException(exception.getMessage(),
							exception);

				}
			} catch (SecurityException e) {

				LOGGER.error(e.getMessage(), e);
			} catch (NoSuchMethodException e) {

				LOGGER.error(e.getMessage(), e);
			}

		}

		dynamicFormRecord.setCompany_ID(entityKey);
		dynamicFormRecord.setEntity_ID(entityID);
		dynamicFormRecord.setForm_ID(formID);
		dynamicFormRecord.setVersion(version);
		dynamicFormRecord.setEntityKey(entityKey);
		DynamicFormRecord dynamicFormRecordVO = dynamicFormRecordDAO
				.saveReturn(dynamicFormRecord);
		cmpForm.setDynamicFormRecordId(dynamicFormRecordVO.getRecordId());
		return cmpForm;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#checkCompany(com.payasia.common
	 * .form.CompanyForm)
	 */
	@Override
	public CompanyForm checkCompany(CompanyForm companyForm) {

		CompanyForm cmpForm = new CompanyForm();
		CompanyConditionDTO conditionDTO = new CompanyConditionDTO();

		try {
			conditionDTO.setCompanyName(URLDecoder.decode(
					companyForm.getCompanyName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		conditionDTO.setGroupId(companyForm.getGroupId());

		conditionDTO.setCompanyId(companyForm.getCompanyId());

		Company cmpCompanyCode = companyDAO.findByCompanyCode(
				companyForm.getCompanyCode(), null);

		if (cmpCompanyCode != null) {
			cmpForm.setCompanyStatus(PayAsiaConstants.AVAILABLE);
			cmpForm.setCompanyId(cmpCompanyCode.getCompanyId());
			cmpForm.setMessage("payasia.company.code.already.exists");

		} else {

			Company cmp = companyDAO.findByCondition(conditionDTO);

			if (cmp != null) {
				cmpForm.setCompanyStatus(PayAsiaConstants.AVAILABLE);
				cmpForm.setCompanyId(cmp.getCompanyId());
				cmpForm.setMessage("payasia.company.already.exists");

			} else {
				cmpForm.setCompanyStatus(PayAsiaConstants.NOTAVAILABLE);
			}

		}

		return cmpForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#checkCompany(com.payasia.common
	 * .form.CompanyForm, java.lang.Long)
	 */
	@Override
	public CompanyForm checkCompany(CompanyForm companyForm, Long companyId) {

		CompanyForm cmpForm = new CompanyForm();
		CompanyConditionDTO conditionDTO = new CompanyConditionDTO();
		try {
			conditionDTO.setCompanyName(URLDecoder.decode(
					companyForm.getCompanyName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		conditionDTO.setGroupId(companyForm.getGroupId());

		conditionDTO.setCompanyId(companyId);

		Company cmpCompanyCode = companyDAO.findByCompanyCode(
				companyForm.getCompanyCode(), companyId);

		if (cmpCompanyCode != null) {
			cmpForm.setCompanyStatus(PayAsiaConstants.AVAILABLE);
			cmpForm.setCompanyId(cmpCompanyCode.getCompanyId());
			cmpForm.setMessage("payasia.company.code.already.exists");

		} else {

			Company cmp = companyDAO.findByCondition(conditionDTO);

			if (cmp != null) {
				cmpForm.setCompanyStatus(PayAsiaConstants.AVAILABLE);
				cmpForm.setCompanyId(cmp.getCompanyId());
				cmpForm.setMessage("payasia.company.already.exists");

			} else {
				cmpForm.setCompanyStatus(PayAsiaConstants.NOTAVAILABLE);
			}

		}

		return cmpForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#getCompanyUpdatedXmls(java.
	 * lang.Long, long, java.lang.Long)
	 */
	@Override
	public CompanyForm getCompanyUpdatedXmls(Long companyId, long entityKey,
			Long languageId) {

		Long entityId = null;
        
		Company cmpVO = companyDAO.findById(entityKey);

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.COMPANY_ENTITY_NAME)) {
				entityId = getEntityMasterId(
						PayAsiaConstants.COMPANY_ENTITY_NAME, entityMasterList);
			}
		}

		CompanyForm companyDTO = new CompanyForm();
		List<CompanyForm> tabDataList = new ArrayList<CompanyForm>();
		List<Long> formIdList = dynamicFormDAO.getDistinctFormId(companyId,
				entityId);
		int dynamicFormCount = 0;

		for (Long dynamicFormId : formIdList) {
			CompanyForm cmp = new CompanyForm();
			CompanyDynamicFormDTO companyDynamicFormDTO = new CompanyDynamicFormDTO();
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
					companyId, entityId, formIdList.get(dynamicFormCount));
			cmp.setCompanyId(dynamicForm.getId().getCompany_ID());
			companyDynamicFormDTO.setEntityId(dynamicForm.getId()
					.getEntity_ID());
			companyDynamicFormDTO.setFormId(dynamicForm.getId().getFormId());
			companyDynamicFormDTO.setVersion(dynamicForm.getId().getVersion());

			DynamicFormRecord cmpRecord = dynamicFormRecordDAO.getEmpRecords(
					entityKey, dynamicForm.getId().getVersion(), dynamicFormId,
					entityId, null);

			if (cmpRecord != null) {
				companyDynamicFormDTO.setTabID(cmpRecord.getRecordId());
				companyDynamicFormDTO.setMetaData(multilingualLogic
						.convertLabelsToSpecificLanguage(
								updateXML(cmpRecord, dynamicForm.getMetaData(),
										cmpVO.getDateFormat()), languageId,
								companyId, entityId, dynamicForm.getId()
										.getFormId()));
			} else {
				companyDynamicFormDTO.setMetaData(multilingualLogic
						.convertLabelsToSpecificLanguage(dynamicForm
								.getMetaData(), languageId, companyId,
								entityId, dynamicForm.getId().getFormId()));
			}

			companyDynamicFormDTO.setTabName(dynamicForm.getTabName());
			cmp.setCompanyDynamicFormDTO(companyDynamicFormDTO);

			tabDataList.add(cmp);
			dynamicFormCount++;
		}

		companyDTO.setUpdatedCompanyList(tabDataList);
		companyDTO.setActive(cmpVO.isActive());
		companyDTO.setDemoCompany(cmpVO.isDemoCompany());
		companyDTO.setCompanyName(cmpVO.getCompanyName());
		companyDTO.setCompanyCode(cmpVO.getCompanyCode());
		companyDTO.setPayslipImportType(cmpVO.getPayslipImportType());
		companyDTO.setPaySlipFrequencyId(cmpVO.getPayslipFrequency()
				.getPayslipFrequencyID());
		companyDTO.setGroupId(cmpVO.getCompanyGroup().getGroupId());
		companyDTO
				.setFinancialYearId(cmpVO.getFinancialYearMaster() == null ? -1
						: cmpVO.getFinancialYearMaster().getFinYearId());
		companyDTO.setCountryID(cmpVO.getCountryMaster().getCountryId());
		companyDTO.setDateFormatId(cmpVO.getDateFormat());
		companyDTO.setTimeZoneId(cmpVO.getTimeZoneMaster() == null ? -1 : cmpVO
				.getTimeZoneMaster().getTimeZoneId());
		companyDTO.setAudit(cmpVO.getAuditEnable());
		if (cmpVO.getPayslipFrequency().getFrequency()
				.equalsIgnoreCase(PayAsiaConstants.PARTLY)) {
			companyDTO.setPart(cmpVO.getPart());
		}

		CurrencyMaster currencyMaster = cmpVO.getCurrencyMaster();
		if (currencyMaster != null) {
			companyDTO.setCurrencyId(cmpVO.getCurrencyMaster().getCurrencyId());
		}
		return companyDTO;

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
	 * @return the string
	 */
	private String updateXML(DynamicFormRecord dynamicFormRecord,
			String metaData, String companyDateFormat) {

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
		Class<?> c = dynamicFormRecord.getClass();

		final StringReader xmlReader = new StringReader(metaData);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XMLStreamWriter streamWriter = null;
		try {
			streamWriter = outputFactory
					.createXMLStreamWriter(byteArrayOutputStream);
		} catch (XMLStreamException xMLStreamException) {
			LOGGER.error(xMLStreamException.getMessage(), xMLStreamException);
			throw new PayAsiaSystemException(xMLStreamException.getMessage(),
					xMLStreamException);
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
			String fieldName = field.getName();
			String fieldType = field.getType();
			String value;

			try {
				String methodName = PayAsiaConstants.GET_COL
						+ fieldName.substring(fieldName.lastIndexOf('_') + 1,
								fieldName.length());
				Method m = c.getMethod(methodName);
				try {

					value = (String) m.invoke(dynamicFormRecord);

					if (value != null) {

						if (fieldType
								.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
								&& !value.equalsIgnoreCase("")) {
							value = DateUtils.convertDateToSpecificFormat(
									value, companyDateFormat);
						}
					}

					field.setValue(value);
				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(),
							illegalArgumentException);
					throw new PayAsiaSystemException(
							illegalArgumentException.getMessage(),
							illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(),
							illegalAccessException);
					throw new PayAsiaSystemException(
							illegalAccessException.getMessage(),
							illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(),
							invocationTargetException);
					throw new PayAsiaSystemException(
							invocationTargetException.getMessage(),
							invocationTargetException);
				}
			} catch (SecurityException securityException) {
				LOGGER.error(securityException.getMessage(), securityException);
				throw new PayAsiaSystemException(
						securityException.getMessage(), securityException);
			} catch (NoSuchMethodException noSuchMethodException) {

				LOGGER.error(noSuchMethodException.getMessage(),
						noSuchMethodException);
				throw new PayAsiaSystemException(
						noSuchMethodException.getMessage(),
						noSuchMethodException);
			}

		}

		try {
			Marshaller m = XMLUtil.getDocumentMarshaller();
			m.marshal(tab, streamWriter);
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		} catch (SAXException sAXException) {

			LOGGER.error(sAXException.getMessage(), sAXException);
			throw new PayAsiaSystemException(sAXException.getMessage(),
					sAXException);
		}

		return byteArrayOutputStream.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#tableRecordList(java.lang.Long,
	 * int, java.lang.String[], java.lang.String[], java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public CompanyForm tableRecordList(Long tid, int ColumnCount,
			String[] fieldNames, String[] fieldTypes, Long companyId,
			String editCompanyJqueryDateFormat) {
		String companyDateFormat = editCompanyJqueryDateFormat;

		List<DynamicFormTableRecord> tableRecordList = dynamicFormTableRecordDAO
				.getTableRecords(tid,
						PayAsiaConstants.SORT_ORDER_NEWEST_TO_OLDEST, "");
		List<CompanyForm> tableRecords = new ArrayList<CompanyForm>();
		int count;
		CompanyForm companyForm = null;

		String fieldNameCount;
		String fieldName;
		String fieldType;
		String dynamicFormTableRecordValue;
		for (DynamicFormTableRecord dynamicFormTableRecord : tableRecordList) {
			companyForm = new CompanyForm();
			CompanyDynamicFormDTO companyDynamicFormDTO = new CompanyDynamicFormDTO();
			for (count = 1; count <= ColumnCount; count++) {
				fieldName = fieldNames[count - 1];
				fieldType = fieldTypes[count - 1];
				fieldNameCount = fieldName.substring(
						fieldName.lastIndexOf('_') + 1, fieldName.length());
				Class<?> dynamicTableClass = dynamicFormTableRecord.getClass();
				Class<?> companyDynamicFormDTOClass = companyDynamicFormDTO
						.getClass();
				String dynamicFormTableRecordMethodName = PayAsiaConstants.GET_COL
						+ fieldNameCount;
				String cmpListFormMethodName = PayAsiaConstants.SET_COL + count;

				try {
					Method dynamicFormTableRecordMethod = dynamicTableClass
							.getMethod(dynamicFormTableRecordMethodName);
					Method cmpListFormMethod = companyDynamicFormDTOClass
							.getMethod(cmpListFormMethodName, String.class);

					dynamicFormTableRecordValue = (String) dynamicFormTableRecordMethod
							.invoke(dynamicFormTableRecord);

					if (fieldName.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
						dynamicFormTableRecordValue = DateUtils
								.convertDateFormat(dynamicFormTableRecordValue,
										companyDateFormat);

					} else if (fieldType
							.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
							&& dynamicFormTableRecordValue != null
							&& !dynamicFormTableRecordValue
									.equalsIgnoreCase("")) {

						dynamicFormTableRecordValue = DateUtils
								.convertDateToSpecificFormat(
										dynamicFormTableRecordValue,
										companyDateFormat);

					} else if (fieldType
							.equalsIgnoreCase(PayAsiaConstants.CODEDESC_FIELD_TYPE)
							&& dynamicFormTableRecordValue != null
							&& !dynamicFormTableRecordValue
									.equalsIgnoreCase("")) {

						DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
								.findById(Long
										.parseLong(dynamicFormTableRecordValue));
						dynamicFormTableRecordValue = dynamicFormFieldRefValue
								.getCode();

					}
					if (StringUtils.isNotBlank(dynamicFormTableRecordValue)) {
						cmpListFormMethod.invoke(companyDynamicFormDTO,
								dynamicFormTableRecordValue);
					}

				} catch (SecurityException securityException) {
					LOGGER.error(securityException.getMessage(),
							securityException);
					throw new PayAsiaSystemException(
							securityException.getMessage(), securityException);
				} catch (NoSuchMethodException noSuchMethodException) {

					LOGGER.error(noSuchMethodException.getMessage(),
							noSuchMethodException);
					throw new PayAsiaSystemException(
							noSuchMethodException.getMessage(),
							noSuchMethodException);
				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(),
							illegalArgumentException);
					throw new PayAsiaSystemException(
							illegalArgumentException.getMessage(),
							illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(),
							illegalAccessException);
					throw new PayAsiaSystemException(
							illegalAccessException.getMessage(),
							illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(),
							invocationTargetException);
					throw new PayAsiaSystemException(
							invocationTargetException.getMessage(),
							invocationTargetException);
				}
			}
			companyForm.setTableRecordId(dynamicFormTableRecord.getId()
					.getSequence());
			companyForm.setCompanyDynamicFormDTO(companyDynamicFormDTO);
			tableRecords.add(companyForm);
		}

		CompanyForm companyFormDTO = new CompanyForm();
		companyFormDTO.setTableDataList(tableRecords);

		return companyFormDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#updateCompany(com.payasia.common
	 * .form.CompanyForm, java.lang.Long)
	 */
	@Override
	public void updateCompany(CompanyForm companyForm, Long entityKey) {
		Company company = companyDAO.findById(entityKey);

		company.setCompanyCode(companyForm.getCompanyCode().toLowerCase());
		try {
			company.setCompanyName(URLDecoder.decode(
					companyForm.getCompanyName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		if (companyForm.getFinancialYearId() != -1) {
			FinancialYearMaster financialYearMaster = financialYearMasterDAO
					.findById(companyForm.getFinancialYearId());
			company.setFinancialYearMaster(financialYearMaster);
		}

		CompanyGroup companyGroup = companyGroupDAO.findById(companyForm
				.getGroupId());
		company.setCompanyGroup(companyGroup);
		CountryMaster countryMaster = countryMasterDAO.findById(companyForm
				.getCountryID());
		company.setCountryMaster(countryMaster);
		company.setCompanyCode(companyForm.getCompanyCode());
		company.setDateFormat(companyForm.getDateFormatId());
		company.setPart(companyForm.getPart());
		company.setPayslipImportType(companyForm.getPayslipImportType());

		PayslipFrequency payslipFrequency = payslipFrequencyDAO
				.findByFrequency(PayAsiaConstants.PARTLY);
		company.setPayslipFrequency(payslipFrequency);
		CurrencyMaster currencyMaster = currencyMasterDAO.findById(companyForm
				.getCurrencyId());
		company.setCurrencyMaster(currencyMaster);
		TimeZoneMaster timeZoneMaster = timeZoneMasterDAO.findById(companyForm
				.getTimeZoneId());
		company.setTimeZoneMaster(timeZoneMaster);
		if (companyForm.getAudit() != null) {
			company.setAuditEnable(companyForm.getAudit());
		}
		company.setActive(companyForm.isActive());
		company.setDemoCompany(companyForm.isDemoCompany());
		companyDAO.update(company);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#updateCompanyDynamicFormRecord
	 * (java.lang.String, java.lang.Long, java.lang.Long, java.lang.Long,
	 * java.lang.Integer, java.lang.Long, java.lang.Long, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void updateCompanyDynamicFormRecord(String xml, Long companyID,
			Long entityID, Long formID, Integer version, Long entityKey,
			Long tabID, String companyDateFormat,
			String existingCompanyDateFormat) {

		DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO
				.findById(tabID);

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
		Class<?> dynamicFormRecordClass = dynamicFormRecord.getClass();

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
		} catch (JAXBException jAXBException) {
			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			String fieldName = field.getName();

			String fieldValue = field.getValue();

			String fieldtype = field.getType();

			if (!fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF8");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
					&& !fieldValue.equalsIgnoreCase("")) {
				fieldValue = DateUtils.convertDateFormatyyyyMMdd(DateUtils
						.convertDate(fieldValue, existingCompanyDateFormat,
								companyDateFormat), companyDateFormat);
			}

			try {
				String methodName = PayAsiaConstants.SET_COL
						+ fieldName.substring(fieldName.lastIndexOf('_') + 1,
								fieldName.length());
				Method dynamicFormRecordMethod = dynamicFormRecordClass
						.getMethod(methodName, String.class);
				try {

					dynamicFormRecordMethod.invoke(dynamicFormRecord,
							fieldValue);

				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(),
							illegalArgumentException);
					throw new PayAsiaSystemException(
							illegalArgumentException.getMessage(),
							illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(),
							illegalAccessException);
					throw new PayAsiaSystemException(
							illegalAccessException.getMessage(),
							illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(),
							invocationTargetException);
					throw new PayAsiaSystemException(
							invocationTargetException.getMessage(),
							invocationTargetException);
				}
			} catch (SecurityException securityException) {
				LOGGER.error(securityException.getMessage(), securityException);
				throw new PayAsiaSystemException(
						securityException.getMessage(), securityException);
			} catch (NoSuchMethodException noSuchMethodException) {

				LOGGER.error(noSuchMethodException.getMessage(),
						noSuchMethodException);
				throw new PayAsiaSystemException(
						noSuchMethodException.getMessage(),
						noSuchMethodException);
			}

		}

		dynamicFormRecordDAO.update(dynamicFormRecord);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#deleteCompany(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public void deleteCompany(Long cmpId, Long employeeId) {
		companyDAO.deleteCompanyProc(cmpId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyInformationLogic#editView(java.lang.Long,
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
			if (entityMaster.getEntityName().toUpperCase()
					.equals(PayAsiaConstants.COMPANY_ENTITY_NAME)) {
				entityMasterId = getEntityMasterId(
						PayAsiaConstants.COMPANY_ENTITY_NAME, entityMasterList);
			}
		}

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntity(entityMasterId, fieldType);
		List<EntityListViewField> viewIds = entityListViewFieldDAO
				.findByEntityListViewId(viewId);

		for (DataDictionary dataDictionary : dataDictionaryList) {

			entityListViewField = new EntityListViewFieldForm();
			entityListViewField.setFieldName(dataDictionary.getDataDictName());
			entityListViewField.setDataDictionaryId(dataDictionary
					.getDataDictionaryId());
			for (EntityListViewField viewID : viewIds) {
				if (viewID.getDataDictionary().getDataDictionaryId() == dataDictionary
						.getDataDictionaryId()) {
					entityListViewField.setStatus(PayAsiaConstants.CHECKED);
					entityListViewField.setViewName(viewID.getEntityListView()
							.getViewName());
					entityListViewField.setRecords(viewID.getEntityListView()
							.getRecordsPerPage());
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
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#listEditView(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public List<EntityListViewFieldForm> listEditView(Long companyId,
			Long viewId) {
		Locale locale = UserContext.getLocale();
		Long entityMasterId = null;
		String fieldType = PayAsiaConstants.STATIC_TYPE;
		boolean status = false;

		List<EntityListViewFieldForm> entityListViewFieldList = new ArrayList<EntityListViewFieldForm>();
		EntityListViewFieldForm entityListViewField;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().toUpperCase()
					.equals(PayAsiaConstants.COMPANY_ENTITY_NAME)) {
				entityMasterId = getEntityMasterId(
						PayAsiaConstants.COMPANY_ENTITY_NAME, entityMasterList);
			}
		}

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntity(entityMasterId, fieldType);
		List<EntityListViewField> viewIds = entityListViewFieldDAO
				.findByEntityListViewId(viewId);

		for (EntityListViewField viewID : viewIds) {

			entityListViewField = new EntityListViewFieldForm();

			if (StringUtils.isNotBlank(viewID.getDataDictionary()
					.getDescription())) {
				String staticLabelMsg = messageSource.getMessage(viewID
						.getDataDictionary().getDescription(), new Object[] {},
						locale);
				if (StringUtils.isNotBlank(staticLabelMsg)) {
					entityListViewField.setFieldName(staticLabelMsg);
				} else {
					entityListViewField.setFieldName(viewID.getDataDictionary()
							.getDataDictName());
				}
			} else {
				entityListViewField.setFieldName(viewID.getDataDictionary()
						.getDataDictName());
			}

			entityListViewField.setDataDictionaryId(viewID.getDataDictionary()
					.getDataDictionaryId());
			entityListViewFieldList.add(entityListViewField);

		}

		for (DataDictionary dataDictionary : dataDictionaryList) {

			for (EntityListViewField viewID : viewIds) {

				if (viewID.getDataDictionary().getDataDictionaryId() == dataDictionary
						.getDataDictionaryId()) {
					status = true;

				}
			}

			if (status == false) {
				entityListViewField = new EntityListViewFieldForm();

				if (StringUtils.isNotBlank(dataDictionary.getDescription())) {
					String staticLabelMsg = messageSource.getMessage(
							dataDictionary.getDescription(), new Object[] {},
							locale);
					if (StringUtils.isNotBlank(staticLabelMsg)) {
						entityListViewField.setFieldName(staticLabelMsg);
					} else {
						entityListViewField.setFieldName(dataDictionary
								.getDataDictName());
					}
				} else {
					entityListViewField.setFieldName(dataDictionary
							.getDataDictName());
				}

				entityListViewField.setDataDictionaryId(dataDictionary
						.getDataDictionaryId());
				entityListViewFieldList.add(entityListViewField);
			}
			status = false;
		}

		return entityListViewFieldList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyInformationLogic#deleteView(java.lang.Long)
	 */
	@Override
	public void deleteView(Long viewId) {

		EntityListView entityListView = entityListViewDAO.findById(viewId);
		entityListViewDAO.delete(entityListView);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyInformationLogic#getCurrencyName()
	 */
	@Override
	public List<CompanyForm> getCurrencyName() {
		List<CompanyForm> currencyDefinitionList = new ArrayList<CompanyForm>();
		List<CurrencyMaster> currencyMasterList = currencyMasterDAO.findAll();
		for (CurrencyMaster currencyMaster : currencyMasterList) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setCurrency(currencyMaster.getCurrencyName());
			companyForm.setCurrencyId(currencyMaster.getCurrencyId());
			currencyDefinitionList.add(companyForm);
		}

		return currencyDefinitionList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#copyCompany(com.payasia.common
	 * .form.CompanyCopyForm, int)
	 */
	@Override
	public Boolean copyCompany(CompanyCopyForm companyCopyForm, int companyId,
			Long employeeId) {

		CompanyCopyDTO companyCopyDTO = new CompanyCopyDTO();
		BeanUtils.copyProperties(companyCopyForm, companyCopyDTO);
		companyCopyDTO.setCompanyId(companyId);
		companyCopyDTO.setEmployeeId(employeeId);
		CompanyCopyDTO returnedCopyDTO = companyDAO.copyCompany(companyCopyDTO);

		HashMap<Long, Long> oldNewDictionaries = returnedCopyDTO
				.getDataDictionaryMap();
		Long newCompanyId = returnedCopyDTO.getNewCompanyID();

		Integer payslipDesignerCount = dynamicFormDAO.getDynamicFormCount(
				Long.parseLong(String.valueOf(companyId)),
				PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
		if (companyCopyForm.getCopyPayslipDesigner()
				&& payslipDesignerCount > 0) {
			changePayslipDesignerCongif(newCompanyId, oldNewDictionaries);
		}

		Integer payslipFormCount = dynamicFormDAO.getDynamicFormCount(
				Long.parseLong(String.valueOf(companyId)),
				PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);
		if (companyCopyForm.getCopyPayslipFormDesigner()
				&& payslipFormCount > 0) {
			changeFormulaFields(newCompanyId, oldNewDictionaries);
		}

		if (companyCopyForm.getCopyEmployeeFormDesigner()) {
			changeEmployeeFormDesignerByNewDataDictIds(newCompanyId,
					oldNewDictionaries);
		}
		return returnedCopyDTO.getStatus();

	}

	/**
	 * Employee Form Designer : Change Old Data Dictionary Ids By New Data
	 * Dictionary Ids
	 * 
	 * @param newCompanyId
	 *            the new company id
	 * @param oldNewDictionaries
	 *            the old new dictionaries
	 */
	private void changeEmployeeFormDesignerByNewDataDictIds(Long newCompanyId,
			HashMap<Long, Long> oldNewDictionaries) {
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(newCompanyId,
				entityMaster.getEntityId());

		for (Long formId : formIds) {
			DynamicForm maxDynamicForm = dynamicFormDAO.findMaxVersionByFormId(
					newCompanyId, entityMaster.getEntityId(), formId);
			dynamicFormDAO.getDetachedEntity(maxDynamicForm);
			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = XMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
			}

			final StringReader xmlReader = new StringReader(
					maxDynamicForm.getMetaData());
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(),
						e1);
			}
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			try {
				streamWriter = outputFactory
						.createXMLStreamWriter(byteArrayOutputStream);
			} catch (XMLStreamException xmlStreamException) {
				LOGGER.error(xmlStreamException.getMessage(),
						xmlStreamException);
			}
			Tab tab = null;
			try {
				tab = (Tab) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			}
			List<Field> listOfFields = tab.getField();
			for (Field field : listOfFields) {
				Long oldFieldDataDictId = field.getDictionaryId();
				if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
					if (oldFieldDataDictId != null) {
						Long newDataDicId = oldNewDictionaries
								.get(oldFieldDataDictId);
						if (newDataDicId != null) {
							field.setDictionaryId(newDataDicId);
						}
					}
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						Long oldColumnDataDictId = column.getDictionaryId();
						if (StringUtils.equalsIgnoreCase(column.getType(),
								PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
							String formula = column.getFormula();
							if (column
									.getType()
									.equalsIgnoreCase(
											PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC)) {
								List<Long> dataDictionaries = new ArrayList<Long>();
								String formulaEx = column.getFormula();
								while (!formulaEx.equals("")) {
									if (formulaEx.indexOf('{') != -1) {
										String fex = formulaEx.substring(
												formulaEx.indexOf('{') + 1,
												formulaEx.indexOf('}'));
										dataDictionaries.add(Long
												.parseLong(fex));
										formulaEx = formulaEx
												.substring(formulaEx
														.indexOf('}') + 1);
									} else {
										break;
									}
								}
								for (Long dataDicId : dataDictionaries) {
									Long newDataDicId = oldNewDictionaries
											.get(dataDicId);
									if (newDataDicId != null) {
										formula = formula.replace(
												Long.toString(dataDicId),
												Long.toString(newDataDicId));
									}

								}
								column.setFormula(formula);
							} else if (column.getType().equalsIgnoreCase(
									PayAsiaConstants.FORMULA_FIELD_TYPE_STRING)
									|| column
											.getType()
											.equalsIgnoreCase(
													PayAsiaConstants.FORMULA_FIELD_TYPE_DATE)) {
								List<Long> dataDictionaries = new ArrayList<Long>();
								String formulaEx = column.getFormula();
								while (!formulaEx.equals("")) {
									if (formulaEx.indexOf('{') != -1) {
										String fex = formulaEx.substring(
												formulaEx.indexOf('(') + 1,
												formulaEx.indexOf(')'));
										dataDictionaries.add(Long.parseLong(fex
												.substring(
														fex.indexOf('{') + 1,
														fex.indexOf('}'))));
										formulaEx = formulaEx
												.substring(formulaEx
														.indexOf(')') + 1);
									} else {
										break;
									}
								}
								for (Long dataDicId : dataDictionaries) {
									Long newDataDicId = oldNewDictionaries
											.get(dataDicId);
									if (newDataDicId != null) {
										formula = formula.replace(
												Long.toString(dataDicId),
												Long.toString(newDataDicId));
									}

								}
								column.setFormula(formula);
							}
						} else if (column.getType().equalsIgnoreCase(
								PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
							Long fieldReferencedId = column.getReferenced();
							if (fieldReferencedId != null) {
								Long newColumnDataDicId = oldNewDictionaries
										.get(fieldReferencedId);
								if (newColumnDataDicId != null) {
									column.setReferenced(newColumnDataDicId);
								}
							}

						} else {
							if (oldColumnDataDictId != null) {
								Long newColumnDataDicId = oldNewDictionaries
										.get(oldColumnDataDictId);
								if (newColumnDataDicId != null) {
									column.setDictionaryId(newColumnDataDicId);
								}
							}
						}

					}

				} else {
					if (StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
						String formula = field.getFormula();
						if (field.getType().equalsIgnoreCase(
								PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC)) {
							List<Long> dataDictionaries = new ArrayList<Long>();
							String formulaEx = field.getFormula();
							while (!formulaEx.equals("")) {
								if (formulaEx.indexOf('{') != -1) {
									String fex = formulaEx.substring(
											formulaEx.indexOf('{') + 1,
											formulaEx.indexOf('}'));
									dataDictionaries.add(Long.parseLong(fex));
									formulaEx = formulaEx.substring(formulaEx
											.indexOf('}') + 1);
								} else {
									break;
								}
							}
							for (Long dataDicId : dataDictionaries) {
								Long newDataDicId = oldNewDictionaries
										.get(dataDicId);
								if (newDataDicId != null) {
									formula = formula.replace(
											Long.toString(dataDicId),
											Long.toString(newDataDicId));
								}

							}
							field.setFormula(formula);
						} else if (field.getType().equalsIgnoreCase(
								PayAsiaConstants.FORMULA_FIELD_TYPE_STRING)
								|| field.getType()
										.equalsIgnoreCase(
												PayAsiaConstants.FORMULA_FIELD_TYPE_DATE)) {
							List<Long> dataDictionaries = new ArrayList<Long>();
							String formulaEx = field.getFormula();
							while (!formulaEx.equals("")) {
								if (formulaEx.indexOf('{') != -1) {
									String fex = formulaEx.substring(
											formulaEx.indexOf('(') + 1,
											formulaEx.indexOf(')'));
									dataDictionaries.add(Long.parseLong(fex
											.substring(fex.indexOf('{') + 1,
													fex.indexOf('}'))));
									formulaEx = formulaEx.substring(formulaEx
											.indexOf(')') + 1);
								} else {
									break;
								}
							}
							for (Long dataDicId : dataDictionaries) {
								Long newDataDicId = oldNewDictionaries
										.get(dataDicId);
								if (newDataDicId != null) {
									formula = formula.replace(
											Long.toString(dataDicId),
											Long.toString(newDataDicId));
								}

							}
							field.setFormula(formula);
						}
					} else if (field.getType().equalsIgnoreCase(
							PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
						Long fieldReferencedId = field.getReferenced();
						if (fieldReferencedId != null) {
							Long newColumnDataDicId = oldNewDictionaries
									.get(fieldReferencedId);
							if (newColumnDataDicId != null) {
								field.setReferenced(newColumnDataDicId);
							}
						}

					} else {
						if (oldFieldDataDictId != null) {
							Long newDataDicId = oldNewDictionaries
									.get(oldFieldDataDictId);
							if (newDataDicId != null) {
								field.setDictionaryId(newDataDicId);
							}
						}
					}

				}
			}

			Marshaller marshaller = null;

			try {
				marshaller = XMLUtil.getDocumentMarshaller();
				marshaller.marshal(tab, streamWriter);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
			}

			maxDynamicForm.setMetaData(byteArrayOutputStream.toString());
			dynamicFormDAO.update(maxDynamicForm);

		}

	}

	/**
	 * Change formula fields.
	 * 
	 * @param newCompanyId
	 *            the new company id
	 * @param oldNewDictionaries
	 *            the old new dictionaries
	 */
	private void changeFormulaFields(Long newCompanyId,
			HashMap<Long, Long> oldNewDictionaries) {
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(newCompanyId,
				entityMaster.getEntityId());

		for (Long formId : formIds) {
			DynamicForm maxDynamicForm = dynamicFormDAO.findMaxVersionByFormId(
					newCompanyId, entityMaster.getEntityId(), formId);
			dynamicFormDAO.getDetachedEntity(maxDynamicForm);
			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = XMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
			}

			final StringReader xmlReader = new StringReader(
					maxDynamicForm.getMetaData());
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(),
						e1);
			}
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			try {
				streamWriter = outputFactory
						.createXMLStreamWriter(byteArrayOutputStream);
			} catch (XMLStreamException xmlStreamException) {
				LOGGER.error(xmlStreamException.getMessage(),
						xmlStreamException);
			}
			Tab tab = null;
			try {
				tab = (Tab) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			}
			List<Field> listOfFields = tab.getField();
			for (Field field : listOfFields) {
				if (StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
					String formula = field.getFormula();
					if (formula.substring(0, 1).equalsIgnoreCase("Y")) {
						Long ytmFieldId = Long
								.parseLong(formula.substring(
										formula.indexOf('{') + 1,
										formula.indexOf('}')));

						Long newDataDicId = oldNewDictionaries.get(ytmFieldId);
						if (newDataDicId != null) {
							formula = "YTM{" + newDataDicId + "}";
						}

					} else {

						List<Long> dataDictionaries = new ArrayList<>();
						String formulaEx = field.getFormula();

						while (!formulaEx.equals("")) {
							dataDictionaries.add(Long.parseLong(formulaEx
									.substring(formulaEx.indexOf('{') + 1,
											formulaEx.indexOf('}'))));

							formulaEx = formulaEx.substring(formulaEx
									.indexOf('}') + 1);
						}

						for (Long dataDicId : dataDictionaries) {
							Long newDataDicId = oldNewDictionaries
									.get(dataDicId);
							if (newDataDicId != null) {
								formula = formula.replace(
										Long.toString(dataDicId),
										Long.toString(newDataDicId));
							}

						}
					}
					field.setFormula(formula);
				}
			}

			Marshaller marshaller = null;

			try {
				marshaller = XMLUtil.getDocumentMarshaller();
				marshaller.marshal(tab, streamWriter);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
			}

			maxDynamicForm.setMetaData(byteArrayOutputStream.toString());
			dynamicFormDAO.update(maxDynamicForm);

		}

	}

	/**
	 * Change payslip designer congif.
	 * 
	 * @param companyId
	 *            the company id
	 * @param oldNewDictionaries
	 *            the old new dictionaries
	 * @throws FactoryConfigurationError
	 *             the factory configuration error
	 */
	private void changePayslipDesignerCongif(Long companyId,
			HashMap<Long, Long> oldNewDictionaries)
			throws FactoryConfigurationError {
		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);

		List<String> sectionNames = new ArrayList<>();
		sectionNames.add(PayAsiaConstants.FOOTER_SECTION);
		sectionNames.add(PayAsiaConstants.LOGO_SECTION);
		sectionNames.add(PayAsiaConstants.HEADER_SECTION);
		sectionNames.add(PayAsiaConstants.TOTAL_INCOME_SECTION);
		sectionNames.add(PayAsiaConstants.STATUTORY_SECTION);
		sectionNames.add(PayAsiaConstants.SUMMARY_SECTION);

		for (String sectionName : sectionNames) {
			int maxVersion = dynamicFormDAO.getMaxVersion(companyId,
					entityMaster.getEntityId(), sectionName);
			DynamicForm maxDynamicForm = dynamicFormDAO.findByMaxVersion(
					companyId, entityMaster.getEntityId(), maxVersion,
					sectionName);

			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = XMLUtil.getDocumentUnmarshaller();
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
			}

			final StringReader xmlReader = new StringReader(
					maxDynamicForm.getMetaData());
			Source xmlSource = null;
			try {
				xmlSource = XMLUtil.getSAXSource(xmlReader);
			} catch (SAXException | ParserConfigurationException e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new PayAsiaSystemException(e1.getMessage(),
						e1);
			}
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			XMLStreamWriter streamWriter = null;
			try {
				streamWriter = outputFactory
						.createXMLStreamWriter(byteArrayOutputStream);
			} catch (XMLStreamException xmlStreamException) {
				LOGGER.error(xmlStreamException.getMessage(),
						xmlStreamException);
			}
			Tab tab = null;
			try {
				tab = (Tab) unmarshaller.unmarshal(xmlSource);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			}
			List<Field> listOfFields = tab.getField();
			for (Field field : listOfFields) {

				if (!StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.TABLE_FIELD_TYPE)
						&& !StringUtils.equalsIgnoreCase(field.getType(),
								PayAsiaConstants.LABEL_FIELD_TYPE)) {
					Long newDataDicId = oldNewDictionaries.get(field
							.getDictionaryId());
					if (newDataDicId != null) {
						field.setDictionaryId(newDataDicId);
					}
				}
			}

			Marshaller marshaller = null;

			try {
				marshaller = XMLUtil.getDocumentMarshaller();
				marshaller.marshal(tab, streamWriter);
			} catch (JAXBException jaxbException) {
				LOGGER.error(jaxbException.getMessage(), jaxbException);
			} catch (SAXException saxException) {
				LOGGER.error(saxException.getMessage(), saxException);
			}

			maxDynamicForm.setMetaData(byteArrayOutputStream.toString());
			dynamicFormDAO.update(maxDynamicForm);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#checkView(java.lang.String,
	 * java.lang.Long)
	 */
	@Override
	public CompanyForm checkView(String viewName, Long companyId) {
		CompanyForm companyForm = new CompanyForm();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		Long entityMasterID = null;
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.COMPANY_ENTITY_NAME)) {
				entityMasterID = getEntityMasterId(
						PayAsiaConstants.COMPANY_ENTITY_NAME, entityMasterList);
			}
		}

		EntityListView entityListView = entityListViewDAO
				.findByEntityIdCompanyIdAndViewName(companyId, entityMasterID,
						viewName);

		if (entityListView != null) {
			companyForm.setStatus(PayAsiaConstants.AVAILABLE);
		} else {
			companyForm.setStatus(PayAsiaConstants.NOTAVAILABLE);
		}

		return companyForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#checkViewUpdate(java.lang.String
	 * , java.lang.Long, java.lang.Long)
	 */
	@Override
	public CompanyForm checkViewUpdate(String viewName, Long companyId,
			Long viewId) {
		CompanyForm companyForm = new CompanyForm();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		Long entityMasterID = null;
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.COMPANY_ENTITY_NAME)) {
				entityMasterID = getEntityMasterId(
						PayAsiaConstants.COMPANY_ENTITY_NAME, entityMasterList);
			}
		}

		EntityListView entityListView = entityListViewDAO
				.findByEntityIdCompanyIdAndViewNameViewId(companyId,
						entityMasterID, viewName, viewId);

		if (entityListView != null) {
			companyForm.setStatus(PayAsiaConstants.AVAILABLE);
		} else {
			companyForm.setStatus(PayAsiaConstants.NOTAVAILABLE);
		}

		return companyForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyInformationLogic#getTimeZoneList()
	 */
	@Override
	public List<CompanyForm> getTimeZoneList() {
		List<CompanyForm> timeZoneList = new ArrayList<CompanyForm>();
		List<TimeZoneMaster> timeZoneListVO = timeZoneMasterDAO.findAll();
		for (TimeZoneMaster timeZoneMaster : timeZoneListVO) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setTimeZoneId(timeZoneMaster.getTimeZoneId());
			companyForm.setTimeZoneName(timeZoneMaster.getTimeZoneName());
			timeZoneList.add(companyForm);
		}
		return timeZoneList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#saveCompanyTableRecord(java
	 * .lang.String, java.lang.Long, java.lang.Long, java.lang.Long,
	 * java.lang.Integer, java.lang.Long)
	 */
	@Override
	public CompanyForm saveCompanyTableRecord(String tableXML, Long tabId,
			Long companyId, Long formId, Integer version, Long entityKey) {
		Integer seqNo;
		Long entityId = null;

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.COMPANY_ENTITY_NAME)) {
				entityId = getEntityMasterId(
						PayAsiaConstants.COMPANY_ENTITY_NAME, entityMasterList);
			}
		}

		CompanyForm companyForm = new CompanyForm();

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
			String fieldName = field.getName();
			fieldValue = field.getValue();
			String fieldtype = field.getType();

			if (fieldtype.equalsIgnoreCase(PayAsiaConstants.TABLE_FIELD_TYPE)) {
				synchronized (this) {

					Long maxTableRecordId;
					if (fieldValue.equalsIgnoreCase("")) {

						maxTableRecordId = dynamicFormTableRecordDAO
								.getMaxTableRecordId() + 1;
						seqNo = 1;
					} else {
						maxTableRecordId = Long.parseLong(fieldValue);
						Integer maxSeqNo = dynamicFormTableRecordDAO
								.getMaxSequenceNumber(maxTableRecordId);
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

							String colName = rowValue.getName();
							String colValue;
							try {
								colValue = URLDecoder.decode(
										rowValue.getValue(), "UTF8");
							} catch (UnsupportedEncodingException unsupportedEncodingException) {
								LOGGER.error(unsupportedEncodingException
										.getMessage(),
										unsupportedEncodingException);
								throw new PayAsiaSystemException(
										unsupportedEncodingException
												.getMessage(),
										unsupportedEncodingException);
							}

							String colType = rowValue.getType();

							if (colName
									.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								colValue = DateUtils.appendTodayTime(DateUtils
										.convertDateFormatyyyyMMdd(colValue,
												companyDateFormat));

							} else if (colType
									.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !colValue.equalsIgnoreCase("")) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(
										colValue, companyDateFormat);
							}

							String tableRecordMethodName = PayAsiaConstants.SET_COL
									+ colName.substring(
											colName.lastIndexOf('_') + 1,
											colName.length());
							Method dynamicFormTableRecordMethod;
							try {
								dynamicFormTableRecordMethod = DynamicFormTableRecord.class
										.getMethod(tableRecordMethodName,
												String.class);
								if (StringUtils.isNotBlank(colValue)) {
									dynamicFormTableRecordMethod.invoke(
											dynamicFormTableRecord, colValue);
								}
							} catch (SecurityException | NoSuchMethodException
									| IllegalArgumentException
									| IllegalAccessException
									| InvocationTargetException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										e.getMessage(), e);
							}

						}

						fieldValue = maxTableRecordId.toString();
						dynamicFormTableRecordPK
								.setDynamicFormTableRecordId(maxTableRecordId);
						dynamicFormTableRecordPK.setSequence(seqNo);
						dynamicFormTableRecord.setId(dynamicFormTableRecordPK);
						dynamicFormTableRecordDAO.save(dynamicFormTableRecord);

					}
				}

			}
			try {
				String methodName = "setCol"
						+ fieldName.substring(fieldName.lastIndexOf('_') + 1,
								fieldName.length());
				Method dynamicFormRecordMethod = dynamicFormRecordClass
						.getMethod(methodName, String.class);
				try {
					if (StringUtils.isNotBlank(fieldValue)) {
						dynamicFormRecordMethod.invoke(dynamicFormRecord,
								fieldValue);
					}
				} catch (IllegalArgumentException illegalArgumentException) {
					LOGGER.error(illegalArgumentException.getMessage(),
							illegalArgumentException);
					throw new PayAsiaSystemException(
							illegalArgumentException.getMessage(),
							illegalArgumentException);
				} catch (IllegalAccessException illegalAccessException) {
					LOGGER.error(illegalAccessException.getMessage(),
							illegalAccessException);
					throw new PayAsiaSystemException(
							illegalAccessException.getMessage(),
							illegalAccessException);
				} catch (InvocationTargetException invocationTargetException) {
					LOGGER.error(invocationTargetException.getMessage(),
							invocationTargetException);
					throw new PayAsiaSystemException(
							invocationTargetException.getMessage(),
							invocationTargetException);
				}
			} catch (SecurityException securityException) {
				LOGGER.error(securityException.getMessage(), securityException);
				throw new PayAsiaSystemException(
						securityException.getMessage(), securityException);
			} catch (NoSuchMethodException noSuchMethodException) {

				LOGGER.error(noSuchMethodException.getMessage(),
						noSuchMethodException);
				throw new PayAsiaSystemException(
						noSuchMethodException.getMessage(),
						noSuchMethodException);
			}

		}
		if (tabId == 0) {
			DynamicFormRecord dynamicRecordVO = dynamicFormRecordDAO
					.saveReturn(dynamicFormRecord);
			companyForm.setDynamicFormRecordId(dynamicRecordVO.getRecordId());
			companyForm.setDynamicFormTableRecordId(Long.parseLong(fieldValue));
			companyForm.setMode("SAVE");

		} else {
			companyForm.setDynamicFormTableRecordId(Long.parseLong(fieldValue));
			dynamicFormRecordDAO.update(dynamicFormRecord);
			companyForm.setMode("UPDATE");
		}

		return companyForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#updateCompanyTableRecord(java
	 * .lang.String, java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public CompanyForm updateCompanyTableRecord(String tableXML, Long tabId,
			Long companyId, Integer seqNo) {
		CompanyForm companyForm = new CompanyForm();

		Company cmp = companyDAO.findById(companyId);
		String companyDateFormat = cmp.getDateFormat();

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

							String colName = rowValue.getName();
							String colValue;
							try {
								colValue = URLDecoder.decode(
										rowValue.getValue(), "UTF8");
							} catch (UnsupportedEncodingException unsupportedEncodingException) {
								LOGGER.error(unsupportedEncodingException
										.getMessage(),
										unsupportedEncodingException);
								throw new PayAsiaSystemException(
										unsupportedEncodingException
												.getMessage(),
										unsupportedEncodingException);
							}

							String colType = rowValue.getType();

							if (colName
									.equalsIgnoreCase(PayAsiaConstants.CUSTCOL_1)) {
								colValue = DateUtils.appendTodayTime(DateUtils
										.convertDateFormatyyyyMMdd(colValue,
												companyDateFormat));

							} else if (colType
									.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)
									&& !colValue.equalsIgnoreCase("")) {
								colValue = DateUtils.convertDateFormatyyyyMMdd(
										colValue, companyDateFormat);
							}

							String tableRecordMethodName = PayAsiaConstants.SET_COL
									+ colName.substring(
											colName.lastIndexOf('_') + 1,
											colName.length());
							Method dynamicFormTableRecordMethod;
							try {
								dynamicFormTableRecordMethod = DynamicFormTableRecord.class
										.getMethod(tableRecordMethodName,
												String.class);

								dynamicFormTableRecordMethod.invoke(
										dynamicFormTableRecord, colValue);

							} catch (SecurityException | NoSuchMethodException
									| IllegalArgumentException
									| IllegalAccessException
									| InvocationTargetException e) {
								LOGGER.error(e.getMessage(), e);
								throw new PayAsiaSystemException(
										e.getMessage(), e);
							}

						}

						fieldValue = maxTableRecordId.toString();
						dynamicFormTableRecordDAO
								.update(dynamicFormTableRecord);

					}
				}

			}

		}
		return companyForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyInformationLogic#deleteCompanyTableRecord(java
	 * .lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public CompanyForm deleteCompanyTableRecord(Long tableId, Long companyId,
			Integer seqNo) {
		CompanyForm companyForm = new CompanyForm();
		dynamicFormTableRecordDAO.deleteByConditionEmployeeTableRecord(tableId,
				seqNo);
		return companyForm;
	}

	@Override
	public List<CompanyForm> getCompanyList() {
		List<CompanyForm> companyForms = new ArrayList<>();

		SortCondition sort = null;
		List<Company> companyList = companyDAO.findAll(sort);
		for (Company company : companyList) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setCompanyName(company.getCompanyName());
			companyForm.setCompanyId(company.getCompanyId());
			companyForms.add(companyForm);
		}
		return companyForms;
	}

	@Override
	public Map<Long, CompanyForm> getCompanyGroupYEP() {

		List<CompanyGroup> companyGroupList = companyGroupDAO.findAll();
		Map<Long, CompanyForm> companyGroupMap = new HashMap<>();
		for (CompanyGroup companyGroup : companyGroupList) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setGroupName(companyGroup.getGroupName());
			companyForm.setGroupId(companyGroup.getGroupId());
			companyForm.setGroupCode(companyGroup.getGroupCode());
			companyGroupMap.put(companyGroup.getGroupId(), companyForm);
		}
		return companyGroupMap;

	}

	@Override
	public String getShortCompanyCode(Long companyId) {
		String shortCompanyCode = "";
		Company companyVO = companyDAO.findById(companyId);
		if (companyVO != null) {
			shortCompanyCode = companyVO.getShortCompanyCode();
		}
		return shortCompanyCode;
	}
	
	@Override
	public Long getCompanyIdByCode(String companyCode){
		return companyDAO.findByCompanyCode(companyCode, null).getCompanyId();
	}
	
}
