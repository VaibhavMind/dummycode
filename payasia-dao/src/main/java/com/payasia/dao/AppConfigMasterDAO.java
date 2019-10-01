package com.payasia.dao;

import com.payasia.dao.bean.AppConfigMaster;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface AppCodeMasterDAO.
 */
public interface AppConfigMasterDAO {
	/**
	 * purpose : find By Name paramName.
	 * 
	 * @param String
	 *            the paramName
	 * @return AppConfigMaster Entity Bean
	 */
	AppConfigMaster findByName(String paramName);

}
