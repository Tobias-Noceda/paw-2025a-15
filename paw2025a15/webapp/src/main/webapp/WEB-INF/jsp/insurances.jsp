<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
  </head>
  <body>
    <c:set var="title">
      <spring:message code="header.ensurance"/>
    </c:set>
    <jsp:include page="components/header.jsp">
      <jsp:param name="title" value="${title}"/>
    </jsp:include>
    <div class="page-container">
      <h2>${title}</h2>
    </div>
  </body>
</html>
