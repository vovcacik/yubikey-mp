package com.yubico.yubikeymp;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class handles requests for master passwords via /eval URL.
 * 
 * @author Vlastimil Ovčáčík
 */
@SuppressWarnings("serial")
public class YubikeyEval extends HttpServlet {

    // TODO stop printing stack trace and logs to resp
    // TODO print only first match of pid?

    private static final Logger log = Logger.getLogger(YubikeyEval.class.getName());

    /**
     * {@inheritDoc}
     */
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("utf-8");

        final YubikeyOTP otp = YubikeyOTP.createInstance(req.getParameter("otp"));
        final String pid = req.getParameter("pid");

        if (otp != null && otp.verify()) {
            final YubikeySecret secretEntity = YubikeySecret.findInstance(otp.getStaticPart(), pid);
            if (secretEntity != null) {
                final byte[] secret = secretEntity.getPropertyAsByteArray(YubikeySecret.SECRET);
                final int iterations = secretEntity.getPropertyAsInt(YubikeySecret.ITERATIONS);
                final byte[] salt = secretEntity.getPropertyAsByteArray(YubikeySecret.SALT);
                final byte[] iv = secretEntity.getPropertyAsByteArray(YubikeySecret.IV);

                final KingdomKey kk = new KingdomKey(iterations, salt, iv);
                final byte[] decrypted = kk.decrypt(secret);
                if (decrypted != null) {
                    resp.getWriter().print(new String(decrypted, KingdomKey.ENCODING));
                    // Try to minimize password exposure in RAM memory.
                    System.gc(); // TODO performance ok?
                }
                KingdomKey.overwrite(decrypted);
                log.info("Yubikey: eval successfully finished. USER: " + otp.getStaticPart() + ". PID: " + pid + ".");
            }
        } else {
            // TODO verification failed - send 401 or empty?
            // UNAUTHORIZED
            // resp.sendError(401);
        }
    }
}
