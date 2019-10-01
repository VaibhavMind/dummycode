package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.CompanyDocumentConditionDTO;
import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.dto.EmployeeDocumentConditionDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.SelectOptionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CompanyDocumentCenterForm;
import com.payasia.common.form.CompanyDocumentCenterResponseForm;
import com.payasia.common.form.EmpDocumentCenterForm;
import com.payasia.common.form.EmpDocumentCentreResponse;
import com.payasia.common.form.EmployeeDocFormResponse;
import com.payasia.common.form.EmployeeDocumentCenterForm;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDocumentDAO;
import com.payasia.dao.CompanyDocumentDetailDAO;
import com.payasia.dao.CompanyDocumentShortListDAO;
import com.payasia.dao.DocumentCategoryMasterDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeDocumentDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyDocumentDetail;
import com.payasia.dao.bean.CompanyDocumentShortList;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeDocument;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.CompanyDocumentCenterLogic;
import com.payasia.logic.EmpDocumentCenterLogic;
import com.payasia.logic.FiltersInfoUtilsLogic;
import com.payasia.logic.GeneralFilterLogic;

@Component
public class EmpDocumentCenterLogicImpl implements EmpDocumentCenterLogic {
	private static final Logger LOGGER = Logger.getLogger(EmpDocumentCenterLogicImpl.class);
	@Resource
	CompanyDocumentDAO companyDocumentDAO;

	@Resource
	DocumentCategoryMasterDAO documentCategoryMasterDAO;

	@Resource
	EmployeeDocumentDAO employeeDocumentDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	CompanyDocumentDetailDAO companyDocumentDetailDAO;

	@Resource
	CompanyDocumentShortListDAO companyDocumentShortListDAO;

	@Resource
	FiltersInfoUtilsLogic filtersInfoUtilsLogic;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	AWSS3Logic awss3LogicImpl;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Resource
	GeneralFilterLogic generalFilterLogic;

	@Resource
	CompanyDocumentCenterLogic companyDocumentCenterLogic;

	@Resource
	FileUtils fileUtils;

	@Override
	public EmpDocumentCentreResponse searchDocument(String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId) {
		EmployeeDocumentConditionDTO employeeDocumentConditionDTO = new EmployeeDocumentConditionDTO();

		if ("documentName".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				employeeDocumentConditionDTO.setDocumentName("%" + searchText.trim() + "%");
			}

		}

		if ("description".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				employeeDocumentConditionDTO.setDescription("%" + searchText.trim() + "%");
			}

		}
		if ("year".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				employeeDocumentConditionDTO.setYear(Integer.parseInt(searchText.trim()));
			}

		}
		if ("documentType".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				employeeDocumentConditionDTO.setType("%" + searchText.trim() + "%");
			}
		}

		int recordSize = employeeDocumentDAO.getCountForCondition(employeeDocumentConditionDTO, employeeId);

		List<EmployeeDocument> empDocList = employeeDocumentDAO.findByCondition(employeeDocumentConditionDTO, pageDTO,
				sortDTO, employeeId);

		List<EmpDocumentCenterForm> empDocumentCentreFormList = new ArrayList<EmpDocumentCenterForm>();
		for (EmployeeDocument employeeDocument : empDocList) {
			EmpDocumentCenterForm empDocumentCentreForm = new EmpDocumentCenterForm();
			empDocumentCentreForm.setEmployeeDocumentId(employeeDocument.getEmpDocumentId());
			empDocumentCentreForm.setDocType(employeeDocument.getFileType());
			empDocumentCentreForm.setDescription(employeeDocument.getDescription());
			empDocumentCentreForm.setUploadDate(DateUtils.timeStampToString(employeeDocument.getUploadedDate()));
			empDocumentCentreForm.setYear(employeeDocument.getYear());
			empDocumentCentreForm.setDocName(employeeDocument.getFileName());

			empDocumentCentreFormList.add(empDocumentCentreForm);
		}

		EmpDocumentCentreResponse response = new EmpDocumentCentreResponse();

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
		response.setEmpDocumentCentreForm(empDocumentCentreFormList);

		return response;
	}

	private int getCountFromParamValueMap(Map<String, String> paramValueMap) {
		int count = 0;

		for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
			if (entry.getKey().startsWith("sysdateParam")) {
				count++;
			}
			if (entry.getKey().startsWith("param")) {
				count++;
			}
		}

		return count;
	}

	private List<BigInteger> getDoumentIdsForGeneral(Long employeeId,
			CompanyDocumentConditionDTO companyDocumentConditionDTO, Long companyId) {
		Map<String, String> paramValueMap = new LinkedHashMap<String, String>();
		List<BigInteger> documentIds = new ArrayList<BigInteger>();
		List<CompanyDocumentDetail> cmpDocList = companyDocumentDetailDAO.findByCondition(companyDocumentConditionDTO,
				null, null, companyId);
		String queryString = "";
		int count = 1;

		for (CompanyDocumentDetail companyDocumentDetail : cmpDocList) {
			List<CompanyDocumentShortList> companyDocumentShortList = companyDocumentShortListDAO
					.findByCondition(companyDocumentDetail.getCompanyDocument().getDocumentId());
			Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
			List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
			List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();
			setFilterInfo(companyDocumentShortList, finalFilterList, tableNames, codeDescDTOs);

			EntityMaster entityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

			List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId, entityMaster.getEntityId());
			int paramValueCount;
			if (!paramValueMap.isEmpty()) {
				paramValueCount = getCountFromParamValueMap(paramValueMap) + 1;
			} else {
				paramValueCount = 1;
			}

			if (cmpDocList.size() > 1) {
				queryString += createQuery(employeeId, formIds,
						companyDocumentDetail.getCompanyDocument().getDocumentId(), finalFilterList, tableNames,
						codeDescDTOs, companyId, paramValueMap, paramValueCount);

				if (count != cmpDocList.size()) {
					queryString += " UNION ";
				}

			} else {
				queryString = createQuery(employeeId, formIds,
						companyDocumentDetail.getCompanyDocument().getDocumentId(), finalFilterList, tableNames,
						codeDescDTOs, companyId, paramValueMap, paramValueCount);
			}
			count++;
		}

		if (!cmpDocList.isEmpty()) {
			documentIds.addAll(employeeDAO.checkForEmployeeDocuments(queryString, paramValueMap, employeeId, null));
		}

		return documentIds;
	}

	private String createQuery(Long employeeId, List<Long> formIds, Long documentId,
			List<GeneralFilterDTO> finalFilterList, Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			Long companyId, Map<String, String> paramValueMap, Integer paramValueCount) {
		String queryString = "";
		String select = "SELECT " + documentId + " AS [Document ID] ";
		StringBuilder fromBuilder = new StringBuilder(" FROM Employee AS employee ");

		for (Long formId : formIds) {
			fromBuilder.append("LEFT OUTER JOIN Dynamic_Form_Record AS dynamicFormRecord");
			fromBuilder.append(formId);
			fromBuilder.append(" ");
			fromBuilder.append("ON (employee.Employee_ID = dynamicFormRecord");
			fromBuilder.append(formId);
			fromBuilder.append(".Entity_Key) AND (dynamicFormRecord");
			fromBuilder.append(formId);
			fromBuilder.append(".Form_ID = ");
			fromBuilder.append(formId);
			fromBuilder.append(" ) ");

		}

		for (DynamicTableDTO dynamicTableDTO : tableNames) {
			StringBuilder dynamicFormTableRecordBuilder = new StringBuilder("dynamicFormTableRecordT");
			dynamicFormTableRecordBuilder.append(dynamicTableDTO.getFormId());
			dynamicFormTableRecordBuilder.append(dynamicTableDTO.getTableName());
			String dynamicFormTableRecord = dynamicFormTableRecordBuilder.toString();

			fromBuilder.append("LEFT OUTER JOIN Dynamic_Form_Table_Record AS ");
			fromBuilder.append(dynamicFormTableRecord);
			fromBuilder.append(" ");
			fromBuilder.append("ON (dynamicFormRecord");
			fromBuilder.append(dynamicTableDTO.getFormId());
			fromBuilder.append(".Col_");
			fromBuilder.append(dynamicTableDTO.getTableName());
			fromBuilder.append(" = CAST(");
			fromBuilder.append(dynamicFormTableRecord);
			fromBuilder.append(".Dynamic_Form_Table_Record_ID AS varchar(255))) ");
		}

		List<String> codeAliasList = new ArrayList<>();

		for (CodeDescDTO codeDescDTO : codeDescDTOs) {

			if (codeDescDTO.isChildVal()) {
				for (DynamicTableDTO dynamicTableDTO : tableNames) {
					StringBuilder dynamicFormTableBuilder = new StringBuilder("dynamicFormTableRecordT");
					dynamicFormTableBuilder.append(dynamicTableDTO.getFormId());
					dynamicFormTableBuilder.append(dynamicTableDTO.getTableName());
					String dynamicFormTableRecord = dynamicFormTableBuilder.toString();

					StringBuilder dynamicFormFieldBuilder = new StringBuilder("dynamicFormFieldRefValueT");
					dynamicFormFieldBuilder.append(codeDescDTO.getFormId());
					dynamicFormFieldBuilder.append(codeDescDTO.getMethodName());
					String dynamicFormFieldRefValueT = dynamicFormFieldBuilder.toString();

					if (!codeAliasList.contains(dynamicFormFieldRefValueT)) {

						fromBuilder.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS ");
						fromBuilder.append(dynamicFormFieldRefValueT);
						fromBuilder.append(" ");
						fromBuilder.append("ON (");
						fromBuilder.append(dynamicFormTableRecord);
						fromBuilder.append(".");
						fromBuilder.append(codeDescDTO.getMethodName());
						fromBuilder.append(" = CAST(");
						fromBuilder.append(dynamicFormFieldRefValueT);
						fromBuilder.append(".Field_Ref_Value_ID AS varchar(255))) ");
						codeAliasList.add(dynamicFormFieldRefValueT);
					}

				}

			} else {
				StringBuilder dynamicFormBuilder = new StringBuilder("dynamicFormFieldRefValue");
				dynamicFormBuilder.append(codeDescDTO.getFormId());
				dynamicFormBuilder.append(codeDescDTO.getMethodName());
				String dynamicFormFieldRefValueT = dynamicFormBuilder.toString();

				if (!codeAliasList.contains(dynamicFormFieldRefValueT)) {
					fromBuilder.append("LEFT OUTER JOIN Dynamic_Form_Field_Ref_Value AS ");
					fromBuilder.append(dynamicFormFieldRefValueT);
					fromBuilder.append(" ");
					fromBuilder.append("ON (dynamicFormRecord");
					fromBuilder.append(codeDescDTO.getFormId());
					fromBuilder.append(".");
					fromBuilder.append(codeDescDTO.getMethodName());
					fromBuilder.append(" = CAST(");
					fromBuilder.append(dynamicFormFieldRefValueT);
					fromBuilder.append(".Field_Ref_Value_ID AS varchar(255))) ");
					codeAliasList.add(dynamicFormFieldRefValueT);
				}

			}

		}

		String where = generalFilterLogic.createWhereWithCount(employeeId, finalFilterList, companyId, paramValueMap,
				paramValueCount);
		queryString = select + fromBuilder.toString() + where;
		return queryString;
	}

	private void setFilterInfo(List<CompanyDocumentShortList> companyDocumentShortList,
			List<GeneralFilterDTO> finalFilterList, Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs) {
		for (CompanyDocumentShortList companyDocumentFliter : companyDocumentShortList) {
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			if (companyDocumentFliter.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

				filtersInfoUtilsLogic.getStaticDictionaryInfo(companyDocumentFliter.getDataDictionary(),
						dataImportKeyValueDTO);

			} else {

				filtersInfoUtilsLogic.getDynamicDictionaryInfo(companyDocumentFliter.getDataDictionary(),
						dataImportKeyValueDTO, tableNames, codeDescDTOs);

			}
			GeneralFilterDTO generalFilterDTO = new GeneralFilterDTO();

			if (companyDocumentFliter.getCloseBracket() != null) {
				generalFilterDTO.setCloseBracket(companyDocumentFliter.getCloseBracket());
			} else {
				generalFilterDTO.setCloseBracket("");
			}

			if (companyDocumentFliter.getOpenBracket() != null) {
				generalFilterDTO.setOpenBracket(companyDocumentFliter.getOpenBracket());
			} else {
				generalFilterDTO.setOpenBracket("");
			}

			generalFilterDTO.setDataDictionaryName(companyDocumentFliter.getDataDictionary().getDataDictName());
			generalFilterDTO.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			generalFilterDTO.setDictionaryId(companyDocumentFliter.getDataDictionary().getDataDictionaryId());
			generalFilterDTO.setEqualityOperator(companyDocumentFliter.getEqualityOperator());
			generalFilterDTO.setFilterId(companyDocumentFliter.getShortListId());
			generalFilterDTO.setLogicalOperator(companyDocumentFliter.getLogicalOperator());
			generalFilterDTO.setValue(companyDocumentFliter.getValue());

			finalFilterList.add(generalFilterDTO);
		}

	}

	@Override
	public CompanyDocumentCenterResponseForm searchDocumentEmployeeDocumentCenter(String searchCondition, String searchText,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId, Long categoryId, Long employeeId) {

		Employee emp = employeeDAO.findById(employeeId);
		String employeeNumber = emp.getEmployeeNumber() + "[" + fileNameSeperator + "]";

		/* int recordSize; */
		String categoryName = null;
		CompanyDocumentConditionDTO companyDocumentConditionDTO = new CompanyDocumentConditionDTO();

		if (!StringUtils.isEmpty(searchCondition) && !StringUtils.isEmpty(searchText)) {
			if ("documentName".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					try {
						companyDocumentConditionDTO.setDocumentName("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}

				}

			}
			if ("description".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					try {
						companyDocumentConditionDTO.setDescription("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}

			}

			if ("year".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					companyDocumentConditionDTO.setYear(Integer.parseInt(searchText));
				}

			}
			if ("category".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					try {
						companyDocumentConditionDTO.setCategory("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}
					companyDocumentConditionDTO.setCategoryId(Long.parseLong(searchText));
				}

			}

			if ("documentType".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					try {
						companyDocumentConditionDTO.setType("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}

				}

			}
			if ("uploadedDate".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					companyDocumentConditionDTO.setUploadedDate(DateUtils.stringToTimestamp(searchText));
				}

			}
		}
		// Changes by Gaurav
		if (categoryId != 0) {
			companyDocumentConditionDTO.setCategory(categoryId.toString());
			companyDocumentConditionDTO.setCategoryId(categoryId);

			List<DocumentCategoryMaster> categoryList = documentCategoryMasterDAO.findAll();

			for (DocumentCategoryMaster documentCategoryMaster : categoryList) {

				if (documentCategoryMaster.getDocumentCategoryId() == categoryId) {
					categoryName = documentCategoryMaster.getCategoryName();
				}

			}

		}
		companyDocumentConditionDTO.setEmployeeNumber(employeeNumber + "%");
		companyDocumentConditionDTO.setCategoryName(categoryName);

		/*
		 * recordSize = companyDocumentDetailDAO.getCountForCondition(
		 * companyDocumentConditionDTO, companyId);
		 */

		List<CompanyDocumentDetail> cmpDocList = companyDocumentDetailDAO.findByCondition(companyDocumentConditionDTO,
				null, null, companyId);

		List<BigInteger> validGeneralDocs = getDoumentIdsForGeneral(employeeId, companyDocumentConditionDTO, companyId);

		List<CompanyDocumentCenterForm> companyDocumentCenterFormList = new ArrayList<CompanyDocumentCenterForm>();

		for (CompanyDocumentDetail companyDocumentDetail : cmpDocList) {

			if (companyDocumentConditionDTO.getCategoryName().equals(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {

				Integer docId = Integer
						.valueOf(String.valueOf(companyDocumentDetail.getCompanyDocument().getDocumentId()));

				if (!validGeneralDocs.contains(docId)) {
					continue;
				}
			}
			CompanyDocumentCenterForm companyDocumentCenterForm = new CompanyDocumentCenterForm();

			/* ID ENCRYPT */
			companyDocumentCenterForm
					.setDocId(FormatPreserveCryptoUtil.encrypt(companyDocumentDetail.getCompanyDocumentDetailID()));

			companyDocumentCenterForm.setDocName(companyDocumentDetail.getFileName());
			companyDocumentCenterForm.setDescription(companyDocumentDetail.getCompanyDocument().getDescription());

			companyDocumentCenterForm.setYear(String.valueOf(companyDocumentDetail.getCompanyDocument().getYear()));

			companyDocumentCenterForm.setCategory(
					companyDocumentDetail.getCompanyDocument().getDocumentCategoryMaster().getCategoryName());
			companyDocumentCenterForm.setDocType(companyDocumentDetail.getFileType());
			companyDocumentCenterFormList.add(companyDocumentCenterForm);

		}

		CompanyDocumentCenterResponseForm response = new CompanyDocumentCenterResponseForm();

		/*
		 * if (pageDTO != null) { int recordSize = totalCmpDocList.size(); int pageSize
		 * = pageDTO.getPageSize(); int totalPages = recordSize / pageSize;
		 * 
		 * if (recordSize % pageSize != 0) { totalPages = totalPages + 1; } if
		 * (recordSize == 0) { pageDTO.setPageNumber(0); }
		 * 
		 * response.setPage(pageDTO.getPageNumber()); response.setTotal(totalPages);
		 * 
		 * response.setRecords(recordSize); }
		 */
		 
		response.setRows(companyDocumentCenterFormList);

		return response;
	}

	@Override
	public void uploadDocument(EmpDocumentCenterForm employeeDocumentCenterForm, Long companyId, Long employeeId) {

		/*
		 * String filePath = downloadPath + "/company" + companyId +
		 * "/Employee Document" + "/employee" + employeeId + "/" +
		 * employeeDocumentCenterForm.getYear();
		 */

		try {
			String fileName = employeeDocumentCenterForm.getFileData().getOriginalFilename();
			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);

			EmployeeDocument employeeDocumentVO = employeeDocumentDAO.findDocumentByYearEmIDocNameDocId(employeeId,
					employeeDocumentCenterForm.getYear(), fileName, null);

			if (employeeDocumentVO == null) {

				EmployeeDocument employeeDocument = new EmployeeDocument();
				employeeDocument.setFileType(fileType);
				Employee employee = new Employee();
				employee.setEmployeeId(employeeId);
				employeeDocument.setEmployee(employee);
				employeeDocument.setDescription(employeeDocumentCenterForm.getDescription());
				employeeDocument.setUploadedDate(DateUtils.getCurrentTimestamp());
				employeeDocument.setYear(employeeDocumentCenterForm.getYear());
				employeeDocument.setFileName(fileName);

				employeeDocumentDAO.save(employeeDocument);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

		}

	}

	@Override
	public String viewDocument(long docId) {
		EmployeeDocument employeeDocument = employeeDocumentDAO.findById(docId);
		String filePath;

		/*
		 * filePath = "company" +
		 * employeeDocument.getEmployee().getCompany().getCompanyId() +
		 * PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_DOCUMENT +
		 * employeeDocument.getEmployee().getEmployeeId() + "/" +
		 * employeeDocument.getYear() + "/" + employeeDocument.getFileName();
		 */

		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				employeeDocument.getEmployee().getCompany().getCompanyId(),
				PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_DOCUMENT, employeeDocument.getFileName(),
				String.valueOf(employeeDocument.getYear()),
				String.valueOf(employeeDocument.getEmployee().getEmployeeId()), null,
				PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);

		filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

		return filePath;

	}

	@Override
	public void updateDocument(EmpDocumentCenterForm empDocumentCenterForm, Long employeeId) {

		boolean status = true;
		EmployeeDocument employeeDocument = employeeDocumentDAO.findById(empDocumentCenterForm.getEmployeeDocumentId());
		String deleteFile;
		String fileName = empDocumentCenterForm.getFileData().getOriginalFilename();
		String fileType;
		EmployeeDocument employeeDocumentVO = employeeDocumentDAO.findDocumentByYearEmIDocNameDocId(employeeId,
				employeeDocument.getYear(), fileName, employeeDocument.getEmpDocumentId());
		if (employeeDocumentVO == null) {

			try {

				if (empDocumentCenterForm.getFileData().getSize() > 0) {
					/*
					 * deleteFile = "company" +
					 * employeeDocument.getEmployee().getCompany()
					 * .getCompanyId() +
					 * PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_DOCUMENT +
					 * employeeDocument.getEmployee().getEmployeeId() + "/" +
					 * employeeDocument.getYear() + "/" +
					 * employeeDocument.getFileName();
					 */
					FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath,
							rootDirectoryName, employeeDocument.getEmployee().getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_DOCUMENT, employeeDocument.getFileName(),
							String.valueOf(employeeDocument.getYear()),
							String.valueOf(employeeDocument.getEmployee().getEmployeeId()), null,
							PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);

					deleteFile = fileUtils.getGeneratedFilePath(filePathGenerator);

					if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
						List<String> fileList = new ArrayList<String>();
						fileList.add(deleteFile);
						awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
					} else {
						FileUtils.deletefile(deleteFile);
					}
					/*
					 * String newFilePath = "company" +
					 * employeeDocument.getEmployee().getCompany()
					 * .getCompanyId() +
					 * PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_DOCUMENT +
					 * employeeId + "/" + employeeDocument.getYear();
					 */

					employeeDocument.setFileName(fileName);
					fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
					employeeDocument.setFileType(fileType);

				}

			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				status = false;
			}

			if (status == true) {

				employeeDocument.setDescription(empDocumentCenterForm.getDescription());
				employeeDocumentDAO.update(employeeDocument);

			}

		}

	}

	@Override
	public void deleteCompanyDocument(Long docId) {

		boolean success = true;

		EmployeeDocument employeeDocument = employeeDocumentDAO.findById(docId);
		String filePath;

		try {
			/*
			 * filePath = "company" +
			 * employeeDocument.getEmployee().getCompany() .getCompanyId() +
			 * PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_DOCUMENT +
			 * employeeDocument.getEmployee().getEmployeeId() + "/" +
			 * +employeeDocument.getYear() + "/" +
			 * employeeDocument.getFileName();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					employeeDocument.getEmployee().getCompany().getCompanyId(),
					PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_DOCUMENT, employeeDocument.getFileName(),
					String.valueOf(employeeDocument.getYear()),
					String.valueOf(employeeDocument.getEmployee().getEmployeeId()), null,
					PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);

			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				List<String> fileList = new ArrayList<String>();
				fileList.add(filePath);
				awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
			} else {
				FileUtils.deletefile(filePath);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			success = false;
		}

		if (success == true) {
			employeeDocumentDAO.delete(employeeDocument);
		}

	}

	@Override
	public String viewDocument(long docId, Long companyId, Long loggedInEmployeeId) {
		CompanyDocumentDetail companyDocumentDetail = companyDocumentDetailDAO.findById(docId);
		Employee employeeVO = employeeDAO.findById(loggedInEmployeeId);
		CompanyDocument companyDocument = companyDocumentDetail.getCompanyDocument();

		// authorization check for non-general documents
				// Block this code after discussion with Peeyush Sir
				/*if(!PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL.equalsIgnoreCase(
						companyDocument.getDocumentCategoryMaster().getCategoryName())) {
					
					if(!PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT.equalsIgnoreCase(
							companyDocument.getDocumentCategoryMaster().getCategoryName())) {
						String documentEmpNumber = StringUtils.substringBefore(companyDocument.getDescription(), "_");
						if(!documentEmpNumber.equalsIgnoreCase(employeeVO.getEmployeeNumber()))
							throw new PayAsiaSystemException("Unauthorized_Access", "Unauthorized Access");
					} else {
						String documentEmpNumber = StringUtils.substringBefore(companyDocumentDetail.getFileName(), "_");
						if(!documentEmpNumber.equalsIgnoreCase(employeeVO.getEmployeeNumber()))
							throw new PayAsiaSystemException("Unauthorized_Access", "Unauthorized Access");
					}
				} else {
					if(companyDocument.getCompany().getCompanyId() != companyId.longValue()){
						 throw new PayAsiaSystemException("Unauthorized_Access", "Unauthorized Access");
					}
				}*/

		if(companyDocument.getCompany().getCompanyId() != companyId.longValue()){
			 throw new PayAsiaSystemException("Unauthorized_Access", "Unauthorized Access");
		}
		String filePath = null;
		List<CompanyDocumentLogDTO> documentLogs = new ArrayList<CompanyDocumentLogDTO>();
		List<String> invalidZipFileNames = new ArrayList<String>();
		long categoryId = companyDocumentDetail.getCompanyDocument().getDocumentCategoryMaster()
				.getDocumentCategoryId();

		CompanyDocumentCenterResponseForm response = searchDocumentEmployeeDocumentCenter("", "", null, null,                                   
				companyId, categoryId, loggedInEmployeeId);
		List<CompanyDocumentCenterForm> companyDocumentCenterFormList = response.getRows();
		List<Long> validDocumentIdList = new ArrayList<Long>();
		for (CompanyDocumentCenterForm documentCenterForm : companyDocumentCenterFormList) {
			validDocumentIdList.add(FormatPreserveCryptoUtil.decrypt(documentCenterForm.getDocId()));
		}
		if (validDocumentIdList.isEmpty()) {
			return filePath;
		}
		if (!validDocumentIdList.isEmpty() && !validDocumentIdList.contains(docId)) {
			return filePath;
		}

		List<DocumentCategoryMaster> categoryList = documentCategoryMasterDAO.findAll();
		String categoryName = null;

		for (DocumentCategoryMaster documentCategoryMaster : categoryList) {

			if (documentCategoryMaster.getDocumentCategoryId() == categoryId) {
				categoryName = documentCategoryMaster.getCategoryName();
			}

		}

		if (categoryName.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {
			/*
			 * filePath = "/company/" +
			 * companyDocumentDetail.getCompanyDocument().getCompany()
			 * .getCompanyId() + "/" + PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP
			 * + "/" + companyDocumentDetail.getCompanyDocument().getYear() +
			 * "/" + companyDocumentCenterLogic.getValidEmployeeNumber(
			 * companyDocumentDetail.getFileName(), documentLogs, companyId,
			 * invalidZipFileNames) + "/" + companyDocumentDetail.getFileName();
			 */
			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyDocumentDetail.getCompanyDocument().getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, companyDocumentDetail.getFileName(),
							String.valueOf(companyDocumentDetail.getCompanyDocument().getYear()),
							companyDocumentCenterLogic.getValidEmployeeNumber(companyDocumentDetail.getFileName(),
									documentLogs, companyId, invalidZipFileNames),
							null, PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);

			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		} else if (categoryName.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)) {

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyDocumentDetail.getCompanyDocument().getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT, companyDocumentDetail.getFileName(),
							String.valueOf(companyDocumentDetail.getCompanyDocument().getYear()),
							companyDocumentCenterLogic.getValidEmployeeNumber(companyDocumentDetail.getFileName(),
									documentLogs, companyId, invalidZipFileNames),
							null, PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);

			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * filePath = "/company/" +
			 * companyDocumentDetail.getCompanyDocument().getCompany()
			 * .getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT + "/" +
			 * companyDocumentDetail.getCompanyDocument().getYear() + "/" +
			 * companyDocumentCenterLogic.getValidEmployeeNumber(
			 * companyDocumentDetail.getFileName(), documentLogs, companyId,
			 * invalidZipFileNames) + "/" + companyDocumentDetail.getFileName();
			 */
		} else if (categoryName.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER)) {
			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyDocumentDetail.getCompanyDocument().getCompany().getCompanyId(),
					PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_LETTER, companyDocumentDetail.getFileName(), null,
					employeeVO.getEmployeeNumber(), null, PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);

			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * filePath = "/company/" +
			 * companyDocumentDetail.getCompanyDocument().getCompany()
			 * .getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_LETTER + "/" +
			 * employeeVO.getEmployeeNumber() + "/" +
			 * companyDocumentDetail.getFileName();
			 */
		} else {
			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyDocumentDetail.getCompanyDocument().getCompany().getCompanyId(),
					PayAsiaConstants.COMPANY_DOCUMENT_GENERAL, companyDocumentDetail.getFileName(), null, null, null,
					PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);

			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * filePath = "/company/" +
			 * companyDocumentDetail.getCompanyDocument().getCompany()
			 * .getCompanyId() + "/" + PayAsiaConstants.COMPANY_DOCUMENT_GENERAL
			 * + "/" + companyDocumentDetail.getFileName();
			 */

		}
		return filePath;

	}

	@Override
	public EmployeeDocFormResponse searchEmployeeDocument(String searchCondition, String searchText,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId, Long categoryId, Long employeeId) {

		Employee emp = employeeDAO.findById(employeeId);
		String employeeNumber = emp.getEmployeeNumber() + "[" + fileNameSeperator + "]";

		/* int recordSize; */
		String categoryName = null;
		CompanyDocumentConditionDTO companyDocumentConditionDTO = new CompanyDocumentConditionDTO();

		if (!StringUtils.isEmpty(searchCondition) && !StringUtils.isEmpty(searchText)) {
			if ("docName".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					try {
						companyDocumentConditionDTO.setDocumentName("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}

				}

			}
			if ("description".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					try {
						companyDocumentConditionDTO.setDescription("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}

			}

			if ("year".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					companyDocumentConditionDTO.setYear(Integer.parseInt(searchText));
				}

			}
			if ("category".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					try {
						companyDocumentConditionDTO.setCategory("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}
					companyDocumentConditionDTO.setCategoryId(Long.parseLong(searchText));
				}

			}

			if ("docType".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					try {
						companyDocumentConditionDTO.setType("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
					} catch (UnsupportedEncodingException e) {
						LOGGER.error(e.getMessage(), e);
					}

				}

			}
			if ("uploadedDate".equals(searchCondition)) {
				if (StringUtils.isNotBlank(searchText)) {
					companyDocumentConditionDTO.setUploadedDate(DateUtils.stringToTimestamp(searchText));
				}

			}
		}
		
		
	  DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO.findById(categoryId);
	
	  companyDocumentConditionDTO.setCategoryName(categoryMaster.getCategoryName());
	  companyDocumentConditionDTO.setCategoryId(categoryMaster.getDocumentCategoryId());
	  companyDocumentConditionDTO.setEmployeeNumber(employeeNumber + "%");
	  
	  int recordSize = companyDocumentDetailDAO.getCountForCondition(companyDocumentConditionDTO, companyId);

		List<CompanyDocumentDetail> cmpDocList = companyDocumentDetailDAO.findByCondition(companyDocumentConditionDTO,
				pageDTO, sortDTO, companyId);

		List<BigInteger> validGeneralDocs = getDoumentIdsForGeneral(employeeId, companyDocumentConditionDTO, companyId);

		List<EmployeeDocumentCenterForm> companyDocumentCenterFormList = new ArrayList<EmployeeDocumentCenterForm>();

		for (CompanyDocumentDetail companyDocumentDetail : cmpDocList) {

			if (companyDocumentConditionDTO.getCategoryName().equals(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {

				Integer docId = Integer
						.valueOf(String.valueOf(companyDocumentDetail.getCompanyDocument().getDocumentId()));

				if (!validGeneralDocs.contains(docId)) {
					continue;
				}
			}
			EmployeeDocumentCenterForm companyDocumentCenterForm = new EmployeeDocumentCenterForm();

			/* ID ENCRYPT */
			companyDocumentCenterForm
					.setDocId(FormatPreserveCryptoUtil.encrypt(companyDocumentDetail.getCompanyDocumentDetailID()));
			companyDocumentCenterForm.setDescription(companyDocumentDetail.getCompanyDocument().getDescription());
			companyDocumentCenterForm.setYear(String.valueOf(companyDocumentDetail.getCompanyDocument().getYear()));
			companyDocumentCenterForm.setCategory(companyDocumentDetail.getCompanyDocument().getDocumentCategoryMaster().getCategoryName());
			companyDocumentCenterForm.setAction(true);
			companyDocumentCenterForm.setDocName(FileUtils.extractFileName(companyDocumentDetail.getFileName()));
			companyDocumentCenterForm.setDocType(companyDocumentDetail.getFileType());
			// companyDocumentCenterForm.setUploadDate(companyDocumentDetail.getCompanyDocument().getUploadedDate());

			companyDocumentCenterFormList.add(companyDocumentCenterForm);

		}

		EmployeeDocFormResponse response = new EmployeeDocFormResponse();

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

		response.setRows(companyDocumentCenterFormList);

		return response;
	}

	@Override
	public List<SelectOptionDTO> fetchList() {

		List<SelectOptionDTO> optionList = new ArrayList<>();

		for (DocumentCategoryMaster object : documentCategoryMasterDAO.findAll()) {
			SelectOptionDTO optionDTO = new SelectOptionDTO();
			optionDTO.setLongKey(object.getDocumentCategoryId());
			optionDTO.setValue(object.getCategoryName());
			optionList.add(optionDTO);
		}

		return optionList;
	}

}
