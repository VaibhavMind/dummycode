package com.payasia.logic.impl;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.CalendarCodeValueDTO;
import com.payasia.common.dto.CalendarEventDTO;
import com.payasia.common.dto.CalendarPatternDTO;
import com.payasia.common.dto.CalendarTemplateConditionDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.LeaveCalendarEventDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CalendarDefForm;
import com.payasia.common.form.CalendarDefResponse;
import com.payasia.common.form.CalendarTempDataForm;
import com.payasia.common.form.CalendarTempShortListResponse;
import com.payasia.common.form.CalendarTemplateMonthForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CalendarCodeMasterDAO;
import com.payasia.dao.CalendarPatternDetailDAO;
import com.payasia.dao.CalendarPatternMasterDAO;
import com.payasia.dao.CompanyCalendarDAO;
import com.payasia.dao.CompanyCalendarTemplateDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeCalendarConfigDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.CalendarCodeMaster;
import com.payasia.dao.bean.CalendarPatternDetail;
import com.payasia.dao.bean.CalendarPatternMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyCalendar;
import com.payasia.dao.bean.CompanyCalendarTemplate;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.EmployeeCalendarConfig;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.logic.CalendarDefLogic;

/**
 * The Class CalendarDefLogicImpl.
 */
@Component
public class CalendarDefLogicImpl implements CalendarDefLogic {

	/** The calendar template master dao. */
	@Resource
	CalendarPatternDetailDAO calendarPatternDetailDAO;
	/** The month master dao. */
	@Resource
	MonthMasterDAO monthMasterDAO;

	@Resource
	ModuleMasterDAO moduleMasterDAO;
	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;
	@Resource
	CompanyCalendarDAO companyCalendarDAO;
	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The general dao. */
	@Resource
	GeneralDAO generalDAO;
	@Resource
	CompanyCalendarTemplateDAO companyCalendarTemplateDAO;
	@Resource
	EmployeeCalendarConfigDAO employeeCalendarConfigDAO;
	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The app code master dao. */
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	CalendarCodeMasterDAO calendarCodeMasterDAO;

	@Resource
	CalendarPatternMasterDAO calendarPatternMasterDAO;

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CalendarDefLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CalendarDefLogic#getCalTemplates(com.payasia.common
	 * .form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.String, java.lang.String, java.lang.Long)
	 */
	public CalendarDefResponse getCalTemplates(PageRequest pageDTO,
			SortCondition sortDTO, String calTempFilter, String filterText,
			Long companyId) {

		CalendarTemplateConditionDTO conditionDTO = new CalendarTemplateConditionDTO();

		List<CompanyCalendarTemplate> compCalTempList;

		if ("name".equals(calTempFilter)) {
			if (StringUtils.isNotBlank(filterText)) {
				conditionDTO.setTemplateName("%" + filterText + "%");
			}
		}
		if ("description".equals(calTempFilter)) {
			if (StringUtils.isNotBlank(filterText)) {
				conditionDTO.setTemplateDesc("%" + filterText + "%");
			}
		}
		if ("year".equals(calTempFilter)) {
			if (StringUtils.isNotBlank(filterText)) {
				conditionDTO.setYear(filterText);
			}
		}
		if ("patternName".equals(calTempFilter)) {
			if (StringUtils.isNotBlank(filterText)) {
				conditionDTO.setPatternName("%" + filterText + "%");
			}
		}

		int recordSize = companyCalendarTemplateDAO.getCountforfindByCompanyId(
				conditionDTO, companyId);
		compCalTempList = companyCalendarTemplateDAO.findByCompanyId(
				conditionDTO, pageDTO, sortDTO, companyId);

		List<CalendarDefForm> listCalForm = new ArrayList<CalendarDefForm>();

		for (CompanyCalendarTemplate companyCalendarTemplate : compCalTempList) {
			CalendarDefForm calDefForm = new CalendarDefForm();

			calDefForm.setCalDefId(FormatPreserveCryptoUtil.encrypt(companyCalendarTemplate
					.getCompanyCalendarTemplateId()));
			calDefForm.setCalDefName(companyCalendarTemplate.getTemplateName());
			calDefForm
					.setDescription(companyCalendarTemplate.getTemplateDesc());
			calDefForm.setYear(companyCalendarTemplate.getStart_Year());
			calDefForm.setPatternName(companyCalendarTemplate
					.getCalendarPatternMaster().getPatternName());
			/*calDefForm.setCalDefId(companyCalendarTemplate
					.getCompanyCalendarTemplateId());*/

			CompanyCalendar companyCalendar = companyCalendarDAO
					.findByCompanyCalTemlateId(companyCalendarTemplate
							.getCompanyCalendarTemplateId());
			StringBuilder calendarConfigure = new StringBuilder();
			if (companyCalendar != null) {
				/* ID ENCRYPT*/
				calendarConfigure
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'confCalTemp("
								+ FormatPreserveCryptoUtil.encrypt(companyCalendarTemplate
										.getCompanyCalendarTemplateId())
								+ ")'><span class='ctextgreen'>change</span></a>");

				calDefForm.setConfigureCalendar(String
						.valueOf(calendarConfigure));
			} else {
				/* ID ENCRYPT*/
				calendarConfigure
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'confCalTemp("
								+ FormatPreserveCryptoUtil.encrypt(companyCalendarTemplate
										.getCompanyCalendarTemplateId())
								+ ")'><span class='ctextgray'>No Change</span></a>");

				calDefForm.setConfigureCalendar(String
						.valueOf(calendarConfigure));
			}

			listCalForm.add(calDefForm);
		}

		CalendarDefResponse response = new CalendarDefResponse();

		response.setRows(listCalForm);

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

		return response;

	}

	@Override
	public CalendarTempDataForm getInitialIds() {

		return null;
	}

	@Override
	public void addCalendarMonthDetail(
			CalendarTemplateMonthForm calTempMonthForm) {

	}

	private void setCalEventDTO(CalendarDefForm calDefForm,
			List<LeaveCalendarEventDTO> leaveCalendarEventDTOList,
			String dateFormat) {
		List<LeaveCalendarEventDTO> eventsJanuary = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsFebruary = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsMarch = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsApril = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsMay = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsJune = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsJuly = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsAugust = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsSeptember = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsOctober = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsNovember = new ArrayList<>();
		List<LeaveCalendarEventDTO> eventsDecember = new ArrayList<>();
		for (LeaveCalendarEventDTO eventDTO : leaveCalendarEventDTOList) {
			Date calDate = DateUtils.stringToDate(eventDTO.getCalendarDate(),
					PayAsiaConstants.DEFAULT_DATE_FORMAT);
			Calendar cal = Calendar.getInstance();
			cal.setTime(calDate);
			int month = cal.get(Calendar.MONTH);
			if (month == 0) {
				eventsJanuary.add(eventDTO);
			}
			if (month == 1) {
				eventsFebruary.add(eventDTO);
			}
			if (month == 2) {
				eventsMarch.add(eventDTO);
			}
			if (month == 3) {
				eventsApril.add(eventDTO);
			}
			if (month == 4) {
				eventsMay.add(eventDTO);
			}
			if (month == 5) {
				eventsJune.add(eventDTO);
			}
			if (month == 6) {
				eventsJuly.add(eventDTO);
			}
			if (month == 7) {
				eventsAugust.add(eventDTO);
			}
			if (month == 8) {
				eventsSeptember.add(eventDTO);
			}
			if (month == 9) {
				eventsOctober.add(eventDTO);
			}
			if (month == 10) {
				eventsNovember.add(eventDTO);
			}
			if (month == 11) {
				eventsDecember.add(eventDTO);
			}

		}
		calDefForm.setEventsJanuary(eventsJanuary);
		calDefForm.setEventsFebruary(eventsFebruary);
		calDefForm.setEventsMarch(eventsMarch);
		calDefForm.setEventsApril(eventsApril);
		calDefForm.setEventsMay(eventsMay);
		calDefForm.setEventsJune(eventsJune);
		calDefForm.setEventsJuly(eventsJuly);
		calDefForm.setEventsAugust(eventsAugust);
		calDefForm.setEventsSeptember(eventsSeptember);
		calDefForm.setEventsOctober(eventsOctober);
		calDefForm.setEventsNovember(eventsNovember);
		calDefForm.setEventsDecember(eventsDecember);

	}

	@Override
	public CalendarDefForm getCalTempData(Long companyCalendarTemplateId,
			int year, Long companyId) {
		CalendarDefForm calDefForm = new CalendarDefForm();
		List<CalendarPatternDTO> calendarPatternDTOList = new ArrayList<>();
		CompanyCalendarTemplate companyCalendarTemplate = companyCalendarTemplateDAO
				.findByID(companyCalendarTemplateId);

		Company companyVO = companyDAO.findById(companyId);
		List<LeaveCalendarEventDTO> leaveCalendarEventDTOList = companyCalendarTemplateDAO
				.getCompanyCalendarTemplate(companyId,
						companyCalendarTemplateId, year, true,
						companyVO.getDateFormat(), null);
		Set<CalendarPatternDetail> calendarPatternDetailSet = companyCalendarTemplate
				.getCalendarPatternMaster().getCalendarPatternDetails();
		Set<String> calendarCodeSet = new HashSet<>();
		for (CalendarPatternDetail calendarPatternDetail : calendarPatternDetailSet) {
			calendarCodeSet.add(calendarPatternDetail.getCalendarCodeMaster()
					.getCode());

		}
		for (String calendarCode : calendarCodeSet) {
			CalendarPatternDTO calendarPatternDTO = new CalendarPatternDTO();
			calendarPatternDTO.setCalendarCode(calendarCode);
			calendarPatternDTOList.add(calendarPatternDTO);
		}
		calDefForm.setCalendarPatternDTOList(calendarPatternDTOList);
		setCalEventDTO(calDefForm, leaveCalendarEventDTOList,
				companyVO.getDateFormat());

		return calDefForm;
	}

	/**
	 * Comparator Class for Ordering SectionIntoDTOComp List.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CalendarDefLogic#getYearList(java.lang.Long)
	 */
	@Override
	public List<Integer> getYearList(Long calTempId) {
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CalendarDefLogic#getMonthDetail(java.lang.Integer,
	 * java.lang.Long)
	 */
	@Override
	public CalendarTemplateMonthForm getMonthDetail(Integer year, Long calTempId) {
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CalendarDefLogic#editCalMonthTemplate(com.payasia.common
	 * .form.CalendarTemplateMonthForm)
	 */
	@Override
	public void editCalMonthTemplate(CalendarTemplateMonthForm calTempMonthForm) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CalendarDefLogic#deleteCalTemplate(java.lang.Long)
	 */
	public void deleteCalTemplate(Long companyCalTemplateId) {
		CompanyCalendarTemplate companyCalendarTemplate = companyCalendarTemplateDAO
				.findByID(companyCalTemplateId);
		companyCalendarTemplateDAO.delete(companyCalendarTemplate);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CalendarDefLogic#getEntityData()
	 */
	@Override
	public CalendarTempDataForm getEntityData() {

		long[] entityId = new long[2];
		String[] entityName = new String[2];

		CalendarTempDataForm calTempDataForm = new CalendarTempDataForm();

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName("Employee");
		entityId[0] = entityMaster.getEntityId();
		entityName[0] = entityMaster.getEntityName();

		entityMaster = entityMasterDAO.findByEntityName("Company");
		entityId[1] = entityMaster.getEntityId();
		entityName[1] = entityMaster.getEntityName();

		calTempDataForm.setEntityId(entityId);
		calTempDataForm.setEntityName(entityName);

		return calTempDataForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CalendarDefLogic#getFieldsData(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public CalendarTempDataForm getFieldsData(Long entityId, Long companyId) {

		List<DataDictionary> dataDictionaryVOList = dataDictionaryDAO
				.findByConditionEntityAndCompanyId(companyId, entityId, null);

		List<Long> dataDictionaryId = new ArrayList<Long>();
		List<String> fieldName = new ArrayList<String>();
		List<String> dataType = new ArrayList<String>();

		try {
			for (DataDictionary dataDictionaryVO : dataDictionaryVOList) {
				if (dataDictionaryVO.getFieldType().equalsIgnoreCase(
						PayAsiaConstants.STATIC_TYPE)) {

					dataDictionaryId
							.add(dataDictionaryVO.getDataDictionaryId());
					fieldName.add(dataDictionaryVO.getLabel());
					ColumnPropertyDTO columnPropertyDTO = generalDAO
							.getColumnProperties(
									dataDictionaryVO.getTableName(),
									dataDictionaryVO.getColumnName());
					dataType.add(columnPropertyDTO.getColumnType());
				} else {
					DynamicForm dynamicForm = dynamicFormDAO
							.findMaxVersionByFormId(companyId, entityId,
									dataDictionaryVO.getFormID());

					Unmarshaller unmarshaller = null;
					try {
						unmarshaller = XMLUtil.getDocumentUnmarshaller();
					} catch (JAXBException e1) {

						LOGGER.error(e1.getMessage(), e1);
					} catch (SAXException e1) {

						LOGGER.error(e1.getMessage(), e1);
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

					Tab tab = null;
					try {
						if (unmarshaller != null) {
							tab = (Tab) unmarshaller.unmarshal(xmlSource);
						}
					} catch (JAXBException e1) {
						LOGGER.error(e1.getMessage(), e1);
						throw new PayAsiaSystemException(e1.getMessage(), e1);
					}

					if (tab != null) {
						List<Field> listOfFields = tab.getField();
						for (Field field : listOfFields) {
							if (!StringUtils.equalsIgnoreCase(field.getType(),
									"table")
									&& !StringUtils.equalsIgnoreCase(
											field.getType(), "label")) {
								if (new String(Base64.decodeBase64(field
										.getDictionaryName().getBytes()))
										.equals(dataDictionaryVO
												.getDataDictName())) {
									dataDictionaryId.add(dataDictionaryVO
											.getDataDictionaryId());
									fieldName.add(dataDictionaryVO.getLabel());
									dataType.add(field.getType());
								}
							} else if ("table".equals(field.getType())) {
								List<Column> listOfColumns = field.getColumn();
								for (Column column : listOfColumns) {
									if (new String(Base64.decodeBase64(column
											.getDictionaryName().getBytes()))
											.equals(dataDictionaryVO
													.getDataDictName())) {
										dataDictionaryId.add(dataDictionaryVO
												.getDataDictionaryId());
										fieldName.add(dataDictionaryVO
												.getLabel());
										dataType.add(field.getType());
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		CalendarTempDataForm calTempDataForm = new CalendarTempDataForm();
		calTempDataForm.setDataDictionaryId(dataDictionaryId);
		calTempDataForm.setFieldName(fieldName);
		calTempDataForm.setDataType(dataType);

		return calTempDataForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CalendarDefLogic#getCodeValueList()
	 */
	@Override
	public List<AppCodeDTO> getCodeValueList() {
		List<AppCodeDTO> appCodeDTOs = new ArrayList<>();

		List<AppCodeMaster> appCodeList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_CALENDAR_TEMPLATE);

		for (AppCodeMaster appCodeMaster : appCodeList) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();
			appCodeDTO.setCodeDesc(appCodeMaster.getCodeDesc());
			appCodeDTO.setAppCodeID(appCodeMaster.getAppCodeID());
			appCodeDTO.setCodeValue(appCodeMaster.getCodeValue());
			appCodeDTOs.add(appCodeDTO);
		}

		return appCodeDTOs;
	}

	@Override
	public List<CalendarDefForm> getCalendarCodeList(Long companyId) {
		List<CalendarDefForm> calendarDefFormList = new ArrayList<>();

		List<CalendarCodeMaster> calCodeList = calendarCodeMasterDAO
				.findByConditionCompany(null, null, companyId);

		for (CalendarCodeMaster calCodeMaster : calCodeList) {
			CalendarDefForm calendarDefForm = new CalendarDefForm();
			calendarDefForm.setCalendarCodeMasterId(calCodeMaster
					.getCalendarCodeId());
			calendarDefForm.setCode(calCodeMaster.getCode());
			calendarDefFormList.add(calendarDefForm);
		}

		return calendarDefFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CalendarDefLogic#addShortListData(java.lang.String)
	 */
	@Override
	public String addShortListData(String shortListData) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CalendarDefLogic#getShortListForEdit(java.lang.Long)
	 */
	@Override
	public CalendarTempShortListResponse getShortListForEdit(Long calTempId) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CalendarDefLogic#editShortListData(java.lang.String)
	 */
	@Override
	public String editShortListData(String shortListData) {

		return null;
	}

	/**
	 * Purpose: To Set the date values to Calendar Template Month Detail entity.
	 * 
	 * @param calendarTemplateMonthDetail
	 *            the calendar template month detail
	 * @param month
	 *            the key
	 * @param calendarTemplateCodeMaster
	 *            the calendar template code master
	 */
	private void setEventList(CalendarDefForm calendarDefForm, String month,
			List<CalendarEventDTO> calendarEventDTOs) {
		Class<?> calendarDefFormClass = calendarDefForm.getClass();
		String colMehtodName = "setEvents" + month;
		Method setMethod;
		try {
			setMethod = calendarDefFormClass.getMethod(colMehtodName,
					List.class);
			setMethod.invoke(calendarDefForm, calendarEventDTOs);

		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PayAsiaSystemException(e.getMessage(), e);
		}

	}

	/**
	 * Purpose: To Validate impoted date codes.
	 * 
	 * @param dateCodeList
	 *            the date code list
	 * @param codeMasterMap
	 *            the code master map
	 * @param dataImportLogDTOs
	 *            the data import log dt os
	 */

	@Override
	public String saveYearlyCalendar(Long calTempId, String yearlyMap) {

		return null;
	}

	@Override
	public CalendarDefResponse viewCalendarCodeValue(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		List<CalendarDefForm> listCalForm = new ArrayList<CalendarDefForm>();

		List<CalendarCodeMaster> calendarCodeMasterList = calendarCodeMasterDAO
				.findByConditionCompany(pageDTO, sortDTO, companyId);

		for (CalendarCodeMaster calCodeMasterVO : calendarCodeMasterList) {
			CalendarDefForm calDefForm = new CalendarDefForm();
			calDefForm.setCode(calCodeMasterVO.getCode());
			calDefForm.setCodeValue(calCodeMasterVO.getAppCodeMaster()
					.getCodeValue());
			/* ID ENCRYPT*/
			calDefForm.setCalendarCodeMasterId(FormatPreserveCryptoUtil.encrypt(calCodeMasterVO
					.getCalendarCodeId()));
			listCalForm.add(calDefForm);
		}

		CalendarDefResponse response = new CalendarDefResponse();
		int recordSize = calendarCodeMasterDAO.getCountForConditionCompany(
				companyId).intValue();
		response.setRows(listCalForm);

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

		return response;
	}

	@Override
	public CalendarDefResponse viewCalendarPatternCode(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {
		List<CalendarDefForm> listCalForm = new ArrayList<CalendarDefForm>();

		List<CalendarPatternMaster> calendarPatternMasterList = calendarPatternMasterDAO
				.findByConditionCompany(pageDTO, sortDTO, companyId);

		for (CalendarPatternMaster calPatternMasterVO : calendarPatternMasterList) {
			CalendarDefForm calDefForm = new CalendarDefForm();
			calDefForm.setPatternName(calPatternMasterVO.getPatternName());
			calDefForm.setPatternDesc(calPatternMasterVO.getPatternDesc());
			/* ID ENCRYPT*/
			calDefForm.setCalendarPatternMasterId(FormatPreserveCryptoUtil.encrypt(calPatternMasterVO
					.getCalendarPatternId()));
			listCalForm.add(calDefForm);
		}

		CalendarDefResponse response = new CalendarDefResponse();
		int recordSize = calendarPatternMasterDAO.getCountForConditionCompany(
				companyId).intValue();
		response.setRows(listCalForm);

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

		return response;
	}

	@Override
	public String addCalendarCodeValue(CalendarDefForm calendarDefForm,
			Long companyId) {
		Boolean status = true;
		Company companyVO = companyDAO.findById(companyId);

		status = isDuplicateCalendarCode(companyId, calendarDefForm.getCode(),
				null);

		if (!status) {
			ModuleMaster moduleMaster = moduleMasterDAO
					.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);

			CalendarCodeMaster codeMaster = new CalendarCodeMaster();
			codeMaster.setCode(calendarDefForm.getCode());
			codeMaster.setCompany(companyVO);
			codeMaster.setModuleMaster(moduleMaster);

			AppCodeMaster appCodeMaster = appCodeMasterDAO.findById(Long
					.parseLong(calendarDefForm.getCodeValue()));
			codeMaster.setAppCodeMaster(appCodeMaster);
			calendarCodeMasterDAO.save(codeMaster);
			return "success";
		} else {
			return "duplicate";
		}

	}

	Boolean isDuplicateCalendarCode(Long companyId, String calendarCode,
			Long calendarCodeMasterId) {
		CalendarCodeMaster codeMaster = calendarCodeMasterDAO
				.findByCodeNameAndCalCodeMasterId(companyId, calendarCode,
						calendarCodeMasterId);
		if (codeMaster != null) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String addCalendarPatternValue(CalendarDefForm calendarDefForm,
			Long companyId) {
		Boolean status = true;
		Company companyVO = companyDAO.findById(companyId);
		status = isDuplicateCalendarPattern(companyId,
				calendarDefForm.getPatternName(), null);
		if (!status) {
			CalendarPatternMaster calendarPatternMaster = new CalendarPatternMaster();
			calendarPatternMaster.setCompany(companyVO);
			calendarPatternMaster.setPatternName(calendarDefForm
					.getPatternName());
			if (StringUtils.isNotBlank(calendarDefForm.getPatternDesc())) {
				calendarPatternMaster.setPatternDesc(calendarDefForm
						.getPatternDesc());
			}
			CalendarPatternMaster persistObj = calendarPatternMasterDAO
					.saveCalPatternMaster(calendarPatternMaster);

			List<CalendarCodeValueDTO> calendarCodeValueDTOs = calendarDefForm
					.getCalendarCodeValueDTOList();
			for (CalendarCodeValueDTO calendarCodeValueDTO : calendarCodeValueDTOs) {
				CalendarPatternDetail calendarPatternDetail = new CalendarPatternDetail();
				if (StringUtils.isNotBlank(calendarCodeValueDTO.getCode())) {
					calendarPatternDetail.setPatternIndex(Integer
							.parseInt(calendarCodeValueDTO.getCode()));
					CalendarCodeMaster calendarCodeMaster = calendarCodeMasterDAO
							.findById(calendarCodeValueDTO.getValueId());
					calendarPatternDetail
							.setCalendarCodeMaster(calendarCodeMaster);
					calendarPatternDetail.setCalendarPatternMaster(persistObj);
					calendarPatternDetailDAO.save(calendarPatternDetail);
				}

			}

			return "success";
		} else {
			return "duplicate";
		}

	}

	Boolean isDuplicateCalendarPattern(Long companyId, String calendarPattern,
			Long CalendarPatternMasterId) {
		CalendarPatternMaster calendarPatternMaster = calendarPatternMasterDAO
				.findByPatternNameAndId(companyId, calendarPattern,
						CalendarPatternMasterId);
		if (calendarPatternMaster != null) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String updateCalendarCodeValue(CalendarDefForm calendarDefForm,
			Long companyId) {
		Boolean status = true;
		Company companyVO = companyDAO.findById(companyId);

		status = isDuplicateCalendarCode(companyId, calendarDefForm.getCode(),
				null);
		if (!status) {
			CalendarCodeMaster codeMaster = calendarCodeMasterDAO
					.findById(calendarDefForm.getCalendarCodeMasterId());
			codeMaster.setCode(calendarDefForm.getCode());
			codeMaster.setCompany(companyVO);

			AppCodeMaster appCodeMaster = appCodeMasterDAO.findById(Long
					.parseLong(calendarDefForm.getCodeValue()));
			codeMaster.setAppCodeMaster(appCodeMaster);
			calendarCodeMasterDAO.update(codeMaster);
			return "success";
		} else {
			return "duplicate";
		}

	}

	@Override
	public String updateCalendarPatternValue(CalendarDefForm calendarDefForm,
			Long companyId) {
		Boolean status = true;
		Company companyVO = companyDAO.findById(companyId);

		status = isDuplicateCalendarPattern(companyId,
				calendarDefForm.getPatternName(),
				calendarDefForm.getCalendarPatternMasterId());
		if (!status) {
			CalendarPatternMaster calendarPatternMaster = calendarPatternMasterDAO
					.findById(calendarDefForm.getCalendarPatternMasterId());
			calendarPatternMaster.setCompany(companyVO);
			calendarPatternMaster.setPatternName(calendarDefForm
					.getPatternName());
			if (StringUtils.isNotBlank(calendarDefForm.getPatternDesc())) {
				calendarPatternMaster.setPatternDesc(calendarDefForm
						.getPatternDesc());
			}
			calendarPatternMasterDAO.update(calendarPatternMaster);

			List<CalendarPatternDetail> calendarPatternDetailList = calendarPatternDetailDAO
					.findListByCalPatternMasterId(calendarDefForm
							.getCalendarPatternMasterId());
			List<CalendarCodeValueDTO> calendarCodeValueDTOs = calendarDefForm
					.getCalendarCodeValueDTOList();
			List<Long> existingIds = new ArrayList<>();

			for (CalendarCodeValueDTO calendarCodeValueDTO : calendarCodeValueDTOs) {
				if (StringUtils.isNotBlank(calendarCodeValueDTO.getCode())) {
					CalendarPatternDetail calendarPatternDetail = new CalendarPatternDetail();
					calendarPatternDetail.setPatternIndex(Integer
							.parseInt(calendarCodeValueDTO.getCode()));

					CalendarCodeMaster calendarCodeMaster = calendarCodeMasterDAO
							.findById(calendarCodeValueDTO.getValueId());
					calendarPatternDetail
							.setCalendarCodeMaster(calendarCodeMaster);
					calendarPatternDetail
							.setCalendarPatternMaster(calendarPatternMaster);

					if (calendarCodeValueDTO.getCodeValId() == 0) {
						calendarPatternDetailDAO.save(calendarPatternDetail);
					} else {
						existingIds.add(calendarCodeValueDTO.getCodeValId());
						calendarPatternDetail
								.setCalendarPatternDetailId(calendarCodeValueDTO
										.getCodeValId());
						calendarPatternDetailDAO.update(calendarPatternDetail);
					}

				}
			}

			if (calendarPatternDetailList != null) {
				for (CalendarPatternDetail calendarPatternDetail : calendarPatternDetailList) {
					if (!existingIds.contains(calendarPatternDetail
							.getCalendarPatternDetailId())) {
						calendarPatternDetailDAO.delete(calendarPatternDetail);
					}
				}
			}

			return "success";
		} else {
			return "duplicate";
		}
	}

	@Override
	public CalendarDefForm getCalCodeForEdit(Long calCodeId) {

		CalendarDefForm calendarDefForm = new CalendarDefForm();
		CalendarCodeMaster codeMaster = calendarCodeMasterDAO
				.findById(calCodeId);
		if (codeMaster != null) {
			calendarDefForm.setCode(codeMaster.getCode());
			calendarDefForm.setCodeValue(String.valueOf(codeMaster
					.getAppCodeMaster().getAppCodeID()));
			calendarDefForm.setCalendarCodeMasterId(codeMaster
					.getCalendarCodeId());
		}

		return calendarDefForm;
	}

	@Override
	public CalendarDefForm getCalPatterneForEdit(Long calPatternId) {
		CalendarDefForm calendarDefForm = new CalendarDefForm();
		List<CalendarCodeValueDTO> calendarCodeValueDTOList = new ArrayList<>();

		CalendarPatternMaster codePatternVO = calendarPatternMasterDAO
				.findById(calPatternId);
		calendarDefForm.setPatternName(codePatternVO.getPatternName());
		calendarDefForm.setPatternDesc(codePatternVO.getPatternDesc());

		List<CalendarPatternDetail> codePatternDetailListVO = calendarPatternDetailDAO
				.findListByCalPatternMasterId(calPatternId);
		if (!codePatternDetailListVO.isEmpty()) {
			for (CalendarPatternDetail patternDetail : codePatternDetailListVO) {
				CalendarCodeValueDTO calendarCodeValueDTO = new CalendarCodeValueDTO();
				calendarCodeValueDTO.setCodeValId(patternDetail
						.getCalendarPatternDetailId());
				calendarCodeValueDTO.setValueId(patternDetail
						.getCalendarCodeMaster().getCalendarCodeId());
				calendarCodeValueDTO.setCode(String.valueOf(patternDetail
						.getPatternIndex()));
				calendarCodeValueDTOList.add(calendarCodeValueDTO);
			}
			calendarDefForm
					.setCalendarCodeValueDTOList(calendarCodeValueDTOList);
			calendarDefForm.setCalendarPatternMasterId(codePatternVO
					.getCalendarPatternId());
			List<EmployeeCalendarConfig> employeeCalendarConfig = employeeCalendarConfigDAO
					.findByCalPatternId(calPatternId);
			if (!employeeCalendarConfig.isEmpty()) {
				calendarDefForm.setIsCalendarAssigned(true);
			} else {
				calendarDefForm.setIsCalendarAssigned(false);
			}

		}

		return calendarDefForm;
	}

	@Override
	public void deleteCalCode(Long calCodeId) {
		CalendarCodeMaster codeMaster = calendarCodeMasterDAO
				.findById(calCodeId);

		calendarCodeMasterDAO.delete(codeMaster);

	}

	@Override
	public void deleteCalPattern(Long calPatternId) {
		CalendarPatternMaster calendarPatternMaster = calendarPatternMasterDAO
				.findById(calPatternId);

		calendarPatternMasterDAO.delete(calendarPatternMaster);
	}

	Boolean isDuplicateCalendarTemplate(Long companyId,
			String calendarTemplateName, Long companyCalTemplateId) {
		CompanyCalendarTemplate companyCalendarTemplate = companyCalendarTemplateDAO
				.findByTemplateNameAndId(companyId, calendarTemplateName,
						companyCalTemplateId);
		if (companyCalendarTemplate != null) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String saveCalTemplate(CalendarDefForm calDefForm, Long companyId) {
		Boolean status = true;
		Company companyVO = companyDAO.findById(companyId);

		status = isDuplicateCalendarTemplate(companyId,
				calDefForm.getCalDefName(), null);
		if (!status) {
			CompanyCalendarTemplate companyCalendarTemplate = new CompanyCalendarTemplate();
			companyCalendarTemplate.setCompany(companyVO);
			companyCalendarTemplate.setTemplateName(calDefForm.getCalDefName());
			companyCalendarTemplate
					.setTemplateDesc(calDefForm.getDescription());
			companyCalendarTemplate.setStart_Year(calDefForm.getYear());

			CalendarPatternMaster calendarPatternMaster = calendarPatternMasterDAO
					.findById(calDefForm.getCalendarPatternMasterId());
			companyCalendarTemplate
					.setCalendarPatternMaster(calendarPatternMaster);
			companyCalendarTemplateDAO.save(companyCalendarTemplate);
			return "success";
		} else {
			return "duplicate";
		}
	}

	@Override
	public String editCalTemplate(CalendarDefForm calDefForm, Long companyId) {
		Boolean status = true;
		Company companyVO = companyDAO.findById(companyId);
		/* ID DECRYPT */
		calDefForm.setCompanyCalendarTemplateId(FormatPreserveCryptoUtil.decrypt(calDefForm.getCompanyCalendarTemplateId()));
		status = isDuplicateCalendarTemplate(companyId,
				calDefForm.getCalDefName(),
				calDefForm.getCompanyCalendarTemplateId());

		if (!status) {
			CompanyCalendarTemplate companyCalendarTemplate = companyCalendarTemplateDAO
					.findByID(calDefForm.getCompanyCalendarTemplateId());
			companyCalendarTemplate.setCompany(companyVO);
			companyCalendarTemplate.setTemplateName(calDefForm.getCalDefName());
			if (StringUtils.isNotBlank(calDefForm.getDescription())) {
				companyCalendarTemplate.setTemplateDesc(calDefForm
						.getDescription());
			}
			companyCalendarTemplate.setStart_Year(calDefForm.getYear());

			CalendarPatternMaster calendarPatternMaster = calendarPatternMasterDAO
					.findById(calDefForm.getCalendarPatternMasterIdEdit());
			companyCalendarTemplate
					.setCalendarPatternMaster(calendarPatternMaster);
			companyCalendarTemplateDAO.update(companyCalendarTemplate);
			return "success";
		} else {
			return "duplicate";
		}
	}

	@Override
	public List<CalendarDefForm> getPatternCodeList(Long companyId) {
		List<CalendarDefForm> calendarDefFormList = new ArrayList<>();
		List<CalendarPatternMaster> calendarPatternMasterList = calendarPatternMasterDAO
				.findByConditionCompany(null, null, companyId);
		for (CalendarPatternMaster calendarPatternMaster : calendarPatternMasterList) {
			CalendarDefForm calendarDefForm = new CalendarDefForm();
			calendarDefForm.setCalendarPatternMasterId(calendarPatternMaster
					.getCalendarPatternId());
			calendarDefForm.setPatternName(calendarPatternMaster
					.getPatternName());
			calendarDefFormList.add(calendarDefForm);
		}

		return calendarDefFormList;
	}

	@Override
	public CalendarDefForm getDataForCompanyCalTemplate(
			Long companyCalTemplateId) {
		CalendarDefForm calendarDefForm = new CalendarDefForm();

		CompanyCalendarTemplate companyCalendarTemplateVO = companyCalendarTemplateDAO
				.findByID(companyCalTemplateId);
		List<EmployeeCalendarConfig> employeeCalendarConfig = employeeCalendarConfigDAO
				.findByCalTempId(companyCalTemplateId);
		if (companyCalendarTemplateVO != null) {
			calendarDefForm.setCalDefName(companyCalendarTemplateVO
					.getTemplateName());
			calendarDefForm.setDescription(companyCalendarTemplateVO
					.getTemplateDesc());
			calendarDefForm
					.setCalendarPatternMasterId(companyCalendarTemplateVO
							.getCalendarPatternMaster().getCalendarPatternId());
			calendarDefForm.setYear(companyCalendarTemplateVO.getStart_Year());
			if (!employeeCalendarConfig.isEmpty()) {
				calendarDefForm.setIsCalendarAssigned(true);
			} else {
				calendarDefForm.setIsCalendarAssigned(false);
			}
		}
		return calendarDefForm;
	}

	@Override
	public void saveCalEventByDate(Long calTempId, String calCode,
			String eventDate, Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		CompanyCalendarTemplate calendarTemplate = companyCalendarTemplateDAO
				.findByID(calTempId);

		CalendarCodeMaster calendarCodeMaster = calendarCodeMasterDAO
				.findByCodeName(companyId, calCode);

		CompanyCalendar companyCalendarVO = companyCalendarDAO.findByDate(
				eventDate, companyVO.getDateFormat());

		CompanyCalendar companyCalendar = new CompanyCalendar();
		companyCalendar.setCalendarDate(DateUtils.stringToTimestamp(eventDate,
				PayAsiaConstants.DEFAULT_DATE_FORMAT));
		companyCalendar.setCompanyCalendarTemplate(calendarTemplate);

		companyCalendar.setCalendarCodeMaster(calendarCodeMaster);
		if (companyCalendarVO != null) {
			companyCalendarDAO.update(companyCalendar);
		} else {
			companyCalendarDAO.save(companyCalendar);
		}

	}

	@Override
	public List<CalendarDefForm> getCalTemConfigYearList(Long calConfigId,
			Long companyId) {
		/* ID DECRYPT */
		calConfigId= FormatPreserveCryptoUtil.decrypt(calConfigId);
		List<CalendarDefForm> calendarDefFormList = new ArrayList<>();
		CompanyCalendarTemplate companyCalendarTemplate = companyCalendarTemplateDAO
				.findByID(calConfigId);

		int startyear = companyCalendarTemplate.getStart_Year();
		for (int count = 0; count < 10; count++) {
			CalendarDefForm calendarDefForm = new CalendarDefForm();
			calendarDefForm.setYear(startyear + count);
			calendarDefFormList.add(calendarDefForm);
		}
		return calendarDefFormList;

	}
}
