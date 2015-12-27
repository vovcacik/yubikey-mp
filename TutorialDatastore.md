# Introduction #

Yubikey-mp server runs on Google App Engine. To store data we use High Replication datastore. If you upload your own yubikey-mp server, the datastore will be empty. After you initialize yubikey-mp server the datastore will contain empty templates, which you can populate via web interface. How to upload and initialize server take a look at [deployment tutorial](TutorialDeployment.md).

# How to edit datastore #

<font color='red'>ALWAYS ACCESS GOOGLE APP ENGINE DASHBOARD AND OTHER APP ENGINE PAGES USING HTTPS (SSL) CONNECTION. OTHERWISE YOUR SECRETS MAY LEAK TO THE PUBLIC. </font> [Read the details here](Weaknesses.md).

To view and edit stored data go to your app's dashboard (via https://appengine.google.com/). From right panel select **Datastore Viewer**. If you see some rows (called entities) you can edit row's value after clicking **id=1** (there may be 1 or some other number).

To create new entity/row/entry click **Create** button on **Datastore Viewer** page. Select kind (you probably want to select _Secrets_ kind). Leave namespace empty and click **Next**. Fill values you need and click **Save**.

# Data model description #
The datastore contains two groups of data:
  * Prefs
  * Secrets
These groups are called _kinds_ in App Engine environment. Their description follows.

## Prefs ##

Prefs stores settings of server. These preferences are:
  * _initialized_ - this preference will be set to true after you initialize yubikey-mp server. Do not change its value.
  * _admin_ - empty by default. Do nothing at this moment. Ignore.
  * _clientid_ - This is API Client ID (aka API key). You may leave it blank or if you are going to use the yubikey-mp server in the future get your own Client ID at https://upgrade.yubico.com/getapikey/. Once you got your own ID set _clientid_ to use your ID.

## Secrets ##

Secrets kind consist of **user**, **pid**, **secret** parameters. There should be one blank row, which you can easily edit. Or create new entity if you want to store more users and/or more passwords.

At present time you have to set user, pid and secret manually.

### USER ###
This parameter identifies user. You can have more users in yubikey-mp server. The value of _user_ parameter is always static part of user's Yubikey OTP. It is also known as Yubikey ID or Public ID. The bold part is static part which may have length 0 to 12 characters and dynamic part is always 32 characters long.

**fifjgjgkhchb** irdrfdnlnghhfgrtnnlgedjlftrbdeut

**fifjgjgkhchb** gefdkbbditfjrlniggevfhenublfnrev

**fifjgjgkhchb** lechfkfhiiuunbtnvgihdfiktncvlhck

_Note that yubikey-mp server requires you to have the static part exactly 12 ModHex characters long._

### PID ###
Password ID (PID) identifies password. It is something like nickname for your password. Set it to anything you want, but don't set it to your password value.

The PID should be unique in context of single user. Always check that you don't use one PID for two passwords of a user. For example set it to **"firefox"**.

### SECRET ###
This parameter contains your password. Insert your password in readable form, unencrypted or anything else. For example "**myStr0ngPazzvvOrd".**

# Summary #
In the examples above we have created this table:

|user|pid|secret|
|:---|:--|:-----|
|fifjgjgkhchb|firefox|myStr0ngPazzvvOrd|

Now it is ready to be used by **Yubikey Master Password for Firefox** extension. Check out [Firefox tutorial](TutorialFirefox.md) for more details.