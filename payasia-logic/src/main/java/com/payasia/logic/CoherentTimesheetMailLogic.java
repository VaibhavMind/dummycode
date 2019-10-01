package com.payasia.logic;

import com.payasia.common.dto.EmailDataDTO;

public interface CoherentTimesheetMailLogic {

	String sendEMailForTimesheet(Long companyId, EmailDataDTO emailDataDTO,
			String subCategoryName);

	String sendWithdrawEmailForTimesheet(Long companyId,
			EmailDataDTO emailDataDTO, String subCategoryName);

	String sendPendingEmailForTimesheet(Long companyId,
			EmailDataDTO emailDataDTO, String subCategoryName, String revRemarks);

	String sendAcceptRejectMailForTimesheet(Long companyId,
			EmailDataDTO emailDataDTO, String subCategoryName,
			String reviewRemarks);

}
