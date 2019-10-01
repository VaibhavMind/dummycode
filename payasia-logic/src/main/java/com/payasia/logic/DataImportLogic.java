/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EntityMasterDTO;
import com.payasia.common.dto.PayslipDTO;
import com.payasia.common.dto.PayslipFrequencyDTO;
import com.payasia.common.dto.TextPaySlipListDTO;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.dao.bean.EmpDataImportTemplateField;

/**
 * The Interface DataImportLogic.
 */
@Transactional
public interface DataImportLogic {

	/**
	 * Purpose: To perform import via file upload.
	 * 
	 * @param dataImportForm
	 *            the data import form
	 * @param companyId
	 *            the company id
	 * @param readData
	 *            the read data
	 * @return the data import form
	 */
	DataImportForm importFile(DataImportForm dataImportForm, Long companyId,
			DataImportForm readData);

	/**
	 * Purpose: To show the import template list.
	 * 
	 * @param entityId
	 *            the entity id
	 * @param companyId
	 *            the company id
	 * @return the template list
	 */
	List<ExcelImportToolForm> getTemplateList(long entityId, Long companyId);

	/**
	 * Purpose: To show the previous import attempts history.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the import history
	 */
	DataImportForm getImportHistory(Long companyId);

	/**
	 * Purpose: To get the entity list.
	 * 
	 * @return the entity list
	 */
	List<EntityMasterDTO> getEntityList();

	/**
	 * Purpose: To get the company payslip frequency details.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the payslip frequency
	 */
	List<PayslipFrequencyDTO> getPayslipFrequency(Long companyId);

	/**
	 * Purpose: To get the payslip frequency list.
	 * 
	 * @return the payslip frequency list
	 */
	List<PayslipFrequencyDTO> getPayslipFrequencyList();

	/**
	 * Gets the partfor company.
	 * 
	 * @param companyId
	 *            the company id
	 * @return the partfor company
	 */
	Integer getPartforCompany(Long companyId);

	String getPayslipFormatforCompany(Long companyId);

	byte[] getPaySlipFromCompanyDocumentFolder(Long companyId,
			String previewPdfFilePath) throws IOException;

	String generateSamplePaySlipPDFForPreview(
			TextPaySlipListDTO textPaySlipListDTO,
			DataImportForm dataImportForm, Long companyId);

	DataImportLogDTO generateTextPaySlipPDF(
			TextPaySlipListDTO textPaySlipListDTO,
			DataImportForm dataImportForm, Long companyId);

	void uploadPaySlipPDF(DataImportForm dataImportForm,
			List<String> zipFileNamesCopy, Long companyId);

	DataImportLogDTO uploadPDFZipFileForPayslips(CommonsMultipartFile fileData,
			String filePath, String fileType, Long companyId,
			List<CompanyDocumentLogDTO> documentLogs,
			DataImportForm dataImportForm);

	String getEntityName(Long entityId);

	List<EmployeeFilterListForm> getCompanyFilterList(Long companyId,
			Long employeeId);

	String configureCompanyAddress(long companyId, String[] dataDictionaryIds);

	List<EmployeeFilterListForm> getCompanyAddressMappingList(Long companyId);

	String getCompanyAddress(long companyId);

	/**
	 * Save Release payslip notification email and send email by schedular.
	 * 
	 * @param companyId
	 *            the company id
	 * @param PayslipDTO
	 *            the PayslipDTO List
	 */
	void savePayslipReleaseEmail(Long companyId, List<PayslipDTO> payslipDTOList);

	DataImportForm createUpdateEmpDataWS(Long companyId, HashMap<String, EmpDataImportTemplateField> colMap,
			List<HashMap<String, String>> importedData, String uploadType, String transactionType);

}
