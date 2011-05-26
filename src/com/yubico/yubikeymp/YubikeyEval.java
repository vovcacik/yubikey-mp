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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class YubikeyEval extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String otp = req.getParameter("otp");
        String pid = req.getParameter("pid");
        resp.setContentType("text/plain"); // TODO unicode support?

        if (otp.equals("reg")) {
            otp = "aaa";
            Key key = KeyFactory.createKey("User", otp);
            Entity entity = new Entity("Passwords", key);
            entity.setProperty("secret", "yubikey");
            entity.setProperty("pid", pid);
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(entity);
        }
        if (otp.equals("aaa")) {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Key key = KeyFactory.createKey("User", otp);
            Query query = new Query("Passwords", key);
            List<Entity> secrets = datastore.prepare(query).asList(null);
            if (secrets.isEmpty()) {
                // TODO
            } else {
                Iterator<Entity> iterator = secrets.iterator();
                while(iterator.hasNext()){
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
}
