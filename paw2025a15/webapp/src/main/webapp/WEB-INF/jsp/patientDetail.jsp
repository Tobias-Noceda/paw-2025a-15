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
          <h2 class="patient-name"><c:out value="${patient.name}" escapeXml="true"/></h2>
          <div class="doctor-infopack">
            <div class="patient-image">
              <img
                src="<c:url value='/supersecret/user-profile-pic/${patient.id}'/>"
                alt="<spring:message code='image.alt.patientProfile'/>"
              />
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
            <p class="patient-email"><c:out value="${patient.email}" escapeXml="true"/></p>
          </div>
          <div class="patient-telephone-div">
            <p class="patient-telephone-label"><spring:message code="patient.details.telephone.label"/></p>
            <p class="patient-telephone"><c:out value="${patient.telephone}" escapeXml="true"/></p>
          </div>
        </div>
        <div class="patient-info"><!--TODO: just a placeholder for patientDetails, needs proper styling-->
          <c:if test="${allowedAccessLevels.contains('VIEW_BASIC')}">
          <div class="basic">
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.age"/>:</p>
              <c:choose>
                <c:when test="${patientAge != null}">
                    <c:out value="${patientAge}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.bloodType"/>:</p>
              <c:choose>
                <c:when test="${patient.bloodType != null}">
                    <c:out value="${patient.bloodType.getName()}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.height"/></p>
              <c:choose>
                <c:when test="${patient.height != null}">
                    <c:out value="${patient.height}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.weight"/></p>
              <c:choose>
                <c:when test="${patient.weight != null}">
                    <c:out value="${patient.weight}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
          <c:if test="${allowedAccessLevels.contains('VIEW_HABITS')}">
          <p class="section-title" ><spring:message code="profileInfo.habits"/></p>
          <hr style="border: 1px solid #ccc; margin: 20px 0;" /><!--TODO: ver siesta bien esto con el estilo aca-->
          <div class="habits">
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.smokes"/></p>
              <c:choose>
                <c:when test="${patient.smokes != null}">
                    <c:out value="${patient.smokes}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.drinks"/></p>
              <c:choose>
                <c:when test="${patient.drinks != null}">
                    <c:out value="${patient.drinks}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.diet"/></p>
              <c:choose>
                <c:when test="${not empty patient.diet}">
                    <c:out value="${patient.diet}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
          <c:if test="${allowedAccessLevels.contains('VIEW_MEDICAL')}">
          <p class="section-title"><spring:message code="profileInfo.medical"/></p>
          <hr style="border: 1px solid #ccc; margin: 20px 0;" /><!--TODO: ver siesta bien esto con el estilo aca-->
          <div class="med">
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.meds"/></p>
              <c:choose>
                <c:when test="${not empty patient.meds}">
                    <c:out value="${patient.meds}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.conditions"/></p>
              <c:choose>
                <c:when test="${not empty patient.conditions}">
                    <c:out value="${patient.conditions}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.allergies"/></p>
              <c:choose>
                <c:when test="${not empty patient.allergies}">
                    <c:out value="${patient.allergies}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          </c:if>
          <c:if test="${allowedAccessLevels.contains('VIEW_SOCIAL')}">
          <p class="section-title"><spring:message code="profileInfo.social"/></p>
          <hr style="border: 1px solid #ccc; margin: 20px 0;" /><!--TODO: ver siesta bien esto con el estilo aca-->
          <div class="lifestyle">
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.hobbies"/></p>
              <c:choose>
                <c:when test="${not empty patient.hobbies}">
                    <c:out value="${patient.hobbies}" escapeXml="true" />
                </c:when>
                <c:otherwise>
                  <spring:message code="profileInfo.notProvided"/>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="patient-email-div">
              <p class="patient-email-label"><spring:message code="profileInfo.job"/></p>
              <c:choose>
                <c:when test="${not empty patient.job}">
                    <c:out value="${patient.job}" escapeXml="true" />
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
        <c:url value="/patient/${patient.id}" var="patientStudiesUrl"/>
        <form:form modelAttribute="filterForm"
                   action="${patientStudiesUrl}"
                   method="get">
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
                  <spring:message code="studies.order.mostRecent"/>
                </form:option>
                <form:option value="false">
                  <spring:message code="studies.order.leastRecent"/>
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
                    <c:url value="/study-info/${study.id}" var="studyLink" />
                    <tr class="study-row"
                      onclick="window.location.href='${studyLink}';"
                    >
                      <td class="text-cell">
                        <spring:message code="studyType.${study.type}"/>
                      </td>
                      <td class="text-cell"><c:out value="${study.comment}" escapeXml="true"/></td>
                      <td class="text-cell">
                        <fmt:formatDate value="${study.studyDateAsDate}" dateStyle="short"/>
                      </td>
                      <td class="text-cell">
                        <fmt:formatDate value="${study.uploadDateAsDate}" dateStyle="short"/>
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
    <script src="<c:url value='/js/buttonControl.js'/>"></script>

  </body>

</html>