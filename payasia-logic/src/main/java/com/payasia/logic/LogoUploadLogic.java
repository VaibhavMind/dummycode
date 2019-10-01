package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.LogoUploadForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface LogoUploadLogic.
 */
@Transactional
public interface LogoUploadLogic {
	/**
	 * purpose : logo Upload.
	 * 
	 * @param LogoUploadForm
	 *            the logoUploadForm
	 * @param Long
	 *            the companyId
	 */
	void logoUpload(LogoUploadForm logoUploadForm, Long companyId);

	/**
	 * purpose : get Logo.
	 * 
	 * @param long the companyId
	 * @param String
	 *            the defaultImagePath
	 * @return Logo Image
	 */
	byte[] getLogo(Long companyId, String defaultImagePath);

	/**
	 * purpose : get Company List.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return LogoUploadForm contains Company List
	 */
	List<LogoUploadForm> getCompanyList(Long employeeId);

	/**
	 * purpose : get Company Logo Size.
	 * 
	 * @return Logo Size
	 */
	int getCompanyLogoSize();

	/**
	 * purpose : get Header Logo.
	 * 
	 * @param long the companyId
	 * @param String
	 *            the defaultImagePath
	 * @return Logo
	 */
	byte[] getHeaderLogo(long companyId, String defaultImagePath);

	/**
	 * purpose : get Header Logo Image Width and Height.
	 * 
	 * @param long the companyId
	 * @param String
	 *            the defaultImagePath
	 * @return Logo Image Width and Height
	 */
	String getHeaderLogoWidthHeight(long companyId, String defaultImagePath);

	byte[] getMobileHeaderCompanyLogo(long companyId, String defaultImagePath);

	String getMobileLogoWidthHeight(long companyId, String defaultImagePath);

}