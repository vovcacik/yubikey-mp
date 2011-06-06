package com.yubico.yubikeymp;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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
    private static transient SecretKey KEY = null;
    private static final int KEY_LENGTH = 128;
    private static byte[] SALT = "here should be some really random salt".getBytes();
    private static int ITERATION = 3887;

    private static final String PROVIDER = "SunJCE";
    private static final String ALGORITHM_KEY_GEN = "PBKDF2WithHmacSHA1";
    private static final String ALGORITHM_ENCRYPTION = "AES";
    private static final String MODE = "ECB";
    private static final String PADDING = "PKCS5Padding";

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
     * Set <code>key</code> as a new KingdomKey.KEY.
     * 
     * @param key
     *            non-empty string containing clear-text key to the kingdom.
     * @return true if new key was set otherwise false
     */
    public static boolean setKey(String key) {
        return setKey(key.toCharArray());
    }

    /**
     * Set <code>key</code> as a new KingdomKey.KEY.
     * 
     * @param key
     *            non-empty byte array containing clear-text key to the kingdom.
     * @return true if new key was set, otherwise false.
     */
    public static boolean setKey(char[] key) {
        if (KingdomKey.KEY == null && key != null && key.length > 0) {
            try {
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KingdomKey.ALGORITHM_KEY_GEN,
                        KingdomKey.PROVIDER);
                PBEKeySpec keySpec = new PBEKeySpec(key, KingdomKey.SALT, KingdomKey.ITERATION, KingdomKey.KEY_LENGTH);
                KingdomKey.KEY = new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(),
                        ALGORITHM_ENCRYPTION);

                key = null;
                keySpec = null;

                return true;
            } catch (NoSuchAlgorithmException e) {
                log.severe("Yubikey: " + KingdomKey.ALGORITHM_KEY_GEN + " algorithm is not supported.");
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                log.severe("Yubikey: " + KingdomKey.PROVIDER + " provider is not available.");
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                log.severe("Yubikey: KeySpec is invalid.");
                e.printStackTrace();
            }
        }
        return false;
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
        return new Blob(encrypt(secret)); // TODO check null
    }

    /**
     * Decrypts provided parameter using KingdomKey.KEY.
     * 
     * @param encrypted
     *            is encrypted byte array
     * @return decrypted byte array, otherwise null
     */
    static byte[] decrypt(byte[] encrypted) {
        return performCryptographyOperation(encrypted, Cipher.DECRYPT_MODE);
    }

    /**
     * Decrypts provided parameter using KingdomKey.KEY.
     * 
     * @param encrypted
     *            is encrypted blob instance.
     * @return decrypted string value of blob in KingdomKey.ENCODING encoding, otherwise null
     */
    static String decrypt(Blob encrypted) {
        String decrypted = null;
        byte[] secret = decrypt(encrypted.getBytes());
        if (secret != null) {
            try {
                decrypted = new String(secret, KingdomKey.ENCODING);
            } catch (UnsupportedEncodingException e) {
                log.severe("Yubikey: " + KingdomKey.ENCODING
                        + " encoding is not supported. Exception occurred while converting a byte array to string.");
                e.printStackTrace();
            }
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
     * @return result of cryptography operation in byte array, otherwise null.
     */
    private static byte[] performCryptographyOperation(byte[] input, int opMode) {
        byte[] output = null;

        if (KingdomKey.isSet()) {
            try {
                Cipher cipher = Cipher.getInstance(KingdomKey.ALGORITHM_ENCRYPTION + "/" + KingdomKey.MODE + "/"
                        + KingdomKey.PADDING, KingdomKey.PROVIDER);
                cipher.init(opMode, KingdomKey.KEY);
                output = cipher.doFinal(input);
            } catch (NoSuchAlgorithmException e) {
                log.severe("Yubikey: " + KingdomKey.ALGORITHM_ENCRYPTION + " is not supported.");
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                log.severe("Yubikey: " + KingdomKey.PADDING + " padding is not supported.");
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
            } catch (NoSuchProviderException e) {
                log.severe("Yubikey: " + KingdomKey.PROVIDER + " provider is not available.");
                e.printStackTrace();
            }
        }

        return output;
        /*
         * return null or throw exception? create my own exception for this purpose, or use some existing?
         */
    }
}
