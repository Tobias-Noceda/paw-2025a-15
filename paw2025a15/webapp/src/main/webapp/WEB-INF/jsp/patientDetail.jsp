<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/patient-detail.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/studies.css"/>">
  </head>
  <body>
    <jsp:include page="components/header.jsp">
      <jsp:param name="username" value="${user.name}"/>
      <jsp:param name="pictureId" value="${user.pictureId}"/>
      <jsp:param name="role" value="${user.role}"/>
    </jsp:include>
    <div class="page-container" style="flex-direction: row;">
      <div class="patient-card">
        <div class="patient-info">
          <h2 class="patient-name"><c:out value="${patient.name}"/></h2>
          <div class="patient-image">
            <img src="<c:url value='/supersecret/files/${patient.pictureId}'/>" alt="Doctor Image" />
          </div>
          <div class="patient-email-div">
            <p class="patient-email-label"><spring:message code="patient.details.email.label"/></p>
            <p class="patient-email"><c:out value="${patient.email}"/></p>
          </div>
          <div class="patient-telephone-div">
            <p class="patient-telephone-label"><spring:message code="patient.details.telephone.label"/></p>
            <p class="patient-telephone"><c:out value="${patient.telephone}"/></p>
          </div>
          <div class="upload-button-div">
            <a href="<c:url value='/upload-file/${patient.id}'/>" class="upload-button">
              <spring:message code="patient.details.upload.label"/>
            </a>
          </div>
        </div>
      </div>
      <div class="study-list-container">
        <h3 class="table-title"><spring:message code="studies.title"></spring:message></h3>
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
                    <c:url value="/supersecret/files/${study.fileId}" var="studyLink" />
                    <c:set var="studyName">
                      <spring:message code="studyType.${study.type}"/>_${study.studyDate}
                    </c:set>
                    <tr class="study-row"
                      onclick="window.open('${studyLink}', '_blank')"
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
    </div>
  </body>
</html>