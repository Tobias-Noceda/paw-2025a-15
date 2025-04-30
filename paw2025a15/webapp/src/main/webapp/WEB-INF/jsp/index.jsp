<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
  <head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/landing-page.css"/>" />
    <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
  </head>
  <body>
    <jsp:include page="components/header.jsp">
      <jsp:param name="username" value="${user.name}"/>
      <jsp:param name="pictureId" value="${user.pictureId}"/>
      <jsp:param name="role" value="${user.role}"/>
    </jsp:include>
    <div class="page-container">
      <c:if test="${user.role == 'PATIENT' || pageContext.request.userPrincipal == null}">
        <div class="landing-filters-box">
          <h1 class="landing-filters-title"><spring:message code="landing.filter.title"></spring:message></h1>
          <c:set var="filterUrl">
            <c:url value="/filter" />
          </c:set>
          <form:form modelAttribute="filterForm" action="${filterUrl}" method="get">
            <div class="landing-filters-container">
              <div class="filter-container">
                <form:label path="insurances" class="filter-label">
                  <spring:message code="doctorForm.obrasSociales"/>
                </form:label>
                <form:select path="insurances" class="filter-select">
                  <form:option value=""><spring:message code="landing.filter.insurances"></spring:message></form:option>
                  <form:options itemLabel="name" itemValue="id" items="${insurances}" />
                </form:select>
              </div>
              
              <div class="filter-container">
                <form:label path="weekday" class="filter-label">
                  <spring:message code="doctorForm.schedules.weekday"/>
                </form:label>
                <form:select path="weekday" class="filter-select">
                  <form:option value=""><spring:message code="landing.filter.weekdays"></spring:message></form:option>
                  <form:options items="${weekdaySelectItems}" itemLabel="label" itemValue="value" />
                </form:select>
              </div>
              
              <div class="filter-container">
                <form:label path="specialty" class="filter-label">
                  <spring:message code="doctorForm.specialty"/>
                </form:label>
                <form:select path="specialty" class="filter-select">
                  <form:option value=""><spring:message code="landing.filter.specialty"></spring:message></form:option>
                  <form:options items="${specialtySelectItems}" itemLabel="label" itemValue="value" />
                </form:select>
              </div>

              <input type="submit" class="filter-button" value="<spring:message code='landing.filter'/>"/>
            </div>
          </form:form>
        </div>
  
        <h2 class="landing-title"><spring:message code="landing.title"></spring:message></h2>
        <c:if test="${not empty docList}">
          <div class="doctor-landing-container">
            <c:forEach var="doctor" items="${docList}">
              <jsp:include page="components/doctorCard.jsp">
                <jsp:param name="id" value="${doctor.id}" />
                <jsp:param name="doctorName" value="${doctor.name}" />
                <jsp:param name="imageId" value="${doctor.imageId}" />
                <jsp:param name="insurances" value="${doctor.insurances}" />
                <jsp:param name="speciality" value="${doctor.specialty}" />
                <jsp:param name="weekdays" value="${doctor.weekdays}" />
              </jsp:include>
            </c:forEach>
          </div>
        </c:if>
        <c:if test="${empty docList}">
          <div class="no-doctors-message-div">
            <h1><spring:message code="landing.noDoctors"></spring:message></h1>
            <a href="<c:url value='/'/>" class="no-doctors-button">
              <spring:message code="landing.noDoctors.button" />
            </a>
          </div>
        </c:if>
      </c:if>
      <c:if test="${user.role == 'DOCTOR' || user.role == 'LABORATORY'}">
        <h2 class="landing-title"><spring:message code="landing.doctor.title"></spring:message></h2>
        <c:if test="${not empty patients}">
          <div class="doctor-landing-container">
            <c:forEach var="patient" items="${patients}">
              <jsp:include page="components/patientCard.jsp">
                <jsp:param name="id" value="${patient.id}" />
                <jsp:param name="patientName" value="${patient.name}" />
                <jsp:param name="imageId" value="${patient.pictureId}" />
                <jsp:param name="email" value="${patient.email}" />
                <jsp:param name="telephone" value="${patient.telephone}" />
              </jsp:include>
            </c:forEach>
          </div>
        </c:if>
        <c:if test="${empty patients}">
          <div class="no-doctors-message-div">
            <h1><spring:message code="landing.noPatients"></spring:message></h1>
          </div>
        </c:if>
      </c:if>
      <c:set var="queryString">
        <c:forEach var="param" items="${paramValues}">
          <c:if test="${param.key != 'page'}">
            <c:forEach var="value" items="${param.value}">
              <c:out value="${fn:escapeXml(param.key)}=${fn:escapeXml(value[0])}&"/>
            </c:forEach>
          </c:if>
        </c:forEach>
      </c:set>
      <div class="page-navigator-div">
        <!-- Previous Button -->
        <form method="get" style="margin-block-end: 0px; margin-right: 3px;">
          ${queryParams}
          <input type="hidden" name="page" value="${1}" />
          <button type="submit" class="page-navigation-button"
            <c:if test="${page == 1}">disabled</c:if>
          >
            <spring:message code="landing.pagination.first"/>
          </button>
        </form>

        <form method="get" style="margin-block-end: 0px; margin-right: 3px;">
          ${queryParams}
          <input type="hidden" name="page" value="${page - 1}" />
          <button type="submit" class="page-navigation-button"
            <c:if test="${page == 1}">disabled</c:if>
          >
            <spring:message code="doctorDetail.previousWeek"/>
          </button>
        </form>

        <div class="page-button">
          ${page} / ${totalPages}
        </div>

        <!-- Next Button -->
        <form method="get" style="margin-block-end: 0px; margin-left: 3px;">
          ${queryParams}
          <input type="hidden" name="page" value="${page + 1}" />
          <button type="submit" class="page-navigation-button"
            <c:if test="${page == totalPages}">disabled</c:if>
          >
            <spring:message code="doctorDetail.nextWeek"/>
          </button>
        </form>

        <form method="get" style="margin-block-end: 0px; margin-left: 3px;">
          ${queryParams}
          <input type="hidden" name="page" value="${totalPages}" />
          <button type="submit" class="page-navigation-button"
            <c:if test="${page == totalPages}">disabled</c:if>
          >
            <spring:message code="landing.pagination.last"/>
          </button>
        </form>
      </div>
    </div>
  </body>
</html>
