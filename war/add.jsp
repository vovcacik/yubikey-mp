<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.yubico.yubikeymp.*"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%
	// Atributes declaration
	boolean isAdmin = false;
    String admin = null;

	// Parameters
	final YubikeyOTP auth = YubikeyOTP.createInstance(request.getParameter("auth"));
	final String user = request.getParameter("user");
	final String pid = request.getParameter("pid");
	String secret = request.getParameter("secret");

	// Get admins name
	final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	final Query query = new Query("Prefs");
	Iterator<Entity> iterator = datastore.prepare(query).asIterator();
	
	if (iterator.hasNext()){ //TODO check only latest entity
	    Object o = iterator.next().getProperty("admin");
		if (o instanceof String){
		    admin = (String) o;
		}
	}
	
	// Determine admin access
    if (admin != null && auth != null && admin.equals(auth.getStaticPart()) && auth.verify()) {
        isAdmin = true;
    }

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

<body>
<%  if (isAdmin) {
    	if (user != null && pid != null && secret != null && YubikeyOTP.isOTP(user + "cbdefghijklnrtuvcbdefghijklnrtuv")) {
			// TODO check pid is unique
			// TODO check secret is non-empty string
			Entity secrets = new Entity("Secrets");
			secrets.setProperty("user", user);
			secrets.setProperty("pid", pid);
			secrets.setProperty("secret", KingdomKey.encrypt(secret));
			datastore.put(secrets);
			secret = null;
			%>New password saved.<br /><br /><%
    	}
    	// Show add password form
    	%><form name="add" action="/add.jsp" method="post">
    		User <input type="text" name="user" /><br />
			PID <input type="text" name="pid" /><br />
			Secret <input type="text" name="secret" /><br /><br />
			Auth <input type="text" name="auth" />
			<input type="submit" value="Submit" />
		</form><%
	}
%>
</body>
</html>