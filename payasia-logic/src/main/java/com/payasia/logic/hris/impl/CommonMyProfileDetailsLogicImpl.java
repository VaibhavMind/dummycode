package com.payasia.logic.hris.impl;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.dto.DynamicFormDetailsDTO;
import com.payasia.common.dto.DynamicFormTableDataForm;
import com.payasia.common.dto.DynamicFormTableRecordDTO;
import com.payasia.common.dto.EmployeeListDTO;
import com.payasia.common.dto.SectionDetailsDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DataDictionaryForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormulaUtils;
import com.payasia.common.util.HRISUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyGroupDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.DynamicFormTableRecordDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.HRISChangeRequestDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormFieldRefValue;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.DynamicFormTableRecord;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.logic.hris.CommonMyProfileDetailsLogic;

@Component
public class CommonMyProfileDetailsLogicImpl implements CommonMyProfileDetailsLogic {

	@Resource
	GeneralLogic generalLogic;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	DataExportUtils dataExportUtils;

	@Resource
	private DataDictionaryDAO dataDictionaryDAO;

	@Resource
	private DynamicFormDAO dynamicFormDAO;

	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;

	@Resource
	MultilingualLogic multilingualLogic;

	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	@Resource
	GeneralDAO generalDAO;

	@Resource
	@Qualifier("HRISChangeRequestDAO")
	HRISChangeRequestDAO hrisChangeRequestDAO;

	@Resource
	DynamicFormTableRecordDAO dynamicFormTableRecordDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	CompanyDAO companyDAO;
	
	@Resource
	CompanyGroupDAO companyGroupDAO;

	@Resource
	@Qualifier("HRISPreferenceDAO")
	HRISPreferenceDAO hrisPreferenceDAO;

	@Override
	public Map<String, Boolean> getHRISCompanyConfig(Long companyId) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		Map<String, Boolean> hrisPreferenceMap = new HashMap<String, Boolean>();
		if (hrisPreferenceVO == null) {
			hrisPreferenceMap.put("employeeChangeWorkflow", false);
			hrisPreferenceMap.put("allowEmployeeUploadDoc", false);
		} else {
			hrisPreferenceMap.put("employeeChangeWorkflow", hrisPreferenceVO.isEnableEmployeeChangeWorkflow());
			hrisPreferenceMap.put("allowEmployeeUploadDoc", hrisPreferenceVO.isAllowEmployeeUploadDoc());
		}
		return hrisPreferenceMap;
	}

	public List<SectionDetailsDTO> getSectionDetails(Long companyId, Long employeeId) {

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO.findAllSection(companyId);

		List<SectionDetailsDTO> sectionDetailsList = new ArrayList<SectionDetailsDTO>();

		Long entityId = getEntityId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIdList = generalLogic.getEmployeeAuthorizedSectionIdList(employeeId, companyId, entityId);

		SectionDetailsDTO sectionDetailsDTO = new SectionDetailsDTO();
		sectionDetailsDTO.setFormId(String.valueOf(0));
		sectionDetailsDTO.setSectionName(PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
		sectionDetailsDTO.setIsDefault("true");
		sectionDetailsList.add(sectionDetailsDTO);

		if (dataDictionaryList != null && !dataDictionaryList.isEmpty()) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				if (formIdList != null && formIdList.contains(dataDictionary.getFormID())) {
					sectionDetailsDTO = new SectionDetailsDTO();
					sectionDetailsDTO.setFormId(String.valueOf(dataDictionary.getFormID()));
					sectionDetailsDTO.setSectionName(dataDictionary.getLabel());
					sectionDetailsDTO.setIsDefault("false");
					sectionDetailsList.add(sectionDetailsDTO);
				}
			}
		}
		return sectionDetailsList;
	}

	@Override
	public DynamicFormDetailsDTO getDynamicForm(Long companyId, DynamicFormDataForm dynamicFormDataForm,
			Long languageId) {

		dynamicFormDataForm.setCompanyId(companyId);
		DynamicFormDetailsDTO dynamicFormDetailsDTO = null;
		DynamicForm dynamicForm = getDynamicForm(dynamicFormDataForm);
		if (dynamicForm != null) {
			Long entityId = dynamicForm.getId().getEntity_ID();
			dynamicFormDetailsDTO = new DynamicFormDetailsDTO();
			dynamicFormDetailsDTO.setFormId(String.valueOf(dynamicForm.getId().getFormId()));
			dynamicFormDetailsDTO.setEntityID(String.valueOf(entityId));

			dynamicFormDetailsDTO.setMetadata(processMetaData(dynamicForm.getMetaData(), languageId, companyId,
					entityId, dynamicForm.getId().getFormId()));
		}

		return dynamicFormDetailsDTO;
	}

	@Override
	public DynamicForm getDynamicForm(DynamicFormDataForm dynamicFormDataForm) {

		Long entityId = getEntityId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		DynamicForm dynamicForm = null;
		if (dynamicFormDataForm.getFormId().equals("0") && dynamicFormDataForm.getIsDefault()) {
			dynamicForm = dynamicFormDAO.findMaxVersionBySection(dynamicFormDataForm.getCompanyId(), entityId,
					PayAsiaConstants.EMPLOYEE_BASIC_TAB_NAME);
		} else {
			dynamicForm = dynamicFormDAO.findMaxVersionByFormId(dynamicFormDataForm.getCompanyId(), entityId,
					Long.parseLong(dynamicFormDataForm.getFormId()));
		}
		return dynamicForm;
	}

	@Override
	public List<CodeDescDTO> getCodeDesc(Long companyId, DataDictionaryForm dataDictionaryForm) {

		if (dataDictionaryForm != null) {
			if (!StringUtils.isEmpty(dataDictionaryForm.getDataDictionaryId())) {
				List<DynamicFormFieldRefValue> dynamicFormFieldRefValueList = dynamicFormFieldRefValueDAO
						.findByDataDictionayId(Long.parseLong(dataDictionaryForm.getDataDictionaryId()), companyId);
				List<CodeDescDTO> codeDescDTOList = null;
				if (dynamicFormFieldRefValueList != null && !dynamicFormFieldRefValueList.isEmpty()) {
					codeDescDTOList = new ArrayList<CodeDescDTO>();
					for (DynamicFormFieldRefValue dynamicFormFieldRefValue : dynamicFormFieldRefValueList) {
						CodeDescDTO codeDescDTO = new CodeDescDTO();
						codeDescDTO.setCodeDescId(dynamicFormFieldRefValue.getFieldRefValueId());
						codeDescDTO.setCode(dynamicFormFieldRefValue.getCode());
						codeDescDTO.setDescription(dynamicFormFieldRefValue.getDescription());
						codeDescDTOList.add(codeDescDTO);
					}
					return codeDescDTOList;
				}
			}
		}
		return null;
	}

	@Override
	public List<EmployeeListDTO> getEmployeeList(Long companyId) {

		CompanyGroup companyGroup = companyGroupDAO.findByCompanyId(companyId);
		
		List<Employee> employeeList = employeeDAO.getGroupCompanyEmployee(companyGroup.getGroupId());
		List<EmployeeListDTO> employeeListDTOList = null;
		if (employeeList != null && !employeeList.isEmpty()) {
			employeeListDTOList = new ArrayList<EmployeeListDTO>();
			for (Employee employee : employeeList) {
				if (employee.getEmail() != null && !employee.getEmail().equals("")) {
					EmployeeListDTO employeeListDTO = new EmployeeListDTO();
					
					StringBuilder sb= new StringBuilder();
					sb.append(employee.getFirstName()==null?"":employee.getFirstName());
					sb.append(employee.getMiddleName()==null?" ":employee.getMiddleName());
					sb.append(employee.getLastName()==null?" ":employee.getLastName());
					sb.append(" [ "+employee.getEmployeeNumber()+" ]");
					
					employeeListDTO.setEmployeeRefName(sb.toString());
					employeeListDTO.setEmployeeId("" + employee.getEmployeeId());
					employeeListDTOList.add(employeeListDTO);
				}
			}
			return employeeListDTOList;
		}
		return null;
	}

	@Override
	public List<DynamicFormTableRecordDTO> getDynamicFormTableRecordValue(
			DynamicFormTableDataForm dynamicFormTableDataForm,
			List<DynamicFormTableRecord> dynamicFormTableRecordList) {

		List<DynamicFormTableRecordDTO> dynamicFormRecordDTOList = new ArrayList<DynamicFormTableRecordDTO>();
		if (dynamicFormTableRecordList != null && !dynamicFormTableRecordList.isEmpty()) {

			for (DynamicFormTableRecord dynamicFormTableRecord : dynamicFormTableRecordList) {

				DynamicFormTableRecordDTO dynamicFormTableRecordDTO = new DynamicFormTableRecordDTO(
						dynamicFormTableRecord);
				dynamicFormRecordDTOList.add(dynamicFormTableRecordDTO);
			}
			changeDynamicFormTableRecordValue(dynamicFormTableDataForm, dynamicFormRecordDTOList);

		}
		return dynamicFormRecordDTOList;
	}

	public void changeDynamicFormTableRecordValue(DynamicFormTableDataForm dynamicFormTableDataForm,
			List<DynamicFormTableRecordDTO> dynamicFormTableRecordDTOList) {

		Company cmp = companyDAO.findById(dynamicFormTableDataForm.getCompanyId());
		DynamicFormDataForm dynamicFormDataForm = new DynamicFormDataForm();
		dynamicFormDataForm.setCompanyId(dynamicFormTableDataForm.getCompanyId());
		dynamicFormDataForm.setFormId(dynamicFormTableDataForm.getFormId());
		dynamicFormDataForm.setIsDefault(dynamicFormTableDataForm.getFormId().equals("0") ? true : false);

		DynamicForm dynamicForm = getDynamicForm(dynamicFormDataForm);

		String companyDateFormat = cmp.getDateFormat();

		List<String> hrisStatusList = new ArrayList<>();
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
		hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);

		Map<Long, DynamicForm> formIdMap = new HashMap<Long, DynamicForm>();
		formIdMap.put(dynamicForm.getId().getFormId(), dynamicForm);
		HashMap<Long, Tab> dataTabMap = new HashMap<>();
		HashMap<Long, DynamicForm> dynamicFormMap = new HashMap<>();
		HashMap<String, ColumnPropertyDTO> staticPropMap = new HashMap<>();

		Tab tab = (Tab) HRISUtils.unmarshal(dynamicForm.getMetaData());

		Field fieldObj = null;
		for (Field field : tab.getField()) {
			if (field.getDictionaryId() == Long.parseLong(dynamicFormTableDataForm.getDataDictionaryId())) {
				fieldObj = field;
			}
		}
		if (fieldObj != null) {
			for (int count = 0; count < dynamicFormTableRecordDTOList.size(); count++) {
				DynamicFormTableRecordDTO dynamicFormTableRecordDTO = dynamicFormTableRecordDTOList.get(count);
				if (fieldObj.getType().equalsIgnoreCase(PayAsiaConstants.DOCUMENT_FIELD_TYPE)) {
					if ("false".equalsIgnoreCase(dynamicFormTableRecordDTO.getCustcol_8())) {
						dynamicFormTableRecordDTOList.remove(dynamicFormTableRecordDTO);
						continue;
					}
				}

				for (Column column : fieldObj.getColumn()) {
					String colName = column.getName();
					String colType = column.getType();
					Long colDictId = column.getDictionaryId();
					String fieldNameCount = colName.substring(colName.lastIndexOf('_') + 1, colName.length());

					try {
						Class<?> dynamicTableClass = dynamicFormTableRecordDTO.getClass();
						String dynamicFormTableRecordDTOMethodName = PayAsiaConstants.GET_CUST_COL + fieldNameCount;
						Method dynamicFormTableRecordDTOMethod = dynamicTableClass
								.getMethod(dynamicFormTableRecordDTOMethodName);
						String dynamicFormTableRecordValue = (String) dynamicFormTableRecordDTOMethod
								.invoke(dynamicFormTableRecordDTO);

						switch (colType) {
						case PayAsiaConstants.FIELD_TYPE_DATE:
							if (dynamicFormTableRecordValue != null && !dynamicFormTableRecordValue.equals("")) {
								dynamicFormTableRecordValue = DateUtils
										.convertDateToSpecificFormat(dynamicFormTableRecordValue, companyDateFormat);
							}
							break;
						case PayAsiaConstants.CODEDESC_FIELD_TYPE:
							if (dynamicFormTableRecordValue != null && !dynamicFormTableRecordValue.equals("")) {

								DynamicFormFieldRefValue dynamicFormFieldRefValue = dynamicFormFieldRefValueDAO
										.findById(Long.parseLong(dynamicFormTableRecordValue));
								if (dynamicFormFieldRefValue != null) {
									dynamicFormTableRecordValue = dynamicFormFieldRefValue.getCode();
								} else {
									dynamicFormTableRecordValue = "";
								}
							}
							break;
						case PayAsiaConstants.EMPLOYEE_lIST_REFERENCE:
							// not working
							break;
						case PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD:
							DataDictionary dataDictionary = dataDictionaryDAO.findById(colDictId);
							DynamicForm dynaForm = null;
							if (formIdMap.containsKey(dataDictionary.getFormID())) {
								dynaForm = formIdMap.get(dataDictionary.getFormID());
							} else {
								dynaForm = dynamicFormDAO.findMaxVersionByFormId(
										dynamicFormTableDataForm.getCompanyId(),
										dataDictionary.getEntityMaster().getEntityId(), dataDictionary.getFormID());
								formIdMap.put(dynaForm.getId().getFormId(), dynaForm);
								dynamicFormMap.put(dynaForm.getId().getFormId(), dynaForm);
							}
							if (dynaForm != null) {
								try {
									dynamicFormTableRecordValue = addTableFormulaValueInExistingDynXML(dynamicFormMap,
											dataTabMap, staticPropMap, dynaForm.getMetaData(),
											dynamicFormTableDataForm.getEmployeeId(),
											dynamicFormTableDataForm.getCompanyId(), true,
											Long.parseLong(dynamicFormTableRecordDTO.getTableId()),
											Integer.parseInt(dynamicFormTableRecordDTO.getSeqNo()),
											dataDictionary.getDataDictionaryId());
								} catch (XMLStreamException e) {
									throw new PayAsiaSystemException(e.getMessage(), e);
								}
							}

							break;
						case PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD:
							DataDictionary dataDictionaryRef = dataDictionaryDAO.findById(colDictId);
							DynamicForm dynamicFormRef = null;
							if (formIdMap.containsKey(dataDictionaryRef.getFormID())) {
								dynamicFormRef = formIdMap.get(dataDictionaryRef.getFormID());
							} else {
								dynamicFormRef = dynamicFormDAO.findMaxVersionByFormId(
										dynamicFormTableDataForm.getCompanyId(),
										dataDictionaryRef.getEntityMaster().getEntityId(),
										dataDictionaryRef.getFormID());
								formIdMap.put(dynamicFormRef.getId().getFormId(), dynamicForm);
								dynamicFormMap.put(dynamicFormRef.getId().getFormId(), dynamicFormRef);
							}
							if (dynamicFormRef != null) {
								try {
									try {
										dynamicFormTableRecordValue = addTableReferenceValueInExistingDynXML(
												dynamicFormMap, dynamicFormRef.getMetaData(),
												dynamicFormTableDataForm.getEmployeeId(),
												dynamicFormTableDataForm.getCompanyId(), companyDateFormat,
												Long.parseLong(dynamicFormTableRecordDTO.getTableId()),
												Integer.parseInt(dynamicFormTableRecordDTO.getSeqNo()),
												dataDictionaryRef.getDataDictionaryId());
									} catch (SAXException e) {
										throw new PayAsiaSystemException(e.getMessage(), e);
									}
								} catch (XMLStreamException e) {
									throw new PayAsiaSystemException(e.getMessage(), e);
								}
							}
							break;
						}

						if (fieldObj.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)
								&& !colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)
								&& !colType.equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_REFERENCE_FIELD)) {

							HRISChangeRequest hrisChangeRequest = hrisChangeRequestDAO.findByConditionTableSeq(
									colDictId, dynamicFormTableDataForm.getEmployeeId(), hrisStatusList,
									Integer.parseInt(dynamicFormTableRecordDTO.getSeqNo()));
							if (hrisChangeRequest != null) {
								DynamicForm dynamicFormTmp = null;
								if (formIdMap.containsKey(hrisChangeRequest.getDataDictionary().getFormID())) {
									dynamicFormTmp = formIdMap.get(hrisChangeRequest.getDataDictionary().getFormID());
								} else {
									dynamicFormTmp = dynamicFormDAO.findMaxVersionByFormId(
											dynamicFormTableDataForm.getCompanyId(),
											hrisChangeRequest.getDataDictionary().getEntityMaster().getEntityId(),
											hrisChangeRequest.getDataDictionary().getFormID());
									formIdMap.put(dynamicFormTmp.getId().getFormId(), dynamicForm);
								}

								EmployeeListFormPage employeeListFormPage = generalLogic
										.getEmployeeHRISChangeRequestData(hrisChangeRequest.getHrisChangeRequestId(),
												dynamicFormTableDataForm.getCompanyId(), dynamicFormTmp.getMetaData(),
												null);

								dynamicFormTableRecordValue = employeeListFormPage.getNewValue();

								dynamicFormTableRecordDTO.getApprovalReq()
										.add(PayAsiaConstants.CUST_COL + fieldNameCount);
							}
						}
						String dynamicFormTableRecordDTOSetMethodName = PayAsiaConstants.SET_CUST_COL + fieldNameCount;
						Method dynamicFormTableRecordDTOSetMethod = dynamicTableClass
								.getMethod(dynamicFormTableRecordDTOSetMethodName, String.class);

						dynamicFormTableRecordDTOSetMethod.invoke(dynamicFormTableRecordDTO,
								dynamicFormTableRecordValue);
					} catch (NoSuchMethodException noSuchMethodException) {
						throw new PayAsiaSystemException(noSuchMethodException.getMessage(), noSuchMethodException);
					} catch (IllegalArgumentException illegalArgumentException) {
						throw new PayAsiaSystemException(illegalArgumentException.getMessage(),
								illegalArgumentException);
					} catch (IllegalAccessException illegalAccessException) {
						throw new PayAsiaSystemException(illegalAccessException.getMessage(), illegalAccessException);
					} catch (InvocationTargetException invocationTargetException) {
						throw new PayAsiaSystemException(invocationTargetException.getMessage(),
								invocationTargetException);
					}
				}
			}
		}
	}

	private String addTableReferenceValueInExistingDynXML(HashMap<Long, DynamicForm> dynamicFormMap, String XML,
			Long employeeId, Long companyId, String companyDateFormat, Long tableId, Integer seqNo, Long fieldDictId)
			throws SAXException, XMLStreamException {

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}
		final StringReader xmlReader = new StringReader(XML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}
		Employee employeeVO = employeeDAO.findById(employeeId);
		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
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
									throw new PayAsiaSystemException(e.getMessage(), e);
								} catch (SecurityException e) {
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
									throw new PayAsiaSystemException(e.getMessage(), e);
								} catch (IllegalArgumentException e) {
									throw new PayAsiaSystemException(e.getMessage(), e);
								} catch (InvocationTargetException e) {
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
										throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
									} catch (SAXException sAXException) {
										throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
									}
									final StringReader xmlRefReader = new StringReader(dynamicForm.getMetaData());
									Source xmlRefSource = null;
									try {
										xmlRefSource = XMLUtil.getSAXSource(xmlRefReader);
									} catch (SAXException | ParserConfigurationException e1) {
										throw new PayAsiaSystemException(e1.getMessage(), e1);
									}

									Tab tabRef = null;
									try {
										tabRef = (Tab) unmarshallerRef.unmarshal(xmlRefSource);
									} catch (JAXBException jAXBException) {
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
															throw new PayAsiaSystemException(e.getMessage(), e);
														} catch (IllegalArgumentException e) {
															throw new PayAsiaSystemException(e.getMessage(), e);
														} catch (InvocationTargetException e) {
															throw new PayAsiaSystemException(e.getMessage(), e);
														}
													} catch (NoSuchMethodException e) {
														throw new PayAsiaSystemException(e.getMessage(), e);
													} catch (SecurityException e) {
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
														throw new PayAsiaSystemException(e.getMessage(), e);
													} catch (SecurityException e) {
														throw new PayAsiaSystemException(e.getMessage(), e);
													}

													try {
														fieldRValue = (String) dynamicFormTableRecordMethod
																.invoke(refDynFormTableRecord);
													} catch (IllegalAccessException e) {
														throw new PayAsiaSystemException(e.getMessage(), e);
													} catch (IllegalArgumentException e) {
														throw new PayAsiaSystemException(e.getMessage(), e);
													} catch (InvocationTargetException e) {
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

	private String processMetaData(String metadata, Long languageId, Long companyID, Long entityId, Long formId) {

		Tab tab = (Tab) HRISUtils.unmarshal(metadata);

		multilingualLogic.convertLabelsToSpecificLanguage(tab, languageId, companyID, entityId, formId);

		return ResponseDataConverter.getObjectToString(tab);
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

	private String addTableFormulaValueInExistingDynXML(HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<Long, Tab> dataTabMap, HashMap<String, ColumnPropertyDTO> staticPropMap, String XML,
			Long employeeId, Long companyId, boolean isTableField, Long tableId, Integer seqNo, Long fieldDictId)
			throws XMLStreamException {

		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException jAXBException) {
			throw new PayAsiaSystemException(jAXBException.getMessage(), jAXBException);
		} catch (SAXException sAXException) {
			throw new PayAsiaSystemException(sAXException.getMessage(), sAXException);
		}
		final StringReader xmlReader = new StringReader(XML);
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {
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
									throw new PayAsiaSystemException(e.getMessage(), e);
								} catch (IllegalAccessException e) {
									throw new PayAsiaSystemException(e.getMessage(), e);
								} catch (InvocationTargetException e) {
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

	private String getFormulaFieldCalculatedValue(HashMap<Long, DynamicForm> dynamicFormMap,
			HashMap<Long, Tab> dataTabMap, HashMap<String, ColumnPropertyDTO> staticPropMap, String formula,
			String formulaType, DataDictionary dataDictionary, Long employeeId, Long companyId, Long tableId,
			Integer seqNo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		List<Long> dataDictionaries = new ArrayList<Long>();
		String formulaEx = formula;

		if (!formulaType.equals("") || "undefined".equalsIgnoreCase(formulaType)) {
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
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException(e.getMessage(), e);
		}
		return tableRecordId;
	}

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
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

		return tableRecordId;

	}
}
