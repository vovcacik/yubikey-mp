package com.yubico.yubikeymp;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class YubikeyReg extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String otp = req.getParameter("otp");
        // String pid = req.getParameter("pid");
        resp.setContentType("text/plain"); // TODO unicode support?

        Key key = KeyFactory.createKey("User", otp);
        Entity entity = new Entity("Passwords", key);
        entity.setProperty("secret", "");
        entity.setProperty("pid", "");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entity);
    }
}
