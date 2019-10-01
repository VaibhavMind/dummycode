package com.payasia.logic;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.common.dto.PayslipDTO;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.EmployeePaySlipForm;
import com.payasia.common.form.EmployeePayslipResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PartsForm;
import com.payasia.common.form.PayslipResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateResponse;
import com.payasia.dao.bean.Payslip;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface EmployeePaySlipLogic.
 */
@Transactional
public interface EmployeePaySlipLogic {

	/**
	 * purpose : Get PaySlip Frequency Details.
	 * 
	 * @param Long
	 *            the employeeId
	 * @return EmployeePaySlipForm
	 */
	EmployeePaySlipForm getPaySlipFrequencyDetails(Long employeeId);

	/**
	 * purpose : Get PaySlip Details.
	 * 
	 * @param EmployeePaySlipForm
	 *            the employeePaySlipForm
	 * @param Long
	 *            the employeeId
	 * @return Payslip Entity Bean
	 */
	Payslip getPaySlipDetails(Long employeeId,
			EmployeePaySlipForm employeePaySlipForm);

	/**
	 * purpose : Generate pay slip pdf.
	 * 
	 * @param companyId
	 *            the company id
	 * @param payslip
	 *            the payslip
	 * @return Payslip with byte[]
	 * @throws SAXException
	 * @throws JAXBException
	 */
	byte[] generatePdf(Long companyId, Long employeeId, Payslip payslip)
			throws DocumentException, IOException, JAXBException, SAXException;

	/**
	 * purpose : Get Year List For Payslip.
	 * 
	 * @param Long
	 *            the employeeId
	 * @return YearList
	 */
	List<Integer> getYearList(Long employeeId);

	/**
	 * purpose : Get PaySlip Details.
	 * 
	 * @param employeeId
	 * 
	 * @param AdminPaySlipForm
	 *            the adminPaySlipForm
	 * @param Long
	 *            the companyId
	 * @return Payslip Entity Bean
	 */
	Payslip getPaySlipDetails(AdminPaySlipForm adminPaySlipForm,
			Long companyId, Long employeeId);

	String checkForEffectiveFrom(Long companyId, Payslip payslip);

	/**
	 * purpose : Get PaySlip Release Details.
	 * 
	 * @param employeeId
	 * 
	 * @param EmployeePaySlipForm
	 *            the employeePaySlipForm
	 * @param Long
	 *            the companyId
	 * @return Payslip Release status
	 */

	boolean getPaySlipReleaseDetails(Long employeeId, Long companyId,
			EmployeePaySlipForm employeePaySlipForm);

	boolean getPaySlipStatusFromCompanyDocument(Long employeeId,
			Long companyId, EmployeePaySlipForm employeePaySlipForm);

	boolean getPaySlipStatusFromCompanyDocumentForAdmin(Long employeeId,
			Long companyId, AdminPaySlipForm adminPaySlipForm);

	boolean getPaySlipStatusFromCompanyDocumentForPrint(Long employeeId,
			Long companyId, int year, int part, Long month);

	byte[] getPaySlipFromCompanyDocumentFolderForAdmin(Long employeeId,
			Long companyId, AdminPaySlipForm adminPaySlipForm)
			throws IOException;

	byte[] getPaySlipFromCompanyDocumentFolderForPrint(Long employeeId,
			Long companyId, int year, int part, Long month) throws IOException;

	byte[] getPaySlipFromCompanyDocumentFolder(Long employeeId, Long companyId,
			EmployeePaySlipForm employeePaySlipForm) throws IOException;

	Payslip getPaySlipDetailsForPrint(Long companyId, Long sessionEmployeeId,
			Long reqEmployeeId, Integer year, Long month, Integer part);

	List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId);

	WorkFlowDelegateResponse searchEmployeeListForPrint(PageRequest pageDTO,
			SortCondition sortDTO, Long sessionEmployeeId, String empName,
			String empNumber, Long companyId, boolean isShortList,
			String metaData);

	PayslipResponse getPayslips(Long employeeId, Long companyId);

	Payslip getPayslip(Long payslipId);

	byte[] getEmployeeTextPayslip(Long employeeId, Long companyId,
			Long payslipId);

	PartsForm getParts(Integer year, Long month, Long companyId, Long employeeId);

	String savePayslipEmail(byte[] byteFile, Long empId, Long companyId,
			Integer month, Integer year, Integer part);

	PartsForm getPayslipPartDetailsForAdmin(String employeeNumber,
			Integer year, Long monthId, Long companyId, Long loogedInEmployeeId);
	
	 Payslip getPayslipByEmpId(Long payslipId,Long employeeId);

	List<PayslipDTO> getPaySlipDetailsForEmployee(Long employeeId, EmployeePaySlipForm employeePaySlipForm);

	EmployeePayslipResponse getPaySlipDetailsForEmployee(Long employeeId, EmployeePaySlipForm employeePaySlipForm,
			String searchCondition, String searchText, PageRequest pageDTO, SortCondition sortDTO);

	EmployeePayslipResponse getPaySlipDetailsForEmployeePdfNew(Long employeeId, EmployeePaySlipForm employeePaySlipForm,
			Integer year, Long monthId, PageRequest pageDTO, SortCondition sortDTO);

}
