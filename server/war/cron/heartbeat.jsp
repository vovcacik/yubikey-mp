<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.yubico.yubikeymp.YubikeyServer"%>
<%@ page import="com.yubico.yubikeymp.KingdomKey"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<title></title>
</head>

<body>
<%  if (YubikeyServer.isInitialized() && !KingdomKey.isSet()) {
    	// Key to the kingdom should be set but is not. Send email to admin each 2 hours.
    	String host = request.getServerName();
    	YubikeyServer.alertAdmin("The " + host + " server is not working properly.", 
    	        "The key to the kingdom is not set. Go to https://" + host + " and set your key to the kingdom.",
    	        1000*60*60*2);
	}
%>
</body>
</html>