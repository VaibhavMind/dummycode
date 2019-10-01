package com.payasia.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

/**
 * The Class PasswordUtils.
 */
/**
 * @author vivekjain
 *
 */
public class PasswordUtils {
	
	/**
	 * Gets the random password.
	 *
	 * @return the random password
	 */
	public static String getRandomPassword() {
        StringBuffer password = new StringBuffer(20);
        int next = RandomUtils.nextInt(1) + 8;
        password.append(RandomStringUtils.randomAlphanumeric(next));
        return password.toString();
    }
	
	/**
	 * Gets the random password By Given Length.
	 *
	 * @param length the length
	 * @return the random password
	 */
	public static String getRandomPassword(int length) {
        StringBuffer password = new StringBuffer(20);
        int next = RandomUtils.nextInt(1) + length;
        password.append(RandomStringUtils.randomAlphanumeric(next));
        return password.toString();
    }
	
	/**
	 * Gets the random password with special character by Given Length.
	 *
	 * @param length the length
	 * @return the random password with special char
	 */
	public static String getRandomPasswordWithSpecialChar(int length) {
        StringBuffer password = new StringBuffer(20);
        int next = RandomUtils.nextInt(1) + length-1;
        password.append(RandomStringUtils.randomAlphanumeric(next));
        
        String specialChar= "!@_#$%^&*-?~";
        Random rand = new Random();
        password.append(specialChar.charAt( rand.nextInt(specialChar.length()) ));
        return password.toString();
    }
	
	
	/**
	 * Gets the bytes from file.
	 *
	 * @param file the file
	 * @return the bytes from file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {        
         
        long length = file.length();

         
         
         
         
        if (length > Integer.MAX_VALUE) {
             
            throw new IOException("File is too large!");
        }

         
        byte[] bytes = new byte[(int)length];

         
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

         
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        return bytes;
    }


}
