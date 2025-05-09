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
                    <c:url value="/study-info/${study.id}" var="studyLink" />
                    <c:set var="studyName">
                      <spring:message code="studyType.${study.type}"/>_${study.studyDate}
                    </c:set>
                    <tr class="study-row"
                      onclick="window.location='${studyLink}'"
                    >
                      <c:set var="studyDay">
                        <fmt:formatNumber value="${study.studyDate.dayOfMonth}" pattern="00" />
                      </c:set>
                      <c:set var="studyMonth">
                        <fmt:formatNumber value="${study.studyDate.monthValue}" pattern="00" />
                      </c:set>
                      <c:set var="studyYear" value="${study.studyDate.year}" />

                      <c:set var="uploadDay">
                        <fmt:formatNumber value="${study.uploadDate.dayOfMonth}" pattern="00" />
                      </c:set>
                      <c:set var="uploadMonth">
                        <fmt:formatNumber value="${study.uploadDate.monthValue}" pattern="00" />
                      </c:set>
                      <c:set var="uploadYear" value="${study.uploadDate.year}" />

                      <td class="text-cell">
                        <spring:message code="studyType.${study.type}"/>
                      </td>
                      <td class="text-cell"><c:out value="${study.comment}"/></td>
                      <td class="text-cell">
                        <spring:message code="dateFormat" arguments="${studyDay},${studyMonth},${studyYear}"/>
                      </td>
                      <td class="text-cell">
                        <spring:message code="dateFormat" arguments="${uploadDay},${uploadMonth},${uploadYear}"/>
                      </td>
                      <td class="download-cell">
                        <a
                          class="download-button"
                          href="${studyLink}"
                          download="${studyName}"
                          onclick="event.stopPropagation();"
                        >
                          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                            <path fill="currentColor" d="M12 16a1 1 0 0 1-.7-.29l-4-4a1 1 0 0 1 1.41-1.41L11 12.59V4a1 1 0 0 1 2 0v8.59l2.29-2.29a1 1 0 0 1 1.41 1.41l-4 4a1 1 0 0 1-.7.29zM19 14a1 1 0 0 0-1 1v3a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1v-3a1 1 0 0 0-2 0v3a3 3 0 0 0 3 3h10a3 3 0 0 0 3-3v-3a1 1 0 0 0-1-1z"/>
                          </svg>
                        </a>
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
        <h3 class="table-title"><spring:message code="studies.authorizedDoctors"></spring:message></h3>
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
          <c:if test="${not empty patientAuthDoctors}">
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
                  <c:forEach var="doctor" items="${patientAuthDoctors}">

                    <c:url value="/doctors/${doctor.id}" var="doctorUrl"/>

                    <tr class="doctor-row"

                        onclick="window.location='${doctorUrl}'"

                        style="cursor:pointer;">

                      <td class="text-cell">

                        <c:out value="${doctor.name}"/>

                      </td>

                      <td class="text-cell">
                          <spring:message code="specialty.${doctor.specialty}"/>
                      </td>
                        <td class="deauthorize-cell">
                          <c:url value="/patientAuthDoctor/${doctor.id}" var="deauthDoctorUrl" />
                          <form
                            id="authDoctorForm"
                            action="${deauthDoctorUrl}"
                            method="post"
                          >
                            <button
                              type="button"
                              name="action"
                              value="toggle"
                              class="deauthorize-button"
                              onclick="confirmAuthDoctor('${confirmationText}', null, '${buttonText}', '${authCancelText}', this.name, this.value);"
                            >
                              <c:out value="${buttonText}"/>
                            </button>
                          </form>
                        </td>
                      </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:if>
          <c:if test="${empty patientAuthDoctors}">
            <div class="no-studies-container">
              <h4 class="no-studies-text"><spring:message code="studies.authorizedDoctors.empty"/></h4>
            </div>
          </c:if>
        </div>
      </div>
    </div>
    <script src="<c:url value='/js/authConfirmationModal.js'/>"></script>

    <%@include file="components/confirmDialog.jsp" %>
  </body>
</html>