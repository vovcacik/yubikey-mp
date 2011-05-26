package com.yubico.yubikeymp;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.yubico.YubicoClient;

@SuppressWarnings("serial")
public class YubikeyEval extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String otp = req.getParameter("otp");
        String deviceID = otp.substring(0, otp.length() - 32); // TODO out of bound excep?
        String pid = req.getParameter("pid");
        resp.setContentType("text/plain"); // TODO unicode support?

        YubicoClient yubicoClient = new YubicoClient(1); // client id???
        if (yubicoClient.verify(otp)) {
            // otp ok
            // resp.getWriter().println(yubicoClient.getLastResponse());
        } else {
            // otp bad or replay
            // resp.getWriter().println(yubicoClient.getLastResponse());
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key key = KeyFactory.createKey("User", deviceID);
        Query query = new Query("Passwords", key);
        List<Entity> secrets = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000)); // TODO how to get
                                                                                                      // unlimited?
        if (secrets.isEmpty()) {
            // TODO
        } else {
            Iterator<Entity> iterator = secrets.iterator();
            while (iterator.hasNext()) {
                Entity entity = iterator.next();
                if (pid.equals(entity.getProperty("pid"))) {
                    String secret = (String) entity.getProperty("secret");
                    resp.getWriter().print(secret);
                    // destroy resp, entity, secret and secrets variables
                }
            }
        }
    }
}
