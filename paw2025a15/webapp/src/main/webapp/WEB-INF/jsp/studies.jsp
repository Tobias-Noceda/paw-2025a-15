<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
  <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
  <link rel="stylesheet" href="<c:url value="/css/studies.css"/>">

</head>
<body>
<c:set var="title">studies</c:set>
<jsp:include page="components/header.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>
<fmt:setLocale value="${pageContext.request.locale}" />
<div class="page-container studies-div" style="display: flex; flex-direction: row;">
  <div class="study-list-container">
    <div class="title-container">
      <h3 class="table-title"><spring:message code="studies.title"></spring:message></h3>
      <a href="<c:url value='/upload-study/${user_data.id}'/>" class="upload-button">
        <spring:message code="patient.details.upload.label"/>
      </a>
    </div>
    <c:url value="/studies" var="studiesUrl"/>
    <form:form modelAttribute="filterForm"
               action="${studiesUrl}"
               method="get"
    >
      <div class="field-container" style="display: flex; flex-direction: column; gap: 12px;">

        <!-- Tipo de estudio -->
        <div>
          <form:label path="type" class="field-label">
            <spring:message code="studies.filter.label"/>
          </form:label>
          <form:select path="type" class="input-field">
            <form:option value="">
              <spring:message code="studies.all"/>
            </form:option>
            <form:options items="${studyTypeSelectItems}"
                          itemLabel="label"
                          itemValue="value"/>
          </form:select>
        </div>

        <!-- Orden -->
        <div>
          <form:label path="mostRecent" class="field-label">
            <spring:message code="studies.order.label"/>
          </form:label>
          <form:select path="mostRecent" class="input-field">
            <form:option value="true">
              <spring:message code="studies.order.mostRecent" />
            </form:option>
            <form:option value="false">
              <spring:message code="studies.order.leastRecent" />
            </form:option>
          </form:select>
        </div>
        <div class="filter-button-div">
          <button type="submit" class="filter-button">
            <spring:message code="studies.apply"/>
          </button>
        </div>
      </div>

    </form:form>
    <p class="info-text"><spring:message code="studies.info.clickToManage"/></p>



    <div class="study-table-container">
      <div class="studies-table-header">
        <table class="studies-table">
          <thead>
            <tr>
              <th><spring:message code="studyTable.typeColumn.title"></spring:message></th>
              <th><spring:message code="studyTable.detailsColumn.title"></spring:message></th>
              <th><spring:message code="appointmentTable.dateColumn.title"></spring:message></th>
              <th><spring:message code="studyTable.uploadDateColumn.title"></spring:message></th>
              <th class="studies-last-column"><spring:message code="appointmentTable.actionColumn.title"></spring:message></th>
            </tr>
          </thead>
        </table>
      </div>
      <c:if test="${not empty patientStudies}">
        <div class="studies-table-body">
          <table class="studies-table">
            <tbody>
              <c:forEach var="study" items="${patientStudies}">
                <c:url value="/study-info/${study.id}" var="studyDetailLink" />
                <c:url var="deleteUrl" value="/delete-study/${study.id}"/>
                <c:set var="studyName">
                  <spring:message code="studyType.${study.type}"/>_${study.studyDate}
                </c:set>
                <c:set var="studyDate">
                  <fmt:formatDate value="${study.studyDateAsDate}" dateStyle="short"/>
                </c:set>
                <c:set var="studyUploadDate">
                  <fmt:formatDate value="${study.uploadDateAsDate}" dateStyle="short"/>
                </c:set>
                <c:set var="studyComment">
                  <c:out value="${study.comment}" escapeXml="true"/>
                </c:set>
                <tr class="study-row"
                    onclick="window.location='${studyDetailLink}'"
                >
                  <td class="text-cell">
                    <spring:message code="studyType.${study.type}"/>
                  </td>
                  <td class="text-cell">${studyComment}</td>
                  <td class="text-cell">
                    <c:out value="${studyDate}" escapeXml="true"/>
                  </td>
                  <td class="text-cell">
                    <c:out value="${studyUploadDate}" escapeXml="true"/>
                  </td>
                  <td class="download-cell">
                    <c:set var="dateText">
                      <spring:message code="study.delete.date" arguments="${studyDate}"/>
                    </c:set>
                    <c:set var="commentText">
                      <spring:message code="study.delete.comment" arguments="${studyComment}"/>
                    </c:set>
                    <button
                      class="cancel-button"
                      onclick="event.stopPropagation(); confirmStudyDelete('${deleteUrl}', '${dateText}', '${commentText}')"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                        <path d="M3 6h18v2H3V6zm2 3h14l-1.5 13h-11L5 9zm5 2v8h2v-8H10zm4 0v8h2v-8h-2zM9 4V3h6v1h5v2H4V4h5z"/>
                      </svg>
                    </button>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:if>
      <c:if test="${empty patientStudies}">
        <div class="no-studies-container">
          <h4 class="no-studies-text"><spring:message code="studies.empty"/></h4>
        </div>
      </c:if>
    </div>
  </div>
  <div class="study-list-container">
    <h3 class="table-title"><spring:message code="studies.myDoctors"></spring:message></h3>
    <p class="info-text"><spring:message code="studies.info.clickDoctorToManage"/></p>
    
    <!-- Botón de desautorizar todos -->
    <c:if test="${not empty patient.authorizedDoctors}">
      <div class="bulk-actions" style="margin-bottom: 1rem;">
        <c:url value="/patientDeAuthAllDoctors" var="authActionsUrl" />
        <form action="${authActionsUrl}" method="post" style="display: inline;">
          <button type="button" class="deauthorize-button" 
                  onclick="confirmBulkAction('<spring:message code="studies.deauthorizeAll.confirm"/>', this.form, 'deauthorize', '<spring:message code="confirmDialog.yes"/>', '<spring:message code="confirmDialog.no"/>')">
            <spring:message code="studies.deauthorizeAll"/>
          </button>
        </form>
      </div>
    </c:if>
    
    <div class="study-table-container">
      <div class="studies-table-header">
        <table class="studies-table">
          <thead>
          <tr>
            <th><spring:message code="appointmentTable.doctorColumn.title"></spring:message></th>
            <th><spring:message code="studyTable.specialtyColumn.title"></spring:message></th>
            <th class="doctors-last-column"><spring:message code="appointmentTable.actionColumn.title"></spring:message></th>
          </tr>
          </thead>
        </table>
      </div>
      <c:if test="${not empty patient.authorizedDoctors}">
        <c:set var="confirmationMessage">
          <spring:message code="studies.cancelConfirm"/>
        </c:set>
        <div class="studies-table-body">
          <table class="studies-table">
            <tbody>
            <c:set var="buttonText">
              <spring:message code="doctorDetail.toggleButton.deauthorize"/>
            </c:set>
            <c:set var="confirmationText">
              <spring:message code="doctorDetail.deauthorize.confirm"/>
            </c:set>
            <c:set var="authCancelText">
              <spring:message code="doctorDetail.authorize.cancelButton"/>
            </c:set>
            <c:forEach var="doctor" items="${patient.authorizedDoctors}">

              <c:url value="/doctors/${doctor.id}" var="doctorUrl"/>

              <tr class="doctor-row"

                  onclick="window.location='${doctorUrl}'"

                  style="cursor:pointer;">

                <td class="text-cell">

                  <c:out value="${doctor.name}" escapeXml="true"/>

                </td>             
                <td class="text-cell">
                  <spring:message code="specialty.${doctor.specialty}"/>
                </td>
                <td class="deauthorize-cell">
                  <c:url value="/patientAuthDoctor/${doctor.id}" var="deauthDoctorUrl" />
                  <form
                    id="deauthDoctor_${doctor.id}"
                    action="${deauthDoctorUrl}"
                    method="post"
                  >
                    <button
                      type="button"
                      name="action"
                      value="toggle"
                      class="deauthorize-button"
                      data-confirmation="${confirmationText}"
                      data-button-text="${buttonText}"
                      data-cancel-text="${authCancelText}"
                      data-doctor-id="${doctor.id}"
                      onclick="event.stopPropagation(); confirmDeauthDoctor(this.dataset.confirmation, null, this.dataset.buttonText, this.dataset.cancelText, this.name, this.value, this.dataset.doctorId);"
                    >
                      <c:out value="${buttonText}" escapeXml="true"/>
                    </button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </div>
      </c:if>
      <c:if test="${empty patient.authorizedDoctors}">
        <div class="no-studies-container">
          <h4 class="no-studies-text"><spring:message code="studies.authorizedDoctors.empty"/></h4>
        </div>
      </c:if>
    </div>
  </div>
</div>

<%@include file="components/confirmDialog.jsp" %>
<%@include file="components/studyDeleteDialog.jsp" %>
<script src="<c:url value='/js/deauthConfirmationModal.js'/>"></script>
<script src="<c:url value='/js/buttonControl.js'/>"></script>

</body>
</html>