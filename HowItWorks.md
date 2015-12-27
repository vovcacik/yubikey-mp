# How It Works #

![https://raw.githubusercontent.com/vovcacik/yubikey-mp/wiki/images/HowItWorks.png](https://raw.githubusercontent.com/vovcacik/yubikey-mp/wiki/images/HowItWorks.png)

### Commentary ###
  1. After you are asked for your master password just plug in your [Yubikey](http://www.yubico.com/yubikey) and generate [one time password](http://en.wikipedia.org/wiki/One_time_password).
  1. [Yubikey Master Password for Firefox](https://addons.mozilla.org/cs/firefox/addon/yubikey-master-password-ff/) extension will handle the OTP and [password ID (PID)](TutorialDatastore#PID). Then it sends encrypted request to the server using [HTTPS/SSL](http://en.wikipedia.org/wiki/Https). The OTP is sent to [authenticate](http://en.wikipedia.org/wiki/Authentication) user and PID is sent to identify required password.
  1. Server will request [Yubico Validation Server (YubiCloud)](http://www.yubico.com/for-developers) to verify the OTP using [HTTPS/SSL](http://en.wikipedia.org/wiki/Https) connection.
  1. YubiCloud returns status of OTP. If the status is "OK" process continues, otherwise ends.
  1. Yubikey-mp server will query [High Replication](http://code.google.com/appengine/docs/java/datastore/hr/) datastore for stored password for appropriate [USER](TutorialDatastore#USER) and [PID](TutorialDatastore#PID). Then the password is returned using the secured connection.