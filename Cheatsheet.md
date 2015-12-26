# Used terms #

### General terms ###

**Yubico**: `Swedish company manufacturing Yubikeys.`

**Yubikey**: `USB cryptographic device generating one time passwords (OTP).`

**OTP**: `One time password resistant to keyloggers, eavesdropping, replay attacks etc.`

### Datastore terms ###

**Kind**: `term used in Google datastore. You can imagine it as a table.`

**Entity**: `unit of information in Google datastore. Has properties. You can imagine it as a one line in a table.`

**Property**: `is a value binded to an entity. You can imagine it as a cell. Also it can be name of column in a table.`

**Secrets**: `datastore kind holding all secret entities.`

**user**: `property of secret entities. It's value is Yubikey OTP static part (aka Yubikey ID). This identifies user.`

**pid**: `property of secret entities. It's value is user-defined string. This identifies user's passwords (it is something like password's nickname).`

**secret**: `property of secret entities. It's value is unencrypted password. Only authorized user may access this.`

---

# Yubikey-mp server #

**Author**: `Vlastimil Ovčáčík`

**Open source**: `yes, under Apache License 2.0`

**Demo instance**: `does not exist, deploy your own server`

# Yubikey-mp server instances #

**Hosting**: `Google App Engine`

**Price**: `free (exceeding quotas will not result in any charges)`

**Quotas**: `Free Default Quota ([http://code.google.com/appengine/docs/quotas.html details])`

---

# Used example #

John Doe owns Yubikey from Swedish company Yubico. John Doe's identity is not required or stored in any way. He is identified as a owner of Yubikey with particular static part (aka Yubikey ID). The Yubikey has default settings from manufacture:

**Mode**: `Yubikey OTP`

**Static Part of OTP**: `fifjgjgkhchb`

John Doe uses Mozilla Firefox as his browser. He also uses master password to protect login credentials in his Firefox profile.

**Master password**: `myStr0ngPazzvvOrd`

**Login credentials**: `username and password to many websites. Yubikey-mp server does not read, modify or write to the Firefox password database.`

# Yubikey-mp Firefox extension #

John Doe has also installed "Yubikey Master Password for Firefox" extension, which let him use his Yubikey OTP to unlock the Firefox password database (which is protected with the master password). The extension has this settings:

**extensions.yubikey-mp.server**: `yubikey-mp-demo.appspot.com`

**extensions.yubikey-mp.mpid**: `firefox`

# Yubikey-mp server instance #

The server used is his own server. John Doe's server details:

**Application identifier**: `yubikey-mp-demo`

**Address**: `yubikey-mp-demo.appspot.com`