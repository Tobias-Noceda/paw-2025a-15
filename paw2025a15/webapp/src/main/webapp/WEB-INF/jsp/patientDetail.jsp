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
            <a href="<c:url value='/patient/upload/${patient.id}'/>" class="upload-button">
              <spring:message code="patient.details.upload.label"/>
            </a>
          </div>
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