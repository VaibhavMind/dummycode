package com.payasia.api.admin.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.admin.EmployeeDocumentApi;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.EmployeeTaxDocumentForm;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.EmployeeDocumentLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author anujsaxena
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_ADMIN + "/tax-document")
public class EmployeeDocumentApiImpl implements EmployeeDocumentApi {
	
	@Resource
	private EmployeeDocumentLogic employeeDocumentLogic;

	@Autowired
	private MessageSource messageSource;
	
	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Autowired
	@Qualifier("AWSS3Logic")
	private AWSS3Logic awss3LogicImpl;

	@Override
	@PostMapping("years")
	public ResponseEntity<?> showYearList() {
		Long companyId = UserContext.getClientAdminId();
		List<Integer> yearList = employeeDocumentLogic.getYearList(companyId);
		return new ResponseEntity<>(yearList, HttpStatus.OK);
	}

	@Override
	@PostMapping("emp-number")
	public ResponseEntity<?> getEmployeeId(@RequestParam(value = "searchString") String searchString) {
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<AdminPaySlipForm> adminPaySlipFormFormList = employeeDocumentLogic.getEmployeeId(companyId, searchString,employeeId);
		if (adminPaySlipFormFormList.isEmpty()) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.employee.document.tax", new Object[] {}, UserContext.getLocale())),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(adminPaySlipFormFormList, HttpStatus.OK);
	}
	

	@Override
	@PostMapping("tax-documents")
	public ResponseEntity<?> getTaxDocumentList(@RequestBody String documnetDataStr) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(documnetDataStr, jsonConfig);
		String employeeNumber=jsonObj.getString("employeeNumber").trim();
		int year = jsonObj.getInt("year");
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		EmployeeTaxDocumentForm employeeTaxDocumentForm = employeeDocumentLogic.getTaxDocumentList(employeeNumber, year,companyId, employeeId);
		
		if (!employeeTaxDocumentForm.isDocumentExist()) {
			String responseStr = messageSource.getMessage(employeeTaxDocumentForm.getVerifyResponse(), new Object[] {}, UserContext.getLocale());
			String splitStr[] = responseStr.split(" ");
			responseStr="";
			Object obj[] =employeeTaxDocumentForm.getMessageParam();
			for(String str : splitStr) {

		    	if(str.equals("{0}")) {
		    	
		    		str =(String)obj[0];
		    	
		    }
		    	if(str.equals("{1}")) {
		        	
		    		str =String.valueOf(year);
		    	
		    }
		    	responseStr= responseStr + " "+str;
				
			}
			
			employeeTaxDocumentForm.setVerifyResponse(responseStr);
		
		}
		return new ResponseEntity<>(employeeTaxDocumentForm, HttpStatus.OK);
	}

	@Override
	@GetMapping("download")
	public ResponseEntity<?> downloadTaxDocuments(@RequestParam(value = "documentId", required = true) Long documentId)throws DocumentException, IOException, JAXBException, SAXException {
		Long documentID = FormatPreserveCryptoUtil.decrypt(documentId);
		String filePath = employeeDocumentLogic.generateTaxDocument(documentID);
		
		
		byte[] byteFile=null;
		String fileName = "";
	
		if(PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			File pdfFile = new File(filePath);
			fileName = pdfFile.getName();
			byteFile = IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
		} else {
			File pdfFile = new File(filePath);
			InputStream pdfIn = null;
			pdfIn = new FileInputStream(pdfFile);
			byteFile = IOUtils.toByteArray(pdfIn);
		}
		if(byteFile == null) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.employee.document.tax.file", new Object[] {}, UserContext.getLocale())),HttpStatus.NOT_FOUND);
		}
		Map<String, Object> downloadMap = new HashMap<>();
		downloadMap.put("downloadTaxbody", byteFile);
		downloadMap.put("pdfName", fileName);
		return new ResponseEntity<>(downloadMap, HttpStatus.OK);
	}

	@Override
	@GetMapping("generate")
	public ResponseEntity<?> getTaxDocuments(@RequestParam(value = "documentId", required = true) Long documentId)
			throws DocumentException, IOException, JAXBException, SAXException {
		Long documentID = FormatPreserveCryptoUtil.decrypt(documentId);
		String filePath = employeeDocumentLogic.generateTaxDocument(documentID);
		byte[] byteFile =null;
		String fileName = "";
		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			File pdfFile = new File(filePath);
			fileName = pdfFile.getName();
			 InputStream inputStream =awss3LogicImpl.readS3ObjectAsStream(filePath);
			 if(inputStream != null ) {
				 byteFile = IOUtils.toByteArray(inputStream);
			 }
			
		} else {
			File pdfFile = new File(filePath);
			InputStream pdfIn = null;
			pdfIn = new FileInputStream(pdfFile);
			byteFile = IOUtils.toByteArray(pdfIn);
		}
		if(byteFile == null) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.employee.document.tax.file", new Object[] {}, UserContext.getLocale())),HttpStatus.NOT_FOUND);
		}
		Map<String, Object> downloadMap = new HashMap<>();
		downloadMap.put("generatedtaxdocument", byteFile);
		downloadMap.put("pdfName", fileName);
		return new ResponseEntity<>(downloadMap, HttpStatus.OK);

	}

	@Override
	@GetMapping("print")
	public ResponseEntity<?> printTaxDocuments(Long documentId)
			throws DocumentException, IOException, JAXBException, SAXException {
		Long documentID = FormatPreserveCryptoUtil.decrypt(documentId);
		String filePath = employeeDocumentLogic.generateTaxDocument(documentID);
		String fileName = "";
		byte[] byteFile = null;
		if(PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			File pdfFile = new File(filePath);
			fileName = pdfFile.getName();
			byteFile = IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
		}
		if(byteFile == null) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.employee.document.tax.file", new Object[] {}, UserContext.getLocale())),HttpStatus.NOT_FOUND);
		}
		Map<String, Object> printMap = new HashMap<>();
		printMap.put("printTaxBody", byteFile);
		printMap.put("pdfName", fileName);
		return new ResponseEntity<>(printMap, HttpStatus.OK);
	}

}
