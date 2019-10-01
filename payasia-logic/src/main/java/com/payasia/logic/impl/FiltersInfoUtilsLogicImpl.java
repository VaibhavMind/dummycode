package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.logic.DataExportUtils;
import com.payasia.logic.FiltersInfoUtilsLogic;
import com.payasia.logic.GeneralFilterLogic;

@Component
public class FiltersInfoUtilsLogicImpl implements FiltersInfoUtilsLogic {
	private static final Logger LOGGER = Logger
			.getLogger(FiltersInfoUtilsLogicImpl.class);
	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	DataExportUtils dataExportUtils;

	@Resource
	GeneralDAO generalDAO;

	@Resource
	GeneralFilterLogic generalFilterLogic;

	@Override
	public void getDynamicDictionaryInfo(DataDictionary dataDictionary,
			DataImportKeyValueDTO dataImportKeyValueDTO,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs) {
		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(dataDictionary
				.getCompany().getCompanyId(), dataDictionary.getEntityMaster()
				.getEntityId(), dataDictionary.getFormID());

		DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
				dataDictionary.getCompany().getCompanyId(), dataDictionary
						.getEntityMaster().getEntityId(), maxVersion,
				dataDictionary.getFormID());

		Tab tab = dataExportUtils.getTabObject(dynamicForm);

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(), "dependents")
					&& !StringUtils.equalsIgnoreCase(field.getType(), "table")
					&& !StringUtils.equalsIgnoreCase(field.getType(), "label")) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
							.getColNumber(field.getName()));
					dataImportKeyValueDTO.setFormId(dynamicForm.getId()
							.getFormId());

					if (StringUtils.equalsIgnoreCase(field.getType(),
							"codedesc")) {
						CodeDescDTO codeDescDTO = new CodeDescDTO();
						codeDescDTO.setMethodName("Col_"
								+ PayAsiaStringUtils.getColNumber(field
										.getName()));
						codeDescDTO.setFormId(dynamicForm.getId().getFormId());
						codeDescDTO.setChildVal(false);
						codeDescDTOs.add(codeDescDTO);
					}

				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(), "table")
					|| StringUtils.equalsIgnoreCase(field.getType(),
							"dependents")) {

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

						if (StringUtils.equalsIgnoreCase(column.getType(),
								"codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName("Col_"
									+ PayAsiaStringUtils.getColNumber(column
											.getName()));
							codeDescDTO.setFormId(dynamicForm.getId()
									.getFormId());
							codeDescDTO.setChildVal(true);
							codeDescDTOs.add(codeDescDTO);
						}

						DynamicTableDTO dynamicTableDTO = new DynamicTableDTO();
						dynamicTableDTO.setFormId(dataDictionary.getFormID());
						dynamicTableDTO.setTableName(PayAsiaStringUtils
								.getColNumber(field.getName()));
						tableNames.add(dynamicTableDTO);

					}
				}
			}
		}
	}

	@Override
	public void getDynamicDictionaryInfo(DataDictionary dataDictionary,
			DataImportKeyValueDTO dataImportKeyValueDTO,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			HashMap<Long, Tab> tabMap) {

		Tab tab = tabMap.get(dataDictionary.getFormID());

		if (tab == null) {
			int maxVersion = dynamicFormDAO.getMaxVersionByFormId(
					dataDictionary.getCompany().getCompanyId(), dataDictionary
							.getEntityMaster().getEntityId(), dataDictionary
							.getFormID());

			DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(
					dataDictionary.getCompany().getCompanyId(), dataDictionary
							.getEntityMaster().getEntityId(), maxVersion,
					dataDictionary.getFormID());
			tab = dataExportUtils.getTabObject(dynamicForm);
			tabMap.put(dataDictionary.getFormID(), tab);
		}

		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(), "dependents")
					&& !StringUtils.equalsIgnoreCase(field.getType(), "table")
					&& !StringUtils.equalsIgnoreCase(field.getType(), "label")) {
				if (new String(Base64.decodeBase64(field.getDictionaryName()
						.getBytes())).equals(dataDictionary.getDataDictName())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils
							.getColNumber(field.getName()));
					dataImportKeyValueDTO.setFormId(dataDictionary.getFormID());

					if (StringUtils.equalsIgnoreCase(field.getType(),
							"codedesc")) {
						CodeDescDTO codeDescDTO = new CodeDescDTO();
						codeDescDTO.setMethodName("Col_"
								+ PayAsiaStringUtils.getColNumber(field
										.getName()));
						codeDescDTO.setFormId(dataDictionary.getFormID());
						codeDescDTO.setChildVal(false);
						codeDescDTOs.add(codeDescDTO);
					}

				}
			} else if (StringUtils.equalsIgnoreCase(field.getType(), "table")
					|| StringUtils.equalsIgnoreCase(field.getType(),
							"dependents")) {

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
						dataImportKeyValueDTO.setFormId(dataDictionary
								.getFormID());
						dataImportKeyValueDTO
								.setTablePosition(PayAsiaStringUtils
										.getColNumber(field.getName()));

						if (StringUtils.equalsIgnoreCase(column.getType(),
								"codedesc")) {
							CodeDescDTO codeDescDTO = new CodeDescDTO();
							codeDescDTO.setMethodName("Col_"
									+ PayAsiaStringUtils.getColNumber(column
											.getName()));
							codeDescDTO.setFormId(dataDictionary.getFormID());
							codeDescDTO.setChildVal(true);
							codeDescDTOs.add(codeDescDTO);
						}

						DynamicTableDTO dynamicTableDTO = new DynamicTableDTO();
						dynamicTableDTO.setFormId(dataDictionary.getFormID());
						dynamicTableDTO.setTableName(PayAsiaStringUtils
								.getColNumber(field.getName()));
						dynamicTableDTO.setFieldType(field.getType());
						tableNames.add(dynamicTableDTO);

					}
				}
			}
		}
	}

	@Override
	public void getStaticDictionaryInfo(DataDictionary dataDictionary,
			DataImportKeyValueDTO dataImportKeyValueDTO) {
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
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public String createQuery(Long employeeId, List<Long> formIds,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			Long companyId, Map<String, String> paramValueMap) {
		// Map<String, String> paramValueMap = new HashMap<String, String>();
		String queryString = "";
		String select = "SELECT " + employeeId + " AS [Document ID] ";
		StringBuilder fromBuilder = new StringBuilder(
				" FROM Employee AS employee ");
		// String from = " FROM Employee AS employee ";
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

		for (DynamicTableDTO dynamicTableDTO : tableNames) {

			String dynamicFormTableRecord = new StringBuilder(
					"dynamicFormTableRecordT")
					.append(dynamicTableDTO.getFormId())
					.append(dynamicTableDTO.getTableName()).toString();

			fromBuilder.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS ")
					.append(dynamicFormTableRecord).append(" ")
					.append("ON (dynamicFormRecord")
					.append(dynamicTableDTO.getFormId()).append(".Col_")
					.append(dynamicTableDTO.getTableName()).append(" = CAST(")
					.append(dynamicFormTableRecord)
					.append(".Dynamic_Form_Table_Record_ID AS varchar(255))) ");
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

		String where = generalFilterLogic.createWhere(employeeId,
				finalFilterList, companyId, paramValueMap);
		queryString = select + fromBuilder.toString() + where;
		return queryString;
	}

	@Override
	public String createQueryFilters(Long employeeId, List<Long> formIds,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			Long companyId, Date currentDate, Map<String, String> paramValueMap) {
		String queryString = "";
		String select = "SELECT  employee.Employee_ID  AS [Employee Id] ";
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

		for (DynamicTableDTO dynamicTableDTO : tableNames) {

			String dynamicFormTableRecord = new StringBuilder(
					"dynamicFormTableRecordT")
					.append(dynamicTableDTO.getFormId())
					.append(dynamicTableDTO.getTableName()).toString();

			String dynamicFormTableMaxRecord = new StringBuilder(
					"dynamicFormTableRecordT_max")
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
					.append(".Dynamic_Form_Table_Record_ID AS varchar(255)))  ");

			if (StringUtils.isBlank(dynamicTableDTO.getFieldType())
					|| !dynamicTableDTO.getFieldType().equalsIgnoreCase(
							PayAsiaConstants.DEPENDENTS_FIELD_TYPE)) {
				fromBuilder
						.append(" AND ( CONVERT(datetime,")
						.append(dynamicFormTableRecord)
						.append(".Col_1) = (select top 1 max(CONVERT(datetime, ")
						.append(dynamicFormTableMaxRecord)
						.append(".Col_1)) from ")
						.append("Dynamic_Form_Table_Record As ")
						.append(dynamicFormTableMaxRecord)
						.append(" where ")
						.append(dynamicFormTableMaxRecord)
						.append(".Dynamic_Form_Table_Record_ID =")
						.append(dynamicFormTableRecord)
						.append(".Dynamic_Form_Table_Record_ID  and CONVERT(date, ")
						.append(dynamicFormTableMaxRecord)
						.append(".Col_1) <= getDate()) )");
			}

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

		String where = generalFilterLogic.createFiltersWhere(employeeId,
				finalFilterList, companyId, currentDate, paramValueMap);
		queryString = select + fromBuilder.toString() + where;
		return queryString;
	}
}
