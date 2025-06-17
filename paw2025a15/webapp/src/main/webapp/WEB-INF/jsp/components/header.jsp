<%@ page contentType="charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
  <head>
    <meta charset="UTF-8"></meta>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
    <title>
      CareTrace<c:if test="${param.title != null}"> - <spring:message code="header.${param.title}"></spring:message></c:if>
    </title>
    <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
    <link rel="stylesheet" href="<c:url value="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200&icon_names=login"/>"  />
    <link rel="stylesheet" href="<c:url value="/css/main.css" />" />
  </head>
  <body>
    <c:set var="imgSrc">
      <c:url value="/resources/icono.jpg"/>
    </c:set>
    <div class="topbar">
      <div class="logo">
        <a href="<c:url value="/"/>" class="logo-link">
          <img
            src="${imgSrc}"
            alt="<spring:message code="image.alt.logo" />"
          />
        </a>
      </div>
      <c:if test="${user_data != null}">
        <nav class="nav-links">
          <sec:authorize access="hasRole('ROLE_PATIENT') or hasRole('ROLE_DOCTOR')">
            <a href="<c:url value="/appointments"/>" class="nav-item <c:if test='${param.title eq "appointments"}'>active</c:if>">
              <spring:message code="header.appointments" />
            </a>
            <sec:authorize access="hasRole('ROLE_PATIENT')">
              <a href="<c:url value="/studies"/>" class="nav-item <c:if test='${param.title eq "studies"}'>active</c:if>">
                <spring:message code="header.studies" />
              </a>
            </sec:authorize>
          </sec:authorize>
        </nav>
      </c:if>
      <c:set var="barPlaceholder">
        <sec:authorize access="not isAuthenticated() or hasRole('ROLE_PATIENT')">
          <spring:message code="header.patient.placeholder"/>
        </sec:authorize>
        <sec:authorize access="hasRole('ROLE_DOCTOR') or hasRole('ROLE_LABORATORY')">
          <spring:message code="header.doctor.placeholder"/>
        </sec:authorize>
      </c:set>
      <c:set var="searchLink">
        <c:url value="/home" />
      </c:set>

      <sec:authorize access="!hasRole('ROLE_ADMIN')">
        <div class="search-bar-container">
          <div class="search-bar">
            <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20">
              <path fill="white" d="M10 2a8 8 0 015.29 13.71l3.94 3.94-1.42 1.42-3.94-3.94A8 8 0 1110 2m0 2a6 6 0 104.24 10.24A6 6 0 0010 4z"/>
            </svg>
            <form:form modelAttribute="landingForm" action="${searchLink}" method="get" class="search-bar-form">
              <form:input path="query" class="search-bar-text" placeholder="${barPlaceholder}" />
              <button type="submit" style="display: none;"></button>
              <sec:authorize access="not isAuthenticated() or hasRole('ROLE_PATIENT')">
                <form:input type="hidden" path="insurances"/>
                <form:input type="hidden" path="weekday"/>
                <form:input type="hidden" path="specialty"/>
              </sec:authorize>
            </form:form>
          </div>
        </div>
      </sec:authorize>
      <sec:authorize access="hasRole('ROLE_ADMIN')">
        <span style="display: flex; flex: 1"></span>
      </sec:authorize>      
      <!-- Show only if user IS authenticated -->
      <c:if test="${user_data != null}">
        <button id="userBtn" class="user-btn" onclick="toggleUserDropdown()">
          <div class="user-image">
            <img
              src="<c:url value='/supersecret/user-profile-pic/${user_data.id}'/>"
              alt="<spring:message code='image.alt.userProfile'/>"
            />
          </div>
          <div class="user-info">
            <p class="user-name"><c:out value="${user_data.name}" escapeXml="true"/></p>
            <p class="user-role"><spring:message code="role.${user_data.role}" /></p>
          </div>
        </button>
        <div id="userDropdownMenu" class="user-dropdown-menu">
          <sec:authorize access="!hasRole('ROLE_ADMIN')">
            <c:url value="/profile" var="profile"/>
            <a href="${profile}"><spring:message code="header.profile"/></a>
          </sec:authorize>
          <c:url value="/logout" var="logout"/>
          <a href="${logout}"><spring:message code="header.logout"/></a>
        </div>
      </c:if>

      <!-- Show only if user is NOT authenticated -->
      <c:if test="${user_data == null}">
        <a href="<c:url value="/login"/>">
          <button class="login-btn">
            <span class="login-icon material-symbols-outlined">
              login
            </span>
            <spring:message code="header.login" />
          </button>
        </a>
      </c:if>
    </div>
    <script>
      function toggleUserDropdown() {
        const dropdown = document.getElementById("userDropdownMenu");
        const button = document.getElementById("userBtn");

        dropdown.classList.toggle("show");
        button.classList.toggle("active");

        // Optional: Close dropdown when clicking outside
        document.addEventListener("click", function (event) {
          const isClickInside = button.contains(event.target) || dropdown.contains(event.target);

          if (!isClickInside) {
            dropdown.classList.remove("show");
            button.classList.remove("active");
          }
        }, { once: true });
      }
    </script>
    <script src="<c:url value='/js/buttonControl.js'/>"></script>
  </body>
</html>