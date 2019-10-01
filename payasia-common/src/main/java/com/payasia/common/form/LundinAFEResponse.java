package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.LundinAFEDTO;

public class LundinAFEResponse extends PageResponse implements Serializable {
	
	
	List<LundinAFEDTO> lundinAFEDTO;

	public List<LundinAFEDTO> getLundinAFEDTO() {
		return lundinAFEDTO;
	}

	public void setLundinAFEDTO(List<LundinAFEDTO> lundinAFEDTO) {
		this.lundinAFEDTO = lundinAFEDTO;
	}


}
