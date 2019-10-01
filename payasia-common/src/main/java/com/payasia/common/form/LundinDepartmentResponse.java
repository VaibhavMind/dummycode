package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.LundinDepartmentDTO;

public class LundinDepartmentResponse extends PageResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5986633346767522699L;

	public List<LundinDepartmentDTO> getLundinDepartmentDTO() {
		return lundinDepartmentDTO;
	}

	public void setLundinDepartmentDTO(List<LundinDepartmentDTO> lundinDepartmentDTO) {
		this.lundinDepartmentDTO = lundinDepartmentDTO;
	}

	List<LundinDepartmentDTO> lundinDepartmentDTO;
	
}
