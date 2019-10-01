package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.form.LundinTimesheetPreferenceForm;

public interface LundinTimesheetPreferenceLogic {

	void createOTBatches(int cutOffDay, int yearOfBatch, long companyId);

	List<LundinOTBatchDTO> getOTBacthesByCompanyId(long companyId,
			Long employeeId);

	int getCutOffDay(long companyId) throws Exception;

	void saveLundinTimesheetPreference(
			LundinTimesheetPreferenceForm lundinTimesheetPreferenceForm,
			Long companyId);

	LundinTimesheetPreferenceForm getLundinTimesheetPreference(Long companyId);
	
	LundinOTBatchDTO getOTBatchById(Long otBachId,Long companyId,
			Long employeeId);

}
