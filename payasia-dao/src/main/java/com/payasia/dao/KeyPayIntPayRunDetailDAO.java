package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.KeyPayIntPayRunDetail;

public interface KeyPayIntPayRunDetailDAO {

	void save(KeyPayIntPayRunDetail keyPayIntPayRunDetail);

	List<KeyPayIntPayRunDetail> findByKeyPayIntPayRunId(Long keyPayIntPayRunId);

	KeyPayIntPayRunDetail findByCondition(Long keyPayIntPayRunId,
			Long leaveCategoryId, String externalId, Long employeeId);

	void update(KeyPayIntPayRunDetail keyPayIntPayRunDetail);

	KeyPayIntPayRunDetail saveReturn(KeyPayIntPayRunDetail keyPayIntPayRunDetail);

	List<Long> findKeyPayRunDetailId(Long keyPayIntPayRunId);

	void delete(KeyPayIntPayRunDetail keyPayIntPayRunDetail);

}
