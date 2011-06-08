<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.yubico.yubikeymp.YubikeyServer"%>
<%@ page import="com.yubico.yubikeymp.YubikeyOTP"%>
<%@ page import="com.yubico.yubikeymp.KingdomKey"%>
<%
	final YubikeyOTP auth = YubikeyOTP.createInstance(request.getParameter("auth"));
	final boolean isAdmin = YubikeyServer.hasAdminAccess(auth);

%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<title>Yubikey Master Password server - key to the kingdom</title>
</head>

<body>
<%  if (isAdmin) {
    	if (!KingdomKey.isSet()) {
	    	if (KingdomKey.setKey(request.getParameter("kk"))) {
	    	    %>Key to the kingdom set successfully.<%
	    	} else {
	    	    %>Key to the kingdom could not be set.<%
	    	}
    	} else {
    	    %>Key to the kingdom is already set.<%
    	}
	} else {
	 	// UNAUTHORIZED
	    response.sendError(401);
	}
%>
</body>
</html>