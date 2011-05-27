package com.yubico.yubikeymp;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class YubikeyReg extends HttpServlet {
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("utf-8");

        final YubikeyOTP otp = YubikeyOTP.createInstance(req.getParameter("otp"));
        final String pid = req.getParameter("pid"); // TODO encoding of param?
        final String secret = req.getParameter("secret"); // TODO destroy after usage

        if (otp != null && otp.verify()) {
            final Entity entity = new Entity("Secrets");
            // TODO add from url params...
            // TODO do i have to check user input - sql inject etc.?
            entity.setProperty("user", otp.getStaticPart()); // TODO has to be unique, unique by design?
            entity.setProperty("pid", pid);// FIXME has to be unique in user's context
            entity.setProperty("secret", secret);
            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(entity);
        } else {
            // TODO verification failed
            resp.getWriter().println("otp is null\npid: " + pid);
            resp.getWriter().println("secret: " + secret);
        }
    }
}
