package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.ExternalLinkForm;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface ExternalLinkLogic.
 */
@Transactional
public interface ExternalLinkLogic {

	/**
	 * purpose : Save ExternalLink data.
	 * 
	 * @param ExternalLinkForm
	 *            the externalLinkForm
	 * @param Long
	 *            the companyId
	 * @return Response
	 */
	String saveExternalLink(ExternalLinkForm externalLinkForm, Long companyId);

	/**
	 * purpose : get ExternalLink Data.
	 * 
	 * @param Long
	 *            the companyId
	 * @return ExternalLinkForm
	 */
	ExternalLinkForm getExternalLink(Long companyId);

	/**
	 * purpose : get ExternalLink Image.
	 * 
	 * @param long the companyId
	 * @param String
	 *            the defaultImagePath
	 * @return Image in byte array
	 */
	byte[] getExternalImage(long companyId, String defaultImagePath);

	/**
	 * purpose : get ExternalLink Image Size.
	 * 
	 * @return Image Size
	 */
	int getExtLinkImageSize();

	/**
	 * purpose : Get ExternalLink Home Page Image.
	 * 
	 * @param long the companyId
	 * @param String
	 *            the defaultImagePath
	 * @param int the imageWidth
	 * @param int the imageHeight
	 * @return Image in byte array
	 */
	byte[] getHomePageImage(long companyId, String defaultImagePath,
			int imageWidth, int imageHeight);

}
