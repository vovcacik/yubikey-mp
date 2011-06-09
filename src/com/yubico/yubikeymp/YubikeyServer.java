package com.yubico.yubikeymp;

import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 * This class represents currently running server instance.
 * 
 * @author Vlastimil Ovčáčík
 */
public class YubikeyServer {

    /**
     * Provides access to datastore.
     */
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    /**
     * Hash map used as timeout table for alerts.
     * 
     * @see {@link YubikeyServer#alertAdmin(String, String)}
     */
    private static final HashMap<String, Long> timeouts = new HashMap<String, Long>();

    /**
     * Private constructor.
     */
    private YubikeyServer() {
        super();
    }

    /**
     * Getter for admin's name.
     * 
     * @return admin's name or null if not set.
     */
    public static String getAdminName() {
        // TODO DRY here, in alertAdmin() and in getAdminName()
        String adminName = null;
        final YubikeyPref pref = YubikeyPref.findInstance();
        if (pref != null) {
            adminName = pref.getPropertyAsString(YubikeyPref.ADMIN);
        }
        return adminName;
    }

    /**
     * Returns true if the provided OTP is issued by admin.
     * 
     * @param otp
     *            OTP to verify
     * @return true if OTP is issued by admin otherwise false.
     */
    public static boolean hasAdminAccess(final YubikeyOTP otp) {
        final String adminName = getAdminName();
        return isInitialized() && adminName != null && otp != null && adminName.equals(otp.getStaticPart())
                && otp.verify();
    }

    /**
     * Server is initialized when datastore contains valid pref entity.
     * 
     * @return true if server is initialized otherwise false.
     */
    public static boolean isInitialized() {
        if (getAdminName() != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Put YubikeySecret to datastore.
     * 
     * @param secret
     *            the YubikeySecret to put in
     * @return true if operation is successful otherwise false.
     */
    public static boolean put(final YubikeySecret secret) {
        if (secret != null) {
            final Transaction transaction = datastore.beginTransaction();

            try {
                datastore.put(secret.getEntity());
                transaction.commit();
            } finally {
                // If transaction is active at this point, something went wrong.
                if (transaction.isActive()) {
                    transaction.rollback();
                    return false;
                }
            } // TODO catch clause missing

            return true;
        } else {
            return false;
        }
    }

    /**
     * Put YubikeyPref to datastore.
     * 
     * @param pref
     *            the YubikeyPref to put in
     * @return true if operation is successful otherwise false.
     */
    public static boolean put(final YubikeyPref pref) {
        if (pref != null) {
            final Transaction transaction = datastore.beginTransaction();

            try {
                datastore.put(pref.getEntity());
                transaction.commit();
            } finally {
                // If transaction is active at this point, something went wrong.
                if (transaction.isActive()) {
                    transaction.rollback();
                    return false;
                }
            } // TODO catch clause missing

            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns API key for use in YubiCloud.
     * 
     * @return API key from datastore Prefs kind if defined or 1 as default value.
     */
    public static int getAPIKey() {
        int apiKey = 1; // TODO default value ok?
        final YubikeyPref pref = YubikeyPref.findInstance();
        if (pref != null) {
            apiKey = pref.getPropertyAsInt(YubikeyPref.API_KEY);
        }
        return apiKey;
    }

    /**
     * Use this method to alert admin. The alert should have subject and body. Alerts with exactly same subject can be
     * send again after timeout expires. Timeout is set by you in timeoutMillis parameter and is reseted after
     * expiration or after restarting server. Timeout can be set to zero.
     * 
     * @param subject
     *            Subject of the alert.
     * @param body
     *            Description of the alert. Is used as a message body.
     * @param timeoutMillis
     *            timeout in milliseconds.
     */
    public static void alertAdmin(final String subject, final String body, final long timeoutMillis) {
        // TODO DRY here, in alertAdmin() and in getAdminName()
        // TODO xmpp alerts too
        final long now = System.currentTimeMillis();
        final Long timeout = timeouts.get(subject);

        // Get admin's email address
        String admin = null;
        final YubikeyPref pref = YubikeyPref.findInstance();
        if (pref != null) {
            admin = pref.getPropertyAsString(YubikeyPref.EMAIL);
        }

        // Do nothing if the admin's email address is not set.
        if (admin == null || admin.equals("")) {
            return;
        }

        // Process the alert request.
        if (timeout == null || timeout < now) {
            // It is first-time alert OR timeout expired.
            sendEmail(admin, subject, body);
            // Create/Update timeout.
            timeouts.put(subject, now + timeoutMillis);
        }
    }

    /**
     * Sends email. E-mails are always sent from admin.
     * 
     * @param to
     *            To whom the email is send.
     * @param subject
     *            Subject of the email.
     * @param body
     *            Body of the email.
     */
    private static void sendEmail(final String to, final String subject, final String body) {
        // TODO DRY here, in alertAdmin() and in getAdminName()
        // Get admin's email address. E-mails are always sent from admin.
        String from = null;
        final YubikeyPref pref = YubikeyPref.findInstance();
        if (pref != null) {
            from = pref.getPropertyAsString(YubikeyPref.EMAIL);
        }

        // Do nothing if the admin's email address is not set.
        if (from == null || from.equals("")) {
            return;
        }

        try {
            final Session session = Session.getDefaultInstance(new Properties());
            final Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(from));
            email.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            email.setSubject(subject);
            email.setText(body);
            Transport.send(email);
        } catch (final Exception e) { // TODO too much general exception catch?
            // TODO log
            e.printStackTrace();
        }
    }
}
