package com.payasia.logic;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.HRLetterForm;
import com.payasia.common.form.HRLetterResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface HRLetterLogic.
 */
/**
 * @author vivekjain
 * 
 */
@Transactional
public interface HRLetterLogic {

	/**
	 * Gets the hR letter list.
	 * 
	 * @return the hR letter list
	 */
	HRLetterResponse getHRLetterList(Long companyId, PageRequest pageDTO,
			SortCondition sortDTO);

	/**
	 * Save hr letter.
	 * 
	 * @param hrLetterForm
	 *            the hr letter form
	 * @return
	 */
	String saveHRLetter(HRLetterForm hrLetterForm, Long companyId);

	/**
	 * Delete hr letter.
	 * 
	 * @param letterId
	 *            the letter id
	 * @return
	 */
	String deleteHRLetter(long letterId, Long companyId);

	/**
	 * Gets the hR letter.
	 * 
	 * @param letterId
	 *            the letter id
	 * @return the hR letter
	 */
	HRLetterForm getHRLetter(long letterId);

	/**
	 * Update hr letter.
	 * 
	 * @param hrLetterForm
	 *            the hr letter form
	 * @return
	 */
	String updateHRLetter(HRLetterForm hrLetterForm, Long companyId);

	/**
	 * purpose : search HRLetter.
	 * 
	 * @param SortCondition
	 *            the sortDTO
	 * @param Long
	 *            the companyId
	 * @param PageRequest
	 *            the pageDTO
	 * @param String
	 *            the searchCondition
	 * @param String
	 *            the searchText
	 * @return HRLetterResponse contains HrLetter List
	 */
	HRLetterResponse searchHRLetter(Long companyId, String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO);

	List<EmployeeFilterListForm> getEditEmployeeFilterList(Long hrLetterId,
			Long companyId);

	void deleteFilter(Long filterId,Long companyId);

	String saveEmployeeFilterList(String metaData, Long hrLetterId);

	/**
	 * purpose : Send HRLetter to Given EmailId.
	 * 
	 * @param long the companyId
	 * @param long the letterId
	 * @param String
	 *            the emailId
	 * @return Response
	 */
	String sendHRLetter(Long companyId, long hrLetterId,
			HRLetterForm hrLetterForm, Long loggedInEmployeeId);

	Set<String> custPlaceHolderList(Long companyId, long hrLetterId,
			Long loggedInEmployeeId);

	String sendHrLetterWithPDF(Long companyId, long hrLetterId,
			long employeeId, Long loggedInEmployeeId, String hrLetterBodyText,
			String ccEmails, boolean saveInDocCenterCheck);

	HRLetterResponse searchEmployeeHRLetter(Long companyId,
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long loggedInEmployeeId);

	String isSaveHrLetterInDocumentCenter(Long companyId);

	String previewHRLetterBodyText(Long companyId, long hrLetterId,
			long employeeId, Long loggedInEmpId, String[] userInputKeyArr,
			String[] userInputValArr, boolean isAdmin);
	
	HRLetterForm getHRLetter(long letterId,Long companyId);

	Long getEmployeeIdByCode(String employeeNumber, Long companyID);
	
}
