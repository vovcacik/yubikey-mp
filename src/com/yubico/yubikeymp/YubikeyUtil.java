package com.yubico.yubikeymp;

import java.util.Iterator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

public class YubikeyUtil {

    public static final String MODHEX = "cbdefghijklnrtuv";

    private YubikeyUtil() {
        super();
    }

    public static boolean isServerInitialized() {
        return getAdminName() != null;
    }

    public static String getAdminName() {
        String name = null;

        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        final Query query = new Query("Prefs");
        Iterator<Entity> iterator = datastore.prepare(query).asIterator();

        if (iterator.hasNext()) {
            Entity prefs = iterator.next();
            Object o = prefs.getProperty("admin");
            if (o instanceof String) {
                name = (String) o;
            }
        }

        return name;
    }

    public static boolean isAdminsOTP(YubikeyOTP otp) {
        String admin = getAdminName();
        return admin != null && otp != null && admin.equals(otp.getStaticPart()) && otp.verify();
    }
}
