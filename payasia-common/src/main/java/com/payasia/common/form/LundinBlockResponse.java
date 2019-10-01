package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.LundinBlockDTO;

public class LundinBlockResponse extends PageResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5986633346767522699L;

	List<LundinBlockDTO> lundinBlockDTO;

	public List<LundinBlockDTO> getLundinBlockDTO() {
		return lundinBlockDTO;
	}

	public void setLundinBlockDTO(List<LundinBlockDTO> lundinBlockDTO) {
		this.lundinBlockDTO = lundinBlockDTO;
	}

	
}
