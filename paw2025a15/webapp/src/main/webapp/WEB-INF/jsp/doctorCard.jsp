<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
  <head>
    <title>Title</title>
    <link rel="stylesheet" href="<c:url value="/css/landing-page.css"/>"/>
  </head>
  <body>
    <c:set var="redirectLink">
      <c:url value="/doctors/${param.id}" />
    </c:set>
    <%-- userCard.jsp --%>
    <div class="doctor-landing-card">
      <a href="${redirectLink}" class="clickable-card">
        <h3><c:out value="${param.doctorName}"/></h3>
        <p><spring:message code="specialty.${param.speciality}"/></p>
        <p><c:out value="${fn:replace(fn:replace(param.insurances, '[', '') ,']', '')}"/></p>
      </a>
    </div>
  </body>
</html>
