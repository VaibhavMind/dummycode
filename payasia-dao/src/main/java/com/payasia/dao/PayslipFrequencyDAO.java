/**
 * @author ragulapraveen
 *
 */
package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.PayslipFrequency;

/**
 * The Interface PayslipFrequencyDAO.
 */

public interface PayslipFrequencyDAO {

	/**
	 * Find all PayslipFrequency Object list.
	 * 
	 * @return the list
	 */
	List<PayslipFrequency> findAll();

	/**
	 * Find PayslipFrequency Object by paySLipFrequncyId.
	 * 
	 * @param paySlipfreId
	 *            the pay slipfre id
	 * @return the payslip frequency
	 */
	PayslipFrequency findById(long paySlipfreId);

	/**
	 * Find PayslipFrequency Object by frequency.
	 * 
	 * @param frequency
	 *            the frequency
	 * @return the payslip frequency
	 */
	PayslipFrequency findByFrequency(String frequency);
}
