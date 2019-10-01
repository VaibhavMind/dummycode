/**
 * @author vivekjain
 *
 */
package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.HelpDeskDTO;
import com.payasia.common.form.AnouncementForm;
import com.payasia.common.form.AnouncementFormResponse;
import com.payasia.common.form.HelpDeskFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface AnouncementLogic.
 */
public interface AnouncementLogic {

	String postAnouncement(AnouncementForm anouncementForm, Long companyId);

	List<AnouncementForm> getAnouncement(Long companyId);

	AnouncementForm getAnouncementForEdit(Long companyId, Long announcementId);

	String updateAnouncement(AnouncementForm anouncementForm, Long companyId);

	String deleteAnouncement(Long announcementId, Long companyId);                    

	List<HelpDeskFormResponse> getTopicGuidelineList(HelpDeskDTO helpDesk);

	AnouncementFormResponse getAnnouncementList(Long companyId, PageRequest pageDTO, SortCondition sortCondition,
			AnouncementForm announcementForm);

	AnouncementFormResponse getAllAnnouncementList(Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			AnouncementForm announcementForm);

}
