# Introduction #

If you want to test yubikey-mp features without deploying your own yubikey-mp server read on.

I have established a test server for demo purposes. It is not exact yubikey-mp server. The test server is modified for your convenience. It does not store any secret and you will not be asked to provide any secret. You don't even have to provide valid OTP.

It is recommended to create new blank Firefox profile for this demo. If you don't know how:
  1. start your firefox with this command: `firefox.exe -profilemanager`
  1. in the profile manager select to create new profile and follow instructions
  1. select newly created profile and run Firefox

After this follow the [Firefox tutorial](TutorialFirefox.md) or [Firefox video tutorial](http://yubikey-mp.googlecode.com/svn/wiki/swf/firefox.htm) and use values described in the following section.

# Test server details #

**master password**: `myStr0ngPazzvvOrd`

**extensions.yubikey-mp.mpid**: `firefox`

**extensions.yubikey-mp.server**: `yubikey-mp-test.appspot.com`

You may want to save some username and password into Firefox password database. You can use some random username and password on any website - it won't log you in, but the values will be filled - that's enough for testing purposes. Also note that usernames and passwords to websites are not read, modified or delete by yubikey-mp server at any time.

As Yubikey OTP you can use predefined _Test OTP_ or your own Yubikey. The _Test OTP_ is always valid and will always return the master password. If you choose to use your own Yubikey, the generated OTP will be validated against Yubico Validation Server. The master password will or won't be returned depending on the real result from Yubico Validation Server.

**Test OTP**: `cbdefghijklncbdefghijklnrtuvcbdefghijklnrtuv`