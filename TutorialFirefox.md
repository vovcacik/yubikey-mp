# Introduction #

Yubikey-mp project has two parts. Server and Firefox extension. Server stores data, passwords, user details etc. Firefox extension handles Yubikey OTP insertion and queries the server for result.

The yubikey-mp server is independent on the extension. You could easily create your own extension for Firefox, Chrome or other browsers. And not only browsers!

# Settings #
To get latest version of **Yubikey Master Password for Firefox** extension go to https://addons.mozilla.org/cs/firefox/addon/yubikey-master-password-ff/.

Once you have the extension installed. Stop looking for settings windows, extension icon or anything else. It has no GUI, yet. Instead go to "about:config" and filter with "extensions.yubikey-mp". You will see two entries:
  * extensions.yubikey-mp.server
  * extensions.yubikey-mp.mpid
Both are empty. To start using the extension you have to set:
  * extensions.yubikey-mp.server to address of your yubikey-mp server. For example: "yubikey-mp-demo.appspot.com". See summary in [deployment tutorial](TutorialDeployment.md).
  * extensions.yubikey-mp.mpid to a value of PID which is defined in datastore. For example "firefox". See summary in [datastore tutorial](TutorialDatastore.md).

# Usage #
todo

- you have to set master password in firefox

- in prompt just paste your OTP and click ok, press enter etc.