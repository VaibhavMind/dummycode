package com.payasia.logic.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.KeyPayIntPayRunDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeHistoryDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.IntegrationMasterDAO;
import com.payasia.dao.KeyPayIntLeaveApplicationDAO;
import com.payasia.dao.KeyPayIntPayRunDAO;
import com.payasia.dao.KeyPayIntPayRunDetailDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.LeaveSessionMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeTypeHistory;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.IntegrationMaster;
import com.payasia.dao.bean.KeyPayIntLeaveApplication;
import com.payasia.dao.bean.KeyPayIntPayRun;
import com.payasia.dao.bean.KeyPayIntPayRunDetail;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeaveSessionMaster;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.KeyPayIntLogic;

@Component
public class KeyPayIntLogicImpl implements KeyPayIntLogic {
	/** The logger. */
	Logger LOGGER = LoggerFactory.getLogger(KeyPayIntLogicImpl.class);

	@Resource
	private KeyPayIntLeaveApplicationDAO keyPayIntLeaveApplicationDAO;
	@Resource
	private LeaveApplicationDAO leaveApplicationDAO;
	@Resource
	private KeyPayIntPayRunDAO keyPayIntPayRunDAO;
	@Resource
	private KeyPayIntPayRunDetailDAO keyPayIntPayRunDetailDAO;
	@Resource
	private EmployeeDAO employeeDAO;
	@Resource
	private GeneralLogic generalLogic;
	@Resource
	private DataDictionaryDAO dataDictionaryDAO;
	@Resource
	private GeneralDAO generalDAO;
	@Resource
	private DynamicFormDAO dynamicFormDAO;
	@Resource
	private DataExportUtils dataExportUtils;
	@Resource
	private HRISPreferenceDAO hrisPreferenceDAO;
	@Resource
	private CompanyDAO companyDAO;
	@Resource
	private EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;
	@Resource
	private EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;
	@Resource
	private AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	private EmployeeLeaveSchemeTypeHistoryDAO employeeLeaveSchemeTypeHistoryDAO;
	@Resource
	private LeavePreferenceDAO leavePreferenceDAO;
	@Resource
	LeaveSessionMasterDAO leaveSessionMasterDAO;
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;
	@Resource
	IntegrationMasterDAO integrationMasterDAO;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.mind.proxy.enabled.for.dev']}")
	private boolean PAYASIA_MIND_PROXY_ENABLED_FOR_DEV;

	@Value("#{payasiaptProperties['payasia.mind.proxy.hostname']}")
	private String PAYASIA_MIND_PROXY_HOSTNAME;

	@Value("#{payasiaptProperties['payasia.mind.proxy.port']}")
	private int PAYASIA_MIND_PROXY_PORT;

	@Override
	public void updateEmployeeExternalId(Long companyId, String baseURL,
			String apiKey) {
		Company companyVO = companyDAO.findById(companyId);

		List<String> employeeNumberList = new ArrayList<String>();
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO
				.findByCompanyId(companyId);
		if (hrisPreferenceVO != null
				&& hrisPreferenceVO.getExternalId() != null) {
			List<Object[]> keyPayEmpExternalIdList = getKeyPayExternalIdList(
					hrisPreferenceVO.getExternalId(), companyId,
					companyVO.getDateFormat(), null);
			for (Object[] extIdObj : keyPayEmpExternalIdList) {
				// Get employees whose external Id is not updated
				if (extIdObj != null
						&& extIdObj[1] != null
						&& (extIdObj[0] == "null" || extIdObj[0] == null || StringUtils
								.isBlank(String.valueOf(extIdObj[0])))) {
					employeeNumberList.add(String.valueOf(extIdObj[1])
							.toUpperCase());
				}
			}
		}
		if (!employeeNumberList.isEmpty()) {
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			setInternalProxy(requestFactory);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			String url = baseURL + "/employee/unstructured";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Authorization", "Basic " + apiKey);

			Map<String, String> params = new HashMap<String, String>();
			HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);

			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			ResponseEntity<String> response = null;
			try {
				response = restTemplate.exchange(url, HttpMethod.GET, entity,
						String.class);

			} catch (HttpClientErrorException httpClientErrorException) {
				LOGGER.error(httpClientErrorException.getMessage(),
						httpClientErrorException);
			}

			try {
				if (response != null) {
					// Get All Employees By company
					Map<String, Employee> empNumEmployeeMap = new HashMap<String, Employee>();
					List<Employee> employeeVOList = employeeDAO
							.findByCompany(companyId);
					for (Employee employee : employeeVOList) {
						// if (employee.isStatus()) {
						empNumEmployeeMap.put(employee.getEmployeeNumber()
								.toUpperCase(), employee);
						// }
					}

					JSONArray jsonArray = new JSONArray(response.getBody());
					for (int count = 0; count < jsonArray.length(); count++) {
						JSONObject jsonObj = (JSONObject) jsonArray.get(count);
						String keyPayIntId = jsonObj.getString("id");
						String employeeNumber = jsonObj.getString("externalId");
						if (hrisPreferenceVO != null
								&& hrisPreferenceVO.getExternalId() != null) {
							if (employeeNumberList.contains(employeeNumber
									.toUpperCase())) {
								DataDictionary dataDictionary = hrisPreferenceVO
										.getExternalId();
								DynamicForm dynamicForm = dynamicFormDAO
										.findMaxVersionByFormId(companyId,
												dataDictionary
														.getEntityMaster()
														.getEntityId(),
												dataDictionary.getFormID());
								if (dynamicForm == null) {
									continue;
								}
								Field externalIdField = getDynamicFormField(
										dynamicForm.getMetaData(),
										dataDictionary.getDataDictionaryId());
								if (externalIdField == null) {
									continue;
								}
								DynamicFormRecord dynamicFormRecord = dynamicFormRecordDAO
										.getEmpRecords(
												empNumEmployeeMap.get(
														employeeNumber
																.toUpperCase())
														.getEmployeeId(),
												dynamicForm.getId()
														.getVersion(),
												dynamicForm.getId().getFormId(),
												dynamicForm.getId()
														.getEntity_ID(),
												companyId);
								if (dynamicFormRecord == null) {
									continue;
								}
								if (!externalIdField
										.getType()
										.equalsIgnoreCase(
												PayAsiaConstants.TABLE_FIELD_TYPE)) {
									String methodName = "col"
											+ externalIdField
													.getName()
													.substring(
															externalIdField
																	.getName()
																	.lastIndexOf(
																			'_') + 1,
															externalIdField
																	.getName()
																	.length());

									dynamicFormRecordDAO
											.updateEmployeeExternalId(
													companyId,
													empNumEmployeeMap
															.get(employeeNumber
																	.toUpperCase())
															.getEmployeeId(),
													dynamicForm.getId()
															.getEntity_ID(),
													dynamicForm.getId()
															.getFormId(),
													dynamicFormRecord
															.getRecordId(),
													methodName, keyPayIntId);
								}
							}
						}
					}
				}
			} catch (JSONException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private Field getDynamicFormField(String XML, Long dataDictionaryId) {
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
			if (field.getDictionaryId().equals(dataDictionaryId)) {
				return field;
			}
		}
		return null;
	}

	private void updateKeyPayIntLeaveAppTable(Long companyId) {
		// Get Last(Max) Leave Application Id : from upto this max Leave
		// Application Id, Leave Applications are moved
		// to KeyPayIntLeaveApplication table
		Long maxLeaveApplicationId = keyPayIntLeaveApplicationDAO
				.getMaxApprovedLeaveAppId(companyId);
		if (maxLeaveApplicationId == null) {
			maxLeaveApplicationId = 0l;
		}
		// maxLeaveApplicationId = 62409l;

		// Get Leave Approved and Leave Cancel applications (i.e. those
		// applications are not still moved to KeyPayIntLeaveApplication table)
		List<LeaveApplication> approvedLeaveForKeyPayInt = leaveApplicationDAO
				.getApprovedNCancelLeaveForKeyPayInt(maxLeaveApplicationId,
						companyId, PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		for (LeaveApplication leaveApplication : approvedLeaveForKeyPayInt) {
			KeyPayIntLeaveApplication keyPayIntLeaveApplication = new KeyPayIntLeaveApplication();
			keyPayIntLeaveApplication.setLeaveApplication(leaveApplication);
			keyPayIntLeaveApplication.setCompany(leaveApplication.getCompany());
			keyPayIntLeaveApplication.setEmployeeNumber(leaveApplication
					.getEmployee().getEmployeeNumber());
			keyPayIntLeaveApplication.setStartDate(leaveApplication
					.getStartDate());
			keyPayIntLeaveApplication.setEndDate(leaveApplication.getEndDate());
			keyPayIntLeaveApplication.setHours(new BigDecimal(leaveApplication
					.getTotalDays()));
			keyPayIntLeaveApplication.setLeaveTypeName(leaveApplication
					.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveTypeMaster().getLeaveTypeName());
			keyPayIntLeaveApplication.setRemarks(leaveApplication.getReason());
			if (leaveApplication.getLeaveCancelApplication() == null) {
				keyPayIntLeaveApplication
						.setLeaveStatus(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			} else {
				keyPayIntLeaveApplication
						.setLeaveStatus(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
				keyPayIntLeaveApplication
						.setCancelLeaveApplicationId(leaveApplication
								.getLeaveCancelApplication()
								.getLeaveApplicationId());
			}
			keyPayIntLeaveApplication
					.setSyncStatus(PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_SYNC_STATUS_UNPROCESS);
			keyPayIntLeaveApplicationDAO.save(keyPayIntLeaveApplication);
		}
	}

	@Override
	public void sendApprovedLeaveApp(Long companyId,
			Map<String, Long> leaveCategoryMap, String baseURL, String apiKey) {
		// move Leave Applications(Approved and cancel leave) to
		// KeyPayIntLeaveApplication table
		// updateKeyPayIntLeaveAppTable(companyId);

		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

		setInternalProxy(requestFactory);

		Company companyVO = companyDAO.findById(companyId);

		// Find All Leave which is not send(Sync) to KeyPay
		List<KeyPayIntLeaveApplication> keyPayIntLeaveAppList = keyPayIntLeaveApplicationDAO
				.findByCondition(
						PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_SYNC_STATUS_SUCCESS,
						PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED,
						companyId);

		Map<String, String> empNumExternalIdMap = new HashMap<String, String>();
		if (!keyPayIntLeaveAppList.isEmpty()) {
			HRISPreference hrisPreferenceVO = hrisPreferenceDAO
					.findByCompanyId(companyId);
			if (hrisPreferenceVO != null
					&& hrisPreferenceVO.getExternalId() != null) {
				List<Object[]> keyPayEmpExternalIdList = getKeyPayExternalIdList(
						hrisPreferenceVO.getExternalId(), companyId,
						companyVO.getDateFormat(), null);
				for (Object[] extIdObj : keyPayEmpExternalIdList) {
					if (extIdObj != null && extIdObj[0] != null
							&& extIdObj[1] != null) {
						empNumExternalIdMap.put(String.valueOf(extIdObj[1]),
								String.valueOf(extIdObj[0]));
					}
				}
			}

			for (KeyPayIntLeaveApplication keyPayIntLeaveApplication : keyPayIntLeaveAppList) {
				if (empNumExternalIdMap.get(keyPayIntLeaveApplication
						.getEmployeeNumber()) == null) {
					continue;
				}
				RestTemplate restTemplate = new RestTemplate(requestFactory);
				String url = baseURL
						+ "/employee/"
						+ empNumExternalIdMap.get(keyPayIntLeaveApplication
								.getEmployeeNumber()) + "/leaverequest";

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.add("Authorization", "Basic " + apiKey);

				// create request body
				JSONObject request = new JSONObject();
				try {
					request.put("EmployeeId", empNumExternalIdMap
							.get(keyPayIntLeaveApplication.getEmployeeNumber()));
					request.put("FromDate", DateUtils.timeStampToString(
							keyPayIntLeaveApplication.getStartDate(),
							PayAsiaConstants.DATE_FORMAT_YYYY_MM_DD));
					request.put("ToDate", DateUtils.timeStampToString(
							keyPayIntLeaveApplication.getEndDate(),
							PayAsiaConstants.DATE_FORMAT_YYYY_MM_DD));
					request.put("Hours", keyPayIntLeaveApplication.getHours());
					request.put("LeaveCategoryId", leaveCategoryMap
							.get(keyPayIntLeaveApplication.getLeaveTypeName()));

					if (StringUtils.isNotBlank(keyPayIntLeaveApplication
							.getRemarks())
							&& keyPayIntLeaveApplication.getRemarks().length() > 250) {
						request.put("Notes", keyPayIntLeaveApplication
								.getRemarks().substring(0, 250));
					} else {
						request.put("Notes",
								keyPayIntLeaveApplication.getRemarks());
					}

					request.put("AutomaticallyApprove", true);
				} catch (JSONException e) {
					LOGGER.error(e.getMessage(), e);
				}
				HttpEntity<String> entity = new HttpEntity<String>(
						request.toString(), headers);

				restTemplate.getMessageConverters().add(
						new StringHttpMessageConverter());

				JSONObject jsonObj;
				ResponseEntity<String> response1 =null;
				try {
					LOGGER.info("Request: "+request.toString());
					 response1 = restTemplate.exchange(
							url, HttpMethod.POST, entity, String.class);
                      LOGGER.info("Response Body In Try Section: "+response1.getBody());
					// System.out.println("response>>>");
					// System.out.println(response1.getBody());
					jsonObj = new JSONObject(response1.getBody());
					if (jsonObj.getString("status") != null
							&& jsonObj
									.getString("status")
									.equalsIgnoreCase(
											PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
						keyPayIntLeaveApplication
								.setExternalLeaveRequestId(jsonObj
										.getLong("id"));
						keyPayIntLeaveApplication
								.setSyncStatus(PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_SYNC_STATUS_SUCCESS);
						keyPayIntLeaveApplicationDAO
								.update(keyPayIntLeaveApplication);
					}
				} catch (Exception e) {
					keyPayIntLeaveApplication
							.setSyncStatus(PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_SYNC_STATUS_FAILED);
					keyPayIntLeaveApplicationDAO
							.update(keyPayIntLeaveApplication);
					LOGGER.info("In Catch Response : "+response1);
					LOGGER.info("In Catch Response Body: "+response1.getBody());
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void sendCancelledLeaveApp(Long companyId,
			Map<String, Long> leaveCategoryMap, String baseURL, String apiKey) {
		// move Leave Applications(Approved and cancel leave) to
		// KeyPayIntLeaveApplication table
		// updateKeyPayIntLeaveAppTable(companyId);

		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

		setInternalProxy(requestFactory);

		Company companyVO = companyDAO.findById(companyId);

		// Find All Leave which is not send(Sync) to KeyPay
		List<KeyPayIntLeaveApplication> keyPayIntLeaveAppList = keyPayIntLeaveApplicationDAO
				.findByCondition(
						PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_SYNC_STATUS_SUCCESS,
						PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CANCELLED,
						companyId);

		Map<String, String> empNumExternalIdMap = new HashMap<String, String>();
		if (!keyPayIntLeaveAppList.isEmpty()) {
			HRISPreference hrisPreferenceVO = hrisPreferenceDAO
					.findByCompanyId(companyId);
			if (hrisPreferenceVO != null
					&& hrisPreferenceVO.getExternalId() != null) {
				List<Object[]> keyPayEmpExternalIdList = getKeyPayExternalIdList(
						hrisPreferenceVO.getExternalId(), companyId,
						companyVO.getDateFormat(), null);
				for (Object[] extIdObj : keyPayEmpExternalIdList) {
					if (extIdObj != null && extIdObj[0] != null
							&& extIdObj[1] != null) {
						empNumExternalIdMap.put(String.valueOf(extIdObj[1]),
								String.valueOf(extIdObj[0]));
					}
				}
			}

			for (KeyPayIntLeaveApplication keyPayIntLeaveApplication : keyPayIntLeaveAppList) {
				if (empNumExternalIdMap.get(keyPayIntLeaveApplication
						.getEmployeeNumber()) == null) {
					continue;
				}
				String leaveRequestId = "";
				KeyPayIntLeaveApplication leaveApplication = keyPayIntLeaveApplicationDAO
						.findByLeaveAppId(
								companyId,
								keyPayIntLeaveApplication
										.getCancelLeaveApplicationId(),
								PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CANCELLED);
				if (leaveApplication != null
						&& leaveApplication.getExternalLeaveRequestId() != null) {
					leaveRequestId = String.valueOf(leaveApplication
							.getExternalLeaveRequestId());
				} else {
					leaveRequestId = getLeaveRequestForEmployee(
							empNumExternalIdMap.get(keyPayIntLeaveApplication
									.getEmployeeNumber()),
							keyPayIntLeaveApplication, leaveCategoryMap,
							baseURL, apiKey);
				}

				if (StringUtils.isNotBlank(leaveRequestId)) {
					RestTemplate restTemplate = new RestTemplate(requestFactory);
					String url = baseURL
							+ "/employee/"
							+ empNumExternalIdMap.get(keyPayIntLeaveApplication
									.getEmployeeNumber()) + "/leaverequest/"
							+ leaveRequestId;

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
					headers.add("Authorization", "Basic " + apiKey);

					Map<String, String> params = new HashMap<String, String>();
					HttpEntity<Map> entity = new HttpEntity<Map>(params,
							headers);

					restTemplate.getMessageConverters().add(
							new StringHttpMessageConverter());

					JSONObject jsonObj;
					try {
						ResponseEntity<String> response1 = restTemplate
								.exchange(url, HttpMethod.DELETE, entity,
										String.class);

						// System.out.println("response>>>");
						// System.out.println(response1.getBody());
						jsonObj = new JSONObject(response1.getBody());
						if (jsonObj.getString("status") != null
								&& jsonObj
										.getString("status")
										.equalsIgnoreCase(
												PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CANCELLED)) {
							keyPayIntLeaveApplication
									.setExternalLeaveRequestId(jsonObj
											.getLong("id"));
							keyPayIntLeaveApplication
									.setSyncStatus(PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_SYNC_STATUS_SUCCESS);
							keyPayIntLeaveApplicationDAO
									.update(keyPayIntLeaveApplication);
						}
					} catch (Exception e) {
						keyPayIntLeaveApplication
								.setSyncStatus(PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_SYNC_STATUS_FAILED);
						keyPayIntLeaveApplicationDAO
								.update(keyPayIntLeaveApplication);
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	private String getLeaveRequestForEmployee(String employeeId,
			KeyPayIntLeaveApplication keyPayIntLeaveApplication,
			Map<String, Long> leaveCategoryMap, String baseURL, String apiKey) {
		String leaveRequestId = "";
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		setInternalProxy(requestFactory);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String fromDate = DateUtils.timeStampToString(
				keyPayIntLeaveApplication.getStartDate(),
				PayAsiaConstants.DATE_FORMAT_YYYY_MM_DD);
		String toDate = DateUtils.timeStampToString(
				keyPayIntLeaveApplication.getEndDate(),
				PayAsiaConstants.DATE_FORMAT_YYYY_MM_DD);
		String url = baseURL
				+ "/employee/"
				+ employeeId
				+ "/leaverequest?$filter=FromDate eq datetime'"
				+ fromDate
				+ "' & ToDate eq datetime'"
				+ toDate
				+ "' & Status eq '"
				+ keyPayIntLeaveApplication.getLeaveStatus()
				+ "' & LeaveCategory eq '"
				+ keyPayIntLeaveApplication.getLeaveTypeName()
				+ "' & totalHours eq "
				+ keyPayIntLeaveApplication.getHours()
				+ " & LeaveCategoryId eq "
				+ leaveCategoryMap.get(keyPayIntLeaveApplication
						.getLeaveTypeName());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Basic " + apiKey);

		Map<String, String> params = new HashMap<String, String>();
		HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);

		restTemplate.getMessageConverters().add(
				new StringHttpMessageConverter());
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, entity,
					String.class);

		} catch (HttpClientErrorException httpClientErrorException) {
			LOGGER.error(httpClientErrorException.getMessage(),
					httpClientErrorException);
		}

		try {
			if (response != null) {
				JSONArray jsonArray = new JSONArray(response.getBody());
				for (int count = 0; count < jsonArray.length(); count++) {
					JSONObject jsonObj = (JSONObject) jsonArray.get(count);
					leaveRequestId = jsonObj.getString("id");
				}
			}
		} catch (JSONException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return leaveRequestId;
	}

	private void setInternalProxy(SimpleClientHttpRequestFactory requestFactory) {
		// Set proxy for development
		if (PAYASIA_MIND_PROXY_ENABLED_FOR_DEV) {
			Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(
					PAYASIA_MIND_PROXY_HOSTNAME, PAYASIA_MIND_PROXY_PORT));
			requestFactory.setProxy(proxy);
		}
	}

	@Override
	public Map<String, Long> getLeaveCategoryDetails(String baseURL,
			String apiKey) {
		Map<String, Long> leaveCategoryMap = new HashMap<String, Long>();

		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		setInternalProxy(requestFactory);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String url = baseURL + "/leavecategory/";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Basic " + apiKey);

		Map<String, String> params = new HashMap<String, String>();
		HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);

		restTemplate.getMessageConverters().add(
				new StringHttpMessageConverter());
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, entity,
					String.class);

		} catch (HttpClientErrorException httpClientErrorException) {
			LOGGER.error(httpClientErrorException.getMessage(),
					httpClientErrorException);
		}

		try {
			if (response != null) {
				JSONArray jsonArray = new JSONArray(response.getBody());
				for (int count = 0; count < jsonArray.length(); count++) {
					JSONObject jsonObj = (JSONObject) jsonArray.get(count);
					leaveCategoryMap.put(jsonObj.getString("name"),
							jsonObj.getLong("id"));
				}
			}
		} catch (JSONException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return leaveCategoryMap;
	}

	// private String getAPIBase64Key() {
	// String apiKey = PAYASIA_KEYPAY_INTEGRATION_APIKEY;
	// byte[] apiKeyInBytes = apiKey.getBytes();
	// byte[] base64CredsBytes = Base64.encodeBase64(apiKeyInBytes);
	// String base64Creds = new String(base64CredsBytes);
	// return base64Creds;
	// }

	@Override
	public boolean checkLeaveStatusForCancellation(
			Long cancelLeaveApplicationId, Long companyId) {
		IntegrationMaster integrationMasterVO = integrationMasterDAO
				.findByCondition(companyId);
		if (integrationMasterVO != null) {
			String baseURL = integrationMasterVO.getBaseURL()
					+ integrationMasterVO.getExternalCompanyId();
			KeyPayIntLeaveApplication keyPayIntLeaveApplication = keyPayIntLeaveApplicationDAO
					.findByLeaveAppId(
							companyId,
							cancelLeaveApplicationId,
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CANCELLED);
			if (keyPayIntLeaveApplication != null
					&& keyPayIntLeaveApplication.getExternalLeaveRequestId() != null) {
				SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
				setInternalProxy(requestFactory);

				RestTemplate restTemplate = new RestTemplate(requestFactory);
				String url = baseURL + "/leaverequest?$filter=Id eq "
						+ keyPayIntLeaveApplication.getExternalLeaveRequestId();

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.add("Authorization",
						"Basic " + integrationMasterVO.getApiKey());

				Map<String, String> params = new HashMap<String, String>();
				HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);

				restTemplate.getMessageConverters().add(
						new StringHttpMessageConverter());
				ResponseEntity<String> response = null;
				try {
					response = restTemplate.exchange(url, HttpMethod.GET,
							entity, String.class);
				} catch (HttpClientErrorException httpClientErrorException) {
					LOGGER.error(httpClientErrorException.getMessage(),
							httpClientErrorException);
				}

				try {
					if (response != null) {
						JSONArray jsonArray = new JSONArray(response.getBody());
						JSONObject jsonObj = (JSONObject) jsonArray.get(0);
						Double hoursApplied = jsonObj.getDouble("hoursApplied");
						if (hoursApplied > 0) {
							return false;
						}
						return true;
					}
				} catch (JSONException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return true;
	}

	private List<KeyPayIntPayRunDTO> getPayRunIdList(Long companyId,
			String baseURL, String apiKey) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

		setInternalProxy(requestFactory);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String url = baseURL + "/payrun/";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Basic " + apiKey);

		Map<String, String> params = new HashMap<String, String>();
		HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);

		restTemplate.getMessageConverters().add(
				new StringHttpMessageConverter());
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, entity,
					String.class);

		} catch (HttpClientErrorException httpClientErrorException) {
			LOGGER.error(httpClientErrorException.getMessage(),
					httpClientErrorException);
		}

		List<KeyPayIntPayRunDTO> keyPayIntPayRunDTOList = new ArrayList<KeyPayIntPayRunDTO>();
		try {
			if (response != null) {
				// Get Leave Calendar Year Start and end date
				EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();
				setEmpHistoryStartAndEndDate(companyId,
						employeeLeaveSchemeTypeHistory);

				LeavePreference leavePreferenceVO = leavePreferenceDAO
						.findByCompanyId(companyId);

				JSONArray jsonArray = new JSONArray(response.getBody());
				for (int count = 0; count < jsonArray.length(); count++) {
					JSONObject jsonObj = (JSONObject) jsonArray.get(count);
					boolean isFinalised = jsonObj.getBoolean("isFinalised");
					if (isFinalised) {
						KeyPayIntPayRunDTO keyPayIntPayRunDTO = new KeyPayIntPayRunDTO();
						Timestamp dateFinalisedNew = convertJsonStringToTimestamp(jsonObj
								.getString("dateFinalised"));
						keyPayIntPayRunDTO.setDateFinalised(dateFinalisedNew);
						keyPayIntPayRunDTO.setPayRunId(jsonObj.getLong("id"));

						Timestamp payPeriodStartingDateNew = convertJsonStringToTimestamp(jsonObj
								.getString("payPeriodStarting"));
						keyPayIntPayRunDTO
								.setPayPeriodStarting(payPeriodStartingDateNew);
						Timestamp payPeriodEndingDateNew = convertJsonStringToTimestamp(jsonObj
								.getString("payPeriodEnding"));
						keyPayIntPayRunDTO
								.setPayPeriodEnding(payPeriodEndingDateNew);

						if (leavePreferenceVO != null
								&& StringUtils.isNotBlank(leavePreferenceVO
										.getKeypayPayRunParameter())) {
							if (leavePreferenceVO.getKeypayPayRunParameter()
									.equalsIgnoreCase("payPeriodStarting")
									&& isKeypayPayRunDateExist(
											payPeriodStartingDateNew,
											companyId,
											employeeLeaveSchemeTypeHistory)) {
								keyPayIntPayRunDTO
										.setPayRunParameterDate(payPeriodStartingDateNew);
								keyPayIntPayRunDTOList.add(keyPayIntPayRunDTO);
							}
							if (leavePreferenceVO.getKeypayPayRunParameter()
									.equalsIgnoreCase("payPeriodEnding")
									&& isKeypayPayRunDateExist(
											payPeriodEndingDateNew, companyId,
											employeeLeaveSchemeTypeHistory)) {
								keyPayIntPayRunDTO
										.setPayRunParameterDate(payPeriodEndingDateNew);
								keyPayIntPayRunDTOList.add(keyPayIntPayRunDTO);
							}
							if (leavePreferenceVO.getKeypayPayRunParameter()
									.equalsIgnoreCase("dateFinalised")
									&& isKeypayPayRunDateExist(
											dateFinalisedNew, companyId,
											employeeLeaveSchemeTypeHistory)) {
								keyPayIntPayRunDTO
										.setPayRunParameterDate(dateFinalisedNew);
								keyPayIntPayRunDTOList.add(keyPayIntPayRunDTO);
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return keyPayIntPayRunDTOList;
	}

	private boolean isKeypayPayRunDateExist(Timestamp keyPayRunTimestamp,
			Long companyId,
			EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory) {
		// Check Whether keyPayRunTimestamp exist between Leave Calendar Year
		// Start and end date
		if ((keyPayRunTimestamp.after(employeeLeaveSchemeTypeHistory
				.getStartDate()) && keyPayRunTimestamp
				.before(employeeLeaveSchemeTypeHistory.getEndDate()))
				|| (keyPayRunTimestamp.equals(employeeLeaveSchemeTypeHistory
						.getStartDate()))
				|| (keyPayRunTimestamp.equals(employeeLeaveSchemeTypeHistory
						.getEndDate()))) {
			return true;
		}
		return false;
	}

	private Timestamp convertJsonStringToTimestamp(String jsonString)
			throws JSONException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		String formattedTime = null;
		try {
			date = sdf.parse(jsonString);
			formattedTime = output.format(date);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return Timestamp.valueOf(formattedTime);
	}

	@Override
	public void getPayRunDetails(Long companyId, String baseURL, String apiKey) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

		setInternalProxy(requestFactory);
		Company companyVO = companyDAO.findById(companyId);
		// Get payRunIds
		List<KeyPayIntPayRunDTO> keyPayIntPayRunDTOList = getPayRunIdList(
				companyId, baseURL, apiKey);

		Map<Long, KeyPayIntPayRunDTO> payRunIdMap = new HashMap<Long, KeyPayIntPayRunDTO>();
		for (KeyPayIntPayRunDTO keyPayIntPayRunDTO : keyPayIntPayRunDTOList) {
			payRunIdMap.put(keyPayIntPayRunDTO.getPayRunId(),
					keyPayIntPayRunDTO);
		}

		List<Long> payRunIdList = new ArrayList<Long>(payRunIdMap.keySet());
		List<Long> payRunIdList2 = new ArrayList<Long>(payRunIdMap.keySet());
		List<Long> payRunIdExistList = new ArrayList<Long>();
		List<Long> payRunIdExistAllList = new ArrayList<Long>();

		// Get Leave Calendar Year Start and end date
		EmployeeLeaveSchemeTypeHistory leaveCalStartEndDate = new EmployeeLeaveSchemeTypeHistory();
		setEmpHistoryStartAndEndDate(companyId, leaveCalStartEndDate);

		if (!payRunIdMap.isEmpty()) {
			List<KeyPayIntPayRun> keyPayIntPayRunVOList = keyPayIntPayRunDAO
					.findByPayRunParameterDate(companyId,
							leaveCalStartEndDate.getStartDate(),
							leaveCalStartEndDate.getEndDate());
			for (KeyPayIntPayRun keyPayIntPayRun : keyPayIntPayRunVOList) {
				if (payRunIdMap.containsKey(keyPayIntPayRun.getPayRunId())
						&& keyPayIntPayRun.getProcessStatus() == PayAsiaConstants.PAYASIA_KEYPAY_INT_PAYRUN_PROCESS_STATUS_SUCCESS
						&& (keyPayIntPayRun.getPayRunFinalizedDate().equals(
								payRunIdMap.get(keyPayIntPayRun.getPayRunId())
										.getDateFinalised()) || !keyPayIntPayRun
								.getPayRunFinalizedDate().before(
										payRunIdMap.get(
												keyPayIntPayRun.getPayRunId())
												.getDateFinalised()))) {
					payRunIdExistList.add(keyPayIntPayRun.getPayRunId());
				}
				payRunIdExistAllList.add(keyPayIntPayRun.getPayRunId());
			}
		}
		payRunIdList.removeAll(payRunIdExistList);

		// Check If PayRun exists, which is deleted from keypay Int.
		payRunIdExistAllList.removeAll(payRunIdList2);
		if (!payRunIdExistAllList.isEmpty()) {
			deletePayRun(payRunIdExistAllList, companyId);
		}

		// Get Employee And employee External Ids
		Map<String, String> externalIdEmpNumMap = new HashMap<String, String>();
		Map<String, Employee> empNumEmployeeMap = new HashMap<String, Employee>();
		if (!payRunIdList.isEmpty()) {
			HRISPreference hrisPreferenceVO = hrisPreferenceDAO
					.findByCompanyId(companyId);
			if (hrisPreferenceVO != null
					&& hrisPreferenceVO.getExternalId() != null) {
				List<Object[]> keyPayEmpExternalIdList = getKeyPayExternalIdList(
						hrisPreferenceVO.getExternalId(), companyId,
						companyVO.getDateFormat(), null);
				for (Object[] extIdObj : keyPayEmpExternalIdList) {
					if (extIdObj != null && extIdObj[0] != null
							&& extIdObj[1] != null) {
						externalIdEmpNumMap.put(String.valueOf(extIdObj[0]),
								String.valueOf(extIdObj[1]));
					}
				}
			}

			List<Employee> employeeVOList = employeeDAO
					.findByCompany(companyId);
			for (Employee employee : employeeVOList) {
				// if (employee.isStatus()) {
				empNumEmployeeMap.put(employee.getEmployeeNumber(), employee);
				// }
			}
		}

		// For deleted payrun detail,IF After reprocessing is required for
		// payrun
		List<Long> keyPayRunDetailIdDeletedList = new ArrayList<Long>();
		for (Long payRunId : payRunIdList) {
			// For delete payrun detail,IF After reprocessing is required for
			// payrun
			List<Long> keyPayRunDetailIdProcessList = new ArrayList<Long>();

			// save payrunId
			KeyPayIntPayRun keyPayIntPayRunVO = null;
			KeyPayIntPayRun persistObj = null;
			keyPayIntPayRunVO = keyPayIntPayRunDAO.findByPayRunId(companyId,
					payRunId);
			if (keyPayIntPayRunVO == null) {
				keyPayIntPayRunVO = new KeyPayIntPayRun();
				keyPayIntPayRunVO.setPayRunId(payRunId);
				keyPayIntPayRunVO.setProcessStatus(0);// 0 for not processed at
				keyPayIntPayRunVO.setPayRunFinalizedDate(payRunIdMap.get(
						payRunId).getDateFinalised());
				keyPayIntPayRunVO.setPayRunParameterDate(payRunIdMap.get(
						payRunId).getPayRunParameterDate());
				keyPayIntPayRunVO.setCompany(companyVO);
				persistObj = keyPayIntPayRunDAO.saveReturn(keyPayIntPayRunVO);
			} else {
				keyPayIntPayRunVO.setPayRunId(payRunId);
				keyPayIntPayRunVO.setProcessStatus(0);// 0 for not processed at
														// payasia end
				keyPayIntPayRunVO.setPayRunFinalizedDate(payRunIdMap.get(
						payRunId).getDateFinalised());
				keyPayIntPayRunVO.setPayRunParameterDate(payRunIdMap.get(
						payRunId).getPayRunParameterDate());
				keyPayIntPayRunVO.setCompany(companyVO);
				keyPayIntPayRunDAO.update(keyPayIntPayRunVO);
			}

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			String url = baseURL + "/payrun/" + payRunId + "/leaveaccrued";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Authorization", "Basic " + apiKey);

			Map<String, String> params = new HashMap<String, String>();
			HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);

			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			ResponseEntity<String> response1 = null;
			try {
				response1 = restTemplate.exchange(url, HttpMethod.GET, entity,
						String.class);
			} catch (HttpClientErrorException httpClientErrorException) {
				LOGGER.error(httpClientErrorException.getMessage(),
						httpClientErrorException);
			}

			try {
				if (response1 != null) {
					JSONObject jsonObject = new JSONObject(response1.getBody());
					String pendingMsgArr = jsonObject.getString("leave");
					JSONObject object = new JSONObject(pendingMsgArr);
					String[] keys = JSONObject.getNames(object);

					// Keys is EmployeeId
					if (keys != null) {
						for (String externalId : keys) {
							try {
								if (empNumEmployeeMap.get(externalIdEmpNumMap
										.get(externalId)) == null) {
									continue;
								}
								JSONArray jsonArray = object
										.getJSONArray(externalId);
								for (int count = 0; count < jsonArray.length(); count++) {
									JSONObject jsonObj = (JSONObject) jsonArray
											.get(count);
									KeyPayIntPayRunDetail keyPayIntPayRunDetailVO = new KeyPayIntPayRunDetail();
									keyPayIntPayRunDetailVO
											.setLeaveTypeName(jsonObj
													.getString("leaveCategoryName"));
									keyPayIntPayRunDetailVO
											.setLeaveCategoryId(jsonObj
													.getLong("leaveCategoryId"));
									keyPayIntPayRunDetailVO
											.setLeaveEntitlement(BigDecimal.valueOf(jsonObj
													.getDouble("amount")));
									keyPayIntPayRunDetailVO
											.setRemarks("Leave Accrued for period '"
													+ DateUtils
															.timeStampToString(
																	payRunIdMap
																			.get(payRunId)
																			.getPayPeriodStarting(),
																	companyVO
																			.getDateFormat())
													+ "' to '"
													+ DateUtils
															.timeStampToString(
																	payRunIdMap
																			.get(payRunId)
																			.getPayPeriodEnding(),
																	companyVO
																			.getDateFormat())
													+ "' finalized on '"
													+ DateUtils
															.timeStampToString(
																	payRunIdMap
																			.get(payRunId)
																			.getDateFinalised(),
																	companyVO
																			.getDateFormat())
													+ "'");

									keyPayIntPayRunDetailVO
											.setExternalId(externalId);
									keyPayIntPayRunDetailVO
											.setEmployee(empNumEmployeeMap
													.get(externalIdEmpNumMap
															.get(externalId)));
									KeyPayIntPayRunDetail keyPayIntPayRunDetailDataVO = null;
									if (persistObj == null) {
										keyPayIntPayRunDetailVO
												.setKeyPayIntPayRun(keyPayIntPayRunVO);
										keyPayIntPayRunDetailDataVO = keyPayIntPayRunDetailDAO
												.findByCondition(
														keyPayIntPayRunVO
																.getKeyPayIntPayRunId(),
														jsonObj.getLong("leaveCategoryId"),
														externalId,
														empNumEmployeeMap
																.get(externalIdEmpNumMap
																		.get(externalId))
																.getEmployeeId());
										if (keyPayIntPayRunDetailDataVO != null
												&& !keyPayIntPayRunDetailDataVO
														.getLeaveEntitlement()
														.equals(BigDecimal
																.valueOf(
																		jsonObj.getDouble("amount"))
																.setScale(5))) {
											keyPayIntPayRunDetailDataVO
													.setLeaveEntitlement(BigDecimal.valueOf(jsonObj
															.getDouble("amount")));
											keyPayIntPayRunDetailDAO
													.update(keyPayIntPayRunDetailDataVO);
										}
										if (keyPayIntPayRunDetailDataVO == null) {
											keyPayIntPayRunDetailDataVO = keyPayIntPayRunDetailDAO
													.saveReturn(keyPayIntPayRunDetailVO);
										}
									} else {
										keyPayIntPayRunDetailVO
												.setKeyPayIntPayRun(persistObj);
										keyPayIntPayRunDetailDataVO = keyPayIntPayRunDetailDAO
												.saveReturn(keyPayIntPayRunDetailVO);
									}

									// Added KeyPayRun Detail id
									if (keyPayIntPayRunDetailDataVO != null) {
										if (!keyPayRunDetailIdProcessList
												.isEmpty()
												&& !keyPayRunDetailIdProcessList
														.contains(keyPayIntPayRunDetailDataVO
																.getDetailId())) {
											keyPayRunDetailIdProcessList
													.add(keyPayIntPayRunDetailDataVO
															.getDetailId());
										} else {
											keyPayRunDetailIdProcessList
													.add(keyPayIntPayRunDetailDataVO
															.getDetailId());
										}
									}
								}
							} catch (JSONException e) {
								LOGGER.error(e.getMessage(), e);
							}
						}
					}
				}
			} catch (JSONException e) {
				LOGGER.error(e.getMessage(), e);
			}

			// Delete those payrun detail which is not exist after Re-Processing
			if (persistObj == null) {
				List<Long> keyPayRunDetailVOList = keyPayIntPayRunDetailDAO
						.findKeyPayRunDetailId(keyPayIntPayRunVO
								.getKeyPayIntPayRunId());
				keyPayRunDetailVOList.removeAll(keyPayRunDetailIdProcessList);
				keyPayRunDetailIdDeletedList.addAll(keyPayRunDetailVOList);
			}
		}
		// move payrun details from table 'keyPayIntPayRunDetail' to table
		// 'EmployeeLeaveSchemeTypeHistory'
		processPayRun(payRunIdList, companyId, companyVO.getDateFormat(),
				keyPayRunDetailIdDeletedList);
	}

	private void deletePayRun(List<Long> payRunIdExistAllList, Long companyId) {
		for (Long payRunId : payRunIdExistAllList) {
			KeyPayIntPayRun keyPayIntPayRun = keyPayIntPayRunDAO
					.findByPayRunId(companyId, payRunId);
			if (keyPayIntPayRun == null) {
				continue;
			}
			List<KeyPayIntPayRunDetail> keyPayIntPayRunDetailVOList = keyPayIntPayRunDetailDAO
					.findByKeyPayIntPayRunId(keyPayIntPayRun
							.getKeyPayIntPayRunId());
			for (KeyPayIntPayRunDetail keyPayIntPayRunDetail : keyPayIntPayRunDetailVOList) {
				if (keyPayIntPayRunDetail.getEmployeeLeaveSchemeTypeHistoryId() != null) {
					EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = employeeLeaveSchemeTypeHistoryDAO
							.findById(keyPayIntPayRunDetail
									.getEmployeeLeaveSchemeTypeHistoryId());
					employeeLeaveSchemeTypeHistoryDAO
							.delete(employeeLeaveSchemeTypeHistory);
					keyPayIntPayRunDetailDAO.delete(keyPayIntPayRunDetail);
				}
			}
			keyPayIntPayRunDAO.delete(keyPayIntPayRun);
		}
	}

	private void processPayRun(List<Long> payRunIdList, Long companyId,
			String dateFormat, List<Long> keyPayRunDetailIdDeletedList) {
		if (!payRunIdList.isEmpty()) {

			Map<String, LeaveSessionMaster> leaveSessionMasterMap = new HashMap<>();
			List<LeaveSessionMaster> leaveSessionMasterList = leaveSessionMasterDAO
					.findAll();
			for (LeaveSessionMaster leaveSessionMaster : leaveSessionMasterList) {
				leaveSessionMasterMap.put(leaveSessionMaster.getSession(),
						leaveSessionMaster);
			}

			for (Long payRunId : payRunIdList) {
				KeyPayIntPayRun keyPayIntPayRun = keyPayIntPayRunDAO
						.findByPayRunId(companyId, payRunId);
				if (keyPayIntPayRun == null) {
					continue;
				}
				if (keyPayIntPayRun != null
						&& keyPayIntPayRun.getProcessStatus() == PayAsiaConstants.PAYASIA_KEYPAY_INT_PAYRUN_PROCESS_STATUS_SUCCESS) {
					continue;
				}
				List<KeyPayIntPayRunDetail> keyPayIntPayRunDetailVOList = keyPayIntPayRunDetailDAO
						.findByKeyPayIntPayRunId(keyPayIntPayRun
								.getKeyPayIntPayRunId());
				for (KeyPayIntPayRunDetail keyPayIntPayRunDetail : keyPayIntPayRunDetailVOList) {
					EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();
					EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO
							.getActiveLeaveSchemeByDate(keyPayIntPayRunDetail
									.getEmployee().getEmployeeId(), DateUtils
									.timeStampToString(DateUtils
											.getCurrentTimestampWithTime(),
											dateFormat), dateFormat);
					if (employeeLeaveScheme == null) {
						continue;
					}
					EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO
							.findByEmpLeaveSchemeAndLeaveType(
									employeeLeaveScheme
											.getEmployeeLeaveSchemeId(),
									keyPayIntPayRunDetail.getLeaveTypeName());
					employeeLeaveSchemeTypeHistory
							.setStartSessionMaster(leaveSessionMasterMap
									.get(PayAsiaConstants.LEAVE_SESSION_1));
					employeeLeaveSchemeTypeHistory
							.setEndSessionMaster(leaveSessionMasterMap
									.get(PayAsiaConstants.LEAVE_SESSION_2));
					employeeLeaveSchemeTypeHistory
							.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);
					AppCodeMaster appCodeMaster = appCodeMasterDAO
							.findByCategoryAndDesc(
									PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE,
									PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CREDITED);
					employeeLeaveSchemeTypeHistory
							.setAppCodeMaster(appCodeMaster);
					employeeLeaveSchemeTypeHistory
							.setDays(keyPayIntPayRunDetail
									.getLeaveEntitlement());
					employeeLeaveSchemeTypeHistory
							.setReason(keyPayIntPayRunDetail.getRemarks());
					// Set Start and End Date
					setEmpHistoryStartAndEndDate(companyId,
							employeeLeaveSchemeTypeHistory);
					employeeLeaveSchemeTypeHistory.setCompanyId(companyId);

					EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistoryVO = null;
					if (keyPayIntPayRunDetail
							.getEmployeeLeaveSchemeTypeHistoryId() != null) {
						employeeLeaveSchemeTypeHistoryVO = employeeLeaveSchemeTypeHistoryDAO
								.findById(keyPayIntPayRunDetail
										.getEmployeeLeaveSchemeTypeHistoryId());
					}

					if (employeeLeaveSchemeTypeHistoryVO != null
							&& !keyPayRunDetailIdDeletedList.isEmpty()
							&& keyPayRunDetailIdDeletedList
									.contains(keyPayIntPayRunDetail
											.getDetailId())) {
						employeeLeaveSchemeTypeHistoryDAO
								.delete(employeeLeaveSchemeTypeHistoryVO);
						keyPayIntPayRunDetailDAO.delete(keyPayIntPayRunDetail);
					} else if (employeeLeaveSchemeTypeHistoryVO != null
							&& !employeeLeaveSchemeTypeHistoryVO.getDays()
									.equals(keyPayIntPayRunDetail
											.getLeaveEntitlement().setScale(2,
													BigDecimal.ROUND_HALF_UP))) {
						employeeLeaveSchemeTypeHistoryVO
								.setDays(keyPayIntPayRunDetail
										.getLeaveEntitlement());
						employeeLeaveSchemeTypeHistoryDAO
								.update(employeeLeaveSchemeTypeHistoryVO);
					}

					if (employeeLeaveSchemeTypeHistoryVO == null
							&& employeeLeaveSchemeType != null) {
						EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistoryPersistObj = employeeLeaveSchemeTypeHistoryDAO
								.saveReturn(employeeLeaveSchemeTypeHistory);
						keyPayIntPayRunDetail
								.setEmployeeLeaveSchemeTypeHistoryId(employeeLeaveSchemeTypeHistoryPersistObj
										.getEmployeeLeaveSchemeTypeHistoryId());
						keyPayIntPayRunDetailDAO.update(keyPayIntPayRunDetail);
					}
				}
				if (keyPayIntPayRun != null
						&& !keyPayIntPayRunDetailVOList.isEmpty()) {
					keyPayIntPayRun.setProcessStatus(1);
					keyPayIntPayRunDAO.update(keyPayIntPayRun);
				}

			}
		}
	}

	private void setEmpHistoryStartAndEndDate(Long companyId,
			EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory) {
		// Create Start and End Date
		LeavePreference leavePreferenceVO = leavePreferenceDAO
				.findByCompanyId(companyId);
		SimpleDateFormat sdf = new SimpleDateFormat(
				PayAsiaConstants.DEFAULT_DATE_FORMAT);

		// Get Current Date
		Calendar currentDateCal = Calendar.getInstance();
		Date currentDate = currentDateCal.getTime();

		int startMonth = 0;
		startMonth = Integer.parseInt(String.valueOf(leavePreferenceVO
				.getStartMonth().getMonthId())) - 1;
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		Calendar startCalendar = new GregorianCalendar(cal.get(Calendar.YEAR),
				startMonth, 1, 0, 0, 0);
		Date calStartDate = startCalendar.getTime();
		if (currentDate.before(calStartDate)) {
			year = year - 1;
		}
		startCalendar = new GregorianCalendar(year, startMonth, 1, 0, 0, 0);

		Calendar endCalendar = new GregorianCalendar(year + 1, startMonth, 1,
				0, 0, 0);
		endCalendar.add(Calendar.DAY_OF_MONTH, -1);

		String startDate = sdf.format(startCalendar.getTime());
		String endDate = sdf.format(endCalendar.getTime());
		employeeLeaveSchemeTypeHistory.setStartDate(DateUtils
				.stringToTimestamp(startDate,
						PayAsiaConstants.DEFAULT_DATE_FORMAT));
		employeeLeaveSchemeTypeHistory.setEndDate(DateUtils.stringToTimestamp(
				endDate, PayAsiaConstants.DEFAULT_DATE_FORMAT));
	}

	private List<Object[]> getKeyPayExternalIdList(
			DataDictionary dataDictionary, Long companyId, String dateFormat,
			Long employeeId) {
		List<Long> formIds = new ArrayList<Long>();
		formIds.add(dataDictionary.getFormID());
		Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
		Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();
		getColMap(dataDictionary, colMap);
		List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();
		List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();

		if (colMap.get(
				dataDictionary.getLabel()
						+ dataDictionary.getDataDictionaryId())
				.getTablePosition() != null) {
			String tableKey;
			tableKey = colMap.get(
					dataDictionary.getLabel()
							+ dataDictionary.getDataDictionaryId()).getFormId()
					+ colMap.get(
							dataDictionary.getLabel()
									+ dataDictionary.getDataDictionaryId())
							.getTablePosition();
			tableRecordInfoFrom.put(
					tableKey,
					colMap.get(dataDictionary.getLabel()
							+ dataDictionary.getDataDictionaryId()));
		}

		EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();
		List<BigInteger> list = new ArrayList<BigInteger>();
		employeeShortListDTO.setEmployeeShortList(false);
		employeeShortListDTO.setShortListEmployeeIds(list);

		// Get Static Dictionary
		DataDictionary dataDictionaryEmp = dataDictionaryDAO
				.findByDictionaryNameGroup(null, 1l, "Employee Number", null,
						null);
		setStaticDictionary(colMap, dataDictionaryEmp.getLabel()
				+ dataDictionaryEmp.getDataDictionaryId(), dataDictionaryEmp);
		// Show by effective date table data if one custom table record is
		// required, here it is true i.e. show only by effective date table data
		boolean showByEffectiveDateTableData = true;
		List<Object[]> objectList = employeeDAO.findByCondition(colMap,
				formIds, companyId, finalFilterList, dateFormat,
				tableRecordInfoFrom, tableElements, employeeShortListDTO,
				showByEffectiveDateTableData);
		return objectList;
	}

	private void getColMap(DataDictionary dataDictionary,
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
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.TABLE_FIELD_TYPE)) {
				if (field.getDictionaryId().equals(
						dataDictionary.getDataDictionaryId())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
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

	private void setStaticDictionary(Map<String, DataImportKeyValueDTO> colMap,
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

}
