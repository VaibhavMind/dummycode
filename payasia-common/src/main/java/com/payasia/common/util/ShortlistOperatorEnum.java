package com.payasia.common.util;

public enum ShortlistOperatorEnum {
	ISLESSTHAN("<"),
	ISGREATERTHAN(">"),
	ISGREATERTHANEQUALTO(">="),
	ISLESSTHANEQUALTO("<="),
	ISNOTEQUALTO("!="),
	ISEQUALTO("="),
	ISEQUALTOSYSDATEBY(PayAsiaConstants.IS_EQUAL_TO_SYSDATE_BY),
	ISNOTEQUALTOSYSDATEBY(PayAsiaConstants.IS_NOT_EQUAL_TO_SYSDATE_BY),
	ISGREATERTHANSYSDATEBY(PayAsiaConstants.IS_GREATER_THAN_SYSDATE_BY),
	ISGREATERTHANEQUALTOSYSDATEBY(PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSDATE_BY),
	ISLESSTHANSYSDATEBY(PayAsiaConstants.IS_LESS_THAN_SYSDATE_BY),
	ISLESSTHANEQUALTOSYSDATEBY(PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSDATE_BY),
	ISEQUALTOSYSMONTHBY(PayAsiaConstants.IS_EQUAL_TO_SYSMONTH_BY),
	ISNOTEQUALTOSYSMONTHBY(PayAsiaConstants.IS_NOT_EQUAL_TO_SYSMONTH_BY),
	ISGREATERTHANSYSMONTHBY(PayAsiaConstants.IS_GREATER_THAN_SYSMONTH_BY),
	ISGREATERTHANEQUALTOSYSMONTHBY(PayAsiaConstants.IS_GREATER_THAN_EQUAL_TO_SYSMONTH_BY),
	ISLESSTHANSYSMONTHBY(PayAsiaConstants.IS_LESS_THAN_SYSMONTH_BY),
	ISLESSTHANEQUALTOSYSMONTHBY(PayAsiaConstants.IS_LESS_THAN_EQUAL_TO_SYSMONTH_BY);
	
	private final String operator;
	
	ShortlistOperatorEnum(String operator) {
		this.operator = operator;
	}

	
	public static ShortlistOperatorEnum getFromOperator(String operator) {
		for (ShortlistOperatorEnum entity : ShortlistOperatorEnum.values()) {
			if (operator.equals(entity.operator)) {
				return entity;
			}
		}
		return null;
	}
	
	public String getShortlistOperator() {
		return operator;
	}


}
