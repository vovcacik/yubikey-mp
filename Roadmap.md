# Roadmap #

**[Version 0.1](https://github.com/vovcacik/yubikey-mp/releases/tag/v0.1)**
  * SSL only
  * Unicode support
  * Multiuser support
  * Remote access via /eval URL

**[Version 0.11](https://github.com/vovcacik/yubikey-mp/releases/tag/v0.11)**
  * using SSL to access Yubico Validation Server

**Version 0.2** (expected on 1 July 2011)
  * Keep server always running (with Cron job)
  * Encrypt stored passwords with key to the kingdom (i.e. server master password)
    * Follow PKCS #5 V2.0 recommendation
    * Use AES 128 bit/CBC/PKCS5Padding
      * Use random initialization vector (aka IV)
    * Hash key to the kingdom with PBKDF2WithHmacSHA1 algorithm
      * Hash the key for random number of iterations (5000 - 10000 times by default)
      * Salt the key with random bits (128 bits by default)
    * Iterations, salt and IV is random for each stored secret
  * Provide web interface
    * Keep the interface as simple as possible
    * Avoid cookies - use Yubikey instead
    * Avoid Javascript, Flash etc.
  * Alert admin via email and/or instant messaging on error events

**Version 0.3**
  * Multiadmin support
  * _(to be added)_

**Undecided**
  * Support storing websites credentials - username, password, etc.
  * Use keyfile along key to the kingdom
  * Use true RNG (random.org possibly)
  * Filter request by IP (i.e. IP whitelist/blacklist)
  * Lock server after some number of failed attempts
_(vote for features on yubikey-mp [Moderator page](http://www.google.com/moderator/#16/e=982d2))_