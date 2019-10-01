/**
 * @author vivekjain
 *
 */
package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.HrisMyRequestConditionDTO;
import com.payasia.common.dto.HrisPendingItemsConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.HRISChangeRequest;

public interface HRISChangeRequestDAO {

	void update(HRISChangeRequest hrisChangeRequest);

	void delete(HRISChangeRequest hrisChangeRequest);

	void save(HRISChangeRequest hrisChangeRequest);

	HRISChangeRequest findById(Long hrisChangeRequestId);

	List<HRISChangeRequest> findByConditionChangeRequests(
			HrisMyRequestConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Integer getCountChangeRequests(HrisMyRequestConditionDTO conditionDTO);

	HRISChangeRequest savePersist(HRISChangeRequest hrisChangeRequest);

	List<HRISChangeRequest> findByConditionSubmitted(
			HrisPendingItemsConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	HRISChangeRequest findByConditionTableSeq(Long dictionaryId,
			Long employeeId, List<String> hrisStatusList, int tableSeq);

	HRISChangeRequest findByIdAndTableSeq(Long hrisChangeReqId, int tableSeq);

	List<HRISChangeRequest> findByDataDictIdAndSeq(Long companyId,
			Long employeeId, Integer seqNo, List<String> hrisStatusList,
			List<String> fieldDictIds);

	HRISChangeRequest findByDataDictAndSeq(Long dictionaryId, Long employeeId,
			Integer seqNo, List<String> hrisStatusList);

	HRISChangeRequest findByIdAndEmployeeId(Long hrisChangeRequestId, Long employeeId);
	
	HRISChangeRequest findByIdAndCompanyId(Long hrisChangeRequestId,Long companyId);
	
	HRISChangeRequest findByDataDictAndSeq(Long dictionaryId, Long employeeId,
			Integer seqNo, List<String> hrisStatusList,Long companyId);

	List<HRISChangeRequest> findByConditionSubmitted2(HrisPendingItemsConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, int position);

	HRISChangeRequest findByCondition(Long dictionaryId, Integer tableId, Long employeeId, List<String> hrisStatusList);

	HRISChangeRequest findByCondition(Long dictionaryId, Long employeeId, List<String> hrisStatusList);

}
