package com.yubico.yubikeymp;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.yubico.YubicoClient;

/**
 * This class represents Yubikey one time password. This OTP has to comply with Yubico requirements
 * and yubikey-mp requirements. To create an instance use YubikeyOTP.createInstance() method.
 * 
 * @author Vlastimil Ovčáčík
 */
public class YubikeyOTP {

    private static final Logger log = Logger.getLogger(YubikeyOTP.class.getName());

    /**
     * Static part of OTP. Also known as "Yubikey ID". We use static part as user ID in datastore.
     */
    private final String staticPart;

    /**
     * Dynamic part of OTP.
     */
    private final String dynamicPart;

    /**
     * Private constructor.
     * 
     * @param staticPart
     *            static part of OTP
     * @param dynamicPart
     *            dynamic part of OTP
     */
    private YubikeyOTP(final String staticPart, final String dynamicPart) {
        this.staticPart = staticPart;
        this.dynamicPart = dynamicPart;
        log.info("Yubikey: new OTP instance created with static part: " + this.staticPart + ".");
    }

    /**
     * Creates instance of YubikeyOTP. Represents valid OTP fulfilling extra requirements: <br/>
     * - Yubikey ID part of OTP is 12 ModHex characters long.
     * 
     * @param otp
     *            Yubikey one time password
     * @return instance of YubikeyOTP or null if provided OTP is not valid
     */
    public static YubikeyOTP createInstance(final String otp) {
        // FIXME check param!=null in other methods!
        if (otp != null && isOTP(otp)) {
            final String staticPart = otp.substring(0, otp.length() - 32);
            final String dynamicPart = otp.substring(otp.length() - 32);
            return new YubikeyOTP(staticPart, dynamicPart);
        } else {
            return null;
        }
    }

    /**
     * This method checks whether <code>otp</code> parameter is valid OTP for yubikey-mp purposes:<br/>
     * - ModHex characters only.<br/>
     * - Static part (aka Yubikey ID) is exactly 12 characters long.<br/>
     * - Whole OTP is 44 characters long.<br/>
     * <br/>
     * This function does <b>not</b> verifies the OTP - use YubikeyOTP.verify() instead.
     * 
     * @param otp
     *            Yubikey OTP to be checked.
     * @return true if the passed OTP is usable in yubikey-mp, otherwise false.
     */
    public static boolean isOTP(final String otp) {
        if (otp != null && Pattern.matches("^[cbdefghijklnrtuv]{44}$", otp)) {
            return true;
        } else {
            log.info("Yubikey: The provided OTP is malformed: " + otp + ".");
            return false;
        }
    }

    /**
     * Getter for static part of OTP.
     * 
     * @return static part of OTP
     */
    public String getStaticPart() {
        return this.staticPart;
    }

    /**
     * Verifies if the OTP is valid on YubiCloud servers at the moment of calling this method. Note that you can verify
     * each OTP once and all subsequent verifications will fail. This method does NOT consider any of possible previous
     * verifications.
     * 
     * @return true if the OTP is verified, otherwise false
     */
    public boolean verify() {
        final int clientID = this.getClientID();
        final YubicoClient yubicoClient = new YubicoClient(clientID);
        if (yubicoClient.verify(this.toString())) {
            log.info("Yubikey: OTP successfully verified: " + this + ".");
            return true;
        } else {
            log.warning("Yubikey: OTP verification failed: " + this + ".");
            // FIXME delete this demo OTP:
            if (this.toString().equals("cbdefghijklncbdefghijklnrtuvcbdefghijklnrtuv")) {
                log.warning("Yubikey: Demo OTP detected: " + this);
                return true;
            }
            return false;
        }
    }

    /**
     * Returns API client ID for use in YubiCloud.
     * 
     * @return Client ID from datastore Prefs/clientid if defined or 1 as default value.
     */
    private int getClientID() {
        int clientID = 1; // TODO this default value ok?

        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query("Prefs");
        query.addFilter("clientid", Query.FilterOperator.NOT_EQUAL, null);

        final Iterator<Entity> clientIDs = datastore.prepare(query).asIterator();
        
        // TODO matches only first entity
        if (clientIDs.hasNext()) {
            final Object value = clientIDs.next().getProperty("clientid");
            if (value instanceof Integer) {
                final int id = (Integer) value;
                if (id > 0) {
                    clientID = id;
                }
            }
        }
        return clientID;
    }

    /**
     * String representation of YubikeyOTP is 44 ModHex characters long string.
     * 
     * @return 44 ModHex characters long Yubikey OTP
     */
    @Override
    public String toString() {
        return this.staticPart + this.dynamicPart;
    }

    /**
     * Two Yubikey OTPs are equal when their ModHex representation is the same.
     * 
     * @param obj
     *            any object
     * @return true if provided obj parameter is YubikeyOTP instance with exact same ModHex representation, otherwise
     *         false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof YubikeyOTP) {
            final YubikeyOTP otp = (YubikeyOTP) obj;
            return this.toString().equals(otp.toString());
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
