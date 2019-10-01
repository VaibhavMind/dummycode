package com.payasia.logic.hris.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.dto.DynamicFormRecordDTO;
import com.payasia.common.dto.DynamicFormTableDataForm;
import com.payasia.common.dto.DynamicFormTableRecordDTO;
import com.payasia.common.dto.EmployeeDocumentHistoryDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CalculatoryForm;
import com.payasia.common.form.DataDictionaryForm;
import com.payasia.common.form.DynamicFormTableDocumentDTO;
import com.payasia.common.form.EmployeeDetailForm;
import com.payasia.common.form.EmployeeDocumentForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormulaUtils;
import com.payasia.common.util.HRISUtils;
import com.payasia.common.util.ImageUtils;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeDocumentHistoryDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.HRISChangeRequestDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeDocumentHistory;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.hris.CommonMyProfileDetailsLogic;
import com.payasia.logic.hris.EmpMyProfileDetailsLogic;
import com.payasia.logic.impl.ClaimReportsLogicImpl;

import net.sf.json.JSONObject;

@Component
public class EmpMyProfileDetailsLogicImpl implements EmpMyProfileDetailsLogic {
	
	private static final Logger LOGGER = Logger.getLogger(EmpMyProfileDetailsLogicImpl.class);

	@Resource
	private DynamicFormRecordDAO dynamicFormRecordDAO;

	@Resource
	private DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	@Resource
	private EmployeeDAO employeeDAO;

	@Resource
	GeneralDAO generalDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	DataExportUtils dataExportUtils;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	private DynamicFormDAO dynamicFormDAO;

	@Resource
	private DataDictionaryDAO dataDictionaryDAO;

	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;

	@Resource
	CommonMyProfileDetailsLogic commonMyProfileDetailsLogic;

	@Resource
	EmployeeDocumentHistoryDAO employeeDocumentHistoryDAO;

	@Resource
	FileUtils fileUtils;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	@Qualifier("AWSS3Logic")
	private AWSS3Logic awss3Logic;

	@Resource
	@Qualifier("HRISChangeRequestDAO")
	HRISChangeRequestDAO hrisChangeRequestDAO;

	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Override
	public EmployeeDetailForm getUserDetailsData(Long companyId, Long employeeId) {

		Employee employee = employeeDAO.findById(employeeId, companyId);
		if (employee != null) {
			EmployeeDetailForm employeeDetailForm = new EmployeeDetailForm();
			employeeDetailForm.setEmail(employee.getEmail());
			employeeDetailForm.setEmployeeNumber(employee.getEmployeeNumber());
			employeeDetailForm.setFirstName(employee.getFirstName());
			employeeDetailForm.setMiddleName(employee.getMiddleName());
			employeeDetailForm.setLastName(employee.getLastName());
			employeeDetailForm.setHiredate(
					employee.getHireDate() == null ? "" : DateUtils.timeStampToString(employee.getHireDate()));
			employeeDetailForm.setOriginalhiredate(employee.getOriginalHireDate() == null ? ""
					: DateUtils.timeStampToString(employee.getOriginalHireDate()));
			employeeDetailForm.setConfirmationdate(employee.getConfirmationDate() == null ? ""
					: DateUtils.timeStampToString(employee.getConfirmationDate()));
			employeeDetailForm.setOriginalhiredate(employee.getOriginalHireDate() == null ? ""
					: DateUtils.timeStampToString(employee.getOriginalHireDate()));
			employeeDetailForm.setEmploymentstatus(employee.getEmploymentStatus());
			employeeDetailForm
					.setProfileImg(getEmployeeImage(employeeId, companyId, employeeImageWidth, employeeImageHeight));
			return employeeDetailForm;
		}
		return null;
	}

	@Override
	public DynamicFormRecordDTO getDynamicFormRecordData(DynamicFormDataForm dynamicFormDataForm) {
		Long entityId = getEntityId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		if (dynamicFormDataForm.getFormId().equals("0") && dynamicFormDataForm.getIsDefault()) {
			DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionBySection(dynamicFormDataForm.getCompanyId(),
					entityId, PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
			if (dynamicForm != null) {
				dynamicFormDataForm.setFormId("" + dynamicForm.getId().getFormId());
			}
		}
		DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO.getEmpRecords(dynamicFormDataForm.getEmployeeId(),
				null, Long.parseLong(dynamicFormDataForm.getFormId()), entityId, dynamicFormDataForm.getCompanyId());
		if (dynamicFormRecord != null) {
			return new DynamicFormRecordDTO(dynamicFormRecord);
		}
		return null;
	}

	@Override
	public List<DynamicFormTableRecordDTO> getDynamicFormTableRecordData(
			DynamicFormTableDataForm dynamicFormTableDataForm) {
		String sortBy = dynamicFormTableDataForm.getSortBy();
		if (sortBy != null && !sortBy.equals("")) {
			sortBy = sortBy.toLowerCase().replace("custcol_", "col");
		}
		return commonMyProfileDetailsLogic.getDynamicFormTableRecordValue(dynamicFormTableDataForm,
				dynamicFormTableRecordDAO.getTableRecords(Long.parseLong(dynamicFormTableDataForm.getFormtableid()),
						dynamicFormTableDataForm.getSortOrder(), sortBy));
	}

	@Override
	public String getReferenceValue(DataDictionaryForm dataDictionaryForm) {
		if (dataDictionaryForm != null) {
			if (!StringUtils.isEmpty(dataDictionaryForm.getDataDictionaryId())) {
				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(Long.parseLong(dataDictionaryForm.getDataDictionaryId()));
				if (dataDictionary != null) {
					if (dataDictionary.getFieldType().equals("S")) {
						Employee employee = employeeDAO.findById(dataDictionaryForm.getEmployeeId(),
								dataDictionaryForm.getCompanyId());
						return getStaticData(employee, dataDictionary.getDescription());
					} else {
						Long entityId = getEntityId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
						DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
								dataDictionaryForm.getCompanyId(), entityId, dataDictionary.getFormID());

						if (dynamicForm != null) {
							DynamicFormDataForm dynamicFormDataForm = new DynamicFormDataForm();
							dynamicFormDataForm.setFormId("" + dynamicForm.getId().getFormId());
							dynamicFormDataForm.setEmployeeId(dataDictionaryForm.getEmployeeId());
							dynamicFormDataForm.setCompanyId(dataDictionaryForm.getCompanyId());
							DynamicFormRecordDTO dynamicFormRecordDTO = getDynamicFormRecordData(dynamicFormDataForm);
							if (dynamicFormRecordDTO != null) {
								Tab tab = (Tab) HRISUtils.unmarshal(dynamicForm.getMetaData());
								List<Field> listOfFields = tab.getField();
								for (Field field : listOfFields) {
									if (("" + field.getDictionaryId())
											.equals(dataDictionaryForm.getDataDictionaryId())) {

										String colNumber = PayAsiaStringUtils.getColNumber(field.getName());

										String value = getColValueFile(colNumber, dynamicFormRecordDTO);

										value = getValueByType(value, field.getType());

										return value;
									}
								}
							}
						}
					}
				}
			}
		}
		return "";
	}

	@Override
	public String getCalculatoryValue(CalculatoryForm calculatoryFieldForm) {

		String formulaType = calculatoryFieldForm.getFormulaType();
		calculatoryFieldForm.setFormula(calculatoryFieldForm.getFormula().replaceAll("\"", ""));
		String formulaEx = calculatoryFieldForm.getFormula();

		HashMap<Long, Tab> dataTabMap = new HashMap<>();
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();

		List<Long> dataDictionaries = new ArrayList<Long>();

		if (formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC)
				|| formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_UNDEFINED)) {
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
		List<String> valueList = null;
		DataDictionary dataDictionary = dataDictionaryDAO
				.findById(Long.parseLong(calculatoryFieldForm.getDataDictionaryId()));
		try {
			valueList = getValueForDictionary(calculatoryFieldForm.getEmployeeId(), dataDictionary, dataDictionaries,
					dataTabMap, dynamicFormMap, staticPropMap, calculatoryFieldForm.getCompanyId());
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (valueList != null && !valueList.isEmpty()) {
			if (formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_STRING)) {
				calculatedValue = FormulaUtils.getStringTypeFormulaCalulatedValue(calculatoryFieldForm.getFormula(),
						valueList);
			} else if (formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_NUMERIC)
					|| formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_UNDEFINED)) {
				calculatedValue = FormulaUtils.getNumericTypeFormulaCalculatedValue(calculatoryFieldForm.getFormula(),
						valueList);
			} else if (formulaType.equalsIgnoreCase(PayAsiaConstants.FORMULA_FIELD_TYPE_DATE)) {
				Company companyVO = companyDAO.findById(calculatoryFieldForm.getCompanyId());
				calculatedValue = FormulaUtils.getDateTypeFormulaCalulatedValue(calculatoryFieldForm.getFormula(),
						valueList, companyVO.getDateFormat());
			}
		}
		return calculatedValue;
	}

	@Override
	public HRISChangeRequest getChangesReqId(DataDictionaryForm dataDictionaryForm) {

		if (dataDictionaryForm != null) {
			List<String> hrisStatusList = new ArrayList<>();
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);
			Integer tableId = null;
			if (dataDictionaryForm.getTableSeqId() != null && !dataDictionaryForm.equals("")) {
				tableId = Integer.parseInt(dataDictionaryForm.getTableSeqId());
			}
			HRISChangeRequest hrisChangeRequest = hrisChangeRequestDAO.findByCondition(
					Long.parseLong(dataDictionaryForm.getDataDictionaryId()), tableId,
					dataDictionaryForm.getEmployeeId(), hrisStatusList);
			return hrisChangeRequest;
		}
		return null;
	}

	@Override
	public Map<String, Object> saveUserDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue) {
		Map<String, Object> error = new HashMap<String, Object>();
		Map<String, Boolean> hrisPreferenceMap = commonMyProfileDetailsLogic
				.getHRISCompanyConfig(dynamicFormDataForm.getCompanyId());
		if (hrisPreferenceMap.get("employeeChangeWorkflow")) {
			DynamicForm dynamicForm = commonMyProfileDetailsLogic.getDynamicForm(dynamicFormDataForm);
			if (dynamicFormDataForm.getFormRecordId() == null || dynamicFormDataForm.getFormRecordId().equals("0")) {
				return employeeDetailLogic.saveUserDetails(dynamicFormDataForm, formValue, dynamicForm);
			} else {
				return employeeDetailLogic.updateUserDetails(dynamicFormDataForm, formValue, dynamicForm);
			}
		}else{
			error.put("error", "Workflow not enable");
		}
	return error;
	}

	@Override
	public Map<String, Object> saveTableDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue) {
		Map<String, Object> error = new HashMap<String, Object>();
		Map<String, Boolean> hrisPreferenceMap = commonMyProfileDetailsLogic
				.getHRISCompanyConfig(dynamicFormDataForm.getCompanyId());
		if (hrisPreferenceMap.get("employeeChangeWorkflow")) {
			DynamicForm dynamicForm = commonMyProfileDetailsLogic.getDynamicForm(dynamicFormDataForm);
			return employeeDetailLogic.saveTableDetails(dynamicFormDataForm, formValue, dynamicForm);
		} else {
			error.put("error", "Workflow not enable");
			return error;
		}
	}

	@Override
	public Map<String, Object> saveDocumentDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			MultipartFile files[]) {

		Map<String, Object> error = new HashMap<String, Object>();
		boolean isFileValid = false;
		MultipartFile filesData = null;
		if (files != null && files.length > 0) {
			try {
				filesData = files[0];
				isFileValid = FileUtils.isValidFile(files[0].getBytes(), files[0].getOriginalFilename(),
						PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT,
						PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				error.put("error", "Invalid File");
				return error;
			}
		} else {
			isFileValid = true;
		}
		if (isFileValid) {
			DynamicForm dynamicForm = commonMyProfileDetailsLogic.getDynamicForm(dynamicFormDataForm);
			return employeeDetailLogic.saveDocumentDetails(dynamicFormDataForm, formValue, dynamicForm, filesData);
		} else {
			error.put("error", "Invalid File");
		}
		return error;
	}

	@Override
	public Map<String, Object> deleteTableDetails(DynamicFormDataForm dynamicFormDataForm) {

		Map<String, Object> error = new HashMap<String, Object>();

		Map<String, Boolean> hrisPreferenceMap = commonMyProfileDetailsLogic
				.getHRISCompanyConfig(dynamicFormDataForm.getCompanyId());
		if (hrisPreferenceMap.get("employeeChangeWorkflow")) {
			DynamicForm dynamicForm = commonMyProfileDetailsLogic.getDynamicForm(dynamicFormDataForm);

			return employeeDetailLogic.deleteTableRecord(dynamicForm, dynamicFormDataForm);

		} else {
			error.put("error", "Workflow disable");
		}
		return error;
	}

	@Override
	public EmployeeListFormPage getEmployeeDocHistory(EmployeeDocumentForm employeeDocumentForm) {

		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		List<EmployeeDocumentHistory> employeeDocumentHistoryList = employeeDocumentHistoryDAO.findByCondition(
				Long.parseLong(employeeDocumentForm.getTableId()), Integer.parseInt(employeeDocumentForm.getSeqNo()));
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
	public DynamicFormTableDocumentDTO getDocumentFile(Long companyId, Long employeeId, String fileName) {
		return employeeDetailLogic.downloadEmpDoc(companyId, employeeId, fileName);
	}

	private List<String> getValueForDictionary(Long employeeId, DataDictionary dataDictionary,
			List<Long> dataDictionaries, HashMap<Long, Tab> dataTabMap, HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<String, ColumnPropertyDTO> staticPropMap, Long companyId)
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
					
				} else {
					List<DynamicFormRecord> formulaDynFormRecordList = dynamicFormRecordDAO.findByEntityKey(employeeId,
							dataDictionary.getEntityMaster().getEntityId(), companyId);
					boolean isRecordFound = false;
					if (!formulaDynFormRecordList.isEmpty() && formulaDynFormRecordList != null) {
						for (DynamicFormRecord formulaDynFormRecord : formulaDynFormRecordList) {
							if (formulaValueDTO.getFormId() == formulaDynFormRecord.getForm_ID()) {
								isRecordFound = true;
								String value = getColValueFile(formulaValueDTO.getMethodName(),
										new DynamicFormRecordDTO(formulaDynFormRecord));
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

	private String getStaticData(Employee employee, String description) {
		if (employee != null) {
			switch (description) {
			case "payasia.first.name":
				return employee.getFirstName();
			case "payasia.middle.name":
				return employee.getMiddleName();
			case "payasia.last.name":
				return employee.getLastName();
			case "payasia.employee.personalinfo.email":
				return employee.getEmail();
			case "payasia.employee.personalinfo.hire.date":
				return DateUtils.timeStampToString(employee.getHireDate(), "dd-MM-yyyy");
			case "payasia.employee.personalinfo.original.hire.date":
				return DateUtils.timeStampToString(employee.getOriginalHireDate(), "dd-MM-yyyy");
			case "payasia.employee.personalinfo.confirmation.date":
				return DateUtils.timeStampToString(employee.getConfirmationDate(), "dd-MM-yyyy");
			case "payasia.employee.personalinfo.resignation.date":
				return DateUtils.timeStampToString(employee.getResignationDate(), "dd-MM-yyyy");
			}
		}
		return "";
	}

	private String getValueByType(String value, String type) {
		switch (type.toLowerCase()) {
		case "date":
			return DateUtils.convertDateFormat(value, "dd-MM-yyyy");
		case "codedesc":
			return getDynamicFormFieldRefValue(Long.parseLong(value));

		}
		return value;
	}

	private String getDynamicFormFieldRefValue(Long id) {
		DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO.findById(id);
		if (dynamicFormFieldRefValue != null) {
			return dynamicFormFieldRefValue.getDescription() + "[" + dynamicFormFieldRefValue.getCode() + "]";
		}
		return "";
	}

	private String getColValueFile(String colNumber, DynamicFormRecordDTO dynamicFormRecordDTO) {
		String tableRecordId = null;
		Class<?> dynamicFormRecordClass = dynamicFormRecordDTO.getClass();
		String colMehtodName = "getCustfield_" + colNumber;
		Method dynamicFormRecordMethod;
		try {
			dynamicFormRecordMethod = dynamicFormRecordClass.getMethod(colMehtodName);
			tableRecordId = (String) dynamicFormRecordMethod.invoke(dynamicFormRecordDTO);
		} catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return tableRecordId;
	}

	private Long getEntityId(String entityName) {
		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityMaster.getEntityName().equalsIgnoreCase(entityName)) {
				return entityMaster.getEntityId();
			}
		}
		return null;
	}

	private byte[] getEmployeeImage(Long employeeId, Long companyId, int imageWidth, int imageHeight) {

		byte[] originalByteFile = null;
		String defaultImagePath = "";

		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.EMPLOYEE_PHOTO_DIRECTORY_NAME, null, null, String.valueOf(employeeId), null,
				PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

		File file = new File(filePath);
		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				try {
					originalByteFile = org.apache.commons.io.IOUtils
							.toByteArray(awss3Logic.readS3ObjectAsStream(filePath));
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
				if (originalByteFile == null || originalByteFile.length == 0) {
					File defaultImageFile = new File(defaultImagePath);
					originalByteFile = PasswordUtils.getBytesFromFile(defaultImageFile);
				}
			} else {
				if (file.exists()) {
					originalByteFile = Files.readAllBytes(file.toPath());
				} else {
					File defaultImageFile = new File(defaultImagePath);
					originalByteFile = PasswordUtils.getBytesFromFile(defaultImageFile);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		byte[] resizedImageInByte = null;
		if (originalByteFile != null) {
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
		}

		return resizedImageInByte;
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
}
