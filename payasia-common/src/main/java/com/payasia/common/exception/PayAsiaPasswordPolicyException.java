/*
 * <h4>Description</h4>
 * All business exceptions should be thrown using this class.
 *
 * @author tarungupta
 */
package com.payasia.common.exception;

import java.util.Arrays;

public class PayAsiaPasswordPolicyException extends PayAsiaException {

	private static final long serialVersionUID = -8817025373765348946L;

	public PayAsiaPasswordPolicyException(String key) {
		super(ErrorType.BUSINESS, key);
		this.key = key;
	}

	public PayAsiaPasswordPolicyException(String key, String[] args) {
		super(ErrorType.BUSINESS, key);
		this.key = key;
		 if(args !=null){
			   this.errorArgs = Arrays.copyOf(args, args.length); 
		} 
	}

	public PayAsiaPasswordPolicyException(String key, String defaultMessage) {
		super(ErrorType.BUSINESS, key);
		this.key = key;
		this.defaultMessage = defaultMessage;
	}

	public PayAsiaPasswordPolicyException(String key, String[] args,
			String defaultMessage) {
		super(ErrorType.BUSINESS, defaultMessage);
		this.key = key;
		 if(args !=null){
			   this.errorArgs = Arrays.copyOf(args, args.length); 
		} 
		this.defaultMessage = defaultMessage;
	}
}
