# Tutorial: How to deploy your own yubikey-mp server #

## Introduction ##

This tutorial will walk you through process of deploying your own yubike-mp server.

You will need:
  * a Google account.
  * to download some files.
  * no money at all. Running yubikey-mp server is free on Google App Engine.

## Steps ##

**1. Get a Google account**

If you don't have a Google account you have to create one at https://www.google.com/accounts/NewAccount.

**2. Create new App Engine application**
  * Go to https://appengine.google.com/ and click "Create Application".
    * Create any **Application Identifier** (remember the identifier we will need it in step 3). For example: "yubikey-mp-demo".
    * Create any **Application Title** (this is displayed name of your app).
    * Leave **Authentication Options** on default values.
    * Leave **Storage Options** default value **High Replication** (this should be default but make sure you are set to **High Replication**).
    * Click **Create Application** to finish the process.
Now you should have an empty application. Each application has its dashboard, where you can see status, change settings, view logs and edit datastore. You can now return to https://appengine.google.com/ and click on your new app to get to the app's dashboard. From the dashboard you see it is empty. Follow next steps to upload yubikey-mp to your app.

**3. Download latest yubikey-mp server**
  * Go to http://code.google.com/p/yubikey-mp/downloads/list and download latest version.
  * Unzip the archive.
  * Locate this file **yubikey-mp\war\WEB-INF\appengine-web.xml**
  * Open it in an editor and put your **Application Identifier** into `<`application`>` tag. For example:
`<application>yubikey-mp-demo</application>`

**4. Download latest Google App Engine SDK for Java**
  * Go to http://code.google.com/appengine/downloads.html#Google_App_Engine_SDK_for_Java
  * Download **Google App Engine SDK for Java**
    * Or use this direct link for 1.5.0.1 version: http://googleappengine.googlecode.com/files/appengine-java-sdk-1.5.0.1.zip
  * Unzip the archive.

**5. Upload yubikey-mp server**
  * You have unzipped the sdk in step 4.
  * To upload yubikey-mp server you need to run **appcfg.cmd** with two parameters: **update** and **path/to/war/folder**. The appcfg.cmd is located in App Engine SDK folder **appengine-java-sdk\bin**. For example:
    * `appengine-java-sdk\bin\appcfg.cmd update yubikey-mp/war`
    * `c:\appengine-java-sdk-1.5.0.1\bin\appcfg.cmd update c:/yubikey-mp/war`

You will be probably prompted for your Google email address and password during uploading. Both email address and your password is sent via HTTPS/SSL connection. If you are using Google's two factor authentication you need to create application specific password in your Google profile.

_Note that the path to war folder uses forward slash / instead of back slash \. It is important that you use forward slashes too._

**Appcfg.cmd** should display that you successfully uploaded new version. It means you have uploaded yubikey-mp server to your App Engine application successfully.

**5. Initializing server**

Before you start using the server visit it in your browser. The URL address consist from **Application Identifier** and appspot.com. For example: `yubikey-mp-demo.appspot.com`. If the initialization were successful you will see on the page `initialization successful`.

_The initialization runs only first time you visit `*`.appspot.com, so next time you will see only empty page._

**6. Populate datastore**

See our [datastore tutorial](TutorialDatastore.md) for details.

**7. Set up your Firefox extension**

See our [Firefox tutorial](TutorialFirefox.md) for details.

# Summary #

In this tutorial we have successfully created yubikey-mp server with URL address: "yubikey-mp-demo.appspot.com".