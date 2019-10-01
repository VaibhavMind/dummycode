/*
 * <h4>Description</h4>
 * All system exception are wrapped to this class.
 * 
 * @author tarungupta
 */
package com.payasia.common.exception;

import java.util.Arrays;

public class PayAsiaPageNotFoundException extends PayAsiaException {

	private static final long serialVersionUID = 2763187418243954651L;

	public PayAsiaPageNotFoundException(String key, String[] args,
			String defaultMessage) {
		super(ErrorType.SYSTEM, defaultMessage);
		this.key = key;
		 if(args !=null){
			   this.errorArgs = Arrays.copyOf(args, args.length); 
		} 
		this.defaultMessage = defaultMessage;
	}

	public PayAsiaPageNotFoundException(String key) {
		super(ErrorType.SYSTEM, key);
		this.key = key;
	}

	public PayAsiaPageNotFoundException(String key, String[] args) {
		super(ErrorType.SYSTEM, key);
		this.key = key;
		 if(args !=null){
			   this.errorArgs = Arrays.copyOf(args, args.length); 
		} 
	}

	public PayAsiaPageNotFoundException(String key, String defaultMessage) {
		super(ErrorType.SYSTEM, key);
		this.key = key;
		this.defaultMessage = defaultMessage;
	}

	public PayAsiaPageNotFoundException(String key, Throwable th) {
		super(ErrorType.SYSTEM, key, th);
		this.key = key;
	}

	public PayAsiaPageNotFoundException(String key, Throwable th,
			String defaultMessage) {
		super(ErrorType.SYSTEM, key, th);
		this.key = key;
		this.defaultMessage = defaultMessage;
	}

	public PayAsiaPageNotFoundException(String key, String[] args, Throwable th,
			String defaultMessage) {
		super(ErrorType.SYSTEM, key, th);
		this.key = key;
		 if(args !=null){
			   this.errorArgs = Arrays.copyOf(args, args.length); 
		} 
		this.defaultMessage = defaultMessage;
	}
}
