package com.payasia.web.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import com.payasia.dao.CompanyDAO;
import com.payasia.dao.LanguageMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.LanguageMaster;
import com.payasia.logic.LanguageMasterLogic;
import com.payasia.logic.LoginLogic;

public class PayAsiaLocaleChangeIntercepter extends LocaleChangeInterceptor {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PayAsiaLocaleChangeIntercepter.class);

	@Resource
	private LanguageMasterDAO languageMasterDAO;
	@Resource
	private CompanyDAO companyDAO;
	@Resource
	private LanguageMasterLogic languageMasterLogic;
	@Resource
	private URLUtils URLUtils;
	@Resource
	LoginLogic loginLogic;

	/**
	 * Purpose : Pre handle the request before Locale change.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param HttpServletResponse
	 *            the response
	 * @param Object
	 *            the handler
	 * @return the status
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler)
			throws ServletException {
		final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class
				.getName() + ".LOCALE";

		LocaleResolver localeResolver = RequestContextUtils
				.getLocaleResolver(request);
		if (localeResolver == null) {
			throw new IllegalStateException(
					"No LocaleResolver found: not in a DispatcherServlet request?");
		}

		String localeParam = request.getParameter(super.getParamName());
		LanguageMaster languageMasterVO = getLanguageMaster(request,
				localeParam);

		 
		 
		String shortCompanyCode = "";
		try {
			String companyCodeByURL = URLUtils.getSubDomain(new URL(request
					.getRequestURL().toString()));

			Company companyVO = companyDAO.findByCompanyCode(companyCodeByURL,
					null);
			if (companyVO != null
					&& StringUtils.isNotBlank(companyVO.getShortCompanyCode())) {
				shortCompanyCode = companyVO.getShortCompanyCode();
			}
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		}

		 
		if (languageMasterVO != null) {
			if (languageMasterVO.getLanguageCode().equals(localeParam)) {

				Locale newLocale = URLUtils.getNewLocale(localeParam,
						shortCompanyCode);

				WebUtils.setSessionAttribute(request,
						LOCALE_SESSION_ATTRIBUTE_NAME, newLocale);

				localeResolver.setLocale(request, response, newLocale);

				 
				request.getSession().setAttribute(
						PayAsiaSessionAttributes.PAYASIA_LOCALE_LABEL,
						languageMasterVO.getLanguageCode());
				populateLanguageId(request, languageMasterVO);

			}
			 
			request.setAttribute(PayAsiaSessionAttributes.PAYASIA_LOCALE_LABEL,
					languageMasterVO.getLanguageCode());
		}

		return true;

	}

	/**
	 * Purpose : Get Language Master Data by Locale.
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param localeParam
	 *            the locale Parameter
	 * @return the LanguageMaster
	 */
	private LanguageMaster getLanguageMaster(HttpServletRequest request,
			String localeParam) {
		LanguageMaster languageMasterVO = null;

		if (StringUtils.isNotBlank(localeParam)) {
			languageMasterVO = languageMasterDAO
					.findByLanguageCode(localeParam);
		}

		if (languageMasterVO == null) {
			Locale existingLocale = request.getLocale();
			localeParam = existingLocale.getLanguage() + "_"
					+ existingLocale.getCountry();
			 
			 
			languageMasterVO = languageMasterDAO
					.findByLanguageCode(localeParam);
			if (languageMasterVO == null) {
				languageMasterVO = languageMasterDAO.getDefaultLanguage();
			}
		}
		populateLanguageId(request, languageMasterVO);

		return languageMasterVO;
	}

	/**
	 * Purpose : populate Language Id in session
	 * 
	 * @param HttpServletRequest
	 *            the request
	 * @param LanguageMaster
	 *            the Language Master VO
	 */
	private void populateLanguageId(HttpServletRequest request,
			LanguageMaster languageMasterVO) {
		String localeLangId = String.valueOf(languageMasterVO.getLanguageId());
		request.getSession().setAttribute(PayAsiaSessionAttributes.LANGUAGE_ID,
				localeLangId);
	}
}
