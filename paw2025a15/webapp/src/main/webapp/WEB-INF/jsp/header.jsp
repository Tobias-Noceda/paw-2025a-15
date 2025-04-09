<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CareTrace <c:if test="${param.title != null}"> - ${param.title}</c:if></title>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/main.css" />" />
  </head>
  <body>
    <c:set var="studies">
      <spring:message code="header.studies"/>
    </c:set>
    <c:set var="ensurance">
      <spring:message code="header.ensurance"/>
    </c:set>
    <c:set var="imgSrc">
      <c:url value="/resources/icono.jpg"/>
    </c:set>
    <div class="topbar">
      <div class="logo">
        <a href="<c:url value="/"/>" class="logo-link">
          <img src="${imgSrc}" alt="Logo">
        </a>
      </div>
      <!--
      <nav class="nav-links">
        <a href="<c:url value="/studies"/>" class="nav-item <c:if test='${param.title == studies}'>active</c:if>">
          <spring:message code="header.studies" />
        </a>
        <a href="<c:url value="/insurances"/>" class="nav-item <c:if test='${param.title == ensurance}'>active</c:if>">
          <spring:message code="header.ensurance" />
        </a>
      </nav>
      -->
      <c:set var="barPlaceholder">
        <spring:message code="header.placeholder"/>
      </c:set>

      <c:set var="searchLink">
        <c:url value="/search" />
      </c:set>

      <div class="search-bar-container">
        <div class="search-bar">
          <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20">
            <path fill="white" d="M10 2a8 8 0 015.29 13.71l3.94 3.94-1.42 1.42-3.94-3.94A8 8 0 1110 2m0 2a6 6 0 104.24 10.24A6 6 0 0010 4z"/>
          </svg>
          <form:form modelAttribute="searchForm" action="${searchLink}" method="get" class="search-bar-form">
            <form:input path="query" class="search-bar-text" placeholder="${barPlaceholder}" />
            <button type="submit" style="display: none;"></button>
          </form:form>
        </div>

      </div>


      <a href="<c:url value="/doctor-form"/>">
        <button class="doctor-btn">
          <spring:message code="header.doctor" />
        </button>
      </a>
    </div>
  </body>
</html>