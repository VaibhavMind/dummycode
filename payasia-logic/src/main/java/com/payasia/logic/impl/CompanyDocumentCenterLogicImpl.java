package com.payasia.logic.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.EmployeeFilter;
import com.mind.payasia.xml.bean.EmployeeFilterTemplate;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.CompanyDocumentConditionDTO;
import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CompanyDocumentCenterForm;
import com.payasia.common.form.CompanyDocumentCenterResponseForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.EmployeeFilterXMLUtil;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.ShortlistOperatorEnum;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDocumentDAO;
import com.payasia.dao.CompanyDocumentDetailDAO;
import com.payasia.dao.CompanyDocumentShortListDAO;
import com.payasia.dao.CompanyEmployeeShortListDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DocumentCategoryMasterDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyDocumentDetail;
import com.payasia.dao.bean.CompanyDocumentDetail_;
import com.payasia.dao.bean.CompanyDocumentShortList;
import com.payasia.dao.bean.CompanyDocument_;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.DocumentCategoryMaster_;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.CompanyDocumentCenterLogic;
import com.payasia.logic.GeneralFilterLogic;
import com.payasia.logic.GeneralLogic;

/**
 * The Class CompanyDocumentCenterLogicImpl.
 */
/**
 * @author ragulapraveen
 * 
 */
@Component
public class CompanyDocumentCenterLogicImpl extends BaseLogic implements
		CompanyDocumentCenterLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CompanyDocumentCenterLogicImpl.class);

	/** The company document dao. */
	@Resource
	CompanyDocumentDAO companyDocumentDAO;

	/** The document category master dao. */
	@Resource
	DocumentCategoryMasterDAO documentCategoryMasterDAO;

	/** The dynamic form record dao. */
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	/** The company document detail dao. */
	@Resource
	CompanyDocumentDetailDAO companyDocumentDetailDAO;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The file name seperator. */
	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Value("#{payasiaptProperties['payasia.document.path.separator']}")
	private String docPathSeperator;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The entity master dao. */
	@Resource
	EntityMasterDAO entityMasterDAO;

	/** The general dao. */
	@Resource
	GeneralDAO generalDAO;

	/** The data dictionary dao. */
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	/** The company employee short list dao. */
	@Resource
	CompanyEmployeeShortListDAO companyEmployeeShortListDAO;

	/** The dynamic form dao. */
	@Resource
	DynamicFormDAO dynamicFormDAO;

	/** The company document short list dao. */
	@Resource
	CompanyDocumentShortListDAO companyDocumentShortListDAO;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	GeneralFilterLogic generalFilterLogic;
	@Resource
	MessageSource messageSource;

	@Resource
	FileUtils fileUtils;

	@Resource
	AWSS3Logic awss3LogicImpl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDocumentCenterLogic#searchDocument(java.lang
	 * .String, java.lang.String, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long, java.lang.Long)
	 */
	@Override
	public CompanyDocumentCenterResponseForm searchDocument(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long categoryId, Long employeeId) {

		/* int recordSize; */
		CompanyDocumentConditionDTO companyDocumentConditionDTO = new CompanyDocumentConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.DOCUMENT_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				companyDocumentConditionDTO.setDocumentName("%"+ searchText + "%");

			}

		}
		if (searchCondition.equals(PayAsiaConstants.DESCRIPTION)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					companyDocumentConditionDTO.setDescription("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
			}

		}

		if (searchCondition.equals(PayAsiaConstants.YEAR)) {
			if (StringUtils.isNotBlank(searchText)) {
				companyDocumentConditionDTO.setYear(Integer
						.parseInt(searchText));
			}

		}
		if (searchCondition.equals(PayAsiaConstants.CATEGORY)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					companyDocumentConditionDTO.setCategory("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
				companyDocumentConditionDTO.setCategoryId(Long
						.parseLong(searchText));
			}

		}

		if (categoryId != 0) {
			companyDocumentConditionDTO.setCategory(categoryId.toString());
			companyDocumentConditionDTO.setCategoryId(categoryId);

		}

		if (searchCondition.equals(PayAsiaConstants.DOCUMENT_TYPE)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					companyDocumentConditionDTO.setType("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}

		}
		if (searchCondition.equals(PayAsiaConstants.UPLOADED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				companyDocumentConditionDTO.setUploadedDate(DateUtils
						.stringToTimestamp(searchText));
			}

		}

		// recordSize =
		// companyDocumentDetailDAO.getCountForTuplesbyConditionCmp(
		// companyDocumentConditionDTO, companyId);

		List<Tuple> totalCmpDocList = companyDocumentDetailDAO
				.findTuplesByConditionCmp(companyDocumentConditionDTO, null,
						null, companyId);
		List<Tuple> cmpDocList = companyDocumentDetailDAO
				.findTuplesByConditionCmp(companyDocumentConditionDTO, pageDTO,
						sortDTO, companyId);

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeNumbers(employeeId, companyId);

		List<String> shortlistedEmployeeNumbers = employeeShortListDTO
				.getShortListEmployeeNumbers();

		List<CompanyDocumentCenterForm> companyDocumentCenterFormList = new ArrayList<CompanyDocumentCenterForm>();
		for (Tuple companyDocumentDetail : cmpDocList) {
			CompanyDocumentCenterForm companyDocumentCenterForm = new CompanyDocumentCenterForm();
			
			/*ID ENCRYPT */
			companyDocumentCenterForm.setDocId(FormatPreserveCryptoUtil.encrypt((Long)companyDocumentDetail.get(getAlias(CompanyDocumentDetail_.companyDocumentDetailID),Long.class)));
			
			String fileName;
			String cmpDocCategoryName = (String) companyDocumentDetail.get(
					getAlias(DocumentCategoryMaster_.categoryName),
					String.class);
			if (cmpDocCategoryName
					.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {
				fileName = (String) companyDocumentDetail
						.get(getAlias(CompanyDocumentDetail_.fileName),
								String.class);
				String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
				fileName = fileName.substring(0, fileName.lastIndexOf('_'))
						+ "." + ext;

			} else if (cmpDocCategoryName
					.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER)) {
				fileName = (String) companyDocumentDetail
						.get(getAlias(CompanyDocumentDetail_.fileName),
								String.class);
				String[] fileNameArr = fileName.split("_");
				String empNumber = fileNameArr[0];
				if (employeeShortListDTO.getEmployeeShortList()
						&& !shortlistedEmployeeNumbers.contains(empNumber)) {

					continue;
				}

			} else {

				fileName = (String) companyDocumentDetail
						.get(getAlias(CompanyDocumentDetail_.fileName),
								String.class);
				String empNumber = fileName.substring(0,
						fileName.lastIndexOf('_'));
				if (employeeShortListDTO.getEmployeeShortList()
						&& !shortlistedEmployeeNumbers.contains(empNumber)) {

					continue;
				}

			}

			companyDocumentCenterForm.setDocName(fileName);
			companyDocumentCenterForm
					.setDescription((String) companyDocumentDetail.get(
							getAlias(CompanyDocument_.description),
							String.class));

			companyDocumentCenterForm.setYear(String
					.valueOf(companyDocumentDetail.get(
							getAlias(CompanyDocument_.year), Integer.class)));

			companyDocumentCenterForm.setCategory(cmpDocCategoryName);
			companyDocumentCenterForm.setDocType((String) companyDocumentDetail
					.get(getAlias(CompanyDocumentDetail_.fileType),
							String.class));

			companyDocumentCenterFormList.add(companyDocumentCenterForm);
		}

		CompanyDocumentCenterResponseForm response = new CompanyDocumentCenterResponseForm();

		
		if (pageDTO != null) {
			int recordSize = totalCmpDocList.size();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyDocumentCenterLogic#
	 * searchDocumentEmployeeDocumentCenter(java.lang.String, java.lang.String,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long, java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public CompanyDocumentCenterResponseForm searchDocumentEmployeeDocumentCenter(
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long categoryId,
			Long employeeId) {

		Employee emp = employeeDAO.findById(employeeId);

		/* int recordSize; */
		String categoryName = null;
		CompanyDocumentConditionDTO companyDocumentConditionDTO = new CompanyDocumentConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.DOCUMENT_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					companyDocumentConditionDTO.setDocumentName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}

		}
		if (searchCondition.equals(PayAsiaConstants.DESCRIPTION)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					companyDocumentConditionDTO.setDescription("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
			}

		}

		if (searchCondition.equals(PayAsiaConstants.YEAR)) {
			if (StringUtils.isNotBlank(searchText)) {
				companyDocumentConditionDTO.setYear(Integer
						.parseInt(searchText));
			}

		}
		if (searchCondition.equals(PayAsiaConstants.CATEGORY)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					companyDocumentConditionDTO.setCategory("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}
				companyDocumentConditionDTO.setCategoryId(Long
						.parseLong(searchText));
			}

		}

		if (categoryId != 0) {
			companyDocumentConditionDTO.setCategory(categoryId.toString());
			companyDocumentConditionDTO.setCategoryId(categoryId);

			List<DocumentCategoryMaster> categoryList = documentCategoryMasterDAO
					.findAll();

			for (DocumentCategoryMaster documentCategoryMaster : categoryList) {

				if (documentCategoryMaster.getDocumentCategoryId() == categoryId) {
					categoryName = documentCategoryMaster.getCategoryName();
				}

			}

		}

		if (searchCondition.equals(PayAsiaConstants.DOCUMENT_TYPE)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					companyDocumentConditionDTO.setType("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}

		}
		if (searchCondition.equals(PayAsiaConstants.UPLOADED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				companyDocumentConditionDTO.setUploadedDate(DateUtils
						.stringToTimestamp(searchText));
			}

		}

		/*
		 * recordSize =
		 * companyDocumentDetailDAO.getCountForTuplesbyConditionCmp(
		 * companyDocumentConditionDTO, companyId);
		 */

	
		List<Tuple> totalCmpDocList = companyDocumentDetailDAO
				.findTuplesByConditionCmp(companyDocumentConditionDTO, null,
						null, companyId);
		List<Tuple> cmpDocList = companyDocumentDetailDAO
				.findTuplesByConditionCmp(companyDocumentConditionDTO, pageDTO,
						sortDTO, companyId);

		List<CompanyDocumentCenterForm> companyDocumentCenterFormList = new ArrayList<CompanyDocumentCenterForm>();

		if (categoryId != 0) {

			if (categoryName
					.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {

				String[] fileName;

				for (Tuple companyDocumentDetail : cmpDocList) {
					CompanyDocumentCenterForm companyDocumentCenterForm = new CompanyDocumentCenterForm();

					companyDocumentCenterForm
							.setDocId((Long) companyDocumentDetail
									.get(getAlias(CompanyDocumentDetail_.companyDocumentDetailID),
											Long.class));
					companyDocumentCenterForm
							.setDocName((String) companyDocumentDetail.get(
									getAlias(CompanyDocumentDetail_.fileName),
									String.class));
					companyDocumentCenterForm
							.setDescription((String) companyDocumentDetail.get(
									getAlias(CompanyDocument_.description),
									String.class));

					companyDocumentCenterForm
							.setYear((String) companyDocumentDetail.get(
									getAlias(CompanyDocument_.year),
									String.class));

					companyDocumentCenterForm
							.setCategory((String) companyDocumentDetail
									.get(getAlias(DocumentCategoryMaster_.categoryName),
											String.class));
					companyDocumentCenterForm
							.setDocType((String) companyDocumentDetail.get(
									getAlias(CompanyDocumentDetail_.fileType),
									String.class));
					String fileNameStr = (String) companyDocumentDetail.get(
							getAlias(CompanyDocumentDetail_.fileName),
							String.class);
					fileName = fileNameStr.split(fileNameSeperator);

					try {
						if (fileName[0].equalsIgnoreCase(emp
								.getEmployeeNumber())) {
							companyDocumentCenterFormList
									.add(companyDocumentCenterForm);
						}
					} catch (Exception exception) {
						LOGGER.error(exception.getMessage(), exception);

					}
				}

			} else {

				for (Tuple companyDocumentDetail : cmpDocList) {
					CompanyDocumentCenterForm companyDocumentCenterForm = new CompanyDocumentCenterForm();
					companyDocumentCenterForm
							.setDocId((Long) companyDocumentDetail
									.get(getAlias(CompanyDocumentDetail_.companyDocumentDetailID),
											Long.class));
					String fileName;
					String cmpDocCategoryName = (String) companyDocumentDetail
							.get(getAlias(DocumentCategoryMaster_.categoryName),
									String.class);
					if (cmpDocCategoryName
							.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {
						fileName = (String) companyDocumentDetail.get(
								getAlias(CompanyDocumentDetail_.fileName),
								String.class);
						String ext = fileName.substring(fileName
								.lastIndexOf('.') + 1);
						fileName = fileName.substring(0, fileName.indexOf('_'))
								+ "." + ext;

					} else {

						fileName = (String) companyDocumentDetail.get(
								getAlias(CompanyDocumentDetail_.fileName),
								String.class);
					}

					companyDocumentCenterForm.setDocName(fileName);
					companyDocumentCenterForm
							.setDescription((String) companyDocumentDetail.get(
									getAlias(CompanyDocument_.description),
									String.class));

					companyDocumentCenterForm
							.setYear((String) companyDocumentDetail.get(
									getAlias(CompanyDocument_.year),
									String.class));

					companyDocumentCenterForm
							.setCategory((String) companyDocumentDetail
									.get(getAlias(DocumentCategoryMaster_.categoryName),
											String.class));
					companyDocumentCenterForm
							.setDocType((String) companyDocumentDetail.get(
									getAlias(CompanyDocumentDetail_.fileType),
									String.class));

					companyDocumentCenterFormList
							.add(companyDocumentCenterForm);
				}

			}
		}

CompanyDocumentCenterResponseForm response = new CompanyDocumentCenterResponseForm();

		
		if (pageDTO != null) {
			int recordSize = totalCmpDocList.size();
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

	private String getFilePath(String categoryType, Long companyId, String year) {

		String filePath = null;
		if (categoryType
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {
			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyId,
							PayAsiaConstants.COMPANY_DOCUMENT_GENERAL, null,
							null, null, null,
							PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * filePath = downloadPath + "/company/" + companyId + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_GENERAL;
			 */

		} else if (categoryType
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {
			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyId,
							PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, null,
							year, null, null,
							PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * filePath = downloadPath + "/company/" + companyId + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" + year + "/";
			 */

		} else {
			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyId,
							PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT,
							null, year, null, null,
							PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * filePath = downloadPath + "/company/" + companyId + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT + "/" + year +
			 * "/";
			 */

		}

		return filePath;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDocumentCenterLogic#uploadDocument(com.payasia
	 * .common.form.CompanyDocumentCenterForm, java.lang.Long)
	 */
	private CompanyDocumentCenterForm validateZipEntiresForbiddenChars(
			byte[] zipFileBytes, String pattern) {
		CompanyDocumentCenterForm response = new CompanyDocumentCenterForm();
		List<String> invalidZipEntries = FileUtils
				.validateZipEntriesForbiddenChars(zipFileBytes, pattern);
		if (!invalidZipEntries.isEmpty()) {
			response.setZipEntriesInvalid(true);
			response.setMessage("Zipped filenames must not contain invalid characters [ \\ / : * ? \" | , ‘ ]");
		}
		return response;
	}

	@Override
	public CompanyDocumentCenterForm uploadDocument(
			CompanyDocumentCenterForm companyDocumentCenterForm, Long companyId) {
		CompanyDocumentCenterForm response = new CompanyDocumentCenterForm();

		final String invalidCharPattern = ".*[\\/:*?\"<>|,‘'].*";

		List<CompanyDocumentLogDTO> documentLogs = new ArrayList<CompanyDocumentLogDTO>();

		List<DocumentCategoryMaster> categoryList = documentCategoryMasterDAO
				.findAll();
		String categoryName = null;
		List<String> zipFileNames = null;

		List<String> notExistingZipFileNames = new ArrayList<String>();
		boolean status = false;
		for (DocumentCategoryMaster documentCategoryMaster : categoryList) {

			if (documentCategoryMaster.getDocumentCategoryId() == companyDocumentCenterForm
					.getCategoryId()) {
				categoryName = documentCategoryMaster.getCategoryName();
			}

		}
		String fileName = companyDocumentCenterForm.getFileData()
				.getOriginalFilename();
		String filePath = getFilePath(categoryName, companyId,
				companyDocumentCenterForm.getYear());

		CompanyDocumentDetail companyDocumentDetailVO = null;

		String fileType = fileName.substring(fileName.lastIndexOf('.') + 1);

		if (fileType.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {
			CompanyDocumentCenterForm res = validateZipEntiresForbiddenChars(
					companyDocumentCenterForm.getFileData().getBytes(),
					invalidCharPattern);
			if (res.isZipEntriesInvalid()) {
				return res;
			}
		}

		if (categoryName
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {

			if (fileType.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {

				zipFileNames = uploadZipFile(
						companyDocumentCenterForm.getFileData(), filePath,
						fileType, fileNameSeperator);

			} else {
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {

					String ext = fileName
							.substring(fileName.lastIndexOf('.') + 1);

					if (!("").equals(fileName)) {
						UUID uuid = UUID.randomUUID();
						fileName = fileName.substring(0, fileName.indexOf('.'))
								+ fileNameSeperator + uuid + "." + ext;
					}
					awss3LogicImpl.uploadCommonMultipartFile(
							companyDocumentCenterForm.getFileData(), filePath
									+ fileName);
				} else {
					fileName = FileUtils.uploadFile(
							companyDocumentCenterForm.getFileData(), filePath,
							fileNameSeperator);
				}
			}
		} else {
			if (fileType.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {

				CompanyDocumentLogDTO companyDocumentSummaryLogDTO = uploadZipFileForTaxDocumentsPayslips(
						companyDocumentCenterForm.getFileData(), filePath,
						fileType, companyId,
						companyDocumentCenterForm.isFileNameContainsCompName(),
						documentLogs);
				companyDocumentSummaryLogDTO.setUploadTypeTaxPdfFile(true);
				companyDocumentSummaryLogDTO
						.setTotalSuccessRecords(companyDocumentSummaryLogDTO
								.getZipFileNamesList().size());
				companyDocumentSummaryLogDTO
						.setTotalFailedRecords(companyDocumentSummaryLogDTO
								.getInvalidEmployeeNumbersList().size());
				response.setUploadTypeTaxPdfFile(true);
				zipFileNames = companyDocumentSummaryLogDTO
						.getZipFileNamesList();
				response.setCompanyDocumentSummaryDTO(companyDocumentSummaryLogDTO);

			}

		}

		if (categoryName
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)
				&& fileType.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {

			for (String zipFileName : zipFileNames) {

				companyDocumentDetailVO = companyDocumentDetailDAO
						.findByCompanyDocumentId(companyId, zipFileName, null,
								PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL,
								companyDocumentCenterForm.getCategoryId());

				if (companyDocumentDetailVO == null) {
					notExistingZipFileNames.add(zipFileName);
					status = true;
				}

			}

		} else if (categoryName
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)
				&& fileType.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {
			for (String zipFileName : zipFileNames) {

				companyDocumentDetailVO = companyDocumentDetailDAO
						.findByCompanyDocumentId(
								companyId,
								zipFileName,
								Integer.parseInt(companyDocumentCenterForm
										.getYear()),
								PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT,
								companyDocumentCenterForm.getCategoryId());

				if (companyDocumentDetailVO == null) {
					notExistingZipFileNames.add(zipFileName);
					status = true;
				}

			}

		} else if (categoryName
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)
				&& fileType.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {
			for (String zipFileName : zipFileNames) {

				companyDocumentDetailVO = companyDocumentDetailDAO
						.findByCompanyDocumentId(companyId, zipFileName,
								Integer.parseInt(companyDocumentCenterForm
										.getYear()),
								PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP,
								companyDocumentCenterForm.getCategoryId());

				if (companyDocumentDetailVO == null) {
					notExistingZipFileNames.add(zipFileName);
					status = true;
				}

			}

		} else if (categoryName
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {

			companyDocumentDetailVO = companyDocumentDetailDAO
					.findByCompanyDocumentId(companyId, fileName, null,
							PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL,
							companyDocumentCenterForm.getCategoryId());
			if (companyDocumentDetailVO == null) {
				status = true;
			}
		}

		else if (categoryName
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER)
				&& fileType.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {
			for (String zipFileName : zipFileNames) {

				companyDocumentDetailVO = companyDocumentDetailDAO
						.findByCompanyDocumentId(companyId, zipFileName,null,
								PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER,
								companyDocumentCenterForm.getCategoryId());

				if (companyDocumentDetailVO == null) {
					notExistingZipFileNames.add(zipFileName);
					status = true;
				}

			}
			if(notExistingZipFileNames.isEmpty()) {
				response.setSuccess(PayAsiaConstants.PAYASIA_ERROR);
				return response;
			}

		}
		

		try {

			if (status) {

				CompanyDocument companyDocument = new CompanyDocument();
				Company company = new Company();
				company.setCompanyId(companyId);
				companyDocument.setCompany(company);
				DocumentCategoryMaster documentCategoryMaster = new DocumentCategoryMaster();
				documentCategoryMaster
						.setDocumentCategoryId(companyDocumentCenterForm
								.getCategoryId());
				companyDocument
						.setDocumentCategoryMaster(documentCategoryMaster);
				try {
					companyDocument
							.setDescription(URLDecoder.decode(
									companyDocumentCenterForm.getDescription(),
									"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}

				companyDocument
						.setUploadedDate(DateUtils.getCurrentTimestamp());
				if (categoryName
						.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)
						|| categoryName
								.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {
					companyDocument.setYear(Integer
							.parseInt(companyDocumentCenterForm.getYear()));
				}
				companyDocument
						.setSource(PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_DOCUMENT);
				CompanyDocument companyDocumentVO = companyDocumentDAO
						.save(companyDocument);

				Long getMaxDocumentId = companyDocumentVO.getDocumentId();

				if (fileType.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {

					for (String notExistingZipFileName : notExistingZipFileNames) {

						CompanyDocumentDetail companyDocumentDetail = new CompanyDocumentDetail();
						companyDocument.setDocumentId(getMaxDocumentId);

						companyDocumentDetail
								.setCompanyDocument(companyDocument);
						companyDocumentDetail
								.setFileName(notExistingZipFileName);
						
						 if (categoryName.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER)) {
								companyDocumentDetailVO = companyDocumentDetailDAO
										.findByCompanyDocumentId(
												companyId,
												notExistingZipFileName,
												 null, categoryName,
												companyDocumentVO
														.getDocumentCategoryMaster()
														.getDocumentCategoryId());
						 }else{
							 companyDocumentDetailVO = companyDocumentDetailDAO
										.findByCompanyDocumentId(
												companyId,
												notExistingZipFileName,
												Integer.parseInt(companyDocumentCenterForm
														.getYear()), categoryName,
												companyDocumentVO
														.getDocumentCategoryMaster()
														.getDocumentCategoryId());
						 }
						
						if (companyDocumentDetailVO == null) {
							companyDocumentDetail
									.setFileType(notExistingZipFileName
											.substring(notExistingZipFileName
													.lastIndexOf('.') + 1));
							companyDocumentDetailDAO
									.save(companyDocumentDetail);
						}
					}

				} else {

					CompanyDocumentDetail companyDocumentDetail = new CompanyDocumentDetail();
					companyDocument.setDocumentId(getMaxDocumentId);

					companyDocumentDetail.setCompanyDocument(companyDocument);
					companyDocumentDetail.setFileName(fileName);

					companyDocumentDetail.setFileType(fileType);
					companyDocumentDetailDAO.save(companyDocumentDetail);
				}

			}

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);

		}
		response.setCompanyDocumentLogs(documentLogs);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyDocumentCenterLogic#getCategoryList()
	 */
	@Override
	public List<CompanyDocumentCenterForm> getCategoryList() {
		Locale locale = UserContext.getLocale();
		List<CompanyDocumentCenterForm> catList = new ArrayList<CompanyDocumentCenterForm>();
		List<DocumentCategoryMaster> categoryList = documentCategoryMasterDAO
				.findAll();
		for (DocumentCategoryMaster documentCategoryMaster : categoryList) {
			CompanyDocumentCenterForm CompanyDocumentCenterForm = new CompanyDocumentCenterForm();
			CompanyDocumentCenterForm.setCategoryId(documentCategoryMaster
					.getDocumentCategoryId());
			String labelMsg;
			if (StringUtils.isNotBlank(documentCategoryMaster.getLabelKey())) {
				labelMsg = messageSource.getMessage(
						documentCategoryMaster.getLabelKey(), new Object[] {},
						locale);
			} else {
				labelMsg = documentCategoryMaster.getCategoryName();
			}
			try {

				CompanyDocumentCenterForm.setCategoryName(URLEncoder.encode(
						labelMsg, "UTF-8"));
			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
				throw new PayAsiaSystemException(
						unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}
			catList.add(CompanyDocumentCenterForm);
		}
		return catList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyDocumentCenterLogic#getUploadedDoc(long)
	 */
	@Override
	public CompanyDocumentCenterForm getUploadedDoc(long docId) {

		CompanyDocumentCenterForm uploadedDoc = new CompanyDocumentCenterForm();
		CompanyDocumentDetail companyDocumentDetail = companyDocumentDetailDAO
				.findById(docId);

		uploadedDoc.setCategoryId(companyDocumentDetail.getCompanyDocument()
				.getDocumentCategoryMaster().getDocumentCategoryId());
		
		/*ID ENCRYPT */
		uploadedDoc.setDocId(FormatPreserveCryptoUtil.encrypt(docId));
		
		uploadedDoc.setDescription(companyDocumentDetail.getCompanyDocument()
				.getDescription());
		uploadedDoc.setDocName(companyDocumentDetail.getFileName());
		uploadedDoc.setYear(String.valueOf(companyDocumentDetail
				.getCompanyDocument().getYear()));

		return uploadedDoc;
	}

	@Override
	public String deleteCompanyDocument(Long docId, Long companyId) {

		boolean success = true;
		List<CompanyDocumentLogDTO> documentLogs = new ArrayList<CompanyDocumentLogDTO>();

		CompanyDocumentConditionDTO companyDocumentConditionDTO = new CompanyDocumentConditionDTO();
		CompanyDocumentDetail companyDocumentDetail = companyDocumentDetailDAO
				.findById(docId);
		
		if (companyDocumentDetail != null) {

			String filePath;
			long categoryId = companyDocumentDetail.getCompanyDocument().getDocumentCategoryMaster()
					.getDocumentCategoryId();

			List<DocumentCategoryMaster> categoryList = documentCategoryMasterDAO.findAll();
			String categoryName = null;

			for (DocumentCategoryMaster documentCategoryMaster : categoryList) {

				if (documentCategoryMaster.getDocumentCategoryId() == categoryId) {
					categoryName = documentCategoryMaster.getCategoryName();
				}

			}
			List<String> invalidZipFileNames = new ArrayList<String>();
			try {
				if (categoryName.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {
					FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath,
							rootDirectoryName, companyDocumentDetail.getCompanyDocument().getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, companyDocumentDetail.getFileName(),
							String.valueOf(companyDocumentDetail.getCompanyDocument().getYear()),
							getValidEmployeeNumber(companyDocumentDetail.getFileName(), documentLogs, companyId,
									invalidZipFileNames),
							null, PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);
					filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
					/*
					 * filePath = "/company/" + companyDocumentDetail.getCompanyDocument()
					 * .getCompany().getCompanyId() + "/" +
					 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" +
					 * companyDocumentDetail.getCompanyDocument().getYear() + "/" +
					 * getValidEmployeeNumber( companyDocumentDetail.getFileName(), documentLogs,
					 * companyId, invalidZipFileNames) + "/" + companyDocumentDetail.getFileName();
					 */
				} else if (categoryName.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)) {

					FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath,
							rootDirectoryName, companyDocumentDetail.getCompanyDocument().getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT, companyDocumentDetail.getFileName(),
							String.valueOf(companyDocumentDetail.getCompanyDocument().getYear()),
							getValidEmployeeNumber(companyDocumentDetail.getFileName(), documentLogs, companyId,
									invalidZipFileNames),
							null, PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);

					filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
					/*
					 * filePath = "/company/" + companyDocumentDetail.getCompanyDocument()
					 * .getCompany().getCompanyId() + "/" +
					 * PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT + "/" +
					 * companyDocumentDetail.getCompanyDocument().getYear() + "/" +
					 * getValidEmployeeNumber( companyDocumentDetail.getFileName(), documentLogs,
					 * companyId, invalidZipFileNames) + "/" + companyDocumentDetail.getFileName();
					 */
				} else {

					FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath,
							rootDirectoryName, companyDocumentDetail.getCompanyDocument().getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_GENERAL, companyDocumentDetail.getFileName(), null, null,
							null, PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);
					filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
					/*
					 * filePath = "/company/" + companyDocumentDetail.getCompanyDocument()
					 * .getCompany().getCompanyId() + "/" +
					 * PayAsiaConstants.COMPANY_DOCUMENT_GENERAL + "/" +
					 * companyDocumentDetail.getFileName();
					 */

				}
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
					List<String> fileList = new ArrayList<String>();
					fileList.add(filePath);
					awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
				} else {
					FileUtils.deletefile(filePath);
				}
			} catch (Exception exception) {
				success = false;
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(), exception);

			}

			if (success == true) {
				companyDocumentConditionDTO
						.setCompanyDocumentId(companyDocumentDetail.getCompanyDocument().getDocumentId());
				List<CompanyDocumentDetail> cmpDocDetail = companyDocumentDetailDAO
						.findByConditionCmp(companyDocumentConditionDTO, null, null, companyId);
				companyDocumentDetailDAO.delete(companyDocumentDetail);
				if (cmpDocDetail.size() == 1) {
					companyDocumentDAO.delete(companyDocumentDetail.getCompanyDocument());
				}
			}
			return PayAsiaConstants.PAYASIA_SUCCESS;
		}
		return PayAsiaConstants.PAYASIA_ERROR;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CompanyDocumentCenterLogic#viewDocument(long)
	 */
	@Override
	public String viewDocument(long docId, Long companyId,
			Long loggedInEmployeeId) {
		CompanyDocumentDetail companyDocumentDetail = companyDocumentDetailDAO
				.findById(docId);
		if(companyDocumentDetail!=null) {
		String filePath = null;
		List<CompanyDocumentLogDTO> documentLogs = new ArrayList<CompanyDocumentLogDTO>();
		List<String> invalidZipFileNames = new ArrayList<String>();
		long categoryId = companyDocumentDetail.getCompanyDocument()
				.getDocumentCategoryMaster().getDocumentCategoryId();

		CompanyDocumentCenterResponseForm response = searchDocument("", "", null, null,                                   //change retrun type CompanyDocumentCenterResponseForm
				companyId, categoryId, loggedInEmployeeId);
		//List<CompanyDocumentCenterForm> companyDocumentCenterFormList = response.getCompanyDocumentCenterFormList();
		List<CompanyDocumentCenterForm> companyDocumentCenterFormList = response.getRows();
		List<Long> validDocumentIdList = new ArrayList<Long>();
		for (CompanyDocumentCenterForm documentCenterForm : companyDocumentCenterFormList) {
			/*ID DECRYPT */
			validDocumentIdList.add(FormatPreserveCryptoUtil.decrypt(documentCenterForm.getDocId()));
		}
		if (validDocumentIdList.isEmpty()) {
			return filePath;
		}
		if (!validDocumentIdList.isEmpty()
				&& !validDocumentIdList.contains(docId)) {
			return filePath;
		}

		List<DocumentCategoryMaster> categoryList = documentCategoryMasterDAO
				.findAll();
		String categoryName = null;

		for (DocumentCategoryMaster documentCategoryMaster : categoryList) {

			if (documentCategoryMaster.getDocumentCategoryId() == categoryId) {
				categoryName = documentCategoryMaster.getCategoryName();
			}

		}

		if (categoryName
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {
			/*
			 * filePath = "/company/" +
			 * companyDocumentDetail.getCompanyDocument().getCompany()
			 * .getCompanyId() + "/" + PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP
			 * + "/" + companyDocumentDetail.getCompanyDocument().getYear() +
			 * "/" + getValidEmployeeNumber(
			 * companyDocumentDetail.getFileName(), documentLogs, companyId,
			 * invalidZipFileNames) + "/" + companyDocumentDetail.getFileName();
			 */
			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(
							downloadPath,
							rootDirectoryName,
							companyDocumentDetail.getCompanyDocument()
									.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP,
							companyDocumentDetail.getFileName(),
							String.valueOf(companyDocumentDetail
									.getCompanyDocument().getYear()),
							getValidEmployeeNumber(
									companyDocumentDetail.getFileName(),
									documentLogs, companyId,
									invalidZipFileNames), null,
							PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);
			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		} else if (categoryName
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)) {
			/*
			 * filePath = "/company/" +
			 * companyDocumentDetail.getCompanyDocument().getCompany()
			 * .getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT + "/" +
			 * companyDocumentDetail.getCompanyDocument().getYear() + "/" +
			 * getValidEmployeeNumber( companyDocumentDetail.getFileName(),
			 * documentLogs, companyId, invalidZipFileNames) + "/" +
			 * companyDocumentDetail.getFileName();
			 */
			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(
							downloadPath,
							rootDirectoryName,
							companyDocumentDetail.getCompanyDocument()
									.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT,
							companyDocumentDetail.getFileName(),
							String.valueOf(companyDocumentDetail
									.getCompanyDocument().getYear()),
							getValidEmployeeNumber(
									companyDocumentDetail.getFileName(),
									documentLogs, companyId,
									invalidZipFileNames), null,
							PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);
			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		} else if (categoryName
				.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER)) {
			/*
			 * filePath = "/company/" +
			 * companyDocumentDetail.getCompanyDocument().getCompany()
			 * .getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_LETTER + "/" +
			 * employeeVO.getEmployeeNumber() + "/" +
			 * companyDocumentDetail.getFileName();
			 */
			String employeeNumber = companyDocumentDetail.getFileName()
					.substring(0,
							companyDocumentDetail.getFileName().indexOf("_"));
			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyDocumentDetail.getCompanyDocument()
									.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_EMPLOYEE_LETTER,
							companyDocumentDetail.getFileName(), null,
							employeeNumber, null,
							PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);
			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		} else {
			/*
			 * filePath = "/company/" +
			 * companyDocumentDetail.getCompanyDocument().getCompany()
			 * .getCompanyId() + "/" + PayAsiaConstants.COMPANY_DOCUMENT_GENERAL
			 * + "/" + companyDocumentDetail.getFileName();
			 */
			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyDocumentDetail.getCompanyDocument()
									.getCompany().getCompanyId(),
							PayAsiaConstants.COMPANY_DOCUMENT_GENERAL,
							companyDocumentDetail.getFileName(), null, null,
							null, PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);
			filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

		}
		return filePath;
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDocumentCenterLogic#updateDocument(com.payasia
	 * .common.form.CompanyDocumentCenterForm, java.lang.Long)
	 */
	@Override
	public String updateDocument(
			CompanyDocumentCenterForm companyDocumentCenterForm, Long companyId) {
		boolean status = true;
		CompanyDocumentDetail companyDocumentDetail = null;
		CompanyDocument companyDocument = null;
		List<CompanyDocumentLogDTO> documentLogs = new ArrayList<CompanyDocumentLogDTO>();
		List<String> invalidZipFileNames = new ArrayList<String>();
		try {
			companyDocumentDetail = companyDocumentDetailDAO
					.findById(companyDocumentCenterForm.getDocId());
			Long categoryId = companyDocumentDetail.getCompanyDocument()
					.getDocumentCategoryMaster().getDocumentCategoryId();
			companyDocument = companyDocumentDetail.getCompanyDocument();

			List<DocumentCategoryMaster> categoryList = documentCategoryMasterDAO
					.findAll();
			String categoryName = null;

			for (DocumentCategoryMaster documentCategoryMaster : categoryList) {

				if (documentCategoryMaster.getDocumentCategoryId() == categoryId) {
					categoryName = documentCategoryMaster.getCategoryName();
				}

			}

			String deleteFile;
			String newFilePath;
			String fileName;
			String fileType;
			if (categoryName
					.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {
				if(companyDocumentCenterForm.getFileData()!=null) {             // file is not overwrite so we get null value of file data,therefore we added this
				if (companyDocumentCenterForm.getFileData().getSize() > 0) {
					FilePathGeneratorDTO filePathGenerator = fileUtils
							.getFileCommonPath(downloadPath, rootDirectoryName,
									companyDocumentDetail.getCompanyDocument()
											.getCompany().getCompanyId(),
									PayAsiaConstants.COMPANY_DOCUMENT_GENERAL,
									companyDocumentDetail.getFileName(), null,
									null, null,
									PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT,
									0);

					deleteFile = fileUtils
							.getGeneratedFilePath(filePathGenerator);
					/*
					 * deleteFile = "/company/" +
					 * companyDocumentDetail.getCompanyDocument()
					 * .getCompany().getCompanyId() + "/" +
					 * PayAsiaConstants.COMPANY_DOCUMENT_GENERAL +
					 * companyDocumentDetail.getFileName();
					 */
					FileUtils.deletefile(deleteFile);
					filePathGenerator
							.setEventName(PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT);
					newFilePath = fileUtils
							.getGeneratedFilePath(filePathGenerator);
					/*
					 * newFilePath = "/company/" +
					 * companyDocumentDetail.getCompanyDocument()
					 * .getCompany().getCompanyId() + "/" +
					 * PayAsiaConstants.COMPANY_DOCUMENT_GENERAL;
					 */

					fileName = FileUtils.uploadFile(
							companyDocumentCenterForm.getFileData(),
							newFilePath, fileNameSeperator);

					companyDocumentDetail.setFileName(fileName);
					fileType = fileName
							.substring(fileName.lastIndexOf('.') + 1);
					companyDocumentDetail.setFileType(fileType);

				}
				}

			} else {
				if(companyDocumentCenterForm.getFileData()!=null) {             // file is not overwrite so we get null value of file data,therefore we added this
				if (companyDocumentCenterForm.getFileData().getSize() > 0) {

					String employeeNumber = generalLogic
							.getValidEmployeeNumberFromFileName(
									companyDocumentCenterForm.getFileData()
											.getOriginalFilename(),
									documentLogs, companyId,
									companyDocumentCenterForm
											.isFileNameContainsCompName(),
									invalidZipFileNames);
					if (!employeeNumber
							.equalsIgnoreCase(PayAsiaConstants.PAYASIA_ERROR)) {
						if (categoryName
								.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {
							FilePathGeneratorDTO filePathGenerator = fileUtils
									.getFileCommonPath(
											downloadPath,
											rootDirectoryName,
											companyDocumentDetail
													.getCompanyDocument()
													.getCompany()
													.getCompanyId(),
											PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP,
											companyDocumentDetail.getFileName(),
											String.valueOf(companyDocument
													.getYear()),
											getValidEmployeeNumber(
													companyDocumentDetail
															.getFileName(),
													documentLogs, companyId,
													invalidZipFileNames),
											null,
											PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT,
											0);

							deleteFile = fileUtils
									.getGeneratedFilePath(filePathGenerator);
							/*
							 * deleteFile = "/company/" + companyDocumentDetail
							 * .getCompanyDocument().getCompany()
							 * .getCompanyId() + "/" +
							 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" +
							 * companyDocument.getYear() + "/" +
							 * getValidEmployeeNumber(
							 * companyDocumentDetail.getFileName(),
							 * documentLogs, companyId, invalidZipFileNames) +
							 * "/" + companyDocumentDetail.getFileName();
							 */

						} else {
							FilePathGeneratorDTO filePathGenerator = fileUtils
									.getFileCommonPath(
											downloadPath,
											rootDirectoryName,
											companyDocumentDetail
													.getCompanyDocument()
													.getCompany()
													.getCompanyId(),
											PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT,
											companyDocumentDetail.getFileName(),
											String.valueOf(companyDocument
													.getYear()),
											getValidEmployeeNumber(
													companyDocumentDetail
															.getFileName(),
													documentLogs, companyId,
													invalidZipFileNames),
											null,
											PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT,
											0);
							deleteFile = fileUtils
									.getGeneratedFilePath(filePathGenerator);
							/*
							 * deleteFile = "/company/" + companyDocumentDetail
							 * .getCompanyDocument().getCompany()
							 * .getCompanyId() + "/" +
							 * PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT +
							 * "/" + companyDocument.getYear() + "/" +
							 * getValidEmployeeNumber(
							 * companyDocumentDetail.getFileName(),
							 * documentLogs, companyId, invalidZipFileNames) +
							 * "/" + companyDocumentDetail.getFileName();
							 */

						}
						FileUtils.deletefile(deleteFile);
						if (categoryName
								.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {
							FilePathGeneratorDTO filePathGenerator = fileUtils
									.getFileCommonPath(
											downloadPath,
											rootDirectoryName,
											companyDocumentDetail
													.getCompanyDocument()
													.getCompany()
													.getCompanyId(),
											PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP,
											null,
											String.valueOf(companyDocument
													.getYear()),
											employeeNumber,
											null,
											PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT,
											0);
							newFilePath = fileUtils
									.getGeneratedFilePath(filePathGenerator);

							/*
							 * newFilePath = "/company/" + companyDocumentDetail
							 * .getCompanyDocument().getCompany()
							 * .getCompanyId() + "/" +
							 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" +
							 * companyDocument.getYear() + "/" + employeeNumber;
							 */

						} else {
							FilePathGeneratorDTO filePathGenerator = fileUtils
									.getFileCommonPath(
											downloadPath,
											rootDirectoryName,
											companyDocumentDetail
													.getCompanyDocument()
													.getCompany()
													.getCompanyId(),
											PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT,
											null,
											String.valueOf(companyDocument
													.getYear()),
											employeeNumber,
											null,
											PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT,
											0);
							newFilePath = fileUtils
									.getGeneratedFilePath(filePathGenerator);
							/*
							 * newFilePath = "/company/" + companyDocumentDetail
							 * .getCompanyDocument().getCompany()
							 * .getCompanyId() + "/" +
							 * PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT +
							 * "/" + companyDocument.getYear() + "/" +
							 * employeeNumber;
							 */

						}

						if (companyDocumentCenterForm
								.isFileNameContainsCompName()) {
							fileName = companyDocumentCenterForm.getFileData()
									.getOriginalFilename();
							fileName = fileName.substring(
									fileName.indexOf('_') + 1,
									fileName.length());
						} else {
							fileName = companyDocumentCenterForm.getFileData()
									.getOriginalFilename();
						}
						if (categoryName
								.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {
							FileUtils.uploadFile(
									companyDocumentCenterForm.getFileData(),
									newFilePath, fileNameSeperator);

						} else {

							FileUtils.uploadFileTaxPaySlip(
									companyDocumentCenterForm.getFileData(),
									fileName, newFilePath);
						}

						companyDocumentDetail.setFileName(fileName);
						fileType = fileName
								.substring(fileName.lastIndexOf('.') + 1);
						companyDocumentDetail.setFileType(fileType);

					}
				}

			}
			}
		} catch (Exception exception) {

			status = false;
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}

		if (status == true) {
			try {
				companyDocument.setDescription(URLDecoder.decode(
						companyDocumentCenterForm.getDescription(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			companyDocument
					.setSource(PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_DOCUMENT);
			companyDocumentDAO.update(companyDocument);
			companyDocumentDetailDAO.update(companyDocumentDetail);
		}
		return PayAsiaConstants.PAYASIA_SUCCESS;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDocumentCenterLogic#checkCompanyDocument(java
	 * .lang.Long, java.lang.String, java.lang.Long)
	 */
	@Override
	public CompanyDocumentCenterForm checkCompanyDocument(Long catergoryId,
			String documentName, Long companyId) {
		CompanyDocumentCenterForm cmpCenterForm = new CompanyDocumentCenterForm();
		CompanyDocument companyDocumentVO = companyDocumentDAO
				.findByCategoryCompanyAndDocumentName(catergoryId,
						documentName, companyId);
		if (companyDocumentVO != null) {
			cmpCenterForm.setCompanyDocumentStatus(PayAsiaConstants.EXISTS);
		} else {
			cmpCenterForm.setCompanyDocumentStatus(PayAsiaConstants.NOT_EXISTS);
		}
		return cmpCenterForm;
	}

	/**
	 * get Employee FilterList.
	 * 
	 * @param companyId
	 *            the company id
	 * @return ManageUserAddCompanyResponseForm contains companies list.
	 */
	@Override
	public List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();

		List<EntityMaster> entityMasterList = entityMasterDAO.findAll();
		Long entityId = getEntityId(PayAsiaConstants.EMPLOYEE_ENTITY_NAME,
				entityMasterList);

		List<DataDictionary> dataDictionaryList = dataDictionaryDAO
				.findByConditionEntity(entityId, PayAsiaConstants.STATIC_TYPE);
		if (dataDictionaryList != null) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setDataDictionaryId(dataDictionary
						.getDataDictionaryId());
				employeeFilterListForm.setFieldName(dataDictionary.getLabel());
				try {
					ColumnPropertyDTO columnPropertyDTO = generalDAO
							.getColumnProperties(dataDictionary.getTableName(),
									dataDictionary.getColumnName());
					employeeFilterListForm.setDataType(columnPropertyDTO
							.getColumnType());
				} catch (Exception exception) {
					LOGGER.error(exception.getMessage(), exception);
					throw new PayAsiaSystemException(exception.getMessage(),
							exception);
				}
				employeeFilterList.add(employeeFilterListForm);
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

				DynamicForm dynamicForm = dynamicFormDAO
						.findMaxVersionByFormId(companyId, entityId,
								dataDictionary.getFormID());
				Unmarshaller unmarshaller = null;
				try {
					unmarshaller = XMLUtil.getDocumentUnmarshaller();
				} catch (JAXBException jAXBException) {

					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				} catch (SAXException sAXException) {

					LOGGER.error(sAXException.getMessage(), sAXException);
					throw new PayAsiaSystemException(sAXException.getMessage(),
							sAXException);
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
				} catch (JAXBException jAXBException) {

					LOGGER.error(jAXBException.getMessage(), jAXBException);
					throw new PayAsiaSystemException(
							jAXBException.getMessage(), jAXBException);
				}
				List<Field> listOfFields = tab.getField();

				for (Field field : listOfFields) {
					if (!StringUtils.equalsIgnoreCase(field.getType(), "table")
							&& !StringUtils.equalsIgnoreCase(field.getType(),
									"label")
							&& !StringUtils.equalsIgnoreCase(field.getType(),
									"calculatoryfield")) {
						if (new String(Base64.decodeBase64(field
								.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())) {
							employeeFilterListForm.setDataType(field.getType());

						}

					} else if ("table".equals(field.getType())) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDocumentCenterLogic#getEditEmployeeFilterList
	 * (java.lang.Long)
	 */
	@Override
	public List<EmployeeFilterListForm> getEditEmployeeFilterList(
			Long documentId, Long companyId) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();

		CompanyDocumentDetail companyDocumentDetail = companyDocumentDetailDAO
				.findById(documentId);

		List<CompanyDocumentShortList> companyDocumentShortVOList = companyDocumentShortListDAO
				.findByCondition(companyDocumentDetail.getCompanyDocument()
						.getDocumentId());
		if (companyDocumentShortVOList != null) {
			for (CompanyDocumentShortList documentShortList : companyDocumentShortVOList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setCloseBracket(documentShortList
						.getCloseBracket());
				employeeFilterListForm.setDataDictionaryId(documentShortList
						.getDataDictionary().getDataDictionaryId());
				employeeFilterListForm.setEqualityOperator(documentShortList
						.getEqualityOperator());
				employeeFilterListForm.setFilterId(documentShortList
						.getShortListId());
				employeeFilterListForm.setLogicalOperator(documentShortList
						.getLogicalOperator());
				employeeFilterListForm.setOpenBracket(documentShortList
						.getOpenBracket());
				employeeFilterListForm.setValue(documentShortList.getValue());
				employeeFilterListForm.setDataType(generalFilterLogic
						.getFieldDataType(companyId,
								documentShortList.getDataDictionary()));
				employeeFilterList.add(employeeFilterListForm);
			}
		}

		return employeeFilterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDocumentCenterLogic#deleteFilter(java.lang.Long)
	 */
	@Override
	public String deleteFilter(Long filterId) {
		CompanyDocumentShortList companyDocumentShortList = companyDocumentShortListDAO
				.findById(filterId);
		companyDocumentShortListDAO.delete(companyDocumentShortList);
		return PayAsiaConstants.PAYASIA_SUCCESS;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CompanyDocumentCenterLogic#saveEmployeeFilterList(java
	 * .lang.String, java.lang.Long)
	 */
	@Override
	public String saveEmployeeFilterList(String metaData, Long documentId) {
		Boolean saveStatus = false;
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = EmployeeFilterXMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException | SAXException exception) {

			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(),
					exception);
		}

		final StringReader xmlReader = new StringReader(metaData);
		
		Source xmlSource = null;
		try {
			xmlSource = EmployeeFilterXMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		EmployeeFilterTemplate empFilterTemplate = null;
		try {
			empFilterTemplate = (EmployeeFilterTemplate) unmarshaller
					.unmarshal(xmlSource);
		} catch (JAXBException jAXBException) {

			LOGGER.error(jAXBException.getMessage(), jAXBException);
			throw new PayAsiaSystemException(jAXBException.getMessage(),
					jAXBException);
		}
		CompanyDocumentDetail companyDocumentDetail = companyDocumentDetailDAO
				.findById(documentId);
		companyDocumentShortListDAO.deleteByCondition(companyDocumentDetail
				.getCompanyDocument().getDocumentId());

		List<EmployeeFilter> listOfFields = empFilterTemplate
				.getEmployeeFilter();
		for (EmployeeFilter field : listOfFields) {
			CompanyDocumentShortList companyDocumentShortList = new CompanyDocumentShortList();

			CompanyDocument companydocument = companyDocumentDAO
					.findByID(companyDocumentDetail.getCompanyDocument()
							.getDocumentId());
			companyDocumentShortList.setCompanyDocument(companydocument);

			if (StringUtils.isNotBlank(field.getCloseBracket())) {
				companyDocumentShortList.setCloseBracket(field
						.getCloseBracket());
			}
			if (StringUtils.isNotBlank(field.getOpenBracket())) {
				companyDocumentShortList.setOpenBracket(field.getOpenBracket());

			}
			if (field.getDictionaryId() != 0) {
				DataDictionary dataDictionary = dataDictionaryDAO
						.findById(field.getDictionaryId());
				companyDocumentShortList.setDataDictionary(dataDictionary);
			}

			if (StringUtils.isNotBlank(field.getEqualityOperator())) {
				try {
					String equalityOperator = URLDecoder.decode(
							field.getEqualityOperator(), "UTF-8");

					// Check Valid ShortList Operator
					ShortlistOperatorEnum shortlistOperatorEnum = ShortlistOperatorEnum
							.getFromOperator(equalityOperator);
					if (shortlistOperatorEnum == null) {
						throw new PayAsiaSystemException(
								PayAsiaConstants.PAYASIA_SHORTLIST_OPERATOR_NOT_VALID);
					}
					companyDocumentShortList
							.setEqualityOperator(equalityOperator);
				} catch (UnsupportedEncodingException unsupportedEncodingException) {
					LOGGER.error(unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
					throw new PayAsiaSystemException(
							unsupportedEncodingException.getMessage(),
							unsupportedEncodingException);
				}

			}
			if (StringUtils.isNotBlank(field.getLogicalOperator())) {
				companyDocumentShortList.setLogicalOperator(field
						.getLogicalOperator());
			}

			if (StringUtils.isNotBlank(field.getValue())) {
				String fieldValue = "";
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
				companyDocumentShortList.setValue(fieldValue);
				saveStatus = true;
			}
			companyDocumentShortListDAO.save(companyDocumentShortList);

		}
		if (saveStatus) {
			return "payasia.company.document.center.shortlist.saved.successfully";
		} else {
			return "0";
		}

	}

	/**
	 * Gets the entity id.
	 * 
	 * @param entityName
	 *            the entity name
	 * @param entityMasterList
	 *            the entity master list
	 * @return the entity id
	 */
	private Long getEntityId(String entityName,
			List<EntityMaster> entityMasterList) {
		for (EntityMaster entityMaster : entityMasterList) {
			if (entityName.equalsIgnoreCase(entityMaster.getEntityName())) {
				return entityMaster.getEntityId();
			}
		}
		return null;
	}

	@Override
	public String getValidEmployeeNumber(String fileName,
			List<CompanyDocumentLogDTO> documentLogs, Long companyId,
			List<String> invalidZipFileNames) {
		CompanyDocumentLogDTO companyDocumentLogDTO = new CompanyDocumentLogDTO();
		String[] fileNameArr;
		String employeeNumber;
		String ZipFileEmployeeNumber;
		try {
			fileNameArr = fileName.split(fileNameSeperator);
			ZipFileEmployeeNumber = fileNameArr[0];
			Employee emp = employeeDAO.findByNumber(fileNameArr[0], companyId);
			if (emp != null) {
				employeeNumber = emp.getEmployeeNumber();
			} else {
				companyDocumentLogDTO.setName(fileName);
				companyDocumentLogDTO
						.setMessage("No Employee exists with given employee code");
				documentLogs.add(companyDocumentLogDTO);
				invalidZipFileNames.add(ZipFileEmployeeNumber);
				employeeNumber = PayAsiaConstants.PAYASIA_ERROR;
			}

		} catch (Exception exception) {

			companyDocumentLogDTO.setName(fileName);
			companyDocumentLogDTO.setMessage("Invalid fileName");
			documentLogs.add(companyDocumentLogDTO);
			employeeNumber = PayAsiaConstants.PAYASIA_ERROR;
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);

		}
		return employeeNumber;
	}

	private CompanyDocumentLogDTO uploadZipFileForTaxDocumentsPayslips(
			CommonsMultipartFile fileData, String filePath, String fileType,
			Long companyId, boolean isFileNameContainsCompName,
			List<CompanyDocumentLogDTO> documentLogs) {
		CompanyDocumentLogDTO documentLogDTO = new CompanyDocumentLogDTO();
		List<String> invalidZipFileNames = new ArrayList<String>();
		String entryFileType;
		int totalPdfCount = 0;
		List<String> zipFileNames = new ArrayList<String>();
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
						.substring(entryName.lastIndexOf('.') + 1);
				if (entry.isDirectory()) {

					continue;
				}
				if (!"pdf".equalsIgnoreCase(entryFileType)) {

					continue;
				}

				String employeeNumber = generalLogic
						.getValidEmployeeNumberFromFileName(entryName
								.substring(entryName.lastIndexOf("/") + 1),
								documentLogs, companyId,
								isFileNameContainsCompName, invalidZipFileNames);
				totalPdfCount++;
				if (!employeeNumber
						.equalsIgnoreCase(PayAsiaConstants.PAYASIA_ERROR)) {
					String path = filePath + employeeNumber;

					File folder = new File(path);
					if (!folder.exists()) {
						folder.mkdirs();
					}
					String fileNameForModification;
					if (isFileNameContainsCompName) {
						fileNameForModification = entryName.substring(entryName
								.lastIndexOf("/") + 1);
						fileNameForModification = fileNameForModification
								.substring(
										fileNameForModification.indexOf('_') + 1,
										fileNameForModification.length());
					} else {
						fileNameForModification = entryName.substring(entryName
								.lastIndexOf("/") + 1);
					}
					zipFileNames.add(fileNameForModification);
					if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
							.equalsIgnoreCase(appDeployLocation)) {
						InputStream inputStream = zipFile.getInputStream(entry);
						byte[] fileByteArrayData = IOUtils
								.toByteArray(inputStream);
						awss3LogicImpl.uploadByteArrayFile(fileByteArrayData,
								path + docPathSeperator
										+ fileNameForModification);
					} else {
						File file = new File(path, fileNameForModification);
						InputStream inputStream = zipFile.getInputStream(entry);
						OutputStream out = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int len;
						while ((len = inputStream.read(buf)) > 0)
							out.write(buf, 0, len);
						out.close();
						inputStream.close();
					}
				}

			}

			zipFile.close();
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
			throw new PayAsiaSystemException(ioe.getMessage(), ioe);

		}

		documentLogDTO.setTotalRecords(totalPdfCount);
		documentLogDTO.setZipFileNamesList(zipFileNames);
		documentLogDTO.setInvalidEmployeeNumbersList(invalidZipFileNames);
		return documentLogDTO;
	}

	@Override
	public String getDocumentCategoryName(Long categoryId) {
		DocumentCategoryMaster documentCategory = documentCategoryMasterDAO
				.findById(categoryId);
		if (documentCategory != null) {
			if (StringUtils.isNotBlank(documentCategory.getCategoryName())) {
				return documentCategory.getCategoryName();
			} else {
				return null;
			}

		}
		return null;
	}

	@Override
	public String deleteTaxDocuments(Long[] documentDetailIds, Long companyId) {
		List<DocumentCategoryMaster> categoryList = documentCategoryMasterDAO.findAll();
		for (int count = 0; count < documentDetailIds.length; count++) {

			boolean success = true;
			List<CompanyDocumentLogDTO> documentLogs = new ArrayList<CompanyDocumentLogDTO>();

			CompanyDocumentConditionDTO companyDocumentConditionDTO = new CompanyDocumentConditionDTO();
			CompanyDocumentDetail companyDocumentDetail = companyDocumentDetailDAO.findById(documentDetailIds[count]);
			if (companyDocumentDetail != null) {
				String filePath = null;
				long categoryId = companyDocumentDetail.getCompanyDocument().getDocumentCategoryMaster()
						.getDocumentCategoryId();

				String categoryName = null;
				for (DocumentCategoryMaster documentCategoryMaster : categoryList) {

					if (documentCategoryMaster.getDocumentCategoryId() == categoryId) {
						categoryName = documentCategoryMaster.getCategoryName();
					}

				}
				List<String> invalidZipFileNames = new ArrayList<String>();
				try {
					if (categoryName.equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)) {
						FilePathGeneratorDTO filePathGenerator = fileUtils
								.getFileCommonPath(downloadPath, rootDirectoryName,
										companyDocumentDetail.getCompanyDocument().getCompany().getCompanyId(),
										PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT,
										companyDocumentDetail.getFileName(),
										String.valueOf(companyDocumentDetail.getCompanyDocument().getYear()),
										getValidEmployeeNumber(companyDocumentDetail.getFileName(), documentLogs,
												companyId, invalidZipFileNames),
										null, PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);
						filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
						/*
						 * filePath = "/company/" + companyDocumentDetail.getCompanyDocument()
						 * .getCompany().getCompanyId() + "/" +
						 * PayAsiaConstants.COMPANY_DOCUMENT_TAX_DOCUMENT + "/" +
						 * companyDocumentDetail.getCompanyDocument() .getYear() + "/" +
						 * getValidEmployeeNumber( companyDocumentDetail.getFileName(), documentLogs,
						 * companyId, invalidZipFileNames) + "/" + companyDocumentDetail.getFileName();
						 */
					}

					FileUtils.deletefile(filePath);
				} catch (Exception exception) {
					success = false;
					LOGGER.error(exception.getMessage(), exception);
					throw new PayAsiaSystemException(exception.getMessage(), exception);

				}

				if (success == true) {
					companyDocumentConditionDTO
							.setCompanyDocumentId(companyDocumentDetail.getCompanyDocument().getDocumentId());
					List<CompanyDocumentDetail> cmpDocDetail = companyDocumentDetailDAO
							.findByConditionCmp(companyDocumentConditionDTO, null, null, companyId);
					companyDocumentDetailDAO.delete(companyDocumentDetail);
					if (cmpDocDetail.size() == 1) {
						companyDocumentDAO.delete(companyDocumentDetail.getCompanyDocument());
					}
				}
			}
		}
		return PayAsiaConstants.PAYASIA_SUCCESS;
	}

	private List<String> uploadZipFile(CommonsMultipartFile fileData,
			String filePath, String fileType, String fileNameSeperator) {

		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		List<String> zipFileNames = new ArrayList<String>();
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

				if (entry.isDirectory()) {

					continue;
				}
				String ext = entryName
						.substring(entryName.lastIndexOf('.') + 1);
				UUID uuid = UUID.randomUUID();
				entryName = entryName.substring(0, entryName.indexOf('.'))
						+ fileNameSeperator + uuid + "." + ext;

				File file = new File(filePath, entryName.substring(entryName
						.lastIndexOf("/") + 1));
				InputStream inputStream = zipFile.getInputStream(entry);
				zipFileNames
						.add(entryName.substring(entryName.lastIndexOf("/") + 1));

				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
						.equalsIgnoreCase(appDeployLocation)) {
					awss3LogicImpl.uploadCommonMultipartFile(
							fileData,
							filePath
									+ entryName.substring(entryName
											.lastIndexOf("/") + 1));
				} else {
					OutputStream out = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int len;
					while ((len = inputStream.read(buf)) > 0)
						out.write(buf, 0, len);
					out.close();
				}
				inputStream.close();

			}

			zipFile.close();
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
			throw new PayAsiaSystemException(ioe.getMessage(), ioe);

		}

		return zipFileNames;
	}
	
	
}
