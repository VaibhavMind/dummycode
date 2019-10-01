/*
 * <h4>Description</h4>
 * Used for showing custom messages on screen.
 *
 * @author tarungupta
 */
package com.payasia.web.util;

import java.util.Arrays;

public class Message {

	private String msgKey;
	private String[] msgArgs;
	private String defaultMsg = "Default Message";

	public Message(String msgKey) {
		super();
		this.msgKey = msgKey;
	}

	public Message(String msgKey, String defaultMsg) {
		super();
		this.msgKey = msgKey;
		this.defaultMsg = defaultMsg;
	}

	public Message(String msgKey, String[] msgArgs, String defaultMsg) {
		super();
		this.msgKey = msgKey;
		if (msgArgs != null) {
			this.msgArgs = Arrays.copyOf(msgArgs, msgArgs.length);
		}
		this.defaultMsg = defaultMsg;
	}

	public String getMsgKey() {
		return msgKey;
	}

	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}

	public String[] getMsgArgs() {
		return msgArgs;
	}

	public void setMsgArgs(String[] msgArgs) {
		if (msgArgs != null) {
			this.msgArgs = Arrays.copyOf(msgArgs, msgArgs.length);
		}
	}

	public String getDefaultMsg() {
		return defaultMsg;
	}

	public void setDefaultMsg(String defaultMsg) {
		this.defaultMsg = defaultMsg;
	}

}
