package com.payasia.common.exception;

import java.util.ArrayList;
import java.util.List;

import com.payasia.common.dto.DataImportLogDTO;

public class PayAsiaRollBackDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private List<DataImportLogDTO> errors;
	
	public PayAsiaRollBackDataException(List<DataImportLogDTO> argErrors) {
		super();
		this.errors = argErrors;
	}
	
	public PayAsiaRollBackDataException(DataImportLogDTO argError) {
		super();
		if(errors == null)
		{
			errors = new ArrayList<DataImportLogDTO>();
		}
		this.errors.add(argError);
	}
	
	public PayAsiaRollBackDataException(String errorType, String errorMsg) {
		super();
		DataImportLogDTO error = new DataImportLogDTO();
		
		error.setFailureType(errorType);
		error.setRemarks(errorMsg);
		error.setFromMessageSource(false);
		
		if(errors == null)
		{
			errors = new ArrayList<DataImportLogDTO>();
		}
		
		this.errors.add(error);
	}

	public List<DataImportLogDTO> getErrors() {
		return errors;
	}
	
}
