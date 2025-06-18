<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="redirectLink" value="${pageContext.request.contextPath}/doctors/${param.id}" />

<div class="doctor-card card">
  <a href="${redirectLink}" class="doctor-card__link">

    <!-- Avatar -->
    <div class="doctor-card__avatar">
      <img
        src="<c:url value='/supersecret/user-profile-pic/${param.id}'/>"
        alt="<spring:message code='image.alt.doctorProfile'/>"
      />
    </div>

    <!-- Datos principales -->
    <div class="doctor-card__info">
      <h3 class="doctor-card__name">
        <c:out value="${param.doctorName}" escapeXml="true" />
      </h3>
      <p class="doctor-card__specialty">
        <spring:message code="specialty.${param.specialty}"/>
      </p>
      <p class="doctor-card__insurances">
        <c:out value="${fn:replace(fn:replace(param.insurances,'[',''),']','')}" escapeXml="true" />
      </p>
    </div>

    <!-- Horarios -->
    <div class="doctor-card__schedule">
      <c:set var="allWeekdays"
             value="MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY"/>
      <c:forEach var="wd" items="${fn:split(allWeekdays, ',')}">
        <c:choose>
          <c:when test="${fn:contains(param.weekdays, wd)}">
            <span class="weekday-tag day-active">
              <spring:message code="weekday.${wd}.initial"/>
            </span>
          </c:when>
          <c:otherwise>
            <span class="weekday-tag">
              <spring:message code="weekday.${wd}.initial"/>
            </span>
          </c:otherwise>
        </c:choose>
      </c:forEach>
    </div>

  </a>
</div>