**Threat**: Secret/password leak to public.

**Risk**: <font color='red'>High</font>

**Explanation**: Deployed instance of yubikey-mp server is running on Google App Engine. The App Engine provides web interface to administrate the server. The web interface provides Dashboard, Datastore Viewer and many other useful tools. We use the Datastore Viewer to view, add, modify or remove passwords and other entities. When you try to view the passwords in the Datastore viewer, these passwords are transmitted over the Internet with unsecured HTTP connection and it is possible for attacker to see the passwords in plain text.

**Solution**: Use HTTPS/SSL when accessing App Engine Dashboard, Datastore Viewer and other pages. Just overwrite http to https in address bar. If https is used the threat is fully mitigated.

**Comment**:

This threat cannot be fully mitigated by yubikey-mp server itself. Also this is not weakness of yubikey-mp server itself, but the hosting company - Google.

In the future the yubikey-mp server could encrypt saved passwords. This would require asking user for strong static password everytime he uses Yubikey OTP.

Bookmark this https://appengine.google.com/.

---

**Threat**:

**Risk**:

**Explanation**:

**Solution**:

Comment: