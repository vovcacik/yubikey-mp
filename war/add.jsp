<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.yubico.yubikeymp.YubikeyServer"%>
<%@ page import="com.yubico.yubikeymp.YubikeyOTP"%>
<%@ page import="com.yubico.yubikeymp.YubikeySecret"%>
<%
    final YubikeyOTP auth = YubikeyOTP.createInstance(request.getParameter("auth"));
	final boolean isAdmin = YubikeyServer.hasAdminAccess(auth);

%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<title>Yubikey Master Password server - add new password</title>
</head>

<body onLoad="document.forms[0].elements[0].focus();">
<%  if (isAdmin) {
    	final YubikeySecret secret = YubikeySecret.createInstance(request.getParameter("user"), request.getParameter("pid"), request.getParameter("secret"));
    	if (YubikeyServer.put(secret)){
			%>New password saved.<%
    	} else {
    	    %>Password was not saved.<%
    	}
    	// Show add password form
    	%><br /><br />
    	<form name="add" action="/add.jsp" method="post">
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
	} else {
	    // UNAUTHORIZED
	    response.sendError(401);
	}
%>
</body>
</html>