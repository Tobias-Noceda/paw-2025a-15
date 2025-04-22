<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
  <head>
    <link rel="stylesheet" href="<c:url value="/css/landing-page.css"/>"/>
  </head>
  <body>
    <c:set var="redirectLink">
      <c:url value="/doctors/${param.id}" />
    </c:set>
    <div class="doctor-landing-card">
      <a href="${redirectLink}" class="clickable-card">
        <div class="doctor-info">
          <div class="doctor-image">
            <img src="<c:url value='/supersecret/files/${param.imageId}'/>" alt="Doctor Image" />
          </div>
          <div class="doctor-details">
            <h3><c:out value="${param.patientName}"/></h3>
            <p style="font-size: 13px; margin-top: 5px; margin-bottom: 3px;"><c:out value="${param.email}"/></p>
            <p style="font-size: 13px;"><c:out value="${param.telephone}"/></p>
          </div>
        </div>
      </a>
    </div>
  </body>
</html>
