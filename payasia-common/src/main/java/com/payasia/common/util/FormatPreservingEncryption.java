package com.payasia.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Format Preserving Encryption using the scheme FE1 from the paper "Format-Preserving Encryption" by Bellare, Rogaway, et al (http://eprint.iacr.org/2009/251).
 * Ported from DotFPE (https://dotfpe.codeplex.com/); which was ported from Botan Library Version 1.10.3 (http://botan.randombit.net).
 * <p>
 * Methods on this class are not thread-safe.
 */
public class FormatPreservingEncryption {

	/**
	 * Instantiates a new FE1 instance.
	 */
	public FormatPreservingEncryption() {
	}

	/**
	 * A simple round function based on HMAC(SHA-256).
	 */
	private static class FPEEncryptor {

		/**
		 * Using a HMAC over SHA256 for the HMAC algorithm. This must be available on the JVM in order to work.
		 */
		private static final String HMAC_ALGORITHM = "HmacSHA256";

		private byte[] macNT;

		private Mac macGenerator;

		/**
		 * Initialise a new encryptor.
		 * <p>
		 * Requires the availability of the {@value FPEEncryptor#HMAC_ALGORITHM} MAC implementation via JCA.
		 * <p>
		 * Ultimately this initialises {@link #macNT} by creating a byte array with:
		 * <ol>
		 * <li>Writing the length of the encoded value of the modulusBI.</li>
		 * <li>Writing the encoded value of the modulusBI.</li>
		 * <li>Writing the length of the tweak.</li>
		 * <li>Writing the tweak.</li>
		 * </ol>
		 * And then setting {@link #macNT} to the HMAC'd value of this byte array.
		 *
		 * @param key the key to initiate the HMAC with SHA256 generator with (must be valid for this algorithm).
		 * @param modulus the range of the output numbers.
		 * @param tweak an initialisation vector (IV) that will be used in the initialisation of the HMAC generator. Must be non-null and length &gt; 0.
		 * @throws FPEException if either the HMAC algorithm or the key is incompatible with the HMAC.
		 */
		private FPEEncryptor(byte[] key, BigInteger modulus, byte[] tweak) throws FPEException {
			try {
				this.macGenerator = Mac.getInstance(HMAC_ALGORITHM);
				this.macGenerator.init(new SecretKeySpec(key, HMAC_ALGORITHM));
			} catch (NoSuchAlgorithmException e) {
				// This should never happen as HMAC/SHA-2 are built in to the JVM.
				throw new FPEException(HMAC_ALGORITHM + " is not a valid MAC algorithm on this JVM", e);
			} catch (InvalidKeyException e) {
				// Outer class checks that key is more than 1 byte, so don't think this can happen, included for completeness though.
				throw new FPEException("The key passed was not valid for use with " + HMAC_ALGORITHM, e);
			}

			if (tweak == null || tweak.length == 0) {
				throw new IllegalArgumentException("tweak (IV) must be an array of length > 0");
			}

			byte[] encodedModulus = Utility.encode(modulus);

			if (encodedModulus.length > MAX_N_BYTES) {
				throw new IllegalArgumentException("Size of encoded n is too large for FPE encryption (was " + encodedModulus.length + " bytes, max permitted "
								+ MAX_N_BYTES + ")");
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				baos.write(Utility.toBEBytes(encodedModulus.length));
				baos.write(encodedModulus);

				baos.write(Utility.toBEBytes(tweak.length));
				baos.write(tweak);

				// Flushing most likely a no-op, but best be sure.
				baos.flush();
			} catch (IOException e) {
				// Can't imagine why this would ever happen!
				throw new FPEException("Unable to write to byte array output stream!", e);
			}
			this.macNT = this.macGenerator.doFinal(baos.toByteArray());
		}

		/**
		 * Mixes the round number, the input value r and the previously calculated {@link #macNT} in to a new value. Calling this repeatedly on the value of r
		 * with a new round number applies a reversible encryption to the value.
		 * <p>
		 * Encryption works as follows:
		 * <ol>
		 * <li>Serialise r to a minimal byte array with no leading zero bytes</li>
		 * <li>Create a byte array consisting of:
		 * <ol type="i">
		 * <li>macNT value</li>
		 * <li>the round number</li>
		 * <li>the length of the serialised value of r (32-bit)</li>
		 * <li>the value of serialised r</li>
		 * </ol>
		 * </li>
		 * <li>Create an HMAC-SHA256 value of the output array and convert back to a BigInteger, this is the encrypted r.
		 * </ol>
		 *
		 * @param roundNo to ensure that value is changed in a different way each time, increase this for each time you call the method on the same value.
		 * @param r the number that we are using as input to the function.
		 * @return a new BigInteger value that has reversibly encrypted r.
		 * @throws FPEException if any problems occur whilst writing to an internal {@link ByteArrayOutputStream}. This should never happen.
		 */
		BigInteger f(int roundNo, BigInteger r) throws FPEException {
			byte[] rBin = Utility.encode(r);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				baos.write(this.macNT);
				baos.write(Utility.toBEBytes(roundNo));

				baos.write(Utility.toBEBytes(rBin.length));
				baos.write(rBin);
			} catch (IOException e) {
				throw new FPEException("Unable to write to internal byte array, this should never happen so indicates a defect in the code", e);
			}

			byte[] x = this.macGenerator.doFinal(baos.toByteArray());
			byte[] positiveX = new byte[x.length + 1];
			System.arraycopy(x, 0, positiveX, 1, x.length);
			// First byte will always be 0 (default value) so BigInteger will always be positive.
			BigInteger ret = new BigInteger(positiveX);
			return ret;
		}
	}

	/**
	 * Normally FPE is for SSNs, CC#s, etc.; so limit modulus to 128-bit numbers.
	 */
	private static final int MAX_N_BYTES = 128 / 8;

	/**
	 * Generic Z_n FPE decryption, FE1 scheme.
	 * 
	 * @param modulus Use to determine the range of the numbers. Example, if the numbers range from 0 to 999, use "1000" here.
	 * @param ciphertext The number to decrypt. Must be &lt;= modulus.
	 * @param key Secret key, must be compatible with HMAC(SHA256).
	 * @param tweak Non-secret parameter, think of it as an IV - use the same one used to encrypt. Must be non-null and non-zero length.
	 * @return The decrypted number
	 * @throws FPEException if the passed data is too large to decrypt.
	 * @throws IllegalArgumentException if any of the parameters are invalid.
	 */
	public static BigInteger decrypt(final BigInteger modulus, final BigInteger ciphertext, final byte[] key, final byte[] tweak) throws FPEException {
		if (modulus == null) {
			throw new IllegalArgumentException("modulus must not be null.");
		}
		if (ciphertext == null) {
			throw new IllegalArgumentException("ciphertext must not be null.");
		}

		if (ciphertext.compareTo(modulus) >= 0) {
			throw new IllegalArgumentException("Cannot decrypt a number bigger than the modulus (otherwise this wouldn't be format preserving encryption");
		}

		FPEEncryptor encryptor = new FPEEncryptor(key, modulus, tweak);

		BigInteger[] factors = NumberTheory.factor(modulus);
		BigInteger firstFactor = factors[0];
		BigInteger secondFactor = factors[1];

		int rounds = getNumberOfRounds(firstFactor, secondFactor);

		/*
		 * x starts as the ciphertext value and will be modified by several rounds of encryption in the rest of this method before arriving back at the
		 * plaintext value, which is returned.
		 */
		BigInteger x = ciphertext;

		/*
		 * Apply the same algorithm repeatedly on x for the number of rounds given by getNumberOfRounds. Each round increases the security. Note that you must
		 * use EXACTLY the same number of rounds to decrypt as you did to encrypt! As the round number is used in the calculation, we count down rather than up.
		 */
		for (int round = rounds - 1; round >= 0; round--) {
			/*
			 * Effectively reversing the calculation in encrypt for this.
			 */
			BigInteger w = x.mod(firstFactor);
			BigInteger right = x.divide(firstFactor);

			BigInteger left = w.subtract(encryptor.f(round, right)).mod(firstFactor);
			x = secondFactor.multiply(left).add(right);
		}

		return x;
	}

	/**
	 * Generic Z_n FPE encryption using the FE1 scheme.
	 *
	 * @param modulus Use to determine the range of the numbers. Example, if the numbers range from 0 to 999, use "1000" here. Must not be null.
	 * @param plaintext The number to encrypt. Must not be null.
	 * @param key Secret key to encrypt with. Must be compatible with HMAC(SHA256).  See https://tools.ietf.org/html/rfc2104#section-3 for recommendations,
	 * on key length and generation.  Anything over 64 bytes is hashed to a 64 byte value, 32 bytes is generally considered "good enough" for most applications.
	 * @param tweak Non-secret parameter, think of it as an initialisation vector. Must be non-null and at least 1 byte long, no upper limit.
	 * @return the encrypted version of <code>plaintext</code>.
	 * @throws FPEException if encryption was not possible.
	 * @throws IllegalArgumentException if any of the parameters are invalid.
	 */
	public static BigInteger encrypt(final BigInteger modulus, final BigInteger plaintext, final byte[] key, final byte[] tweak) throws FPEException {
		if (modulus == null) {
			throw new IllegalArgumentException("modulus must not be null.");
		}
		if (plaintext == null) {
			throw new IllegalArgumentException("plaintext must not be null.");
		}

		if (plaintext.compareTo(modulus) >= 0) {
			throw new IllegalArgumentException("Cannot encrypt a number bigger than the modulus (otherwise this wouldn't be format preserving encryption");
		}
		
		FPEEncryptor encryptor = new FPEEncryptor(key, modulus, tweak);

		BigInteger[] factors = NumberTheory.factor(modulus);
		BigInteger firstFactor = factors[0];
		BigInteger secondFactor = factors[1];

		int rounds = getNumberOfRounds(firstFactor, secondFactor);

		/*
		 * x starts as the plaintext value and will be modified by several rounds of encryption in the rest of this method before being returned.
		 */
		BigInteger x = plaintext;

		/*
		 * Apply the same algorithm repeatedly on x for the number of rounds given by getNumberOfRounds. Each round increases the security. Note that the
		 * attribute and method names used align to the paper on FE1, not Java conventions on readability.
		 */
		for (int round = 0; round != rounds; round++) {
			/*
			 * Split the value of x in to left and right values (think splitting the binary in to two halves), around the second (smaller) factor
			 */
			BigInteger left = x.divide(secondFactor);
			BigInteger right = x.mod(secondFactor);

			// Recalculate x as firstFactor * right + (left + F(round, right) % firstFactor)
			BigInteger w = left.add(encryptor.f(round, right)).mod(firstFactor);
			x = firstFactor.multiply(right).add(w);
		}

		return x;
	}

	/**
	 * According to <a href="http://eprint.iacr.org/2009/251.pdf">FPE paper by Rogaway, Bellare, etc.</a>, the minimum safe number of rounds to use for FPE is
	 * 2+log_a(b). If a &gt;= b then log_a(b)&lt;= 1 so 3 rounds is safe. The FPE factorization routine should always return a &gt;= b, so just confirm that and
	 * return 3.
	 *
	 * @param a first number output from {@link NumberTheory#factor(BigInteger)}
	 * @param b second number output from {@link NumberTheory#factor(BigInteger)}
	 * @return Always returns the value 3
	 * @throws FPEException FPE rounds: a &lt; b
	 */
	private static int getNumberOfRounds(BigInteger a, BigInteger b) throws FPEException {
		if (a.compareTo(b) == -1) {
			throw new FPEException("FPE rounds: a < b");
		}
		return 3;
	}
}
