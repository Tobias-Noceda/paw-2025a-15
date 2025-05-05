<%@ page contentType="charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
  <head>
    <meta charset="UTF-8"></meta>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
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
      <c:if test="${pageContext.request.userPrincipal != null}">
        <nav class="nav-links">
          <c:if test="${param.role == 'PATIENT' || param.role == 'DOCTOR'}">
            <a href="<c:url value="/appointments"/>" class="nav-item <c:if test='${param.title == "appointments"}'>active</c:if>">
              <spring:message code="header.appointments" />
            </a>
            <c:if test="${param.role == 'PATIENT'}">
              <a href="<c:url value="/studies"/>" class="nav-item <c:if test='${param.title == "studies"}'>active</c:if>">
                <spring:message code="header.studies" />
              </a>
            </c:if>
          </c:if>
        </nav>
      </c:if>
      <c:set var="barPatientPlaceholder">
        <spring:message code="header.patient.placeholder"/>
      </c:set>
      <c:set var="barDoctorPlaceholder">
        <spring:message code="header.doctor.placeholder"/>
      </c:set>
      <c:set var="patientSearchLink">
        <c:url value="/doctorSearch" />
      </c:set>
      <c:set var="doctorSearchLink">
        <c:url value="/patientSearch" />
      </c:set>

      <div class="search-bar-container">
        <div class="search-bar">
          <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20">
            <path fill="white" d="M10 2a8 8 0 015.29 13.71l3.94 3.94-1.42 1.42-3.94-3.94A8 8 0 1110 2m0 2a6 6 0 104.24 10.24A6 6 0 0010 4z"/>
          </svg>
          <c:if test="${param.role == 'PATIENT' || pageContext.request.userPrincipal == null}">
            <form:form modelAttribute="searchForm" action="${patientSearchLink}" method="get" class="search-bar-form">
              <form:input path="query" class="search-bar-text" placeholder="${barPatientPlaceholder}" />
              <button type="submit" style="display: none;"></button>
            </form:form>
          </c:if>
          <c:if test="${param.role == 'DOCTOR' || param.role == 'LABORATORY'}">
            <form:form modelAttribute="searchForm" action="${doctorSearchLink}" method="get" class="search-bar-form">
              <form:input path="query" class="search-bar-text" placeholder="${barDoctorPlaceholder}" />
              <button type="submit" style="display: none;"></button>
            </form:form>
          </c:if>
        </div>
      </div>
      <!-- Show only if user IS authenticated -->
      <c:if test="${pageContext.request.userPrincipal != null}">
        <button id="userBtn" class="user-btn" onclick="toggleUserDropdown()">
          <div class="user-image">
            <img  src="<c:url value='/supersecret/files/${param.pictureId}'/>" alt="User Image" />
          </div>
          <div class="user-info">
            <p class="user-name"><c:out value="${param.username}"/></p>
            <p class="user-role"><spring:message code="role.${param.role}" /></p>
          </div>
        </button>
        <div id="userDropdownMenu" class="user-dropdown-menu">
          <c:url value="/profile" var="profile"/>
          <a href="${profile}"><spring:message code="header.profile"/></a>
          <c:url value="/logout" var="logout"/>
          <a href="${logout}"><spring:message code="header.logout"/></a>
        </div>
      </c:if>

      <!-- Show only if user is NOT authenticated -->
      <c:if test="${pageContext.request.userPrincipal == null}">
        <a href="<c:url value="/login"/>">
          <button class="login-btn">
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
  </body>
</html>