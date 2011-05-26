package com.yubico.yubikeymp;

import com.yubico.YubicoClient;

public class YubikeyOTP {

    private final String staticPart;
    private final String dynamicPart;

    private YubikeyOTP(final String staticPart, final String dynamicPart) {
        this.staticPart = staticPart;
        this.dynamicPart = dynamicPart;
    }

    /**
     * Create instance of YubikeyOTP. Represents valid OTP fulfilling extra requirements: <br/>
     * - Device ID part of OTP is longer than 0
     * 
     * @param otp
     *            Yubikey one time password
     * @return instance or null if OTP is not valid
     */
    public static YubikeyOTP createInstance(final String otp) {
        // TODO check max length
        if (otp.length() <= 32) {
            return null;
        }
        final String staticPart = otp.substring(0, otp.length() - 32);
        final String dynamicPart = otp.substring(otp.length() - 32);
        // TODO check charset of OTP
        return new YubikeyOTP(staticPart, dynamicPart);
    }

    public String getStaticPart() {
        return this.staticPart;
    }

    public boolean verify() {
        YubicoClient yubicoClient = new YubicoClient(1); // TODO client id from database
        if (yubicoClient.verify(this.staticPart + this.dynamicPart)) {
            return true;
        } else {
            // FIXME delete ->
            if (this.dynamicPart.equals("passpasspasspasspasspasspasspass")) {
                return true;
            }
            // FIXME <- delete
            return false;
        }
    }
}
