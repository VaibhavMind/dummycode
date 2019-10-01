package com.payasia.logic.impl;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

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
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.logic.GeneralFilterLogic;

@Component
public class GeneralFilterLogicImpl implements GeneralFilterLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(GeneralFilterLogicImpl.class);

	@Resource
	CompanyDAO companyDAO;

	@Resource
	GeneralDAO generalDAO;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;
	@Resource
	LeavePreferenceDAO leavePreferenceDAO;

	@Override
	public String createWhere(Long employeeId,
			List<GeneralFilterDTO> finalFilterList, Long companyId,
			Map<String, String> paramValueMap) {
		String where = " WHERE employee.Employee_ID =:employeeId ";
		return createSubWhere(where, finalFilterList, companyId, null,
				paramValueMap);
	}

	@Override
	public String createWhereWithCount(Long employeeId,
			List<GeneralFilterDTO> finalFilterList, Long companyId,
			Map<String, String> paramValueMap, int count) {
		String where = " WHERE employee.Employee_ID =:employeeId ";
		return createSubWhereWithCount(where, finalFilterList, companyId, null,
				paramValueMap, count);
	}

	@Override
	public String createFiltersWhere(Long employeeId,
			List<GeneralFilterDTO> finalFilterList, Long companyId,
			Date currentdate, Map<String, String> paramValueMap) {
		String where = " WHERE employee.company_id =:companyId  ";
		return createSubWhere(where, finalFilterList, companyId, currentdate,
				paramValueMap);

	}

	@Override
	public String createSubWhere(String where,
			List<GeneralFilterDTO> finalFilterList, Long companyId,
			Date currentdate, Map<String, String> paramValueMap) {
		int count = 1;
		return createSubWhere(where, finalFilterList, companyId, currentdate,
				paramValueMap, count);
	}

	@Override
	public String createSubWhereWithCount(String where,
			List<GeneralFilterDTO> finalFilterList, Long companyId,
			Date currentdate, Map<String, String> paramValueMap, int count) {
		return createSubWhere(where, finalFilterList, companyId, currentdate,
				paramValueMap, count);
	}

	private String createSubWhere(String where,
			List<GeneralFilterDTO> finalFilterList, Long companyId,
			Date currentdate, Map<String, String> paramValueMap, int count) {

		if (!finalFilterList.isEmpty()) {
			where = where + " AND ( ";
		}

		List<String> sysMonthOperandList = new ArrayList<String>();
		sysMonthOperandList.add(PayAsiaConstants.IS_EQUAL_TO_SYSMONTH_BY);
		sysMonthOperandList
				.add(PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSMONTH_BY);
		sysMonthOperandList.add(PayAsiaConstants.IS_GREATER_THAN_SYSMONTH_BY);
		sysMonthOperandList
				.add(PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSMONTH_BY);
		sysMonthOperandList.add(PayAsiaConstants.IS_LESS_THAN_SYSMONTH_BY);
		sysMonthOperandList.add(PayAsiaConstants.IS_NOT_EQUAL_TO_SYSMONTH_BY);

		int finalFilterListCount = 1;
		for (GeneralFilterDTO generalFilterDTO : finalFilterList) {
			Boolean sysDateField = false;
			String sysDatePartField = "day";

			ColumnPropertyDTO columnPropertyDTO = null;
			if (generalFilterDTO.getEqualityOperator().equals(
					PayAsiaConstants.IS_EQUAL_TO_SYSDATE_BY)
					|| generalFilterDTO
							.getEqualityOperator()
							.equals(PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSDATE_BY)
					|| generalFilterDTO.getEqualityOperator().equals(
							PayAsiaConstants.IS_GREATER_THAN_SYSDATE_BY)
					|| generalFilterDTO.getEqualityOperator().equals(
							PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSDATE_BY)
					|| generalFilterDTO.getEqualityOperator().equals(
							PayAsiaConstants.IS_LESS_THAN_SYSDATE_BY)
					|| generalFilterDTO.getEqualityOperator().equals(
							PayAsiaConstants.IS_NOT_EQUAL_TO_SYSDATE_BY)) {
				sysDateField = true;
				sysDatePartField = "day";
			} else if (sysMonthOperandList.contains(generalFilterDTO
					.getEqualityOperator())) {
				sysDateField = true;
				sysDatePartField = "month";
			} else {
				sysDateField = false;
			}

			if (generalFilterDTO.getDataImportKeyValueDTO().isStatic()) {

				columnPropertyDTO = generalDAO.getColumnProperties(
						PayAsiaConstants.EMPLOYEE_TABLE_NAME, generalFilterDTO
								.getDataImportKeyValueDTO().getActualColName());

				if (sysDateField) {
					paramValueMap.put("sysdateParam" + count,
							generalFilterDTO.getValue());
					where = where + " "
							+ generalFilterDTO.getOpenBracket()
							+ " ( "
							+ "cast(dateadd("
							+ sysDatePartField
							+ ",:sysdateParam"
							+ count
							// + generalFilterDTO.getValue()
							+ ",employee."
							+ generalFilterDTO.getDataImportKeyValueDTO()
									.getActualColName() + ") as date)" + " ";

				} else {
					where = where
							+ " "
							+ generalFilterDTO.getOpenBracket()
							+ " ( "
							+ " employee."
							+ generalFilterDTO.getDataImportKeyValueDTO()
									.getActualColName() + " ";
				}

			} else {

				if (!generalFilterDTO.getDataImportKeyValueDTO().isChild()) {

					if (generalFilterDTO.getDataImportKeyValueDTO()
							.getFieldType().equalsIgnoreCase("numeric")) {
						where = where
								+ " "
								+ generalFilterDTO.getOpenBracket()
								+ " ( "
								+ "CAST(dynamicFormRecord"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getFormId()
								+ ".Col_"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getMethodName() + " as int ) ";
					} else if (generalFilterDTO.getDataImportKeyValueDTO()
							.getFieldType().equalsIgnoreCase("date")) {

						if (sysDateField) {
							paramValueMap.put("sysdateParam" + count,
									generalFilterDTO.getValue());
							where = where
									+ " "
									+ generalFilterDTO.getOpenBracket()
									+ " ( "
									+ "dateadd("
									+ sysDatePartField
									+ ",:sysdateParam"
									+ count
									+ ",convert(date, dynamicFormRecord"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName() + ")) ";

						} else {
							where = where
									+ " "
									+ generalFilterDTO.getOpenBracket()
									+ " ( "
									+ "convert(datetime, dynamicFormRecord"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName() + ") ";
						}

					} else if (generalFilterDTO.getDataImportKeyValueDTO()
							.getFieldType().equalsIgnoreCase("codedesc")) {

						where = where
								+ " "
								+ generalFilterDTO.getOpenBracket()
								+ " ( "
								+ " dynamicFormFieldRefValue"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getFormId()
								+ "Col_"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getMethodName() + ".Code ";

					} else {
						where = where
								+ " "
								+ generalFilterDTO.getOpenBracket()
								+ " ( "
								+ " dynamicFormRecord"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getFormId()
								+ ".Col_"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getMethodName() + " ";
					}
				} else {

					String dynamicFormTableRecord = "dynamicFormTableRecordT"
							+ generalFilterDTO.getDataImportKeyValueDTO()
									.getFormId()
							+ generalFilterDTO.getDataImportKeyValueDTO()
									.getTablePosition();

					if (generalFilterDTO.getDataImportKeyValueDTO()
							.getFieldType().equalsIgnoreCase("numeric")) {
						where = where
								+ " "
								+ generalFilterDTO.getOpenBracket()
								+ " ( "
								+ "CAST("
								+ dynamicFormTableRecord
								+ ".Col_"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getMethodName() + " as int ) ";
					} else if (generalFilterDTO.getDataImportKeyValueDTO()
							.getFieldType().equalsIgnoreCase("date")) {

						if (sysDateField) {
							paramValueMap.put("sysdateParam" + count,
									generalFilterDTO.getValue());
							where = where
									+ " "
									+ generalFilterDTO.getOpenBracket()
									+ " ( "
									+ "dateadd("
									+ sysDatePartField
									+ ",:sysdateParam"
									+ count
									+ ",convert(date, ltrim("
									+ dynamicFormTableRecord
									+ ".Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName() + ")))";

						} else {

							where = where
									+ " "
									+ generalFilterDTO.getOpenBracket()
									+ " ( "
									+ "convert(datetime, ltrim("
									+ dynamicFormTableRecord
									+ ".Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName() + ")) ";

						}

					} else if (generalFilterDTO.getDataImportKeyValueDTO()
							.getFieldType().equalsIgnoreCase("codedesc")) {
						where = where
								+ " "
								+ generalFilterDTO.getOpenBracket()
								+ " ( "
								+ " dynamicFormFieldRefValueT"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getFormId()
								+ "Col_"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getMethodName() + ".Code ";
					} else {
						where = where
								+ " "
								+ generalFilterDTO.getOpenBracket()
								+ " ( "
								+ " "
								+ dynamicFormTableRecord
								+ ".Col_"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getMethodName() + " ";
					}
				}

			}

			String[] fieldNameDetail = generalFilterDTO.getDataDictionaryName()
					.split("\\.");
			if (fieldNameDetail.length == PayAsiaConstants.PAYASIA_EMP_DYNAMIC_TABLE_FIELD_DICT_NAME_DIVISION_LENGTH
					&& StringUtils.isNotBlank(fieldNameDetail[1])
					&& StringUtils.isNotBlank(fieldNameDetail[2])
					&& fieldNameDetail[1]
							.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
					&& fieldNameDetail[2]
							.equalsIgnoreCase(PayAsiaConstants.DEPENDENTS_DATE_OF_BIRTH_FIELD)) {
				paramValueMap.put("param" + count,
						getLeaveCalendarStartDateParam(companyId));
			} else {
				paramValueMap.put(
						"param" + count,
						getEqualityOperatorValue(
								generalFilterDTO.getEqualityOperator(),
								generalFilterDTO.getValue(), companyId,
								columnPropertyDTO, generalFilterDTO,
								currentdate));
			}

			where = where
					+ getEqualityOperator(generalFilterDTO
							.getEqualityOperator())
					// + " N'"
					+ ":param" + count + " ";
			// + "' ";

			if (getEqualityOperator(generalFilterDTO.getEqualityOperator())
					.equalsIgnoreCase("!=")) {
				if (generalFilterDTO.getDataImportKeyValueDTO().isStatic()) {
					where = where
							+ " or  employee."
							+ generalFilterDTO.getDataImportKeyValueDTO()
									.getActualColName() + " is null ) ";
				} else {

					if (!generalFilterDTO.getDataImportKeyValueDTO().isChild()) {

						if (generalFilterDTO.getDataImportKeyValueDTO()
								.getFieldType().equalsIgnoreCase("numeric")) {
							where = where
									+ " or "
									+ "CAST(dynamicFormRecord"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName()
									+ " as int ) is null ) ";
						} else if (generalFilterDTO.getDataImportKeyValueDTO()
								.getFieldType().equalsIgnoreCase("date")) {

							if (sysDateField) {
								paramValueMap.put("sysdateParam" + count,
										generalFilterDTO.getValue());
								where = where
										+ " or "
										+ "dateadd("
										+ sysDatePartField
										+ ",:sysdateParam"
										+ count
										+ ",convert(date, dynamicFormRecord"
										+ generalFilterDTO
												.getDataImportKeyValueDTO()
												.getFormId()
										+ ".Col_"
										+ generalFilterDTO
												.getDataImportKeyValueDTO()
												.getMethodName()
										+ ")) is null ) ";

							} else {
								where = where
										+ " or "
										+ "convert(datetime, dynamicFormRecord"
										+ generalFilterDTO
												.getDataImportKeyValueDTO()
												.getFormId()
										+ ".Col_"
										+ generalFilterDTO
												.getDataImportKeyValueDTO()
												.getMethodName()
										+ ") is null ) ";
							}

						} else if (generalFilterDTO.getDataImportKeyValueDTO()
								.getFieldType().equalsIgnoreCase("codedesc")) {

							where = where
									+ " or "
									+ " dynamicFormFieldRefValue"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getFormId()
									+ "Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName()
									+ ".Code is null ) ";

						} else {
							where = where
									+ " or "
									+ " dynamicFormRecord"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getFormId()
									+ ".Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName() + " is null ) ";
						}
					} else {

						String dynamicFormTableRecord = "dynamicFormTableRecordT"
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getFormId()
								+ generalFilterDTO.getDataImportKeyValueDTO()
										.getTablePosition();

						if (generalFilterDTO.getDataImportKeyValueDTO()
								.getFieldType().equalsIgnoreCase("numeric")) {
							where = where
									+ " or "
									+ "CAST("
									+ dynamicFormTableRecord
									+ ".Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName()
									+ " as int ) is null ) ";
						} else if (generalFilterDTO.getDataImportKeyValueDTO()
								.getFieldType().equalsIgnoreCase("date")) {

							if (sysDateField) {
								paramValueMap.put("sysdateParam" + count,
										generalFilterDTO.getValue());
								where = where
										+ " or "
										+ "dateadd("
										+ sysDatePartField
										+ ",:sysdateParam"
										+ count
										+ ",convert(date, ltrim("
										+ dynamicFormTableRecord
										+ ".Col_"
										+ generalFilterDTO
												.getDataImportKeyValueDTO()
												.getMethodName()
										+ "))) is null ) ";

							} else {

								where = where
										+ " or "
										+ "convert(datetime, ltrim("
										+ dynamicFormTableRecord
										+ ".Col_"
										+ generalFilterDTO
												.getDataImportKeyValueDTO()
												.getMethodName()
										+ ")) is null ) ";

							}

						} else if (generalFilterDTO.getDataImportKeyValueDTO()
								.getFieldType().equalsIgnoreCase("codedesc")) {
							where = where
									+ " or "
									+ " dynamicFormFieldRefValueT"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getFormId()
									+ "Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName()
									+ ".Code is null ) ";
						} else {
							where = where
									+ " or "
									+ dynamicFormTableRecord
									+ ".Col_"
									+ generalFilterDTO
											.getDataImportKeyValueDTO()
											.getMethodName() + " is null ) ";
						}
					}

				}
			} else {
				where = where + ") ";
			}

			where = where + generalFilterDTO.getCloseBracket() + " ";

			if (finalFilterListCount < finalFilterList.size()) {
				where = where + generalFilterDTO.getLogicalOperator() + " ";
			}
			finalFilterListCount++;
			count++;
		}
		if (!finalFilterList.isEmpty()) {
			where = where + " ) ";
		}
		return where;

	}

	private String getLeaveCalendarStartDateParam(Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		Calendar cal = Calendar.getInstance();
		// Leave Preference By Company
		LeavePreference leavePreferenceVO = leavePreferenceDAO
				.findByCompanyId(companyId);
		if (leavePreferenceVO != null
				&& leavePreferenceVO.getStartMonth() != null) {

			int startMonth = 0;
			startMonth = Integer.parseInt(String.valueOf(leavePreferenceVO
					.getStartMonth().getMonthId())) - 1;

			Calendar startCalendar = new GregorianCalendar(
					cal.get(Calendar.YEAR), startMonth, 1, 0, 0, 0);
			Date calStartDate = startCalendar.getTime();

			Date date = DateUtils.getDateAccToTimeZone(calStartDate,
					leavePreferenceVO.getCompany().getTimeZoneMaster()
							.getGmtOffset());
			SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy-MM-dd");
			return sdfDestination.format(date);
		}

		// Current Date
		Date date = DateUtils.getDateAccToTimeZone(cal.getTime(), companyVO
				.getTimeZoneMaster().getGmtOffset());
		SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy-MM-dd");
		return sdfDestination.format(date);
	}

	private String getEqualityOperator(String equalityOperator) {

		switch (equalityOperator) {
		case PayAsiaConstants.IS_EQUAL_TO_SYSDATE_BY:
			equalityOperator = "=";

			break;

		case PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSDATE_BY:

			equalityOperator = "<=";
			break;

		case PayAsiaConstants.IS_GREATER_THAN_SYSDATE_BY:
			equalityOperator = "<";

			break;

		case PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSDATE_BY:
			equalityOperator = ">=";
			break;

		case PayAsiaConstants.IS_LESS_THAN_SYSDATE_BY:
			equalityOperator = ">";
			break;

		case PayAsiaConstants.IS_NOT_EQUAL_TO_SYSDATE_BY:
			equalityOperator = "!=";
			break;

		case PayAsiaConstants.IS_EQUAL_TO_SYSMONTH_BY:
			equalityOperator = "=";

			break;

		case PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSMONTH_BY:

			equalityOperator = "<=";
			break;

		case PayAsiaConstants.IS_GREATER_THAN_SYSMONTH_BY:
			equalityOperator = "<";

			break;

		case PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSMONTH_BY:
			equalityOperator = ">=";
			break;

		case PayAsiaConstants.IS_LESS_THAN_SYSMONTH_BY:
			equalityOperator = ">";
			break;

		case PayAsiaConstants.IS_NOT_EQUAL_TO_SYSMONTH_BY:
			equalityOperator = "!=";
			break;

		}

		return equalityOperator;

	}

	private String getEqualityOperatorValue(String equalityOperator,
			String equalityOperatorValue, Long companyId,
			ColumnPropertyDTO columnPropertyDTO,
			GeneralFilterDTO generalFilterDTO, Date currentDate) {

		Company companyVO = companyDAO.findById(companyId);

		List<String> sysDateMonthOperandList = new ArrayList<String>();
		sysDateMonthOperandList.add(PayAsiaConstants.IS_EQUAL_TO_SYSDATE_BY);
		sysDateMonthOperandList
				.add(PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSDATE_BY);
		sysDateMonthOperandList
				.add(PayAsiaConstants.IS_GREATER_THAN_SYSDATE_BY);
		sysDateMonthOperandList
				.add(PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSDATE_BY);
		sysDateMonthOperandList.add(PayAsiaConstants.IS_LESS_THAN_SYSDATE_BY);
		sysDateMonthOperandList
				.add(PayAsiaConstants.IS_NOT_EQUAL_TO_SYSDATE_BY);
		sysDateMonthOperandList.add(PayAsiaConstants.IS_EQUAL_TO_SYSMONTH_BY);
		sysDateMonthOperandList
				.add(PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSMONTH_BY);
		sysDateMonthOperandList
				.add(PayAsiaConstants.IS_GREATER_THAN_SYSMONTH_BY);
		sysDateMonthOperandList
				.add(PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSMONTH_BY);
		sysDateMonthOperandList.add(PayAsiaConstants.IS_LESS_THAN_SYSMONTH_BY);
		sysDateMonthOperandList
				.add(PayAsiaConstants.IS_NOT_EQUAL_TO_SYSMONTH_BY);

		if (sysDateMonthOperandList.contains(equalityOperator)) {

			Calendar calCurrentDate = Calendar.getInstance();
			if (currentDate == null) {
				currentDate = calCurrentDate.getTime();
			}

			Date date = DateUtils.getDateAccToTimeZone(currentDate, companyVO
					.getTimeZoneMaster().getGmtOffset());
			SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy-MM-dd");
			return sdfDestination.format(date);

		} else {

			if (generalFilterDTO.getDataImportKeyValueDTO().isStatic()) {

				if ("bit".equals(columnPropertyDTO.getColumnType())) {

					if (generalFilterDTO.getValue().equalsIgnoreCase(
							PayAsiaConstants.SHORTLIST_ENABLE)) {
						equalityOperatorValue = "1";

					} else if (generalFilterDTO.getValue().equalsIgnoreCase(
							PayAsiaConstants.SHORTLIST_DISABLE)) {
						equalityOperatorValue = "0";

					} else {
						equalityOperatorValue = "-1";

					}

				}
			}

			return equalityOperatorValue;
		}

	}

	@Override
	public String getFieldDataType(Long companyId, DataDictionary dataDictionary) {

		String dataType = "";

		if (dataDictionary.getFieldType().equalsIgnoreCase("s")) {
			if ("Confirmation_Date".equals(dataDictionary.getColumnName())
					|| "Hire_Date".equals(dataDictionary.getColumnName())
					|| dataDictionary.getColumnName().equals(
							"Original_Hire_Date")
					|| dataDictionary.getColumnName()
							.equals("Resignation_Date")) {
				dataType = "date";

			}
			return dataType;

		}

		EntityMaster entityMaster = entityMasterDAO
				.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(
				companyId, entityMaster.getEntityId(),
				dataDictionary.getFormID());
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (SAXException ex) {

			LOGGER.error(ex.getMessage(), ex);
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
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}
		List<Field> listOfFields = tab.getField();

		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(),
					PayAsiaConstants.DEPENDENTS_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.TABLE_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.LABEL_FIELD_TYPE)
					&& !StringUtils.equalsIgnoreCase(field.getType(),
							PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())) {
					dataType = field.getType();

				}

			} else if (field.getType()
					.equals(PayAsiaConstants.TABLE_FIELD_TYPE)
					|| field.getType().equals(
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				List<Column> listOfColumns = field.getColumn();
				for (Column column : listOfColumns) {
					if (new String(Base64.decodeBase64(column
							.getDictionaryName().getBytes()))
							.equals(dataDictionary.getDataDictName())) {
						dataType = column.getType();
					}
				}
			}
		}

		return dataType;

	}
}
