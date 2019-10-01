package com.payasia.api.hris.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.hris.EmployeeHrLetterApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.HRLetterForm;
import com.payasia.common.form.HRLetterResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.HRLetterLogic;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.jxls.exception.ParsePropertyException;

/**
 * @author gauravkumar
 * @param :
 *            This class used for HR-Letter management
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH + "/employee/" + "hris/")
public class EmployeeHrLetterApiImpl implements EmployeeHrLetterApi {

	@Autowired
	@Qualifier("HRLetterLogic")
	private HRLetterLogic hrLetterLogic;

	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;

	private static final Logger LOGGER = Logger.getLogger(EmployeeHrLetterApiImpl.class);

	/**
	 * SHOWS ALL THE LETTERS ACCORDING TO THE CONDITIONS
	 */

	@Override
	@PostMapping(value = "search-hrletter")
	public ResponseEntity<?> doSearchHrLetter(@RequestBody SearchParam searchParamObj) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long loggedInEmployeeId = Long.valueOf(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());

		HRLetterResponse hrLetterResponse = null;
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		// CASE 1 : sort data according to request parameters/MultiSortMeta []
		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}

		// CASE 2 : filter data according to request parameters/Filters[]
		if ((filterllist != null && !filterllist.isEmpty())) {
			hrLetterResponse = hrLetterLogic.searchEmployeeHRLetter(companyId, filterllist.get(0).getField(),
					filterllist.get(0).getValue(), searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), loggedInEmployeeId);
		} else {
			hrLetterResponse = hrLetterLogic.searchEmployeeHRLetter(companyId, "", "", searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), loggedInEmployeeId);
		}

		if (hrLetterResponse != null) {
			return new ResponseEntity<>(hrLetterResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = "generate-hrletter")
	public ResponseEntity<?> doGenerateHrLetter(@RequestBody String jsonStr) throws JSONException {
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long loggedInEmployeeId = Long.valueOf(UserContext.getUserId());
		boolean isAdmin = false;
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long letterId = jsonObj.getLong("letterId");
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);
		HRLetterForm hrLetterForm = hrLetterLogic.getHRLetter(letterId, companyId);
		String convertedPreviewMessage = hrLetterLogic.previewHRLetterBodyText(companyId, letterId, loggedInEmployeeId, loggedInEmployeeId,null, null, isAdmin);
		if(StringUtils.isEmpty(convertedPreviewMessage) || "send.hr.letter.configuration.is.not.defined".equalsIgnoreCase(convertedPreviewMessage)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(convertedPreviewMessage, new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
		
		String isSaveHrLetterInDocumentCenter = hrLetterLogic.isSaveHrLetterInDocumentCenter(companyId);
		hrLetterForm.setSaveInDocumentCenter(isSaveHrLetterInDocumentCenter!=null && isSaveHrLetterInDocumentCenter.equalsIgnoreCase("TRUE")?true:false);
		hrLetterForm.setBody(convertedPreviewMessage);
		return new ResponseEntity<>(hrLetterForm, HttpStatus.OK);
	}
	

	@Override
	@PostMapping(value = "preview-hrletter")
	public ResponseEntity<?> doPreviewHrLetterinPDF(@RequestBody String jsonStr){

		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		boolean isAdmin = false;
		byte[] data = null;
		Map<String, Object> hrLetterMap = new HashMap<>();

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(jsonStr, jsonConfig);
		Long letterId = jsonObject.getLong("letterId");
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);

		UUID uuid = UUID.randomUUID();

		String hrLetterBodyText = hrLetterLogic.previewHRLetterBodyText(companyId, letterId, employeeId, employeeId,
				null, null, isAdmin);

		String hrLetterHTMLBody = "<html><head><meta charset='UTF-8' /></head><body>";
		hrLetterHTMLBody += hrLetterBodyText;
		hrLetterHTMLBody += "</body></html>";

		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrLetter/" + uuid);
		tempFolder.mkdirs();

		String destFileNameHTML = PAYASIA_TEMP_PATH + "/HRLetter" + uuid + ".html";
		
		File htmlFile = new File(destFileNameHTML);
		FileWriterWithEncoding fw;
		
		try {
			fw = new FileWriterWithEncoding(htmlFile.getAbsoluteFile(), "UTF-8");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(hrLetterHTMLBody);
			bw.close();
		} catch (IOException e1) {
			LOGGER.error(e1);
		}

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/HRLetter" + uuid + ".pdf";

		try {
			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameHTML;
			String file2 = destFileNamePDF;

			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   " + processBuilder.command());
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			process.waitFor();

		} catch (ParsePropertyException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		try {

			Path path = Paths.get(destFileNamePDF);
			data = Files.readAllBytes(path);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

		if (data == null) {
			return new ResponseEntity<>(
					new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
							.getMessage("hrletter.preview.msg", new Object[] {}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
		
		hrLetterMap.put("hrletterbodyFormat", data);
		return new ResponseEntity<>(hrLetterMap, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "send-hrletter")
	public ResponseEntity<?> doSendHrLetter(@RequestBody String jsonStr) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long loginEmployeeID = Long.valueOf(UserContext.getUserId());

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(jsonStr, jsonConfig);
		Long letterId = jsonObject.getLong("letterId");
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);
		
		Long employeeId = Long.valueOf(UserContext.getUserId());
		String hrLetterBodyText = jsonObject.getString("hrLetterBodyText");
		String ccEmails = jsonObject.getString("ccEmails");
		Boolean saveInDocCenterCheck = jsonObject.getBoolean("saveInDocCenterCheck");
		String status = hrLetterLogic.sendHrLetterWithPDF(companyId, letterId, employeeId, loginEmployeeID,
				hrLetterBodyText, ccEmails, saveInDocCenterCheck);

		if(status.equalsIgnoreCase("payasia.hr.letters.hrletter.send.successfully")){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(status, new Object[] {}, UserContext.getLocale())), HttpStatus.OK);
		}else{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(status, new Object[] {}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
	}

}
