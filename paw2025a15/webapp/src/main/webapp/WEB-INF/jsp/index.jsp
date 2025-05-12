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
    <link rel="icon" href="<c:url value='/favicon.ico'/>">
    <!-- CSS base compartido -->
    <link rel="stylesheet" href="<c:url value='/css/base.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/landing-page.css'/>">
  </head>
  <body class="landing-page">
    <jsp:include page="components/header.jsp"/>
    <div class="page-container">

      <!-- FILTROS -->
      <c:if test="${user_data.role=='PATIENT' || user_data == null}">
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
              <div class="custom-select">
                <div class="select-box">
                  <span class="select-text">
                    <c:choose>
                      <c:when test="${landingForm.insurances > 0}">
                        <c:forEach var="insurance" items="${insurances}">
                          <c:if test="${insurance.id == landingForm.insurances}">
                            ${insurance.name}
                          </c:if>
                        </c:forEach>
                      </c:when>
                      <c:otherwise>
                        <spring:message code="landing.filter.insurances"/>
                      </c:otherwise>
                    </c:choose>
                  </span>
                  <div class="select-arrow"></div>
                </div>
                <ul class="options-list">
                  <li class="option" data-value="">
                    <span class="option-image"></span>
                    <span class="option-text"><spring:message code="landing.filter.insurances"/></span>
                  </li>

                  <c:forEach var="insurance" items="${insurances}">
                    <li class="option" data-value="${insurance.id}">
                      <img src="<c:url value='/supersecret/insurance-picture/${insurance.id}'/>" alt="${insurance.name}" class="option-image" />
                      <span class="option-text">${insurance.name}</span>
                    </li>
                  </c:forEach>
                </ul>
              </div>
              <form:hidden id="selectedInsurance" path="insurances" />
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
            <div></div>
            
            <div class="field-container">
              <form:label path="orderBy" class="field-label">
                <spring:message code="landing.order.label"/>
              </form:label>
              <form:select path="orderBy" class="filter-select">
                <form:option value="">
                  -
                </form:option>
                <form:options items="${orderSelectItems}"
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
            <div class="card-grid">
              <c:forEach var="doctor" items="${docList}">
                <c:set var="insuranceNames" value="" />
                <c:forEach var="insurance" items="${doctor.insurances}" varStatus="status">
                  <c:set var="insuranceNames" value="${insuranceNames}${insurance.name}${!status.last ? ',' : ''}" />
                </c:forEach>
                <jsp:include page="components/doctorCard.jsp">
                  <jsp:param name="id" value="${doctor.id}" />
                  <jsp:param name="doctorName" value="${doctor.name}" />
                  <jsp:param name="insurances" value="${insuranceNames}" />
                  <jsp:param name="specialty" value="${doctor.specialty}" />
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

      <c:if test="${user_data.role == 'DOCTOR' || user_data.role == 'LABORATORY'}">
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

      <c:if test="${totalPages > 0}">
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
            onclick="document.getElementById('pageInput').value = ${totalPages}"
            <c:if test="${page == totalPages}">disabled</c:if>
          >

            <spring:message code="landing.pagination.last"/>
          </button>
          <form:input type="hidden" path="query"/>
          <c:if test="${user_data.role == 'PATIENT' || user_data == null}">
            <form:input type="hidden" path="insurances"/>
            <form:input type="hidden" path="weekday"/>
            <form:input type="hidden" path="specialty"/>
            <form:input type="hidden" path="orderBy"/>
          </c:if>
        </form:form>
      </c:if>
    </div>
    <script>
      document.querySelectorAll('.custom-select').forEach(select => {
        const box = select.querySelector('.select-box');
        const optionsList = select.querySelector('.options-list');
        const options = optionsList.querySelectorAll('.option');
        const textSpan = box.querySelector('.select-text');
        const hiddenInput = document.querySelector('#selectedInsurance');

        box.addEventListener('click', (e) => {
          document.querySelectorAll('.options-list').forEach(o => {
            if (o !== optionsList) o.classList.remove('show');
          });
          optionsList.classList.toggle('show');
          e.stopPropagation();
        });

        options.forEach(option => {
          option.addEventListener('click', () => {
            const value = option.getAttribute('data-value');
            const label = option.querySelector('.option-text').innerText;

            textSpan.innerText = label;
            hiddenInput.value = value;
            optionsList.classList.remove('show');
          });
        });
      });

      // Global click closes any dropdown
      document.addEventListener('click', () => {
        document.querySelectorAll('.options-list').forEach(o => o.classList.remove('show'));
      });

      document.querySelectorAll('.options-list').forEach(o => {
        if (o !== optionsList) o.classList.remove('show');
      });

      window.addEventListener("scroll", () => {
        document.querySelectorAll('.options-list').forEach(o => {
          list.classList.remove('show');
        });
      });
    </script>
  </body>
</html>