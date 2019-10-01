package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.HRLetterShortlist;

public interface HRLetterShortlistDAO {

	void update(HRLetterShortlist hRLetterShortlist);

	void save(HRLetterShortlist hRLetterShortlist);

	void delete(HRLetterShortlist hRLetterShortlist);

	HRLetterShortlist findByID(long hRLetterShortlistId);

	void deleteByCondition(Long documentId);

	List<HRLetterShortlist> findByCondition(Long hrLetterId);
	List<HRLetterShortlist> findByHRLetterShortlistCondition(Long hrLetterId,Long companyId);
	
	HRLetterShortlist findByID(long hRLetterShortlistId,Long companyId);
	
	
	


}
