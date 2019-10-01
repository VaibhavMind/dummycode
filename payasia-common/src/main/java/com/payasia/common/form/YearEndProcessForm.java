package com.payasia.common.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.payasia.common.dto.CompanyDTO;
import com.payasia.common.dto.YearEndProcessDTO;

public class YearEndProcessForm implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, List<CompanyDTO>>  companyGroupList ;
	
	private List<YearEndProcessDTO> yearEndProcessList = LazyList.decorate(
			new ArrayList<YearEndProcessDTO>(),
			FactoryUtils.instantiateFactory(YearEndProcessDTO.class));

	

	public List<YearEndProcessDTO> getYearEndProcessList() {
		return yearEndProcessList;
	}

	public void setYearEndProcessList(List<YearEndProcessDTO> yearEndProcessList) {
		this.yearEndProcessList = yearEndProcessList;
	}

	public Map<String, List<CompanyDTO>> getCompanyGroupList() {
		return companyGroupList;
	}

	public void setCompanyGroupList(Map<String, List<CompanyDTO>> companyGroupList) {
		this.companyGroupList = companyGroupList;
	}

	

	

	
	
	

}
