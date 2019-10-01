package com.payasia.logic.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.payasia.common.dto.PrintTokenDTO;
import com.payasia.logic.PayslipSchedularLogic;

@Component
public class PayslipSchedularLogicImpl implements PayslipSchedularLogic {

	@Override
	public void checkPayslipTokens(
			HashMap<String, PrintTokenDTO> printTokenMap,
			Integer printTokenDeleteTime) {

		 
		if (printTokenMap == null) {
			return;
		}

		for (Map.Entry<String, PrintTokenDTO> entry : printTokenMap.entrySet()) {
			PrintTokenDTO printTokenDTO = entry.getValue();

			Calendar cal = Calendar.getInstance();  
			cal.setTime(printTokenDTO.getTokenTime());  
			cal.add(Calendar.HOUR_OF_DAY, printTokenDeleteTime);  
																	 
			Date validTokenTime = cal.getTime();
			if (new Date().compareTo(validTokenTime) > 0) {
				printTokenMap.remove(entry.getKey());
			}

		}

	}

}
