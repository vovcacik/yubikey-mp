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
	boolean isInitialized = true;

	// Parameters
	final YubikeyOTP auth = YubikeyOTP.createInstance(request.getParameter("auth"));

	// Check if server need to initialize
	final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	final Query query = new Query("Prefs");
	Iterator<Entity> iterator = datastore.prepare(query).asIterator();
	isInitialized = iterator.hasNext();
	
	// Get admins name
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
<%  if (isAdmin) {
	    %><title>Yubikey Master Password server</title><%
	} else {
	    %><title></title><%
	}
%>
</head>

<body>
<%  if (!isInitialized) {
    	// Show initialize form
    	%>Initialization required.<br /><br />
    	<form name="init" action="/init.jsp" method="post">
			Auth <input type="text" name="auth" />
			<input type="submit" value="Submit" />
		</form><%
	} else if (isAdmin) {
	    if (KingdomKey.isSet()) {
	    	// Show add password form
	    	%><form name="add" action="/add.jsp" method="post">
		    	User <input type="text" name="user" /><br />
	    		PID <input type="text" name="pid" /><br />
	    		Secret <input type="text" name="secret" /><br /><br />
				Auth <input type="text" name="auth" />
				<input type="submit" value="Submit" />
			</form><%
	    } else {
	        // Show form for setting up kingdom key
	        %><form name="kk" action="/kk.jsp" method="post">
    			Key to the kingdom <input type="text" name="kk" /><br /><br />
				Auth <input type="text" name="auth" />
				<input type="submit" value="Submit" />
			</form><%
	    }
	} else {
	    // Show login form
		%><form name="login" action="/" method="post">
			Auth <input type="text" name="auth" />
			<input type="submit" value="Submit" />
		</form><%
	}
%>
</body>
</html>