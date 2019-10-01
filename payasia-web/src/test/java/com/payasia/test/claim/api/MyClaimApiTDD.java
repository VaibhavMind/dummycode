package com.payasia.test.claim.api;

import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.claim.impl.EmployeeMyClaimApiImpl;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.MyClaimLogic;
import com.payasia.test.root.TDDConfig;

@RestController
public class MyClaimApiTDD extends TDDConfig{

	@InjectMocks
	private EmployeeMyClaimApiImpl employeeMyClaimApiImpl;
	
	@Mock
	private MyClaimLogic myClaimLogic;
	
	@Mock
	private AddClaimLogic addClaimLogic;
	
	@Before
	public void setup() {
		mockMvc=MockMvcBuilders.standaloneSetup(employeeMyClaimApiImpl).build();
		this.employeeMyClaimApiImpl.intialize(messageSource);
	}
	
	@SuppressWarnings("unchecked")
	@Test
//	@Ignore("$.data is incorrect.")
	
	public void test_print_my_claim_data() throws Exception{
		Long claimApplicationId = 11l;
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(267l);
		claimDTO.setEmployeeId(12440l);
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(11l));
		claimDTO.setAdmin(false);
		claimDTO.setHasLundinTimesheetModule(true);
		
		ClaimFormPdfDTO claimFormPdfDTO = new ClaimFormPdfDTO();
		byte[] data1 = "hello My name is KK".getBytes();
		claimFormPdfDTO.setEmployeeNumber("99");
		claimFormPdfDTO.setClaimTemplateName("tt");
		claimFormPdfDTO.setClaimFormPdfByteFile(data1);
		
		String filename = claimFormPdfDTO.getEmployeeNumber() + "_" + claimFormPdfDTO.getClaimTemplateName() + ".pdf";
		
		when(myClaimLogic.generateClaimFormPrintPDF(claimDTO)).thenReturn(claimFormPdfDTO);
		
		ResponseEntity<Map<String, Object>> res = (ResponseEntity<Map<String, Object>>) employeeMyClaimApiImpl.doPrintMyClaimData(claimApplicationId);
		Assert.assertEquals(claimFormPdfDTO.getEmployeeNumber(), res.getBody().get("employeeNumber"));
		Assert.assertEquals(claimFormPdfDTO.getClaimTemplateName(), res.getBody().get("claimTemplateName"));
		Assert.assertEquals(filename, res.getBody().get("filename"));
		Assert.assertTrue(res.hasBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());

	}
	
	// TODO: Expect the message for this API.(Message Like : "Operation succeeded for the request")
//	@Ignore
//	@Test(timeout=9000)
	@Test
	public void test_delete_my_claim_template_item() throws Exception{
		Long claimApplicationItemId = 22l;
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(267l);
		claimDTO.setEmployeeId(12440l);
		claimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(22l));
		
		AddClaimForm addClaimForm = new AddClaimForm();
		addClaimForm.setRemarks("Hello");
		
		when(addClaimLogic.deleteClaimTemplateItem(claimDTO)).thenReturn(addClaimForm);
		
		ResponseEntity<?> res = employeeMyClaimApiImpl.deleteMyClaimTemplateItem(claimApplicationItemId);
		
		Assert.assertEquals(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.my.claim.request.operation.success", new Object[]{}, UserContext.getLocale())), res.getBody());
		// Assert.assertEquals(res.getBody(), apiMessageHandler);
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_view_claim_application_data() throws Exception{
		Long claimApplicationId = 33l;
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(267l);
		claimDTO.setEmployeeId(12440l);
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(33l));
		claimDTO.setLocale(UserContext.getLocale());
		claimDTO.setAdmin(false);
	
		AddClaimForm addClaimForm = new AddClaimForm();
		addClaimForm.setRemarks("Hello data");
		addClaimForm.setIsApproved(true);
		addClaimForm.setEmailCC("thanks@sir");
		
		when(myClaimLogic.getClaimAppDataMsgSource(claimDTO, messageSource)).thenReturn(addClaimForm);
		
		ResponseEntity<?> res = employeeMyClaimApiImpl.viewClaimApplicationData(claimApplicationId);
		Assert.assertEquals(addClaimForm, res.getBody());
		Assert.assertTrue(res.hasBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
//	@Ignore
	@Test
	public void test_get_save_or_create_claim_application() throws Exception{
		Long employeeClaimTemplateId = 44l;
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(44l));
		addClaimDTO.setCompanyId(267l);
		addClaimDTO.setEmployeeId(12440l);
		addClaimDTO.setAdmin(false);
		
		AddClaimForm addClaimForm = new AddClaimForm();
		addClaimForm.setClaimReviewerNotDefined(false);
		addClaimForm.setRemarks("Hello");
		
		when(addClaimLogic.saveClaimApplication(addClaimDTO)).thenReturn(addClaimForm);
		
		ResponseEntity<?> res = employeeMyClaimApiImpl.getSaveOrCreateClaimApplication(employeeClaimTemplateId);
		Assert.assertEquals(addClaimForm, res.getBody());
		Assert.assertTrue(res.hasBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@SuppressWarnings("unchecked")
//	@Ignore
	@Test
	public void test_update_claim_template_item() throws Exception{
		Long companyId = 267l;
		
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		claimApplicationItemForm.setLundinTimesheetModule(true);
		
		AddClaimForm addClaimForm = new AddClaimForm();
		addClaimForm.setRemarks("Hi Claim");
		
		when(addClaimLogic.updateClaimTemplateItem(claimApplicationItemForm, companyId)).thenReturn(addClaimForm);
		
		ResponseEntity<?> res = (ResponseEntity<Map<String, Object>>) employeeMyClaimApiImpl.updateClaimTemplateItem(claimApplicationItemForm);
		Assert.assertEquals(addClaimForm, res.getBody());
		Assert.assertTrue(res.hasBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}

//	@Ignore
	@Test
	public void test_save_as_draft_from_withdraw() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		
		AddClaimForm addClaimForm = new AddClaimForm();
		addClaimForm.setClaimApplicationId(55l);
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));

		AddClaimForm addClaimFormRes = new AddClaimForm();
		addClaimFormRes.setRemarks("Hello draft");
		
		when(addClaimLogic.saveAsDraftFromWithdrawClaim(addClaimDTO, addClaimForm)).thenReturn(addClaimFormRes);
		
		ResponseEntity<?> res = employeeMyClaimApiImpl.saveAsDraftFromWithdraw(addClaimForm);
		Assert.assertEquals(addClaimFormRes, res.getBody());
		Assert.assertTrue(res.hasBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}

//	@Ignore
	@Test
	public void test_get_claim_application_item_list() throws Exception{
		Long employeeId = 12440l;
		Long companyId = 267l;
		Long claimApplicationId = 77l;
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(77l));
		
		AddClaimForm addClaimFormRes = new AddClaimForm();
		addClaimFormRes.setRemarks("Hello list");
		
		when(addClaimLogic.getClaimApplicationItemList(addClaimDTO)).thenReturn(addClaimFormRes);

		ResponseEntity<?> res = employeeMyClaimApiImpl.getClaimApplicationItemList(claimApplicationId);
		Assert.assertEquals(addClaimFormRes, res.getBody());
		Assert.assertTrue(res.hasBody());
		Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
}
