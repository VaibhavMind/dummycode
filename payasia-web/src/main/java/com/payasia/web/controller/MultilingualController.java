package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface MultilingualController {
	String getMultilingualLabels(Long entityId, Long languageId,
			String columnName, String sortingType, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	void updateMultilingualLabel(Long dataDictionaryId, Long languageId,
			String labelValue, HttpServletRequest request);

	void deleteMultilingualRecord(Long dataDictionaryId, Long languageId);

	String getMultilingualLabels(Long entityId, Long languageId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);
}
