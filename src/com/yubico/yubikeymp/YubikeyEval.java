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
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class YubikeyEval extends HttpServlet {

    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain"); // TODO unicode support?

        final YubikeyOTP otp = YubikeyOTP.createInstance(req.getParameter("otp"));
        final String pid = req.getParameter("pid"); // TODO decode from url, maybe already decoded?

        if (otp != null && otp.verify()) {
            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            final Query query = new Query("Secrets");// FIXME use filters
            query.addFilter("user", Query.FilterOperator.EQUAL, otp.getStaticPart());
            query.addFilter("pid", Query.FilterOperator.EQUAL, pid);

            // TODO how to get unlimited results? asIterator maybe?
            final List<Entity> secrets = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));

            final Iterator<Entity> iterator = secrets.iterator();
            while (iterator.hasNext()) {
                final Entity entity = iterator.next();
                final String secret = (String) entity.getProperty("secret");
                resp.getWriter().print(secret);
                // TODO destroy resp, entity, secret and secrets variables
            }
        } else {
            // TODO verification failed
        }
    }
}
