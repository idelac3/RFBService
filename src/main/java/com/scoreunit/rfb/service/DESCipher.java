package com.scoreunit.rfb.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains some methods to validate if VNC client sent proper DES encrypted
 * challenge response data.
 * <p>
 * Note that VNC password bits should be reversed, before use with Java DES algo.
 * <p>
 * Ref.
 *  http://www.avajava.com/tutorials/lessons/how-do-i-encrypt-and-decrypt-files-using-des.html
 *  
 * @author igor.delac@gmail.com
 *
 */
class DESCipher {

	public final static Logger log = LoggerFactory.getLogger(DESCipher.class);
	
	/**
	 * Perform encoding of original data.
	 * 
	 * @param key		-	secret key
	 * @param in		-	input stream with orig. data
	 * @param out		-	output stream where to write encrypted data
	 */
	public static void enc(final String key
			, final InputStream in
			, final OutputStream out) {
		
		try {
			
			final SecretKey desKey = buildVNCAuthKey(key);
			final Cipher cipher = Cipher.getInstance("DES"); // DESede/CBC/NoPadding
			
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			final CipherInputStream cipherIn = new CipherInputStream(in, cipher);
			
			doCopy(cipherIn, out);
		} catch (final Exception ex) {
			
			log.error("DES encryption failed.", ex);
		}
	}
	
	/**
	 * Perform decoding of DES data.
	 * 
	 * @param key		-	secret key
	 * @param in		-	input stream with original encrypted data
	 * @param out		-	output stream where to write decoded data
	 */
	public static void dec(final String key
			, final InputStream in
			, final OutputStream out) {
		
		try {
			
			final SecretKey desKey = buildVNCAuthKey(key);
			final Cipher cipher = Cipher.getInstance("DES"); // DES/ECB/PKCS5Padding for SunJCE
			
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			final CipherOutputStream cipherOut = new CipherOutputStream(out, cipher);
			
			doCopy(in, cipherOut);
		} catch (final Exception ex) {
			
			log.error("DES encryption failed.", ex);
		}
	}
	
	/**
	 * Encrypt data using DES.
	 * 
	 * @param key	-	secret password
	 * @param data	-	original data
	 * 
	 * @return	encrypted data
	 */
	public static byte[] enc(final String key
			, final byte[] data) {
	
		final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		final ByteArrayInputStream in = new ByteArrayInputStream(data);
		
		enc(key, in, bOut);
		
		return bOut.toByteArray();
	}
	
	/**
	 * Perform decoding of DES data.
	 * 
	 * @param key		-	a password string
	 * @param encrypted	-	encrypted data
	 * 
	 * @return	original data
	 */
	public static byte[] dec(final String key
			, final byte[] encrypted) {
	
		final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		final ByteArrayInputStream in = new ByteArrayInputStream(encrypted);
		
		dec(key, in, bOut);
		
		return bOut.toByteArray();
	}
	
	/**
	 * Create {@link SecretKey} object for encryption.
	 * <p>
	 * This is special case, for VNC auth., where password string
	 * bytes must be reversed first.
	 * <p>
	 * Ref.
	 *    https://www.vidarholen.net/contents/junk/vnc.html
	 *    
	 * @param password			-	password string which VNC client must enter
	 * 
	 * @return {@link SecretKey} object
	 * 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static SecretKey buildVNCAuthKey(final String password) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		byte[] key = password.getBytes();
		
		for (int i = 0 ; i < key.length ; i++) {
			
			key[i] = reverseByte(key[i]);
		}
		
		final DESKeySpec dks = new DESKeySpec(key);
		final SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		final SecretKey desKey = skf.generateSecret(dks);
		
		return desKey;		
	}
	
	/**
	 * Reverse bits in a byte value.
	 * <p>
	 * Ref.
	 *  https://stackoverflow.com/questions/31725733/reverse-byte-in-java
	 *  
	 * @param b	-	input byte value
	 * 
	 * @return	reversed byte value
	 */
    public static byte reverseByte(byte b) {
    	
        int bi = 0xFF & b, res = 0, count = 8;
        for ( ; bi != 0; count--, bi >>>= 1)
            res = (res << 1) | (bi & 1);

        res <<= count;
        
        return (byte) (0xFF & res);
    }

	/**
	 * Copy bytes from {@link InputStream} to {@link OutputStream}.
	 * 
	 * @param in		-		input stream
	 * @param out		-		output stream
	 * 
	 * @throws IOException	if IO operation fails
	 */
	public static void doCopy(final InputStream in, final OutputStream out) throws IOException {
		
		final byte[] bytes = new byte[64];
		
		int numBytes;
		
		while ((numBytes = in.read(bytes)) != -1) {
			
			out.write(bytes, 0, numBytes);
		}
		
		out.flush();
		out.close();
		in.close();
	}

}
