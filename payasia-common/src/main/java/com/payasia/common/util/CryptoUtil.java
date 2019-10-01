package com.payasia.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


public class CryptoUtil {

	private static final String ENCRYT_DYCRYT_ALGO_MODE_PADDING = "AES/ECB/PKCS5Padding"; // AES/ECB/PKCS5Padding it specify <algorithm>/<mode>/padding
	private static final String ENCRYT_DYCRYT_ALGO = "AES";
	public static String SECRET_KEY = "payasia#project#";//PROVIDE 16 CHARACTER KEY AS 128 bit -> 16 bytes -> 16 char (as 1char = 8bit)

	
	public static String encrypt(String plainText, String secretKey) {
		if(plainText != null && !plainText.isEmpty()){
			try {
				Cipher cipher = Cipher.getInstance(ENCRYT_DYCRYT_ALGO_MODE_PADDING);
	            final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), ENCRYT_DYCRYT_ALGO);
	            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
	            byte[] cipherBytes = cipher.doFinal(plainText.getBytes());
	            final String encryptedString = Base64.encodeBase64URLSafeString(cipherBytes);
	            return encryptedString;
	        } catch (Exception e) {
	           e.printStackTrace();
	        }
		}		
        return null;
	}
	
	public static String decrypt(String cipherText, String secretKey) {
		if(cipherText != null && !cipherText.isEmpty()){
			try {
	        	Cipher cipher = Cipher.getInstance(ENCRYT_DYCRYT_ALGO_MODE_PADDING);
	            final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), ENCRYT_DYCRYT_ALGO);
	            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
	            final String decryptedString = new String(cipher.doFinal(Base64.decodeBase64(cipherText.getBytes())));
	            return decryptedString;
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
		}        
        return null;
    }
	
	public static Long convertStringAsLong(String cipherText ){
		String decryptString =  decrypt(cipherText, SECRET_KEY);
		return Long.valueOf(decryptString);	
	}
	

	public static void main(String... strings){
		
		String plainText = "314917";
		System.out.println("PLAIN TEXT : "+plainText);
		String ciperText = encrypt(plainText, SECRET_KEY);
		System.out.println("CIPHER TEXT : "+ciperText);
		plainText = decrypt(ciperText, SECRET_KEY);
		System.out.println("PLAIN TEXT : "+plainText);
		
		/*long a = System.currentTimeMillis();
		System.out.println("IN" + a);
		for(long i=1l; i<=1000000l;i++){
			String plainText = String.valueOf(i);
		
			String ciperText = encrypt(plainText, SECRET_KEY);
			 //String ciperText = encode(plainText);
			System.out.println("CIPHER TEXT : "+ciperText);
			
			plainText = decrypt(ciperText, SECRET_KEY);
			System.out.println("PLAIN TEXT : "+plainText);
		}
		System.out.println("Result" + (System.currentTimeMillis()-a));*/
				
	}
}