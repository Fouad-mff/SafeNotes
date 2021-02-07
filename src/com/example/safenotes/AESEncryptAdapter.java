package com.example.safenotes;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptAdapter {
	
	   private static String KEY = null;
	   			// Clé de chiffrement/déchiffrement.
	   
	   public void key(String hash){
		   		// Définir le hash comme clé de chiffrement/déchiffrement.
		   AESEncryptAdapter.KEY=hash;
	   }

       public static String encrypt(String clearString) throws Exception {
    	   		// Convertir la chaine de caractères en octets, la chiffrer puis la retourner en hexadécimal.
           byte[] rawKey = getRawKey(KEY.getBytes());
           byte[] result = encrypt(rawKey, clearString.getBytes());
           return convertToHex(result);
       }
      
       public static String decrypt(String encryptedString) throws Exception {
    	   		// Convertir le hexadécimal en octets, le déchiffrer et retourner en chaine de caractères.
           byte[] rawKey = getRawKey(KEY.getBytes());
           byte[] enc = toByte(encryptedString);
           byte[] result = decrypt(rawKey, enc);
           return new String(result);
       }

       private static byte[] getRawKey(byte[] seed) throws Exception {
    	   		/* Transformer la clé initiale en clé codée après l'initialisation par
    	   		 * le générateur de clé (keyGenerator) en utilisant une clé de 128 bits
    	   		 * (10 tours) et un aléatoire (random) basé sur la clé initiale (primaire).*/
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
    	   		// Déchiffrement avec l'algorithme de decryptage "AES".
           SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
           Cipher cipher = Cipher.getInstance("AES");
           cipher.init(Cipher.DECRYPT_MODE, skeySpec);
           byte[] decrypted = cipher.doFinal(encrypted);
               return decrypted;
       }
       
       private static byte[] toByte(String hexString) {
    	   		// Convertir le hexadécimal en octets.
               int len = hexString.length()/2;
               byte[] result = new byte[len];
               for (int i = 0; i < len; i++)
                       result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
               return result;
       }
       
       private static String convertToHex(byte[] data) {
    	   		// Convertir l'ensemble d'octets en hexadécimal.
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
