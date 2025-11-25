<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<c:set var="redirectLink"
       value="${pageContext.request.contextPath}/patient/${param.id}" />

<div class="doctor-card patient-card">
  <a href="${redirectLink}" class="doctor-card__link">

    <!-- Avatar -->
    <div class="doctor-card__avatar">
      <img
        src="<c:url value='/supersecret/user-profile-pic/${param.id}'/>"
        alt="<spring:message code='image.alt.patientProfile'/>"
      />
    </div>

    <!-- Info -->
    <div class="doctor-card__info">
      <h3 class="doctor-card__name">
        <c:out value="${param.patientName}" escapeXml="true" />
      </h3>
      <p class="patient-card__contacts">
        <strong>Email:</strong> <c:out value="${param.email}" escapeXml="true"/>
      </p>
      <p class="patient-card__contacts">
        <strong>Tel:</strong> <c:out value="${param.telephone}" escapeXml="true"/>
      </p>
    </div>

  </a>
</div>