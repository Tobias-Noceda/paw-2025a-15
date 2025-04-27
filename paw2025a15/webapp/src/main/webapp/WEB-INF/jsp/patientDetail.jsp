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
        <div class="patient-info"><!--TODO: just a placeholder for patientDetails, needs proper styling and inter-->
          <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_BASIC')}">
          <div class="basic">
            <div class="patient-email-div">
              <p class="patient-email-label">age</p>
              <c:choose>
                <c:when test="${patientDetails.age != null}">
                    <c:out value="${patientDetails.age}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label">blood type</p>
              <c:choose>
                <c:when test="${patientDetails.bloodType != null}">
                    <c:out value="${patientDetails.bloodType}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
          <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_MEDICAL')}">
          <div class="med">
            <div class="patient-email-div">
              <p class="patient-email-label">height</p>
              <c:choose>
                <c:when test="${patientDetails.height != null}">
                    <c:out value="${patientDetails.height}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label">weight</p>
              <c:choose>
                <c:when test="${patientDetails.weight != null}">
                    <c:out value="${patientDetails.weight}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label">smokes</p>
              <c:choose>
                <c:when test="${patientDetails.smokes != null}">
                    <c:out value="${patientDetails.smokes}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label">drinks</p>
              <c:choose>
                <c:when test="${patientDetails.drinks != null}">
                    <c:out value="${patientDetails.drinks}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label">meds</p>
              <c:choose>
                <c:when test="${patientDetails.meds != null}">
                    <c:out value="${patientDetails.meds}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label">conditions</p>
              <c:choose>
                <c:when test="${patientDetails.conditions != null}">
                    <c:out value="${patientDetails.conditions}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label">allergies</p>
              <c:choose>
                <c:when test="${patientDetails.allergies != null}">
                    <c:out value="${patientDetails.allergies}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
          <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_LIFESTYLE')}">
          <div class="lifestyle">
            <div class="patient-email-div">
              <p class="patient-email-label">diet</p>
              <c:choose>
                <c:when test="${patientDetails.diet != null}">
                    <c:out value="${patientDetails.diet}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label">hobbies</p>
              <c:choose>
                <c:when test="${patientDetails.hobbies != null}">
                    <c:out value="${patientDetails.hobbies}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label">job</p>
              <c:choose>
                <c:when test="${patientDetails.job != null}">
                    <c:out value="${patientDetails.job}" />
                </c:when>
                <c:otherwise>
                    Not Provided
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
        </div>
      </div>
      <div class="study-list-container">
        <h3 class="table-title"><spring:message code="studies.title"></spring:message></h3>
        <div class="study-table-container">
          <div class="studies-table-header">
            <table class="studies-table">
              <thead>
                <tr>
                  <th><spring:message code="studyTable.typeColumn.title"></spring:message></th>
                  <th><spring:message code="studyTable.detailsColumn.title"></spring:message></th>
                  <th><spring:message code="appointmentTable.dateColumn.title"></spring:message></th>
                  <th><spring:message code="studyTable.uploadDateColumn.title"></spring:message></th>
                </tr>
              </thead>
            </table>
          </div>
          <c:if test="${not empty patientStudies}">
            <div class="studies-table-body">
              <table class="studies-table">
                <tbody>
                  <c:forEach var="study" items="${patientStudies}">
                    <tr class="study-row"
                      data-fileurl="<c:url value='/supersecret/files/${study.fileId}'/>"
                    >
                      <td class="text-cell"><c:out value="${study.type}"/></td>
                      <td class="text-cell"><c:out value="${study.comment}"/></td>
                      <td class="text-cell"><c:out value="${study.studyDate}"/></td>
                      <td class="text-cell"><c:out value="${study.uploadDate.toLocalDate()}"/></td>
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
    <%@include file="components/fileDialog.jsp" %>
  </body>
</html>