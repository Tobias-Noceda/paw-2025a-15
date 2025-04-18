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
            <h3><c:out value="${param.doctorName}"/></h3>
            <p><spring:message code="specialty.${param.speciality}"/></p>
            <p><c:out value="${fn:replace(fn:replace(param.insurances, '[', '') ,']', '')}"/></p>
          </div>
        </div>
        <div class="doctor-schedule">
          <c:set var="allWeekdays" value="MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY"/>
          <c:forEach var="weekday" items="${fn:split(allWeekdays, ',')}">
            <c:choose>
              <c:when test="${fn:contains(param.weekdays, weekday)}">
                <p class="weekday-tag day-active"><spring:message code="weekday.${weekday}.initial"></spring:message></p>
              </c:when>
              <c:otherwise>
                <p class="weekday-tag"><spring:message code="weekday.${weekday}.initial"></spring:message></p>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </div>
      </a>
    </div>
  </body>
</html>
