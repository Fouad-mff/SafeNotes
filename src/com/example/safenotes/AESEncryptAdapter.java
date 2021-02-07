package com.example.safenotes;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptAdapter {
	
	   private static String KEY = null;
	   			// Cl� de chiffrement/d�chiffrement.
	   
	   public void key(String hash){
		   		// D�finir le hash comme cl� de chiffrement/d�chiffrement.
		   AESEncryptAdapter.KEY=hash;
	   }

       public static String encrypt(String clearString) throws Exception {
    	   		// Convertir la chaine de caract�res en octets, la chiffrer puis la retourner en hexad�cimal.
           byte[] rawKey = getRawKey(KEY.getBytes());
           byte[] result = encrypt(rawKey, clearString.getBytes());
           return convertToHex(result);
       }
      
       public static String decrypt(String encryptedString) throws Exception {
    	   		// Convertir le hexad�cimal en octets, le d�chiffrer et retourner en chaine de caract�res.
           byte[] rawKey = getRawKey(KEY.getBytes());
           byte[] enc = toByte(encryptedString);
           byte[] result = decrypt(rawKey, enc);
           return new String(result);
       }

       private static byte[] getRawKey(byte[] seed) throws Exception {
    	   		/* Transformer la cl� initiale en cl� cod�e apr�s l'initialisation par
    	   		 * le g�n�rateur de cl� (keyGenerator) en utilisant une cl� de 128 bits
    	   		 * (10 tours) et un al�atoire (random) bas� sur la cl� initiale (primaire).*/
           KeyGenerator kgen = KeyGenerator.getInstance("AES");
           SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
           sr.setSeed(seed);
           kgen.init(128, sr);
           SecretKey skey = kgen.generateKey();
           byte[] raw = skey.getEncoded();
           return raw;
       }
      
       private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
    	   		// Chiffrement avec l'algorithme de cryptage "AES".
           SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
           Cipher cipher = Cipher.getInstance("AES");
           cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
           byte[] encrypted = cipher.doFinal(clear);
               return encrypted;
       }

       private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
    	   		// D�chiffrement avec l'algorithme de decryptage "AES".
           SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
           Cipher cipher = Cipher.getInstance("AES");
           cipher.init(Cipher.DECRYPT_MODE, skeySpec);
           byte[] decrypted = cipher.doFinal(encrypted);
               return decrypted;
       }
       
       private static byte[] toByte(String hexString) {
    	   		// Convertir le hexad�cimal en octets.
               int len = hexString.length()/2;
               byte[] result = new byte[len];
               for (int i = 0; i < len; i++)
                       result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
               return result;
       }
       
       private static String convertToHex(byte[] data) {
    	   		// Convertir l'ensemble d'octets en hexad�cimal.
           StringBuilder buf = new StringBuilder();
           for (byte b : data) {
               int halfbyte = (b >>> 4) & 0x0F;
               int two_halfs = 0;
               do {
                   buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) 
                		   		: (char) ('a' + (halfbyte - 10)));
                   halfbyte = b & 0x0F;
               } while (two_halfs++ < 1);
           }
           return buf.toString();
       }
}
