/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Resource;
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
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.xml.sax.SAXException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.dto.CustomFieldReportDTO;
import com.payasia.common.dto.DataImportHistoryDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DynamicFormComboDTO;
import com.payasia.common.dto.EmpInfoHeaderTextPayslipDTO;
import com.payasia.common.dto.EntityMasterDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.PayslipConditionDTO;
import com.payasia.common.dto.PayslipDTO;
import com.payasia.common.dto.PayslipFrequencyDTO;
import com.payasia.common.dto.TextPaySlipDTO;
import com.payasia.common.dto.TextPaySlipListDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.common.util.ValidationUtils;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.AnnouncementDAO;
import com.payasia.dao.CompanyAddressMappingDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyDocumentDAO;
import com.payasia.dao.CompanyDocumentDetailDAO;
import com.payasia.dao.CompanyLogoDAO;
import com.payasia.dao.CompanyPayslipReleaseDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DataImportHistoryDAO;
import com.payasia.dao.DataImportLogDAO;
import com.payasia.dao.DocumentCategoryMasterDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmailDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.EmpDataImportTemplateDAO;
import com.payasia.dao.EmpDataImportTemplateFieldDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.PayslipDAO;
import com.payasia.dao.PayslipFrequencyDAO;
import com.payasia.dao.bean.Announcement;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyAddressMapping;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyDocumentDetail;
import com.payasia.dao.bean.CompanyLogo;
import com.payasia.dao.bean.CompanyPayslipRelease;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DataImportHistory;
import com.payasia.dao.bean.DataImportLog;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Email;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.EmpDataImportTemplate;
import com.payasia.dao.bean.EmpDataImportTemplateField;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.Payslip;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.CompanyDataImportLogic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.DataImportLogic;
import com.payasia.logic.EmployeeDataImportLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.PayslipDataImportLogic;

/**
 * The Class DataImportLogicImpl.
 */
@Component
public class DataImportLogicImpl implements DataImportLogic {

	/** The data import utils. */

	/** The employee data import logic. */
	@Resource
	EmployeeDataImportLogic employeeDataImportLogic;
	@Resource
	CompanyDocumentDetailDAO companyDocumentDetailDAO;
	/** The company data import logic. */
	@Resource
	CompanyDataImportLogic companyDataImportLogic;
	@Resource
	CompanyDocumentDAO companyDocumentDAO;
	/** The payslip data import logic. */
	@Resource
	PayslipDataImportLogic payslipDataImportLogic;

	/** The emp data import template dao. */
	@Resource
	EmpDataImportTemplateDAO empDataImportTemplateDAO;
	@Resource
	DocumentCategoryMasterDAO documentCategoryMasterDAO;
	/** The emp data import template field dao. */
	@Resource
	EmpDataImportTemplateFieldDAO empDataImportTemplateFieldDAO;

	/** The data import history dao. */
	@Resource
	DataImportHistoryDAO dataImportHistoryDAO;

	/** The data import log dao. */
	@Resource
	DataImportLogDAO dataImportLogDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The payslip frequency dao. */
	@Resource
	PayslipFrequencyDAO payslipFrequencyDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The general dao. */
	@Resource
	GeneralDAO generalDAO;
	@Resource
	CompanyPayslipReleaseDAO companyPayslipReleaseDAO;
	@Resource
	MonthMasterDAO monthMasterDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	CompanyLogoDAO companyLogoDAO;

	@Resource
	com.payasia.logic.PayAsiaTextPaySlipToPdf payAsiaTextPaySlipToPdf;

	@Resource
	AnnouncementDAO announcementDAO;
	@Resource
	MessageSource messageSource;

	@Resource
	GeneralLogic generalLogic;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	DataExportUtils dataExportUtils;
	@Resource
	CompanyAddressMappingDAO companyAddressMappingDAO;

	@Resource
	PayslipDAO payslipDAO;
	@Resource
	GeneralMailLogic generalMailLogic;
	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;
	@Resource
	EmailTemplateDAO emailTemplateDAO;
	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;
	@Autowired
	private VelocityEngine velocityEngine;
	@Resource
	EmailDAO emailDAO;

	@Resource
	FileUtils fileUtils;

	@Resource
	AWSS3Logic awss3LogicImpl;

	private static final Logger LOGGER = Logger
			.getLogger(DataImportLogicImpl.class);
	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.document.path.separator']}")
	private String docPathSeperator;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;

	/** The file name seperator. */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.DataImportLogic#importFile(com.payasia.common.form.
	 * DataImportForm, java.lang.Long)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public DataImportForm importFile(DataImportForm dataImportForm,
			Long companyId, DataImportForm readData) {

		Company company = companyDAO.findByIdDetached(companyId);

		boolean isTemplateValid = true;

		List<DataImportLogDTO> dataImportLogList = new ArrayList<DataImportLogDTO>();
		List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
		List<HashMap<String, String>> inValidData = new ArrayList<HashMap<String, String>>();
		List<HashMap<String, DataImportKeyValueDTO>> keyValMapList = new ArrayList<HashMap<String, DataImportKeyValueDTO>>();

		HashMap<String, ColumnPropertyDTO> staticColPropertyMap = new HashMap<>();
		HashMap<Long, Integer> dynamicFormVersions = new HashMap<>();

		HashMap<String, EmpDataImportTemplateField> colMap = new HashMap<String, EmpDataImportTemplateField>();

		isTemplateValid = isTemplateValid
				&& processDuplicateColumns(dataImportForm, readData,
						dataImportLogList);

		importedData.addAll(readData.getImportedData());

		EmpDataImportTemplate empDataImportTemplate = empDataImportTemplateDAO
				.findById(dataImportForm.getTemplateId());

		isTemplateValid = isTemplateValid
				&& isTemplateValid(dataImportForm, dataImportLogList, colMap,
						readData, empDataImportTemplate);

		DataImportForm importForm = new DataImportForm();
		importForm.setDataImportLogList(dataImportLogList);
		importForm.setTemplateValid(isTemplateValid);

		if (!isTemplateValid) {
			return importForm;
		}

		HashMap<Long, HashMap<String, DynamicFormComboDTO>> newDropDownValues = new HashMap<>();
		HashMap<Long, Tab> dynamicFormObjects = new HashMap<>();
		List<DataImportLogDTO> finalLogs = preProcessFieldData(company,
				importedData, inValidData, keyValMapList, dynamicFormObjects,
				staticColPropertyMap, dynamicFormVersions, colMap,
				newDropDownValues);

		dataImportLogList.addAll(finalLogs);

		if (empDataImportTemplate.getTransaction_Type().equals(
				PayAsiaConstants.ROLLBACK_ON_ERROR)) {
			importForm = preprocessRollBackOnError(dataImportForm, company,
					dataImportLogList, inValidData, empDataImportTemplate,
					isTemplateValid);

			if (importForm != null) {
				return importForm;
			}
		}

		List<DataImportLogDTO> transactionLogs = new ArrayList<DataImportLogDTO>();

		String entityName = empDataImportTemplate.getEntityMaster()
				.getEntityName().toLowerCase();
		if (PayAsiaConstants.EMPLOYEE_ENTITY_NAME.equalsIgnoreCase(entityName)) {
			entityName = PayAsiaConstants.EMPLOYEE_ENTITY_NAME;
		} else if (PayAsiaConstants.COMPANY_ENTITY_NAME
				.equalsIgnoreCase(entityName)) {
			entityName = PayAsiaConstants.COMPANY_ENTITY_NAME;
		} else {
			entityName = PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME;
		}

		try {
			switch (entityName) {
			case PayAsiaConstants.EMPLOYEE_ENTITY_NAME:
				employeeDataImportLogic.saveValidDataFile(keyValMapList,
						dataImportForm.getTemplateId(), empDataImportTemplate
								.getEntityMaster().getEntityId(), colMap,
						empDataImportTemplate.getUpload_Type(),
						empDataImportTemplate.getTransaction_Type(), companyId,
						dynamicFormObjects, dynamicFormVersions);
				break;

			case PayAsiaConstants.COMPANY_ENTITY_NAME:

				companyDataImportLogic.saveValidDataFile(

				keyValMapList, dataImportForm.getTemplateId(),
						empDataImportTemplate.getEntityMaster().getEntityId(),
						colMap, empDataImportTemplate.getUpload_Type(),
						empDataImportTemplate.getTransaction_Type(), companyId,
						dynamicFormObjects, dynamicFormVersions);
				break;

			case PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME:
				payslipDataImportLogic.saveValidDataFile(keyValMapList,
						dataImportForm, empDataImportTemplate.getEntityMaster()
								.getEntityId(), colMap, empDataImportTemplate
								.getUpload_Type(), empDataImportTemplate
								.getTransaction_Type(), companyId,
						dynamicFormObjects, dynamicFormVersions);
				break;
			}

			if (!newDropDownValues.isEmpty()) {
				importDropDownComboValues(newDropDownValues,
						empDataImportTemplate.getEntityMaster().getEntityId(),
						company.getCompanyId());
			}

		} catch (PayAsiaDataException ex) {
			LOGGER.error(ex.getMessage(), ex);
			transactionLogs.addAll(ex.getErrors());
			dataImportLogList.addAll(transactionLogs);
			throw new PayAsiaRollBackDataException(dataImportLogList);
		}

		dataImportLogList.addAll(transactionLogs);
		saveImportHistory(dataImportForm, company, dataImportLogList,
				empDataImportTemplate);

		if (importForm == null) {
			importForm = new DataImportForm();
		}

		importForm.setDataImportLogList(dataImportLogList);
		importForm.setTemplateValid(isTemplateValid);
		if (entityName
				.equalsIgnoreCase(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {
			saveCompanyPayslipRelease(dataImportForm, companyId);

			List<String> employeeNumberList = dataImportForm
					.getEmployeeNumberList();
			sendPayslipReleaseMail(dataImportForm, companyId,
					employeeNumberList,
					PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_EXCEL);
		}
		return importForm;

	}

	private void sendPayslipReleaseMail(DataImportForm dataImportForm,
			Long companyId, List<String> employeeNumberList,
			String payslipSource) {
		boolean releaseMail = false;
		List<PayslipDTO> payslipDTOList = new ArrayList<PayslipDTO>();
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO
				.findByCompanyId(companyId);
		if (hrisPreferenceVO != null
				&& hrisPreferenceVO.isSendPayslipReleaseMail()) {
			releaseMail = true;
		}

		if (dataImportForm.isReleasePayslipWithImport() && releaseMail) {
			if (payslipSource
					.equalsIgnoreCase(PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_EXCEL)) {
				PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
				payslipConditionDTO.setCompanyId(companyId);
				payslipConditionDTO.setMonthMasterId(dataImportForm
						.getMonthId());
				payslipConditionDTO.setYear(dataImportForm.getYear());
				payslipConditionDTO.setPart(dataImportForm.getPart());
				payslipConditionDTO.setEmployeeNumberList(employeeNumberList);
				List<Payslip> payslipVOList = payslipDAO
						.getReleasedPayslipDetails(payslipConditionDTO,
								companyId);
				for (Payslip payslipVO : payslipVOList) {
					PayslipDTO payslipDTO = new PayslipDTO();
					payslipDTO.setYear(payslipVO.getYear());
					payslipDTO.setMonthName(payslipVO.getMonthMaster()
							.getMonthAbbr());
					payslipDTO.setPart(payslipVO.getPart());
					payslipDTO.setEmployeeId(payslipVO.getEmployee()
							.getEmployeeId());
					payslipDTO.setEmployeeNumber(payslipVO.getEmployee()
							.getEmployeeNumber());
					payslipDTO.setEmail(payslipVO.getEmployee().getEmail());
					payslipDTO.setFirstName(payslipVO.getEmployee()
							.getFirstName());
					payslipDTO.setLastName(payslipVO.getEmployee()
							.getLastName());
					payslipDTO.setMiddleName(payslipVO.getEmployee()
							.getMiddleName());
					payslipDTO.setLoginName(payslipVO.getEmployee()
							.getEmployeeLoginDetail().getLoginName());
					payslipDTO.setCompanyName(payslipVO.getEmployee()
							.getCompany().getCompanyName());
					payslipDTOList.add(payslipDTO);
				}
			}
			if (payslipSource
					.equalsIgnoreCase(PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT)
					|| payslipSource
							.equalsIgnoreCase(PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF)) {
				MonthMaster monthMasterVO = monthMasterDAO
						.findById(dataImportForm.getMonthId());
				DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
						.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
				List<CompanyDocument> companyDocumentVOList = companyDocumentDAO
						.findByConditionSourceTextAndDesc(companyId,
								categoryMaster.getDocumentCategoryId(),
								dataImportForm.getYear(),
								dataImportForm.getPart(),
								dataImportForm.getMonthId(), payslipSource);

				List<Employee> employeeVOList;
				Map<String, Employee> employeeMap = new HashMap<String, Employee>();
				if (!companyDocumentVOList.isEmpty()) {
					employeeVOList = employeeDAO.getEmployeeListByEmpNum(
							companyId, employeeNumberList);
					for (Employee employeeVO : employeeVOList) {
						employeeMap.put(employeeVO.getEmployeeNumber(),
								employeeVO);
					}
				}
				for (CompanyDocument companyDocument : companyDocumentVOList) {
					PayslipDTO payslipDTO = new PayslipDTO();
					payslipDTO.setYear(companyDocument.getYear());
					payslipDTO.setMonthName(monthMasterVO.getMonthAbbr());
					payslipDTO.setPart(companyDocument.getPart());
					payslipDTO.setEmployeeNumber(companyDocument
							.getDescription().substring(
									0,
									companyDocument.getDescription().indexOf(
											'_')));
					Employee employeeVO = employeeMap.get(companyDocument
							.getDescription().substring(
									0,
									companyDocument.getDescription().indexOf(
											'_')));
					if (employeeVO != null) {
						payslipDTO.setEmployeeId(employeeVO.getEmployeeId());
						payslipDTO.setEmployeeNumber(employeeVO
								.getEmployeeNumber());
						payslipDTO.setEmail(employeeVO.getEmail());
						payslipDTO.setFirstName(employeeVO.getFirstName());
						payslipDTO.setLastName(employeeVO.getLastName());
						payslipDTO.setMiddleName(employeeVO.getMiddleName());
						payslipDTO.setLoginName(employeeVO
								.getEmployeeLoginDetail().getLoginName());
						payslipDTO.setCompanyName(employeeVO.getCompany()
								.getCompanyName());
						payslipDTOList.add(payslipDTO);
					}
				}
			}
			savePayslipReleaseEmail(companyId, payslipDTOList);
		}
	}

	@Override
	public void savePayslipReleaseEmail(Long companyId,
			List<PayslipDTO> payslipDTOList) {
		Company companyVO = companyDAO.findById(companyId);
		List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();
		Long subCategoryId = generalMailLogic.getSubCategoryId(
				PayAsiaConstants.PAYASIA_SUB_CATEGORY_PAYSLIP_RELEASE,
				EmailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO
				.findByConditionSubCategoryAndCompId(
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_PAYSLIP_RELEASE,
						subCategoryId, companyId);
		if (emailTemplateVO == null) {
			// return "send.password.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			// return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			// return "email.configuration.contact.email.is.not.defined";
		}

		for (PayslipDTO payslipDTO : payslipDTOList) {
			if (StringUtils.isNotBlank(payslipDTO.getEmail())) {
				String mailBody = emailTemplateVO.getBody();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put("firstName", payslipDTO.getFirstName());
				modelMap.put("First_Name", payslipDTO.getFirstName());
				modelMap.put("employeeId", payslipDTO.getEmployeeNumber());
				modelMap.put("Employee_Number", payslipDTO.getEmployeeNumber());

				modelMap.put("userName", payslipDTO.getLoginName());
				modelMap.put("Username", payslipDTO.getLoginName());
				modelMap.put("companyName", payslipDTO.getCompanyName());
				modelMap.put("Company_Name", payslipDTO.getCompanyName());

				modelMap.put("Month", payslipDTO.getMonthName());
				modelMap.put("Year", payslipDTO.getYear());
				modelMap.put("Part", payslipDTO.getPart());
				if (StringUtils.isNotBlank(payslipDTO.getMiddleName())) {
					modelMap.put("middleName", payslipDTO.getMiddleName());
					modelMap.put("Middle_Name", payslipDTO.getMiddleName());
				}
				if (!StringUtils.isNotBlank(payslipDTO.getMiddleName())) {
					modelMap.put("middleName", "");
					modelMap.put("Middle_Name", "");
				}
				if (StringUtils.isNotBlank(payslipDTO.getLastName())) {
					modelMap.put("lastName", payslipDTO.getLastName());
					modelMap.put("Last_Name", payslipDTO.getLastName());
				}
				if (!StringUtils.isNotBlank(payslipDTO.getLastName())) {
					modelMap.put("lastName", "");
					modelMap.put("Last_Name", "");
				}
				if (StringUtils.isNotBlank(payslipDTO.getEmail())) {
					modelMap.put("email", payslipDTO.getEmail());
					modelMap.put("Email", payslipDTO.getEmail());
				}
				if (!StringUtils.isNotBlank(payslipDTO.getEmail())) {
					modelMap.put("email", "");
					modelMap.put("Email", "");
				}

				if (emailPreferenceMasterVO.getCompanyUrl() != null) {
					String link = "<a href='"
							+ emailPreferenceMasterVO.getCompanyUrl() + "'>"
							+ emailPreferenceMasterVO.getCompanyUrl() + "</a>";
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);

				} else {
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
				}
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME,
						getEmployeeName(payslipDTO));
				File emailTemplate = null;
				FileOutputStream fos = null;
				File emailSubjectTemplate = null;
				FileOutputStream fosSubject = null;
				Email email = new Email();
				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = emailTemplateVO.getSubject()
							.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator
							.getNDigitRandomNumber(8);
					emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendReleasePayslipMailTemplate"
							+ payslipDTO.getEmployeeId() + "_" + uniqueId
							+ ".vm");
					emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendReleasePayslipMailTemplateSubject"
							+ payslipDTO.getEmployeeId() + "_" + uniqueId
							+ ".vm");
					emailSubjectTemplate.deleteOnExit();
					emailTemplate.deleteOnExit();

					try {
						fos = new FileOutputStream(emailTemplate);
						fos.write(mailBodyBytes);
						fos.flush();
						fos.close();
						fosSubject = new FileOutputStream(emailSubjectTemplate);
						fosSubject.write(mailSubjectBytes);
						fosSubject.flush();
						fosSubject.close();
					} catch (FileNotFoundException ex) {
						LOGGER.error(ex.getMessage(), ex);
					} catch (IOException ex) {
						LOGGER.error(ex.getMessage(), ex);
					}

					String templateBodyString = emailTemplate.getPath()
							.substring(emailTemplate.getParent().length() + 1);
					String templateSubjectString = emailSubjectTemplate
							.getPath()
							.substring(
									emailSubjectTemplate.getParent().length() + 1);

					StringBuilder subjectText = new StringBuilder("");
					subjectText.append(VelocityEngineUtils
							.mergeTemplateIntoString(velocityEngine,
									templateSubjectString, modelMap));

					StringBuilder bodyText = new StringBuilder("");
					bodyText.append(VelocityEngineUtils
							.mergeTemplateIntoString(velocityEngine,
									templateBodyString, modelMap));

					email.setBody(bodyText.toString());
					email.setSubject(subjectText.toString());
					email.setCompany(companyVO);
					email.setEmailFrom(emailPreferenceMasterVO
							.getContactEmail());
					email.setCreatedDate(DateUtils
							.getCurrentTimestampWithTime());
					if (StringUtils.isNotBlank(payslipDTO.getEmail())) {
						email.setEmailTo(payslipDTO.getEmail());
						emailDAO.saveReturn(email);
					}

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				} finally {
					if (emailTemplate != null) {
						emailTemplate.delete();

					}
				}
			}
		}
	}

	private String getEmployeeName(PayslipDTO payslipDTO) {
		String employeeName = payslipDTO.getFirstName();

		if (StringUtils.isNotBlank(payslipDTO.getMiddleName())) {
			employeeName = employeeName + " " + payslipDTO.getMiddleName();
		}
		if (StringUtils.isNotBlank(payslipDTO.getLastName())) {
			employeeName = employeeName + " " + payslipDTO.getLastName();
		}
		return employeeName;
	}

	private void saveCompanyPayslipRelease(DataImportForm dataImportForm,
			Long companyId) {
		CompanyPayslipRelease companyPayslipReleaseVO = companyPayslipReleaseDAO
				.findByCondition(null, dataImportForm.getMonthId(),
						dataImportForm.getYear(), dataImportForm.getPart(),
						companyId);
		Company company = new Company();
		company.setCompanyId(companyId);
		MonthMaster monthMaster = null;

		if (dataImportForm.isReleasePayslipWithImport()) {

			Boolean isPayslipReleased = false;
			if (companyPayslipReleaseVO == null) {
				CompanyPayslipRelease payslipRelease = new CompanyPayslipRelease();
				payslipRelease.setCompany(company);
				monthMaster = monthMasterDAO.findById(dataImportForm
						.getMonthId());
				payslipRelease.setName(monthMaster.getMonthName() + " "
						+ dataImportForm.getYear());
				payslipRelease.setMonthMaster(monthMaster);
				payslipRelease.setReleased(true);
				payslipRelease.setSendEmail(true);
				isPayslipReleased = true;
				payslipRelease.setPart(dataImportForm.getPart());
				payslipRelease.setYear(dataImportForm.getYear());
				if(StringUtils.isNotBlank(dataImportForm.getScheduleDate())){
					
					String str=dataImportForm.getScheduleDate()+" "+dataImportForm.getScheduleTime();
					try {
						Company payslipCompany = companyDAO.findById(companyId);
						payslipRelease.setReleaseDateTime(DateUtils.stringToTimestampDate(str,payslipCompany.getDateFormat(),payslipCompany.getTimeZoneMaster().getGmtOffset()));
					} catch (Exception ex) {
						LOGGER.error(ex.getMessage(), ex);
					}
				}
				else{
					payslipRelease.setReleaseDateTime(null);
				}
				payslipRelease.setEmailTo(hrisPreferenceDAO.findByCompanyId(companyId).getPaySlipDefaultEmailTo());
				companyPayslipReleaseDAO.save(payslipRelease);
			} else {
				isPayslipReleased = true;
				companyPayslipReleaseVO.setReleased(true);
				companyPayslipReleaseVO.setSendEmail(true);
				companyPayslipReleaseVO.setReleaseDateTime(null);
				companyPayslipReleaseDAO.update(companyPayslipReleaseVO);
			}

			if (isPayslipReleased) {
				boolean isFuturePaySlipReleased = companyPayslipReleaseDAO
						.isFuturePaySlipReleased(dataImportForm.getMonthId(),
								dataImportForm.getYear(), companyId,
								dataImportForm.getPart());
				if (!isFuturePaySlipReleased) {

					announcementDAO.updatePayslipEndate(companyId);

					Announcement announcement = new Announcement();

					announcement.setCompany(company);
					announcement.setCompanyGroup(company.getCompanyGroup());
					announcement
							.setTitle(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);

					if (monthMaster == null) {
						monthMaster = companyPayslipReleaseVO.getMonthMaster();
					}
					announcement
							.setDescription(PayAsiaConstants.PAYASIA_PAYSLIP_ANNOUNCEMENT_START_TEXT
									+ monthMaster.getMonthName()
									+ " "
									+ dataImportForm.getYear()
									+ PayAsiaConstants.PAYASIA_PAYSLIP_ANNOUNCEMENT_END_TEXT);
					announcement.setScope("C");
					announcement.setPostDateTime(DateUtils
							.getCurrentTimestamp());
					announcementDAO.save(announcement);
				}
			}

		} else {
			Company payslipCompany = companyDAO.findById(companyId);
			if (companyPayslipReleaseVO == null) {
				CompanyPayslipRelease payslipRelease = new CompanyPayslipRelease();
				payslipRelease.setCompany(company);
				monthMaster = monthMasterDAO.findById(dataImportForm
						.getMonthId());
				payslipRelease.setName(monthMaster.getMonthName() + " "
						+ dataImportForm.getYear());
				payslipRelease.setMonthMaster(monthMaster);
				payslipRelease.setReleased(false);
				payslipRelease.setSendEmail(false);
				payslipRelease.setPart(dataImportForm.getPart());
				payslipRelease.setYear(dataImportForm.getYear());
				if(StringUtils.isNotBlank(dataImportForm.getScheduleDate())) {
					String str = dataImportForm.getScheduleDate() + " " + dataImportForm.getScheduleTime();
					try {
						payslipRelease.setReleaseDateTime(DateUtils.stringToTimestampDate(str,
								payslipCompany.getDateFormat(), payslipCompany.getTimeZoneMaster().getGmtOffset()));
					} catch (Exception ex) {
						LOGGER.error(ex.getMessage(), ex);
					}
				}
				payslipRelease.setEmailTo(hrisPreferenceDAO.findByCompanyId(companyId).getPaySlipDefaultEmailTo());
				companyPayslipReleaseDAO.save(payslipRelease);
			} else {
				companyPayslipReleaseVO.setReleased(false);
				if(StringUtils.isNotBlank(dataImportForm.getScheduleDate())) {
					String str = dataImportForm.getScheduleDate() + " " + dataImportForm.getScheduleTime();
					try {
						companyPayslipReleaseVO.setReleaseDateTime(DateUtils.stringToTimestampDate(str,
								payslipCompany.getDateFormat(), payslipCompany.getTimeZoneMaster().getGmtOffset()));
						
					}catch (Exception ex) {
						LOGGER.error(ex.getMessage(), ex);
					}
				}
				companyPayslipReleaseVO
				.setEmailTo(hrisPreferenceDAO.findByCompanyId(companyId).getPaySlipDefaultEmailTo());
				companyPayslipReleaseDAO.update(companyPayslipReleaseVO);
			}
		}
	}

	private void importDropDownComboValues(
			HashMap<Long, HashMap<String, DynamicFormComboDTO>> newDropDownValues,
			Long entityId, Long companyId) {

		synchronized (this) {
			for (Entry<Long, HashMap<String, DynamicFormComboDTO>> entry : newDropDownValues
					.entrySet()) {

				Long formId = entry.getKey();
				Integer maxversion = dynamicFormDAO.getMaxVersionByFormId(
						companyId, entityId, formId);

				DynamicForm dynamicForm = dynamicFormDAO
						.findByMaxVersionByFormId(companyId, entityId,
								maxversion, formId);

				String updatedXML = updateDrowDownValues(dynamicForm,
						entry.getValue());
				dynamicForm.setMetaData(updatedXML);
				dynamicFormDAO.update(dynamicForm);

			}
		}

	}

	private String updateDrowDownValues(DynamicForm dynamicForm,
			HashMap<String, DynamicFormComboDTO> dynamicFormComboDTOVal) {

		Unmarshaller unmarshaller = null;
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
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
			throw new PayAsiaSystemException(jaxbException.getMessage(),
					jaxbException);
		} catch (SAXException saxException) {
			LOGGER.error(saxException.getMessage(), saxException);
			throw new PayAsiaSystemException(saxException.getMessage(),
					saxException);
		}

		final StringReader xmlReader = new StringReader(
				dynamicForm.getMetaData());
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jaxbException) {
			LOGGER.error(jaxbException.getMessage(), jaxbException);
			throw new PayAsiaSystemException(jaxbException.getMessage(),
					jaxbException);
		}

		List<Field> listOfFields = tab.getField();

		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.LABEL_FIELD_TYPE)) {
				if (PayAsiaConstants.FIELD_TYPE_COMBO.equals(field.getType())) {

					if (dynamicFormComboDTOVal.get(field.getName()) != null
							&& dynamicFormComboDTOVal.get(field.getName())
									.getCustFieldName().equals(field.getName())) {
						Set<String> options = dynamicFormComboDTOVal.get(
								field.getName()).getDropDownValues();

						field.getOption().addAll(options);
					}

				}
			}

			if (StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {
				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {

					if (PayAsiaConstants.FIELD_TYPE_DROPDOWN.equals(column
							.getType())) {
						if (dynamicFormComboDTOVal.get(field.getName()
								+ column.getName()) != null
								&& dynamicFormComboDTOVal
										.get(field.getName() + column.getName())
										.getCustFieldName()
										.equals(column.getName())) {
							Set<String> options = dynamicFormComboDTOVal.get(
									field.getName() + column.getName())
									.getDropDownValues();

							column.getOption().addAll(options);
						}

					}
				}
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

	private void saveImportHistory(DataImportForm dataImportForm,
			Company company, List<DataImportLogDTO> dataImportLogList,
			EmpDataImportTemplate empDataImportTemplate) {
		DataImportHistory dataImportHistory = new DataImportHistory();
		dataImportHistory.setFileName(dataImportForm.getFileName()
				.getOriginalFilename());
		dataImportHistory.setCompany(company);
		dataImportHistory.setEntityMaster(empDataImportTemplate
				.getEntityMaster());
		Date date = new Date();
		Timestamp timeStampDate = new Timestamp(date.getTime());
		dataImportHistory.setImportDate(timeStampDate);

		if (!dataImportLogList.isEmpty()) {

			dataImportHistory
					.setStatus(PayAsiaConstants.IMPORT_HISTORY_STATUS_FAILURE);
			DataImportHistory importHistory = dataImportHistoryDAO
					.saveHistory(dataImportHistory);

			for (DataImportLogDTO dataImportLogDTO : dataImportLogList) {
				DataImportLog dataImportLog = new DataImportLog();
				dataImportLog.setColumnName(dataImportLogDTO.getColName());
				dataImportLog.setDataImportHistory(importHistory);
				if (dataImportLogDTO.getFailureType().equalsIgnoreCase(
						"payasia.data.import.validation")) {
					dataImportLog
							.setFailureType(PayAsiaConstants.FAILTURE_TYPE_VALIDATION);
				} else {
					dataImportLog
							.setFailureType(PayAsiaConstants.FAILTURE_TYPE_TRANSACTION);
				}

				dataImportLog.setRemarks(dataImportLogDTO.getRemarks());
				dataImportLog.setRowNumber(dataImportLogDTO.getRowNumber());
				dataImportLogDAO.saveLog(dataImportLog);
			}

		} else {
			dataImportHistory
					.setStatus(PayAsiaConstants.IMPORT_HISTORY_STATUS_COMPLETED);
			dataImportHistoryDAO.saveHistory(dataImportHistory);
		}
	}

	private List<DataImportLogDTO> preProcessFieldData(
			Company company,
			List<HashMap<String, String>> importedData,
			List<HashMap<String, String>> inValidData,
			List<HashMap<String, DataImportKeyValueDTO>> keyValMapList,
			HashMap<Long, Tab> dynamicFormObjects,
			HashMap<String, ColumnPropertyDTO> staticColPropertyMap,
			HashMap<Long, Integer> dynamicFormVersions,
			HashMap<String, EmpDataImportTemplateField> colMap,
			HashMap<Long, HashMap<String, DynamicFormComboDTO>> newDropDownValues) {
		List<DataImportLogDTO> finalLogs = new ArrayList<DataImportLogDTO>();

		for (HashMap<String, String> map : importedData) {
			Set<String> keySet = map.keySet();
			String rowNumber = map.get("rowNumber");
			HashMap<String, DataImportKeyValueDTO> keyValMap = new HashMap<String, DataImportKeyValueDTO>();
			DataImportKeyValueDTO importKeyValueDTO = new DataImportKeyValueDTO();
			importKeyValueDTO.setValue(rowNumber);
			keyValMap.put("rowNumber", importKeyValueDTO);
			HashMap<String, Boolean> checkMap = new HashMap<String, Boolean>();

			validateFieldData(company, dynamicFormObjects, dynamicFormVersions,
					colMap, finalLogs, map, keySet, rowNumber, keyValMap,
					checkMap, staticColPropertyMap, newDropDownValues);

			Collection<Boolean> checkSet = checkMap.values();
			if (!checkSet.contains(false)) {
				keyValMapList.add(keyValMap);
			} else {
				inValidData.add(map);
			}

		}
		return finalLogs;
	}

	private boolean processDuplicateColumns(DataImportForm dataImportForm,
			DataImportForm readData, List<DataImportLogDTO> dataImportLogList) {
		boolean isTemplateValid = true;
		if (readData.isSameExcelField()
				&& readData.getDuplicateColNames().size() > 0) {
			for (String column : readData.getDuplicateColNames()) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setColName(column);
				dataImportLogDTO
						.setRemarks("payasia.data.import.duplicate.column.name.error");
				isTemplateValid = false;
				dataImportLogDTO
						.setFailureType("payasia.data.import.validation");
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO.setRowNumber(1);
				dataImportForm.setSameExcelField(true);
				dataImportLogList.add(dataImportLogDTO);
			}

		}
		return isTemplateValid;
	}

	private DataImportForm preprocessRollBackOnError(
			DataImportForm dataImportForm, Company company,
			List<DataImportLogDTO> dataImportLogList,
			List<HashMap<String, String>> inValidData,
			EmpDataImportTemplate empDataImportTemplate, boolean isTemplateValid) {
		if (!inValidData.isEmpty()) {

			DataImportHistory dataImportHistory = new DataImportHistory();
			dataImportHistory.setFileName(dataImportForm.getFileName()
					.getOriginalFilename());
			dataImportHistory.setCompany(company);
			dataImportHistory.setEntityMaster(empDataImportTemplate
					.getEntityMaster());
			Date date = new Date();
			Timestamp timeStampDate = new Timestamp(date.getTime());
			dataImportHistory.setImportDate(timeStampDate);
			dataImportHistory
					.setStatus(PayAsiaConstants.IMPORT_HISTORY_STATUS_FAILURE);
			DataImportHistory importHistory = dataImportHistoryDAO
					.saveHistory(dataImportHistory);
			for (DataImportLogDTO dataImportLogDTO : dataImportLogList) {
				DataImportLog dataImportLog = new DataImportLog();
				dataImportLog.setColumnName(dataImportLogDTO.getColName());
				dataImportLog.setDataImportHistory(importHistory);
				if (dataImportLogDTO.getFailureType().equalsIgnoreCase(
						"payasia.data.import.validation")) {
					dataImportLog
							.setFailureType(PayAsiaConstants.FAILTURE_TYPE_VALIDATION);
				} else {
					dataImportLog
							.setFailureType(PayAsiaConstants.FAILTURE_TYPE_TRANSACTION);
				}

				dataImportLog.setRemarks(dataImportLogDTO.getRemarks());
				dataImportLog.setRowNumber(dataImportLogDTO.getRowNumber());
				dataImportLogDAO.saveLog(dataImportLog);
			}

			DataImportForm importForm = new DataImportForm();
			importForm.setDataImportLogList(dataImportLogList);
			importForm.setTemplateValid(isTemplateValid);

			return importForm;

		}
		return null;
	}

	private void validateFieldData(
			Company company,
			HashMap<Long, Tab> dynamicFormObjects,
			HashMap<Long, Integer> dynamicFormVersions,
			HashMap<String, EmpDataImportTemplateField> colMap,
			List<DataImportLogDTO> finalLogs,
			HashMap<String, String> map,
			Set<String> keySet,
			String rowNumber,
			HashMap<String, DataImportKeyValueDTO> keyValMap,
			HashMap<String, Boolean> checkMap,
			HashMap<String, ColumnPropertyDTO> staticColPropertyMap,
			HashMap<Long, HashMap<String, DynamicFormComboDTO>> newDropDownValues) {
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			DataImportKeyValueDTO dataImportKeyValueDTO;
			String key = (String) iterator.next();
			String value = (String) map.get(key);
			if (key != "rowNumber") {
				dataImportKeyValueDTO = validateFieldData(colMap, key, value,
						company, rowNumber, dynamicFormObjects,
						dynamicFormVersions, staticColPropertyMap,
						newDropDownValues);
				if (dataImportKeyValueDTO.getDataImportLogDTO() != null) {
					finalLogs.add(dataImportKeyValueDTO.getDataImportLogDTO());
				}

				if (!dataImportKeyValueDTO.isNonTemplateVal()) {
					keyValMap.put(key, dataImportKeyValueDTO);
					checkMap.put(key, dataImportKeyValueDTO.isFieldValid());
				}

			}
		}
	}

	private boolean isTemplateValid(DataImportForm dataImportForm,
			List<DataImportLogDTO> dataImportLogList,
			HashMap<String, EmpDataImportTemplateField> colMap,
			DataImportForm readData, EmpDataImportTemplate empDataImportTemplate) {

		boolean isTemplateValid = true;

		for (String column : readData.getColName()) {
			List<EmpDataImportTemplateField> empDataImportTemplateField = empDataImportTemplateFieldDAO
					.findByExcelField(column, dataImportForm.getTemplateId());

			if (empDataImportTemplateField != null
					&& empDataImportTemplateField.size() == 1) {
				colMap.put(column, empDataImportTemplateField.get(0));
			}

			if (empDataImportTemplateField != null
					&& empDataImportTemplateField.size() > 1) {
				LOGGER.error("Field value : " + column);
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setColName(column);
				dataImportLogDTO
						.setRemarks("payasia.data.import.duplicate.column.name.error");
				isTemplateValid = false;
				dataImportLogDTO
						.setFailureType("payasia.data.import.validation");
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO.setRowNumber(1);

				dataImportLogList.add(dataImportLogDTO);
			}

		}
		if (empDataImportTemplate.getEmpDataImportTemplateFields() != null) {
			for (EmpDataImportTemplateField dataImportTemplateField : empDataImportTemplate
					.getEmpDataImportTemplateFields()) {
				if (!readData.getColName().contains(
						dataImportTemplateField.getExcelFieldName())) {
					isTemplateValid = false;
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO.setColName(dataImportTemplateField
							.getExcelFieldName());
					dataImportLogDTO
							.setRemarks("payasia.data.import.no.column.error");
					dataImportLogDTO
							.setFailureType("payasia.data.import.validation");
					dataImportLogDTO.setFromMessageSource(true);
					dataImportLogDTO.setRowNumber(1);
					isTemplateValid = false;
					dataImportLogList.add(dataImportLogDTO);
				}
			}
		}
		return isTemplateValid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataImportLogic#getTemplateList(long,
	 * java.lang.Long)
	 */
	@Override
	public List<ExcelImportToolForm> getTemplateList(long entityId,
			Long companyId) {
		List<EmpDataImportTemplate> exportTemplateList = empDataImportTemplateDAO
				.findByEntity(entityId, companyId);
		List<ExcelImportToolForm> excelImportToolFormList = new ArrayList<ExcelImportToolForm>();
		if (exportTemplateList != null) {
			for (EmpDataImportTemplate empDataImportTemplate : exportTemplateList) {

				ExcelImportToolForm excelImportToolForm = new ExcelImportToolForm();
				try {
					excelImportToolForm.setTemplateName(URLEncoder.encode(
							empDataImportTemplate.getTemplateName(), "UTF-8"));
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
				excelImportToolForm.setTemplateId(empDataImportTemplate
						.getImportTemplateId());
				excelImportToolFormList.add(excelImportToolForm);
			}
		}

		return excelImportToolFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataImportLogic#getImportHistory(java.lang.Long)
	 */
	@Override
	public DataImportForm getImportHistory(Long companyId) {

		DataImportForm dataImportForm = new DataImportForm();
		List<DataImportHistory> dataImportHistoryList = dataImportHistoryDAO
				.findAll(companyId);

		List<DataImportHistoryDTO> dataImportHistoryDTOList = new ArrayList<DataImportHistoryDTO>();

		for (DataImportHistory dataImportHistory : dataImportHistoryList) {
			DataImportHistoryDTO dataImportHistoryDTO = new DataImportHistoryDTO();
			dataImportHistoryDTO.setDataImportHistoryId(dataImportHistory
					.getDataImportHistoryId());
			dataImportHistoryDTO.setFileName(dataImportHistory.getFileName());
			dataImportHistoryDTO.setImportDate(DateUtils
					.timeStampToString(dataImportHistory.getImportDate()));
			dataImportHistoryDTO.setStatus(dataImportHistory.getStatus());
			dataImportHistoryDTOList.add(dataImportHistoryDTO);
		}

		dataImportForm.setDataImportHistoryList(dataImportHistoryDTOList);
		return dataImportForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataImportLogic#getEntityList()
	 */
	@Override
	public List<EntityMasterDTO> getEntityList() {
		Locale locale = UserContext.getLocale();
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		List<EntityMasterDTO> entityList = new ArrayList<EntityMasterDTO>();

		for (EntityMaster entityMaster : entityMasterList) {

			if (!entityMaster.getEntityName().equalsIgnoreCase(
					PayAsiaConstants.PAY_SLIP_ENTITY_NAME)
					&& !entityMaster.getEntityName().equalsIgnoreCase(
							PayAsiaConstants.PAY_DATA_COLLECTION_ENTITY_NAME)) {
				EntityMasterDTO entityMasterDTO = new EntityMasterDTO();
				entityMasterDTO.setEntityId(entityMaster.getEntityId());
				if (StringUtils.isNotBlank(entityMaster.getLabelKey())) {
					String labelMsg = messageSource
							.getMessage(entityMaster.getLabelKey(),
									new Object[] {}, locale);

					entityMasterDTO.setEntityName(labelMsg);
				} else {
					entityMasterDTO.setEntityName(entityMaster.getEntityName());
					if (entityMaster.getEntityName().equalsIgnoreCase(
							PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {
						entityMasterDTO
								.setEntityName(PayAsiaConstants.PAY_SLIP_ENTITY_NAME);
					}

				}

				entityMasterDTO.setEntityDesc(entityMaster.getEntityDesc());

				entityList.add(entityMasterDTO);
			}
		}

		return entityList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.DataImportLogic#getPayslipFrequencyList()
	 */
	@Override
	public List<PayslipFrequencyDTO> getPayslipFrequencyList() {

		List<PayslipFrequency> freqList = payslipFrequencyDAO.findAll();
		List<PayslipFrequencyDTO> paySlipFreqList = new ArrayList<PayslipFrequencyDTO>();

		for (PayslipFrequency payslipFrequency : freqList) {
			PayslipFrequencyDTO payslipFrequencyDTO = new PayslipFrequencyDTO();
			payslipFrequencyDTO.setFrequency(payslipFrequency.getFrequency());
			payslipFrequencyDTO.setFrequency_Desc(payslipFrequency
					.getFrequency_Desc());
			payslipFrequencyDTO.setPayslipFrequencyID(payslipFrequency
					.getPayslipFrequencyID());
			paySlipFreqList.add(payslipFrequencyDTO);
		}

		return paySlipFreqList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.DataImportLogic#getPayslipFrequency(java.lang.Long)
	 */
	@Override
	public List<PayslipFrequencyDTO> getPayslipFrequency(Long companyId) {
		List<PayslipFrequencyDTO> paySlipFreqList = new ArrayList<PayslipFrequencyDTO>();

		Company company = companyDAO.findById(companyId);
		PayslipFrequencyDTO payslipFrequencyDTO = new PayslipFrequencyDTO();
		payslipFrequencyDTO.setFrequency(company.getPayslipFrequency()
				.getFrequency());
		payslipFrequencyDTO.setPayslipFrequencyID(company.getPayslipFrequency()
				.getPayslipFrequencyID());
		paySlipFreqList.add(payslipFrequencyDTO);
		return paySlipFreqList;
	}

	/**
	 * Purpose: To Validate excel file Data.
	 * 
	 * @param colMap
	 *            the col map
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @param company
	 *            the company
	 * @param rowNumber
	 *            the row number
	 * @return the data import key value dto
	 */
	public DataImportKeyValueDTO validateFieldData(
			HashMap<String, EmpDataImportTemplateField> colMap,
			String key,
			String value,
			Company company,
			String rowNumber,
			HashMap<Long, Tab> dynamicFormObjects,
			HashMap<Long, Integer> dynamicFormVersions,
			HashMap<String, ColumnPropertyDTO> staticColPropertyMap,
			HashMap<Long, HashMap<String, DynamicFormComboDTO>> newDropDownValues) {
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		dataImportKeyValueDTO.setNonTemplateVal(false);
		EmpDataImportTemplateField empDataImportTemplateField = colMap.get(key);

		if (empDataImportTemplateField == null) {
			colMap.remove(key);
			dataImportKeyValueDTO.setFieldValid(true);
			dataImportKeyValueDTO.setNonTemplateVal(true);
			return dataImportKeyValueDTO;
		}

		dataImportKeyValueDTO.setValue(value);
		if (StringUtils.isBlank(value)) {
			if (StringUtils.isNotBlank(empDataImportTemplateField
					.getDefaultValue())) {
				dataImportKeyValueDTO.setValue(empDataImportTemplateField
						.getDefaultValue());
				value = empDataImportTemplateField.getDefaultValue();
			}
		}

		boolean isFieldValid = true;
		DynamicForm dynamicForm = null;
		int isNumericValid = 0;
		int isDateValid = 0;
		int isStringValid = 0;
		int isCheckBoxValid = 0;
		int isComboValid = 0;
		int isBlankValid = 0;
		int isColNumericValid = 0;
		int isColDateValid = 0;
		int isColStringValid = 0;
		int isColCheckBoxValid = 0;
		int isColComboValid = 0;
		int isColBlankValid = 0;
		int minLength = 0;
		int maxLength = 0;
		int precision = 0;
		int scale = 0;
		int isEmploymentStatusValid = 0;

		List employmentStatusList = new ArrayList<>();
		employmentStatusList.add(PayAsiaConstants.EMPLOYMENT_STATUS_CONFIRMED);
		employmentStatusList.add(PayAsiaConstants.EMPLOYMENT_STATUS_PROBATION);

		if (empDataImportTemplateField.getDataDictionary().getFieldType()
				.equals(PayAsiaConstants.DYNAMIC_TYPE)) {
			long entityId = empDataImportTemplateField.getDataDictionary()
					.getEntityMaster().getEntityId();
			long formId = empDataImportTemplateField.getDataDictionary()
					.getFormID();
			String dataDictionaryName = empDataImportTemplateField
					.getDataDictionary().getDataDictName();
			Tab tab = dynamicFormObjects.get(formId);

			if (tab == null) {
				int maxVersion = 0;
				synchronized (this) {
					maxVersion = dynamicFormDAO.getMaxVersionByFormId(
							company.getCompanyId(), entityId, formId);
				}

				dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(

				company.getCompanyId(), entityId, maxVersion, formId);

				Unmarshaller unmarshaller = null;

				try {
					unmarshaller = XMLUtil.getDocumentUnmarshaller();
				} catch (JAXBException jaxbException) {
					LOGGER.error(jaxbException.getMessage(), jaxbException);
					throw new PayAsiaSystemException(
							jaxbException.getMessage(), jaxbException);
				} catch (SAXException saxException) {
					LOGGER.error(saxException.getMessage(), saxException);
					throw new PayAsiaSystemException(saxException.getMessage(),
							saxException);
				}

				final StringReader xmlReader = new StringReader(
						dynamicForm.getMetaData());
				Source xmlSource = null;
				try {
					xmlSource = XMLUtil.getSAXSource(xmlReader);
				} catch (SAXException | ParserConfigurationException e1) {
					LOGGER.error(e1.getMessage(), e1);
					throw new PayAsiaSystemException(e1.getMessage(),
							e1);
				}

				try {
					tab = (Tab) unmarshaller.unmarshal(xmlSource);
				} catch (JAXBException jaxbException) {
					LOGGER.error(jaxbException.getMessage(), jaxbException);
					throw new PayAsiaSystemException(
							jaxbException.getMessage(), jaxbException);
				}
				dynamicFormVersions.put(formId, maxVersion);
				dynamicFormObjects.put(formId, tab);
			}

			List<Field> listOfFields = tab.getField();

			for (Field field : listOfFields) {
				if (!StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.TABLE_FIELD_TYPE)
						&& !StringUtils.equalsIgnoreCase(field.getType(),
								PayAsiaConstants.LABEL_FIELD_TYPE)
						&& !StringUtils.equalsIgnoreCase(field.getType(),
								PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
					if (dataDictionaryName
							.equals(new String(Base64.decodeBase64(field
									.getDictionaryName().getBytes())))) {

						if (field.isOptional() && StringUtils.isBlank(value)) {

							isBlankValid = PayAsiaConstants.IS_BLANK;
							break;
						} else if (StringUtils.isNotBlank(value)) {
							value = value.trim();
							if (PayAsiaConstants.FIELD_TYPE_NUMERIC
									.equals(field.getType())) {
								precision = field.getPrecision();
								scale = field.getScale();
								isNumericValid = ValidationUtils
										.validateNumber(key, value, precision,
												scale);
								dataImportKeyValueDTO
										.setFieldType(PayAsiaConstants.FIELD_TYPE_NUMERIC);
								break;
							}
							if (PayAsiaConstants.FIELD_TYPE_TEXT.equals(field
									.getType())) {
								minLength = field.getMinLength();
								maxLength = field.getMaxLength();
								isStringValid = ValidationUtils.validateString(
										key, value, minLength, maxLength);
								dataImportKeyValueDTO
										.setFieldType(PayAsiaConstants.FIELD_TYPE_TEXT);
								break;
							}
							if (PayAsiaConstants.FIELD_TYPE_DATE.equals(field
									.getType())) {

								isDateValid = ValidationUtils
										.validateDate(
												value,
												PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								dataImportKeyValueDTO
										.setFieldType(PayAsiaConstants.FIELD_TYPE_DATE);
								break;
							}
							if (PayAsiaConstants.FIELD_TYPE_CHECK.equals(field
									.getType())) {
								isCheckBoxValid = ValidationUtils
										.validateCheck(key, value);
								dataImportKeyValueDTO
										.setFieldType(PayAsiaConstants.PAYASIA_DATATYPE_BIT);
								break;
							}
							if (PayAsiaConstants.FIELD_TYPE_COMBO.equals(field
									.getType())) {

								List<String> options = field.getOption();
								List<String> optionsUpr = new ArrayList<String>();
								for (String option : options) {
									String decodedOptions = StringEscapeUtils
											.unescapeHtml(new String(Base64
													.decodeBase64(option
															.getBytes())));
									if (StringUtils.isNotBlank(decodedOptions
											.toUpperCase())) {
										optionsUpr.add(decodedOptions
												.toUpperCase().trim());
									}
								}
								isComboValid = ValidationUtils.validateCombo(
										key, value, optionsUpr);

								if (isComboValid == 1) {

									DynamicFormComboDTO dynamicComboDTOValue = new DynamicFormComboDTO();
									HashMap<String, DynamicFormComboDTO> dynamicFormValue;
									Set<String> dropDownValues = new HashSet();
									if (newDropDownValues.get(formId) != null) {

										dynamicFormValue = newDropDownValues
												.get(formId);
										if (dynamicFormValue.get(field
												.getName()) != null) {
											dynamicComboDTOValue = dynamicFormValue
													.get(field.getName());

											dropDownValues = dynamicComboDTOValue
													.getDropDownValues();

										} else {
											dropDownValues = new HashSet();
										}
										dropDownValues
												.add(new String(Base64
														.encodeBase64(value
																.getBytes())));
										dynamicComboDTOValue
												.setDropDownValues(dropDownValues);
										dynamicComboDTOValue
												.setCustFieldName(field
														.getName());

										dynamicFormValue.put(field.getName(),
												dynamicComboDTOValue);

									} else {

										dynamicComboDTOValue = new DynamicFormComboDTO();
										dropDownValues = new HashSet();
										dropDownValues
												.add(new String(Base64
														.encodeBase64(value
																.getBytes())));
										dynamicComboDTOValue
												.setDropDownValues(dropDownValues);
										dynamicComboDTOValue
												.setCustFieldName(field
														.getName());
										dynamicFormValue = new HashMap<>();
										dynamicFormValue.put(field.getName(),
												dynamicComboDTOValue);

										newDropDownValues.put(formId,
												dynamicFormValue);
									}

								}

								dataImportKeyValueDTO
										.setFieldType(PayAsiaConstants.FIELD_TYPE_TEXT);
								break;

							} else {
								break;
							}
						}
					}
				}

				if (StringUtils.equalsIgnoreCase(field.getType(),
						PayAsiaConstants.TABLE_FIELD_TYPE)
						|| StringUtils.equalsIgnoreCase(field.getType(),
								PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
					List<Column> listOfColumns = field.getColumn();
					for (Column column : listOfColumns) {
						if (dataDictionaryName.equals(new String(Base64
								.decodeBase64(column.getDictionaryName()
										.getBytes())))) {

							if (column.isOptional()
									&& StringUtils.isBlank(value)) {

								isColBlankValid = 1;
								break;
							} else if (StringUtils.isNotBlank(value)) {
								value = value.trim();
								if (PayAsiaConstants.FIELD_TYPE_NUMERIC
										.equals(column.getType())) {
									precision = column.getPrecision();
									scale = column.getScale();
									isColNumericValid = ValidationUtils
											.validateNumber(key, value,
													precision, scale);
									dataImportKeyValueDTO
											.setFieldType(PayAsiaConstants.FIELD_TYPE_NUMERIC);
									break;
								}
								if (PayAsiaConstants.FIELD_TYPE_TEXT
										.equals(column.getType())) {
									minLength = column.getMinLength();
									maxLength = column.getMaxLength();
									isColStringValid = ValidationUtils
											.validateString(key, value,
													minLength, maxLength);
									dataImportKeyValueDTO
											.setFieldType(PayAsiaConstants.FIELD_TYPE_TEXT);
									break;
								}
								if (PayAsiaConstants.FIELD_TYPE_DATE
										.equals(column.getType())) {
									isDateValid = ValidationUtils
											.validateDate(
													value,
													PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

									dataImportKeyValueDTO
											.setFieldType(PayAsiaConstants.FIELD_TYPE_DATE);
									break;
								}
								if (PayAsiaConstants.FIELD_TYPE_CHECK
										.equals(column.getType())) {
									isColCheckBoxValid = ValidationUtils
											.validateCheck(key, value);
									dataImportKeyValueDTO
											.setFieldType(PayAsiaConstants.PAYASIA_DATATYPE_BIT);
									break;
								}
								if (PayAsiaConstants.FIELD_TYPE_DROPDOWN
										.equals(column.getType())) {

									List<String> options = column.getOption();
									List<String> optionsUpr = new ArrayList<String>();
									for (String option : options) {
										String decodedOptions;
										try {
											decodedOptions = URLDecoder
													.decode(StringEscapeUtils
															.unescapeHtml(new String(
																	Base64.decodeBase64(option
																			.getBytes()))),
															"UTF-8");
											if (StringUtils
													.isNotBlank(decodedOptions
															.toUpperCase())) {
												optionsUpr.add(decodedOptions
														.toUpperCase().trim());
											}

										} catch (UnsupportedEncodingException e) {
											LOGGER.error(e.getMessage(), e);
										}

									}

									isColComboValid = ValidationUtils
											.validateCombo(key, value,
													optionsUpr);
									if (isColComboValid == 1) {

										DynamicFormComboDTO dynamicComboDTOValue = null;
										HashMap<String, DynamicFormComboDTO> dynamicFormValue;
										Set<String> dropDownValues = null;
										if (newDropDownValues.get(formId) != null) {

											dynamicFormValue = newDropDownValues
													.get(formId);
											if (dynamicFormValue.get(field
													.getName()
													+ column.getName()) != null) {
												dynamicComboDTOValue = dynamicFormValue
														.get(field.getName()
																+ column.getName());
												dropDownValues = dynamicComboDTOValue
														.getDropDownValues();
											} else {
												dropDownValues = new HashSet();
												dynamicComboDTOValue = new DynamicFormComboDTO();
											}
											dropDownValues.add(new String(
													Base64.encodeBase64(value
															.getBytes())));
											dynamicComboDTOValue
													.setDropDownValues(dropDownValues);
											dynamicComboDTOValue
													.setCustFieldName(column
															.getName());

											dynamicFormValue.put(
													field.getName()
															+ column.getName(),
													dynamicComboDTOValue);

										} else {

											dynamicComboDTOValue = new DynamicFormComboDTO();
											dropDownValues = new HashSet();
											dropDownValues.add(new String(
													Base64.encodeBase64(value
															.getBytes())));
											dynamicComboDTOValue
													.setDropDownValues(dropDownValues);
											dynamicComboDTOValue
													.setCustFieldName(column
															.getName());
											dynamicFormValue = new HashMap<>();
											dynamicFormValue.put(
													field.getName()
															+ column.getName(),
													dynamicComboDTOValue);

											newDropDownValues.put(formId,
													dynamicFormValue);
										}

									}

									dataImportKeyValueDTO
											.setFieldType(PayAsiaConstants.FIELD_TYPE_TEXT);
									break;

								} else {
									break;
								}
							}
						}

					}
				}
			}

		} else {
			String dbColName = empDataImportTemplateField.getDataDictionary()
					.getColumnName();
			String tableName = empDataImportTemplateField.getDataDictionary()
					.getTableName();
			ColumnPropertyDTO colProp = staticColPropertyMap.get(dbColName
					+ tableName);

			if (colProp == null) {
				colProp = generalDAO.getColumnProperties(tableName, dbColName);
				staticColPropertyMap.put(dbColName + tableName, colProp);
			}

			dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
			if (colProp.getColumnNullable() == 0 && StringUtils.isBlank(value)) {
				isBlankValid = PayAsiaConstants.IS_BLANK;
			} else if (StringUtils.isNotBlank(value)) {
				if (value.length() > colProp.getColumnLength()) {
					if (!StringUtils.equalsIgnoreCase(colProp.getColumnType(),
							PayAsiaConstants.PAYASIA_DATATYPE_BIT)) {
						isStringValid = PayAsiaConstants.MORE_THEN_MAXLENGTH;
						maxLength = colProp.getColumnLength();
					}

				}
				if (StringUtils.equalsIgnoreCase(colProp.getColumnType(),
						PayAsiaConstants.PAYASIA_DATATYPE_DATETIME)) {

					isDateValid = ValidationUtils.validateDate(value,
							PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

				}
				if (StringUtils.equalsIgnoreCase(colProp.getColumnType(),
						PayAsiaConstants.PAYASIA_DATATYPE_BIT)) {
					isCheckBoxValid = ValidationUtils.validateCheck(key, value);
				}
				if (StringUtils.equalsIgnoreCase(colProp.getColumnType(),
						PayAsiaConstants.FIELD_TYPE_NUMERIC)) {
					isNumericValid = ValidationUtils.validateNAN(value);
				}
				if (dbColName
						.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYMENT_STATUS)) {
					if (!employmentStatusList.contains(value)) {
						isEmploymentStatusValid = PayAsiaConstants.EMPLOYMENT_STATUS_NOT_VALID;
					}

				}
			}

		}
		if (isNumericValid == 0 && isDateValid == 0 && isStringValid == 0
				&& isCheckBoxValid == 0 && isBlankValid == 0
				&& isColNumericValid == 0 && isColDateValid == 0
				&& isColStringValid == 0 && isColCheckBoxValid == 0
				&& isColBlankValid == 0 && isEmploymentStatusValid == 0) {
			isFieldValid = true;
		} else {
			isFieldValid = false;

			String remarks = "";
			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			dataImportLogDTO.setFromMessageSource(false);
			if (isBlankValid == PayAsiaConstants.IS_BLANK
					|| isColBlankValid == PayAsiaConstants.IS_BLANK) {
				dataImportLogDTO.setFromMessageSource(true);
				remarks = "payasia.data.import.mandatory.blank";
			} else if (isDateValid > 0 || isColDateValid > 0) {
				dataImportLogDTO.setFromMessageSource(true);
				remarks = "payasia.data.import.invalid.date";
			} else if (isCheckBoxValid == PayAsiaConstants.CHECK_ERROR
					|| isColCheckBoxValid == PayAsiaConstants.CHECK_ERROR) {
				dataImportLogDTO.setFromMessageSource(true);
				remarks = "payasia.data.import.checkbox.validate.message";
			}

			else if (isStringValid == PayAsiaConstants.LESS_THEN_MINLENGTH
					|| isColStringValid == PayAsiaConstants.LESS_THEN_MINLENGTH) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO.setPostParameter(new Object[] { minLength });
				remarks = "payasia.data.import.minlength.error";
			} else if (isStringValid == PayAsiaConstants.MORE_THEN_MAXLENGTH
					|| isColStringValid == PayAsiaConstants.MORE_THEN_MAXLENGTH) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO.setPostParameter(new Object[] { maxLength });
				remarks = "payasia.data.import.maxlength.error";
			} else if (isNumericValid == PayAsiaConstants.SCALE_ERROR
					|| isColNumericValid == PayAsiaConstants.SCALE_ERROR) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO.setPostParameter(new Object[] { scale });
				remarks = "payasia.data.import.scale.error";
			} else if (isNumericValid == PayAsiaConstants.PRECISION_ERROR
					|| isColNumericValid == PayAsiaConstants.PRECISION_ERROR) {
				dataImportLogDTO.setFromMessageSource(true);
				dataImportLogDTO.setPostParameter(new Object[] { precision });
				remarks = "payasia.data.import.precision.error";
			} else if (isNumericValid == PayAsiaConstants.NOT_A_NUMBER
					|| isColNumericValid == PayAsiaConstants.NOT_A_NUMBER) {
				dataImportLogDTO.setFromMessageSource(true);
				remarks = "payasia.data.import.nan.error";
			} else if (isEmploymentStatusValid == PayAsiaConstants.EMPLOYMENT_STATUS_NOT_VALID) {
				dataImportLogDTO.setFromMessageSource(true);
				remarks = "payasia.data.import.employment.status.not.valid";
			}

			dataImportLogDTO.setColName(key);
			dataImportLogDTO.setFailureType("payasia.data.import.validation");
			dataImportLogDTO.setRemarks(remarks);
			dataImportLogDTO.setRowNumber(Long.parseLong(rowNumber) + 1);
			dataImportKeyValueDTO.setDataImportLogDTO(dataImportLogDTO);
		}
		dataImportKeyValueDTO.setFieldValid(isFieldValid);

		return dataImportKeyValueDTO;
	}

	@Override
	public Integer getPartforCompany(Long companyId) {

		Company company = companyDAO.findById(companyId);

		if ((company.getPart() != null) && (company.getPart() != 0)) {
			return company.getPart();
		}
		return 0;

	}

	@Override
	public String getPayslipFormatforCompany(Long companyId) {

		Company company = companyDAO.findById(companyId);

		if (StringUtils.isNotBlank(company.getPayslipImportType())) {
			return company.getPayslipImportType();
		}
		return "";

	}

	@Override
	public byte[] getPaySlipFromCompanyDocumentFolder(Long companyId,
			String previewPdfFilePath) throws IOException {
		InputStream pdfIn = null;

		File pdfFile = new File(previewPdfFilePath);
		try {
			pdfIn = new FileInputStream(pdfFile);
		} catch (FileNotFoundException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}
		return IOUtils.toByteArray(pdfIn);

	}

	@Override
	public String generateSamplePaySlipPDFForPreview(
			TextPaySlipListDTO textPaySlipListDTO,
			DataImportForm dataImportForm, Long companyId) {
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();

		List<List<TextPaySlipDTO>> textPaySlipList = mergeAndPreProcessTextPayslip(
				textPaySlipListDTO, companyId);

		CompanyLogo companyLogo = companyLogoDAO
				.findByConditionCompany(companyId);

		List<String> employeeNumberList = employeeDAO
				.findAllEmpNumberByCompany(companyId);
		Map<String, String> employeeNumberMap = convertListtoMap(employeeNumberList);

		List<CompanyDocument> employeePayslipList = companyDocumentDAO
				.findByConditionSourceTextAndDesc(companyId, null,
						dataImportForm.getYear(), dataImportForm.getPart(),
						dataImportForm.getMonthId(),
						PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT);
		Map<String, CompanyDocument> payslipEmployeeNumberMap = convertPayslipListtoMap(employeePayslipList);

		Map<String, Object> employeeNumberCache = new HashMap<>();
		employeeNumberCache.put("employeeNumberMap", employeeNumberMap);
		employeeNumberCache.put("payslipEmployeeNumberMap",
				payslipEmployeeNumberMap);

		List<TextPaySlipDTO> textPaySlipDTO = textPaySlipList.get(0);
		try {

			PDFThreadLocal.pageNumbers.set(false);
			return generateTextPaySlipPDF(textPaySlipDTO, dataImportForm,
					companyId, null, dataImportLogDTO, companyLogo,
					employeeNumberCache);
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(false);
			return generateTextPaySlipPDF(textPaySlipDTO, dataImportForm,
					companyId, null, dataImportLogDTO, companyLogo,
					employeeNumberCache);
		}

	}

	private Map<String, String> convertListtoMap(List<String> list) {
		Map<String, String> map = new HashMap<>();
		for (String str : list) {
			map.put(str, str);
		}
		return map;

	}

	private Map<String, CompanyDocument> convertPayslipListtoMap(
			List<CompanyDocument> payslipList) {
		Map<String, CompanyDocument> map = new HashMap<>();
		for (CompanyDocument companyDocument : payslipList) {
			map.put(companyDocument.getDescription().substring(0,
					companyDocument.getDescription().indexOf('_')),
					companyDocument);
		}
		return map;

	}

	@Override
	public DataImportLogDTO generateTextPaySlipPDF(
			TextPaySlipListDTO textPaySlipListDTO,
			DataImportForm dataImportForm, Long companyId) {
		DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
				.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);

		saveCompanyPayslipRelease(dataImportForm, companyId);

		if (dataImportForm.isReleasePayslipWithImport()) {
			companyDocumentDAO.updatePayslipReleaseStatus(companyId,
					dataImportForm.getMonthId(), dataImportForm.getYear(),
					dataImportForm.getPart(),
					categoryMaster.getDocumentCategoryId(), true);
		} else {
			companyDocumentDAO.updatePayslipReleaseStatus(companyId,
					dataImportForm.getMonthId(), dataImportForm.getYear(),
					dataImportForm.getPart(),
					categoryMaster.getDocumentCategoryId(), false);
		}

		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();

		List<List<TextPaySlipDTO>> textPaySlipList = mergeAndPreProcessTextPayslip(
				textPaySlipListDTO, companyId);

		dataImportLogDTO.setImportTypeTextPdfFile(true);
		dataImportLogDTO.setTotalRecords(textPaySlipList.size());
		dataImportLogDTO.setTotalSuccessRecords(0);
		dataImportLogDTO.setTotalFailedRecords(0);
		List<String> invalidEmployeesNumbersList = new ArrayList<>();
		List<String> validEmployeesNumbersList = new ArrayList<>();
		dataImportLogDTO.setValidEmployeeNumbersList(validEmployeesNumbersList);
		dataImportLogDTO
				.setInvalidEmployeeNumbersList(invalidEmployeesNumbersList);

		CompanyLogo companyLogo = companyLogoDAO
				.findByConditionCompany(companyId);

		List<String> employeeNumberList = employeeDAO
				.findAllEmpNumberByCompany(companyId);
		Map<String, String> employeeNumberMap = convertListtoMap(employeeNumberList);

		List<CompanyDocument> employeePayslipList = companyDocumentDAO
				.findByConditionSourceTextAndDesc(companyId,
						categoryMaster.getDocumentCategoryId(),
						dataImportForm.getYear(), dataImportForm.getPart(),
						dataImportForm.getMonthId(),
						PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT);
		Map<String, CompanyDocument> payslipEmployeeNumberMap = convertPayslipListtoMap(employeePayslipList);

		Map<String, Object> employeeNumberCache = new HashMap<>();
		employeeNumberCache.put("employeeNumberMap", employeeNumberMap);
		employeeNumberCache.put("payslipEmployeeNumberMap",
				payslipEmployeeNumberMap);

		for (List<TextPaySlipDTO> textPaySlipDTOList : textPaySlipList) {
			try {

				PDFThreadLocal.pageNumbers.set(false);
				generateTextPaySlipPDF(textPaySlipDTOList, dataImportForm,
						companyId, categoryMaster.getDocumentCategoryId(),
						dataImportLogDTO, companyLogo, employeeNumberCache);
			} catch (PDFMultiplePageException mpe) {

				LOGGER.error(mpe.getMessage(), mpe);
				PDFThreadLocal.pageNumbers.set(false);
				generateTextPaySlipPDF(textPaySlipDTOList, dataImportForm,
						companyId, categoryMaster.getDocumentCategoryId(),
						dataImportLogDTO, companyLogo, employeeNumberCache);
			}
		}

		List<String> validEmpNumberList = dataImportLogDTO
				.getValidEmployeeNumbersList();
		sendPayslipReleaseMail(dataImportForm, companyId, validEmpNumberList,
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT);
		return dataImportLogDTO;
	}

	private String generateTextPaySlipPDF(
			List<TextPaySlipDTO> textPaySlipDTOList,
			DataImportForm dataImportForm, Long companyId,
			Long documentCategoryId, DataImportLogDTO dataImportLogDTO,
			CompanyLogo companyLogo, Map<String, Object> employeeNumberCache) {
		Map<String, String> employeeNumberMap = (Map<String, String>) employeeNumberCache
				.get("employeeNumberMap");
		Map<String, CompanyDocument> payslipEmployeeNumberMap = (Map<String, CompanyDocument>) employeeNumberCache
				.get("payslipEmployeeNumberMap");

		String employeenumber = getTextPayslipEmployeeNumber(textPaySlipDTOList
				.get(0));
		Employee employee = employeeDAO.findByNumber(employeenumber, companyId);
		if (employee != null) {
			employeenumber = employee.getEmployeeNumber();
		}

		if (employeeNumberMap.containsKey(employeenumber)) {
			int totalSuccessRecords = dataImportLogDTO.getTotalSuccessRecords();
			dataImportLogDTO.setTotalSuccessRecords(totalSuccessRecords + 1);
			List<String> validEmployeesNumbersList = dataImportLogDTO
					.getValidEmployeeNumbersList();
			validEmployeesNumbersList.add(employeenumber);
			dataImportLogDTO
					.setValidEmployeeNumbersList(validEmployeesNumbersList);
		} else {
			int totalFailedRecords = dataImportLogDTO.getTotalFailedRecords();
			dataImportLogDTO.setTotalFailedRecords(totalFailedRecords + 1);
			List<String> invalidEmployeesNumbersList = dataImportLogDTO
					.getInvalidEmployeeNumbersList();
			invalidEmployeesNumbersList.add(employeenumber);
			dataImportLogDTO
					.setInvalidEmployeeNumbersList(invalidEmployeesNumbersList);
		}

		String filePath = "";
		File tempFile = null;
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		List<byte[]> pdfFileInByteArrList = new ArrayList<>();
		try {
			for (TextPaySlipDTO textPaySlipDTO : textPaySlipDTOList) {

				/*
				 * filePath = downloadPath + "/company/" + companyId + "/" +
				 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" +
				 * dataImportForm.getYear() + "/" + employeenumber + "/";
				 */

				FilePathGeneratorDTO filePathGenerator = fileUtils
						.getFileCommonPath(
								downloadPath,
								rootDirectoryName,
								companyId,
								PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP,
								null,
								String.valueOf(dataImportForm.getYear()),
								employeenumber,
								null,
								PayAsiaConstants.PAYASIA_DOC_DOWNLOAD_PAYSLIPPDF,
								0);
				filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

				tempFile = PDFUtils.getTempFileForTextPayslip(companyId,
						dataImportForm.getYear(), dataImportForm.getMonthId(),
						dataImportForm.getPart(), employeenumber, downloadPath,
						appDeployLocation, PAYASIA_TEMP_PATH);

				document = new Document(PayAsiaPDFConstants.PAGE_SIZE,
						PayAsiaPDFConstants.PAGE_LEFT_MARGIN,
						PayAsiaPDFConstants.PAGE_TOP_MARGIN,
						PayAsiaPDFConstants.PAGE_RIGHT_MARGIN,
						PayAsiaPDFConstants.PAGE_BOTTOM_MARGIN);

				pdfOut = new FileOutputStream(tempFile);

				PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
				writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

				document.open();

				PdfPTable textPayslipTable = payAsiaTextPaySlipToPdf
						.createTextPaySlipPdf(document, 1, textPaySlipDTO,
								dataImportForm, companyId, companyLogo);
				document.add(textPayslipTable);

				PdfAction action = PdfAction.gotoLocalPage(1,
						new PdfDestination(PdfDestination.FIT, 0, 10000, 1),
						writer);
				writer.setOpenAction(action);
				writer.setViewerPreferences(PdfWriter.FitWindow
						| PdfWriter.CenterWindow);

				document.close();

				pdfIn = new FileInputStream(tempFile);
				pdfFileInByteArrList.add(IOUtils.toByteArray(pdfIn));

				tempFile.delete();
			}
			File destMergedPdfFile = null;
			if (!textPaySlipDTOList.isEmpty()) {

				destMergedPdfFile = PDFUtils.getTempFileForTextPayslip(
						companyId, dataImportForm.getYear(),
						dataImportForm.getMonthId(), dataImportForm.getPart(),
						employeenumber, downloadPath, appDeployLocation,
						PAYASIA_TEMP_PATH);

				Document copyDocument = new Document();

				PdfCopy copy = new PdfCopy(copyDocument, new FileOutputStream(
						destMergedPdfFile));

				copyDocument.open();

				PdfReader copyPdfReader;
				int noOfPagesInExistPdf;

				for (byte[] pdfByteFile : pdfFileInByteArrList) {
					copyPdfReader = new PdfReader(pdfByteFile);
					ByteArrayOutputStream boas = new ByteArrayOutputStream();
					PdfStamper stamper;
					noOfPagesInExistPdf = copyPdfReader.getNumberOfPages();
					for (int page = 0; page < noOfPagesInExistPdf;) {
						copy.addPage(copy
								.getImportedPage(copyPdfReader, ++page));
						stamper = new PdfStamper(copyPdfReader, boas);
						stamper.close();
					}
					copy.freeReader(copyPdfReader);
					copyPdfReader.close();

				}
				copyDocument.close();
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					InputStream inputStream = new FileInputStream(
							destMergedPdfFile);
					byte[] pdf = IOUtils.toByteArray(inputStream);
					awss3LogicImpl.uploadByteArrayFile(pdf, filePath
							+ destMergedPdfFile.getName());
				}

				if (documentCategoryId != null) {
					MonthMaster monthMaster = new MonthMaster();
					monthMaster.setMonthId(dataImportForm.getMonthId());

					CompanyDocument companyDocumentVO = payslipEmployeeNumberMap
							.get(employeenumber);

					String fileName = destMergedPdfFile
							.getName()
							.trim()
							.substring(
									0,
									destMergedPdfFile.getName().trim()
											.lastIndexOf("."));

					CompanyDocument companyDocument = new CompanyDocument();
					Company company = new Company();
					company.setCompanyId(companyId);
					companyDocument.setCompany(company);

					DocumentCategoryMaster documentCategoryMaster = new DocumentCategoryMaster();
					documentCategoryMaster
							.setDocumentCategoryId(documentCategoryId);
					companyDocument
							.setDocumentCategoryMaster(documentCategoryMaster);

					companyDocument.setYear(dataImportForm.getYear());
					companyDocument.setPart(dataImportForm.getPart());
					companyDocument.setReleased(dataImportForm
							.isReleasePayslipWithImport());
					companyDocument.setUploadedDate(DateUtils
							.getCurrentTimestampWithTime());

					companyDocument.setMonth(monthMaster);
					companyDocument.setDescription(fileName);
					companyDocument
							.setSource(PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT);
					CompanyDocument saveReturnObj = null;
					if (companyDocumentVO != null) {
						companyDocumentVO.setReleased(dataImportForm
								.isReleasePayslipWithImport());
						companyDocumentVO.setUploadedDate(DateUtils
								.getCurrentTimestampWithTime());
						companyDocumentVO.setDescription(destMergedPdfFile
								.getName().trim());
						companyDocumentDAO.update(companyDocumentVO);
					} else {
						saveReturnObj = companyDocumentDAO
								.save(companyDocument);
					}

					if (companyDocumentVO == null) {
						CompanyDocumentDetail companyDocumentDetail = new CompanyDocumentDetail();
						companyDocumentDetail.setCompanyDocument(saveReturnObj);
						companyDocumentDetail.setCompanyId(companyId);
						companyDocumentDetail
								.setFileType(PayAsiaConstants.FILE_TYPE_PDF);
						companyDocumentDetail.setFileName(fileName);
						companyDocumentDetailDAO.save(companyDocumentDetail);
					}
				}
			}

			String destMergedPdfFileName = "";
			if (destMergedPdfFile != null)
				destMergedPdfFileName = filePath + destMergedPdfFile.getName();
			else
				destMergedPdfFileName = filePath + "unknownFileName";

			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
					.equalsIgnoreCase(appDeployLocation)) {
				if (destMergedPdfFile.exists()) {
					destMergedPdfFile.delete();
				}
			}

			return destMergedPdfFileName;
		}

		catch (DocumentException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {

			LOGGER.error(e.getMessage(), e);
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}

		}
		return null;
	}

	public String getTextPayslipEmployeeNumber(TextPaySlipDTO textPaySlipDTO) {
		String employeenumber = "";
		List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSectionDTOList1 = textPaySlipDTO
				.getEmpInfoHeaderSection1DTOList();
		for (EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO : empInfoHeaderSectionDTOList1) {
			if (StringUtils.isNotBlank(empInfoHeaderSectionDTO
					.getKeyConstant1())
					&& empInfoHeaderSectionDTO.getKeyConstant1().contains(
							"EMPL NO")) {
				employeenumber = empInfoHeaderSectionDTO
						.getKeyConstant1()
						.trim()
						.substring(
								9,
								empInfoHeaderSectionDTO.getKeyConstant1()
										.length());
			}
			if (StringUtils.isNotBlank(empInfoHeaderSectionDTO
					.getKeyConstant2())
					&& empInfoHeaderSectionDTO.getKeyConstant2().contains(
							"EMPL NO")) {
				employeenumber = empInfoHeaderSectionDTO
						.getKeyConstant2()
						.trim()
						.substring(
								9,
								empInfoHeaderSectionDTO.getKeyConstant2()
										.trim().length());
			}
			if (StringUtils.isNotBlank(empInfoHeaderSectionDTO
					.getKeyConstant3())
					&& empInfoHeaderSectionDTO.getKeyConstant3().contains(
							"EMPL NO")) {
				employeenumber = empInfoHeaderSectionDTO
						.getKeyConstant3()
						.trim()
						.substring(
								9,
								empInfoHeaderSectionDTO.getKeyConstant3()
										.length());
			}

		}

		if (StringUtils.isBlank(employeenumber)) {
			List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSectionDTOList2 = textPaySlipDTO
					.getEmpInfoHeaderSection2DTOList();
			for (EmpInfoHeaderTextPayslipDTO empInfoHeaderSectionDTO : empInfoHeaderSectionDTOList2) {
				if (StringUtils.isNotBlank(empInfoHeaderSectionDTO
						.getKeyConstant1())
						&& empInfoHeaderSectionDTO.getKeyConstant1().contains(
								"EMPL NO")) {
					employeenumber = empInfoHeaderSectionDTO
							.getKeyConstant1()
							.trim()
							.substring(
									9,
									empInfoHeaderSectionDTO.getKeyConstant1()
											.length());
				}
				if (StringUtils.isNotBlank(empInfoHeaderSectionDTO
						.getKeyConstant2())
						&& empInfoHeaderSectionDTO.getKeyConstant2().contains(
								"EMPL NO")) {
					employeenumber = empInfoHeaderSectionDTO
							.getKeyConstant2()
							.trim()
							.substring(
									9,
									empInfoHeaderSectionDTO.getKeyConstant2()
											.trim().length());
				}
				if (StringUtils.isNotBlank(empInfoHeaderSectionDTO
						.getKeyConstant3())
						&& empInfoHeaderSectionDTO.getKeyConstant3().contains(
								"EMPL NO")) {
					employeenumber = empInfoHeaderSectionDTO
							.getKeyConstant3()
							.trim()
							.substring(
									9,
									empInfoHeaderSectionDTO.getKeyConstant3()
											.length());
				}

			}
		}

		return employeenumber.trim();
	}

	@Override
	public DataImportLogDTO uploadPDFZipFileForPayslips(
			CommonsMultipartFile fileData, String filePath, String fileType,
			Long companyId, List<CompanyDocumentLogDTO> documentLogs,
			DataImportForm dataImportForm) {
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		String entryFileType;
		Image logoImage = null;
		CompanyLogo companyLogoData = companyLogoDAO
				.findByConditionCompany(companyId);
		if (companyLogoData != null) {
			/*
			 * String fileLogoPath = "company/" +
			 * companyLogoData.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogoData.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyLogoData.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
							String.valueOf(companyLogoData.getCompanyLogoId()),
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String fileLogoPath = fileUtils
					.getGeneratedFilePath(filePathGenerator);

			try {
				byte[] byteFile;
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					byteFile = org.apache.commons.io.IOUtils
							.toByteArray(awss3LogicImpl
									.readS3ObjectAsStream(fileLogoPath));
				} else {
					File file = new File(fileLogoPath);
					byteFile = Files.readAllBytes(file.toPath());
				}

				logoImage = Image.getInstance(byteFile);
			} catch (BadElementException | IOException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage());
			}
		} else {
			URL imageURL = Thread.currentThread().getContextClassLoader()
					.getResource("images/logo.png");

			try {
				logoImage = Image.getInstance(imageURL);
			} catch (BadElementException | IOException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage());
			}
		}

		List<String> zipFileNames = new ArrayList<String>();
		List<String> invalidZipFileNames = new ArrayList<String>();
		int totalPdfCount = 0;
		Enumeration<?> entries;
		ZipFile zipFile;
		String sourceDesc = fileData.getStorageDescription();
		try {
			String fileName = sourceDesc.substring(4).replace("]", "");

			zipFile = new ZipFile(new File(fileName));
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String entryName = entry.getName();
				entryFileType = entryName
						.substring(entryName.lastIndexOf(".") + 1);
				if (entry.isDirectory()) {

					continue;
				}
				if (!entryFileType
						.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_PDF)) {

					continue;
				}

				String employeeNumber = generalLogic
						.getValidEmployeeNumberFromFileName(entryName
								.substring(entryName.lastIndexOf("/") + 1),
								documentLogs, companyId, dataImportForm
										.isFileNameContainsCompNameForPdf(),
								invalidZipFileNames);
				totalPdfCount++;
				if (!employeeNumber
						.equalsIgnoreCase(PayAsiaConstants.PAYASIA_ERROR)) {

					String path = filePath + employeeNumber;

					String ext = "pdf";
					String pdfFileName = employeeNumber + "_payslip_"
							+ dataImportForm.getYear()
							+ dataImportForm.getMonthId()
							+ dataImportForm.getPart() + "." + ext;
					OutputStream out = null;
					if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
							.equalsIgnoreCase(appDeployLocation)) {
						File folder = new File(path);
						if (!folder.exists()) {
							folder.mkdirs();
						}
						File file = new File(path, pdfFileName);
						out = new FileOutputStream(file);
					}
					InputStream inputStream = zipFile.getInputStream(entry);
					zipFileNames.add(pdfFileName);
					if (dataImportForm.isAddCompanyLogoInPdf()) {
						try {

							PdfReader pdfReader = new PdfReader(inputStream);

							PdfStamper pdfStamper;
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
									.equalsIgnoreCase(appDeployLocation)) {
								pdfStamper = new PdfStamper(pdfReader, baos);
							} else {
								pdfStamper = new PdfStamper(pdfReader, out);
							}

							for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

								PdfContentByte content = pdfStamper
										.getUnderContent(i);
								logoImage
										.scaleToFit(
												PayAsiaPDFConstants.PAYSLIP_LOGO_IMAGE_WIDTH,
												PayAsiaPDFConstants.PAYSLIP_LOGO_IMAGE_HEIGHT);
								logoImage.setRotationDegrees(0);
								if (dataImportForm.getLogoAlignment()
										.equalsIgnoreCase("center")) {
									logoImage
											.setAbsolutePosition(
													PayAsiaPDFConstants.PAYSLIP_LOGO_IMAGE_CENTER_ALIGNMENT_X_PADDING,
													PayAsiaPDFConstants.PAYSLIP_LOGO_IMAGE_ALIGNMENT_Y_PADDING);
								} else if (dataImportForm.getLogoAlignment()
										.equalsIgnoreCase("right")) {
									logoImage
											.setAbsolutePosition(
													PayAsiaPDFConstants.PAYSLIP_LOGO_IMAGE_RIGHT_ALIGNMENT_X_PADDING,
													PayAsiaPDFConstants.PAYSLIP_LOGO_IMAGE_ALIGNMENT_Y_PADDING);
								} else {
									logoImage
											.setAbsolutePosition(
													PayAsiaPDFConstants.PAYSLIP_LOGO_IMAGE_LEFT_ALIGNMENT_X_PADDING,
													PayAsiaPDFConstants.PAYSLIP_LOGO_IMAGE_ALIGNMENT_Y_PADDING);
								}

								content.addImage(logoImage);
							}
							pdfStamper.close();
							if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
									.equalsIgnoreCase(appDeployLocation)) {

								awss3LogicImpl.uploadByteArrayFile(
										baos.toByteArray(), path
												+ docPathSeperator
												+ pdfFileName);
							}
						} catch (IOException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage());
						} catch (DocumentException e) {
							LOGGER.error(e.getMessage(), e);
							throw new PayAsiaSystemException(e.getMessage());
						}

					} else {
						if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
								.equalsIgnoreCase(appDeployLocation)) {
							awss3LogicImpl.uploadByteArrayFile(
									IOUtils.toByteArray(inputStream), path
											+ docPathSeperator + pdfFileName);
						} else {
							byte[] buf = new byte[1024];
							int len;
							while ((len = inputStream.read(buf)) > 0) {
								out.write(buf, 0, len);
							}
							out.close();
						}
					}
					inputStream.close();
				}

			}

			zipFile.close();
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
			throw new PayAsiaSystemException(ioe.getMessage(), ioe);
		}
		dataImportLogDTO.setInvalidEmployeeNumbersList(invalidZipFileNames);
		dataImportLogDTO.setZipFileNamesList(zipFileNames);
		dataImportLogDTO.setTotalRecords(totalPdfCount);
		return dataImportLogDTO;
	}

	@Override
	public void uploadPaySlipPDF(DataImportForm dataImportForm,
			List<String> zipFileNamesList, Long companyId) {

		List<String> notExistingZipFileNames = new ArrayList<String>();
		List<String> notExistingZipFileEmpNums = new ArrayList<String>();

		List<String> zipFileNames = zipFileNamesList;
		CompanyDocumentDetail companyDocumentDetailVO = null;
		for (String zipFileName : zipFileNames) {
			companyDocumentDetailVO = companyDocumentDetailDAO
					.findByFileNameAndCondition(companyId, zipFileName,
							dataImportForm.getYear(),
							dataImportForm.getMonthId(),
							dataImportForm.getPart(),
							PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF);

			if (companyDocumentDetailVO == null) {
				notExistingZipFileNames.add(zipFileName);
			}
		}

		try {
			DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
					.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);

			for (String notExistingZipFileName : notExistingZipFileNames) {
				String employeeNumByzipFile = notExistingZipFileName.substring(
						0, notExistingZipFileName.indexOf('_'));
				notExistingZipFileEmpNums.add(employeeNumByzipFile);
				if (categoryMaster != null) {
					MonthMaster monthMaster = new MonthMaster();
					monthMaster.setMonthId(dataImportForm.getMonthId());

					CompanyDocument companyDocumentVO = companyDocumentDAO
							.findByConditionSourceTextAndDesc(
									companyId,
									categoryMaster.getDocumentCategoryId(),
									dataImportForm.getYear(),
									dataImportForm.getPart(),
									dataImportForm.getMonthId(),
									PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF,
									employeeNumByzipFile);

					CompanyDocument companyDocument = new CompanyDocument();
					Company company = new Company();
					company.setCompanyId(companyId);
					companyDocument.setCompany(company);

					DocumentCategoryMaster documentCategoryMaster = new DocumentCategoryMaster();
					documentCategoryMaster.setDocumentCategoryId(categoryMaster
							.getDocumentCategoryId());
					companyDocument
							.setDocumentCategoryMaster(documentCategoryMaster);

					companyDocument.setYear(dataImportForm.getYear());
					companyDocument.setPart(dataImportForm.getPart());
					companyDocument.setReleased(dataImportForm
							.isReleasePayslipWithImport());
					companyDocument.setUploadedDate(DateUtils
							.getCurrentTimestampWithTime());

					companyDocument.setMonth(monthMaster);
					companyDocument.setDescription(notExistingZipFileName);
					companyDocument
							.setSource(PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF);
					CompanyDocument saveReturnObj = null;
					if (companyDocumentVO != null) {
						companyDocumentVO.setReleased(dataImportForm
								.isReleasePayslipWithImport());
						companyDocumentVO.setUploadedDate(DateUtils
								.getCurrentTimestampWithTime());
						companyDocumentVO
								.setDescription(notExistingZipFileName);
						companyDocumentDAO.update(companyDocumentVO);
					} else {
						saveReturnObj = companyDocumentDAO
								.save(companyDocument);
					}

					if (companyDocumentVO == null) {
						CompanyDocumentDetail companyDocumentDetail = new CompanyDocumentDetail();
						companyDocumentDetail.setCompanyDocument(saveReturnObj);
						companyDocumentDetail.setCompanyId(companyId);
						companyDocumentDetail
								.setFileType(PayAsiaConstants.FILE_TYPE_PDF);
						companyDocumentDetail
								.setFileName(notExistingZipFileName);
						companyDocumentDetailDAO.save(companyDocumentDetail);
					}
				}

			}
			if (!notExistingZipFileNames.isEmpty()) {
				saveCompanyPayslipRelease(dataImportForm, companyId);

				if (dataImportForm.isReleasePayslipWithImport()) {
					companyDocumentDAO.updatePayslipReleaseStatus(companyId,
							dataImportForm.getMonthId(),
							dataImportForm.getYear(), dataImportForm.getPart(),
							categoryMaster.getDocumentCategoryId(), true);
				} else {
					companyDocumentDAO.updatePayslipReleaseStatus(companyId,
							dataImportForm.getMonthId(),
							dataImportForm.getYear(), dataImportForm.getPart(),
							categoryMaster.getDocumentCategoryId(), false);
				}

				sendPayslipReleaseMail(dataImportForm, companyId,
						notExistingZipFileEmpNums,
						PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF);
			}
			else
			{
				saveCompanyPayslipRelease(dataImportForm, companyId);
			}

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);

		}

	}

	public List<List<TextPaySlipDTO>> mergeAndPreProcessTextPayslip(
			TextPaySlipListDTO textPaySlipListDTO, Long companyId) {

		Map<String, List<TextPaySlipDTO>> textPayslipMap = new HashMap<>();
		List<TextPaySlipDTO> textPaySlipDTOList = textPaySlipListDTO
				.getTextPaySlipDTOList();
		for (TextPaySlipDTO textPaySlipDTO : textPaySlipDTOList) {
			List<TextPaySlipDTO> empTextPaySlipDTOList = new ArrayList<>();
			String employeeNumber = getTextPayslipEmployeeNumber(textPaySlipDTO);
			if (!textPayslipMap.containsKey(employeeNumber)) {
				empTextPaySlipDTOList.add(textPaySlipDTO);
				textPayslipMap.put(employeeNumber, empTextPaySlipDTOList);
			} else {
				List<TextPaySlipDTO> existingTextPaySlipDTOList = textPayslipMap
						.get(employeeNumber);
				empTextPaySlipDTOList.add(textPaySlipDTO);
				existingTextPaySlipDTOList.addAll(empTextPaySlipDTOList);
			}

		}

		List<List<TextPaySlipDTO>> modifiedTextPaySlipDTOList = new ArrayList<>();
		Set<String> keySet = textPayslipMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			List<TextPaySlipDTO> textPaySlipDTO = textPayslipMap.get(key);
			if (!textPaySlipDTO.isEmpty()) {
				modifiedTextPaySlipDTOList.add(textPaySlipDTO);
			}
		}
		return modifiedTextPaySlipDTOList;

	}

	@Override
	public String getEntityName(Long entityId) {
		EntityMaster entityMasterVO = entityMasterDAO.findById(entityId);
		String entityName = entityMasterVO.getEntityName();
		return entityName;
	}

	private Long getEntityId(String entityName,
			List<EntityMaster> entityMasterList) {
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityName.equalsIgnoreCase(entityMaster.getEntityName())) {
				return entityMaster.getEntityId();
			}
		}
		return null;
	}

	/**
	 * get Employee FilterList.
	 * 
	 * @param companyId
	 *            the company id
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	@Override
	public List<EmployeeFilterListForm> getCompanyFilterList(Long companyId,
			Long employeeId) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		Long entityId = getEntityId(PayAsiaConstants.COMPANY_ENTITY_NAME,
				entityMasterList);
		HashMap<Long, Tab> tabMap = new HashMap<>();
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntity(entityId, PayAsiaConstants.STATIC_TYPE);
		if (dataDictionaryList != null) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				if (dataDictionary.getDataDictName().equalsIgnoreCase(
						PayAsiaConstants.COMPANY_COMPANY_NAME)) {
					EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
					employeeFilterListForm.setDataDictionaryId(dataDictionary
							.getDataDictionaryId());
					employeeFilterListForm.setFieldName(dataDictionary
							.getLabel());
					try {
						ColumnPropertyDTO columnPropertyDTO = generalDAO
								.getColumnProperties(
										dataDictionary.getTableName(),
										dataDictionary.getColumnName());
						employeeFilterListForm.setDataType(columnPropertyDTO
								.getColumnType());
					} catch (Exception exception) {
						LOGGER.error(exception.getMessage(), exception);
					}
					employeeFilterList.add(employeeFilterListForm);
				}

			}
		}

		List<DataDictionary> dataDictionaryCompanyList = dataDictionaryDAO
				.findByConditionEntityAndCompanyId(companyId, entityId,
						PayAsiaConstants.DYNAMIC_TYPE);
		if (dataDictionaryCompanyList != null) {
			for (DataDictionary dataDictionary : dataDictionaryCompanyList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setDataDictionaryId(dataDictionary
						.getDataDictionaryId());
				employeeFilterListForm.setFieldName(dataDictionary.getLabel());
				Tab tab = tabMap.get(dataDictionary.getFormID());

				if (tab == null) {
					DynamicForm dynamicForm = dynamicFormDAO
							.findMaxVersionByFormId(companyId, entityId,
									dataDictionary.getFormID());
					tab = dataExportUtils.getTabObject(dynamicForm);
					tabMap.put(dataDictionary.getFormID(), tab);
				}

				List<Field> listOfFields = tab.getField();

				for (Field field : listOfFields) {
					if (!StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.TABLE_FIELD_TYPE)
							&& !StringUtils.equalsIgnoreCase(field.getType(),
									PayAsiaConstants.LABEL_FIELD_TYPE)
							&& !StringUtils
									.equalsIgnoreCase(
											field.getType(),
											PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
						if (new String(Base64.decodeBase64(field
								.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())) {
							employeeFilterListForm.setDataType(field.getType());

						}

					} else if (field.getType().equals(
							PayAsiaConstants.TABLE_FIELD_TYPE)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (new String(Base64.decodeBase64(column
									.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {
								employeeFilterListForm.setDataType(column
										.getType());
							}
						}
					}
				}

				employeeFilterList.add(employeeFilterListForm);

			}
		}
		return employeeFilterList;

	}

	@Override
	public String configureCompanyAddress(long companyId,
			String[] dataDictionaryIds) {

		companyAddressMappingDAO.deleteByCondition(companyId);

		Company company = companyDAO.findById(companyId);
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		for (int count = 0; count < dataDictionaryIds.length; count++) {
			if (!dataDictionaryIds[count].equals("0")) {
				dataDictionaryIdsList.add(Long
						.parseLong(dataDictionaryIds[count]));
				// Save in Company Address Mapping
				CompanyAddressMapping companyAddressMapping = new CompanyAddressMapping();
				companyAddressMapping.setCompany(company);
				DataDictionary dataDictionary = dataDictionaryDAO.findById(Long
						.parseLong(dataDictionaryIds[count]));
				companyAddressMapping.setDataDictionary(dataDictionary);
				companyAddressMappingDAO.save(companyAddressMapping);
			}
		}

		CustomFieldReportDTO customFieldReportDTO = generalLogic
				.getCustomFieldDataForCompanyEntity(dataDictionaryIdsList,
						companyId, false);

		List<Object[]> customFieldObjList = customFieldReportDTO
				.getCustomFieldObjList();
		String companyAddress = "";
		int count = 1;
		for (Object[] objArr : customFieldObjList) {
			for (Object obj : objArr) {
				if (count != 1 && (obj != null)
						&& StringUtils.isNotBlank(obj.toString())) {
					companyAddress += obj + ", ";
				}
				count++;
			}
		}
		companyAddress = StringUtils.removeEnd(companyAddress, ", ");
		return companyAddress;
	}

	@Override
	public List<EmployeeFilterListForm> getCompanyAddressMappingList(
			Long companyId) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();
		List<CompanyAddressMapping> mappingList = companyAddressMappingDAO
				.findByCondition(companyId);
		for (CompanyAddressMapping mapping : mappingList) {
			EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
			employeeFilterListForm.setDataDictionaryId(mapping
					.getDataDictionary().getDataDictionaryId());
			employeeFilterListForm.setFilterId(mapping.getCompanyAddressId());
			employeeFilterList.add(employeeFilterListForm);
		}

		return employeeFilterList;
	}

	@Override
	public String getCompanyAddress(long companyId) {
		List<Long> dataDictionaryIdsList = new ArrayList<>();
		List<CompanyAddressMapping> companyAddressMappingList = companyAddressMappingDAO
				.findByCondition(companyId);
		for (CompanyAddressMapping addressMapping : companyAddressMappingList) {
			dataDictionaryIdsList.add(addressMapping.getDataDictionary()
					.getDataDictionaryId());
		}

		CustomFieldReportDTO customFieldReportDTO = generalLogic
				.getCustomFieldDataForCompanyEntity(dataDictionaryIdsList,
						companyId, false);

		List<Object[]> customFieldObjList = customFieldReportDTO
				.getCustomFieldObjList();
		String companyAddress = "";
		int count = 1;
		for (Object[] objArr : customFieldObjList) {
			for (Object obj : objArr) {
				if (count != 1 && (obj != null)
						&& StringUtils.isNotBlank(obj.toString())) {
					companyAddress += obj + ", ";
				}
				count++;
			}
		}
		companyAddress = StringUtils.removeEnd(companyAddress, ", ");
		return companyAddress;
	}

	public static byte[] toByteArrayUsingJava(InputStream is)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int reads = is.read();
		while (reads != -1) {
			baos.write(reads);
			reads = is.read();
		}
		return baos.toByteArray();
	}
	
	@Override
	public DataImportForm createUpdateEmpDataWS(Long companyId,
			HashMap<String, EmpDataImportTemplateField> colMap,
			List<HashMap<String, String>> importedData, String uploadType,
			String transactionType) {
		Company company = companyDAO.findById(companyId);

		List<DataImportLogDTO> dataImportLogList = new ArrayList<DataImportLogDTO>();
		HashMap<Long, HashMap<String, DynamicFormComboDTO>> newDropDownValues = new HashMap<>();
		HashMap<Long, Tab> dynamicFormObjects = new HashMap<>();
		List<HashMap<String, String>> inValidData = new ArrayList<HashMap<String, String>>();
		List<HashMap<String, DataImportKeyValueDTO>> keyValMapList = new ArrayList<HashMap<String, DataImportKeyValueDTO>>();

		HashMap<String, ColumnPropertyDTO> staticColPropertyMap = new HashMap<>();
		HashMap<Long, Integer> dynamicFormVersions = new HashMap<>();

		List<DataImportLogDTO> finalLogs = preProcessFieldData(company,
				importedData, inValidData, keyValMapList, dynamicFormObjects,
				staticColPropertyMap, dynamicFormVersions, colMap,
				newDropDownValues);
		dataImportLogList.addAll(finalLogs);

		// ROLLBACK_ON_ERROR
		DataImportForm dataImportForm = new DataImportForm();
		dataImportForm = preprocessCreateUpdateEmpDataWS(company,
				dataImportLogList, inValidData, true);
		if (dataImportForm != null) {
			return dataImportForm;
		}

		DataImportForm importLogs = new DataImportForm();
		try {
			employeeDataImportLogic.saveValidDataFile(keyValMapList, 0l,
					PayAsiaConstants.EMPLOYEE_ENTITY_ID, colMap, uploadType,
					transactionType, companyId, dynamicFormObjects,
					dynamicFormVersions);
		} catch (PayAsiaRollBackDataException ex) {
			LOGGER.error(ex.getMessage(), ex);
			importLogs.setDataImportLogList(ex.getErrors());
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			importLogs.setDataImportLogList(pde.getErrors());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();

			DataImportLogDTO error = new DataImportLogDTO();

			error.setFailureType("payasia.record.error");
			error.setRemarks("payasia.record.error");
			error.setFromMessageSource(false);

			errors.add(error);

			importLogs.setDataImportLogList(errors);

		}
		return importLogs;
	}

	private DataImportForm preprocessCreateUpdateEmpDataWS(Company company,
			List<DataImportLogDTO> dataImportLogList,
			List<HashMap<String, String>> inValidData, boolean isTemplateValid) {
		if (!inValidData.isEmpty()) {
			DataImportForm importForm = new DataImportForm();
			importForm.setDataImportLogList(dataImportLogList);
			importForm.setTemplateValid(true);

			return importForm;
		}
		return null;
	}

}
