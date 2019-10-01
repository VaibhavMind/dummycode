package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.AssignClaimTemplateConditionDTO;
import com.payasia.common.dto.AssignClaimTemplateDTO;
import com.payasia.common.dto.AssignClaimTemplateFormRes;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.form.AssignClaimSchemeResponse;
import com.payasia.common.form.AssignClaimTemplateForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.ClaimTemplateDAO;
import com.payasia.dao.ClaimTemplateItemDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemShortlist;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.logic.AssignClaimTemplateLogic;
import com.payasia.logic.FiltersInfoUtilsLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class AssignClaimTemplateLogicImpl implements AssignClaimTemplateLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(AssignClaimTemplateLogicImpl.class);

	@Resource
	ClaimTemplateItemDAO claimTemplateItemDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	ClaimTemplateDAO claimTemplateDAO;

	@Resource
	EmployeeClaimTemplateDAO employeeClaimTemplateDAO;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	FiltersInfoUtilsLogic filtersInfoUtilsLogic;

	@Resource
	EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;

	@Override
	public List<AssignClaimTemplateForm> getClaimTemplateList(Long companyId) {

		AssignClaimTemplateConditionDTO assignClaimTemplateConditionDTO = new AssignClaimTemplateConditionDTO();
		assignClaimTemplateConditionDTO.setCompanyId(companyId);

		List<AssignClaimTemplateForm> claimTemplateItemList = new ArrayList<AssignClaimTemplateForm>();
		List<ClaimTemplateItem> claimTemplateItems = claimTemplateItemDAO
				.findByCondition(assignClaimTemplateConditionDTO);
		Set<ClaimTemplate> claimTemplateList = new LinkedHashSet<>();
		for (ClaimTemplateItem claimTemplateItem : claimTemplateItems) {
			claimTemplateList.add(claimTemplateItem.getClaimTemplate());
		}

		for (ClaimTemplate claimTemplate : claimTemplateList) {
			AssignClaimTemplateForm assignClaimTemplateForm = new AssignClaimTemplateForm();

			assignClaimTemplateForm
					.setClaimTemplateId(FormatPreserveCryptoUtil.encrypt(claimTemplate.getClaimTemplateId()));
			try {
				assignClaimTemplateForm
						.setClaimTemplateName(URLEncoder.encode(claimTemplate.getTemplateName(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			claimTemplateItemList.add(assignClaimTemplateForm);
		}

		return claimTemplateItemList;
	}

	@Override
	public String assignClaimTemplate(Long companyId, AssignClaimTemplateForm assignClaimTemplateForm) {
		boolean status = true;
		EmployeeClaimTemplate employeeClaimTemplate = new EmployeeClaimTemplate();

		Company companyVO = companyDAO.findById(companyId);
		Long empId=FormatPreserveCryptoUtil.decrypt(assignClaimTemplateForm.getEmployeeId());
		Long claimTemplateId=FormatPreserveCryptoUtil.decrypt(assignClaimTemplateForm.getClaimTemplateId());
		Employee employeeVO = employeeDAO.findById(empId,companyId);
		employeeClaimTemplate.setEmployee(employeeVO);

		ClaimTemplate claimTemplateVO = claimTemplateDAO.findByClaimTemplateID(claimTemplateId
				, companyId);

		employeeClaimTemplate.setClaimTemplate(claimTemplateVO);

		EmployeeClaimTemplate employeeClaimTemplateVO = employeeClaimTemplateDAO.findByEmpIdAndEndDate(empId,claimTemplateId);
		if (employeeClaimTemplateVO != null) {
			if (employeeClaimTemplateVO.getEndDate() == null) {
				return "enddate";
			}
		}

		EmployeeClaimTemplate employeeClaimTempByFromDateVO = employeeClaimTemplateDAO.checkEmpClaimTemplateOverlap(claimTemplateId,empId, assignClaimTemplateForm.getStartDate(),
				assignClaimTemplateForm.getEndDate(), companyVO.getDateFormat(), null);

		if (employeeClaimTempByFromDateVO != null) {
			status = false;
		}

		if (status) {
			if (StringUtils.isNotBlank(assignClaimTemplateForm.getEndDate())) {
				employeeClaimTemplate.setEndDate(
						DateUtils.stringToTimestamp(assignClaimTemplateForm.getEndDate(), companyVO.getDateFormat()));
			}
			employeeClaimTemplate.setStartDate(
					DateUtils.stringToTimestamp(assignClaimTemplateForm.getStartDate(), companyVO.getDateFormat()));
			EmployeeClaimTemplate persistEmployeeClaimTemplate = employeeClaimTemplateDAO
					.saveReturn(employeeClaimTemplate);

			saveEmployeeClaimTemplateType(companyId, persistEmployeeClaimTemplate, employeeVO, claimTemplateVO);

			return "success";
		} else {
			return "overlap";
		}
	}

	private void saveEmployeeClaimTemplateType(Long companyId, EmployeeClaimTemplate persistEmployeeClaimTemplate,
			Employee employeeVO, ClaimTemplate claimTemplateVO) {
		List<ClaimTemplateItem> validClaimItems = new ArrayList<>();
		List<ClaimTemplateItem> notValidClaimItems = new ArrayList<>();
		HashMap<Long, Tab> tabMap = new HashMap<>();
		EntityMaster entityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId, entityMaster.getEntityId());
		for (ClaimTemplateItem claimTemplateItem : claimTemplateVO.getClaimTemplateItems()) {
			Map<String, String> paramValueMap = new HashMap<String, String>();
			Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
			List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
			List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
			List<ClaimTemplateItemShortlist> claimTemplateItemShortlist = new ArrayList<>(
					claimTemplateItem.getClaimTemplateItemShortlists());
			setFilterInfo(claimTemplateItemShortlist, finalFilterList, tableNames, codeDescDTOs, tabMap);

			String query = filtersInfoUtilsLogic.createQuery(employeeVO.getEmployeeId(), formIds, finalFilterList,
					tableNames, codeDescDTOs, companyId, paramValueMap);

			List<BigInteger> resultList = employeeDAO.checkForEmployeeDocuments(query, paramValueMap,
					employeeVO.getEmployeeId(), null);

			if (!resultList.isEmpty()) {
				validClaimItems.add(claimTemplateItem);
			} else {
				notValidClaimItems.add(claimTemplateItem);
			}

		}

		for (ClaimTemplateItem claimTemplateItem : validClaimItems) {

			EmployeeClaimTemplateItem employeeClaimTemplateItem = new EmployeeClaimTemplateItem();
			employeeClaimTemplateItem.setClaimTemplateItem(claimTemplateItem);
			employeeClaimTemplateItem.setEmployeeClaimTemplate(persistEmployeeClaimTemplate);
			employeeClaimTemplateItemDAO.save(employeeClaimTemplateItem);
		}

		for (ClaimTemplateItem claimTemplateItem : notValidClaimItems) {

			EmployeeClaimTemplateItem employeeClaimTemplateItem = new EmployeeClaimTemplateItem();
			employeeClaimTemplateItem.setClaimTemplateItem(claimTemplateItem);
			employeeClaimTemplateItem.setActive(false);
			employeeClaimTemplateItem.setEmployeeClaimTemplate(persistEmployeeClaimTemplate);
			employeeClaimTemplateItemDAO.save(employeeClaimTemplateItem);
		}

	}

	private void setFilterInfo(List<ClaimTemplateItemShortlist> ClaimTemplateItemShortlist,
			List<GeneralFilterDTO> finalFilterList, Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			HashMap<Long, Tab> tabMap) {
		for (ClaimTemplateItemShortlist claimTemplateItemShortlist : ClaimTemplateItemShortlist) {
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			if (claimTemplateItemShortlist.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

				filtersInfoUtilsLogic.getStaticDictionaryInfo(claimTemplateItemShortlist.getDataDictionary(),
						dataImportKeyValueDTO);

			} else {

				filtersInfoUtilsLogic.getDynamicDictionaryInfo(claimTemplateItemShortlist.getDataDictionary(),
						dataImportKeyValueDTO, tableNames, codeDescDTOs, tabMap);

			}
			GeneralFilterDTO generalFilterDTO = new GeneralFilterDTO();

			if (claimTemplateItemShortlist.getCloseBracket() != null) {
				generalFilterDTO.setCloseBracket(claimTemplateItemShortlist.getCloseBracket());
			} else {
				generalFilterDTO.setCloseBracket("");
			}

			if (claimTemplateItemShortlist.getOpenBracket() != null) {
				generalFilterDTO.setOpenBracket(claimTemplateItemShortlist.getOpenBracket());
			} else {
				generalFilterDTO.setOpenBracket("");
			}

			generalFilterDTO.setDataDictionaryName(claimTemplateItemShortlist.getDataDictionary().getDataDictName());
			generalFilterDTO.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			generalFilterDTO.setDictionaryId(claimTemplateItemShortlist.getDataDictionary().getDataDictionaryId());
			generalFilterDTO.setEqualityOperator(claimTemplateItemShortlist.getEqualityOperator());
			generalFilterDTO.setFilterId(claimTemplateItemShortlist.getShort_List_ID());
			generalFilterDTO.setLogicalOperator(claimTemplateItemShortlist.getLogicalOperator());
			generalFilterDTO.setValue(claimTemplateItemShortlist.getValue());

			finalFilterList.add(generalFilterDTO);
		}

	}

	@Override
	public AssignClaimTemplateFormRes searchAssignClaimTemplate(AddClaimDTO claimDTO, PageRequest pageDTO,
			SortCondition sortDTO) {

		Company companyVO = companyDAO.findById(claimDTO.getCompanyId());

		List<AssignClaimTemplateForm> assignClaimTemplateList = new ArrayList<>();
		AssignClaimTemplateConditionDTO conditionDTO = new AssignClaimTemplateConditionDTO();

		if (claimDTO.getSearchText() != null && StringUtils.isNotBlank(claimDTO.getSearchText())) {

			try {
				claimDTO.setSearchText("%" + URLDecoder.decode(claimDTO.getSearchText(), "UTF-8") + "%");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}

			if (claimDTO.getSearchCondition().equals(PayAsiaConstants.PAYDATA_COLLECTION_EMPLOYEE_NUM)) {
				conditionDTO.setEmployeeNumber(claimDTO.getSearchText());
			}
			if (claimDTO.getSearchCondition().equals(PayAsiaConstants.PAYDATA_COLLECTION_EMP_NAME)) {
				conditionDTO.setEmployeeName(claimDTO.getSearchText());
			}
			if (claimDTO.getSearchCondition().equals(PayAsiaConstants.CLAIM_TEMPLATE)) {
				conditionDTO.setClaimTemplateName(claimDTO.getSearchText());
			}
		}

		if (StringUtils.isNotBlank(claimDTO.getFromDate())) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(claimDTO.getFromDate(), companyVO.getDateFormat()));
		}
		if (StringUtils.isNotBlank(claimDTO.getToDate())) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(claimDTO.getToDate(), companyVO.getDateFormat()));
		}

		List<EmployeeClaimTemplate> claimTemplateVOs = employeeClaimTemplateDAO.findByCondition(conditionDTO, pageDTO,
				sortDTO, claimDTO.getCompanyId());

		for (EmployeeClaimTemplate employeeClaimTemplateVO : claimTemplateVOs) {
			AssignClaimTemplateForm assignClaimTemplateForm = new AssignClaimTemplateForm();

			assignClaimTemplateForm.setEmployeeNumber(employeeClaimTemplateVO.getEmployee().getEmployeeNumber());
			String empName = "";
			empName += employeeClaimTemplateVO.getEmployee().getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeClaimTemplateVO.getEmployee().getLastName())) {
				empName += employeeClaimTemplateVO.getEmployee().getLastName();
			}
			assignClaimTemplateForm.setEmployeeName(empName);

			assignClaimTemplateForm.setClaimTemplateName(employeeClaimTemplateVO.getClaimTemplate().getTemplateName());
			assignClaimTemplateForm.setStartDate(
					DateUtils.timeStampToString(employeeClaimTemplateVO.getStartDate(), companyVO.getDateFormat()));
			if (employeeClaimTemplateVO.getEndDate() != null) {
				assignClaimTemplateForm.setEndDate(
						DateUtils.timeStampToString(employeeClaimTemplateVO.getEndDate(), companyVO.getDateFormat()));
			}
			long employeeClaimTemplateId = employeeClaimTemplateVO.getEmployeeClaimTemplateId();
			assignClaimTemplateForm
					.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateId));
			assignClaimTemplateList.add(assignClaimTemplateForm);
		}
		int recordSize = (employeeClaimTemplateDAO.getCountForCondition(conditionDTO, claimDTO.getCompanyId()))
				.intValue();
		AssignClaimTemplateFormRes response = new AssignClaimTemplateFormRes();
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

		}
		response.setAssignClaimTemplateList(assignClaimTemplateList);
		response.setRecords(recordSize);
		return response;
	}

	@Override
	public AssignClaimTemplateForm getEmployeeClaimTemplateData(AddClaimDTO addClaimDTO) {

		EmployeeClaimTemplate employeeClaimTemplateVO = employeeClaimTemplateDAO
				.findByEmployeeClaimTemplateID(addClaimDTO);
		Company companyVO = companyDAO.findById(addClaimDTO.getCompanyId());

		AssignClaimTemplateForm assignClaimTemplateForm = new AssignClaimTemplateForm();

		assignClaimTemplateForm
				.setEmployeeId(FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateVO.getEmployee().getEmployeeId()));
		assignClaimTemplateForm.setEmployeeNumber(employeeClaimTemplateVO.getEmployee().getEmployeeNumber());
		String employeeName = "";
		employeeName += employeeClaimTemplateVO.getEmployee().getFirstName() + " ";
		if (StringUtils.isNotBlank(employeeClaimTemplateVO.getEmployee().getLastName())) {
			employeeName += employeeClaimTemplateVO.getEmployee().getLastName();
		}

		assignClaimTemplateForm.setEmployeeName(employeeName);
		assignClaimTemplateForm.setStartDate(
				DateUtils.timeStampToString(employeeClaimTemplateVO.getStartDate(), companyVO.getDateFormat()));
		if (employeeClaimTemplateVO.getEndDate() != null) {
			assignClaimTemplateForm.setEndDate(
					DateUtils.timeStampToString(employeeClaimTemplateVO.getEndDate(), companyVO.getDateFormat()));
		}

		assignClaimTemplateForm.setClaimTemplateId(
				FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateVO.getClaimTemplate().getClaimTemplateId()));
		assignClaimTemplateForm.setClaimTemplateName(employeeClaimTemplateVO.getClaimTemplate().getTemplateName());
		assignClaimTemplateForm.setEmployeeClaimTemplateId(
				FormatPreserveCryptoUtil.encrypt(employeeClaimTemplateVO.getEmployeeClaimTemplateId()));
		return assignClaimTemplateForm;
	}

	@Override
	public String deleteEmployeeClaimTemplateId(AddClaimDTO addClaimDTO) {
		EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO
				.findByEmployeeClaimTemplateID(addClaimDTO);
		if (employeeClaimTemplate == null) {
			return "false";
		}
		employeeClaimTemplateDAO.delete(employeeClaimTemplate);
		return "true";
	}

	@Override
	public String updateAssignClaimTemplate(Long companyId, AssignClaimTemplateForm assignClaimTemplateForm) {

		boolean status = true;
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(assignClaimTemplateForm.getEmployeeClaimTemplateId()));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setAdmin(true);
		EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO
				.findByEmployeeClaimTemplateID(addClaimDTO);
		Company companyVO = companyDAO.findById(companyId);

		EmployeeClaimTemplate employeeClaimTempByFromDateVO = employeeClaimTemplateDAO.checkEmpClaimTemplateOverlap(
				employeeClaimTemplate.getClaimTemplate().getClaimTemplateId(),
				FormatPreserveCryptoUtil.decrypt(assignClaimTemplateForm.getEmployeeId()),
				assignClaimTemplateForm.getStartDate(), assignClaimTemplateForm.getEndDate(), companyVO.getDateFormat(),
				employeeClaimTemplate.getEmployeeClaimTemplateId());
		if (employeeClaimTempByFromDateVO != null) {
			status = false;
		}

		if (status) {

			employeeClaimTemplate.setStartDate(
					DateUtils.stringToTimestamp(assignClaimTemplateForm.getStartDate(), companyVO.getDateFormat()));
			if (StringUtils.isNotBlank(assignClaimTemplateForm.getEndDate())) {
				employeeClaimTemplate.setEndDate(
						DateUtils.stringToTimestamp(assignClaimTemplateForm.getEndDate(), companyVO.getDateFormat()));
			} else {
				employeeClaimTemplate.setEndDate(null);
			}
			employeeClaimTemplateDAO.update(employeeClaimTemplate);
			return "payasia.assign.claim.template.updated.successfully";
		} else {
			return "payasia.assign.claim.template.period.not.overlap";
		}

	}

	@Override
	public AssignClaimTemplateForm importAssignClaimTemplate(AssignClaimTemplateForm assignClaimTemplateForm,
			Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		String fileName = assignClaimTemplateForm.getFileUpload().getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		List<HashMap<String, AssignClaimTemplateDTO>> empClaimTemplateMapList = null;
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			empClaimTemplateMapList = ExcelUtils.getAssignClaimTemplateFromXLS(assignClaimTemplateForm.getFileUpload(),
					companyVO.getDateFormat());
		} else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			empClaimTemplateMapList = ExcelUtils.getAssignClaimTemplateFromXLSX(assignClaimTemplateForm.getFileUpload(),
					companyVO.getDateFormat());
		}
		AssignClaimTemplateForm assignClaimTemplateFrm = new AssignClaimTemplateForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();
		if (empClaimTemplateMapList != null)
			validateImpotedData(empClaimTemplateMapList, dataImportLogDTOs, companyId);

		if (!dataImportLogDTOs.isEmpty()) {
			assignClaimTemplateFrm.setDataValid(false);
			assignClaimTemplateFrm.setDataImportLogDTOs(dataImportLogDTOs);
			return assignClaimTemplateFrm;
		}

		for (HashMap<String, AssignClaimTemplateDTO> empClaimTemplate : empClaimTemplateMapList) {
			Set<String> keySet = empClaimTemplate.keySet();
			EmployeeClaimTemplate employeeClaimTemplateVO = new EmployeeClaimTemplate();
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				AssignClaimTemplateDTO assignClaimTemplateDTO = empClaimTemplate.get(key);
				if (!key.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)
						&& !key.equalsIgnoreCase(PayAsiaConstants.LEAVE_SCHEME_NAME)) {
					Employee employeeVO = employeeDAO.findByNumber(assignClaimTemplateDTO.getEmployeeNumber(),
							companyId);

					employeeClaimTemplateVO.setEmployee(employeeVO);

					ClaimTemplate claimTemplate = claimTemplateDAO.findByClaimTemplateAndCompany(null,
							assignClaimTemplateDTO.getClaimTemplateName(), companyId);
					employeeClaimTemplateVO.setClaimTemplate(claimTemplate);
					employeeClaimTemplateVO.setStartDate(DateUtils
							.stringToTimestamp(assignClaimTemplateDTO.getFromDateString(), companyVO.getDateFormat()));
					if (StringUtils.isNotBlank(assignClaimTemplateDTO.getToDateString())) {
						employeeClaimTemplateVO.setEndDate(DateUtils.stringToTimestamp(
								assignClaimTemplateDTO.getToDateString(), companyVO.getDateFormat()));
					}

					EmployeeClaimTemplate returnedEmployeeClaimTemplate = employeeClaimTemplateDAO
							.saveReturn(employeeClaimTemplateVO);
					saveEmployeeClaimTemplateType(companyId, returnedEmployeeClaimTemplate, employeeVO, claimTemplate);

				}

			}

		}
		assignClaimTemplateFrm.setDataValid(true);
		return assignClaimTemplateFrm;

	}

	/**
	 * Purpose: To Validate imported employee Claim Templates.
	 * 
	 * @param empClaimTemplateList
	 *            the employee Claim Template List
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateImpotedData(List<HashMap<String, AssignClaimTemplateDTO>> empClaimTemplateList,
			List<DataImportLogDTO> dataImportLogDTOs, Long companyId) {
		boolean status = true;
		for (HashMap<String, AssignClaimTemplateDTO> empClaimTemplate : empClaimTemplateList) {
			Set<String> keySet = empClaimTemplate.keySet();
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				AssignClaimTemplateDTO assignClaimTemplateDTO = empClaimTemplate.get(key);

				Employee employeeVO = employeeDAO.findByNumber(assignClaimTemplateDTO.getEmployeeNumber(), companyId);
				if (employeeVO == null) {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO.setColName(PayAsiaConstants.CAMEL_CASE_EMPLOYEE_NUMBER);
					dataImportLogDTO.setRemarks("payasia.assign.claim.template.not.valid.emp.num.error");
					dataImportLogDTO.setRowNumber(Long.parseLong(key));
					dataImportLogDTOs.add(dataImportLogDTO);
				}
				ClaimTemplate claimTemplate = claimTemplateDAO.findByClaimTemplateAndCompany(null,
						assignClaimTemplateDTO.getClaimTemplateName(), companyId);
				if (claimTemplate == null) {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO.setColName(PayAsiaConstants.PAYASIA_CLAIM_TEMPLATE_NAME);
					dataImportLogDTO.setRemarks("payasia.assign.claim.template.not.valid.claim.template.name.error");
					dataImportLogDTO.setRowNumber(Long.parseLong(key));
					dataImportLogDTOs.add(dataImportLogDTO);
				}
				if (!StringUtils.isNotBlank(assignClaimTemplateDTO.getFromDateString())) {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO.setColName(PayAsiaConstants.ASSIGN_CLAIM_TEMPLATE_START_DATE);
					dataImportLogDTO.setRemarks("payasia.assign.claim.template.from.date.can.not.empty");
					dataImportLogDTO.setRowNumber(Long.parseLong(key));
					dataImportLogDTOs.add(dataImportLogDTO);
				}
				if (employeeVO != null) {
					EmployeeClaimTemplate employeeClaimTemplateVO = employeeClaimTemplateDAO
							.findByEmpIdAndEndDate(employeeVO.getEmployeeId(), claimTemplate.getClaimTemplateId());
					if (employeeClaimTemplateVO != null) {
						if (employeeClaimTemplateVO.getEndDate() == null) {
							DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
							dataImportLogDTO.setColName(PayAsiaConstants.ASSIGN_CLAIM_TEMPLATE_END_DATE);
							dataImportLogDTO.setRemarks(
									"payasia.assign.claim.template.add.end.date.to.previous.claim.template");
							dataImportLogDTO.setRowNumber(Long.parseLong(key));
							dataImportLogDTOs.add(dataImportLogDTO);
						}
					}
					if (employeeClaimTemplateVO == null) {

						EmployeeClaimTemplate employeeClaimTempByFromDateVO = employeeClaimTemplateDAO
								.checkEmpClaimTemplateOverlap(claimTemplate.getClaimTemplateId(),
										employeeVO.getEmployeeId(), assignClaimTemplateDTO.getFromDateString(),
										assignClaimTemplateDTO.getToDateString(),
										employeeVO.getCompany().getDateFormat(), null);

						if (employeeClaimTempByFromDateVO != null) {
							status = false;
						}
						if (!status) {
							DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
							dataImportLogDTO.setColName(PayAsiaConstants.ASSIGN_CLAIM_TEMPLATE_END_DATE);
							dataImportLogDTO
									.setRemarks("payasia.assign.claim.template.claim.template.period.not.overlap");
							dataImportLogDTO.setRowNumber(Long.parseLong(key));
							dataImportLogDTOs.add(dataImportLogDTO);
						}
					}
				}
			}
		}
	}

	@Override
	public AssignClaimSchemeResponse searchEmployee(PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}
		int recordSize = employeeDAO.getCountForCondition(conditionDTO, companyId);

		List<Employee> employeeVOList = employeeDAO.findEmployeesByCondition(conditionDTO, pageDTO, sortDTO, companyId);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : employeeVOList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);
			employeeForm.setEmployeeName(employeeName);
			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

			employeeForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			employeeListFormList.add(employeeForm);
		}

		AssignClaimSchemeResponse response = new AssignClaimSchemeResponse();
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

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}
	
	
}
