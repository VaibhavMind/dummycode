package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.mind.payasia.xml.bean.Column;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.CompanyDocumentConditionDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.PartDTO;
import com.payasia.common.dto.PaySlipPDFTemplateDTO;
import com.payasia.common.dto.PayslipConditionDTO;
import com.payasia.common.dto.PayslipDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeePaySlipForm;
import com.payasia.common.form.EmployeePayslipResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PartsForm;
import com.payasia.common.form.PayslipResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateResponse;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.common.util.PayslipComparison;
import com.payasia.common.util.XMLUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyDocumentDAO;
import com.payasia.dao.CompanyDocumentDetailDAO;
import com.payasia.dao.CompanyPayslipReleaseDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DocumentCategoryMasterDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.EmailAttachmentDAO;
import com.payasia.dao.EmailDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.PayslipDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyDocument;
import com.payasia.dao.bean.CompanyPayslipRelease;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DocumentCategoryMaster;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormRecord;
import com.payasia.dao.bean.Email;
import com.payasia.dao.bean.EmailAttachment;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.Payslip;
import com.payasia.dao.bean.PayslipFrequency;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.EmployeePaySlipLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.PaySlipDesignerLogic;
import com.payasia.logic.PaySlipPDFLogoHeaderSection;
import com.payasia.logic.PaySlipPDFOtherSection;
import com.payasia.logic.SecurityLogic;

/**
 * The Class EmployeePaySlipLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class EmployeePaySlipLogicImpl implements EmployeePaySlipLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(EmployeePaySlipLogicImpl.class);

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Value("#{payasiaptProperties['payasia.document.path.separator']}")
	private static String docPathSeperator;

	@Resource
	CompanyDocumentDetailDAO companyDocumentDetailDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;
	@Resource
	GeneralDAO generalDAO;
	@Resource
	CompanyDocumentDAO companyDocumentDAO;
	@Resource
	DocumentCategoryMasterDAO documentCategoryMasterDAO;
	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The payslip dao. */
	@Resource
	PayslipDAO payslipDAO;

	/** The pay slip pdf logo section. */
	@Resource
	PaySlipPDFLogoHeaderSection paySlipPDFLogoSection;

	/** The pay slip designer logic. */
	@Resource
	PaySlipDesignerLogic paySlipDesignerLogic;

	/** The pay slip pdf other section. */
	@Resource
	PaySlipPDFOtherSection paySlipPDFOtherSection;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	GeneralLogic generalLogic;
	@Resource
	CompanyPayslipReleaseDAO companyPayslipReleaseDAO;

	@Resource
	MonthMasterDAO monthMasterDAO;

	@Resource
	EmailDAO emailDAO;

	@Resource
	EmailAttachmentDAO emailAttachmentDAO;

	@Resource
	SecurityLogic securityLogic;

	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;

	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	@Resource
	FileUtils fileUtils;

	@Resource
	private AWSS3Logic awss3LogicImpl;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeePaySlipLogic#getPaySlipFrequencyDetails(java
	 * .lang.Long)
	 */
	@Override
	public EmployeePaySlipForm getPaySlipFrequencyDetails(Long companyId) {
		EmployeePaySlipForm employeePaySlipForm = new EmployeePaySlipForm();

		Company company = companyDAO.findById(companyId);
		PayslipFrequency paySlipFrequency = company.getPayslipFrequency();
		employeePaySlipForm.setPaySlipFrequency(paySlipFrequency.getFrequency());
		employeePaySlipForm.setPayslipPart(company.getPart());

		return employeePaySlipForm;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeePaySlipLogic#getPaySlipDetails(java.lang.Long,
	 * com.payasia.common.form.EmployeePaySlipForm)
	 */
	@Override
	public Payslip getPaySlipDetails(Long employeeId, EmployeePaySlipForm employeePaySlipForm) {

		Employee employee = employeeDAO.findById(employeeId);
		if (employee != null) {
			Company company = employee.getCompany();
			PayslipFrequency paySlipFrequency = company.getPayslipFrequency();
			employeePaySlipForm.setPaySlipFrequency(paySlipFrequency.getFrequency());

			if (paySlipFrequency != null
					&& paySlipFrequency.getFrequency().equalsIgnoreCase(PayAsiaConstants.PAYSLIP_FREQUENCY_MONTHLY)) {
				employeePaySlipForm.setPayslipPart(1);
			}
			PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
			payslipConditionDTO.setCompanyId(company.getCompanyId());
			payslipConditionDTO.setEmployeeId(employeeId);
			payslipConditionDTO.setMonthMasterId(employeePaySlipForm.getPayslipMonthId());
			payslipConditionDTO.setPayslipFrequencyId(paySlipFrequency.getPayslipFrequencyID());
			payslipConditionDTO.setYear(employeePaySlipForm.getPayslipYear());
			payslipConditionDTO.setPart(employeePaySlipForm.getPayslipPart());

			return payslipDAO.getPayslip(payslipConditionDTO);
		} else {
			return null;
		}

	}

	@Override
	public List<PayslipDTO> getPaySlipDetailsForEmployee(Long employeeId, EmployeePaySlipForm employeePaySlipForm) {

		Employee employee = employeeDAO.findById(employeeId);
		if (employee != null) {
			Company company = employee.getCompany();
			PayslipFrequency paySlipFrequency = company.getPayslipFrequency();
			employeePaySlipForm.setPaySlipFrequency(paySlipFrequency.getFrequency());

			/*
			 * if (paySlipFrequency.getFrequency().equalsIgnoreCase(
			 * PayAsiaConstants.PAYSLIP_FREQUENCY_MONTHLY)) {
			 * employeePaySlipForm.setPayslipPart(1); }
			 */
			PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
			payslipConditionDTO.setCompanyId(company.getCompanyId());
			payslipConditionDTO.setEmployeeId(employeeId);
			payslipConditionDTO.setMonthMasterId(employeePaySlipForm.getPayslipMonthId());
			payslipConditionDTO.setPayslipFrequencyId(paySlipFrequency.getPayslipFrequencyID());
			payslipConditionDTO.setYear(employeePaySlipForm.getPayslipYear());
			payslipConditionDTO.setPart(employeePaySlipForm.getPayslipPart()); // Comment

			List<Payslip> list = payslipDAO.getPayslipEmployee(payslipConditionDTO);

			List<PayslipDTO> listOfPayslip = new ArrayList<>();
			for (Payslip obj : list) {
				PayslipDTO payslipDTO = new PayslipDTO();
				payslipDTO.setFullName((obj.getEmployee().getFirstName()) + " " + obj.getEmployee().getLastName());
				payslipDTO.setMonthName(obj.getMonthMaster().getMonthName());
				payslipDTO.setMonthId(obj.getMonthMaster().getMonthId());
				payslipDTO.setAction(true);
				payslipDTO.setYear(obj.getYear());
				payslipDTO.setPart(obj.getPart());
				listOfPayslip.add(payslipDTO);
			}
			return listOfPayslip;
		}
		return null;
	}

	@Override
	public EmployeePayslipResponse getPaySlipDetailsForEmployee(Long employeeId,
			EmployeePaySlipForm employeePaySlipForm, String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO) {

		EmployeePayslipResponse employeePayslipResponse = new EmployeePayslipResponse();

		Employee employee = employeeDAO.findById(employeeId);
		if (employee != null) {
			Company company = employee.getCompany();
			PayslipFrequency paySlipFrequency = company.getPayslipFrequency();
			employeePaySlipForm.setPaySlipFrequency(paySlipFrequency.getFrequency());
			PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
			payslipConditionDTO.setCompanyId(company.getCompanyId());
			payslipConditionDTO.setEmployeeId(employeeId);
			payslipConditionDTO.setMonthMasterId(employeePaySlipForm.getPayslipMonthId());
			payslipConditionDTO.setPayslipFrequencyId(paySlipFrequency.getPayslipFrequencyID());
			payslipConditionDTO.setYear(employeePaySlipForm.getPayslipYear());
			payslipConditionDTO.setPart(employeePaySlipForm.getPayslipPart()); // Comment

			List<Payslip> recordSize = payslipDAO.getPayslipEmployee(payslipConditionDTO, null, sortDTO,
					UserContext.getWorkingCompanyId());

			List<Payslip> payslipList = payslipDAO.getPayslipEmployee(payslipConditionDTO, pageDTO, sortDTO,
					UserContext.getWorkingCompanyId());

			List<PayslipDTO> listOfPayslip = new ArrayList<>();
			for (Payslip obj : payslipList) {
				PayslipDTO payslipDTO = new PayslipDTO();
				payslipDTO.setFullName((obj.getEmployee().getFirstName()) + " " + obj.getEmployee().getLastName());
				payslipDTO.setMonthName(obj.getMonthMaster().getMonthName());
				payslipDTO.setMonthId(obj.getMonthMaster().getMonthId());
				payslipDTO.setAction(true);
				payslipDTO.setYear(obj.getYear());
				payslipDTO.setPart(obj.getPart());
				payslipDTO.setReleaseDate(DateUtils.timeStampToString(obj.getUpdatedDate()) != null
						? DateUtils.timeStampToString(obj.getUpdatedDate())
						: DateUtils.timeStampToString(obj.getCreatedDate()));
				listOfPayslip.add(payslipDTO);
			}
			if (pageDTO != null) {

				int pageSize = pageDTO.getPageSize();
				int totalPages = recordSize.size() / pageSize;

				if (recordSize.size() % pageSize != 0) {
					totalPages = totalPages + 1;
				}
				if (recordSize.size() == 0) {
					pageDTO.setPageNumber(0);
				}

				employeePayslipResponse.setPage(pageDTO.getPageNumber());
				employeePayslipResponse.setTotal(totalPages);
				employeePayslipResponse.setRecords(recordSize.size());
				employeePayslipResponse.setRows(listOfPayslip);
			}

			return employeePayslipResponse;
		}
		return null;
	}

	@Override
	public boolean getPaySlipReleaseDetails(Long employeeId, Long companyId, EmployeePaySlipForm employeePaySlipForm) {
		boolean status = false;

		CompanyPayslipRelease companyPayslipReleaseVO = null;
		if (employeePaySlipForm.getPayslipMonthId() != null && employeePaySlipForm.getPayslipPart() != null) {
			companyPayslipReleaseVO = companyPayslipReleaseDAO.findByCondition(null,
					employeePaySlipForm.getPayslipMonthId(), employeePaySlipForm.getPayslipYear(),
					employeePaySlipForm.getPayslipPart(), companyId);
		} else {
			companyPayslipReleaseVO = companyPayslipReleaseDAO.findByCondition(null, null,
					employeePaySlipForm.getPayslipYear(), null, companyId);

		}
		if (companyPayslipReleaseVO != null) {
			if (companyPayslipReleaseVO.isReleased()) {
				status = true;
			}
		}
		if (companyPayslipReleaseVO == null) {
			status = true;
		}
		return status;

	}

	@Override
	public boolean getPaySlipStatusFromCompanyDocument(Long employeeId, Long companyId,
			EmployeePaySlipForm employeePaySlipForm) {
		boolean status = false;
		Employee employee = employeeDAO.findById(employeeId);
		DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
				.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
		CompanyDocument companyDocumentSourceTextVO = companyDocumentDAO.findByConditionSourceTextAndDesc(companyId,
				categoryMaster.getDocumentCategoryId(), employeePaySlipForm.getPayslipYear(),
				employeePaySlipForm.getPayslipPart(), employeePaySlipForm.getPayslipMonthId(),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT, employee.getEmployeeNumber());
		CompanyDocument companyDocumentSourcePDFVO = companyDocumentDAO.findByConditionSourceTextAndDesc(companyId,
				categoryMaster.getDocumentCategoryId(), employeePaySlipForm.getPayslipYear(),
				employeePaySlipForm.getPayslipPart(), employeePaySlipForm.getPayslipMonthId(),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF, employee.getEmployeeNumber());
		if (companyDocumentSourceTextVO != null) {
			if (companyDocumentSourceTextVO.getReleased()) {
				status = true;
			}
		} else if (companyDocumentSourcePDFVO != null) {
			if (companyDocumentSourcePDFVO.getReleased()) {
				status = true;
			}
		} else {
			status = false;
		}
		return status;

	}

	@Override
	public boolean getPaySlipStatusFromCompanyDocumentForAdmin(Long employeeId, Long companyId,
			AdminPaySlipForm adminPaySlipForm) {
		boolean status = false;
		DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
				.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
		CompanyDocument companyDocumentSourceTextVO = companyDocumentDAO.findByConditionSourceTextAndDesc(companyId,
				categoryMaster.getDocumentCategoryId(), adminPaySlipForm.getPayslipYear(),
				adminPaySlipForm.getPayslipPart(), adminPaySlipForm.getPayslipMonthId(),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT, adminPaySlipForm.getEmployeeNumber());
		CompanyDocument companyDocumentSourcePDFVO = companyDocumentDAO.findByConditionSourceTextAndDesc(companyId,
				categoryMaster.getDocumentCategoryId(), adminPaySlipForm.getPayslipYear(),
				adminPaySlipForm.getPayslipPart(), adminPaySlipForm.getPayslipMonthId(),
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF, adminPaySlipForm.getEmployeeNumber());
		if (companyDocumentSourceTextVO != null) {
			status = true;
		} else if (companyDocumentSourcePDFVO != null) {
			status = true;
		} else {
			status = false;
		}
		return status;

	}

	@Override
	public boolean getPaySlipStatusFromCompanyDocumentForPrint(Long employeeId, Long companyId, int year, int part,
			Long month) {
		boolean status = false;
		Employee employeeVO = employeeDAO.findById(employeeId);
		DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
				.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
		CompanyDocument companyDocumentSourceTEXTVO = companyDocumentDAO.findByConditionSourceTextAndDesc(companyId,
				categoryMaster.getDocumentCategoryId(), year, part, month,
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT, employeeVO.getEmployeeNumber());
		CompanyDocument companyDocumentSourcePDFVO = companyDocumentDAO.findByConditionSourceTextAndDesc(companyId,
				categoryMaster.getDocumentCategoryId(), year, part, month,
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_PDF, employeeVO.getEmployeeNumber());
		if (companyDocumentSourceTEXTVO != null) {
			status = true;
		} else if (companyDocumentSourcePDFVO != null) {
			status = true;
		} else {
			status = false;
		}
		return status;

	}

	@Override
	public byte[] getPaySlipFromCompanyDocumentFolderForAdmin(Long employeeId, Long companyId,
			AdminPaySlipForm adminPaySlipForm) throws IOException {
		InputStream pdfIn = null;

		/*
		 * String filePath = downloadPath + "/company/" + companyId + "/" +
		 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" +
		 * adminPaySlipForm.getPayslipYear() + "/" +
		 * adminPaySlipForm.getEmployeeNumber() + "/";
		 */
		String ext = "pdf";
		String fileName = adminPaySlipForm.getEmployeeNumber().toUpperCase() + "_payslip_"
				+ adminPaySlipForm.getPayslipYear() + adminPaySlipForm.getPayslipMonthId()
				+ adminPaySlipForm.getPayslipPart() + "." + ext;

		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, fileName, String.valueOf(adminPaySlipForm.getPayslipYear()),
				adminPaySlipForm.getEmployeeNumber().toUpperCase(), null, PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				try {
					pdfIn = awss3LogicImpl.readS3ObjectAsStream(filePath);
				} catch (Exception e) {
					String fileNameAWS = adminPaySlipForm.getEmployeeNumber() + "_payslip_"
							+ adminPaySlipForm.getPayslipYear() + adminPaySlipForm.getPayslipMonthId()
							+ adminPaySlipForm.getPayslipPart() + "." + ext;
					FilePathGeneratorDTO filePathGeneratorAWS = fileUtils.getFileCommonPath(downloadPath,
							rootDirectoryName, companyId, PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, fileNameAWS,
							String.valueOf(adminPaySlipForm.getPayslipYear()), adminPaySlipForm.getEmployeeNumber(),
							null, PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);
					String filePathAWS = fileUtils.getGeneratedFilePath(filePathGeneratorAWS);
					pdfIn = awss3LogicImpl.readS3ObjectAsStream(filePathAWS);
				}

			} else {
				File pdfFile = new File(filePath);
				pdfIn = new FileInputStream(pdfFile);
			}
		} catch (FileNotFoundException ex) {
			LOGGER.error(ex.getMessage());
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}
		return IOUtils.toByteArray(pdfIn);

	}

	@Override
	public byte[] getPaySlipFromCompanyDocumentFolderForPrint(Long employeeId, Long companyId, int year, int part,
			Long month) throws IOException {
		InputStream pdfIn = null;
		Employee employee = employeeDAO.findById(employeeId);
		/*
		 * String filePath = downloadPath + "/company/" + companyId + "/" +
		 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" + year + "/" +
		 * employee.getEmployeeNumber() + "/";
		 */
		String ext = "pdf";
		String fileName = employee.getEmployeeNumber() + "_payslip_" + year + month + part + "." + ext;
		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, fileName, String.valueOf(year), employee.getEmployeeNumber(),
				null, PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {

				pdfIn = awss3LogicImpl.readS3ObjectAsStream(filePath);

			} else {
				File pdfFile = new File(filePath);
				pdfIn = new FileInputStream(pdfFile);
			}
		} catch (FileNotFoundException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}
		return IOUtils.toByteArray(pdfIn);

	}

	@Override
	public byte[] getPaySlipFromCompanyDocumentFolder(Long employeeId, Long companyId,
			EmployeePaySlipForm employeePaySlipForm) throws IOException {
		InputStream pdfIn = null;
		Employee employee = employeeDAO.findById(employeeId);
		String ext = "pdf";
		String fileName = employee.getEmployeeNumber().toUpperCase() + "_payslip_"
				+ employeePaySlipForm.getPayslipYear() + employeePaySlipForm.getPayslipMonthId()
				+ employeePaySlipForm.getPayslipPart() + "." + ext;
		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, fileName,
				String.valueOf(employeePaySlipForm.getPayslipYear()), employee.getEmployeeNumber().toUpperCase(), null,
				PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

		/*
		 * String filePath = downloadPath + "/company/" + companyId + "/" +
		 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" +
		 * employeePaySlipForm.getPayslipYear() + "/" +
		 * employee.getEmployeeNumber() + "/";
		 */

		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {

				try {
					pdfIn = awss3LogicImpl.readS3ObjectAsStream(filePath);
				} catch (Exception e) {
					String fileNameAWS = employee.getEmployeeNumber() + "_payslip_"
							+ employeePaySlipForm.getPayslipYear() + employeePaySlipForm.getPayslipMonthId()
							+ employeePaySlipForm.getPayslipPart() + "." + ext;
					FilePathGeneratorDTO filePathGeneratorAWS = fileUtils.getFileCommonPath(downloadPath,
							rootDirectoryName, companyId, PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, fileNameAWS,
							String.valueOf(employeePaySlipForm.getPayslipYear()), employee.getEmployeeNumber(), null,
							PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);
					String filePathAWS = fileUtils.getGeneratedFilePath(filePathGeneratorAWS);
					pdfIn = awss3LogicImpl.readS3ObjectAsStream(filePathAWS);
				}
			} else {
				File pdfFile = new File(filePath);
				pdfIn = new FileInputStream(pdfFile);
			}
		} catch (FileNotFoundException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		}
		return IOUtils.toByteArray(pdfIn);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeePaySlipLogic#getPaySlipDetails(com.payasia.
	 * common.form.AdminPaySlipForm, java.lang.Long)
	 */
	@Override
	public Payslip getPaySlipDetails(AdminPaySlipForm adminPaySlipForm, Long companyId, Long employeeId) {

		List<Long> employeeIds = new ArrayList<Long>();
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		List<Long> assignedEmployees = new ArrayList(employeeShortListDTO.getShortListEmployeeIds());

		Employee employee = employeeDAO.findByNumber(adminPaySlipForm.getEmployeeNumber(), companyId);

		if (employee == null) {
			return null;
		}

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() == 0) {
			return null;
		}

		if (employeeShortListDTO.getEmployeeShortList() && !employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {
			for (Object existEmployee : assignedEmployees) {
				employeeIds.add(Long.valueOf(existEmployee.toString()));
			}
			if (!employeeIds.contains(employee.getEmployeeId())) {
				return null;
			}
		}

		Company company = employee.getCompany();
		PayslipFrequency paySlipFrequency = company.getPayslipFrequency();
		adminPaySlipForm.setPaySlipFrequency(paySlipFrequency.getFrequency());

		if (paySlipFrequency.getFrequency().equalsIgnoreCase(PayAsiaConstants.PAYSLIP_FREQUENCY_MONTHLY)) {
			adminPaySlipForm.setPayslipPart(1);
		}
		PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
		payslipConditionDTO.setCompanyId(company.getCompanyId());
		payslipConditionDTO.setEmployeeId(employee.getEmployeeId());
		payslipConditionDTO.setMonthMasterId(adminPaySlipForm.getPayslipMonthId());
		payslipConditionDTO.setPayslipFrequencyId(paySlipFrequency.getPayslipFrequencyID());
		payslipConditionDTO.setYear(adminPaySlipForm.getPayslipYear());
		payslipConditionDTO.setPart(adminPaySlipForm.getPayslipPart());
		return payslipDAO.getPayslip(payslipConditionDTO);

	}

	@Override
	public Payslip getPaySlipDetailsForPrint(Long companyId, Long sessionEmployeeId, Long reqEmployeeId, Integer year,
			Long month, Integer part) {

		List<Long> employeeIds = new ArrayList<Long>();
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(sessionEmployeeId, companyId);
		List<Long> assignedEmployees = new ArrayList(employeeShortListDTO.getShortListEmployeeIds());

		Employee employee = employeeDAO.findById(reqEmployeeId);

		if (employee == null) {
			return null;
		}

		if (employeeShortListDTO.getEmployeeShortList() && employeeShortListDTO.getShortListEmployeeIds().size() == 0) {
			return null;
		}

		if (employeeShortListDTO.getEmployeeShortList() && !employeeShortListDTO.getShortListEmployeeIds().isEmpty()) {
			for (Object existEmployee : assignedEmployees) {
				employeeIds.add(Long.valueOf(existEmployee.toString()));
			}
			if (!employeeIds.contains(employee.getEmployeeId())) {
				return null;
			}
		}

		Company company = employee.getCompany();
		PayslipFrequency paySlipFrequency = company.getPayslipFrequency();

		PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
		payslipConditionDTO.setCompanyId(company.getCompanyId());
		payslipConditionDTO.setEmployeeId(employee.getEmployeeId());
		payslipConditionDTO.setMonthMasterId(month);
		payslipConditionDTO.setPayslipFrequencyId(paySlipFrequency.getPayslipFrequencyID());
		payslipConditionDTO.setYear(year);
		payslipConditionDTO.setPart(part);

		return payslipDAO.getPayslip(payslipConditionDTO);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeePaySlipLogic#generatePdf(java.lang.Long,
	 * com.payasia.dao.bean.Payslip)
	 */
	@Override
	public byte[] generatePdf(Long companyId, Long employeeId, Payslip payslip)
			throws DocumentException, IOException, JAXBException, SAXException {
		try {

			PDFThreadLocal.pageNumbers.set(true);
			return generatePaySlipPdf(companyId, employeeId, payslip);
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			return generatePaySlipPdf(companyId, employeeId, payslip);
		}
	}

	@Override
	public String checkForEffectiveFrom(Long companyId, Payslip payslip) {
		EntityMaster payslipFormEntity = entityMasterDAO.findById(PayAsiaConstants.PAYSLIP_FORM_ENTITY_ID);
		EntityMaster payslipEntity = entityMasterDAO.findById(PayAsiaConstants.PAYSLIP_ENTITY_ID);

		List<Object[]> tuples = dynamicFormDAO.getEffectiveDateByCondition(companyId, payslipFormEntity.getEntityId(),
				PayAsiaConstants.PAYSLIP_BASIC_TAB_NAME, payslip.getYear(), payslip.getMonthMaster().getMonthId(),
				payslip.getPart());

		if (tuples.size() == 0) {
			return "payasia.payslip.no.dynamic.form.error";
		}

		List<Object[]> logoTuples = dynamicFormDAO.getEffectiveDateByCondition(companyId, payslipEntity.getEntityId(),
				PayAsiaConstants.LOGO_SECTION, payslip.getYear(), payslip.getMonthMaster().getMonthId(),
				payslip.getPart());

		if (logoTuples.size() == 0) {
			return "payasia.payslip.no.payslip.designer.error";
		}

		return null;
	}

	/**
	 * Generate pay slip pdf.
	 * 
	 * @param companyId
	 *            the company id
	 * @param payslip
	 *            the payslip
	 * @return the byte[]
	 * @throws SAXException
	 * @throws JAXBException
	 */
	private byte[] generatePaySlipPdf(Long companyId, Long employeeId, Payslip payslip)
			throws DocumentException, IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(employeeId, PAYASIA_TEMP_PATH, "Payslip");
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		try {
			document = new Document(PayAsiaPDFConstants.PAGE_SIZE, PayAsiaPDFConstants.PAGE_LEFT_MARGIN,
					PayAsiaPDFConstants.PAGE_TOP_MARGIN, PayAsiaPDFConstants.PAGE_RIGHT_MARGIN,
					PayAsiaPDFConstants.PAGE_BOTTOM_MARGIN);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);

			PaySlipPDFTemplateDTO pdfTemplateDTO = new PaySlipPDFTemplateDTO();

			paySlipPDFLogoSection.setCompanyId(companyId);
			paySlipPDFLogoSection.setPaySlipPDFTemplateDTO(pdfTemplateDTO);
			paySlipPDFLogoSection.setPayslip(payslip);

			loadDataDictionaryForCaching(companyId);

			List<DynamicFormRecord> dynamicFormRecordList = new ArrayList<DynamicFormRecord>();

			EntityMaster payslipEntityMaster = entityMasterDAO.findById(PayAsiaConstants.PAYSLIP_FORM_ENTITY_ID);

			List<DynamicFormRecord> dynamicFormPayslipRecordList = dynamicFormRecordDAO.findByEntityKey(
					payslip.getPayslipId(), payslipEntityMaster.getEntityId(), payslip.getCompany().getCompanyId());
			if (dynamicFormPayslipRecordList != null) {
				dynamicFormRecordList.addAll(dynamicFormPayslipRecordList);
			}

			EntityMaster companyEntityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.COMPANY_ENTITY_NAME);

			List<DynamicFormRecord> dynamicFormCompanyRecordList = dynamicFormRecordDAO.findByEntityKey(
					payslip.getCompany().getCompanyId(), companyEntityMaster.getEntityId(),
					payslip.getCompany().getCompanyId());
			if (dynamicFormCompanyRecordList != null) {
				dynamicFormRecordList.addAll(dynamicFormCompanyRecordList);
			}

			EntityMaster employeeEntityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
			List<DynamicFormRecord> dynamicFormEmployeeRecordList = dynamicFormRecordDAO.findByEntityKey(
					payslip.getEmployee().getEmployeeId(), employeeEntityMaster.getEntityId(),
					payslip.getCompany().getCompanyId());
			if (dynamicFormEmployeeRecordList != null) {
				dynamicFormRecordList.addAll(dynamicFormEmployeeRecordList);
			}

			pdfTemplateDTO.getLogoHeaderSection()
					.setHeight(paySlipPDFLogoSection.getPageHeaderHeight(writer, document, 1, dynamicFormRecordList));

			pdfTemplateDTO.getFooterSection()
					.setHeight(paySlipPDFLogoSection.getFooterHeight(writer, document, dynamicFormRecordList));

			writer.setPageEvent((PdfPageEvent) paySlipPDFLogoSection);

			document.open();

			paySlipPDFOtherSection.setCompanyId(companyId);
			paySlipPDFOtherSection.setPaySlipPDFTemplateDTO(pdfTemplateDTO);
			paySlipPDFOtherSection.preparePaySlipPDFOtherSection(document, writer, pdfTemplateDTO, payslip,
					dynamicFormRecordList);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow | PdfWriter.CenterWindow);

			document.close();

			pdfIn = new FileInputStream(tempFile);

			return IOUtils.toByteArray(pdfIn);
		} catch (DocumentException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					throw new PayAsiaSystemException(ex.getMessage(), ex);
				}
			}

			IOUtils.closeQuietly(pdfOut);
			IOUtils.closeQuietly(pdfIn);

			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new PayAsiaSystemException(ex.getMessage(), ex);
			}

		}
	}

	private void loadDataDictionaryForCaching(long companyId) {

		dataDictionaryDAO.findByCompanyId(companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeePaySlipLogic#getYearList(java.lang.Long)
	 */
	@Override
	public List<Integer> getYearList(Long employeeId) {
		List<Integer> yearList = payslipDAO.getYearList(employeeId);
		return yearList;
	}

	private Tab getTab(DynamicForm dynamicForm) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = XMLUtil.getDocumentUnmarshaller();
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (SAXException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}

		final StringReader xmlReader = new StringReader(dynamicForm.getMetaData());
		Source xmlSource = null;
		try {
			xmlSource = XMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(), e1);
		}

		Tab tab = null;
		try {
			tab = (Tab) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}
		return tab;
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

		EntityMaster entityMasterVO = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		Long entityId = entityMasterVO.getEntityId();
		HashMap<Long, Tab> tabMap = new HashMap<>();
		List<DataDictionary> dataDictionaryList = dataDictionaryDAO.findByConditionEntity(entityId,
				PayAsiaConstants.STATIC_TYPE);
		if (dataDictionaryList != null) {
			for (DataDictionary dataDictionary : dataDictionaryList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setDataDictionaryId(dataDictionary.getDataDictionaryId());
				employeeFilterListForm.setFieldName(dataDictionary.getLabel());
				try {
					ColumnPropertyDTO columnPropertyDTO = generalDAO.getColumnProperties(dataDictionary.getTableName(),
							dataDictionary.getColumnName());
					employeeFilterListForm.setDataType(columnPropertyDTO.getColumnType());
				} catch (Exception exception) {
					LOGGER.error(exception.getMessage(), exception);
				}
				employeeFilterList.add(employeeFilterListForm);
			}
		}

		List<DataDictionary> dataDictionaryCompanyList = dataDictionaryDAO.findByConditionEntityAndCompanyId(companyId,
				entityId, PayAsiaConstants.DYNAMIC_TYPE);
		if (dataDictionaryCompanyList != null) {
			for (DataDictionary dataDictionary : dataDictionaryCompanyList) {
				EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
				employeeFilterListForm.setDataDictionaryId(dataDictionary.getDataDictionaryId());
				employeeFilterListForm.setFieldName(dataDictionary.getLabel());
				Tab tab = tabMap.get(dataDictionary.getFormID());

				if (tab == null) {
					DynamicForm dynamicForm = dynamicFormDAO.findMaxVersionByFormId(companyId, entityId,
							dataDictionary.getFormID());
					tab = getTab(dynamicForm);
					tabMap.put(dataDictionary.getFormID(), tab);
				}

				List<Field> listOfFields = tab.getField();

				for (Field field : listOfFields) {
					if (!StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.TABLE_FIELD_TYPE)
							&& !StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.LABEL_FIELD_TYPE)
							&& !StringUtils.equalsIgnoreCase(field.getType(),
									PayAsiaConstants.FIELD_TYPE_CALCULATORY_FIELD)) {
						if (new String(Base64.decodeBase64(field.getDictionaryName().getBytes()))
								.equals(dataDictionary.getDataDictName())) {
							employeeFilterListForm.setDataType(field.getType());

						}

					} else if (field.getType().equals(PayAsiaConstants.TABLE_FIELD_TYPE)) {
						List<Column> listOfColumns = field.getColumn();
						for (Column column : listOfColumns) {
							if (new String(Base64.decodeBase64(column.getDictionaryName().getBytes()))
									.equals(dataDictionary.getDataDictName())) {
								employeeFilterListForm.setDataType(column.getType());
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
	public WorkFlowDelegateResponse searchEmployeeListForPrint(PageRequest pageDTO, SortCondition sortDTO,
			Long sessionEmployeeId, String empName, String empNumber, Long companyId, boolean isShortList,
			String metaData) {
		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		if (StringUtils.isNotBlank(empName)) {
			try {
				conditionDTO.setEmployeeName("%" + URLDecoder.decode(empName, "UTF-8") + "%");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		if (StringUtils.isNotBlank(empNumber)) {
			try {
				conditionDTO.setEmployeeNumber("%" + URLDecoder.decode(empNumber, "UTF-8") + "%");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}

		}
		conditionDTO.setEmployeeShortList(false);
		List<BigInteger> payslipShortListEmployeeIds = null;
		List<BigInteger> employeeShortListEmployeeIds = null;
		if (isShortList) {
			EmployeeShortListDTO employeeShortList = generalLogic.getShortListEmployeeIdsForAdvanceFilter(companyId,
					metaData);
			payslipShortListEmployeeIds = employeeShortList.getShortListEmployeeIds();

		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(sessionEmployeeId, companyId);

		employeeShortListEmployeeIds = employeeShortListDTO.getShortListEmployeeIds();

		if (isShortList) {
			if (!employeeShortListEmployeeIds.isEmpty()) {
				employeeShortListEmployeeIds.retainAll(payslipShortListEmployeeIds);
				conditionDTO.setShortListEmployeeIds(employeeShortListEmployeeIds);
			} else {
				conditionDTO.setShortListEmployeeIds(payslipShortListEmployeeIds);
			}
			conditionDTO.setEmployeeShortList(true);

		} else {
			conditionDTO.setShortListEmployeeIds(employeeShortListEmployeeIds);
			if (!employeeShortListEmployeeIds.isEmpty()) {
				conditionDTO.setEmployeeShortList(true);
			} else if (employeeShortListDTO.getEmployeeShortList() && employeeShortListEmployeeIds.size() == 0) {
				conditionDTO.setEmployeeShortList(true);
			} else {
				conditionDTO.setEmployeeShortList(false);
			}
		}

		employeeDAO.getEmployeeListByAdvanceFilterCount(conditionDTO, companyId);

		List<Employee> finalList = employeeDAO.getEmployeeListByAdvanceFilter(conditionDTO, pageDTO, sortDTO,
				companyId);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();
			String empNameStr = "";
			if (StringUtils.isNotBlank(employee.getFirstName())) {
				empNameStr += employee.getFirstName() + " ";
			}
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empNameStr += employee.getLastName();
			}
			employeeForm.setEmployeeName(empNameStr);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeForm);
		}

		WorkFlowDelegateResponse response = new WorkFlowDelegateResponse();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public PayslipResponse getPayslips(Long employeeId, Long companyId) {
		PayslipResponse payslipResponse = new PayslipResponse();
		List<PayslipDTO> payslips = new ArrayList<>();

		Employee employee = employeeDAO.findById(employeeId);

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);

		PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
		payslipConditionDTO.setCompanyId(companyId);
		payslipConditionDTO.setEmployeeId(employeeId);
		payslipConditionDTO.setYear(year);
		payslipConditionDTO.setMonthMasterId(Long.parseLong(String.valueOf(month + 1)));

		List<Payslip> payslipEVOs = payslipDAO.getPayslips(payslipConditionDTO);

		for (Payslip payslip : payslipEVOs) {

			CompanyPayslipRelease companyPayslipRelease = companyPayslipReleaseDAO.findByCondition(null,
					payslip.getMonthMaster().getMonthId(), payslip.getYear(), payslip.getPart(), companyId);

			if (companyPayslipRelease != null && !companyPayslipRelease.isReleased()) {
				continue;
			}

			PayslipDTO payslipDTO = new PayslipDTO();
			payslipDTO.setPayslipName(
					payslip.getMonthMaster().getMonthName() + "," + payslip.getYear() + " Part" + payslip.getPart());
			payslipDTO.setPayslipId(payslip.getPayslipId());
			payslipDTO.setPayslipType(PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_EXCEL);
			payslipDTO.setYear(payslip.getYear());
			payslipDTO.setMonthId(payslip.getMonthMaster().getMonthId());
			payslipDTO.setPart(payslip.getPart());

			payslips.add(payslipDTO);

		}

		payslipConditionDTO.setEmployeeNumber(employee.getEmployeeNumber() + "_%");
		List<CompanyDocument> payslipVOs = companyDocumentDAO.getPayslips(payslipConditionDTO);

		for (CompanyDocument companyDocument : payslipVOs) {
			PayslipDTO payslipDTO = new PayslipDTO();
			payslipDTO.setPayslipName(companyDocument.getMonth().getMonthName() + "," + companyDocument.getYear()
					+ " Part" + companyDocument.getPart());

			payslipDTO.setPayslipId(companyDocument.getDocumentId());
			payslipDTO.setPayslipType(companyDocument.getSource());
			payslipDTO.setYear(companyDocument.getYear());
			payslipDTO.setMonthId(companyDocument.getMonth().getMonthId());
			payslipDTO.setPart(companyDocument.getPart());
			payslips.add(payslipDTO);

		}
		Collections.sort(payslips, Collections.reverseOrder(new PayslipYearMonthComp()));

		payslipResponse.setPayslips(payslips);
		return payslipResponse;
	}

	@Override
	public Payslip getPayslip(Long payslipId) {
		return payslipDAO.findByID(payslipId);
	}

	@Override
	public byte[] getEmployeeTextPayslip(Long employeeId, Long companyId, Long payslipId) {

		EmployeePaySlipForm employeePaySlipForm = new EmployeePaySlipForm();
		CompanyDocument companyDocument = companyDocumentDAO.findByID(payslipId);
		employeePaySlipForm.setPayslipMonthId(companyDocument.getMonth().getMonthId());
		employeePaySlipForm.setPayslipPart(companyDocument.getPart());
		employeePaySlipForm.setPayslipYear(companyDocument.getYear());
		byte[] bytes = null;
		try {
			bytes = getPaySlipFromCompanyDocumentFolder(employeeId, companyId, employeePaySlipForm);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return bytes;

	}

	private List<Integer> getPaySlipStatusFromCompanyDocumentForPart(String employeeNumber, Long companyId, int year,
			Long monthId) {
		DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO
				.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
		List<Integer> companyDocumentVOList = companyDocumentDAO.findByConditionSourceTextAndDescWithoutPart(companyId,
				categoryMaster.getDocumentCategoryId(), year, monthId,
				PayAsiaConstants.COMPANY_DOCUMENT_UPLOAD_SOURCE_TEXT, employeeNumber);

		if (companyDocumentVOList != null) {
			return companyDocumentVOList;
		}
		return null;

	}

	@Override
	public PartsForm getParts(Integer year, Long month, Long companyId, Long employeeId) {
		Employee employee = employeeDAO.findById(employeeId);
		PartsForm partsForm = new PartsForm();
		PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
		payslipConditionDTO.setCompanyId(companyId);
		payslipConditionDTO.setYear(year);
		payslipConditionDTO.setMonthMasterId(month);
		payslipConditionDTO.setEmployeeId(employeeId);
		payslipConditionDTO.setEmployeeNumber(employee.getEmployeeNumber() + "%");

		List<PartDTO> partsList = new ArrayList<>();
		Set<Integer> partsSet = new HashSet<>();
		// Get Part For Text & PDF Payslip
		List<Integer> companyDocumentVOList = getPaySlipStatusFromCompanyDocumentForPart(employee.getEmployeeNumber(),
				companyId, year, month);
		if (companyDocumentVOList != null) {
			for (Integer companyDocumentPart : companyDocumentVOList) {
				partsSet.add(companyDocumentPart);
			}
		}

		// Get Part For Excel Payslip
		Set<Integer> partsVO = new HashSet<>(payslipDAO.getParts(payslipConditionDTO));
		for (Integer part : partsVO) {
			partsSet.add(part);
		}

		for (Integer part : partsSet) {
			PartDTO partDTO = new PartDTO();
			partDTO.setPart(part);
			partsList.add(partDTO);
		}

		partsForm.setParts(partsList);
		return partsForm;
	}

	@Override
	public String savePayslipEmail(byte[] byteFile, Long employeeId, Long companyId, Integer month, Integer year,
			Integer part) {
		UUID uuid = UUID.randomUUID();
		MonthMaster monthMaster = monthMasterDAO.findById(Long.parseLong(String.valueOf(month)));

		Employee employee = employeeDAO.findById(employeeId);
		String password = securityLogic.decrypt(employee.getEmployeeLoginDetail().getPassword(),
				employee.getEmployeeLoginDetail().getSalt());
		Company company = companyDAO.findById(companyId);
		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);

		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String USER_PASS = password;
		String OWNER_PASS = password;
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);

		String employeeName = employee.getFirstName();
		if (StringUtils.isNotBlank(employee.getMiddleName())) {
			employeeName += employee.getMiddleName();
		}
		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName += employee.getLastName();
		}

		StringBuilder body = new StringBuilder("");
		StringBuilder subject = new StringBuilder();
		subject.append(employee.getEmployeeNumber()).append("_").append(employeeName).append("_")
				.append("Payslip For Month Of ").append(monthMaster.getMonthAbbr().toUpperCase()).append(",")
				.append(year);
		StringBuilder fileName = new StringBuilder();
		fileName.append(employee.getEmployeeNumber()).append("_").append(employeeName).append("_")
				.append(monthMaster.getMonthAbbr().toUpperCase()).append("_").append(year).append(".pdf");
		String fromAddress = emailPreferenceMasterVO.getContactEmail();
		FileOutputStream fos = null;
		try {

			Email email = new Email();
			email.setBody(String.valueOf(body));
			Employee employeeVO = employeeDAO.findById(employeeId);
			email.setCompany(company);
			email.setSubject(String.valueOf(subject));
			email.setEmailFrom(fromAddress);
			email.setEmailTo(employeeVO.getEmail());
			email.setCreatedDate(DateUtils.getCurrentTimestampWithTime());

			email = emailDAO.saveReturn(email);

			EmailAttachment emailAttachment = new EmailAttachment();

			if (hrisPreferenceVO == null || hrisPreferenceVO.isPasswordProtect()) {
				fos = new FileOutputStream("C:\\payasia\\temp\\sourceTemp" + employeeId + uuid + ".pdf");
				fos.write(byteFile);
				fos.close();

				PdfReader reader = new PdfReader("C:\\payasia\\temp\\sourceTemp" + employeeId + uuid + ".pdf");
				PdfStamper stamper = new PdfStamper(reader,
						new FileOutputStream("C:\\payasia\\temp\\sourceTempDestination" + employeeId + uuid + ".pdf"));
				stamper.setEncryption(USER_PASS.getBytes(), OWNER_PASS.getBytes(), PdfWriter.ALLOW_PRINTING,
						PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
				stamper.close();
				reader.close();

				Path path = Paths.get("C:\\payasia\\temp\\sourceTempDestination" + employeeId + uuid + ".pdf");
				byte[] payslipFile = Files.readAllBytes(path);
				emailAttachment.setAttachment(payslipFile);

			} else {
				emailAttachment.setAttachment(byteFile);
			}
			emailAttachment.setEmail(email);
			emailAttachment.setFileName(String.valueOf(fileName));
			emailAttachmentDAO.saveReturn(emailAttachment);

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			FileUtils.deletefile("C:\\payasia\\temp\\sourceTemp" + employeeId + uuid + ".pdf");
			FileUtils.deletefile("C:\\payasia\\temp\\sourceTempDestination" + employeeId + uuid + ".pdf");
		}

		return "";

	}

	public class PayslipYearMonthComp implements Comparator<PayslipDTO> {

		public int compare(PayslipDTO one, PayslipDTO another) {

			Calendar payslipOneCal = new GregorianCalendar(one.getYear(), one.getMonthId().intValue(), one.getPart());
			Calendar payslipAnotherCal = new GregorianCalendar(another.getYear(), another.getMonthId().intValue(),
					another.getPart());
			return payslipOneCal.compareTo(payslipAnotherCal);

		}
	}

	@Override
	public PartsForm getPayslipPartDetailsForAdmin(String employeeNumber, Integer year, Long monthId, Long companyId,
			Long loogedInEmployeeId) {
		PartsForm partsForm = new PartsForm();
		List<PartDTO> partsList = new ArrayList<>();
		Set<Integer> partsSet = new HashSet<>();
		if (StringUtils.isNotBlank(employeeNumber)) {
			Employee employee = employeeDAO.findByNumber(employeeNumber, companyId);
			if (employee != null) {

				// Get Part For Text & PDF Payslip
				List<Integer> companyDocumentVOList = getPaySlipStatusFromCompanyDocumentForPart(
						employee.getEmployeeNumber(), companyId, year, monthId);
				if (companyDocumentVOList != null) {
					for (Integer part : companyDocumentVOList) {
						partsSet.add(part);
					}
				}
				else {
					partsForm
					.setVerifyResponse("payasia.employee.document.payslip.part");
				}

				// Get Part For Excel Payslip
				PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
				payslipConditionDTO.setCompanyId(companyId);
				payslipConditionDTO.setYear(year);
				payslipConditionDTO.setMonthMasterId(monthId);
				payslipConditionDTO.setEmployeeId(employee.getEmployeeId());
				payslipConditionDTO.setEmployeeNumber(employeeNumber + "%");
				Set<Integer> partsVO = new HashSet<>(payslipDAO.getParts(payslipConditionDTO));
				for (Integer part : partsVO) {
					partsSet.add(part);
				}

				for (Integer part : partsSet) {
					PartDTO partDTO = new PartDTO();
					partDTO.setPart(part);
					partsList.add(partDTO);
				}

			}
			
				if (employee == null) {
					partsForm.setParts(partsList);
					partsForm
							.setVerifyResponse("payasia.employee.not.found");
					return partsForm;
				}
			}
		
		partsForm.setParts(partsList);
		return partsForm;

	}

	@Override
	public Payslip getPayslipByEmpId(Long payslipId, Long employeeId) {
		return payslipDAO.fetchPayslipByEmpId(payslipId, employeeId);
	}

	
	// New
	
	@Override
	public EmployeePayslipResponse getPaySlipDetailsForEmployeePdfNew(Long employeeId,
			EmployeePaySlipForm employeePaySlipForm, Integer year, Long monthId, PageRequest pageDTO,
			SortCondition sortDTO) {

		List<PayslipDTO> listOfCompanyPayslip = new ArrayList<>();
		List<PayslipDTO> listOfAllPayslip = new ArrayList<>();

		Employee employee = employeeDAO.findById(employeeId);

		if (employee != null) {
			EmployeePayslipResponse employeePayslipResponse = new EmployeePayslipResponse();
			CompanyDocumentConditionDTO companyDocumentConditionDTO = new CompanyDocumentConditionDTO();
			DocumentCategoryMaster categoryMaster = documentCategoryMasterDAO.findByCondition(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP);
			Company company = employee.getCompany();
			companyDocumentConditionDTO.setYear(year);
			companyDocumentConditionDTO.setCategoryId(categoryMaster.getDocumentCategoryId());
			companyDocumentConditionDTO.setCategoryName(categoryMaster.getCategoryName());
			companyDocumentConditionDTO.setEmployeeNumber(employee.getEmployeeNumber());
			companyDocumentConditionDTO.setDescription(employee.getEmployeeNumber());
			Long companyID = company.getCompanyId();

			PayslipFrequency paySlipFrequency = company.getPayslipFrequency();
			employeePaySlipForm.setPaySlipFrequency(paySlipFrequency.getFrequency());
			PayslipConditionDTO payslipConditionDTO = new PayslipConditionDTO();
			payslipConditionDTO.setCompanyId(company.getCompanyId());
			payslipConditionDTO.setEmployeeId(employeeId);
			payslipConditionDTO.setMonthMasterId(employeePaySlipForm.getPayslipMonthId());
			payslipConditionDTO.setPayslipFrequencyId(paySlipFrequency.getPayslipFrequencyID());
			payslipConditionDTO.setYear(employeePaySlipForm.getPayslipYear());
			payslipConditionDTO.setPart(employeePaySlipForm.getPayslipPart());

			List<CompanyDocument> cmpDocList = companyDocumentDetailDAO.findByConditionPdf(companyDocumentConditionDTO, monthId, null, null, companyID);

			for (CompanyDocument companyDocumentDetail : cmpDocList) {
				PayslipDTO payslipDTO = new PayslipDTO();
				payslipDTO.setFullName(employee.getFirstName() + " " + employee.getLastName());
				payslipDTO.setMonthName(companyDocumentDetail.getMonth().getMonthName());
				payslipDTO.setMonthId(companyDocumentDetail.getMonth().getMonthId());
				payslipDTO.setAction(true);
				payslipDTO.setYear(companyDocumentDetail.getYear());
				payslipDTO.setPart(companyDocumentDetail.getPart());
				payslipDTO.setReleaseDate(DateUtils.timeStampToString(companyDocumentDetail.getUpdatedDate()) != null
						? DateUtils.timeStampToString(companyDocumentDetail.getUpdatedDate())
						: DateUtils.timeStampToString(companyDocumentDetail.getCreatedDate()));
				listOfAllPayslip.add(payslipDTO);
			}
			
			List<Payslip> payslipList = payslipDAO.getPayslipEmployee(payslipConditionDTO, null, null, UserContext.getWorkingCompanyId());
				
			for (Payslip obj : payslipList) {
				PayslipDTO payslipDTO = new PayslipDTO();
				payslipDTO.setFullName((obj.getEmployee().getFirstName()) + " " + obj.getEmployee().getLastName());
				payslipDTO.setMonthName(obj.getMonthMaster().getMonthName());
				payslipDTO.setMonthId(obj.getMonthMaster().getMonthId());
				payslipDTO.setAction(true);
				payslipDTO.setYear(obj.getYear());
				payslipDTO.setPart(obj.getPart());
				payslipDTO.setReleaseDate(DateUtils.timeStampToString(obj.getUpdatedDate()) != null
						? DateUtils.timeStampToString(obj.getUpdatedDate())
						: DateUtils.timeStampToString(obj.getCreatedDate()));
				listOfCompanyPayslip.add(payslipDTO);
			}

			listOfAllPayslip.addAll(listOfCompanyPayslip);
			
			/*	SORTING	*/
			if (sortDTO != null && !listOfAllPayslip.isEmpty()) {
				Collections.sort(listOfAllPayslip, new PayslipComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
				if(sortDTO.getColumnName().equalsIgnoreCase("part")){
					Collections.sort(listOfAllPayslip, new PayslipComparison("DESC", "monthId"));
				}
			  
			}
			PayslipDTO[] arrayPayslip = new PayslipDTO[listOfAllPayslip.size()];
			listOfAllPayslip.toArray(arrayPayslip);
			
			List<PayslipDTO> finalPayslip = new ArrayList<>();
			/*	PAGINATION	*/
			if (pageDTO != null && !listOfAllPayslip.isEmpty()) {
				
				int recordSizeFinal = listOfAllPayslip.size();
				int pageSize = pageDTO.getPageSize();
				int pageNo = pageDTO.getPageNumber();
				int startPos = (pageSize * (pageDTO.getPageNumber() - 1));
				
				if(startPos < recordSizeFinal){
					for(int i=startPos ; i<(startPos+pageSize) && i<arrayPayslip.length ; i++){
						finalPayslip.add(arrayPayslip[i]);
					}
				}
				
				int totalPages = recordSizeFinal / pageSize;
				if (recordSizeFinal % pageSize != 0) {
					totalPages = totalPages + 1;
				}
				if (recordSizeFinal == 0) {
					pageDTO.setPageNumber(0);
				}

				employeePayslipResponse.setPage(pageNo);
				employeePayslipResponse.setTotal(totalPages);
				employeePayslipResponse.setRecords(recordSizeFinal);
				employeePayslipResponse.setRows(finalPayslip);
			}
			return employeePayslipResponse;
		}
		return null;
	}

}
