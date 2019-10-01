package com.payasia.logic.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PayCodeDataForm;
import com.payasia.common.form.PayCodeDataFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.PayCodeDAO;
import com.payasia.dao.PayDataCollectionDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.PayDataCollection;
import com.payasia.dao.bean.Paycode;
import com.payasia.logic.PayCodeDataLogic;

/**
 * The Class PayCodeDataLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class PayCodeDataLogicImpl implements PayCodeDataLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PayCodeDataLogicImpl.class);

	/** The pay data collection dao. */
	@Resource
	PayDataCollectionDAO payDataCollectionDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The pay code dao. */
	@Resource
	PayCodeDAO payCodeDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PayCodeDataLogic#getPayCodedetails(java.lang.Long,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public PayCodeDataFormResponse getPayCodedetails(Long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {
		List<PayCodeDataForm> PayCodeDataFormList = new ArrayList<PayCodeDataForm>();
		List<Paycode> PaycodeList = payCodeDAO.getAllPayCodeByConditionCompany(
				companyId, pageDTO, sortDTO);

		for (Paycode paycode : PaycodeList) {
			PayCodeDataForm payCodeDataForm = new PayCodeDataForm();
			payCodeDataForm.setPayCodeId(paycode.getPaycodeID());
			payCodeDataForm.setPayCode(paycode.getPaycode());
			PayCodeDataFormList.add(payCodeDataForm);
		}

		PayCodeDataFormResponse response = new PayCodeDataFormResponse();
		int recordSize = payCodeDAO.getCountForAllPayCode(companyId);
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
		response.setPayCodeDataFormList(PayCodeDataFormList);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PayCodeDataLogic#addPayCode(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public void addPayCode(Long companyId, String payCode) {
		Paycode paycode = new Paycode();
		paycode.setPaycode(payCode);
		Company company = companyDAO.findById(companyId);
		paycode.setCompany(company);
		payCodeDAO.save(paycode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PayCodeDataLogic#getPayCodeData(long)
	 */
	@Override
	public PayCodeDataForm getPayCodeData(long payCodeId) {

		Paycode paycode = payCodeDAO.findByID(payCodeId);

		PayCodeDataForm payCodeDataForm = new PayCodeDataForm();

		payCodeDataForm.setPayCodeId(paycode.getPaycodeID());
		payCodeDataForm.setPayCode(paycode.getPaycode());
		return payCodeDataForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PayCodeDataLogic#updatePaycodeData(com.payasia.common
	 * .form.PayCodeDataForm, java.lang.Long)
	 */
	@Override
	public void updatePaycodeData(PayCodeDataForm payCodeDataForm,
			Long companyId) {
		Paycode paycode = new Paycode();

		Company company = companyDAO.findById(companyId);
		paycode.setCompany(company);

		paycode.setPaycodeID(payCodeDataForm.getPayCodeId());
		paycode.setPaycode(payCodeDataForm.getPayCode());
		payCodeDAO.update(paycode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.PayCodeDataLogic#deletePaycode(java.lang.Long,
	 * long)
	 */
	@Override
	public String deletePaycode(Long companyId, long payCodeId) {
		List<PayDataCollection> payDataCollectionList = payDataCollectionDAO
				.findByPayCode(companyId, payCodeId);
		if ((payDataCollectionList.size() == 0)) {
			Paycode paycode = payCodeDAO.findByID(payCodeId);
			payCodeDAO.delete(paycode);
			return "deleted.successfully";
		} else {
			return "please.delete.the.users.assigned.to.this.payCode";
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.PayCodeDataLogic#getPayCodeFromXL(com.payasia.common
	 * .form.PayCodeDataForm, java.lang.Long)
	 */
	@Override
	public void getPayCodeFromXL(PayCodeDataForm payCodeDataForm, Long companyId) {
		POIFSFileSystem fileSystem = null;
		try {
			String fileName = payCodeDataForm.getFileUpload()
					.getOriginalFilename();

			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
					fileName.length());
			if ("xls".equals(fileExt.toLowerCase())) {
				fileSystem = new POIFSFileSystem(payCodeDataForm
						.getFileUpload().getInputStream());
				HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
				HSSFSheet sheet = workBook.getSheetAt(0);
				Iterator rows = sheet.rowIterator();

				while (rows.hasNext()) {
					HSSFRow row = (HSSFRow) rows.next();

					Iterator cells = row.cellIterator();

					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();
						cell.setCellType(Cell.CELL_TYPE_STRING);
						HSSFRichTextString richTextString = cell
								.getRichStringCellValue();

						Paycode paycode = new Paycode();
						paycode.setPaycode(richTextString.getString());
						Company company = companyDAO.findById(companyId);
						paycode.setCompany(company);
						payCodeDAO.save(paycode);
					}
				}
			}

			else if ("xlsx".equals(fileExt.toLowerCase())) {
				XSSFWorkbook workBook = new XSSFWorkbook(payCodeDataForm
						.getFileUpload().getInputStream());
				XSSFSheet sheet = workBook.getSheetAt(0);
				Iterator rows = sheet.rowIterator();

				while (rows.hasNext()) {
					XSSFRow row = (XSSFRow) rows.next();

					Iterator cells = row.cellIterator();

					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						cell.setCellType(Cell.CELL_TYPE_STRING);
						XSSFRichTextString richTextString = cell
								.getRichStringCellValue();

						Paycode paycode = new Paycode();
						paycode.setPaycode(richTextString.getString());
						Company company = companyDAO.findById(companyId);
						paycode.setCompany(company);
						payCodeDAO.save(paycode);
					}
				}
			}

		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

	}
}
