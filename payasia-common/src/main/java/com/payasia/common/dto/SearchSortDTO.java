package com.payasia.common.dto;

import java.io.Serializable;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public class SearchSortDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	public PageRequest pageRequest;

	public SortCondition sortCondition;
	
	public SearchSortDTO() {}

	public SearchSortDTO(PageRequest pageRequest, SortCondition sortCondition) {
		super();
		this.pageRequest = pageRequest;
		this.sortCondition = sortCondition;
	}


	public PageRequest getPageRequest() {
		return pageRequest;
	}

	public void setPageRequest(PageRequest pageRequest) {
		this.pageRequest = pageRequest;
	}

	public SortCondition getSortCondition() {
		return sortCondition;
	}

	public void setSortCondition(SortCondition sortCondition) {
		this.sortCondition = sortCondition;
	}

}
