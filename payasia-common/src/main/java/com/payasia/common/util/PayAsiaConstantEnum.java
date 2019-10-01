package com.payasia.common.util;

public class PayAsiaConstantEnum {

	public enum LoginMode{
		PORTAL("New-Portal"), SSO("SSO");
		
		private final String mode;
		LoginMode(String mode){
			this.mode = mode;
		}
		public String getMode() {
			return mode;
		}
		
	}
}
