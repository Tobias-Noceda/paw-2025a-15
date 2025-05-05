<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
      CareTrace <c:if test="${param.title != null}"> - <spring:message code="header.${param.title}"></spring:message></c:if>
      </title>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/main.css" />" />
  </head>
  <body>
    <c:set var="imgSrc">
      <c:url value="/resources/icono.jpg"/>
    </c:set>
    <div class="topbar">
      <div class="logo">
        <a href="<c:url value="/"/>" class="logo-link">
          <img src="${imgSrc}" alt="Logo">
        </a>
      </div>
      <nav class="nav-links">
        <a href="<c:url value="/appointments"/>" class="nav-item <c:if test='${param.title == "appointments"}'>active</c:if>">
          <spring:message code="header.appointments" />
        </a>
        <a href="<c:url value="/studies/6"/>" class="nav-item <c:if test='${param.title == "studies"}'>active</c:if>">
          <spring:message code="header.studies" />
        </a>
      </nav>
      <c:set var="barPlaceholder">
        <c:if test="${user.role == 'PATIENT'}">
          <spring:message code="header.patient.placeholder"/>
        </c:if>
        <c:if test="${user.role == 'DOCTOR'}">
          <spring:message code="header.doctor.placeholder"/>
        </c:if>
        
      </c:set>

      <c:set var="searchLink">
        <c:url value="/search" />
      </c:set>

      <div class="search-bar-container">
        <div class="search-bar">
          <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20">
            <path fill="white" d="M10 2a8 8 0 015.29 13.71l3.94 3.94-1.42 1.42-3.94-3.94A8 8 0 1110 2m0 2a6 6 0 104.24 10.24A6 6 0 0010 4z"/>
          </svg>
          <form:form modelAttribute="landingForm" action="${searchLink}" method="get" class="search-bar-form">
            <form:input path="query" class="search-bar-text" placeholder="${barPlaceholder}" />
            <button type="submit" style="display: none;"></button>
          </form:form>
          <form:hidden path="insurances">
          <form:hidden path="weekday">
          <form:hidden path="specialty">
        </div>
      </div>
      <!-- Show only if user IS authenticated -->
      <c:if test="${pageContext.request.userPrincipal != null}">

        <a href="<c:url value="/profile"/>">
          <button class="doctor-btn">
            <spring:message code="header.profile"/>
          </button>
        </a>


        <a href="<c:url value="/logout"/>">
          <button class="doctor-btn">
            <spring:message code="header.logout" />
          </button>
        </a>
      </c:if>

      <!-- Show only if user is NOT authenticated -->
      <c:if test="${pageContext.request.userPrincipal == null}">
        <a href="<c:url value="/login"/>">
          <button class="doctor-btn">
            <spring:message code="header.login" />
          </button>
        </a>
      </c:if>




      </div>

      </div>
    </div>
  </body>
</html>