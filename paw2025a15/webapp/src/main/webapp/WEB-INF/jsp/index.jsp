<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="UTF-8">
    <title>CareTrace</title>
    <link rel="icon" href="<c:url value='/resources/favicon.png'/>">
    <!-- CSS base compartido -->
    <link rel="stylesheet" href="<c:url value='/css/base.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/landing-page.css'/>">
  </head>
  <body class="landing-page">
    <jsp:include page="components/header.jsp"> <jsp:param name="username" value="${user.name}"/>
      <jsp:param name="pictureId" value="${user.pictureId}"/> <jsp:param name="role" value="${user.role}"/>
    </jsp:include>
    <div class="page-container">

      <!-- FILTROS -->
      <c:if test="${user.role=='PATIENT' || pageContext.request.userPrincipal==null}">
        <div class="card filter-card" >
          <h2 class="section-title"><spring:message code="landing.filter.title"/></h2>
          <c:url value='/' var="filterUrl"/>
          <form:form modelAttribute="landingForm"
                    action="${filterUrl}"
                    method="get"
                    class="landing-form"
          >
            <form:input type="hidden" path="query"/>
            <!-- Seguro -->
            <div class="field-container">
              <form:label path="insurances" class="field-label">
                <spring:message code="doctorForm.obrasSociales"/>
              </form:label>
              <form:select path="insurances" class="filter-select">
                <form:option value="">
                  <spring:message code="landing.filter.insurances"/>
                </form:option>
                <form:options items="${insurances}"
                              itemLabel="name"
                              itemValue="id"/>
              </form:select>
            </div>

            <!-- Día -->
            <div class="field-container">
              <form:label path="weekday" class="field-label">
                <spring:message code="doctorForm.schedules.weekday"/>
              </form:label>
              <form:select path="weekday" class="filter-select">
                <form:option value="">
                  <spring:message code="landing.filter.weekdays"/>
                </form:option>
                <form:options items="${weekdaySelectItems}"
                              itemLabel="label"
                              itemValue="value"/>
              </form:select>
            </div>

            <!-- Especialidad -->
            <div class="field-container">
              <form:label path="specialty" class="field-label">
                <spring:message code="doctorForm.specialty"/>
              </form:label>
              <form:select path="specialty" class="filter-select">
                <form:option value="">
                  <spring:message code="landing.filter.specialty"/>
                </form:option>
                <form:options items="${specialtySelectItems}"
                              itemLabel="label"
                              itemValue="value"/>
              </form:select>
            </div>

            <button type="submit" class="filter-button">
              <spring:message code="landing.filter"/>
            </button>
          </form:form>
        </div>

        <!-- LISTADO DE DOCTORES -->
        <h2 class="section-title"><spring:message code="landing.title"/></h2>
        <c:choose>
          <c:when test="${not empty docList}">
            <div class="doctor-landing-container" style="display: flex; justify-content: center; flex-wrap: wrap; gap: 20px;">
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
          </c:when>
          <c:otherwise>
            <div style="text-align:center; padding:40px;">
              <p class="subtitle"><spring:message code="landing.noDoctors"/></p>
              <a href="<c:url value='/'/>" class="no-doctors-button">
                <spring:message code="landing.noDoctors.button" />
              </a>
            </div>
          </c:otherwise>
        </c:choose>
      </c:if>

      <c:if test="${user.role == 'DOCTOR' || user.role == 'LABORATORY'}">
        <div class="">
          <!-- 1) Título idéntico -->
          <h2 class="section-title">
            <spring:message code="landing.doctor.title"/>
          </h2>
          <!-- 2) Elegimos entre listado o mensaje vacío -->
          <c:choose>
            <c:when test="${not empty patients}">
              <!-- 3) grid de tarjetas idéntico al de doctores -->
              <div class="card-grid">
                <c:forEach var="patient" items="${patients}">
                  <jsp:include page="components/patientCard.jsp">
                    <jsp:param name="id"          value="${patient.id}" />
                    <jsp:param name="patientName" value="${patient.name}" />
                    <jsp:param name="imageId"     value="${patient.pictureId}" />
                    <jsp:param name="email"       value="${patient.email}" />
                    <jsp:param name="telephone"   value="${patient.telephone}" />
                  </jsp:include>
                </c:forEach>
              </div>
            </c:when>
            <c:otherwise>
              <div class="no-data-message">
                <p class="subtitle">
                  <spring:message code="landing.noPatients"/>
                </p>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
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
      <c:if test="${totalPages != 0}">
        <c:url value='/' var="pageUrl"/>
        <form:form
          modelAttribute="landingForm" 
          method="get" action="${pageUrl}"
          class="page-navigator-div"
          style="margin-block-end: 0px;"
        >
          <input type="hidden" name="page" id="pageInput" value="${page}" />
          <button
            type="submit"
            class="page-navigation-button"
            onclick="document.getElementById('pageInput').value = ${1}"
            <c:if test="${page == 1}">disabled</c:if>
          >
            <spring:message code="landing.pagination.first"/>
          </button>

          <button
            type="submit"
            class="page-navigation-button"
            style="margin-right: 3px;"
            onclick="document.getElementById('pageInput').value = ${page - 1}"
            <c:if test="${page <= 1}">disabled</c:if>
          >
            <spring:message code="doctorDetail.previousWeek"/>
          </button>

          <div class="page-button">
            ${page} / ${totalPages}
          </div>

          <button
            type="submit"
            class="page-navigation-button"
            onclick="document.getElementById('pageInput').value = ${page + 1}"
            <c:if test="${page >= totalPages}">disabled</c:if>
          >
            <spring:message code="doctorDetail.nextWeek"/>
          </button>

          <button
            type="submit"
            class="page-navigation-button"
            style="margin-right: 3px;"
            onclick="document.getElementById('pageInput').value = ${totalPages}"
            <c:if test="${page == totalPages}">disabled</c:if>
          >
            <spring:message code="landing.pagination.last"/>
          </button>
          <form:input type="hidden" path="query"/>
          <form:input type="hidden" path="insurances"/>
          <form:input type="hidden" path="weekday"/>
          <form:input type="hidden" path="specialty"/>
        </form:form>
      </c:if>
    </div>
  </body>
</html>