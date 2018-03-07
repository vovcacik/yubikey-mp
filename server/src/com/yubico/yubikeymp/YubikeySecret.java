package com.yubico.yubikeymp;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

/**
 * This class represents an entity in Secrets kind in datastore. Secrets kind is used to store passwords.
 * 
 * @author Vlastimil Ovčáčík
 */
public class YubikeySecret {

    /**
     * Name of this kind.
     */
    public static final String KIND = "Secrets";

    /**
     * Name of property holding user's name. User's name is 12 ModHex characters long string equal to static part of
     * user's Yubikey OTP.
     */
    public static final String USER = "user";

    /**
     * Name of property holding PID. PID is password ID.
     */
    public static final String PID = "pid";

    /**
     * Name of property holding securely stored password.
     */
    public static final String SECRET = "secret";

    /**
     * Name of property holding number of iterations used in key generator.
     */
    public static final String ITERATIONS = "iterations";

    /**
     * Name of property holding salt used in key generator.
     */
    public static final String SALT = "salt";

    /**
     * Name of property holding IV used in encryption algorithm.
     */
    public static final String IV = "iv";

    private static final Logger log = Logger.getLogger(YubikeySecret.class.getName());

    // Actual entity represented by this instance.
    private final Entity entity;

    /**
     * Use this constructor when you want to create new secret entity.
     * 
     * @param user
     *            name of the user
     * @param pid
     *            password id
     * @param secret
     *            clear-text password in byte array (Use KingdomKey.ENCODING to get the bytes).
     */
    private YubikeySecret(final String user, final String pid, final byte[] secret) {
        entity = new Entity(KIND);
        final KingdomKey kk = new KingdomKey();
        entity.setProperty(USER, user);
        entity.setProperty(PID, pid);
        entity.setProperty(SECRET, new Blob(kk.encrypt(secret)));
        entity.setProperty(ITERATIONS, kk.getIterations());
        entity.setProperty(SALT, new Blob(kk.getSalt()));
        entity.setProperty(IV, new Blob(kk.getIV()));
        KingdomKey.overwrite(secret);
    }

    /**
     * Use this constructor when you already have Entity instance.
     * 
     * @param entity
     *            which will be represented by YubikeySecret instance.
     */
    private YubikeySecret(Entity entity) {
        this.entity = entity;
    }

    /**
     * Use this to create new secret entity represented by YubikeySecret instance.
     * 
     * @param user
     *            name of the user
     * @param pid
     *            password id
     * @param secret
     *            clear-text password
     * @return instance of YubikeySecret or null if something went wrong.
     */
    public static YubikeySecret createInstance(final String user, final String pid, String secret) {
        YubikeySecret instance = null;
        byte[] secretBytes = null;
        
        try {
            secretBytes = secret.getBytes(KingdomKey.ENCODING);
            // Try to minimize password exposure in RAM memory.
            secret = null;
            System.gc();
        } catch (UnsupportedEncodingException e) {
            log.severe("Yubikey: " + KingdomKey.ENCODING
                    + " encoding is not supported. Exception occurred while converting a string to byte array.");
            e.printStackTrace();
        }
        if (validUser(user) && validPID(pid) && validSecret(secretBytes)) {
            instance = new YubikeySecret(user, pid, secretBytes);
        }

        KingdomKey.overwrite(secretBytes);
        return instance;
    }

    /**
     * Use this to get secret entity represented by YubikeySecret instance which is already stored in datastore and has
     * matching user and pid.
     * 
     * @param user
     *            name of the user
     * @param pid
     *            password id
     * @return instance of YubikeySecret or null if secret entity not found.
     */
    public static YubikeySecret findInstance(final String user, final String pid) {
        YubikeySecret instance = null;

        if (validUser(user) && validPID(pid)) {
            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            final Query query = new Query(KIND);
            query.addFilter(USER, Query.FilterOperator.EQUAL, user);
            query.addFilter(PID, Query.FilterOperator.EQUAL, pid);
            final Iterator<Entity> iterator = datastore.prepare(query).asIterator();

            if (iterator.hasNext()) {
                instance = new YubikeySecret(iterator.next());
            }
        }

        return instance;
    }

    /**
     * Getter for entity.
     * 
     * @return entity represented by this instance
     */
    Entity getEntity() {
        return entity; // TODO clone?
    }

    /**
     * Checks whether provided user name is correct.
     * 
     * @param user
     *            name of the user
     * @return true if user's name is well-formed, otherwise false
     */
    static boolean validUser(final String user) {
        return user != null && YubikeyOTP.isOTP(user + YubikeyOTP.MODHEX + YubikeyOTP.MODHEX);
    }

    /**
     * Check whether provided password ID (PID) is correct.
     * 
     * @param pid
     *            the password id
     * @return true if password id is well-formed, otherwise false.
     */
    private static boolean validPID(final String pid) {
        // TODO check pid is unique
        return pid != null && pid.length() > 0;
    }
    
    /**
     * Checks whether provided secret is correct.
     * 
     * @param secret
     *            the password
     * @return true if secret is well-formed, otherwise false.
     */
    private static boolean validSecret(final byte[] secret) {
        return secret != null && secret.length > 0;
    }

    /**
     * Getter for property.
     * 
     * @param property
     *            name of the property
     * @return value of the property
     */
    public Object getProperty(String property) {
        return this.entity.getProperty(property);
    }

    /**
     * Getter for byte property.
     * 
     * @param property
     *            name of the property
     * @return byte array holding value of the property
     */
    public byte[] getPropertyAsByteArray(String property) {
        final Object o = getProperty(property);
        if (o instanceof Blob) {
            return ((Blob) o).getBytes();
        } else {
            return null;
        }
    }

    /**
     * Getter for int property.
     * 
     * @param property
     *            name of the property
     * @return int value of the property
     */
    public int getPropertyAsInt(String property) {
        return ((Long) getProperty(property)).intValue();
    }
}
