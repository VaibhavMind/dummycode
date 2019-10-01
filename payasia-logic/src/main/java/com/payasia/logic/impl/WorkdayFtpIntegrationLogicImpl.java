package com.payasia.logic.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.mind.payasia.xml.bean.workday.empdata.EmployeeType;
import com.mind.payasia.xml.bean.workday.empdata.PayrollExtractEmployees;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DateFormatDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.FtpData;
import com.payasia.common.dto.FtpFileDataDTO;
import com.payasia.common.dto.WorkdayFieldMappingDTO;
import com.payasia.common.dto.WorkdayFieldMappingDataTransformationDTO;
import com.payasia.common.dto.WorkdayFtpImportHistoryDTO;
import com.payasia.common.exception.PayAsiaBusinessException;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CalculatoryFieldForm;
import com.payasia.common.form.CalculatoryFieldFormResponse;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.FTPImportHistoryForm;
import com.payasia.common.form.FTPImportHistoryFormResponse;
import com.payasia.common.form.FieldDataTransformationForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkdayDataFtpConfigForm;
import com.payasia.common.form.WorkdayFtpConfigForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FTPUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.WorkdayEmployeeDataXMLUtil;
import com.payasia.common.util.WorkdayPayrollDataXMLUtil;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.WorkdayAppCodeMasterDAO;
import com.payasia.dao.WorkdayFieldMappingDataTransformationDAO;
import com.payasia.dao.WorkdayFieldMasterDAO;
import com.payasia.dao.WorkdayFtpConfigDAO;
import com.payasia.dao.WorkdayFtpFieldMappingDAO;
import com.payasia.dao.WorkdayFtpImportHistoryDAO;
import com.payasia.dao.WorkdayPaygroupBatchDAO;
import com.payasia.dao.WorkdayPaygroupBatchDataDAO;
import com.payasia.dao.WorkdayPaygroupDAO;
import com.payasia.dao.WorkdayPaygroupEmployeeTimeOffDAO;
import com.payasia.dao.WorkdayPaygroupEmployeeTimeTrackingDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.WorkdayAppCodeMaster;
import com.payasia.dao.bean.WorkdayFieldMappingDataTransformation;
import com.payasia.dao.bean.WorkdayFieldMaster;
import com.payasia.dao.bean.WorkdayFtpConfig;
import com.payasia.dao.bean.WorkdayFtpFieldMapping;
import com.payasia.dao.bean.WorkdayFtpImportHistory;
import com.payasia.dao.bean.WorkdayPaygroupBatch;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;
import com.payasia.dao.bean.WorkdayPaygroupEmployeeTimeOff;
import com.payasia.dao.bean.WorkdayPaygroupEmployeeTimeTracking;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.DataImportLogic;
import com.payasia.logic.EmployeeDynamicFormLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.WorkdayFtpIntegrationLogic;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@Component
public class WorkdayFtpIntegrationLogicImpl extends BaseLogic implements WorkdayFtpIntegrationLogic {

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	EmployeeDynamicFormLogic employeeDynamicFormLogic;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	@Resource
	WorkdayFtpConfigDAO workdayFtpConfigDao;

	@Resource
	WorkdayFieldMasterDAO workdayFieldMasterDAO;

	@Resource
	WorkdayFtpFieldMappingDAO workdayFTPFieldMappingDAO;

	@Resource
	WorkdayFieldMappingDataTransformationDAO workdayFieldMappingDataTransformationDAO;

	@Resource
	WorkdayFtpImportHistoryDAO workdayFtpImportHistoryDAO;

	@Resource
	WorkdayPaygroupDAO workdayPaygroupDAO;

	@Resource
	WorkdayAppCodeMasterDAO workdayAppCodeMasterDAO;

	@Resource
	WorkdayPaygroupBatchDAO workdayPaygroupBatchDAO;

	@Resource
	WorkdayPaygroupBatchDataDAO workdayPaygroupBatchDataDAO;

	@Resource
	WorkdayPaygroupEmployeeTimeOffDAO workdayPaygroupEmployeeTimeOffDAO;

	@Resource
	WorkdayPaygroupEmployeeTimeTrackingDAO workdayPaygroupEmployeeTimeTrackingDAO;

	@Resource
	FileUtils fileUtils;

	@Resource
	AWSS3Logic awss3LogicImpl;

	@Resource
	DataImportLogic dataImportLogic;

	@Resource
	GeneralMailLogic generalMailLogic;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	private final static String CATEGORY_PAYGROUP = "Paygroup";

	private final static String WORKDAY_FTP_LOG_FOLDER = "WorkdayFtpLog";

	private final static String WORKDAY_FTP_IMPORT_EMF = "EMF";

	private final static String WORKDAY_FTP_IMPORT_PTRX = "PTRX";

	private final static String DATA_LOG_FILE = "Data";

	private final static String ERROR_LOG_FILE = "Error";

	private final static String WORKDAY_PICOF_HEADER_DATE_FORMAT = "yyyy-MM-dd";

	private final static String WORKDAY_SECTION_FIELD_SEPARATOR = "~";

	private final static String MSG_FILE_NOT_FOR_COMPANY = "File_Not_For_Company";

	private final static String WD_SECTION_PAYGROUP = "PayGroup";

	private final static String WD_SECTION_HEADER = "Header";

	private final static String WD_SECTION_EMPLOYEE = "Employee";

	private final static String WD_SECTION_SUMMARY = "Summary";

	private final static String WD_SECTION_EMF_PERSONAL = "Personal";

	private final static String WD_SECTION_EMF_POSITION = "Position";

	private final static String WD_SECTION_EMF_STATUS = "Status";

	private final static String WD_SECTION_EMF_ADDITIONAL_INFORMATION = "Additional_Information";

	private final static String WD_SECTION_EMF_ALLOWANCE_PLANS = "Allowance_Plans";

	private final static String WD_SECTION_EMF_SALARY_AND_HOURLY_PLANS = "Salary_and_Hourly_Plans";

	private final static String WD_SECTION_EMF_EARNINGS_DEDUCTIONS = "Earnings_Deductions";

	private final static String WD_SECTION_EMF_PAYMENT_ELECTION = "Payment_Election";

	private final static String WD_SECTION_EMF_IDENTIFIERS = "Identifier";

	private final static String WD_SECTION_PTRX_TIME_TRACKING = "Time_Tracking";

	private final static String WD_SECTION_PTRX_TIME_OFF = "Time_Off";

	private final static String WD_SECTION_PTRX_TIME_TRACKING_CORRECTION = "Time_Tracking_Correction";

	private final static String WD_SECTION_PTRX_TIME_OFF_CORRECTION = "Time_Off_Correction";

	private static final Logger LOGGER = Logger.getLogger(WorkdayFtpIntegrationLogicImpl.class);

	private static final List<String> employeeSectionList = new ArrayList<>(Arrays.asList("Summary", "Status",
			"Position", "Compensation", "Allowance_Plans", "Salary_and_Hourly_Plans", "Identifier", "Payment_Election",
			"Related_Person", "Earnings_Deductions", "Additional_Information"));

	private static final List<String> dateFieldList = new ArrayList<>(
			Arrays.asList("Pay_Period_Start", "Pay_Period_End", "Birth_Date", "Staffing_Event_Date", "Hire_Date",
					"Termination_Date", "Effective_Date", "Compensation_Effective_Date", "Start_Date"));

	@Override
	public List<DateFormatDTO> getdateFormatList() {
		List<AppCodeMaster> dateFormatListVO = appCodeMasterDAO.findByCondition("Date Format");
		List<DateFormatDTO> dateFormatListDTO = new ArrayList<DateFormatDTO>();
		for (AppCodeMaster appCodeMaster : dateFormatListVO) {
			DateFormatDTO dateFormat = new DateFormatDTO();
			dateFormat.setDateFormatValue(appCodeMaster.getCodeValue());
			dateFormat.setDateFormatDesc(appCodeMaster.getCodeDesc());
			dateFormatListDTO.add(dateFormat);
		}
		return dateFormatListDTO;
	}

	@Override
	public WorkdayFtpConfigForm getFTPConfigData(Long companyId) {
		return null;
	}

	@Override
	public WorkdayFtpConfigForm getFTPConfigFormData(Long companyId) {

		WorkdayFtpConfig workdayFtpConfig = workdayFtpConfigDao.findByCompanyId(companyId);
		if (workdayFtpConfig == null)
			return null;
		WorkdayFtpConfigForm workdayFtpConfigDTO = new WorkdayFtpConfigForm();
		workdayFtpConfigDTO.setFtpServer(workdayFtpConfig.getFtpServerAddress());
		workdayFtpConfigDTO.setFtpPort(workdayFtpConfig.getFtpPort());
		workdayFtpConfigDTO.setUsername(workdayFtpConfig.getUserName());
		workdayFtpConfigDTO.setFtpPassword(workdayFtpConfig.getPassword());
		workdayFtpConfigDTO.setProtocol(workdayFtpConfig.getProtocol().getCodeValue());
		workdayFtpConfigDTO.setEncryptionType(workdayFtpConfig.getEncryptionType());
		workdayFtpConfigDTO.setPgpPassword(workdayFtpConfig.getPgpPassword());

		AppCodeMaster dateFormatVO = appCodeMasterDAO.findByCondition("Date Format", workdayFtpConfig.getDateFormat());

		workdayFtpConfigDTO.setDateFormat(new DateFormatDTO(dateFormatVO.getCodeValue(), dateFormatVO.getCodeDesc()));

		WorkdayDataFtpConfigForm employeeDataFtpConfig = new WorkdayDataFtpConfigForm();
		employeeDataFtpConfig.setFrequency(workdayFtpConfig.getEmployeeDataFrequency());
		employeeDataFtpConfig.setIsActive(workdayFtpConfig.isEmployeeDataIsActive());
		employeeDataFtpConfig.setMoveToFolderPath(workdayFtpConfig.getEmployeeDataMoveToFolderPath());
		employeeDataFtpConfig.setRemotePath(workdayFtpConfig.getEmployeeDataRemotePath());

		WorkdayDataFtpConfigForm payrollTransactionDataFtpConfig = new WorkdayDataFtpConfigForm();
		payrollTransactionDataFtpConfig.setFrequency(workdayFtpConfig.getPayrollDataFrequency());
		payrollTransactionDataFtpConfig.setIsActive(workdayFtpConfig.isPayrollDataIsActive());
		payrollTransactionDataFtpConfig.setMoveToFolderPath(workdayFtpConfig.getPayrollDataMoveToFolderPath());
		payrollTransactionDataFtpConfig.setRemotePath(workdayFtpConfig.getPayrollDataRemotePath());

		workdayFtpConfigDTO.setEmployeeDataFtpConfig(employeeDataFtpConfig);
		workdayFtpConfigDTO.setPayrollTransactionDataFtpConfig(payrollTransactionDataFtpConfig);

		return workdayFtpConfigDTO;
	}

	@Override
	public boolean checkPpk(WorkdayFtpConfigForm ftpIntegrationForm, Long companyId) throws IOException {
		return false;
	}

	@Override
	public void saveftpConfigData(WorkdayFtpConfigForm ftpIntegrationForm, Long companyId)
			throws PayAsiaSystemException {

		WorkdayFtpConfig workdayFtpConfig = workdayFtpConfigDao.findByCompanyId(companyId);
		if (workdayFtpConfig == null) {
			workdayFtpConfig = new WorkdayFtpConfig();

			Company company = companyDAO.findById(companyId);
			workdayFtpConfig.setCompany(company);
		}

		if (StringUtils.isNotBlank(ftpIntegrationForm.getFtpServer()))
			workdayFtpConfig.setFtpServerAddress(ftpIntegrationForm.getFtpServer());

		if (ftpIntegrationForm.getFtpPort() != 0)
			workdayFtpConfig.setFtpPort(ftpIntegrationForm.getFtpPort());
		else
			workdayFtpConfig.setFtpPort(Integer.parseInt(PayAsiaConstants.SFTP_DEFAULT_PORT));

		if (StringUtils.isNotBlank(ftpIntegrationForm.getUsername()))
			workdayFtpConfig.setUserName(ftpIntegrationForm.getUsername());

		if (StringUtils.isNotBlank(ftpIntegrationForm.getFtpPassword()))
			workdayFtpConfig.setPassword(ftpIntegrationForm.getFtpPassword());

		AppCodeMaster appCodeFtpProtocol = appCodeMasterDAO.findByCondition("FTP Protocol TYPE", "SFTP");
		if (StringUtils.isNotBlank(ftpIntegrationForm.getProtocol()))
			workdayFtpConfig.setProtocol(appCodeFtpProtocol);

		if (StringUtils.isNotBlank(ftpIntegrationForm.getEncryptionType()))
			workdayFtpConfig.setEncryptionType(ftpIntegrationForm.getEncryptionType());
		else
			workdayFtpConfig.setEncryptionType(PayAsiaConstants.FTP_PROTOCOL_DFAULT_ENCRYPTION);

		workdayFtpConfig.setPgpPassword(ftpIntegrationForm.getPgpPassword());

		if (ftpIntegrationForm.getDateFormat() != null
				&& StringUtils.isNotBlank(ftpIntegrationForm.getDateFormat().getDateFormatValue()))
			workdayFtpConfig.setDateFormat(ftpIntegrationForm.getDateFormat().getDateFormatValue());

		if (ftpIntegrationForm.getEmployeeDataFtpConfig() != null) {
			WorkdayDataFtpConfigForm employeeDataFtpConfig = ftpIntegrationForm.getEmployeeDataFtpConfig();

			workdayFtpConfig.setEmployeeDataIsActive(employeeDataFtpConfig.getIsActive());

			if (StringUtils.isNotBlank(employeeDataFtpConfig.getFrequency()))
				workdayFtpConfig.setEmployeeDataFrequency(employeeDataFtpConfig.getFrequency());

			if (StringUtils.isNotBlank(employeeDataFtpConfig.getRemotePath()))
				workdayFtpConfig.setEmployeeDataRemotePath(employeeDataFtpConfig.getRemotePath());

			workdayFtpConfig.setEmployeeDataMoveToFolderPath(employeeDataFtpConfig.getMoveToFolderPath());
		}

		if (ftpIntegrationForm.getPayrollTransactionDataFtpConfig() != null) {
			WorkdayDataFtpConfigForm payrollTransactionDataFtpConfig = ftpIntegrationForm
					.getPayrollTransactionDataFtpConfig();

			workdayFtpConfig.setPayrollDataIsActive(payrollTransactionDataFtpConfig.getIsActive());

			if (StringUtils.isNotBlank(payrollTransactionDataFtpConfig.getFrequency()))
				workdayFtpConfig.setPayrollDataFrequency(payrollTransactionDataFtpConfig.getFrequency());

			if (StringUtils.isNotBlank(payrollTransactionDataFtpConfig.getRemotePath()))
				workdayFtpConfig.setPayrollDataRemotePath(payrollTransactionDataFtpConfig.getRemotePath());

			workdayFtpConfig.setPayrollDataMoveToFolderPath(payrollTransactionDataFtpConfig.getMoveToFolderPath());
		}

		try {
			workdayFtpConfigDao.update(workdayFtpConfig);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_ERROR);
		}
	}

	@Override
	public List<WorkdayFieldMappingDTO> getWorkdayFieldMappingList(Long companyId, String entityName) {

		List<WorkdayFieldMappingDTO> workdayEmpFieldMappingList = new ArrayList<>();
		List<WorkdayFtpFieldMapping> workdayFTPFieldMappings = workdayFTPFieldMappingDAO
				.findAllWorkdayFTPFieldMappingByCompanyId(companyId);
		Map<Long, WorkdayFtpFieldMapping> workdayfieldMap = new HashMap<>();
		for (WorkdayFtpFieldMapping workdayFTPFieldMapping : workdayFTPFieldMappings) {
			workdayfieldMap.put(workdayFTPFieldMapping.getWorkdayField().getWorkdayFieldId(), workdayFTPFieldMapping);
		}

		List<WorkdayFieldMaster> workdayEmpFields = workdayFieldMasterDAO.findByEntityName(entityName);

		for (WorkdayFieldMaster workdayEmpField : workdayEmpFields) {

			WorkdayFieldMappingDTO workdayFieldMappingDTO = new WorkdayFieldMappingDTO();
			WorkdayFtpFieldMapping workdayFTPFieldMapping = workdayfieldMap.get(workdayEmpField.getWorkdayFieldId());
			if (workdayFTPFieldMapping != null) {
				workdayFieldMappingDTO.setHroFieldId(workdayFTPFieldMapping.getHroField().getDataDictionaryId());
				workdayFieldMappingDTO.setHroFieldLabel(workdayFTPFieldMapping.getHroField().getLabel());
				workdayFieldMappingDTO.setFieldMappingId(workdayFTPFieldMapping.getWorkdayFtpFieldMappingId());
			}
			workdayFieldMappingDTO.setSection(workdayEmpField.getSectionName());
			workdayFieldMappingDTO.setWorkdayFieldId(workdayEmpField.getWorkdayFieldId());
			workdayFieldMappingDTO.setWorkdayFieldLabel(workdayEmpField.getFieldDesc());
			workdayFieldMappingDTO.setOrder(workdayEmpField.getSortOrder());

			workdayEmpFieldMappingList.add(workdayFieldMappingDTO);
		}
		Collections.sort(workdayEmpFieldMappingList, new Comparator<WorkdayFieldMappingDTO>() {
			@Override
			public int compare(WorkdayFieldMappingDTO fieldMapping1, WorkdayFieldMappingDTO fieldMapping2) {
				int result = fieldMapping1.getSection().compareToIgnoreCase(fieldMapping2.getSection());
				return result == 0 ? Integer.compare(fieldMapping1.getOrder(), fieldMapping2.getOrder()) : result;
			}
		});

		return workdayEmpFieldMappingList;
	}

	@Override
	public CalculatoryFieldFormResponse getDataDictionaryFields(Long companyId, boolean isTableField,
			String entityName) {

		List<CalculatoryFieldForm> calculatoryFields = new ArrayList<CalculatoryFieldForm>();
		EntityMaster entityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO.findByConditionEntity(entityMaster.getEntityId(),
				PayAsiaConstants.STATIC_TYPE);
		DataDictionary compDataDictionaryVO = dataDictionaryDAO.findByCondition(null,
				PayAsiaConstants.COMPANY_ENTITY_ID, null, "Company Code",
				PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
		dataDictionaryList.add(compDataDictionaryVO);

		if (dataDictionaryList != null) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
				if (dataDictionary.getFieldType().equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
					if (!dataDictionary.getDataDictName()
							.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
						calculatoryField.setDictionaryId(dataDictionary.getDataDictionaryId());
						calculatoryField.setFieldName(dataDictionary.getDataDictName());
						calculatoryFields.add(calculatoryField);
					}

				}

			}
		}

		List<Long> formList = dynamicFormDAO.getDistinctFormId(companyId, entityMaster.getEntityId());
		for (Long formId : formList) {
			int maxVersion = dynamicFormDAO.getMaxVersionByFormId(companyId, entityMaster.getEntityId(), formId);
			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(companyId, entityMaster.getEntityId(),
					maxVersion, formId);

			Tab tab = employeeDynamicFormLogic.getTabObject(dynamicForm.getMetaData());
			List<Field> listOfFields = tab.getField();

			for (Field field : listOfFields) {
				if (!field.getType().equals(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)
						&& !field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)
						&& !field.getType().equals(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
						&& !field.getType().equals(PayAsiaConstants.DOCUMENT_FIELD_TYPE)
						&& !field.getType().equals(PayAsiaConstants.FIELD_TYPE_LABEL)
						&& !field.getType().equals(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {
					if (field.getDictionaryId() != null) {
						DataDictionary dataDictionary = dataDictionaryDAO.findById(field.getDictionaryId());
						if (dataDictionary != null) {
							CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
							if (!dataDictionary.getDataDictName()
									.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
								calculatoryField.setDictionaryId(dataDictionary.getDataDictionaryId());
							}

							if (dataDictionary.getFieldType().equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
								if (!dataDictionary.getDataDictName()
										.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
									calculatoryField.setFieldName(dataDictionary.getDataDictName());
								}

							} else {
								calculatoryField.setFieldName(dataDictionary.getLabel());
							}

							calculatoryFields.add(calculatoryField);
						}
					}

				}
				if ((field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)
						|| field.getType().equals(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) && isTableField) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						if (!column.getType().equals(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)
								&& !column.getType().equals(PayAsiaConstants.DOCUMENT_FIELD_TYPE)
								&& !column.getType().equals(PayAsiaConstants.FIELD_TYPE_LABEL)
								&& !column.getType().equals(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {

							if (column.getDictionaryId() != null) {
								DataDictionary dataDictionary = dataDictionaryDAO.findById(column.getDictionaryId());
								if (dataDictionary != null) {
									CalculatoryFieldForm calculatoryField = new CalculatoryFieldForm();
									if (!dataDictionary.getDataDictName()
											.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
										calculatoryField.setDictionaryId(dataDictionary.getDataDictionaryId());
									}

									if (dataDictionary.getFieldType().equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
										if (!dataDictionary.getDataDictName()
												.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_STATIC_UPDATED_DATE)) {
											calculatoryField.setFieldName(dataDictionary.getDataDictName());
										}

									} else {
										calculatoryField.setFieldName(dataDictionary.getDataDictName());
									}
									calculatoryFields.add(calculatoryField);
								}
							}
						}
					}
				}
			}
		}

		CalculatoryFieldFormResponse calculatoryFieldFormResponse = new CalculatoryFieldFormResponse();
		calculatoryFieldFormResponse.setCalculatoryFieldList(calculatoryFields);
		return calculatoryFieldFormResponse;

	}

	@Override
	public Long saveUpdateFieldMapping(Long workdayFieldId, Long hroDictionaryId, Long companyId) {
		Long fieldMappingSaveId = null;
		Company company = companyDAO.findById(companyId);
		if (workdayFieldId != null && hroDictionaryId != null) {
			WorkdayFtpFieldMapping ftpFieldMapping = workdayFTPFieldMappingDAO
					.findByCompanyIdAndWorkdayFieldId(companyId, workdayFieldId);
			if (ftpFieldMapping == null) {
				ftpFieldMapping = new WorkdayFtpFieldMapping();
				ftpFieldMapping.setCompany(company);
				ftpFieldMapping.setWorkdayField(workdayFieldMasterDAO.findById(workdayFieldId));
			}

			ftpFieldMapping.setHroField(dataDictionaryDAO.findById(hroDictionaryId));
			workdayFTPFieldMappingDAO.update(ftpFieldMapping);
			fieldMappingSaveId = ftpFieldMapping.getWorkdayFtpFieldMappingId();

		}
		return fieldMappingSaveId;
	}

	@Override
	public void deleteHROField(Long fieldMappingId) {

		workdayFieldMappingDataTransformationDAO.deleteByCondition(fieldMappingId);
		workdayFTPFieldMappingDAO.deleteById(fieldMappingId);
	}

	@Override
	public List<WorkdayFieldMappingDataTransformationDTO> getTransformationData(Long fieldMappingId) {

		List<WorkdayFieldMappingDataTransformationDTO> dataTransformationList = new ArrayList<>();
		List<WorkdayFieldMappingDataTransformation> fieldMappingDataTransformationList = workdayFieldMappingDataTransformationDAO
				.getDataTransformationByFieldMappingId(fieldMappingId);

		if (fieldMappingDataTransformationList != null && !fieldMappingDataTransformationList.isEmpty()) {
			for (WorkdayFieldMappingDataTransformation fieldMappingDataTransformation : fieldMappingDataTransformationList) {
				WorkdayFieldMappingDataTransformationDTO dataTransformation = new WorkdayFieldMappingDataTransformationDTO();
				dataTransformation
						.setDataTransformationId(fieldMappingDataTransformation.getFieldMappingDataTransformationId());
				dataTransformation.setHroFieldValue(fieldMappingDataTransformation.getHroFieldValue());
				dataTransformation.setWorkdayFieldValue(fieldMappingDataTransformation.getWorkdayFieldValue());
				dataTransformation.setWorkdayFtpFieldMappingId(
						fieldMappingDataTransformation.getWorkdayFtpFieldMapping().getWorkdayFtpFieldMappingId());
				dataTransformationList.add(dataTransformation);
			}
		}

		return dataTransformationList;
	}

	@Override
	public void saveFieldDataTransformation(FieldDataTransformationForm dataTransformationFieldForm) {

		if (dataTransformationFieldForm != null && dataTransformationFieldForm.getfMappingId() != null) {

			// Step 1 : delete current data
			workdayFieldMappingDataTransformationDAO.deleteByCondition(dataTransformationFieldForm.getfMappingId());

			// Step 2 : save new data if present in form
			if (dataTransformationFieldForm.getDataTransformationList() != null
					&& !dataTransformationFieldForm.getDataTransformationList().isEmpty()) {

				WorkdayFtpFieldMapping workdayFtpFieldMapping = workdayFTPFieldMappingDAO
						.findById(dataTransformationFieldForm.getfMappingId());
				for (WorkdayFieldMappingDataTransformationDTO dataTransformation : dataTransformationFieldForm
						.getDataTransformationList()) {
					WorkdayFieldMappingDataTransformation fieldMappingDataTransformation = new WorkdayFieldMappingDataTransformation();
					fieldMappingDataTransformation.setWorkdayFtpFieldMapping(workdayFtpFieldMapping);
					fieldMappingDataTransformation.setHroFieldValue(dataTransformation.getHroFieldValue());
					fieldMappingDataTransformation.setWorkdayFieldValue(dataTransformation.getWorkdayFieldValue());

					workdayFieldMappingDataTransformationDAO.save(fieldMappingDataTransformation);
				}

			}
		}
	}

	@Override
	public String runFtpImport(Long companyId, boolean isEmployeeData, Long loggedInEmployeeId) {

		LOGGER.info(
				"---------------------------------------------------------------------------------------------------");
		// Step 1: Initial validations
		WorkdayFtpConfig ftpConfig = workdayFtpConfigDao.findActiveFtpConfigByCompanyId(companyId);
		if (ftpConfig == null) {
			return "No Active FTP Configuration found";
		}

		DataDictionary empNumberDataDictionary = dataDictionaryDAO.findByCondition(null,
				PayAsiaConstants.EMPLOYEE_ENTITY_ID, null, PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY,
				PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);

		// Get all workday-fields at once. This is to avoid repeated DB hits in
		// loop.
		List<WorkdayFieldMaster> workdayFields = workdayFieldMasterDAO
				.findByEntityName(isEmployeeData ? "Employee" : "Payroll");
		Map<String, WorkdayFieldMaster> workdayFieldMap = new HashMap<>();
		for (WorkdayFieldMaster workdayField : workdayFields) {
			workdayFieldMap.put(
					workdayField.getSectionName() + WORKDAY_SECTION_FIELD_SEPARATOR + workdayField.getFieldName(),
					workdayField);
		}

		// import employee-data or payroll-data
		if (isEmployeeData)
			return runEmployeeDataFtpImport(companyId, loggedInEmployeeId, ftpConfig, empNumberDataDictionary,
					workdayFieldMap);
		else
			return runPayrollDataFtpImport(companyId, loggedInEmployeeId, ftpConfig, empNumberDataDictionary,
					workdayFieldMap);

	}

	private String runEmployeeDataFtpImport(Long companyId, Long loggedInEmployeeId, WorkdayFtpConfig ftpConfig,
			DataDictionary empNumberDataDictionary, Map<String, WorkdayFieldMaster> workdayFieldMap) {

		boolean runFTPStatus = false;

		// initial validations for employee-data import
		List<WorkdayFtpFieldMapping> fieldMappings = workdayFTPFieldMappingDAO
				.findAllWorkdayFTPFieldMappingByCompanyId(companyId, Boolean.TRUE);
		if (fieldMappings == null || fieldMappings.isEmpty()) {
			return "No Field-Mappings found";
		}
		WorkdayFtpFieldMapping employeeNumberMapping = workdayFTPFieldMappingDAO.getEmployeeNumberMapping(companyId);
		if (employeeNumberMapping == null) {
			return "No Employee-Number Mapping found";
		}

		// Step 2: Get field-mappings from DB
		Map<String, WorkdayFieldMappingDTO> fieldsMappingMap = new HashMap<>();
		for (WorkdayFtpFieldMapping ftpFieldMapping : fieldMappings) {
			WorkdayFieldMappingDTO ftpMappingDTO = new WorkdayFieldMappingDTO();
			ftpMappingDTO.setFieldMappingId(ftpFieldMapping.getWorkdayFtpFieldMappingId());
			ftpMappingDTO.setHroFieldId(ftpFieldMapping.getHroField().getDataDictionaryId());
			ftpMappingDTO.setHroDataDictionary(ftpFieldMapping.getHroField());
			ftpMappingDTO.setHroFieldLabel(ftpFieldMapping.getHroField().getLabel());

			Map<String, String> dataTransformationMap = new HashMap<String, String>();
			List<WorkdayFieldMappingDataTransformation> ftdFieldDataTransformations = workdayFieldMappingDataTransformationDAO
					.getDataTransformationByFieldMappingId(ftpFieldMapping.getWorkdayFtpFieldMappingId());
			for (WorkdayFieldMappingDataTransformation dataTransformation : ftdFieldDataTransformations) {
				if (StringUtils.isNotBlank(dataTransformation.getWorkdayFieldValue())
						&& StringUtils.isNotBlank(dataTransformation.getHroFieldValue())) {
					dataTransformationMap.put(dataTransformation.getWorkdayFieldValue(),
							dataTransformation.getHroFieldValue());
				}
			}
			ftpMappingDTO.setDataTransformationMap(dataTransformationMap);

			fieldsMappingMap.put(ftpFieldMapping.getWorkdayField().getSectionName() + "."
					+ ftpFieldMapping.getWorkdayField().getFieldName(), ftpMappingDTO);
		}

		// SAHP effective date
		if (fieldsMappingMap.get("Salary_and_Hourly_Plans.Effective_Date") == null) {
			DataDictionary effectiveDateDictionary = dataDictionaryDAO.findByDictionaryName(companyId, 1L,
					"Salary_and_Hourly_Plans.Basic Salary Progression.Effective_Date", null);
			// DataDictionary effectiveDateDictionary =
			// dataDictionaryDAO.findByDictionaryName(companyId, 1L,
			// "Salary_Details.Salary Progression.Effective_Date", null);
			if (effectiveDateDictionary != null) {
				WorkdayFieldMappingDTO ftpMappingDTO = new WorkdayFieldMappingDTO();
				ftpMappingDTO.setHroFieldId(effectiveDateDictionary.getDataDictionaryId());
				ftpMappingDTO.setHroDataDictionary(effectiveDateDictionary);
				ftpMappingDTO.setHroFieldLabel(effectiveDateDictionary.getLabel());
				ftpMappingDTO.setDataTransformationMap(new HashMap<String, String>());
				fieldsMappingMap.put("Salary_and_Hourly_Plans.Effective_Date", ftpMappingDTO);
			}
		}

		// Step 3: SFTP File Import
		// PayrollExtractEmployees employeeData = null;

		/*
		 * try { ftpXmlFileDataList = FTPUtils.getEmployeeData(); } catch
		 * (IOException | XMLStreamException e2) { e2.printStackTrace(); }
		 */
		FtpData ftpData = getDataFromSftp(ftpConfig, true);
		List<FtpFileDataDTO> ftpXmlFileDataList = ftpData.getFileDataList();
		if (ftpXmlFileDataList == null || ftpXmlFileDataList.isEmpty())
			return "No file found at SFTP Server.";

		// Step 4: Decrypt data TODO

		boolean isFilePresentForCompany = false;
		List<FtpFileDataDTO> ftpXmlFileSuccessList = new ArrayList<>();

		// Step 5: Process each XML file read from SFTP Server
		for (int i = 0; i < ftpXmlFileDataList.size(); i++) {
			FtpFileDataDTO ftpXmlFileData = ftpXmlFileDataList.get(i);
			LOGGER.info(
					"---------------------------------------------------------------------------------------------------");
			LOGGER.info("WD EMF Import : Processing EMF File " + ftpXmlFileData.getFileName());
			byte[] fileByteArrayFile = ftpXmlFileData.getData();
			ftpXmlFileSuccessList.add(ftpXmlFileData);

			// Step 5a: Convert byte-array(read from XML) into Map<String,
			// Map<String, List<Map<String, String>>>> employeesListMap
			Company company = ftpConfig.getCompany();
			EmployeeData employeeData = null;
			Map<String, Map<String, List<Map<String, String>>>> employeesDataMap = null;
			try {
				employeeData = getEmployeeDataMapFromByteArray(fileByteArrayFile,
						employeeNumberMapping.getWorkdayField().getFieldName(), company.getCompanyCode());
			} catch (XMLStreamException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace();
				runFTPStatus = false;
				continue;
			}

			// Step 5b: Validate if the data is of companyId. If not, exit.
			if (MSG_FILE_NOT_FOR_COMPANY.equals(employeeData.getMessage())) {
				LOGGER.info("WD EMF Import : " + MSG_FILE_NOT_FOR_COMPANY + " - " + company.getCompanyCode()
						+ ". Skipping...");
				continue;
			}
			// boolean isFileValidForCompany = false;
			String paygroupId = null;
			String payPeriodStartDate = null;
			String payPeriodEndDate = null;
			employeesDataMap = employeeData.getEmployeesDataMap();
			Set<String> employeeNumKeys = employeesDataMap.keySet();
			if (employeeNumKeys != null && !employeeNumKeys.isEmpty()) {
				Map<String, List<Map<String, String>>> tempMap = employeesDataMap
						.get(employeeNumKeys.iterator().next());
				for (Map<String, String> valueMap : tempMap.get(WD_SECTION_HEADER)) {
					/*
					 * if(valueMap.containsKey("Payroll_Company_ID") &&
					 * valueMap.get("Payroll_Company_ID").equalsIgnoreCase(
					 * ftpConfig.getCompany().getCompanyCode())) {
					 * isFileValidForCompany = true;
					 */
					isFilePresentForCompany = true;
					paygroupId = valueMap.get("Pay_Group_ID");
					payPeriodStartDate = valueMap.get("Pay_Period_Start").substring(0, 10);
					payPeriodEndDate = valueMap.get("Pay_Period_End").substring(0, 10);
					break;
				}
			}
			/*
			 * if(!isFileValidForCompany) continue;
			 */

			// Step 5b2: If file contains only header info, log and skip
			// WorkdayPaygroup workdayPaygroup =
			// workdayPaygroupDAO.findByPaygroupId(paygroupId,
			// company.getCompanyId());
			if (paygroupId == null) {// in case of blank xml files
				// TODO
				Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
				saveImportHistory(company, employeeVO, ftpXmlFileData, 0, 0, 0, Boolean.TRUE, "Employee",
						"No employee record in file");
				isFilePresentForCompany = true;
				runFTPStatus = true;
				new FTPUtils().moveXmlAfterReading(ftpConfig, ftpXmlFileData, Boolean.TRUE, ftpData.getSftpChannel(),
						ftpData.getSession(), i < ftpXmlFileDataList.size() ? false : true);
				continue;
			}

			// Step 5b3: Validate if paygroup in the file is defined for the
			// company. If not, exit.
			WorkdayAppCodeMaster workdayAppCode = workdayAppCodeMasterDAO.findByConditionAndCountry(CATEGORY_PAYGROUP,
					paygroupId, company.getCountryMaster().getCountryId());
			if (workdayAppCode == null)
				return "Paygroup " + paygroupId + " not defined for company " + company.getCompanyCode();

			// Step 5c: Determine count of data for new and existing employees
			List<String> employeeNumberList = new ArrayList<>();
			List<Tuple> employeeNameListVO = employeeDAO.getEmployeeNameTupleList(companyId);
			for (Tuple empTuple : employeeNameListVO) {
				String employeeNumber = (String) empTuple.get(getAlias(Employee_.employeeNumber), String.class);
				employeeNumberList.add(employeeNumber.toUpperCase());
			}

			int existingEmployeesCount = 0;
			int newHireEmployeesCount = 0;
			int totalEmployeesCount = employeeNumKeys.size();
			Map<String, Boolean> oldAndNewEmployeeMap = new HashMap<>();
			for (String ftpEmployeeNumber : employeeNumKeys) {
				ftpEmployeeNumber = ftpEmployeeNumber.toUpperCase();
				if (employeeNumberList.contains(ftpEmployeeNumber)) {
					oldAndNewEmployeeMap.put(ftpEmployeeNumber, Boolean.FALSE);
					existingEmployeesCount++;
				} else {
					oldAndNewEmployeeMap.put(ftpEmployeeNumber, Boolean.TRUE);
					newHireEmployeesCount++;
				}
			}

			DataImportForm dataImportForm = null;
			try {
				// Step 5d: Insert/Update data into DB
				LOGGER.info("WD EMF Import : Updating Employee Information in DB Started");
				dataImportForm = preProcessAndCreateUpdateEmpData(companyId, employeesDataMap, employeeNumKeys,
						fieldsMappingMap, PayAsiaConstants.UPDATE_AND_INSERT, PayAsiaConstants.ROLLBACK_ON_ERROR,
						ftpConfig.getDateFormat(), empNumberDataDictionary);
				LOGGER.info("WD EMF Import : Updating Employee Information in DB Completed");

				// Step 5e: Log the successful and failed data import
				LOGGER.info("WD EMF Import : Saving EMF Import Log");
				if (dataImportForm == null || dataImportForm.getDataImportLogList() == null) {
					if (dataImportForm != null && !dataImportForm.getImportedData().isEmpty()) {

						// Step 5f: Insert ALL data from XML to
						// Paygroup-Batch-Data table
						LOGGER.info("WD EMF Import : Dumping EMF Import Data in DB Started");
						Map<String, String> xmlEmployeeMap = new HashMap<>();
						PayrollExtractEmployees xmlData = WorkdayEmployeeDataXMLUtil.getXMLData(fileByteArrayFile);
						if (xmlData != null) {
							List<EmployeeType> employeeXmls = xmlData.getPayGroup().get(0).getEmployee();
							if (employeeXmls != null) {
								for (EmployeeType employeeXml : employeeXmls) {
									xmlEmployeeMap.put(
											employeeXml.getSummary().getEmployeeID().getValue().toUpperCase(),
											WorkdayEmployeeDataXMLUtil.convertEmployeeToXmlString(employeeXml));
								}
							}
						}
						WorkdayAppCodeMaster batchTypeAppCode = workdayAppCodeMasterDAO
								.findBatchTypeByPaygroupType(workdayAppCode.getWorkdayAppCodeId());
						PaygroupBatchMessage paygroupBatchMessage = saveAllXmlData(employeesDataMap, xmlEmployeeMap,
								workdayFieldMap, workdayAppCode, batchTypeAppCode, payPeriodStartDate, payPeriodEndDate,
								ftpConfig, oldAndNewEmployeeMap, Boolean.TRUE);
						LOGGER.info("WD EMF Import : Dumping EMF Import Data in DB Completed");

						// if paygoup is not defined in DB, return message;
						if (paygroupBatchMessage.getErrorMessage() != null)
							return paygroupBatchMessage.getErrorMessage();

						// Step 5g: Log successful import
						JSONObject importLogs = getEmpFieldImportedLogs(employeeNumKeys, dataImportForm);
						WorkdayFtpImportHistory ftpImportHistoryVO = new WorkdayFtpImportHistory();
						ftpImportHistoryVO.setCompany(company);
						ftpImportHistoryVO.setCreatedDate(new Timestamp(new Date().getTime()));
						Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
						if (employeeVO != null) {
							ftpImportHistoryVO.setCreatedBy(employeeVO);
						}
						ftpImportHistoryVO.setExistingEmpRecordsUpdated(existingEmployeesCount);
						ftpImportHistoryVO.setNewEmpRecordsUpdated(newHireEmployeesCount);
						ftpImportHistoryVO.setTotalEmpRecords(totalEmployeesCount);
						ftpImportHistoryVO.setFailedRemarks("");
						ftpImportHistoryVO.setImportFileName(ftpXmlFileData.getFileName());
						ftpImportHistoryVO.setImportStatus(true);
						ftpImportHistoryVO.setImportType("Employee");
						WorkdayFtpImportHistory ftpImportHistoryReturn = workdayFtpImportHistoryDAO
								.saveHistory(ftpImportHistoryVO);
						LOGGER.info("WD EMF Import : Import History Saved");
						runFTPStatus = true;

						saveLogJsonToFile(importLogs, null, ftpImportHistoryReturn.getFtpImportHistoryId(), companyId,
								true);

						// Step 5h: In case of no error only, move the xml file
						// after all work is done
						new FTPUtils().moveXmlAfterReading(ftpConfig, ftpXmlFileData, Boolean.TRUE,
								ftpData.getSftpChannel(), ftpData.getSession(),
								i < ftpXmlFileDataList.size() ? false : true);
					} else {
						return "No Record Found";
					}
				} else {
					LOGGER.info("dataImportForm is not null.");
					Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
					saveImportHistoryAndLog(companyId, employeeVO, ftpXmlFileData, employeeNumKeys,
							existingEmployeesCount, newHireEmployeesCount, totalEmployeesCount, dataImportForm, null,
							"Invalid File Data", false);
					runFTPStatus = false;
				}
			} catch (PayAsiaDataException pde) {
				LOGGER.error(pde.getMessage(), pde);
				Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
				saveImportHistoryAndLog(companyId, employeeVO, ftpXmlFileData, employeeNumKeys, existingEmployeesCount,
						newHireEmployeesCount, totalEmployeesCount, dataImportForm, null, "Invalid File Data", false);
				runFTPStatus = false;
			} catch (PayAsiaSystemException ex) {
				LOGGER.error(ex.getMessage(), ex);
				Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
				saveImportHistoryAndLog(companyId, employeeVO, ftpXmlFileData, employeeNumKeys, existingEmployeesCount,
						newHireEmployeesCount, totalEmployeesCount, dataImportForm, null, "Invalid File Data", false);
				runFTPStatus = false;
			} catch (JSONException e) {
				LOGGER.error(e.getMessage(), e);
				Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
				saveImportHistoryAndLog(companyId, employeeVO, ftpXmlFileData, employeeNumKeys, existingEmployeesCount,
						newHireEmployeesCount, totalEmployeesCount, dataImportForm, null, "Invalid File Data", false);
				runFTPStatus = false;
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
				saveImportHistoryAndLog(companyId, employeeVO, ftpXmlFileData, employeeNumKeys, existingEmployeesCount,
						newHireEmployeesCount, totalEmployeesCount, dataImportForm, null, "Invalid File Data", false);
				runFTPStatus = false;
			}
		}

		if (!isFilePresentForCompany)
			return "No file found for this company";

		LOGGER.info("WD EMF Import : Import Processing Completed.");
		return runFTPStatus ? PayAsiaConstants.PAYASIA_SUCCESS : "Error during FTP import";
	}

	private String runPayrollDataFtpImport(Long companyId, Long loggedInEmployeeId, WorkdayFtpConfig ftpConfig,
			DataDictionary empNumberDataDictionary, Map<String, WorkdayFieldMaster> workdayFieldMap) {

		boolean runFTPStatus = false;

		// Step 2: SFTP File Import

		/*
		 * try { ftpXmlFileDataList = FTPUtils.getPayrollData(); } catch
		 * (IOException | XMLStreamException e2) { e2.printStackTrace(); }
		 */
		FtpData ftpData = getDataFromSftp(ftpConfig, Boolean.FALSE);
		List<FtpFileDataDTO> ftpXmlFileDataList = ftpData.getFileDataList();
		if (ftpXmlFileDataList == null || ftpXmlFileDataList.isEmpty())
			return "No file found at SFTP Server.";

		boolean isFilePresentForCompany = false;
		List<FtpFileDataDTO> ftpXmlFileSuccessList = new ArrayList<>();

		// Step 5: Process each XML file read from SFTP Server
		for (int i = 0; i < ftpXmlFileDataList.size(); i++) {
			FtpFileDataDTO ftpXmlFileData = ftpXmlFileDataList.get(i);
			LOGGER.info(
					"---------------------------------------------------------------------------------------------------");
			LOGGER.info("WD PTRX Import : Processing PTRX File " + ftpXmlFileData.getFileName());
			byte[] fileByteArrayFile = ftpXmlFileData.getData();
			ftpXmlFileSuccessList.add(ftpXmlFileData);

			// Step 5a: Convert byte-array(read from XML) into Map<String,
			// Map<String,
			// List<Map<String, String>>>> payrollDataMap
			Company company = ftpConfig.getCompany();
			EmployeeData payrollData = null;
			Map<String, Map<String, List<Map<String, String>>>> payrollDataMap = null;
			try {
				payrollData = getPayrollDataMapFromByteArray(fileByteArrayFile, "Employee_ID",
						company.getCompanyCode());
			} catch (XMLStreamException e) {
				LOGGER.error(e.getMessage(), e);
				e.printStackTrace();
				runFTPStatus = false;
				continue;
			}

			// Step 5b: Validate if the data is of companyId. If not, check next
			// file.
			if (MSG_FILE_NOT_FOR_COMPANY.equals(payrollData.getMessage())) {
				LOGGER.info("WD PTRX Import : " + MSG_FILE_NOT_FOR_COMPANY + " - " + company.getCompanyCode()
						+ ". Skipping...");
				continue;
			}

			String paygroupId = null;
			String payPeriodStartDate = null;
			String payPeriodEndDate = null;
			payrollDataMap = payrollData.getEmployeesDataMap();
			Set<String> employeeNumKeys = payrollDataMap.keySet();
			if (employeeNumKeys != null && !employeeNumKeys.isEmpty()) {
				Map<String, List<Map<String, String>>> tempMap = payrollDataMap.get(employeeNumKeys.iterator().next());
				for (Map<String, String> valueMap : tempMap.get("Header")) {
					isFilePresentForCompany = true;
					paygroupId = valueMap.get("Pay_Group_ID");
					payPeriodStartDate = valueMap.get("Pay_Period_Start").substring(0, 10);
					payPeriodEndDate = valueMap.get("Pay_Period_End").substring(0, 10);
					break;
				}
			}

			// Step 5b2: If file contains only header info, log and skip
			// WorkdayPaygroup workdayPaygroup =
			// workdayPaygroupDAO.findByPaygroupId(paygroupId,
			// company.getCompanyId());
			if (paygroupId == null) {// in case of blank xml files
				// TODO log
				Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
				saveImportHistory(company, employeeVO, ftpXmlFileData, 0, 0, 0, Boolean.TRUE, "Payroll",
						"No employee record in file");
				LOGGER.info("WD PTRX Import : Import History Saved");
				isFilePresentForCompany = true;
				runFTPStatus = true;
				new FTPUtils().moveXmlAfterReading(ftpConfig, ftpXmlFileData, Boolean.FALSE, ftpData.getSftpChannel(),
						ftpData.getSession(), i < ftpXmlFileDataList.size() ? false : true);
				continue;
			}

			// Step 5b3: Validate if paygroup in the file is defined for the
			// company. If not, exit.
			WorkdayAppCodeMaster workdayAppCode = workdayAppCodeMasterDAO.findByConditionAndCountry(CATEGORY_PAYGROUP,
					paygroupId, company.getCountryMaster().getCountryId());
			if (workdayAppCode == null)
				return "Paygroup " + paygroupId + " not defined for company " + company.getCompanyCode();

			// Step 5c: Insert ALL data from XML to Paygroup-Batch-Data table
			LOGGER.info("WD PTRX Import : Dumping EMF Import Data in DB Started");
			Map<String, String> xmlEmployeeMap = new HashMap<>();
			com.mind.payasia.xml.bean.workday.paydata.PayrollExtractEmployees xmlData = WorkdayPayrollDataXMLUtil
					.getXMLData(fileByteArrayFile);
			if (xmlData != null) {
				List<com.mind.payasia.xml.bean.workday.paydata.EmployeeType> employeeXmls = xmlData.getPayGroup().get(0)
						.getEmployee();
				if (employeeXmls != null) {
					for (com.mind.payasia.xml.bean.workday.paydata.EmployeeType employeeXml : employeeXmls) {
						xmlEmployeeMap.put(employeeXml.getSummary().getEmployeeID().getValue().toUpperCase(),
								WorkdayPayrollDataXMLUtil.convertEmployeeToXmlString(employeeXml));
					}
				}
			}

			// Step 5d: Determine count of data for new and existing employees
			List<String> employeeNumberList = new ArrayList<>();
			List<Tuple> employeeNameListVO = employeeDAO.getEmployeeNameTupleList(companyId);
			for (Tuple empTuple : employeeNameListVO) {
				String employeeNumber = (String) empTuple.get(getAlias(Employee_.employeeNumber), String.class);
				employeeNumberList.add(employeeNumber.toUpperCase());
			}

			int existingEmployeesCount = 0;
			int newHireEmployeesCount = 0;
			int totalEmployeesCount = employeeNumKeys.size();
			Map<String, Boolean> oldAndNewEmployeeMap = new HashMap<>();
			for (String ftpEmployeeNumber : employeeNumKeys) {
				ftpEmployeeNumber = ftpEmployeeNumber.toUpperCase();
				if (employeeNumberList.contains(ftpEmployeeNumber)) {
					oldAndNewEmployeeMap.put(ftpEmployeeNumber, Boolean.FALSE);
					existingEmployeesCount++;
				} else {
					oldAndNewEmployeeMap.put(ftpEmployeeNumber, Boolean.TRUE);
					newHireEmployeesCount++;
				}
			}

			WorkdayAppCodeMaster batchTypeAppCode = workdayAppCodeMasterDAO
					.findBatchTypeByPaygroupType(workdayAppCode.getWorkdayAppCodeId());
			PaygroupBatchMessage paygroupBatchMessage = saveAllXmlData(payrollDataMap, xmlEmployeeMap, workdayFieldMap,
					workdayAppCode, batchTypeAppCode, payPeriodStartDate, payPeriodEndDate, ftpConfig,
					oldAndNewEmployeeMap, Boolean.FALSE);
			// if paygoup is not defined in DB, return message;
			if (paygroupBatchMessage.getErrorMessage() != null)
				return paygroupBatchMessage.getErrorMessage();

			// Step 5e: Save Pay-data, Time-Off Data, Time-tracking data
			savePayrollData(ftpConfig, payrollDataMap, employeeNumKeys, paygroupBatchMessage.getPaygroupBatch());

			// Step 5f: Save Workday Import History for PTRX
			Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
			saveImportHistoryAndLog(companyId, employeeVO, ftpXmlFileData, employeeNumKeys, existingEmployeesCount,
					newHireEmployeesCount, totalEmployeesCount, null, fileByteArrayFile, "", true);

			// Step 5g: Move file after processing
			new FTPUtils().moveXmlAfterReading(ftpConfig, ftpXmlFileData, Boolean.FALSE, ftpData.getSftpChannel(),
					ftpData.getSession(), i < ftpXmlFileDataList.size() ? false : true);
			runFTPStatus = true;
		}

		if (!isFilePresentForCompany)
			return "No file found for this company";
		LOGGER.info("WD PTRX Import : Import Processing Completed.");
		return runFTPStatus ? PayAsiaConstants.PAYASIA_SUCCESS : "Error during FTP import";

	}

	private void savePayrollData(WorkdayFtpConfig ftpConfig,
			Map<String, Map<String, List<Map<String, String>>>> payrollDataMap, Set<String> employeeNumKeys,
			WorkdayPaygroupBatch workdayPaygroupBatch) {

		Company company = ftpConfig.getCompany();
		for (String empNumber : employeeNumKeys) {
			Employee employee = employeeDAO.findByNumber(empNumber, company.getCompanyId());
			Map<String, List<Map<String, String>>> payrollData = payrollDataMap.get(empNumber);

			List<Map<String, String>> headerList = payrollData.get("Header");
			if (headerList != null && !headerList.isEmpty()) {

			}

			List<Map<String, String>> timeOffList = payrollData.get("Time_Off");
			if (timeOffList != null && !timeOffList.isEmpty()) {
				for (Map<String, String> fieldValueMap : timeOffList) {
					String code = fieldValueMap.get("Code");
					String operation = fieldValueMap.get("Operation");
					WorkdayPaygroupEmployeeTimeOff timeOff = null;
					if (operation != null) {

						if ("MODIFY".equals(operation)) {
							timeOff = workdayPaygroupEmployeeTimeOffDAO.findEmployeeTimeOff(employee.getEmployeeId(),
									company.getCompanyId(), code);
							if (timeOff != null) {
								timeOff.setCode(fieldValueMap.get("Code"));
								timeOff.setCompany(company);
								timeOff.setEmployee(employee);
								timeOff.setTimeOffType(fieldValueMap.get("Time_Off_Type"));
								timeOff.setUnitOfTime(fieldValueMap.get("Unit_of_Time"));
								String timeOffStr = fieldValueMap.get("Quantity");
								if (timeOffStr != null) {
									BigDecimal quantity = new BigDecimal(timeOffStr);
									quantity.setScale(2);
									timeOff.setQuantity(quantity);
								}
								workdayPaygroupEmployeeTimeOffDAO.update(timeOff);
							}

						} else if ("REMOVE".equals(operation)) {
							workdayPaygroupEmployeeTimeOffDAO.deleteEmployeeTimeOff(employee.getEmployeeId(),
									company.getCompanyId(), code);
						} else if ("ADD".equals(operation) || "NONE".equals(operation)) {
							timeOff = new WorkdayPaygroupEmployeeTimeOff();
							timeOff.setCode(fieldValueMap.get("Code"));
							timeOff.setCompany(company);
							timeOff.setEmployee(employee);
							timeOff.setTimeOffType(fieldValueMap.get("Time_Off_Type"));
							timeOff.setUnitOfTime(fieldValueMap.get("Unit_of_Time"));
							String timeOffStr = fieldValueMap.get("Quantity");
							if (timeOffStr != null) {
								BigDecimal quantity = new BigDecimal(timeOffStr);
								quantity.setScale(2);
								timeOff.setQuantity(quantity);
							}
							workdayPaygroupEmployeeTimeOffDAO.save(timeOff);
						}
					} else {
						timeOff = new WorkdayPaygroupEmployeeTimeOff();
						timeOff.setCode(fieldValueMap.get("Code"));
						timeOff.setCompany(company);
						timeOff.setEmployee(employee);
						timeOff.setTimeOffType(fieldValueMap.get("Time_Off_Type"));
						timeOff.setUnitOfTime(fieldValueMap.get("Unit_of_Time"));
						String timeOffStr = fieldValueMap.get("Quantity");
						if (timeOffStr != null) {
							BigDecimal quantity = new BigDecimal(timeOffStr);
							quantity.setScale(2);
							timeOff.setQuantity(quantity);
						}
						workdayPaygroupEmployeeTimeOffDAO.update(timeOff);
					}

				}
			}
			List<Map<String, String>> timeTrackingList = payrollData.get("Time_Tracking");
			if (timeTrackingList != null && !timeTrackingList.isEmpty()) {
				for (Map<String, String> fieldValueMap : timeTrackingList) {
					String code = fieldValueMap.get("Code");
					String operation = fieldValueMap.get("Operation");
					WorkdayPaygroupEmployeeTimeTracking timeTracking = null;
					if (operation != null) {

						if ("MODIFY".equals(operation)) {
							timeTracking = workdayPaygroupEmployeeTimeTrackingDAO
									.findEmployeeTimeTracking(employee.getEmployeeId(), company.getCompanyId(), code);
							if (timeTracking != null) {
								timeTracking.setCode(fieldValueMap.get("Code"));
								timeTracking.setCompany(company);
								timeTracking.setEmployee(employee);
								timeTracking.setUnitOfTime(fieldValueMap.get("Unit_of_Time"));
								String timeOffStr = fieldValueMap.get("Quantity");
								if (timeOffStr != null) {
									BigDecimal quantity = new BigDecimal(timeOffStr);
									quantity.setScale(2);
									timeTracking.setQuantity(quantity);
								}
								workdayPaygroupEmployeeTimeTrackingDAO.update(timeTracking);
							}
						} else if ("REMOVE".equals(operation)) {
							workdayPaygroupEmployeeTimeTrackingDAO.deleteEmployeeTimeTracking(employee.getEmployeeId(),
									company.getCompanyId(), code);
						} else if ("ADD".equals(operation) || "NONE".equals(operation)) {
							timeTracking = new WorkdayPaygroupEmployeeTimeTracking();
							timeTracking.setCode(fieldValueMap.get("Code"));
							timeTracking.setCompany(company);
							timeTracking.setEmployee(employee);
							timeTracking.setUnitOfTime(fieldValueMap.get("Unit_of_Time"));
							String timeOffStr = fieldValueMap.get("Quantity");
							if (timeOffStr != null) {
								BigDecimal quantity = new BigDecimal(timeOffStr);
								quantity.setScale(2);
								timeTracking.setQuantity(quantity);
							}
							workdayPaygroupEmployeeTimeTrackingDAO.save(timeTracking);
						}
					} else {
						timeTracking = new WorkdayPaygroupEmployeeTimeTracking();
						timeTracking.setCode(fieldValueMap.get("Code"));
						timeTracking.setCompany(company);
						timeTracking.setEmployee(employee);
						timeTracking.setUnitOfTime(fieldValueMap.get("Unit_of_Time"));
						String timeOffStr = fieldValueMap.get("Quantity");
						if (timeOffStr != null) {
							BigDecimal quantity = new BigDecimal(timeOffStr);
							quantity.setScale(2);
							timeTracking.setQuantity(quantity);
						}
						workdayPaygroupEmployeeTimeTrackingDAO.save(timeTracking);
					}
				}
			}
			List<Map<String, String>> timeOffCorrectionList = payrollData.get("Time_Off_Correction");
			if (timeOffCorrectionList != null && !timeOffCorrectionList.isEmpty()) {
				for (Map<String, String> fieldValueMap : timeOffCorrectionList) {
					String code = fieldValueMap.get("Code");
					WorkdayPaygroupEmployeeTimeOff timeOff = workdayPaygroupEmployeeTimeOffDAO
							.findEmployeeTimeOff(employee.getEmployeeId(), company.getCompanyId(), code);
					String timeOffStr = fieldValueMap.get("Quantity");
					if (timeOffStr != null) {
						BigDecimal quantity = new BigDecimal(timeOffStr);
						quantity.setScale(2);
						if (timeOff != null) {
							timeOff.setQuantity(quantity.add(timeOff.getQuantity()));
							workdayPaygroupEmployeeTimeOffDAO.update(timeOff);
						} else {
							timeOff = new WorkdayPaygroupEmployeeTimeOff();
							timeOff.setCode(code);
							timeOff.setCompany(company);
							timeOff.setEmployee(employee);
							timeOff.setQuantity(quantity);
							timeOff.setTimeOffType(fieldValueMap.get("Time_Off_Type"));
							timeOff.setUnitOfTime(fieldValueMap.get("Unit_of_Time"));
							workdayPaygroupEmployeeTimeOffDAO.save(timeOff);
						}
					}
				}
			}

			List<Map<String, String>> timeTrackingCorrectionList = payrollData.get("Time_Tracking_Correction");
			if (timeTrackingCorrectionList != null && !timeTrackingCorrectionList.isEmpty()) {
				for (Map<String, String> fieldValueMap : timeTrackingCorrectionList) {
					String code = fieldValueMap.get("Code");
					WorkdayPaygroupEmployeeTimeTracking timeTracking = workdayPaygroupEmployeeTimeTrackingDAO
							.findEmployeeTimeTracking(employee.getEmployeeId(), company.getCompanyId(), code);
					String timeTrackingStr = fieldValueMap.get("Quantity");
					if (timeTrackingStr != null) {
						BigDecimal quantity = new BigDecimal(timeTrackingStr);
						quantity.setScale(2);
						if (timeTracking != null) {
							timeTracking.setQuantity(quantity.add(timeTracking.getQuantity()));
							workdayPaygroupEmployeeTimeTrackingDAO.update(timeTracking);
						} else {
							timeTracking = new WorkdayPaygroupEmployeeTimeTracking();
							timeTracking.setCode(code);
							timeTracking.setCompany(company);
							timeTracking.setEmployee(employee);
							timeTracking.setUnitOfTime(fieldValueMap.get("Unit_of_Time"));
							timeTracking.setQuantity(quantity);
							workdayPaygroupEmployeeTimeTrackingDAO.save(timeTracking);
						}
					}
				}
			}
		}
	}

	private FtpData getDataFromSftp(WorkdayFtpConfig ftpConfig, boolean isEmployeeData) {

		FtpData ftpData = null;
		FTPUtils ftpUtils = new FTPUtils();
		if (ftpConfig.getProtocol().getCodeDesc().equalsIgnoreCase("sftp")) {
			ftpData = ftpUtils.getFileDataFromSFTPServer(ftpConfig, isEmployeeData);
		} else {
			throw new PayAsiaBusinessException("SFTP_FILE_READ", "Not Configured for SFTP");
		}
		return ftpData;
	}

	private EmployeeData getEmployeeDataMapFromByteArray(byte[] fileByte, String employeeNumberField,
			String companyCode) throws XMLStreamException {

		Map<String, Map<String, List<Map<String, String>>>> employeeDataMap = new HashMap<String, Map<String, List<Map<String, String>>>>();
		Map<String, List<Map<String, String>>> sectionMap = null;
		Map<String, List<Map<String, String>>> headerSectionMap = null;
		List<Map<String, String>> positionSectionList = null;
		List<Map<String, String>> apSectionList = null;
		List<Map<String, String>> shpSectionList = null;
		List<Map<String, String>> peSectionList = null;
		List<Map<String, String>> idSectionList = null;
		List<Map<String, String>> edSectionList = null;
		List<Map<String, String>> sectionList = null;
		List<Map<String, String>> headerSectionList = null;
		Map<String, String> fieldDetails = null;
		Map<String, String> headerFieldDetails = null;
		String employeeIdNumber = null;
		String payrollCompanyId = null;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try {
			fileByte = new String(fileByte, "UTF-8").replaceAll("><", ">\n\t<").getBytes("UTF-8");
		} catch (UnsupportedEncodingException e2) {
			LOGGER.error(e2.getMessage(), e2);
		}
		InputStream is = new ByteArrayInputStream(fileByte);
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(is);
		boolean isHeader = false;
		boolean isRemove = false;
		boolean isIdentifier = false;
		boolean isAddressLine = false;
		String addressLabel = "";
		try {
			while (streamReader.hasNext()) {
				int e = streamReader.next();

				if (e == XMLStreamReader.START_ELEMENT) {
					String name = streamReader.getLocalName();
					if (name.equalsIgnoreCase(WD_SECTION_PAYGROUP)) {

					} else if (name.equalsIgnoreCase(WD_SECTION_HEADER)) {
						headerSectionMap = new HashMap<String, List<Map<String, String>>>();
						headerSectionList = new ArrayList<Map<String, String>>();
						headerFieldDetails = new HashMap<String, String>();
						isHeader = true;
					} else if (name.equalsIgnoreCase(WD_SECTION_EMPLOYEE)) {
						sectionMap = new HashMap<String, List<Map<String, String>>>();
						employeeIdNumber = null;
						sectionList = null;
						positionSectionList = null;
						apSectionList = null;
						shpSectionList = null;
						peSectionList = null;
						idSectionList = null;
						edSectionList = null;
						fieldDetails = null;
					} else if (name.equalsIgnoreCase(WD_SECTION_SUMMARY)
							|| name.equalsIgnoreCase(WD_SECTION_EMF_PERSONAL)
							|| name.equalsIgnoreCase(WD_SECTION_EMF_STATUS)
							|| name.equalsIgnoreCase(WD_SECTION_EMF_ADDITIONAL_INFORMATION)) {
						sectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_EMF_POSITION)) {
						if (positionSectionList == null)
							positionSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_EMF_ALLOWANCE_PLANS)) {
						if (apSectionList == null)
							apSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_EMF_SALARY_AND_HOURLY_PLANS)) {
						if (shpSectionList == null)
							shpSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_EMF_PAYMENT_ELECTION)) {
						if (peSectionList == null)
							peSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_EMF_EARNINGS_DEDUCTIONS)) {
						if (edSectionList == null)
							edSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_EMF_IDENTIFIERS)) {
						isIdentifier = true;
						if (idSectionList == null)
							idSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					}
					// for address handling
					else if (name.equalsIgnoreCase("First_Address_Line_Data")) {
						isAddressLine = true;
						for (int i = 0; i < streamReader.getAttributeCount(); i++) {
							addressLabel = streamReader.getAttributeValue(i);
							if (addressLabel.startsWith("ADDRESS_LINE_"))
								break;
						}
					}

					int e1 = streamReader.next();
					if (e1 == XMLStreamReader.END_ELEMENT) {

					} else {
						String value = null;
						try {
							if (streamReader.hasText())
								value = streamReader.getText();
						} catch (IllegalStateException ex) {
							LOGGER.error(ex.getMessage(), ex);
							ex.printStackTrace();
						}
						if (value != null && name != null && !value.trim().equals("")) {

							if (!isHeader) {
								if (!isRemove) {
									if (isIdentifier) {
										if ("Identifier_Value".equals(name)) {
											String mapKey = fieldDetails.get("Identifier_Type");
											fieldDetails.clear();
											fieldDetails.put(mapKey, value);
										} else
											fieldDetails.put(name, value);
									} else if (isAddressLine) {
										fieldDetails.put(addressLabel, value);
									} else
										fieldDetails.put(name, value);
								} else {
									if (!"Effective_Date".equalsIgnoreCase(name)
											&& !"Start_Date".equalsIgnoreCase(name))
										fieldDetails.put(name, "");
									else
										fieldDetails.put(name, value);
								}
							} else
								headerFieldDetails.put(name, value);
							if (name.equalsIgnoreCase(employeeNumberField) && employeeIdNumber == null) {
								employeeIdNumber = value;
							} else if (name.equalsIgnoreCase("Operation") && value.equalsIgnoreCase("REMOVE")) {
								isRemove = true;
							}
						}
					}
				}

				if (e == XMLStreamReader.END_ELEMENT) {
					String name = streamReader.getLocalName();
					if (name.equalsIgnoreCase(WD_SECTION_PAYGROUP)) {

					} else if (name.equalsIgnoreCase(WD_SECTION_HEADER)) {
						payrollCompanyId = headerFieldDetails.get("Payroll_Company_ID");
						if (!companyCode.equalsIgnoreCase(payrollCompanyId))
							return new EmployeeData(null, MSG_FILE_NOT_FOR_COMPANY, headerFieldDetails);
						headerSectionList.add(headerFieldDetails);
						headerSectionMap.put(WD_SECTION_HEADER, headerSectionList);
						isHeader = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMPLOYEE)) {
						sectionMap.putAll(headerSectionMap);
						if (positionSectionList != null)
							sectionMap.put(WD_SECTION_EMF_POSITION, positionSectionList);
						if (edSectionList != null)
							sectionMap.put(WD_SECTION_EMF_EARNINGS_DEDUCTIONS, edSectionList);
						if (apSectionList != null)
							sectionMap.put(WD_SECTION_EMF_ALLOWANCE_PLANS, apSectionList);
						if (shpSectionList != null)
							sectionMap.put(WD_SECTION_EMF_SALARY_AND_HOURLY_PLANS, shpSectionList);
						if (idSectionList != null) {
							Map<String, String> newMap = new HashMap<>();
							for (Map<String, String> idMap : idSectionList) {
								for (Map.Entry<String, String> entry : idMap.entrySet()) {
									newMap.put(entry.getKey(), entry.getValue());
								}
							}
							idSectionList.clear();
							idSectionList.add(newMap);
							sectionMap.put(WD_SECTION_EMF_IDENTIFIERS, idSectionList);
						}
						if (peSectionList != null)
							sectionMap.put(WD_SECTION_EMF_PAYMENT_ELECTION, peSectionList);
						employeeDataMap.put(employeeIdNumber, sectionMap);
						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_SUMMARY)) {
						sectionList.add(fieldDetails);
						sectionMap.put(WD_SECTION_SUMMARY, sectionList);
						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMF_PERSONAL)) {
						if (fieldDetails.containsKey("First_Address_Line_Data"))
							fieldDetails.remove("First_Address_Line_Data");
						sectionList.add(fieldDetails);
						sectionMap.put(WD_SECTION_EMF_PERSONAL, sectionList);
						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMF_STATUS)) {
						sectionList.add(fieldDetails);
						sectionMap.put(WD_SECTION_EMF_STATUS, sectionList);
						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMF_POSITION)) {
						positionSectionList.add(fieldDetails);

						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMF_EARNINGS_DEDUCTIONS)) {
						edSectionList.add(fieldDetails);

						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMF_ALLOWANCE_PLANS)) {
						apSectionList.add(fieldDetails);

						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMF_SALARY_AND_HOURLY_PLANS)) {
						fieldDetails.put("Effective_Date", headerFieldDetails.get("Pay_Period_Start"));
						shpSectionList.add(fieldDetails);

						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMF_IDENTIFIERS)) {
						idSectionList.add(fieldDetails);

						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMF_PAYMENT_ELECTION)) {
						peSectionList.add(fieldDetails);

						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase(WD_SECTION_EMF_ADDITIONAL_INFORMATION)) {
						sectionList.add(fieldDetails);
						sectionMap.put(WD_SECTION_EMF_ADDITIONAL_INFORMATION, sectionList);
						isRemove = false;
						isIdentifier = false;
					}
					if (name.equalsIgnoreCase("First_Address_Line_Data")) {
						isAddressLine = false;
						addressLabel = "";
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getMessage(), e1);
		}
		EmployeeData employeeData = new EmployeeData(employeeDataMap, null, headerFieldDetails);
		return employeeData;

	}

	private EmployeeData getPayrollDataMapFromByteArray(byte[] fileByte, String employeeNumberField, String companyCode)
			throws XMLStreamException {

		Map<String, Map<String, List<Map<String, String>>>> employeeDataMap = new HashMap<String, Map<String, List<Map<String, String>>>>();
		Map<String, List<Map<String, String>>> sectionMap = null;
		Map<String, List<Map<String, String>>> headerSectionMap = null;
		List<Map<String, String>> sectionList = null;
		List<Map<String, String>> headerSectionList = null;
		List<Map<String, String>> timeTrackingSectionList = null;
		List<Map<String, String>> timeOffSectionList = null;
		List<Map<String, String>> timeTrackingCorrectionSectionList = null;
		List<Map<String, String>> timeOffCorrectionSectionList = null;
		Map<String, String> fieldDetails = null;
		Map<String, String> headerFieldDetails = null;
		String employeeId = null;
		try {
			fileByte = new String(fileByte, "UTF-8").replaceAll("><", ">\n\t<").getBytes("UTF-8");
		} catch (UnsupportedEncodingException e2) {
			LOGGER.error(e2.getMessage(), e2);
		}
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream is = new ByteArrayInputStream(fileByte);
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(is);
		boolean isHeader = false;
		boolean isRemove = false;
		try {
			while (streamReader.hasNext()) {
				int e = streamReader.next();

				if (e == XMLStreamReader.START_ELEMENT) {
					String name = streamReader.getLocalName();
					if (name.equalsIgnoreCase(WD_SECTION_PAYGROUP)) {

					} else if (name.equalsIgnoreCase(WD_SECTION_HEADER)) {
						headerSectionMap = new HashMap<String, List<Map<String, String>>>();
						headerSectionList = new ArrayList<Map<String, String>>();
						headerFieldDetails = new HashMap<String, String>();
						isHeader = true;
					} else if (name.equalsIgnoreCase(WD_SECTION_EMPLOYEE)) {
						sectionMap = new HashMap<String, List<Map<String, String>>>();
						employeeId = null;
						sectionList = null;
						timeTrackingSectionList = null;
						timeOffSectionList = null;
						timeTrackingCorrectionSectionList = null;
						timeOffCorrectionSectionList = null;
						fieldDetails = null;
					} else if (name.equalsIgnoreCase(WD_SECTION_SUMMARY)) {
						sectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_PTRX_TIME_OFF)) {
						if (timeOffSectionList == null)
							timeOffSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_PTRX_TIME_TRACKING)) {
						if (timeTrackingSectionList == null)
							timeTrackingSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_PTRX_TIME_OFF_CORRECTION)) {
						if (timeOffCorrectionSectionList == null)
							timeOffCorrectionSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					} else if (name.equalsIgnoreCase(WD_SECTION_PTRX_TIME_TRACKING_CORRECTION)) {
						if (timeTrackingCorrectionSectionList == null)
							timeTrackingCorrectionSectionList = new ArrayList<Map<String, String>>();
						fieldDetails = new HashMap<String, String>();
					}

					int e1 = streamReader.next();
					if (e1 == XMLStreamReader.END_ELEMENT) {

					} else {
						String value = null;
						try {
							value = streamReader.getText();

						} catch (IllegalStateException ex) {
							LOGGER.error(ex.getMessage(), ex);
							ex.printStackTrace();
						}
						if (value != null && name != null && !value.trim().equals("")) {

							if (!isHeader) {
								if (!isRemove)
									fieldDetails.put(name, value);
								else
									fieldDetails.put(name, "");
							} else
								headerFieldDetails.put(name, value);
							if (name.equalsIgnoreCase(employeeNumberField) && employeeId == null) {
								employeeId = value;
							} else if (name.equalsIgnoreCase("Operation") && value.equalsIgnoreCase("REMOVE")) {
								isRemove = true;
							}
						}
					}
				}

				if (e == XMLStreamReader.END_ELEMENT) {
					String name = streamReader.getLocalName();
					if (name.equalsIgnoreCase(WD_SECTION_PAYGROUP)) {

					} else if (name.equalsIgnoreCase(WD_SECTION_HEADER)) {
						String payrollCompanyId = headerFieldDetails.get("Payroll_Company_ID");
						if (!companyCode.equalsIgnoreCase(payrollCompanyId))
							return new EmployeeData(null, MSG_FILE_NOT_FOR_COMPANY, headerFieldDetails);
						headerSectionList.add(headerFieldDetails);
						headerSectionMap.put(WD_SECTION_HEADER, headerSectionList);
						isHeader = false;
					} else if (name.equalsIgnoreCase(WD_SECTION_EMPLOYEE)) {
						sectionMap.putAll(headerSectionMap);
						if (timeOffSectionList != null)
							sectionMap.put(WD_SECTION_PTRX_TIME_OFF, timeOffSectionList);
						if (timeTrackingSectionList != null)
							sectionMap.put(WD_SECTION_PTRX_TIME_TRACKING, timeTrackingSectionList);
						if (timeOffCorrectionSectionList != null)
							sectionMap.put(WD_SECTION_PTRX_TIME_OFF_CORRECTION, timeOffCorrectionSectionList);
						if (timeTrackingCorrectionSectionList != null)
							sectionMap.put(WD_SECTION_PTRX_TIME_TRACKING_CORRECTION, timeTrackingCorrectionSectionList);
						employeeDataMap.put(employeeId, sectionMap);
						isRemove = false;
					} else if (name.equalsIgnoreCase(WD_SECTION_SUMMARY)) {
						sectionList.add(fieldDetails);
						sectionMap.put(WD_SECTION_SUMMARY, sectionList);
						isRemove = false;
					} else if (name.equalsIgnoreCase(WD_SECTION_PTRX_TIME_OFF)) {
						timeOffSectionList.add(fieldDetails);

						isRemove = false;
					} else if (name.equalsIgnoreCase(WD_SECTION_PTRX_TIME_TRACKING)) {
						timeTrackingSectionList.add(fieldDetails);

						isRemove = false;
					} else if (name.equalsIgnoreCase(WD_SECTION_PTRX_TIME_OFF_CORRECTION)) {
						timeOffCorrectionSectionList.add(fieldDetails);

						isRemove = false;
					} else if (name.equalsIgnoreCase(WD_SECTION_PTRX_TIME_TRACKING_CORRECTION)) {
						timeTrackingCorrectionSectionList.add(fieldDetails);

						isRemove = false;
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getMessage(), e1);
		}

		return new EmployeeData(employeeDataMap, null, headerFieldDetails);

	}

	/*
	 * public static void main(String[] args) throws XMLStreamException,
	 * IOException { FileInputStream fis = new
	 * FileInputStream("C:/empdataxmlsample4.xml"); byte[] buff = new byte[256];
	 * int bytesRead = 0; ByteArrayOutputStream out = new
	 * ByteArrayOutputStream(); while ((bytesRead = fis.read(buff)) != -1) {
	 * out.write(buff, 0, bytesRead); } System.out.println(new
	 * WorkdayFtpIntegrationLogicImpl().getEmployeeDataMapFromByteArray(out.
	 * toByteArray(), "Employee_ID")); fis.close(); }
	 */

	private DataImportForm preProcessAndCreateUpdateEmpData(Long companyId,
			Map<String, Map<String, List<Map<String, String>>>> employeesDataMap, Set<String> empNumkeys,
			Map<String, WorkdayFieldMappingDTO> fieldsMappingMap, String uploadType, String transactionType,
			String dateFormat, DataDictionary empNumberDataDictionary) throws JSONException {

		DataImportForm dataImportForm = null;
		try {
			HashMap<String, EmpDataImportTemplateField> colMap = new HashMap<String, EmpDataImportTemplateField>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			List<HashMap<String, String>> importedDataExtList = new ArrayList<HashMap<String, String>>();
			List<DataImportLogDTO> dataImportLogList = new ArrayList<DataImportLogDTO>();

			if (empNumkeys != null) {
				int countRow = 0;
				for (String empNumkey : empNumkeys) {
					HashMap<String, String> importedDataMap = new HashMap<String, String>();
					importedDataMap.put("rowNumber", Integer.toString(countRow++));

					// For Employee Number: start
					EmpDataImportTemplateField empDataImportEmpNumField = new EmpDataImportTemplateField();
					empDataImportEmpNumField.setExcelFieldName(PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY);
					empDataImportEmpNumField.setDataDictionary(empNumberDataDictionary);
					colMap.put(PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY + "_"
							+ empNumberDataDictionary.getDataDictionaryId(), empDataImportEmpNumField);
					importedDataMap.put(PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY + "_"
							+ empNumberDataDictionary.getDataDictionaryId(), empNumkey);
					// For Employee Number: end

					Map<String, List<Map<String, String>>> empNumJsonObj = employeesDataMap.get(empNumkey);
					Set<String> sectionNameKeys = empNumJsonObj.keySet();
					if (sectionNameKeys != null) {
						for (String sectionNameKey : sectionNameKeys) {
							List<Map<String, String>> fieldKeyValueMapList = empNumJsonObj.get(sectionNameKey);
							int tableRowCount = 1;
							for (Map<String, String> fieldKeyValueMap : fieldKeyValueMapList) {
								if (employeeSectionList.contains(sectionNameKey)) {
									HashMap<String, String> importedDataExtMap = new HashMap<String, String>();
									if (tableRowCount != 1) {
										importedDataExtMap.put(PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY + "_"
												+ empNumberDataDictionary.getDataDictionaryId(), empNumkey);
									}
									Set<String> fieldKeys = fieldKeyValueMap.keySet();
									for (String fieldColKey : fieldKeys) {
										String fieldVal = fieldKeyValueMap.get(fieldColKey);

										setEmpCustomTableColumnData(companyId, colMap, importedDataMap, sectionNameKey,
												tableRowCount, importedDataExtMap, fieldColKey, fieldVal,
												dataImportLogList, fieldsMappingMap, dateFormat);
									}

									if (tableRowCount != 1 && !importedDataExtMap.isEmpty()
											&& importedDataExtMap.size() != 1) {
										importedDataExtList.add(importedDataExtMap);
									}
									tableRowCount++;

								} else if (sectionNameKey.equalsIgnoreCase("Personal")) {
									Set<String> fieldKeys = fieldKeyValueMap.keySet();

									for (String fieldColKey : fieldKeys) {
										String fieldVal = fieldKeyValueMap.get(fieldColKey);
										setEmpCustomFieldData(companyId, colMap, importedDataMap, sectionNameKey,
												fieldColKey, fieldVal, dataImportLogList, fieldsMappingMap, dateFormat);
									}
								}
							}
						}
					}
					importedData.add(importedDataMap);
				}
				// To merge (insert) more records of custom table
				for (HashMap<String, String> map : importedDataExtList) {
					map.put("rowNumber", Integer.toString(countRow++));
					importedData.add(map);
				}
			}

			if (!dataImportLogList.isEmpty()) {
				dataImportForm = new DataImportForm();
				dataImportForm.setDataImportLogList(dataImportLogList);
				return dataImportForm;
			}
			dataImportForm = dataImportLogic.createUpdateEmpDataWS(companyId, colMap, importedData, uploadType,
					transactionType);
			if (dataImportForm != null) {
				dataImportForm.setImportedData(importedData);
			}
		} catch (Exception e) {
			throw new PayAsiaSystemException(e.getMessage(), e);
		}
		return dataImportForm;
	}

	private void setEmpCustomTableColumnData(Long companyId, HashMap<String, EmpDataImportTemplateField> colMap,
			HashMap<String, String> importedDataMap, String sectionNameKey, int tableRowCount,
			HashMap<String, String> importedDataExtMap, String fieldTableColKey, String fieldTableColVal,
			List<DataImportLogDTO> dataImportLogList, Map<String, WorkdayFieldMappingDTO> fieldsMappingMap,
			String dateFormat) throws JSONException {

		try {
			if (fieldsMappingMap.get(sectionNameKey + "." + fieldTableColKey) != null) {
				DataDictionary dataDictionaryVO = fieldsMappingMap.get(sectionNameKey + "." + fieldTableColKey)
						.getHroDataDictionary();
				if (dataDictionaryVO != null) {
					EmpDataImportTemplateField empDataImportTemplateField = new EmpDataImportTemplateField();
					empDataImportTemplateField.setExcelFieldName(dataDictionaryVO.getLabel());

					empDataImportTemplateField.setDataDictionary(dataDictionaryVO);
					colMap.put(dataDictionaryVO.getLabel() + "_" + dataDictionaryVO.getDataDictionaryId(),
							empDataImportTemplateField);

					if (dateFieldList.contains(fieldTableColKey)) {
						String fieldValue = "";
						if (StringUtils.isNotBlank(dateFormat)) {
							fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldTableColVal, dateFormat);
							if (StringUtils.isBlank(fieldValue)) {
								fieldValue = DateUtils.convertDateFormat(fieldTableColVal, "yyyy-mm-dd");
							}
						} else {
							fieldValue = DateUtils.convertDateFormat(fieldTableColVal, "yyyy-mm-dd");
						}
						fieldTableColVal = fieldValue;
					}

					if (!fieldsMappingMap.get(sectionNameKey + "." + fieldTableColKey).getDataTransformationMap()
							.isEmpty()) {
						String transformedVal = fieldsMappingMap.get(sectionNameKey + "." + fieldTableColKey)
								.getDataTransformationMap().get(fieldTableColVal);
						if (StringUtils.isNotBlank(transformedVal)) {
							fieldTableColVal = transformedVal;
						}
					}

					if (tableRowCount == 1) {
						importedDataMap.put(dataDictionaryVO.getLabel() + "_" + dataDictionaryVO.getDataDictionaryId(),
								fieldTableColVal);
					} else {
						importedDataExtMap.put(
								dataDictionaryVO.getLabel() + "_" + dataDictionaryVO.getDataDictionaryId(),
								fieldTableColVal);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private void setEmpCustomFieldData(Long companyId, HashMap<String, EmpDataImportTemplateField> colMap,
			HashMap<String, String> importedDataMap, String sectionNameKey, String fieldKey, String fieldVal,
			List<DataImportLogDTO> dataImportLogList, Map<String, WorkdayFieldMappingDTO> fieldsMappingMap,
			String dateFormat) {

		// LOGGER.debug("FieldKey: " + fieldKey + ", FieldValue: " + fieldVal);
		try {
			if (fieldsMappingMap.get(sectionNameKey + "." + fieldKey) != null) {
				DataDictionary dataDictionaryVO = fieldsMappingMap.get(sectionNameKey + "." + fieldKey)
						.getHroDataDictionary();
				if (dataDictionaryVO != null) {
					// Custom Field Data
					EmpDataImportTemplateField empDataImportTemplateField = new EmpDataImportTemplateField();
					empDataImportTemplateField.setExcelFieldName(dataDictionaryVO.getLabel());

					empDataImportTemplateField.setDataDictionary(dataDictionaryVO);
					colMap.put(dataDictionaryVO.getLabel() + "_" + dataDictionaryVO.getDataDictionaryId(),
							empDataImportTemplateField);

					if (dateFieldList.contains(fieldKey)) {
						String fieldValue = "";
						if (StringUtils.isNotBlank(dateFormat)) {
							fieldValue = DateUtils.convertDateFormatyyyyMMdd(fieldVal, dateFormat);
							if (StringUtils.isBlank(fieldValue)) {
								fieldValue = DateUtils.convertDateFormat(fieldVal, "yyyy-mm-dd");
							}
						} else {
							fieldValue = DateUtils.convertDateFormat(fieldVal, "yyyy-mm-dd");
						}
						fieldVal = fieldValue;
					}

					if (!fieldsMappingMap.get(sectionNameKey + "." + fieldKey).getDataTransformationMap().isEmpty()) {
						String transformedVal = fieldsMappingMap.get(sectionNameKey + "." + fieldKey)
								.getDataTransformationMap().get(fieldVal);
						if (StringUtils.isNotBlank(transformedVal)) {
							fieldVal = transformedVal;
						}
					}

					importedDataMap.put(dataDictionaryVO.getLabel() + "_" + dataDictionaryVO.getDataDictionaryId(),
							fieldVal);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	private PaygroupBatchMessage saveAllXmlData(Map<String, Map<String, List<Map<String, String>>>> dataMap,
			Map<String, String> xmlEmployeeMap, Map<String, WorkdayFieldMaster> workdayFieldMap,
			WorkdayAppCodeMaster workdayAppCode, WorkdayAppCodeMaster batchTypeAppCode, String payPeriodStartDateStr,
			String payPeriodEndDateStr, WorkdayFtpConfig ftpConfig, Map<String, Boolean> oldAndNewEmployeeMap,
			boolean isEmployeeData) {

		LOGGER.info("## data map ::: " + dataMap);
		LOGGER.info("## oldAndNewEmployeeMap ::: " + oldAndNewEmployeeMap);
		LOGGER.info("## xmlEmployeeMap ::: " + xmlEmployeeMap);
		String importType = isEmployeeData ? "Employee" : "Payroll";
		Company company = ftpConfig.getCompany();

		java.sql.Date payPeriodStartDate = DateUtils.workdayDateStringToDate(payPeriodStartDateStr,
				WORKDAY_PICOF_HEADER_DATE_FORMAT);
		java.sql.Date payPeriodEndDate = DateUtils.workdayDateStringToDate(payPeriodEndDateStr,
				WORKDAY_PICOF_HEADER_DATE_FORMAT);

		WorkdayPaygroupBatch workdayPaygroupBatchExisting = workdayPaygroupBatchDAO
				.findByPayPeriodDates(company.getCompanyId(), payPeriodStartDate, payPeriodEndDate, isEmployeeData);
		// if batch exists
		if (workdayPaygroupBatchExisting != null) {
			workdayPaygroupBatchExisting.setLatest(false);
			workdayPaygroupBatchDAO.update(workdayPaygroupBatchExisting);
		}
		WorkdayPaygroupBatch workdayPaygroupBatch = new WorkdayPaygroupBatch();
		workdayPaygroupBatch.setCompany(company);
		workdayPaygroupBatch.setWorkdayAppCode(workdayAppCode);
		workdayPaygroupBatch.setBatchTypeAppCode(batchTypeAppCode);
		workdayPaygroupBatch.setPayPeriodStartDate(payPeriodStartDate);
		workdayPaygroupBatch.setPayPeriodEndDate(payPeriodEndDate);
		workdayPaygroupBatch.setEmployeeData(isEmployeeData ? true : false);
		workdayPaygroupBatch.setLatest(true);
		workdayPaygroupBatch = workdayPaygroupBatchDAO.saveReturn(workdayPaygroupBatch);

		for (String employeeNumber : xmlEmployeeMap.keySet()) {
			Employee employee = employeeDAO.findByNumber(employeeNumber, company.getCompanyId());
			LOGGER.info("## Employee detail for company " + company.getCompanyId() + ", employee : " + employeeNumber
					+ " is " + employee);
			/*
			 * if(employee == null) { return new
			 * PaygroupBatchMessage(workdayPaygroupBatch,
			 * " Employee "+employeeNumber+" not found."); }
			 */
			WorkdayPaygroupBatchData workdayPaygroupBatchData = new WorkdayPaygroupBatchData();
			workdayPaygroupBatchData.setCompany(company);
			workdayPaygroupBatchData.setEmployee(employee);
			workdayPaygroupBatchData.setWorkdayPaygroupBatch(workdayPaygroupBatch);
			workdayPaygroupBatchData.setEmployeeXML(xmlEmployeeMap.get(employeeNumber));
			workdayPaygroupBatchData.setNewEmployee(oldAndNewEmployeeMap.get(employeeNumber));
			workdayPaygroupBatchData.setImportType(importType);
			workdayPaygroupBatchDataDAO.save(workdayPaygroupBatchData);
		}
		return new PaygroupBatchMessage(workdayPaygroupBatch, null);
	}

	private class PaygroupBatchMessage {

		private WorkdayPaygroupBatch paygroupBatch;
		private String errorMessage;

		public PaygroupBatchMessage(WorkdayPaygroupBatch paygroupBatch, String errorMessage) {
			this.paygroupBatch = paygroupBatch;
			this.errorMessage = errorMessage;
		}

		public WorkdayPaygroupBatch getPaygroupBatch() {
			return paygroupBatch;
		}

		public String getErrorMessage() {
			return errorMessage;
		}
	}

	private class EmployeeData {

		Map<String, Map<String, List<Map<String, String>>>> employeesDataMap = new HashMap<>();
		private String message;
		
		public EmployeeData(Map<String, Map<String, List<Map<String, String>>>> employeesDataMap, String message,
				Map<String, String> headerMap) {
			this.employeesDataMap = employeesDataMap;
			this.message = message;
		}

		public Map<String, Map<String, List<Map<String, String>>>> getEmployeesDataMap() {
			return employeesDataMap;
		}

		public String getMessage() {
			return message;
		}
	}

	private void saveImportHistoryAndLog(Long companyId, Employee employeeVO, FtpFileDataDTO ftpXmlFileData,
			Set<String> employeeNumKeys, int existingEmployeesCount, int newHireEmployeesCount, int totalEmployeesCount,
			DataImportForm dataImportForm, byte[] ptrxImportedData, String failedRemarks, boolean importStatus) {

		String type = ftpXmlFileData.isEmployeeData() ? "EMF" : "PTRX";
		// save import history
		Company companyVO = companyDAO.findById(companyId);
		WorkdayFtpImportHistory ftpImportHistoryReturn = saveImportHistory(companyVO, employeeVO, ftpXmlFileData,
				totalEmployeesCount, existingEmployeesCount, newHireEmployeesCount, importStatus,
				ftpXmlFileData.isEmployeeData() ? "Employee" : "Payroll", failedRemarks);
		LOGGER.info("WD " + type + " Import : Import History Saved");
		// save data & error log
		if (ftpXmlFileData.isEmployeeData() && dataImportForm != null) {
			try {
				JSONObject importLogs = getEmpFieldImportedLogs(employeeNumKeys, dataImportForm);
				JSONArray errorJson = null;
				if (dataImportForm.getDataImportLogList() != null && !dataImportForm.getDataImportLogList().isEmpty()) {
					errorJson = (JSONArray) JSONSerializer.toJSON(dataImportForm.getDataImportLogList());
				}
				saveLogJsonToFile(importLogs, errorJson, ftpImportHistoryReturn.getFtpImportHistoryId(), companyId,
						ftpXmlFileData.isEmployeeData());
			} catch (Exception e) {
				LOGGER.error("ERROR while saving EMF Log: " + e.getMessage(), e);
			}
		} else {
			try {
				savePtrxLogJsonToFile(XML.toJSONObject(new String(ptrxImportedData, "UTF-8")), null,
						ftpImportHistoryReturn.getFtpImportHistoryId(), companyId, ftpXmlFileData.isEmployeeData());
			} catch (UnsupportedEncodingException | org.json.JSONException e) {
				LOGGER.error("ERROR while saving PTRX Log: " + e.getMessage(), e);
			}
		}
		LOGGER.info("WD " + type + " Import : Log File Saved");
	}

	private WorkdayFtpImportHistory saveImportHistory(Company company, Employee employee, FtpFileDataDTO ftpXmlFileData,
			int totalEmployeesCount, int existingEmployeesCount, int newHireEmployeesCount, boolean importStatus,
			String importType, String failedRemarks) {

		WorkdayFtpImportHistory ftpImportHistoryVO = new WorkdayFtpImportHistory();
		ftpImportHistoryVO.setCompany(company);
		ftpImportHistoryVO.setCreatedDate(new Timestamp(new Date().getTime()));
		ftpImportHistoryVO.setCreatedBy(employee);
		ftpImportHistoryVO.setExistingEmpRecordsUpdated(existingEmployeesCount);
		ftpImportHistoryVO.setNewEmpRecordsUpdated(newHireEmployeesCount);
		ftpImportHistoryVO.setTotalEmpRecords(totalEmployeesCount);
		ftpImportHistoryVO.setFailedRemarks(failedRemarks);
		ftpImportHistoryVO.setImportFileName(ftpXmlFileData.getFileName());
		ftpImportHistoryVO.setImportStatus(importStatus);
		ftpImportHistoryVO.setImportType(importType);
		return workdayFtpImportHistoryDAO.saveHistory(ftpImportHistoryVO);
	}

	private JSONObject getEmpFieldImportedLogs(Set<String> employeeNumKeys, DataImportForm dataImportForm) {

		List<HashMap<String, String>> importedDataList = new ArrayList<HashMap<String, String>>();
		importedDataList = dataImportForm.getImportedData();

		HashMap<String, List<HashMap<String, String>>> empImportedDataListMap = new HashMap<String, List<HashMap<String, String>>>();
		for (String ftpEmployeeNumber : employeeNumKeys) {
			for (HashMap<String, String> importedDataMap : importedDataList) {
				if (importedDataMap.containsValue(ftpEmployeeNumber)) {
					if (empImportedDataListMap.get(ftpEmployeeNumber) == null) {
						List<HashMap<String, String>> importedDataMapList = new ArrayList<HashMap<String, String>>();
						importedDataMapList.add(importedDataMap);
						empImportedDataListMap.put(ftpEmployeeNumber, importedDataMapList);
					} else {
						List<HashMap<String, String>> importedDataMapList = empImportedDataListMap
								.get(ftpEmployeeNumber);
						importedDataMapList.add(importedDataMap);
						empImportedDataListMap.put(ftpEmployeeNumber, importedDataMapList);
					}
				}
			}
		}
		JSONObject allEmpDataJsonFinalObj = new JSONObject();
		Set<String> keySet = empImportedDataListMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			List<HashMap<String, String>> empImportedDataMapList = empImportedDataListMap.get(key);
			JSONObject empDataJsonObj = new JSONObject();
			JSONObject fieldKeyValueObj = new JSONObject();
			JSONArray fieldKeyValueArr = new JSONArray();
			for (HashMap<String, String> employeeImportedDataMap : empImportedDataMapList) {
				Set<Entry<String, String>> entrySet = employeeImportedDataMap.entrySet();
				for (Entry<String, String> ent : entrySet) {
					if (ent.getKey().indexOf("_") > 0) {
						fieldKeyValueObj.put(ent.getKey().substring(0, ent.getKey().lastIndexOf("_")), ent.getValue());
					} else {
						fieldKeyValueObj.put(ent.getKey(), ent.getValue());
					}

				}
				fieldKeyValueArr.add(fieldKeyValueObj);
				empDataJsonObj.put(key, fieldKeyValueArr);
			}

			allEmpDataJsonFinalObj.accumulateAll(empDataJsonObj);
		}

		return allEmpDataJsonFinalObj;
	}

	public static void main(String[] args) {
		FileUtils fu = new FileUtils();
		FilePathGeneratorDTO filePathGenerator = fu.getFileCommonPath("/var/lib/tomcat8/data_new/payasia_upload",
				"company", 759L, "Ftp", "123_Data", File.separator + WORKDAY_FTP_LOG_FOLDER + File.separator, "demoemp",
				"txt", PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0L);
		String filePath = fu.getGeneratedFilePath(filePathGenerator);
		System.out.println(filePath);
	}

	private void saveLogJsonToFile(JSONObject importLogs, JSONArray errorJson, long ftpImportHistoryId, Long companyId,
			boolean isEmployeeData) {

		String filePartName = isEmployeeData ? WORKDAY_FTP_IMPORT_EMF : WORKDAY_FTP_IMPORT_PTRX;
		// FOR DATA LOG
		String filePathLocal = downloadPath + File.separator + WORKDAY_FTP_LOG_FOLDER + File.separator + companyId
				+ File.separator;
		String fileName = "" + ftpImportHistoryId + "_" + filePartName + "_" + DATA_LOG_FILE;
		String ext = "txt";
		// if AWS enabled
		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyId, PayAsiaConstants.FTP_DOCUMENT_DIRECTORY_NAME, fileName, WORKDAY_FTP_LOG_FOLDER, null,
					"txt", PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			fileName = filePath;
			awss3LogicImpl.uploadByteArrayFile(importLogs.toString().getBytes(), fileName);
		}
		// else
		else {
			fileName = fileName + "." + ext;
			File newFile = new File(filePathLocal, fileName);

			// check if folder exists. If not, create it.
			File folder = new File(filePathLocal);
			if (!folder.exists()) {
				folder.mkdirs();
			} else {
				if (newFile.exists() && !newFile.isDirectory()) {
					newFile.delete();
				}
			}
			try {
				FileWriter fw = new FileWriter(newFile);
				fw.write(importLogs.toString());
				fw.flush();
				fw.close();
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
				throw new PayAsiaSystemException(iOException.getMessage(), iOException);
			}
		}

		// FOR ERROR LOG
		if (errorJson != null) {
			String errorFilePathLocal = downloadPath + File.separator + WORKDAY_FTP_LOG_FOLDER + File.separator
					+ companyId + File.separator;
			String errorFileName = "" + ftpImportHistoryId + "_" + filePartName + "_" + ERROR_LOG_FILE;

			// if AWS enabled
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				FilePathGeneratorDTO errorFilePathGenerator = fileUtils.getFileCommonPath(downloadPath,
						rootDirectoryName, companyId, PayAsiaConstants.FTP_DOCUMENT_DIRECTORY_NAME, errorFileName,
						WORKDAY_FTP_LOG_FOLDER, null, "txt", PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
				String errorFilePath = fileUtils.getGeneratedFilePath(errorFilePathGenerator);
				errorFileName = errorFilePath;
				awss3LogicImpl.uploadByteArrayFile(errorJson.toString().getBytes(), errorFileName);
			}
			// else
			else {
				errorFileName = errorFileName + "." + ext;
				File newFile = new File(errorFilePathLocal, errorFileName);

				// check if folder exists. If not, create it.
				File folder = new File(errorFilePathLocal);
				if (!folder.exists()) {
					folder.mkdirs();
				} else {
					if (newFile.exists() && !newFile.isDirectory()) {
						newFile.delete();
					}
				}
				try {
					FileWriter fw = new FileWriter(newFile);
					fw.write(errorJson.toString());
					fw.flush();
					fw.close();
				} catch (IOException iOException) {
					LOGGER.error(iOException.getMessage(), iOException);
					throw new PayAsiaSystemException(iOException.getMessage(), iOException);
				}
			}
		}
	}

	private void savePtrxLogJsonToFile(org.json.JSONObject importLogs, JSONArray errorJson, long ftpImportHistoryId,
			Long companyId, boolean isEmployeeData) {

		String filePartName = isEmployeeData ? WORKDAY_FTP_IMPORT_EMF : WORKDAY_FTP_IMPORT_PTRX;
		// FOR DATA LOG
		String filePathLocal = downloadPath + File.separator + WORKDAY_FTP_LOG_FOLDER + File.separator + companyId
				+ File.separator;
		String fileName = "" + ftpImportHistoryId + "_" + filePartName + "_" + DATA_LOG_FILE;
		String ext = "txt";
		// if AWS enabled
		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyId, PayAsiaConstants.FTP_DOCUMENT_DIRECTORY_NAME, fileName, WORKDAY_FTP_LOG_FOLDER, null,
					"txt", PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			fileName = filePath;
			awss3LogicImpl.uploadByteArrayFile(importLogs.toString().getBytes(), fileName);
		}
		// else
		else {
			fileName = fileName + "." + ext;
			File newFile = new File(filePathLocal, fileName);

			// check if folder exists. If not, create it.
			File folder = new File(filePathLocal);
			if (!folder.exists()) {
				folder.mkdirs();
			} else {
				if (newFile.exists() && !newFile.isDirectory()) {
					newFile.delete();
				}
			}
			try {
				FileWriter fw = new FileWriter(newFile);
				fw.write(importLogs.toString());
				fw.flush();
				fw.close();
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
				throw new PayAsiaSystemException(iOException.getMessage(), iOException);
			}
		}

		// FOR ERROR LOG
		if (errorJson != null) {
			String errorFilePathLocal = downloadPath + File.separator + WORKDAY_FTP_LOG_FOLDER + File.separator
					+ companyId + File.separator;
			String errorFileName = "" + ftpImportHistoryId + "_" + filePartName + "_" + ERROR_LOG_FILE;

			// if AWS enabled
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				FilePathGeneratorDTO errorFilePathGenerator = fileUtils.getFileCommonPath(downloadPath,
						rootDirectoryName, companyId, PayAsiaConstants.FTP_DOCUMENT_DIRECTORY_NAME, errorFileName,
						WORKDAY_FTP_LOG_FOLDER, null, "txt", PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
				String errorFilePath = fileUtils.getGeneratedFilePath(errorFilePathGenerator);
				errorFileName = errorFilePath;
				awss3LogicImpl.uploadByteArrayFile(errorJson.toString().getBytes(), errorFileName);
			}
			// else
			else {
				errorFileName = errorFileName + "." + ext;
				File newFile = new File(errorFilePathLocal, errorFileName);

				// check if folder exists. If not, create it.
				File folder = new File(errorFilePathLocal);
				if (!folder.exists()) {
					folder.mkdirs();
				} else {
					if (newFile.exists() && !newFile.isDirectory()) {
						newFile.delete();
					}
				}
				try {
					FileWriter fw = new FileWriter(newFile);
					fw.write(errorJson.toString());
					fw.flush();
					fw.close();
				} catch (IOException iOException) {
					LOGGER.error(iOException.getMessage(), iOException);
					throw new PayAsiaSystemException(iOException.getMessage(), iOException);
				}
			}
		}
	}

	@Override
	public FTPImportHistoryFormResponse getImportHistory(String fromDate, String toDate, Long companyId,
			WorkdayFtpImportHistoryDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO, String importType) {

		Company companyVO = companyDAO.findById(companyId);
		List<FTPImportHistoryForm> importHistoryList = new ArrayList<FTPImportHistoryForm>();
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate, companyVO.getDateFormat()));
		}
		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate, companyVO.getDateFormat()));
		}
		List<WorkdayFtpImportHistory> historyList = workdayFtpImportHistoryDAO.findByCondition(companyId, conditionDTO,
				pageDTO, sortDTO, importType);

		for (WorkdayFtpImportHistory ftpImportHistory : historyList) {

			FTPImportHistoryForm ftpImportHistoryForm = new FTPImportHistoryForm();
			ftpImportHistoryForm.setCompanyId(ftpImportHistory.getCompany().getCompanyId());

			ftpImportHistoryForm.setCreatedDate(DateUtils.timeStampToStringWithTime(ftpImportHistory.getCreatedDate(),
					ftpImportHistory.getCompany().getDateFormat()));

			ftpImportHistoryForm.setFtpImportHistoryId(ftpImportHistory.getFtpImportHistoryId());

			if (ftpImportHistory.getImportStatus()) {
				ftpImportHistoryForm.setImportStatus("Success");
			} else {
				ftpImportHistoryForm.setImportStatus("Failed");
			}

			ftpImportHistoryForm.setImportFileName(ftpImportHistory.getImportFileName());

			ftpImportHistoryForm.setTotalEmpRecords(ftpImportHistory.getTotalEmpRecords());
			ftpImportHistoryForm.setTotalExistingEmpRecords(ftpImportHistory.getExistingEmpRecordsUpdated());
			ftpImportHistoryForm.setTotalNewHireEmpRecords(ftpImportHistory.getNewEmpRecordsUpdated());
			ftpImportHistoryForm.setFailedRemarks(ftpImportHistory.getFailedRemarks());

			importHistoryList.add(ftpImportHistoryForm);

		}
		FTPImportHistoryFormResponse response = new FTPImportHistoryFormResponse();
		int recordSize = workdayFtpImportHistoryDAO.getCountForImportHistory(companyId, conditionDTO, importType);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		response.setFtpImportHistoryList(importHistoryList);
		return response;
	}

	@Override
	public Workbook getEmfImportLog(Long logFileId, Long companyId) {

		Workbook wb = new HSSFWorkbook();
		String fileName = String.valueOf(logFileId) + "_" + WORKDAY_FTP_IMPORT_EMF + "_" + DATA_LOG_FILE;
		String errorFileName = String.valueOf(logFileId) + "_" + WORKDAY_FTP_IMPORT_EMF + "_" + ERROR_LOG_FILE;
		// Creating Sheet for Imported (Success) Data
		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.FTP_DOCUMENT_DIRECTORY_NAME, fileName, WORKDAY_FTP_LOG_FOLDER, null, "txt",
				PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		
		WorkdayFtpImportHistory workdayFtpImportHistory = workdayFtpImportHistoryDAO.findById(logFileId);
		String dataSheetName = workdayFtpImportHistory.getImportStatus() ? "Imported Data" : "Importable Data";
		HSSFSheet firstSheet = (HSSFSheet) wb.createSheet(dataSheetName);
		BufferedReader reader = null;
		try {
			String jsonString = null;

			InputStream fileData = awss3LogicImpl.readS3ObjectAsStream(filePath);
			reader = new BufferedReader(new InputStreamReader(fileData));

			if (reader != null) {
				String line = null;
				StringBuilder stringBuilder = new StringBuilder();
				String ls = System.getProperty("line.separator");

				try {
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line);
						stringBuilder.append(ls);
					}

					jsonString = stringBuilder.toString();
				} finally {
					reader.close();
				}

				JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonString);
				Set jsons = json.keySet();
				Iterator iter = jsons.iterator();
				int rownum = 0;
				while (iter.hasNext()) {
					String key = (String) iter.next();
					// String ss = json.get(key).toString();
					Row row = firstSheet.createRow(rownum++);
					int cellnum = 0;
					Cell cell = row.createCell(cellnum++);
					cell.setCellValue("Employee Information:");
					HSSFFont font = (HSSFFont) wb.createFont();
					font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
					CellStyle cellStyle = wb.createCellStyle();
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);

					row = firstSheet.createRow(rownum++);
					cellnum = 0;
					cell = row.createCell(cellnum++);
					cell.setCellValue("Employee Number");
					cell = row.createCell(cellnum++);
					cell.setCellValue(key);

					// JSONObject subjson = (JSONObject)
					// JSONSerializer.toJSON(ss);
					JSONArray jsonArray = (JSONArray) json.get(key);
					Iterator<String> iterator = jsonArray.iterator();
					while (iterator.hasNext()) {
						JSONObject subjson = (JSONObject) JSONSerializer.toJSON(iterator.next());
						Set jsons2 = subjson.keySet();
						Iterator iter2 = jsons2.iterator();
						while (iter2.hasNext()) {
							String employeeDataKey = (String) iter2.next();
							String employeeDatavalue = subjson.get(employeeDataKey).toString();
							row = firstSheet.createRow(rownum++);
							cellnum = 0;
							cell = row.createCell(cellnum++);
							cell.setCellValue(employeeDataKey);
							int columnIndex = cell.getColumnIndex();
							firstSheet.autoSizeColumn(columnIndex);
							cell = row.createCell(cellnum++);
							cell.setCellValue(employeeDatavalue);
							columnIndex = cell.getColumnIndex();
							firstSheet.autoSizeColumn(columnIndex);
						}
						row = firstSheet.createRow(rownum++);
					}

					// System.out.println(iterator.next());
				}
			}
			LOGGER.info("WD EMF Import : created Data sheet");
			// Creating Sheet for Error Data
			// if import was not success, process error log
			if (!workdayFtpImportHistory.getImportStatus()) {
				FilePathGeneratorDTO errorFilePathGenerator = fileUtils.getFileCommonPath(downloadPath,
						rootDirectoryName, companyId, PayAsiaConstants.FTP_DOCUMENT_DIRECTORY_NAME, errorFileName,
						WORKDAY_FTP_LOG_FOLDER, null, "txt", PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
				String errorFilePath = fileUtils.getGeneratedFilePath(errorFilePathGenerator);
				String errorFilePathLocal = downloadPath + File.separator + WORKDAY_FTP_LOG_FOLDER + File.separator
						+ companyId + File.separator + errorFileName + "." + "txt";

				BufferedReader errorReader = null;
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
					InputStream errorFileData = null;
					try {
						errorFileData = awss3LogicImpl.readS3ObjectAsStream(errorFilePath);
					} catch (Exception e) {
						LOGGER.info("No import error data found at " + errorFilePath);
					}
					if (errorFileData != null)
						errorReader = new BufferedReader(new InputStreamReader(errorFileData));
				} else {
					File file = new File(errorFilePathLocal);
					if (file.exists())
						errorReader = new BufferedReader(new FileReader(errorFilePathLocal));
					else {
						LOGGER.info("No import error data found at " + errorFilePathLocal);
					}
				}

				if (errorReader != null) {
					HSSFSheet secondSheet = (HSSFSheet) wb.createSheet("Error Data");
					String errorJsonString = null;
					String line2 = null;
					StringBuilder stringBuilder2 = new StringBuilder();
					String ls2 = System.getProperty("line.separator");

					try {
						while ((line2 = errorReader.readLine()) != null) {
							stringBuilder2.append(line2);
							stringBuilder2.append(ls2);
						}
						errorJsonString = stringBuilder2.toString();
					} finally {
						errorReader.close();
					}

					int rownum2 = 0;
					Row headerRow = secondSheet.createRow(rownum2++);
					headerRow.createCell(0).setCellValue("Field");
					headerRow.createCell(1).setCellValue("Error");
					JSONArray errorJson = (JSONArray) JSONSerializer.toJSON(errorJsonString);
					for (Iterator<JSONObject> itr = errorJson.iterator(); itr.hasNext();) {
						JSONObject error = itr.next();
						Row row = secondSheet.createRow(rownum2++);
						Cell cell1 = row.createCell(0);
						cell1.setCellValue(error.getString("colName"));
						Cell cell2 = row.createCell(1);
						cell2.setCellValue(error.getString("remarks"));
					}
					secondSheet.autoSizeColumn(0);
					secondSheet.autoSizeColumn(1);
				}
				LOGGER.info("WD EMF Import : created Error sheet");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.info("WD EMF Import : Returning excel log");
		return wb;
	}

	@Override
	public Workbook getPtrxImportLog(Long logFileId, Long companyId) {

		Workbook wb = new HSSFWorkbook();
		String fileName = String.valueOf(logFileId) + "_" + WORKDAY_FTP_IMPORT_PTRX + "_" + DATA_LOG_FILE;
		String errorFileName = String.valueOf(logFileId) + "_" + WORKDAY_FTP_IMPORT_PTRX + "_" + ERROR_LOG_FILE;

		// Creating Sheet for Imported (Success) Data
		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.FTP_DOCUMENT_DIRECTORY_NAME, fileName, WORKDAY_FTP_LOG_FOLDER, null, "txt",
				PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		String filePathLocal = downloadPath + File.separator + WORKDAY_FTP_LOG_FOLDER + File.separator + companyId
				+ File.separator + fileName + "." + "txt";

		HSSFSheet firstSheet = (HSSFSheet) wb.createSheet("Imported Data");
		BufferedReader reader = null;
		try {
			String jsonString = null;
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				InputStream fileData = awss3LogicImpl.readS3ObjectAsStream(filePath);
				reader = new BufferedReader(new InputStreamReader(fileData));
			} else {
				reader = new BufferedReader(new FileReader(filePathLocal));
			}
			if (reader != null) {
				String line = null;
				StringBuilder stringBuilder = new StringBuilder();
				String ls = System.getProperty("line.separator");

				try {
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line);
						stringBuilder.append(ls);
					}

					jsonString = stringBuilder.toString();
				} finally {
					reader.close();
				}

				JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonString.replace("pi:", ""));
				JSONObject dataJson = json.getJSONObject("Payroll_Extract_Employees");
				JSONObject paygroupJson = dataJson.getJSONObject("PayGroup");

				int rownum = 0;

				// header
				JSONObject headerJson = paygroupJson.getJSONObject("Header");
				Row row = firstSheet.createRow(rownum++);
				int cellnum = 0;
				Cell cell = row.createCell(cellnum++);
				cell.setCellValue("Header Information:");
				HSSFFont font = (HSSFFont) wb.createFont();
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				CellStyle cellStyle = wb.createCellStyle();
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);

				// for(Iterator<JSONObject> itr =
				// (Iterator<JSONObject>)headerJsonArray.iterator();
				// itr.hasNext();) {
				for (@SuppressWarnings("unchecked")
				Iterator<String> itr = headerJson.keySet().iterator(); itr.hasNext();) {
					String key = itr.next();
					row = firstSheet.createRow(rownum++);
					cellnum = 0;
					cell = row.createCell(cellnum++);
					cell.setCellValue(key);
					int columnIndex = cell.getColumnIndex();
					firstSheet.autoSizeColumn(columnIndex);
					cell = row.createCell(cellnum++);
					cell.setCellValue(headerJson.getString(key));
					columnIndex = cell.getColumnIndex();
					firstSheet.autoSizeColumn(columnIndex);
				}

				// employees
				Object empObject = paygroupJson.get("Employee");
				if (empObject != null && empObject instanceof JSONArray) {
					JSONArray employees = (JSONArray) empObject;
					for (@SuppressWarnings("unchecked")
					Iterator<JSONObject> itr = (Iterator<JSONObject>) employees.iterator(); itr.hasNext();) {
						JSONObject employee = itr.next();
						rownum++;
						row = firstSheet.createRow(rownum++);
						cellnum = 0;
						cell = row.createCell(cellnum++);
						cell.setCellValue("Employee Information:");
						cell.setCellStyle(cellStyle);

						@SuppressWarnings("unchecked")
						Set<String> empKeySet = (Set<String>) employee.keySet();
						for (String empKey : empKeySet) {
							rownum++;
							row = firstSheet.createRow(rownum++);
							cellnum = 0;
							cell = row.createCell(cellnum++);
							cell.setCellValue(empKey);
							if ("Summary".equalsIgnoreCase(empKey)) {
								JSONObject summary = employee.getJSONObject("Summary");
								for (@SuppressWarnings("unchecked")
								Iterator<String> itr2 = summary.keySet().iterator(); itr2.hasNext();) {
									String key = itr2.next();
									row = firstSheet.createRow(rownum++);
									cellnum = 0;
									cell = row.createCell(cellnum++);
									cell.setCellValue(key);
									int columnIndex = cell.getColumnIndex();
									firstSheet.autoSizeColumn(columnIndex);
									cell = row.createCell(cellnum++);
									cell.setCellValue(summary.getString(key));
									columnIndex = cell.getColumnIndex();
									firstSheet.autoSizeColumn(columnIndex);
								}
							} else {
								Object object = employee.get(empKey);
								if (object instanceof JSONArray) {
									JSONArray jsonArray = (JSONArray) object;
									for (int i = 0; i < jsonArray.size(); i++) {
										JSONObject jsonObject = jsonArray.getJSONObject(i);
										for (@SuppressWarnings("unchecked")
										Iterator<String> itr2 = jsonObject.keySet().iterator(); itr2.hasNext();) {
											String key = itr2.next();
											row = firstSheet.createRow(rownum++);
											cellnum = 0;
											cell = row.createCell(cellnum++);
											cell.setCellValue(key);
											int columnIndex = cell.getColumnIndex();
											firstSheet.autoSizeColumn(columnIndex);
											cell = row.createCell(cellnum++);
											cell.setCellValue(jsonObject.getString(key));
											columnIndex = cell.getColumnIndex();
											firstSheet.autoSizeColumn(columnIndex);
										}
									}
								} else if (object instanceof JSONObject) {
									JSONObject jsonObject = (JSONObject) object;
									for (@SuppressWarnings("unchecked")
									Iterator<String> itr2 = jsonObject.keySet().iterator(); itr2.hasNext();) {
										String key = itr2.next();
										row = firstSheet.createRow(rownum++);
										cellnum = 0;
										cell = row.createCell(cellnum++);
										cell.setCellValue(key);
										int columnIndex = cell.getColumnIndex();
										firstSheet.autoSizeColumn(columnIndex);
										cell = row.createCell(cellnum++);
										cell.setCellValue(jsonObject.getString(key));
										columnIndex = cell.getColumnIndex();
										firstSheet.autoSizeColumn(columnIndex);
									}
								}
							}
						}
					}
				} else if (empObject != null && empObject instanceof JSONObject) {
					JSONObject employee = (JSONObject) empObject;
					rownum++;
					row = firstSheet.createRow(rownum++);
					cellnum = 0;
					cell = row.createCell(cellnum++);
					cell.setCellValue("Employee Information:");
					cell.setCellStyle(cellStyle);

					@SuppressWarnings("unchecked")
					Set<String> empKeySet = (Set<String>) employee.keySet();
					for (String empKey : empKeySet) {
						rownum++;
						row = firstSheet.createRow(rownum++);
						cellnum = 0;
						cell = row.createCell(cellnum++);
						cell.setCellValue(empKey);
						if ("Summary".equalsIgnoreCase(empKey)) {
							JSONObject summary = employee.getJSONObject("Summary");
							for (@SuppressWarnings("unchecked")
							Iterator<String> itr2 = summary.keySet().iterator(); itr2.hasNext();) {
								String key = itr2.next();
								row = firstSheet.createRow(rownum++);
								cellnum = 0;
								cell = row.createCell(cellnum++);
								cell.setCellValue(key);
								int columnIndex = cell.getColumnIndex();
								firstSheet.autoSizeColumn(columnIndex);
								cell = row.createCell(cellnum++);
								cell.setCellValue(summary.getString(key));
								columnIndex = cell.getColumnIndex();
								firstSheet.autoSizeColumn(columnIndex);
							}
						} else {
							Object object = employee.get(empKey);
							if (object instanceof JSONArray) {
								JSONArray jsonArray = (JSONArray) object;
								for (int i = 0; i < jsonArray.size(); i++) {
									JSONObject jsonObject = jsonArray.getJSONObject(i);
									for (@SuppressWarnings("unchecked")
									Iterator<String> itr2 = jsonObject.keySet().iterator(); itr2.hasNext();) {
										String key = itr2.next();
										row = firstSheet.createRow(rownum++);
										cellnum = 0;
										cell = row.createCell(cellnum++);
										cell.setCellValue(key);
										int columnIndex = cell.getColumnIndex();
										firstSheet.autoSizeColumn(columnIndex);
										cell = row.createCell(cellnum++);
										cell.setCellValue(jsonObject.getString(key));
										columnIndex = cell.getColumnIndex();
										firstSheet.autoSizeColumn(columnIndex);
									}
								}
							} else if (object instanceof JSONObject) {
								JSONObject jsonObject = (JSONObject) object;
								for (@SuppressWarnings("unchecked")
								Iterator<String> itr2 = jsonObject.keySet().iterator(); itr2.hasNext();) {
									String key = itr2.next();
									row = firstSheet.createRow(rownum++);
									cellnum = 0;
									cell = row.createCell(cellnum++);
									cell.setCellValue(key);
									int columnIndex = cell.getColumnIndex();
									firstSheet.autoSizeColumn(columnIndex);
									cell = row.createCell(cellnum++);
									cell.setCellValue(jsonObject.getString(key));
									columnIndex = cell.getColumnIndex();
									firstSheet.autoSizeColumn(columnIndex);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException("Error while parsing json log", e.getMessage());
		}
		return wb;
	}
}
