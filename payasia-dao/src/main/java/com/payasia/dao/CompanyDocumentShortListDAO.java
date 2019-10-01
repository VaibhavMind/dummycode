package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.CompanyDocumentShortList;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface CompanyDocumentShortListDAO.
 */
public interface CompanyDocumentShortListDAO {

	/**
	 * purpose : find CompanyDocumentShortList object List By Condition
	 * documentId.
	 * 
	 * @param Long
	 *            the documentId
	 * @return CompanyDocumentShortList List
	 */
	List<CompanyDocumentShortList> findByCondition(Long documentId);

	/**
	 * purpose : find CompanyDocumentShortList object By
	 * companyDocumentShortListId.
	 * 
	 * @param Long
	 *            the companyDocumentShortListId
	 * @return CompanyDocumentShortList EntityBean
	 */
	CompanyDocumentShortList findById(long companyDocumentShortListId);

	/**
	 * purpose : delete CompanyDocumentShortList object.
	 * 
	 * @param CompanyDocumentShortList
	 *            the companyDocumentShortList
	 */
	void delete(CompanyDocumentShortList companyDocumentShortList);

	/**
	 * purpose : update CompanyDocumentShortList object.
	 * 
	 * @param CompanyDocumentShortList
	 *            the companyDocumentShortList
	 */
	void update(CompanyDocumentShortList companyDocumentShortList);

	/**
	 * purpose : save CompanyDocumentShortList object.
	 * 
	 * @param CompanyDocumentShortList
	 *            the companyDocumentShortList
	 */
	void save(CompanyDocumentShortList companyDocumentShortList);

	/**
	 * purpose : Delete CompanyDocumentShortList object By Condition documentId.
	 * 
	 * @param Long
	 *            the documentId
	 */
	void deleteByCondition(Long documentId);

}
