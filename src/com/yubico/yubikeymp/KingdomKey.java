package com.yubico.yubikeymp;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.google.appengine.api.datastore.Blob;

/**
 * This class holds key to the kingdom.<br/>
 * <br/>
 * <i>The secrets stored in datastore are encrypted with the KingdomKey.KEY.</i>
 * 
 * @author Vlastimil Ovčáčík
 */
public final class KingdomKey {
    /**
     * Encoding used in string operations.
     */
    public static final String ENCODING = "UTF-8";
    
    /**
     * Encryption key. If not set its value is null. If set its value is non-empty byte array.
     */
    private static transient byte[] KEY = null;
    private static final String ALGORITHM = "AES"; // TODO best cipher?
    private static final String MODE = "ECB";  //TODO best mode?
    private static final String PADDING = "PKCS5Padding"; //TODO best padding?
    private static final Logger log = Logger.getLogger(KingdomKey.class.getName());

    // TODO null byte arrays after use
    // TODO use 256 bit key
    // TODO run for 14 rounds
    // TODO use IV!!! test this - sha hashes should be different
    /**
     * Constructor.
     */
    private KingdomKey() {
        super();
    }

    /**
     * If key is not set (i.e. key is null), the byte value of <code>unencrypted</code> parameter will be assigned to
     * KingdomKey.KEY.<br/>
     * The <code>unencrypted</code> string is transformed to bytes using KingdomKey.ENCODING encoding.
     * 
     * @param unencrypted
     *            non-empty string in KingdomKey.ENCODING encoding
     * @return true if new key was set otherwise false
     */
    public static boolean setKey(String unencrypted) {
        byte[] key = null;
        try {
            key = unencrypted.getBytes(KingdomKey.ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.severe("Yubikey: " + KingdomKey.ENCODING + " encoding is not supported. Exception occurred while converting a string to byte array.");
            e.printStackTrace();
        }
        return setKey(key);
    }

    /**
     * If key is not set (i.e. key is null), the value of <code>key</code> parameter will be assigned to KingdomKey.KEY.
     * 
     * @param key
     *            non-empty byte array containing clear-text key to the kingdom.
     * @return true if new key was set, otherwise false.
     */
    public static boolean setKey(byte[] key) {
        if (KingdomKey.KEY == null && key != null && key.length > 0) {
            KingdomKey.KEY = key;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if KingdomKey.KEY has been set, otherwise false.
     * 
     * @return true if KingdomKey.KEY is set, otherwise false.
     */
    public static boolean isSet() {
        if (KingdomKey.KEY != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Encrypts provided parameter using KingdomKey.KEY.
     * 
     * @param unencrypted
     *            is unencrypted byte array
     * @return encrypted byte array
     */
    public static byte[] encrypt(byte[] unencrypted) {
        return performCryptographyOperation(unencrypted, Cipher.ENCRYPT_MODE);
    }

    /**
     * Encrypts provided parameter using KingdomKey.KEY.
     * 
     * @param unencrypted
     *            is unencrypted string in KingdomKey.ENCODING encoding
     * @return encrypted blob instance
     */
    public static Blob encrypt(String unencrypted) {
        byte[] secret = null;
        try {
            secret = unencrypted.getBytes(KingdomKey.ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.severe("Yubikey: " + KingdomKey.ENCODING + " encoding is not supported. Exception occurred while converting a string to byte array.");
            e.printStackTrace();
        }
        return new Blob(encrypt(secret));
    }

    /**
     * Decrypts provided parameter using KingdomKey.KEY.
     * 
     * @param encrypted
     *            is encrypted byte array
     * @return decrypted byte array
     */
    static byte[] decrypt(byte[] encrypted) {
        return performCryptographyOperation(encrypted, Cipher.DECRYPT_MODE);
    }

    /**
     * Decrypts provided parameter using KingdomKey.KEY.
     * 
     * @param encrypted
     *            is encrypted blob instance.
     * @return decrypted string value of blob in KingdomKey.ENCODING encoding.
     */
    static String decrypt(Blob encrypted) {
        byte[] secret = decrypt(encrypted.getBytes());
        String decrypted = null;
        // TODO check secret != null
        try {
            decrypted = new String(secret, KingdomKey.ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.severe("Yubikey: " + KingdomKey.ENCODING + " encoding is not supported. Exception occurred while converting a byte array to string.");
            e.printStackTrace();
        }
        return decrypted;
    }

    /**
     * This method performs cryptography operation - encrypting and decrypting.
     * 
     * @param input
     *            is byte array input for cryptography operation
     * @param opMode
     *            is mode of operation - Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE.
     * @return result of cryptography operation in byte array
     */
    private static byte[] performCryptographyOperation(byte[] input, int opMode) {
        if (!KingdomKey.isSet()) {
            return null;
        }
        SecretKeySpec key = new SecretKeySpec(KingdomKey.KEY, KingdomKey.ALGORITHM);
        byte[] output = null;
        try {
            Cipher cipher = Cipher.getInstance(KingdomKey.ALGORITHM + "/" + KingdomKey.MODE + "/" + KingdomKey.PADDING);
            cipher.init(opMode, key);
            output = cipher.doFinal(input);
        } catch (NoSuchAlgorithmException e) {
            log.severe("Yubikey: " + KingdomKey.ALGORITHM + " is not supported.");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            log.severe("Yubikey: " + KingdomKey.PADDING + " is not supported.");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            log.severe("Yubikey: Key is invalid.");
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            log.severe("Yubikey: Block size is invalid.");
            e.printStackTrace();
        } catch (BadPaddingException e) {
            log.severe("Yubikey: Padding block is invalid.");
            e.printStackTrace();
        }
        return output;
    }
}

