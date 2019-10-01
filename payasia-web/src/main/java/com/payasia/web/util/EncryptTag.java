package com.payasia.web.util;

public class EncryptTag{ /*extends SimpleTagSupport {

	private String value;

	public EncryptTag(){
		
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void doTag() throws JspException, IOException {
		try {
			if(value != null && !value.isEmpty()){
				Long encryptValue = FormatPreserveCryptoUtil.encrypt(Long.valueOf(value));
				getJspContext().getOut().write(encryptValue.toString());
			} else {
				throw new SkipPageException("Encryption Value Cannot Be Null");
			}			
		} catch (Exception e) {
			throw new SkipPageException("Exception In Encrypting");
		}
	}*/
}
