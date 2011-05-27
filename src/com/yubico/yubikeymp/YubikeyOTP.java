package com.yubico.yubikeymp;

import java.util.Iterator;

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

    /**
     * Static part of OTP. Also known as "Yubikey ID". We use static part as user ID.
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
    }

    /**
     * Creates instance of YubikeyOTP. Represents valid OTP fulfilling extra requirements: <br/>
     * - Device ID part of OTP is longer than 0
     * 
     * @param otp
     *            Yubikey one time password
     * @return instance or null if OTP is not valid
     */
    public static YubikeyOTP createInstance(final String otp) {
        // OTP should has length between <33, 44>. Note that valid Yubikey may be long only 32 characters,
        // but then it has no static part, which is required for yubikey-mp, thus the bottom limit is 33 characters.
        if (otp.length() <= 32 || otp.length() > 44) {
            return null;
        }

        // Each OTP consist of Modhex characters. These are: cbdefghijklnrtuv.
        for (int i = 0; i < otp.length(); i++) {
            int code = otp.codePointAt(i);
            if (code < 98 || code > 118 || (code >= 111 && code <= 113) || code == 109 || code == 115) {
                // Current character is not defined for Modhex.
                return null;
            }
        }

        final String staticPart = otp.substring(0, otp.length() - 32);
        final String dynamicPart = otp.substring(otp.length() - 32);
        return new YubikeyOTP(staticPart, dynamicPart);
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
        if (yubicoClient.verify(this.staticPart + this.dynamicPart)) {
            return true;
        } else {
            // FIXME delete ->
            // Special dynamic part for testing purposes.
            if (this.dynamicPart.equals("cbdefghijklnrtuvcbdefghijklnrtuv")) {
                return true;
            }
            // FIXME <- delete
            return false;
        }
    }

    /**
     * Returns API client ID for use in YubiCloud.
     * 
     * @return Client ID from datastore Prefs/clientid if defined or 1 as default value.
     */
    private int getClientID() {
        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query("Prefs");
        query.addFilter("clientid", Query.FilterOperator.NOT_EQUAL, "");

        final Iterator<Entity> clientIDs = datastore.prepare(query).asIterator();
        
        if (clientIDs.hasNext()) {
            final Entity entity = clientIDs.next();
            final String clientIDString = (String) entity.getProperty("clientid");
            final int clientID = Integer.parseInt(clientIDString);
            if (clientID > 0)
                return clientID;
        }
        return 1; // TODO this default value ok?
    }
}
