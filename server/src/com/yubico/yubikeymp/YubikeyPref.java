package com.yubico.yubikeymp;

import java.util.Iterator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

/**
 * This class represents an entity in Prefs kind in datastore. Prefs kind is used to store administration information.
 * 
 * @author Vlastimil Ovčáčík
 */
public class YubikeyPref {

    /**
     * Name of this kind.
     */
    public static final String KIND = "Prefs";

    /**
     * Name of property holding admins name. Admins name is 12 ModHex characters long string equal to static part of
     * admin's Yubikey OTP.
     */
    public static final String ADMIN = "admin";

    /**
     * Name of property holding API Key to Yubico services. You may get your API key at
     * https://api.yubico.com/get-api-key/
     */
    public static final String API_KEY = "api_key";

    /**
     * Name of property holding admin's email address.
     */
    public static final String EMAIL = "email";

    /**
     * Name of property holding admin's XMPP address.
     */
    public static final String XMPP = "xmpp";

    // Actual entity which is represented by this instance.
    private final Entity entity;

    /**
     * Use this constructor when you want to create new pref entity.
     * 
     * @param adminName
     *            name of the admin
     * @param apiKey
     *            API key
     */
    private YubikeyPref(final String adminName, final int apiKey) {
        entity = new Entity(KIND);
        entity.setProperty(ADMIN, adminName);
        entity.setProperty(API_KEY, apiKey);
        entity.setProperty(EMAIL, ""); // TODO make web interface for these values.
        entity.setProperty(XMPP, "");
    }

    /**
     * Use this constructor when you already have Entity instance.
     * 
     * @param entity
     *            which will be represented by YubikeyPref instance.
     */
    private YubikeyPref(Entity entity) {
        this.entity = entity;
    }

    /**
     * Use this to create new pref entity represented by YubikeyPref instance.
     * 
     * @param adminName
     *            name of the admin
     * @param apiKey
     *            API key
     * @return instance of YubikeyPref, or null if something went wrong.
     */
    public static YubikeyPref createInstance(final String adminName, final int apiKey) {
        YubikeyPref instance = null;

        if (validAdminName(adminName) && validAPIKey(apiKey)) {
            instance = new YubikeyPref(adminName, apiKey);
        }

        return instance;
    }

    /**
     * Use this to get most recent pref entity represented by YubikeyPref instance which is already stored in datastore.
     * 
     * @return instance of YubikeyPref or null if pref entity not found.
     */
    public static YubikeyPref findInstance() {
        // TODO return latest pref for current admin.
        YubikeyPref instance = null;

        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query(KIND);
        query.addFilter(ADMIN, Query.FilterOperator.NOT_EQUAL, null); // TODO is this good enough?
        final Iterator<Entity> iterator = datastore.prepare(query).asIterator();

        if (iterator.hasNext()) {
            instance = new YubikeyPref(iterator.next());
        }

        return instance;
    }

    /**
     * Checks whether provided admin name is correct.
     * 
     * @param adminName
     *            name of the admin
     * @return true if admin's name is well-formed, otherwise false
     */
    private static boolean validAdminName(final String adminName) {
        return YubikeySecret.validUser(adminName);
    }

    /**
     * Checks whether provided API key is correct.
     * 
     * @param apiKey
     *            API key
     * @return true if the API key is well-formed, otherwise false
     */
    private static boolean validAPIKey(final int apiKey) {
        return apiKey > 0;
    }

    /**
     * Getter for entity.
     * 
     * @return Entity instance which is represented by this YubikeyPref instance.
     */
    Entity getEntity() {
        return entity; // TODO clone entity?
    }

    /**
     * Getter for property.
     * 
     * @param property
     *            name of the property
     * @return value of the property
     */
    public Object getProperty(final String property) {
        return entity.getProperty(property);
    }

    /**
     * Getter for int property.
     * 
     * @param property
     *            name of the property
     * @return int value of the property
     */
    public int getPropertyAsInt(final String property) {
        return ((Long) getProperty(property)).intValue();
    }

    /**
     * Getter for String property.
     * 
     * @param property
     *            name of the property
     * @return String value of the property
     */
    public String getPropertyAsString(final String property) {
        final Object o = getProperty(property);
        if (o instanceof String) {
            return (String) o;
        } else {
            return null;
        }
    }
}
