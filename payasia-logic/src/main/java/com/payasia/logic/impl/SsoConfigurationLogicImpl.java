package com.payasia.logic.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.SsoConfigurationDTO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.SsoConfigurationDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.SsoConfiguration;
import com.payasia.logic.SsoConfigurationLogic;

@Component
public class SsoConfigurationLogicImpl implements SsoConfigurationLogic {

	private static final Logger LOGGER = Logger.getLogger(SsoConfigurationLogicImpl.class);

	@Resource
	SsoConfigurationDAO ssoConfigurationDAO;

	@Resource
	CompanyDAO companyDAO;
	
	@Autowired
	private EmployeeDAO employeeDAO;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private EmployeeLoginDetailDAO employeeLoginDetailDAO;
    

	@Override
	public SsoConfigurationDTO getSsoConfigurationForComapny(Long companyId) {
		SsoConfigurationDTO ssoConfigurationDTO = new SsoConfigurationDTO();

		ssoConfigurationDTO.setSamlSsoUrl(messageSource.getMessage("payasia.saml.sso.url", new Object[] {}, null));
		ssoConfigurationDTO
				.setSamlSpEntityId(messageSource.getMessage("payasia.saml.sp.entity.id", new Object[] {}, null));
		if (companyId != null) {
			SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyId(companyId);
			if (ssoConfiguration != null) {
				ssoConfigurationDTO.setIsEnableSso(
						ssoConfiguration.getIsEnableSso() == null ? false : ssoConfiguration.getIsEnableSso());
				ssoConfigurationDTO.setCompanyId(ssoConfiguration.getCompany().getCompanyId());
				ssoConfigurationDTO.setIdpSsoUrl(ssoConfiguration.getIdpssoUrl());
				ssoConfigurationDTO.setIdpIssuer(ssoConfiguration.getIdpIssuer());
				ssoConfigurationDTO.setMetaData(ssoConfiguration.getIdpMetadata());
				ssoConfigurationDTO.setMetadataUrl(ssoConfiguration.getIdpmetadataUrl());
			}
		}
		return ssoConfigurationDTO;
	}

	@Override
	public void saveSsoConfiguration(SsoConfigurationDTO ssoConfigurationDTO) {

		SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyId(ssoConfigurationDTO.getCompanyId());
		if (ssoConfiguration != null) {

			try {
				ssoConfiguration.setIsEnableSso(ssoConfigurationDTO.getIsEnableSso());
				ssoConfiguration.setIdpssoUrl(ssoConfigurationDTO.getIdpSsoUrl());
				ssoConfiguration.setIdpIssuer(ssoConfigurationDTO.getIdpIssuer());
				ssoConfiguration.setIdpMetadata(ssoConfigurationDTO.getMetaData());
				ssoConfiguration.setIdpmetadataUrl(ssoConfigurationDTO.getMetadataUrl());
				ssoConfigurationDAO.update(ssoConfiguration);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}

		} else {
			// companyDAO.findById(ssoConfigurationDTO.getCompanyId());
			try {
				ssoConfiguration = new SsoConfiguration();
				ssoConfiguration.setCompany(companyDAO.findById(ssoConfigurationDTO.getCompanyId()));
				ssoConfiguration.setIdpssoUrl(ssoConfigurationDTO.getIdpSsoUrl());
				ssoConfiguration.setIdpIssuer(ssoConfigurationDTO.getIdpIssuer());
				ssoConfiguration.setIdpMetadata(ssoConfigurationDTO.getMetaData());
				ssoConfiguration.setIdpmetadataUrl(ssoConfigurationDTO.getMetadataUrl());
				ssoConfiguration.setIsEnableSso(ssoConfigurationDTO.getIsEnableSso());
				ssoConfigurationDAO.save(ssoConfiguration);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}

		}
	}
	
	@Override
	public SsoConfigurationDTO getSsoConfigByCompCodeWithGroup(String companyCode) {
		SsoConfigurationDTO ssoConfigurationDTO = new SsoConfigurationDTO();

			SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyCodeWithGroup(companyCode);
			if (ssoConfiguration != null) {
				ssoConfigurationDTO.setIdpIssuer(ssoConfiguration.getIdpIssuer());
				ssoConfigurationDTO.setIsEnableSso(ssoConfiguration.getIsEnableSso());
		   }else{
			ssoConfigurationDTO.setIsEnableSso(false);
		}
		return ssoConfigurationDTO;
	}

	/**
	 * Get User Detail by Email-Id
	 */
	@Override
	public List<Employee> getUserByEmail(String email, String companyCode){
		
		return employeeDAO.getEmployeeByEmailWithCompanyCode(email, companyCode);
	}
	
	@Override
	public EmployeeLoginDetail getUserLoginByEmployeeID(long employeeID){
	 return  employeeLoginDetailDAO.findByEmployeeId(employeeID);
		
	}
}
