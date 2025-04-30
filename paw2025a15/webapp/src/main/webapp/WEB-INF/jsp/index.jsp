<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>CareTrace</title>
  <link rel="icon" href="<c:url value='/resources/favicon.png'/>">
  <!-- CSS base compartido -->
  <link rel="stylesheet" href="<c:url value='/css/base.css'/>">

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
      <c:url value='/filter' var="filterUrl"/>
      <form:form modelAttribute="filterForm"
                 action="${filterUrl}"
                 method="get"
                 class="landing-form">

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
        <div class="doctor-landing-container"  flex-wrap:wrap; gap:20px;">
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
        </div>
      </c:otherwise>
    </c:choose>
  </c:if>

  <!-- SIMILAR para DOCTOR/LABORATORY ... -->

  <c:if test="${user.role == 'DOCTOR' || user.role == 'LABORATORY'}">

    <!-- envolvemos todo en la misma tarjeta que usamos en landing -->

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
</div>
</body>
</html>