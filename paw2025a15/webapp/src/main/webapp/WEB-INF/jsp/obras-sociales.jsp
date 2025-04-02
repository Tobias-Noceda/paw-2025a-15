<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="resources/favicon.png"/>" />
  </head>
  <body>
    <c:set var="title">
      <spring:message code="header.ensurance"/>
    </c:set>
    <jsp:include page="header.jsp">
      <jsp:param name="title" value="${title}"/>
    </jsp:include>
    <h2>${title}</h2>
  </body>
</html>
