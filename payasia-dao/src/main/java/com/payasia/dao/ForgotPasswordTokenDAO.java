package com.payasia.dao;

import com.payasia.dao.bean.ForgotPasswordToken;

public interface ForgotPasswordTokenDAO {

	void update(ForgotPasswordToken forgotPasswordToken);

	void save(ForgotPasswordToken forgotPasswordToken);

	void delete(ForgotPasswordToken forgotPasswordToken);

	ForgotPasswordToken findByID(long forgotPasswordTokenId);

	void updateByCondition();

	ForgotPasswordToken findByCondition(String token, Long employeeId,
			Long companyId, boolean active);

}
