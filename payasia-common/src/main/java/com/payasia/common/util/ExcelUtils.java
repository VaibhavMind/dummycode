/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.common.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.AssignClaimTemplateDTO;
import com.payasia.common.dto.AssignLeaveSchemeDTO;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.HolidayListMasterDTO;
import com.payasia.common.dto.ImportItemEntitlementTemplateDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ClaimReviewerForm;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.HRISReviewerForm;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.common.form.LeaveReviewerForm;
import com.payasia.common.form.LundinTimesheetReviewerForm;


/**
 * The Class ExcelUtils.
 */
public class ExcelUtils {

	private static final Logger LOGGER = Logger.getLogger(ExcelUtils.class);

	/**
	 * Purpose: Read every cell in a xls.
	 * 
	 * @param xlsFile
	 *            the xls file
	 * @return the list
	 */
	public static List<String> readXLS(CommonsMultipartFile xlsFile) {
		List<String> xlsContent = new ArrayList<String>();
		try {
			POIFSFileSystem fileSystem = new POIFSFileSystem(
					xlsFile.getInputStream());
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();

			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();

				Iterator<Cell> cells = row.cellIterator();

				while (cells.hasNext()) {
					HSSFCell cell = (HSSFCell) cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					HSSFRichTextString richTextString = cell
							.getRichStringCellValue();
					if (!richTextString.getString().trim().equalsIgnoreCase("")) {
						xlsContent.add(URLEncoder.encode(richTextString.getString(),"UTF-8"));
					}

				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return xlsContent;
	}

	/**
	 * Purpose: To get code and description list from a xls.
	 * 
	 * @param xlsFile
	 *            the xls file
	 * @return the list
	 */
	public static List<CodeDescDTO> readCodeDescXLS(CommonsMultipartFile xlsFile) {
		List<CodeDescDTO> xlsContent = new ArrayList<CodeDescDTO>();
		try {
			POIFSFileSystem fileSystem = new POIFSFileSystem(
					xlsFile.getInputStream());
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();

			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();

				Iterator<Cell> cells = row.cellIterator();
				int cellNo = 1;
				CodeDescDTO codeDescDTO = new CodeDescDTO();
				while (cells.hasNext()) {
					HSSFCell cell = (HSSFCell) cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					HSSFRichTextString richTextString = cell
							.getRichStringCellValue();
					if (cellNo == 1) {
						codeDescDTO.setCode(URLEncoder.encode(richTextString.getString(),"UTF-8"));
						cellNo = 2;
					} else if (cellNo == 2) {
						codeDescDTO.setDescription(URLEncoder.encode(richTextString.getString(),"UTF-8"));
						cellNo = 3;
					}
				}
				xlsContent.add(codeDescDTO);

			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return xlsContent;
	}

	/**
	 * Purpose: Read every cell in a xlsx.
	 * 
	 * @param xlsxFile
	 *            the xlsx file
	 * @return the list
	 */
	public static List<String> readXLSX(CommonsMultipartFile xlsxFile) {
		List<String> xlsContent = new ArrayList<String>();
		try {
			XSSFWorkbook workBook = new XSSFWorkbook(xlsxFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();

			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();

				Iterator<Cell> cells = row.cellIterator();

				while (cells.hasNext()) {
					XSSFCell cell = (XSSFCell) cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					XSSFRichTextString richTextString = cell
							.getRichStringCellValue();
					if (!richTextString.getString().trim().equalsIgnoreCase("")) {
						xlsContent.add(URLEncoder.encode(richTextString.getString(),"UTF-8"));
					}
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return xlsContent;
	}

	/**
	 * Purpose: To get code and description list from a xlsx.
	 * 
	 * @param xlsxFile
	 *            the xlsx file
	 * @return the list
	 */
	public static List<CodeDescDTO> readCodeDescXLSX(
			CommonsMultipartFile xlsxFile) {
		List<CodeDescDTO> xlsContent = new ArrayList<CodeDescDTO>();
		try {
			XSSFWorkbook workBook = new XSSFWorkbook(xlsxFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();

			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();

				Iterator<Cell> cells = row.cellIterator();
				int cellNo = 1;
				CodeDescDTO codeDescDTO = new CodeDescDTO();
				while (cells.hasNext()) {
					XSSFCell cell = (XSSFCell) cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					XSSFRichTextString richTextString = cell
							.getRichStringCellValue();

					if (cellNo == 1) {
						codeDescDTO.setCode(URLEncoder.encode(richTextString.getString(),"UTF-8"));
						cellNo = 2;
					} else if (cellNo == 2) {
						codeDescDTO.setDescription(URLEncoder.encode(richTextString.getString(),"UTF-8"));
						cellNo = 3;
					}
				}
				xlsContent.add(codeDescDTO);
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return xlsContent;
	}

	/**
	 * Purpose : To Read data for Data Import from Excel file with xls ext.
	 * 
	 * @param attachedFile
	 *            the attached file
	 * @return the imported data
	 */
	public static DataImportForm getImportedData(
			CommonsMultipartFile attachedFile) {
		POIFSFileSystem fileSystem;
		DataImportForm importForm = new DataImportForm();
		try {
			fileSystem = new POIFSFileSystem(attachedFile.getInputStream());

			List<String> colName = new ArrayList<String>();
			List<String> duplicateColName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();

						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: {

							
							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}
							

							break;
						}

						case HSSFCell.CELL_TYPE_STRING: {

							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						HSSFCell cell = (HSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case HSSFCell.CELL_TYPE_STRING: {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case HSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}

	/**
	 * Purpose : To Read data for Data Import from Excel file with xlsx ext.
	 * 
	 * @param attachedFile
	 *            the attached file
	 * @return the imported data
	 */
	public static DataImportForm getImportedDataForXLSX(
			CommonsMultipartFile attachedFile) {
		DataImportForm importForm = new DataImportForm();
		try {

			List<String> colName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<String> duplicateColName = new ArrayList<String>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();

						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_NUMERIC: {

							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}

							break;
						}

						case XSSFCell.CELL_TYPE_STRING: {

							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();

							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						XSSFCell cell = (XSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case XSSFCell.CELL_TYPE_STRING: {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case XSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}

	public static List<HashMap<String, String>> getDateCodesFromXLS(
			CommonsMultipartFile attachedFile) {
		List<HashMap<String, String>> datesCodeMaps = new ArrayList<>();
		try {
			POIFSFileSystem fileSystem = new POIFSFileSystem(
					attachedFile.getInputStream());
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					int cellNo = 0;
					HashMap<String, String> dateCodeMap = new HashMap<>();
					dateCodeMap.put("rowNumber",
							String.valueOf(row.getRowNum()));
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();
						cell.setCellType(Cell.CELL_TYPE_STRING);
						HSSFRichTextString richTextString = cell
								.getRichStringCellValue();
						if (cellNo == 0) {
							dateCodeMap.put("Month",
									String.valueOf(richTextString.toString()));
						} else {
							dateCodeMap.put(String.valueOf(cellNo),
									String.valueOf(richTextString.toString()));
						}
						cellNo++;
					}

					datesCodeMaps.add(dateCodeMap);
				} else {
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return datesCodeMaps;
	}

	public static List<HashMap<String, String>> getDateCodesFromXLSX(
			CommonsMultipartFile attachedFile) {
		List<HashMap<String, String>> datesCodeMaps = new ArrayList<>();
		try {
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					int cellNo = 0;
					HashMap<String, String> dateCodeMap = new HashMap<>();
					dateCodeMap.put("rowNumber",
							String.valueOf(row.getRowNum()));
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						cell.setCellType(Cell.CELL_TYPE_STRING);
						XSSFRichTextString richTextString = cell
								.getRichStringCellValue();
						if (cellNo == 0) {
							dateCodeMap.put("Month",
									String.valueOf(richTextString.toString()));
						} else {
							dateCodeMap.put(String.valueOf(cellNo),
									String.valueOf(richTextString.toString()));
						}
						cellNo++;
					}

					datesCodeMaps.add(dateCodeMap);
				} else {
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return datesCodeMaps;
	}
	
	public static List<HashMap<String, AssignLeaveSchemeDTO>> getAssignLeaveSchemeFromXLS(
			CommonsMultipartFile attachedFile,String dateFormat) {
		List<HashMap<String, AssignLeaveSchemeDTO>> empLeaveSchemeMapsList = new ArrayList<>();
		try {
			POIFSFileSystem fileSystem = new POIFSFileSystem(
					attachedFile.getInputStream());
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			int cellNo = 2;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HashMap<String, AssignLeaveSchemeDTO> empLeaveSchemeMap = new HashMap<>();
				
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					AssignLeaveSchemeDTO assignLeaveSchemeDTO = new AssignLeaveSchemeDTO();
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();
						
						if (cell.getColumnIndex() == 0) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								assignLeaveSchemeDTO.setEmployeeNumber(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignLeaveSchemeDTO.setEmployeeNumber(richTextString.getString());
							}
						}
						if (cell.getColumnIndex() == 1) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								assignLeaveSchemeDTO.setLeaveSchemeName(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignLeaveSchemeDTO.setLeaveSchemeName(richTextString.getString());
							}
							
						}
						
						if ((cell.getColumnIndex() == 2)) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignLeaveSchemeDTO.setFromDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignLeaveSchemeDTO.setFromDateString("");
								}
							}
						}if ((cell.getColumnIndex() == 3)) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignLeaveSchemeDTO.setToDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignLeaveSchemeDTO.setToDateString("");
								}
							}
						}
						
					}
					empLeaveSchemeMap.put(String.valueOf(cellNo), assignLeaveSchemeDTO);
					cellNo++;
					empLeaveSchemeMapsList.add(empLeaveSchemeMap);
				} else {
					AssignLeaveSchemeDTO assignLeaveSchemeDTO = new AssignLeaveSchemeDTO();
					empLeaveSchemeMap.put("Employee_Number",
							assignLeaveSchemeDTO);
					empLeaveSchemeMapsList.add(empLeaveSchemeMap);
					empLeaveSchemeMap.put("Leave scheme Name",
							assignLeaveSchemeDTO);
					empLeaveSchemeMapsList.add(empLeaveSchemeMap);
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return empLeaveSchemeMapsList;
	}
	
	public static List<HashMap<String, AssignLeaveSchemeDTO>> getAssignLeaveSchemeFromXLSX(
			CommonsMultipartFile attachedFile,String dateFormat) {
		List<HashMap<String, AssignLeaveSchemeDTO>> empLeaveSchemeMapsList = new ArrayList<>();
		try {
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			int cellNo = 2;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, AssignLeaveSchemeDTO> empLeaveSchemeMap = new HashMap<>();
				
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					AssignLeaveSchemeDTO assignLeaveSchemeDTO = new AssignLeaveSchemeDTO();
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						if (cell.getColumnIndex() == 0) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignLeaveSchemeDTO.setEmployeeNumber(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignLeaveSchemeDTO.setEmployeeNumber(richTextString.getString());
							}
							 
						}
						if (cell.getColumnIndex() == 1) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignLeaveSchemeDTO.setLeaveSchemeName(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignLeaveSchemeDTO.setLeaveSchemeName(richTextString.getString());
							}
							
						}
						
						if ((cell.getColumnIndex() == 2)) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignLeaveSchemeDTO.setFromDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignLeaveSchemeDTO.setFromDateString("");
								}
							}
						}if ((cell.getColumnIndex() == 3)) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignLeaveSchemeDTO.setToDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignLeaveSchemeDTO.setToDateString("");
								}
							}
						}
						
					}
					empLeaveSchemeMap.put(String.valueOf(cellNo), assignLeaveSchemeDTO);
					cellNo++;
					empLeaveSchemeMapsList.add(empLeaveSchemeMap);
				} else {
					AssignLeaveSchemeDTO assignLeaveSchemeDTO = new AssignLeaveSchemeDTO();
					empLeaveSchemeMap.put("Employee_Number",
							assignLeaveSchemeDTO);
					empLeaveSchemeMapsList.add(empLeaveSchemeMap);
					empLeaveSchemeMap.put("Leave scheme Name",
							assignLeaveSchemeDTO);
					empLeaveSchemeMapsList.add(empLeaveSchemeMap);
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return empLeaveSchemeMapsList;
	}
	
	
	
	
	public static LeaveBalanceSummaryForm getPostLeaveTranFromXLS(
			CommonsMultipartFile attachedFile) {
		POIFSFileSystem fileSystem;
		LeaveBalanceSummaryForm importForm = new LeaveBalanceSummaryForm();
		try {
			fileSystem = new POIFSFileSystem(attachedFile.getInputStream());

			List<String> colName = new ArrayList<String>();
			List<String> duplicateColName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();

						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: {

							
							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}
							

							break;
						}

						case HSSFCell.CELL_TYPE_STRING: {

							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						HSSFCell cell = (HSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case HSSFCell.CELL_TYPE_STRING: {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case HSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}
	
	
	public static LeaveBalanceSummaryForm getPostLeaveTranFromXLSX(
			CommonsMultipartFile attachedFile) {
		LeaveBalanceSummaryForm importForm = new LeaveBalanceSummaryForm();
		try {

			List<String> colName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<String> duplicateColName = new ArrayList<String>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();

						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_NUMERIC: {

							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}

							break;
						}

						case XSSFCell.CELL_TYPE_STRING: {

							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();

							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						XSSFCell cell = (XSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case XSSFCell.CELL_TYPE_STRING: {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case XSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}
	
	
	public static List<HashMap<String, HolidayListMasterDTO>> getHolidayListMasterFromXLS(
			CommonsMultipartFile attachedFile,String dateFormat) {
		List<HashMap<String, HolidayListMasterDTO>> holidayListMasterMapsList = new ArrayList<>();
		try {
			POIFSFileSystem fileSystem = new POIFSFileSystem(
					attachedFile.getInputStream());
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			int cellNo = 2;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HashMap<String, HolidayListMasterDTO> holidayListMasterMap = new HashMap<>();
				
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					HolidayListMasterDTO holidayListMasterDTO = new HolidayListMasterDTO();
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();
						
						if (cell.getColumnIndex() == 0) {
							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							holidayListMasterDTO.setCountry(richTextString.getString());
						}
						if (cell.getColumnIndex() == 1) {
							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							holidayListMasterDTO.setState(richTextString
									.getString());
							
						}if ((cell.getColumnIndex() == 2)) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									holidayListMasterDTO.setHolidayDateString(sdf.format(cell.getDateCellValue()));
								}else{
									holidayListMasterDTO.setHolidayDateString("");
								}
							}
						}if (cell.getColumnIndex() == 3) {
							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							holidayListMasterDTO.setOccasion(richTextString.getString());

						}
						
					}
					holidayListMasterMap.put(String.valueOf(cellNo), holidayListMasterDTO);
					cellNo++;
					holidayListMasterMapsList.add(holidayListMasterMap);
				} else {
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return holidayListMasterMapsList;
	}
	
	public static List<HashMap<String, HolidayListMasterDTO>> getHolidayListMasterFromXLSX(
			CommonsMultipartFile attachedFile,String dateFormat) {
		List<HashMap<String, HolidayListMasterDTO>> holidayListMasterMapsList = new ArrayList<>();
		try {
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			int cellNo = 2;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, HolidayListMasterDTO> holidayListMasterMap = new HashMap<>();
				
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					HolidayListMasterDTO holidayListMasterDTO = new HolidayListMasterDTO();
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						
						if (cell.getColumnIndex() == 0) {
							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							holidayListMasterDTO.setCountry(richTextString.getString());
						}
						if (cell.getColumnIndex() == 1) {
							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							holidayListMasterDTO.setState(richTextString
									.getString());
							
						}if ((cell.getColumnIndex() == 2)) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									holidayListMasterDTO.setHolidayDateString(sdf.format(cell.getDateCellValue()));
								}else{
									holidayListMasterDTO.setHolidayDateString("");
								}
								
							}
						}if (cell.getColumnIndex() == 3) {
							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							holidayListMasterDTO.setOccasion(richTextString.getString());

						}
						
					}
					holidayListMasterMap.put(String.valueOf(cellNo), holidayListMasterDTO);
					cellNo++;
					holidayListMasterMapsList.add(holidayListMasterMap);
				} else {
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return holidayListMasterMapsList;
	}
	
	
	public static List<HashMap<String, AssignClaimTemplateDTO>> getAssignClaimTemplateFromXLS(
			CommonsMultipartFile attachedFile,String dateFormat) {
		List<HashMap<String, AssignClaimTemplateDTO>> empClaimTemplateMapsList = new ArrayList<>();
		try {
			POIFSFileSystem fileSystem = new POIFSFileSystem(
					attachedFile.getInputStream());
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			int cellNo = 2;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HashMap<String, AssignClaimTemplateDTO> empClaimTemplateMap = new HashMap<>();
				
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					AssignClaimTemplateDTO assignClaimTemplateDTO = new AssignClaimTemplateDTO();
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();
						
						
						
						if (cell.getColumnIndex() == 0) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setEmployeeNumber(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setEmployeeNumber(richTextString.getString());
							}
						}
						if (cell.getColumnIndex() == 1) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setClaimTemplateName(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else{
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setClaimTemplateName(richTextString.getString());
							}
							
						}
						if ((cell.getColumnIndex() == 2)) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignClaimTemplateDTO.setFromDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignClaimTemplateDTO.setFromDateString("");
								}
							}
						}if ((cell.getColumnIndex() == 3)) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignClaimTemplateDTO.setToDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignClaimTemplateDTO.setToDateString("");
								}
							}
						}
						
					}
					empClaimTemplateMap.put(String.valueOf(cellNo), assignClaimTemplateDTO);
					cellNo++;
					empClaimTemplateMapsList.add(empClaimTemplateMap);
				} else {
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return empClaimTemplateMapsList;
	}
	
	public static List<HashMap<String, AssignClaimTemplateDTO>> getAssignClaimTemplateFromXLSX(
			CommonsMultipartFile attachedFile,String dateFormat) {
		List<HashMap<String, AssignClaimTemplateDTO>> empClaimTemplateMapsList = new ArrayList<>();
		try {
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			int cellNo = 2;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, AssignClaimTemplateDTO> empClaimTemplateMap = new HashMap<>();
				
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					AssignClaimTemplateDTO assignClaimTemplateDTO = new AssignClaimTemplateDTO();
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						
						
						if (cell.getColumnIndex() == 0) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setEmployeeNumber(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setEmployeeNumber(richTextString.getString());
							}
							 
						}
						if (cell.getColumnIndex() == 1) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setClaimTemplateName(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else{
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setClaimTemplateName(richTextString.getString());
							}
							
						}
						if ((cell.getColumnIndex() == 2)) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignClaimTemplateDTO.setFromDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignClaimTemplateDTO.setFromDateString("");
								}
							}
						}if ((cell.getColumnIndex() == 3)) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignClaimTemplateDTO.setToDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignClaimTemplateDTO.setToDateString("");
								}
							}
						}
						
					}
					empClaimTemplateMap.put(String.valueOf(cellNo), assignClaimTemplateDTO);
					cellNo++;
					empClaimTemplateMapsList.add(empClaimTemplateMap);
				} else {
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return empClaimTemplateMapsList;
	}

	public static List<ImportEmployeeClaimDTO> getEmployeeClaimFromXLS(
			CommonsMultipartFile fileUpload, String dateFormat,List<DataImportLogDTO> dataImportLogDTOs) {
		List<ImportEmployeeClaimDTO> employeeClaimDTOS = new ArrayList<>();
		POIFSFileSystem fileSystem;
		DataImportForm importForm = new DataImportForm();
		try {
			fileSystem = new POIFSFileSystem(fileUpload.getInputStream());

			List<String> colName = new ArrayList<String>(); 
			List<String> duplicateColName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			List<HashMap<String, String>> employeeClaimMaps = new ArrayList<>();
			boolean firstRow = true;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next(); 
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();

						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: {

							
							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}
							

							break;
						}

						case HSSFCell.CELL_TYPE_STRING: {

							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					 
					int count = 0;
					for (Integer colNumber : colNumbers) {
						HSSFCell cell = (HSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case HSSFCell.CELL_TYPE_STRING: {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case HSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}
					
					employeeClaimMaps.add(hashMap);
					

					
				}
			}
			
			 
			
			for (HashMap<String, String> employeeClaimMap : employeeClaimMaps) {
				ImportEmployeeClaimDTO importEmployeeClaimDTO = new ImportEmployeeClaimDTO();
				setEmployeeClaimDTO(importEmployeeClaimDTO,employeeClaimMap,dataImportLogDTOs);
				employeeClaimDTOS.add(importEmployeeClaimDTO);
				
			}
			
			
		} catch (IOException ioException) {
			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			LOGGER.error(ioException.getMessage(),ioException);
			dataImportLogDTO
			.setErrorKey("payasia.employee.claim.import.invalid.import.template");
			dataImportLogDTO.setErrorValue("");
			dataImportLogDTO
					.setRemarks(ioException.getMessage());
			dataImportLogDTO.setRowNumber(1);
			dataImportLogDTOs.add(dataImportLogDTO);
			throw new PayAsiaRollBackDataException(dataImportLogDTOs);	
		}
		return employeeClaimDTOS;
	}

	
	private static void setEmployeeClaimDTO(
			ImportEmployeeClaimDTO importEmployeeClaimDTO,
			HashMap<String, String> employeeClaimMap,List<DataImportLogDTO> dataImportLogDTOs) {
		try {
		for (Entry<String, String> claimMap : employeeClaimMap.entrySet()) {
			
			String methodName = getSetterMethodName(claimMap.getKey());
			Method importEmployeeClaimDTOMtd = null;
			
			if(claimMap.getKey().equals(PayAsiaConstants.EMPLOYEE_CLAIM_IMPORT_CLAIM_DATE)){
				importEmployeeClaimDTOMtd = ImportEmployeeClaimDTO.class.getMethod(methodName, Date.class);
			}else if(claimMap.getKey().equals(PayAsiaConstants.EMPLOYEE_CLAIM_IMPORT_APPROVED_DATE)){
				importEmployeeClaimDTOMtd = ImportEmployeeClaimDTO.class.getMethod(methodName, Date.class);
			}else{
				importEmployeeClaimDTOMtd = ImportEmployeeClaimDTO.class.getMethod(methodName, String.class);
			}
				
				try {
					
					if(claimMap.getKey().equals(PayAsiaConstants.EMPLOYEE_CLAIM_IMPORT_CLAIM_DATE)){
						importEmployeeClaimDTOMtd.invoke(importEmployeeClaimDTO, DateUtils.stringToDate(claimMap.getValue(),PayAsiaConstants.EMPLOYEE_CLAIM_IMPORT_DATE_FORMAT));
					}else if(claimMap.getKey().equals(PayAsiaConstants.EMPLOYEE_CLAIM_IMPORT_APPROVED_DATE)){
						importEmployeeClaimDTOMtd.invoke(importEmployeeClaimDTO, DateUtils.stringToDate(claimMap.getValue(),PayAsiaConstants.EMPLOYEE_CLAIM_IMPORT_DATE_FORMAT));
					}else{
						importEmployeeClaimDTOMtd.invoke(importEmployeeClaimDTO, claimMap.getValue());
					}
					
					
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
					
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					LOGGER.error(exception.getMessage(),exception);
					dataImportLogDTO
							.setColName(claimMap.getKey());
					dataImportLogDTO
					.setErrorKey("payasia.employee.claim.import.not.valid.value");
					dataImportLogDTO.setErrorValue("");
					dataImportLogDTO.setRowNumber(1);
					dataImportLogDTOs.add(dataImportLogDTO);
					throw new PayAsiaRollBackDataException(dataImportLogDTOs);
				} 
			
			
		}
		} catch (NoSuchMethodException |SecurityException   exception) {
			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			LOGGER.error(exception.getMessage(),exception);
			
			dataImportLogDTO
					.setRemarks(exception.getMessage());
			dataImportLogDTO
			.setErrorKey("payasia.employee.claim.import.not.valid.value");
	dataImportLogDTO.setErrorValue("");
			dataImportLogDTOs.add(dataImportLogDTO);
			throw new PayAsiaRollBackDataException(dataImportLogDTOs);		} 

		
	}

	private static String getSetterMethodName(String prop) {
		prop ="set"+ prop.replaceAll("\\s+", "");
		return prop;
	}

	public static List<ImportEmployeeClaimDTO> getEmployeeClaimFromXLSX(
			CommonsMultipartFile fileUpload, String dateFormat,List<DataImportLogDTO> dataImportLogDTOs) {
		List<ImportEmployeeClaimDTO> employeeClaimDTOS = new ArrayList<>();
		 
		DataImportForm importForm = new DataImportForm();
		try {
			 

			List<String> colName = new ArrayList<String>(); 
			List<String> duplicateColName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			XSSFWorkbook workBook = new XSSFWorkbook(
					fileUpload.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			List<HashMap<String, String>> employeeClaimMaps = new ArrayList<>();
			boolean firstRow = true;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();

						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_NUMERIC: {

							
							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}
							

							break;
						}

						case XSSFCell.CELL_TYPE_STRING: {

							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					 
					int count = 0;
					for (Integer colNumber : colNumbers) {
						XSSFCell cell = (XSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case XSSFCell.CELL_TYPE_STRING: {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case XSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}
					
					employeeClaimMaps.add(hashMap);
					

					
				}
			}
			
			 
			
			for (HashMap<String, String> employeeClaimMap : employeeClaimMaps) {
				ImportEmployeeClaimDTO importEmployeeClaimDTO = new ImportEmployeeClaimDTO();
				setEmployeeClaimDTO(importEmployeeClaimDTO,employeeClaimMap,dataImportLogDTOs);
				employeeClaimDTOS.add(importEmployeeClaimDTO);
				
			}
			
			
		} catch (IOException ioException) {
			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			LOGGER.error(ioException.getMessage(),ioException);
			dataImportLogDTO
			.setErrorKey("payasia.employee.claim.import.employee.not.found");
			dataImportLogDTO.setErrorValue("");
			dataImportLogDTO
					.setRemarks(ioException.getMessage());
			dataImportLogDTO.setRowNumber(1);
			dataImportLogDTOs.add(dataImportLogDTO);
			throw new PayAsiaRollBackDataException(dataImportLogDTOs);	
		}
		return employeeClaimDTOS;
	}
	
	public static String getSheetSafeName(String sheetName) {
		String invalidCharsRegex = "\\W";
		String sheetSafeName = null;
		Pattern invalidCharsRegexPattern = Pattern.compile(invalidCharsRegex);
		sheetSafeName =  invalidCharsRegexPattern.matcher(sheetName).replaceAll(" ");
		if (sheetSafeName.length() > PayAsiaConstants.EXCEL_SHEET_NAME_LENGTH_LIMIT) {
			return sheetSafeName
							.substring(
									0,
									PayAsiaConstants.EXCEL_SHEET_NAME_LENGTH_LIMIT - 1);
		} else {
			return sheetSafeName;
		}
		
	}
	
//	Leave Reviewer
	public static LeaveReviewerForm getLeaveReviewersFromXLS(
			CommonsMultipartFile attachedFile) {
		POIFSFileSystem fileSystem;
		LeaveReviewerForm importForm = new LeaveReviewerForm();
		try {
			fileSystem = new POIFSFileSystem(attachedFile.getInputStream());

			List<String> colName = new ArrayList<String>();
			List<String> duplicateColName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();

						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: {

							
							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}
							

							break;
						}

						case HSSFCell.CELL_TYPE_STRING: {

							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						HSSFCell cell = (HSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case HSSFCell.CELL_TYPE_STRING: {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case HSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}
	
	
	public static LeaveReviewerForm getLeaveReviewersFromXLSX(
			CommonsMultipartFile attachedFile) {
		LeaveReviewerForm importForm = new LeaveReviewerForm();
		try {

			List<String> colName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<String> duplicateColName = new ArrayList<String>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();

						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_NUMERIC: {

							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}

							break;
						}

						case XSSFCell.CELL_TYPE_STRING: {

							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();

							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						XSSFCell cell = (XSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case XSSFCell.CELL_TYPE_STRING: {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case XSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}
	
//	Claim Reviewer
	public static ClaimReviewerForm getClaimReviewersFromXLS(
			CommonsMultipartFile attachedFile) {
		POIFSFileSystem fileSystem;
		ClaimReviewerForm importForm = new ClaimReviewerForm();
		try {
			fileSystem = new POIFSFileSystem(attachedFile.getInputStream());

			List<String> colName = new ArrayList<String>();
			List<String> duplicateColName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();

						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: {

							
							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}
							

							break;
						}

						case HSSFCell.CELL_TYPE_STRING: {

							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						HSSFCell cell = (HSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case HSSFCell.CELL_TYPE_STRING: {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case HSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}
	
	
	public static ClaimReviewerForm getClaimReviewersFromXLSX(
			CommonsMultipartFile attachedFile) {
		ClaimReviewerForm importForm = new ClaimReviewerForm();
		try {

			List<String> colName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<String> duplicateColName = new ArrayList<String>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();

						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_NUMERIC: {

							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}

							break;
						}

						case XSSFCell.CELL_TYPE_STRING: {

							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();

							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						XSSFCell cell = (XSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case XSSFCell.CELL_TYPE_STRING: {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case XSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}
	
//	Claim Reviewer
	public static HRISReviewerForm getHRISReviewersFromXLS(
			CommonsMultipartFile attachedFile) {
		POIFSFileSystem fileSystem;
		HRISReviewerForm importForm = new HRISReviewerForm();
		try {
			fileSystem = new POIFSFileSystem(attachedFile.getInputStream());

			List<String> colName = new ArrayList<String>();
			List<String> duplicateColName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();

						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: {

							
							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}
							

							break;
						}

						case HSSFCell.CELL_TYPE_STRING: {

							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						HSSFCell cell = (HSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case HSSFCell.CELL_TYPE_STRING: {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case HSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}
	
	
	public static HRISReviewerForm getHRISReviewersFromXLSX(
			CommonsMultipartFile attachedFile) {
		HRISReviewerForm importForm = new HRISReviewerForm();
		try {

			List<String> colName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			List<String> duplicateColName = new ArrayList<String>();
			List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean firstRow = true;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (firstRow) {
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();

						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_NUMERIC: {

							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
								importForm.setSameExcelField(true);
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}

							break;
						}

						case XSSFCell.CELL_TYPE_STRING: {

							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();

							if(colName.contains(richTextString.getString())){
								duplicateColName.add(richTextString.getString());
								importForm.setSameExcelField(true);
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							break;
						}

						default: {

							break;
						}
						}
					}
					firstRow = false;

				} else {
					hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						XSSFCell cell = (XSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {

								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

								hashMap.put(colName.get(count),
										sdf.format(cell.getDateCellValue()));
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_BOOLEAN: {
								hashMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case XSSFCell.CELL_TYPE_STRING: {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								hashMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case XSSFCell.CELL_TYPE_BLANK: {
								hashMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							hashMap.put(colName.get(count), "");
						}

						count++;
					}

					importedData.add(hashMap);
				}
			}
			importForm.setDuplicateColNames(duplicateColName);
			importForm.setColName(colName);
			importForm.setImportedData(importedData);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return importForm;
	}
	
			
//			Lundin OT Reviewer
			public static LundinTimesheetReviewerForm getLundinReviewersFromXLS(
					CommonsMultipartFile attachedFile) {
				POIFSFileSystem fileSystem;
				LundinTimesheetReviewerForm importForm = new LundinTimesheetReviewerForm();
				try {
					fileSystem = new POIFSFileSystem(attachedFile.getInputStream());

					List<String> colName = new ArrayList<String>();
					List<String> duplicateColName = new ArrayList<String>();
					List<Integer> colNumbers = new ArrayList<Integer>();
					List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
					HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
					HSSFSheet sheet = workBook.getSheetAt(0);
					Iterator<Row> rows = sheet.rowIterator();
					boolean firstRow = true;
					while (rows.hasNext()) {
						HSSFRow row = (HSSFRow) rows.next();
						HashMap<String, String> hashMap = new HashMap<String, String>();
						Iterator<Cell> cells = row.cellIterator();

						if (firstRow) {
							while (cells.hasNext()) {
								HSSFCell cell = (HSSFCell) cells.next();

								switch (cell.getCellType()) {
								case HSSFCell.CELL_TYPE_NUMERIC: {

									
									if(colName.contains(Double.toString(cell
											.getNumericCellValue()))){
										duplicateColName.add(Double.toString(cell
											.getNumericCellValue()));
										importForm.setSameExcelField(true);
									} else {
										colNumbers.add(cell.getColumnIndex());
										colName.add(Double.toString(cell
												.getNumericCellValue()));
									}
									

									break;
								}

								case HSSFCell.CELL_TYPE_STRING: {

									HSSFRichTextString richTextString = cell
											.getRichStringCellValue();
									if(colName.contains(richTextString.getString())){
										duplicateColName.add(richTextString.getString());
										importForm.setSameExcelField(true);
									} else {
										colName.add(richTextString.getString());
										colNumbers.add(cell.getColumnIndex());
									}
									
									break;
								}

								default: {

									break;
								}
								}
							}
							firstRow = false;

						} else {
							hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
							int count = 0;
							for (Integer colNumber : colNumbers) {
								HSSFCell cell = (HSSFCell) row.getCell(colNumber);
								if (cell != null) {

									if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
											&& DateUtil.isCellDateFormatted(cell)) {

										SimpleDateFormat sdf = new SimpleDateFormat(
												PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

										hashMap.put(colName.get(count),
												sdf.format(cell.getDateCellValue()));
									} else {
										cell.setCellType(Cell.CELL_TYPE_STRING);
									}

									switch (cell.getCellType()) {
									case HSSFCell.CELL_TYPE_BOOLEAN: {
										hashMap.put(colName.get(count), Boolean
												.toString(cell.getBooleanCellValue()));
										break;
									}
									case HSSFCell.CELL_TYPE_STRING: {
										HSSFRichTextString richTextString = cell
												.getRichStringCellValue();
										hashMap.put(colName.get(count),
												richTextString.getString());
										break;
									}
									case HSSFCell.CELL_TYPE_BLANK: {
										hashMap.put(colName.get(count), "");
										break;
									}
									default: {
										break;
									}
									}
								} else {
									hashMap.put(colName.get(count), "");
								}

								count++;
							}

							importedData.add(hashMap);
						}
					}
					importForm.setDuplicateColNames(duplicateColName);
					importForm.setColName(colName);
					importForm.setImportedData(importedData);
				} catch (IOException ioException) {
					LOGGER.error(ioException.getMessage(), ioException);
					throw new PayAsiaSystemException(ioException.getMessage(),
							ioException);
				}
				return importForm;
			}
			
			
			public static LundinTimesheetReviewerForm getLundinReviewersFromXLSX(
					CommonsMultipartFile attachedFile) {
				LundinTimesheetReviewerForm importForm = new LundinTimesheetReviewerForm();
				try {

					List<String> colName = new ArrayList<String>();
					List<Integer> colNumbers = new ArrayList<Integer>();
					List<String> duplicateColName = new ArrayList<String>();
					List<HashMap<String, String>> importedData = new ArrayList<HashMap<String, String>>();
					XSSFWorkbook workBook = new XSSFWorkbook(
							attachedFile.getInputStream());
					XSSFSheet sheet = workBook.getSheetAt(0);
					Iterator<Row> rows = sheet.rowIterator();
					boolean firstRow = true;
					while (rows.hasNext()) {
						XSSFRow row = (XSSFRow) rows.next();
						HashMap<String, String> hashMap = new HashMap<String, String>();
						Iterator<Cell> cells = row.cellIterator();

						if (firstRow) {
							while (cells.hasNext()) {
								XSSFCell cell = (XSSFCell) cells.next();

								switch (cell.getCellType()) {
								case XSSFCell.CELL_TYPE_NUMERIC: {

									if(colName.contains(Double.toString(cell
											.getNumericCellValue()))){
										duplicateColName.add(Double.toString(cell
											.getNumericCellValue()));
										importForm.setSameExcelField(true);
									} else {
										colNumbers.add(cell.getColumnIndex());
										colName.add(Double.toString(cell
												.getNumericCellValue()));
									}

									break;
								}

								case XSSFCell.CELL_TYPE_STRING: {

									XSSFRichTextString richTextString = cell
											.getRichStringCellValue();

									if(colName.contains(richTextString.getString())){
										duplicateColName.add(richTextString.getString());
										importForm.setSameExcelField(true);
									} else {
										colName.add(richTextString.getString());
										colNumbers.add(cell.getColumnIndex());
									}
									break;
								}

								default: {

									break;
								}
								}
							}
							firstRow = false;

						} else {
							hashMap.put("rowNumber", Integer.toString(row.getRowNum()));
							int count = 0;
							for (Integer colNumber : colNumbers) {
								XSSFCell cell = (XSSFCell) row.getCell(colNumber);
								if (cell != null) {

									if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
											&& DateUtil.isCellDateFormatted(cell)) {

										SimpleDateFormat sdf = new SimpleDateFormat(
												PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);

										hashMap.put(colName.get(count),
												sdf.format(cell.getDateCellValue()));
									} else {
										cell.setCellType(Cell.CELL_TYPE_STRING);
									}

									switch (cell.getCellType()) {
									case XSSFCell.CELL_TYPE_BOOLEAN: {
										hashMap.put(colName.get(count), Boolean
												.toString(cell.getBooleanCellValue()));
										break;
									}
									case XSSFCell.CELL_TYPE_STRING: {
										XSSFRichTextString richTextString = cell
												.getRichStringCellValue();
										hashMap.put(colName.get(count),
												richTextString.getString());
										break;
									}
									case XSSFCell.CELL_TYPE_BLANK: {
										hashMap.put(colName.get(count), "");
										break;
									}
									default: {
										break;
									}
									}
								} else {
									hashMap.put(colName.get(count), "");
								}

								count++;
							}

							importedData.add(hashMap);
						}
					}
					importForm.setDuplicateColNames(duplicateColName);
					importForm.setColName(colName);
					importForm.setImportedData(importedData);
				} catch (IOException ioException) {
					LOGGER.error(ioException.getMessage(), ioException);
					throw new PayAsiaSystemException(ioException.getMessage(),
							ioException);
				}
				return importForm;
			}
//			coherent
	public static ImportEmployeeOvertimeShiftForm getCoherentOvertimeShiftFromXLSX(
			CommonsMultipartFile fileUpload, 
			List<DataImportLogDTO> dataImportLogDTOs) {
		ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShift = new ImportEmployeeOvertimeShiftForm();

		try {
			List<String> colName = new ArrayList<String>();
			List<String> duplicateColName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			XSSFWorkbook workBook = new XSSFWorkbook(
					fileUpload.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			List<HashMap<String, String>> timesheetMapsList = new ArrayList<>();
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, String> timesheetMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (row.getRowNum()<=3) {
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();

						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_NUMERIC: {
							if (colName.contains(Double.toString(cell
									.getNumericCellValue()))) {
								importEmployeeOvertimeShift.setSameExcelField(true);
								duplicateColName.add(Double.toString(cell
										.getNumericCellValue()));
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}
							break;
						}

						case XSSFCell.CELL_TYPE_STRING: {
							XSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							if (colName.contains(richTextString.getString())) {
								importEmployeeOvertimeShift.setSameExcelField(true);
								duplicateColName
										.add(richTextString.getString());
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							break;
						}

						default: {
							break;
						}
						}
					}

				} else {
					timesheetMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						XSSFCell cell = (XSSFCell) row.getCell(colNumber);
						if (cell != null) {

							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

								if (formatTime.format(cell.getDateCellValue()).equals("00:00:00")){
									timesheetMap.put(colName.get(count),
											sdf.format(cell.getDateCellValue()));
								}else{
									timesheetMap.put(colName.get(count),
											formatTime.format(cell.getDateCellValue()));
								}
								
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_BOOLEAN: {
								timesheetMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case XSSFCell.CELL_TYPE_STRING: {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								timesheetMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case XSSFCell.CELL_TYPE_BLANK: {
								timesheetMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							timesheetMap.put(colName.get(count), "");
						}

						count++;
					}
					timesheetMapsList.add(timesheetMap);
				}
			}
			importEmployeeOvertimeShift.setDuplicateColNames(duplicateColName);
			importEmployeeOvertimeShift.setColName(colName);
			importEmployeeOvertimeShift.setImportedData(timesheetMapsList);
		} catch (IOException ioException) {
			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			LOGGER.error(ioException.getMessage(), ioException);
			dataImportLogDTO
					.setErrorKey("payasia.employee.claim.import.employee.not.found");
			dataImportLogDTO.setErrorValue("");
			dataImportLogDTO.setRemarks(ioException.getMessage());
			dataImportLogDTO.setRowNumber(1);
			dataImportLogDTOs.add(dataImportLogDTO);
			throw new PayAsiaRollBackDataException(dataImportLogDTOs);
		}
		return importEmployeeOvertimeShift;
	}
	public static ImportEmployeeOvertimeShiftForm getCoherentOvertimeShiftFromXLS(
			CommonsMultipartFile fileUpload, List<DataImportLogDTO> dataImportLogDTOs) {
		ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShift = new ImportEmployeeOvertimeShiftForm();
		POIFSFileSystem fileSystem;
		try {
			fileSystem = new POIFSFileSystem(fileUpload.getInputStream());

			List<String> colName = new ArrayList<String>(); 
			List<String> duplicateColName = new ArrayList<String>();
			List<Integer> colNumbers = new ArrayList<Integer>();
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			List<HashMap<String, String>> timesheetMapList = new ArrayList<>();
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next(); 
				HashMap<String, String> timesheetMap = new HashMap<String, String>();
				Iterator<Cell> cells = row.cellIterator();

				if (row.getRowNum()<=3) {
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();

						switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC: {
							if(colName.contains(Double.toString(cell
									.getNumericCellValue()))){
								importEmployeeOvertimeShift.setSameExcelField(true);
								duplicateColName.add(Double.toString(cell
									.getNumericCellValue()));
							} else {
								colNumbers.add(cell.getColumnIndex());
								colName.add(Double.toString(cell
										.getNumericCellValue()));
							}
							break;
						}

						case HSSFCell.CELL_TYPE_STRING: {
							HSSFRichTextString richTextString = cell
									.getRichStringCellValue();
							if(colName.contains(richTextString.getString())){
								importEmployeeOvertimeShift.setSameExcelField(true);
								duplicateColName.add(richTextString.getString());
							} else {
								colName.add(richTextString.getString());
								colNumbers.add(cell.getColumnIndex());
							}
							break;
						}

						default: {

							break;
						}
						}
					}

				} else {
					timesheetMap.put("rowNumber", Integer.toString(row.getRowNum()));
					int count = 0;
					for (Integer colNumber : colNumbers) {
						HSSFCell cell = (HSSFCell) row.getCell(colNumber);
						if (cell != null) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								SimpleDateFormat sdf = new SimpleDateFormat(
										PayAsiaConstants.SERVER_SIDE_DATE_FORMAT);
								SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

								if (formatTime.format(cell.getDateCellValue()).equals("00:00:00")){
									timesheetMap.put(colName.get(count),
											sdf.format(cell.getDateCellValue()));
								}else{
									timesheetMap.put(colName.get(count),
											formatTime.format(cell.getDateCellValue()));
								}
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_BOOLEAN: {
								timesheetMap.put(colName.get(count), Boolean
										.toString(cell.getBooleanCellValue()));
								break;
							}
							case HSSFCell.CELL_TYPE_STRING: {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								timesheetMap.put(colName.get(count),
										richTextString.getString());
								break;
							}
							case HSSFCell.CELL_TYPE_BLANK: {
								timesheetMap.put(colName.get(count), "");
								break;
							}
							default: {
								break;
							}
							}
						} else {
							timesheetMap.put(colName.get(count), "");
						}
						count++;
					}
					timesheetMapList.add(timesheetMap);
				}
			}
			importEmployeeOvertimeShift.setDuplicateColNames(duplicateColName);
			importEmployeeOvertimeShift.setColName(colName);
			importEmployeeOvertimeShift.setImportedData(timesheetMapList);
		} catch (IOException ioException) {
			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			LOGGER.error(ioException.getMessage(),ioException);
			dataImportLogDTO
			.setErrorKey("payasia.employee.claim.import.invalid.import.template");
			dataImportLogDTO.setErrorValue("");
			dataImportLogDTO
					.setRemarks(ioException.getMessage());
			dataImportLogDTO.setRowNumber(1);
			dataImportLogDTOs.add(dataImportLogDTO);
			throw new PayAsiaRollBackDataException(dataImportLogDTOs);	
		}
		return importEmployeeOvertimeShift;
	}
	
	public static List<HashMap<String, ImportItemEntitlementTemplateDTO>> getimportItemEntitlementTemplateFromXLS(
			CommonsMultipartFile attachedFile,String dateFormat) {
		List<HashMap<String, ImportItemEntitlementTemplateDTO>> empClaimTemplateMapsList = new ArrayList<>();
		try {
			POIFSFileSystem fileSystem = new POIFSFileSystem(
					attachedFile.getInputStream());
			HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
			HSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			int cellNo = 2;
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HashMap<String, ImportItemEntitlementTemplateDTO> empClaimTemplateMap = new HashMap<>();
				
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					ImportItemEntitlementTemplateDTO assignClaimTemplateDTO = new ImportItemEntitlementTemplateDTO();
					while (cells.hasNext()) {
						HSSFCell cell = (HSSFCell) cells.next();
						
						
						
						if (cell.getColumnIndex() == 0) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setEmployeeNumber(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else {
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setEmployeeNumber(richTextString.getString());
							}
						}
						if (cell.getColumnIndex() == 1) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setClaimTemplateName(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else{
								HSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setClaimTemplateName(richTextString.getString());
							}
							
						}
						if ((cell.getColumnIndex() == 2)) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignClaimTemplateDTO.setFromDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignClaimTemplateDTO.setFromDateString("");
								}
							}
						}if ((cell.getColumnIndex() == 3)) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignClaimTemplateDTO.setToDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignClaimTemplateDTO.setToDateString("");
								}
							}
						}
						
					}
					empClaimTemplateMap.put(String.valueOf(cellNo), assignClaimTemplateDTO);
					cellNo++;
					empClaimTemplateMapsList.add(empClaimTemplateMap);
				} else {
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return empClaimTemplateMapsList;
	}
	
	public static List<HashMap<String, ImportItemEntitlementTemplateDTO>> getimportItemEntitlementTemplateFromXLSX(
			CommonsMultipartFile attachedFile,String dateFormat) {
		List<HashMap<String, ImportItemEntitlementTemplateDTO>> empClaimTemplateMapsList = new ArrayList<>();
		try {
			XSSFWorkbook workBook = new XSSFWorkbook(
					attachedFile.getInputStream());
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			boolean isFirstRow = true;
			int cellNo = 2;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				HashMap<String, ImportItemEntitlementTemplateDTO> empClaimTemplateMap = new HashMap<>();
				
				if (!isFirstRow) {

					Iterator<Cell> cells = row.cellIterator();
					ImportItemEntitlementTemplateDTO assignClaimTemplateDTO = new ImportItemEntitlementTemplateDTO();
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						
						
						if (cell.getColumnIndex() == 0) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setEmployeeNumber(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else {
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setEmployeeNumber(richTextString.getString());
							}
							 
						}
						if (cell.getColumnIndex() == 1) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setClaimTemplateName(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else{
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setClaimTemplateName(richTextString.getString());
							}
							
						}if (cell.getColumnIndex() == 2) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setClaimTemplateitemName(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else{
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setClaimTemplateitemName(richTextString.getString());
							}
							
						}
						if ((cell.getColumnIndex() == 3)) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignClaimTemplateDTO.setFromDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignClaimTemplateDTO.setFromDateString("");
								}
							}
						}if ((cell.getColumnIndex() == 4)) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
									&& DateUtil.isCellDateFormatted(cell)) {
								if(cell.getDateCellValue()!=null){
									SimpleDateFormat sdf = new SimpleDateFormat(
											dateFormat);
									assignClaimTemplateDTO.setToDateString(sdf.format(cell.getDateCellValue()));
								}else{
									assignClaimTemplateDTO.setToDateString("");
								}
							}
						}if (cell.getColumnIndex() == 5) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
								assignClaimTemplateDTO.setForfeitAtEndDate(cell.getBooleanCellValue());
							} else{
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setForfeitAtEndDate(Boolean.valueOf(richTextString.getString()));
							}
							
						}if (cell.getColumnIndex() == 6) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setAmount(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else{
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setAmount(richTextString.getString());
							}
							
						}if (cell.getColumnIndex() == 7) {
							if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								assignClaimTemplateDTO.setReason(String.valueOf(Math.round(cell.getNumericCellValue())));
							} else{
								XSSFRichTextString richTextString = cell
										.getRichStringCellValue();
								assignClaimTemplateDTO.setReason(richTextString.getString());
							}
							
						}
						
					}
					empClaimTemplateMap.put(String.valueOf(cellNo), assignClaimTemplateDTO);
					cellNo++;
					empClaimTemplateMapsList.add(empClaimTemplateMap);
				} else {
					isFirstRow = false;
				}
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		}
		return empClaimTemplateMapsList;
	}
}
