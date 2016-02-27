package com.irpulse.Utilities;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Base64;

public class Encryption {
	private static String charset = "UTF-8";

	public static String getAESkey() {
		return "level_idmaxcount";
	}

	public static String encryptAES(String plainText , String key) {
		byte[] encryptedTextBytes = null;
		try {
			byte[] keyBytes =key.getBytes(charset);
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			encryptedTextBytes = cipher.doFinal(plainText.getBytes(charset));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Base64.encodeToString(encryptedTextBytes, Base64.DEFAULT);
		
	}

	public static String decryptAES(String encryptedText , String key) {
		byte[] decryptedTextBytes = null;
		try {
			byte[] keyBytes = key.getBytes(charset);
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
			
			byte[] encryptedTextBytes = Base64.decode(encryptedText, Base64.DEFAULT);
		
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(decryptedTextBytes);
	}
}