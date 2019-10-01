package com.payasia.api.hris.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.hris.EmployeePayslipApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.EmployeePaySlipForm;
import com.payasia.common.form.EmployeePayslipResponse;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.EmployeePaySlipLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.logic.PaySlipDynamicFormLogic;

import net.sf.json.JSONObject;

/**
 * @author gauravkumar
 * @param :This class used for Payslip APIs
 * 
 */

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS , produces={"application/json"}, consumes={"application/json"})
public class EmployeePayslipApiImpl implements EmployeePayslipApi {

	@Resource
	private EmployeePaySlipLogic employeePaySlipLogic;

	@Resource
	private MultilingualLogic multilingualLogic;

	@Resource
	private PaySlipDynamicFormLogic paySlipDynamicFormLogic;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;

	@Override
	@PostMapping(value = "payslip-data")
	public ResponseEntity<?> getPaySlipList(@RequestBody SearchParam searchParamObj) {
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());

		EmployeePaySlipForm employeePaySlipForm = new EmployeePaySlipForm();
		EmployeePayslipResponse employeePayslipResponse = null;
		Integer year = null;
		Long monthId = null;
		
		Map<String, Object> dataStatus = new HashMap<>();
		String noData = "No Data";
		dataStatus.put("info", noData);

		if (filterllist.size() > 1) {
			Long monthid = Long.valueOf(filterllist.get(1).getValue());
			employeePaySlipForm.setPayslipMonthId(monthid);
		}

		if (filterllist != null && !filterllist.isEmpty()) {
			if(filterllist.get(0).getValue()!=null && !filterllist.get(0).getValue().isEmpty()){
				year = Integer.valueOf(filterllist.get(0).getValue());
			}
			else{
				Calendar now = Calendar.getInstance();
				year = now.get(Calendar.YEAR);
				filterllist.get(0).setValue(year.toString());
			}
		} else {
			Calendar now = Calendar.getInstance();
			year = now.get(Calendar.YEAR);
		}

		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}

		employeePaySlipForm.setPayslipYear(year);
		boolean isPayslipReleased = employeePaySlipLogic.getPaySlipReleaseDetails(employeeId, companyId,
				employeePaySlipForm);
		if (!isPayslipReleased) {

			return new ResponseEntity<>(dataStatus, HttpStatus.OK);
		}

		employeePaySlipForm.setPayslipPart(1);

		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic.getPaySlipStatusFromCompanyDocument(employeeId,
				companyId, employeePaySlipForm);

//		if (filterllist.get(0).getField().equalsIgnoreCase("year")) {
//			year = Integer.valueOf(filterllist.get(0).getValue());
//		}
		if (filterllist.size() > 1) {
			if (filterllist.get(1).getField().equalsIgnoreCase("monthId")) {
				monthId = Long.valueOf(filterllist.get(1).getValue());
			}
		}
		if (isPayslipExistinCompanyDocFolder) {
			employeePayslipResponse = employeePaySlipLogic.getPaySlipDetailsForEmployeePdfNew(employeeId,
					employeePaySlipForm, year, monthId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
		} else {
			employeePayslipResponse = employeePaySlipLogic.getPaySlipDetailsForEmployee(employeeId, employeePaySlipForm,
					filterllist.get(0).getField(), filterllist.get(0).getValue(), searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
		}
		if (employeePayslipResponse != null) {
			return new ResponseEntity<>(employeePayslipResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = { "preview-payslip", "download-payslip" })
	public ResponseEntity<?> dogetPaySlip(@RequestBody EmployeePaySlipForm employeePaySlipForm, Locale locale)
			throws DocumentException, IOException, JAXBException, SAXException {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		
		UserContext.setLocale(locale);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
		UserContext.setLanguageId(languageId);

		byte[] byteFile = null;
		Payslip payslip = null;
		String pdfName = "";
		Map<String, Object> payDataMap = new HashMap<>();

		boolean isPayslipExistinCompanyDocFolder = employeePaySlipLogic.getPaySlipStatusFromCompanyDocument(employeeId,
				companyId, employeePaySlipForm);
		if (isPayslipExistinCompanyDocFolder) {
			byteFile = employeePaySlipLogic.getPaySlipFromCompanyDocumentFolder(employeeId, companyId,
					employeePaySlipForm);
			String ext = "pdf";
			String fileName = employeeId + "_payslip_" + employeePaySlipForm.getPayslipYear()
					+ employeePaySlipForm.getPayslipMonthId() + employeePaySlipForm.getPayslipPart() + "." + ext;
			pdfName = fileName;

		} else {
			payslip = employeePaySlipLogic.getPaySlipDetails(employeeId, employeePaySlipForm);
			
			if(payslip==null){
				return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
			}
			byteFile = employeePaySlipLogic.generatePdf(companyId, employeeId, payslip);
			pdfName = createPayslipFileName(payslip);
		}
		payDataMap.put("payslipbody", byteFile);
		payDataMap.put("pdfname", pdfName);
		return new ResponseEntity<>(payDataMap, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "monthlist")
	public ResponseEntity<?> getMonthList() {
		List<MonthMasterDTO> monthList = paySlipDynamicFormLogic.getMonthList();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("monthData", monthList);
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	private String createPayslipFileName(Payslip payslip) {
		String payslipName = payslip.getEmployee().getEmployeeNumber() + "_payslip_" + payslip.getYear();
		if (Long.toString(payslip.getMonthMaster().getMonthId()).length() == 1) {
			payslipName = payslipName + "0" + payslip.getMonthMaster().getMonthId() + payslip.getPart() + ".pdf";
		} else {
			payslipName = payslipName + payslip.getMonthMaster().getMonthId() + payslip.getPart() + ".pdf";
		}
		return payslipName;
	}

}
