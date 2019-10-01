package com.payasia.test.claim.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.auth.impl.LoginApiImpl;
import com.payasia.api.common.claim.impl.CommonClaimApiImpl;
import com.payasia.logic.LanguageMasterLogic;
import com.payasia.logic.MyClaimLogic;
import com.payasia.logic.PendingClaimsLogic;
import com.payasia.test.root.TDDConfig;

@RestController
public class CommonClaimApiTDD extends TDDConfig {

	@InjectMocks
	private CommonClaimApiImpl commonClaimApiImpl;
	
	@InjectMocks
	private LoginApiImpl loginApiImpl;
	
	@Mock
	private PendingClaimsLogic pendingClaimsLogic;
	
	@Mock
	private MyClaimLogic myClaimLogic;
	
	@Mock
	private LanguageMasterLogic languageMasterLogic;

	@Before
	public void setup() {
		mockMvc=MockMvcBuilders.standaloneSetup(commonClaimApiImpl).build();
	}

/*	@Test
	public void language_list_test() throws Exception {
		
		// String uri= getAppUrl()+"login/language-list";
		// ResultActions result = mockMvc.perform(post("/payasia/api/v1/login/language-list").contentType(MediaType.APPLICATION_JSON))
		// .andExpect(status().isOk())
		// .andExpect(content().contentTypeCompatibleWith(TestUtil.APPLICATION_JSON_UTF8));
		// .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));
		// .andExpect(jsonPath("$.remarks", is("HELLO")));	
		
		List<LanguageListDTO> result = languageMasterLogic.getLanguages();
		assertNotNull(result);
		verify(languageMasterLogic, times(1)).getLanguages();
		verifyNoMoreInteractions(languageMasterLogic);
		
	}
	
	@Test
	public void language_list_test2() throws Exception {
		
		Map<String, Object> lmap = new HashMap<String, Object>();
		List<LanguageListDTO> languageList = new ArrayList<LanguageListDTO>();
		LanguageListDTO  obj =	new LanguageListDTO();
		obj.setLanguage("HI");
		languageList.add(obj);
		lmap.put("key", languageList);
		
		when(languageMasterLogic.getLanguages()).thenReturn(languageList);
		
		assertEquals(1, languageList.size());

		mockMvc.perform(post("/payasia/api/v1/login/language-list"))
             .andExpect(status().isOk());

		verify(languageMasterLogic, times(1)).getLanguages();
		verifyNoMoreInteractions(languageMasterLogic);
	
	}*/
	

	@Test
	public void claim_reviewers_test() throws Exception {
		
	/*	PendingClaimsForm pendingClaimsForm = new PendingClaimsForm();
		pendingClaimsForm.setRemarks("HELLO");
	
		when(pendingClaimsLogic.getClaimReviewersData(null, 12440l, new Locale("en","US"))).thenReturn(pendingClaimsForm);
		
		String uri= getAppUrl()+"employee/claim/claim-reviewers";
		
		 ResultActions result = mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isOk())
         .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
         .andExpect(jsonPath("$.remarks", is("HELLO")));	
		  verify(pendingClaimsLogic, times(1)).getClaimReviewersData(null, 12440l, new Locale("en","US"));
	      verifyNoMoreInteractions(pendingClaimsLogic);*/
		
	}
	/*
	*//**
	 * Tests the custom ResourceException and verifies that it returns a HTTP
	 * 400 with the expected message.
	 *//*
	@Test
	public void testResourceException400() {
		try {
			String uri= getAppUrl()+"employee/claim/claim-reviewers";
			mockMvc.perform(get(uri)).andExpect(status().isBadRequest()).andExpect(content().string("Test Bad Request Exception message")).andDo(print());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	*//**
	 * Tests the custom ResourceException and verifies that it returns a HTTP
	 * 404 with the expected message.
	 *//*
	@Test
	public void testResourceException404() {
		try {
			String uri= getAppUrl()+"employee/claim/claim-reviewers";
			mockMvc.perform(get(uri)).andExpect(status().isNotFound()).andExpect(content().string("Test Not Found Exception message")).andDo(print());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	*//**
	 * Tests that a RuntimeException is received and that it returns the
	 * expected message.
	 *//*
	@Test
	public void testRuntimeException() {
		try {
			String uri= getAppUrl()+"employee/claim/claim-reviewers";
			mockMvc.perform(get(uri)).andExpect(status().isInternalServerError()).andExpect(content().string("java.lang.NumberFormatException: For input string: \"x\"")).andDo(print());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	*//**
	 * Tests that an Exception is received and that it returns the expected
	 * message.
	 *//*
	@Test
	public void testException() {
		try {
			String uri= getAppUrl()+"employee/claim/claim-reviewers";
			mockMvc.perform(get(uri)).andExpect(status().isInternalServerError()).andExpect(content().string("Test Exception message")).andDo(print());
		} catch (Exception e) {
			fail(e.toString());
		}
	}*/
}