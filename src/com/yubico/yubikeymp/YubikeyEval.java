package com.yubico.yubikeymp;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

/**
 * This class handles requests for master passwords via /eval URL.
 * 
 * @author Vlastimil Ovčáčík
 */
@SuppressWarnings("serial")
public class YubikeyEval extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(YubikeyEval.class.getName());

    /**
     * {@inheritDoc}
     */
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("utf-8");

        final YubikeyOTP otp = YubikeyOTP.createInstance(req.getParameter("otp"));
        final String pid = req.getParameter("pid");

        if (otp != null && otp.verify()) {
            final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            final Query query = new Query("Secrets");
            query.addFilter("user", Query.FilterOperator.EQUAL, otp.getStaticPart());
            query.addFilter("pid", Query.FilterOperator.EQUAL, pid);

            // TODO how to get unlimited results? asIterator maybe?
            final List<Entity> secrets = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));

            final Iterator<Entity> iterator = secrets.iterator();
            while (iterator.hasNext()) {
                final Entity entity = iterator.next();
                final String secret = (String) entity.getProperty("secret");
                resp.getWriter().print(secret);
                // TODO print only first match?
            }
            // TODO destroy resp, entity, secret and secrets variables
            LOG.info("Yubikey: eval successfully finished. USER: " + otp.getStaticPart() + ". PID: " + pid + ".");
        } else {
            // TODO verification failed
        }
    }
}
