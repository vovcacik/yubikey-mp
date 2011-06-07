<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.yubico.yubikeymp.*"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Blob"%>
<%
	// Atributes declaration
	boolean isAdmin = false;
    String admin = YubikeyUtil.getAdminName();
	final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	// Parameters
	final YubikeyOTP auth = YubikeyOTP.createInstance(request.getParameter("auth"));
	isAdmin = YubikeyUtil.isAdminsOTP(auth);
	final String user = request.getParameter("user");
	final String pid = request.getParameter("pid");
	String secret = request.getParameter("secret"); //TODO store in char[]

%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<%  if (isAdmin) {
	    %><title>Yubikey Master Password server - add new password</title><%
	} else {
	    %><title></title><%
	}
%>
</head>

<body onLoad="document.forms[0].elements[0].focus();">
<%  if (isAdmin) {
    	if (user != null && pid != null && secret != null && YubikeyOTP.isOTP(user + YubikeyUtil.MODHEX + YubikeyUtil.MODHEX)) {
			// TODO check pid is unique
			// TODO check secret is non-empty string
			Entity secrets = new Entity("Secrets");
			KingdomKey kk = new KingdomKey();
			secrets.setProperty("user", user);
			secrets.setProperty("pid", pid);
			secrets.setProperty("secret", kk.encrypt(secret));
			secrets.setProperty("iterations", kk.getIterations());
			secrets.setProperty("salt", new Blob(kk.getSalt()));
			secrets.setProperty("iv", new Blob(kk.getIV()));
			datastore.put(secrets);
			secret = null;
			%>New password saved.<br /><br /><%
    	}
    	// Show add password form
    	%><form name="add" action="/add.jsp" method="post">
    		<label>
    			<span>User</span>
    			<input type="text" name="user" />
    		</label>
			<label>
				<span>PID</span>
				<input type="text" name="pid" />
			</label>
			<label>
				<span>Secret</span>
				<input type="text" name="secret" />
			</label>
			<label>
				<span>Auth</span>
				<input type="text" name="auth" />
				<input type="submit" value="Submit" />
			</label>
		</form><%
	}
%>
</body>
</html>