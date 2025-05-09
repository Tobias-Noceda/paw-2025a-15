<%@ page isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
  <head>
    <meta charset="UTF-8">
  </head>
  <body>
    <jsp:include page="genericError.jsp" >
      <jsp:param name="errorCode" value="418"/>
    </jsp:include> 
  </body>
</html>