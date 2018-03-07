package com.yubico.yubikeymp;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class holds key to the kingdom.<br/>
 * <br/>
 * <i>The secrets stored in datastore are encrypted with the KingdomKey.KEY.</i>
 * 
 * @author Vlastimil Ovčáčík
 */
/**
 * @author Vlastimil Ovčáčík
 */
public final class KingdomKey {

    /**
     * Encoding used in string operations.
     */
    public static final String ENCODING = "UTF-8";

    /**
     * What JCE provider to use. The standard provider is SunJCE.<br/>
     * <br/>
     * <i>Google App Engine supports only SunJCE provider (and some others built-in providers).</i>
     */
    private static final String PROVIDER = "SunJCE";

    /**
     * Encryption key or more precisely encryption password. If not set its value is null. If set its value is non-empty
     * char array. Actual encryption key is generated from this password with key generator algorithm
     * (ALGORITHM_KEY_GEN), salt (random byte[] SALT_LENGTH long) and whole process runs for random number of cycles
     * (see ITERATIONS_MINIMUM and ITERATIONS_MAXIMUM). Unicode is supported.
     */
    private static transient char[] KEY = null;

    /**
     * Defines minimum of iterations required to generate key (inclusive). Its value should be in <1,
     * ITERATIONS_MAXIMUM> range. Actual number of iterations is random for each saved password and is in
     * <ITERATIONS_MINIMUM, ITERATIONS_MAXIMUM> range.<br/>
     * <br/>
     * <i>PKCS #5 V2.0 recommends at least 1000 iterations.</i>
     */
    private static final int ITERATIONS_MINIMUM = 5000;

    /**
     * Defines maximum of iterations required to generate key (inclusive). Its value should be in <ITERATIONS_MINIMUM,
     * Integer.MAX_VALUE> range. Actual number of iterations is random for each saved password and is in
     * <ITERATIONS_MINIMUM, ITERATIONS_MAXIMUM> range.<br/>
     * <br/>
     * <i>Integer.MAX_VALUE = (2^31)-1 = 2 147 483 647.</i>
     */
    private static final int ITERATIONS_MAXIMUM = 10000;

    /**
     * Length of salt in bits.<br/>
     * <br/>
     * <i>PKCS #5 V2.0 recommends at least 64 bit length.</i>
     */
    private static final int SALT_LENGTH = 128;

    /**
     * This algorithm is used to generate KingdomKey.KEY from password.<br/>
     * <br/>
     * <i>PKCS #5 V2.0 recommends PBKDF2WithHmacSHA1.</i>
     */
    private static final String ALGORITHM_KEY_GEN = "PBKDF2WithHmacSHA1";

    /**
     * Encryption algorithm.
     */
    private static final String ALGORITHM_ENCRYPTION = "AES";

    /**
     * Length of encryption key in bits. Depends on used encryption algorithm.<br/>
     * <br/>
     * <i>SunJCE provider limits maximum length of key. For example AES is limited to 128 bits.</i>
     */
    private static final int KEY_LENGTH = 128;
    /**
     * Mode of operation.
     */
    private static final String MODE = "CBC";

    /**
     * Padding used when input of encryption algorithm is not multiple of block size.
     */
    private static final String PADDING = "PKCS5Padding";

    /**
     * Initialization vector length. IV is equal to block size of encryption algorithm.
     */
    private static final int IV_LENGTH = 128;

    /**
     * Preferred algorithm to generate random numbers.
     */
    private static final String ALGORITHM_RNG = "SHA1PRNG";

    private static final Logger log = Logger.getLogger(KingdomKey.class.getName());

    /*
     * Actual number of iterations for this instance. The value is random for each saved password and is in
     * <ITERATIONS_MINIMUM, ITERATIONS_MAXIMUM> range.
     */
    private final int iterations;

    /*
     * Actual salt used in this instance.
     */
    private final byte[] salt;

    /*
     * Actual initialization vector used in this instance.
     */
    private final byte[] iv;

    // TODO null byte arrays after use
    // TODO use 256 bit key
    // TODO run for 14 rounds of encryption AES

    /**
     * Use this constructor when you want decrypt data or you want encrypt some data with your own parameters.
     * 
     * @param iterations
     *            number of hash cycles needed to generate key from a password
     * @param salt
     *            salt used in hashing key from a password
     * @param iv
     *            initialization vector
     */
    KingdomKey(int iterations, byte[] salt, byte[] iv) {
        this.iterations = iterations;
        this.salt = salt;
        this.iv = iv;
    }

    /**
     * Use this constructor when you want encrypt data, and you will let KingdomKey class to generate random number of
     * iterations, salt and initialization vector.
     */
    KingdomKey() {
        this.salt = new byte[KingdomKey.SALT_LENGTH / 8];
        this.iv = new byte[KingdomKey.IV_LENGTH / 8];

        SecureRandom random = getSecureRandom();
        this.iterations = KingdomKey.ITERATIONS_MINIMUM
                + random.nextInt(KingdomKey.ITERATIONS_MAXIMUM + 1 - KingdomKey.ITERATIONS_MINIMUM);
        random.nextBytes(this.salt);
        random.nextBytes(this.iv);
    }

    /**
     * Set <code>key</code> as a new KingdomKey.KEY.
     * 
     * @param key
     *            non-empty string containing clear-text key to the kingdom.
     * @return true if new key was set otherwise false
     */
    public static boolean setKey(String key) {
        if (key != null) {
            return setKey(key.toCharArray());
        } else {
            return false;
        }
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
     * Use this method to securely overwrite data in an array. The array will be overwritten three times and in
     * each cycle with new random byte.
     * 
     * @param array
     */
    public static void overwrite(byte[] array) {
        if (array != null) {
            SecureRandom random = getSecureRandom();
            for (int i = 0; i < 3; i++) {
                Arrays.fill(array, (byte) random.nextInt(256));
            }
        }
    }

    /**
     * This method tries to provide KingdomKey.ALGORITHM_RNG random number generator (RNG). If not available it returns
     * first RNG found among other providers. If no RNG found it returns Java default RNG.
     * 
     * @return preferred KingdomKey.ALGORITHM_RNG RNG if available, or first RNG among other providers if available, or
     *         default Java RNG.
     */
    private static SecureRandom getSecureRandom() {
        SecureRandom random = new SecureRandom();
        try {
            random = SecureRandom.getInstance(KingdomKey.ALGORITHM_RNG);
        } catch (NoSuchAlgorithmException e) {
            log.severe("Yubikey: Preferred " + KingdomKey.ALGORITHM_RNG + " algorithm is not available. "
                    + random.getAlgorithm() + " was used instead. Do you believe " + random.getAlgorithm()
                    + " is strong enough for you?");
            e.printStackTrace();
        }
        return random;
    }

    /**
     * Getter for number of iterations.
     * 
     * @return returns number of iterations used in key generator algorithm (hashing).
     */
    int getIterations() {
        return this.iterations;
    }

    /**
     * Getter for salt.
     * 
     * @return returns cloned salt used in key generator algorithm (hashing).
     */
    byte[] getSalt() {
        return this.salt.clone();
    }

    /**
     * Getter for initialization vector.
     * 
     * @return cloned initialization vector used in cryptographic operation.
     */
    byte[] getIV() {
        return this.iv.clone();
    }


    /**
     * Encrypts provided parameter using KingdomKey.KEY.
     * 
     * @param unencrypted
     *            is unencrypted byte array
     * @return encrypted byte array
     */
    byte[] encrypt(final byte[] unencrypted) {
        return performCryptographyOperation(unencrypted, Cipher.ENCRYPT_MODE);
    }

    /**
     * Decrypts provided parameter using KingdomKey.KEY.
     * 
     * @param encrypted
     *            is encrypted byte array
     * @return decrypted byte array
     */
    byte[] decrypt(final byte[] encrypted) {
        return performCryptographyOperation(encrypted, Cipher.DECRYPT_MODE);
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
    private byte[] performCryptographyOperation(final byte[] input, final int opMode) {
        byte[] output = null; // TODO return null or throw exception?
        PBEKeySpec keySpec = null;

        if (KingdomKey.isSet()) {
            try {
                /* Secret Key Factory is used to generate encryption key from password. */
                final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KingdomKey.ALGORITHM_KEY_GEN,
                        KingdomKey.PROVIDER);
                /*
                 * Password based-encryption key specification keySpec - stores password, salt, number of iterations and
                 * expected key length.
                 */
                keySpec = new PBEKeySpec(KingdomKey.KEY, this.salt, this.iterations, KingdomKey.KEY_LENGTH);
                /*
                 * We take two steps here:
                 * (1) key factory generates key from password specified in keySpec. Iterations, salt and key length is
                 * used here. The result of generateSecret() method is SecretKey instance for
                 * KingdomKey.ALGORITHM_KEY_GEN algorithm. This SecretKey instance is not usable for AES (default)
                 * algorithm - see step 2. Method getEncoded() returns the (actual encryption) key in byte array.
                 * (2) because we use AES algorithm (by default) we have to wrap the key into SecretKey instance with
                 * AES algorithm specified. That instance is created by calling SecretKeySpec constructor and is stored
                 * in "key" variable.
                 */
                final SecretKey key = new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(),
                        ALGORITHM_ENCRYPTION);
                /* Cipher object does the actual encryption operation. */
                final Cipher cipher = Cipher.getInstance(KingdomKey.ALGORITHM_ENCRYPTION + "/" + KingdomKey.MODE + "/"
                        + KingdomKey.PADDING, KingdomKey.PROVIDER);
                /*
                 * Cipher needs to know operation mode (encryption/decryption), encryption key and initialization
                 * vector.
                 */
                cipher.init(opMode, key, new IvParameterSpec(this.iv));
                /* Process input and save it in output variable. */
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
            } catch (InvalidAlgorithmParameterException e) {
                log.severe("Yubikey: Algorithm parameter is invalid. This probably means that wrong initialization vector (IV) were provided.");
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                log.severe("Yubikey: KeySpec is invalid.");
                e.printStackTrace();
            } finally {
                /* Clean up */
                overwrite(input);
                if (keySpec != null) {
                    keySpec.clearPassword();
                }
            }
        }

        return output;
    }
}
