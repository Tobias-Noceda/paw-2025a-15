<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
    <link rel="stylesheet" href="<c:url value="/css/patient-detail.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/studies.css"/>">
  </head>
  <body>
    <jsp:include page="components/header.jsp"/>
    <div class="page-container" style="flex-direction: row;">
      <div class="patient-card">
        <div class="patient-info">
          <h2 class="patient-name"><c:out value="${patient.name}"/></h2>
          <div class="doctor-infopack">
            <div class="patient-image">
              <img src="<c:url value='/supersecret/user-profile-pic/${patient.id}'/>" alt="Patient Image" />
            </div>
          </div>
          <div class="upload-button-div">
            <a href="<c:url value='/upload-study/${patient.id}'/>" class="upload-button">
              <spring:message code="patient.details.upload.label"/>
            </a>
          </div>
          <p class="section-title" ><spring:message code="profileInfo.basic"/></p>
          <hr style="border: 1px solid #ccc; margin: 20px 0;" /><!--TODO: ver siesta bien esto con el estilo aca-->
          <div class="patient-email-div">
            <p class="patient-email-label"><spring:message code="patient.details.email.label"/></p>
            <p class="patient-email"><c:out value="${patient.email}"/></p>
          </div>
          <div class="patient-telephone-div">
            <p class="patient-telephone-label"><spring:message code="patient.details.telephone.label"/></p>
            <p class="patient-telephone"><c:out value="${patient.telephone}"/></p>
          </div>
        </div>
        <div class="patient-info"><!--TODO: just a placeholder for patientDetails, needs proper styling-->
          <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_BASIC')}">
          <div class="basic">
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.age"/>:</p>
              <c:choose>
                <c:when test="${patientDetails.age != null}">
                    <c:out value="${patientDetails.age}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.bloodType"/>:</p>
              <c:choose>
                <c:when test="${patientDetails.bloodType != null}">
                    <c:out value="${patientDetails.bloodType.getName()}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.height"/></p>
              <c:choose>
                <c:when test="${patientDetails.height != null}">
                    <c:out value="${patientDetails.height}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.weight"/></p>
              <c:choose>
                <c:when test="${patientDetails.weight != null}">
                    <c:out value="${patientDetails.weight}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
          <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_HABITS')}">
          <p class="section-title" ><spring:message code="profileInfo.habits"/></p>
          <hr style="border: 1px solid #ccc; margin: 20px 0;" /><!--TODO: ver siesta bien esto con el estilo aca-->
          <div class="habits">
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.smokes"/></p>
              <c:choose>
                <c:when test="${patientDetails.smokes != null}">
                    <c:out value="${patientDetails.smokes}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.drinks"/></p>
              <c:choose>
                <c:when test="${patientDetails.drinks != null}">
                    <c:out value="${patientDetails.drinks}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.diet"/></p>
              <c:choose>
                <c:when test="${not empty patientDetails.diet}">
                    <c:out value="${patientDetails.diet}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
          <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_MEDICAL')}">
          <p class="section-title"><spring:message code="profileInfo.medical"/></p>
          <hr style="border: 1px solid #ccc; margin: 20px 0;" /><!--TODO: ver siesta bien esto con el estilo aca-->
          <div class="med">
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.meds"/></p>
              <c:choose>
                <c:when test="${not empty patientDetails.meds}">
                    <c:out value="${patientDetails.meds}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.conditions"/></p>
              <c:choose>
                <c:when test="${not empty patientDetails.conditions}">
                    <c:out value="${patientDetails.conditions}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.allergies"/></p>
              <c:choose>
                <c:when test="${not empty patientDetails.allergies}">
                    <c:out value="${patientDetails.allergies}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
          <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_SOCIAL')}">
          <p class="section-title"><spring:message code="profileInfo.social"/></p>
          <hr style="border: 1px solid #ccc; margin: 20px 0;" /><!--TODO: ver siesta bien esto con el estilo aca-->
          <div class="lifestyle">
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.hobbies"/></p>
              <c:choose>
                <c:when test="${not empty patientDetails.hobbies}">
                    <c:out value="${patientDetails.hobbies}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.job"/></p>
              <c:choose>
                <c:when test="${not empty patientDetails.job}">
                    <c:out value="${patientDetails.job}" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
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
                    <c:url value="/view-study/${study.id}" var="studyLink" />
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