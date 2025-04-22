<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/studies.css"/>">
  </head>
  <body>
    <c:set var="title">studies</c:set>
    <jsp:include page="components/header.jsp">
      <jsp:param name="title" value="${title}"/>
      <jsp:param name="username" value="${user.name}"/>
      <jsp:param name="pictureId" value="${user.pictureId}"/>
      <jsp:param name="role" value="${user.role}"/>
    </jsp:include>
    <div class="page-container studies-div" style="display: flex; flex-direction: row;">
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
                      data-fileid="<c:out value='${study.fileId}'/>"
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
      <div class="study-list-container">
        <h3 class="table-title"><spring:message code="studies.authorizedDoctors"></spring:message></h3>
        <div class="study-table-container">
          <div class="studies-table-header">
            <table class="studies-table">
              <thead>
                <tr>
                  <th><spring:message code="appointmentTable.doctorColumn.title"></spring:message></th>
                  <th><spring:message code="studyTable.specialtyColumn.title"></spring:message></th>
                  <th class="last-column"><spring:message code="appointmentTable.actionColumn.title"></spring:message></th>
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
                  <c:forEach var="doctor" items="${patientAuthDoctors}">
                    <tr class="doctor-row">
                      <td class="text-cell"><c:out value="${doctor.name}"/></td>
                      <td class="text-cell"><c:out value="${doctor.specialty}"/></td>
                      <td class="cancel-cell">
                        <form 
                          action="/patientAuthDoctor/${patientId}/${doctor.id}"
                          method="post"
                          onsubmit="return openConfirmDialog(this, '${confirmationMessage}', event)"
                        >
                          <button class="deauthorize-button" type="submit">
                            <spring:message code="studies.deauthorize"/>
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

    <%@include file="components/confirmDialog.jsp" %>
    <dialog id="fileDialog" class="file-dialog">
      <div class="file-dialog-header">
        <h2 class="file-dialog-title"><spring:message code="studies.fileDialog.title"/></h2>
        <button id="closeFileDialog" class="cancel-button"><spring:message code="studies.fileDialog.close"/></button>
      </div>
      <iframe id="fileViewer" src="" frameborder="0" class="file-viewer"></iframe>
    </dialog>

    <script src="<c:url value="/js/fileDialogModal.js"/>"></script>
    <script src="<c:url value="/js/confirmDialogModal.js"/>"></script>
  </body>
</html>