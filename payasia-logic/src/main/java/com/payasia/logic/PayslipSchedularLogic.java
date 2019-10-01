package com.payasia.logic;

import java.util.HashMap;

import com.payasia.common.dto.PrintTokenDTO;

public interface PayslipSchedularLogic {

	void checkPayslipTokens(HashMap<String, PrintTokenDTO> printTokenMap,
			Integer printTokenDeleteTime);

}
