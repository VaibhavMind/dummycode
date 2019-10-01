/**
 * @author vivekjain
 *
 */
package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.payasia.common.form.AnouncementForm;

/**
 * The Interface AnouncementController.
 */

public interface AnouncementController {

	void postAnouncement(AnouncementForm anouncementForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String getAnouncement(HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	String getAnouncementForEdit(Long announcementId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	void updateAnouncement(AnouncementForm anouncementForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	void deleteAnouncement(Long announcementId, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

}
