package com.payasia.api.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;

@Component
public class SearchSortUtils {

	public final SearchSortDTO getSearchSortObject(SearchParam searchParamObj) {
		final List<MultiSortMeta> multiSortList = Arrays.asList(searchParamObj.getMultiSortMeta());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParamObj.getPage());
		pageDTO.setPageSize(searchParamObj.getRows());
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParamObj.getSortField());
		sortDTO.setOrderType(searchParamObj.getSortOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		if (multiSortList != null && !multiSortList.isEmpty()) {
			sortDTO.setColumnName(multiSortList.get(0).getField());
			sortDTO.setOrderType(multiSortList.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}
		return new SearchSortDTO(pageDTO,sortDTO);
	}
}
