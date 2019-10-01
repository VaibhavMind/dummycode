package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmployeeDocumentHistory;

/**
 * @author vivekjain
 * 
 */
public interface EmployeeDocumentHistoryDAO {

	EmployeeDocumentHistory findById(long employeeDocumentHistoryId);

	void delete(EmployeeDocumentHistory employeeDocumentHistory);

	void save(EmployeeDocumentHistory employeeDocumentHistory);

	void update(EmployeeDocumentHistory employeeDocumentHistory);

	List<EmployeeDocumentHistory> findByCondition(Long tableId, Integer seqNo);

	void deleteByConditionEmployeeTableRecord(Long tableId, Integer seqNo);

}
