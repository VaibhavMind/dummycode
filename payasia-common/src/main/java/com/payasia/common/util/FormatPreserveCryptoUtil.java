package com.payasia.common.util;

import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.payasia.common.bean.util.UserContext;

@Component
public class FormatPreserveCryptoUtil {
		
	// A key, that will be used with the HMAC(SHA256) algorithm, note that this
	private static final byte[] HMAC_KEY = new byte[] { 0x10, 0x20, 0x10, 0x20, 0x10, 0x20, 0x10, 0x20 };
	//An initialisation vector, or tweak, used in the algorithm.
	private static final byte[] TWEAK = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
	
	public static Long encrypt(Long plainValue) {
		
		try {
			
			if(plainValue==null || UserContext.getKey()==null || StringUtils.isEmpty(UserContext.getKey())){
				return plainValue;
			}
			BigInteger modulus = new BigInteger(UserContext.getKey(), 10);
			BigInteger value = new BigInteger(plainValue.toString(),10);
			return FormatPreservingEncryption.encrypt(modulus,value,
					FormatPreserveCryptoUtil.HMAC_KEY, FormatPreserveCryptoUtil.TWEAK).longValue();
		} catch (FPEException e) {
			throw new IllegalArgumentException("plaintext must not be null.");
		}
	}

	public static Long decrypt(Long encryptedValue) {
	
		try {

			if (encryptedValue == null || UserContext.getKey() == null || StringUtils.isEmpty(UserContext.getKey())) {
				return encryptedValue;
			}

			BigInteger modulus = new BigInteger(UserContext.getKey(), 10);
			BigInteger value = new BigInteger(encryptedValue.toString(), 10);
			return FormatPreservingEncryption.decrypt(modulus, value, FormatPreserveCryptoUtil.HMAC_KEY, FormatPreserveCryptoUtil.TWEAK).longValue();
		} catch (FPEException e) {
			throw new IllegalArgumentException("encryptedValue must not be null.");
		}

	}
}