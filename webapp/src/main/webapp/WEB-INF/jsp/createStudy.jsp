<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>
    <spring:message code="uploadStudies.title" arguments="${patient.name}" />
  </title>
  <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
  <link rel="stylesheet" href="<c:url value='/css/main.css'/>" />

  <link rel="stylesheet" href="<c:url value='/css/file-charge.css'/>" />
  <!-- cargamos sólo nuestro CSS de estudio -->
  <link rel="stylesheet" href="<c:url value='/css/study-form.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/appointments.css'/>" />
</head>
<body>
<jsp:include page="components/header.jsp"/>

<div class="study-page-container">
  <div class="study-card">
    <h2>
      <spring:message code="uploadStudies.title" arguments="${patient.name}" />
    </h2>

    <c:url value="/studies/${patient.id}" var="uploadUrl" />
    <form:form
            modelAttribute="createStudyForm"
            method="post"
            action="${uploadUrl}"
            enctype="multipart/form-data"
            class="study-form">

      <!-- Files -->
      <div class="sf-field">
        <label for="files">
          <spring:message code="uploadStudies.fileLabel"/>:
        </label>
        <form:input id="files"
          type="file"
          path="files"
          accept=".png,.jpg,.jpeg,.pdf"
          multiple="multiple"
        />
        <form:errors path="files" cssClass="sf-error"/>
      </div>

      <!-- Type -->
      <div class="sf-field">
        <label for="type">
          <spring:message code="createStudy.type"/>:
        </label>
        <form:select id="type" cssClass="filter-select" path="type">
          <form:options
            items="${studyTypeSelectItems}"
            itemLabel="label"
            itemValue="value"
          />
        </form:select>
        <form:errors path="type" cssClass="sf-error"/>
      </div>

      <c:if test="${createStudyForm.date == null}">
        <script>
          document.addEventListener("DOMContentLoaded", function () {
            document.getElementById("date").value = "${today}";
          });
        </script>
      </c:if>
      <!-- Date -->
      <div class="sf-field">
        <label for="date">
          <spring:message code="createStudy.date"/>:
        </label>
        <form:input id="date" type="date" cssClass="input-field" path="date"/>
        <form:errors path="date" cssClass="sf-error"/>
      </div>

      <!-- Comment -->
      <div class="sf-field">
        <label for="comment">
          <spring:message code="uploadStudies.description"/>
        </label>
        <form:input id="comment" type="text" path="comment"/>
      </div>
      <sec:authorize access="hasRole('ROLE_PATIENT')">
        <c:if test="${not empty patient.authorizedDoctors}">
          <div class="sf-field">
            <label for="authDoctorIds">
              <spring:message code="uploadStudies.doctor"/>
            </label>
            <!-- Botones de seleccionar todos -->
            <div class="doctor-selection-controls">
              <button type="button" id="selectAllDoctors" class="doctor-action-btn">
                <spring:message code="uploadStudies.selectAll"/>
              </button>
              <button type="button" id="deselectAllDoctors" class="doctor-action-btn doctor-action-btn--deselect">
                <spring:message code="uploadStudies.deselectAll"/>
              </button>
            </div>
            
            <div class="appointments-table-header">
              <table class="appointments-table">
                <thead>
                <tr>
                  <th><spring:message code="studies.authorizedDoctors"/></th>
                  <th class="last-column">
                  </th>
                </tr>
                </thead>
              </table>
            </div>

            <div class="appointments-table-body">
              <table class="appointments-table">
                <tbody>
                <c:forEach var="authDocs" items="${patient.authorizedDoctors}">
                  <tr class="appointments-row">
                    <td class="text-cell">
                      <c:out value="${authDocs.name}"/>
                    </td>
                    <td class="checkbox-cell last-column">
                      <form:checkbox path="authDoctorIds" value="${authDocs.id}" cssClass="doctor-checkbox"/>
                    </td>
                  </tr>
                </c:forEach>
                </tbody>
              </table>
            </div>
          </div>
        </c:if>
      </sec:authorize>

      <button type="submit" class="sf-button">
        <spring:message code="uploadStudies.button"/>
      </button>
    </form:form>
  </div>
</div>
<script src="<c:url value='/js/buttonControl.js'/>"></script>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const selectAllBtn = document.getElementById('selectAllDoctors');
    const deselectAllBtn = document.getElementById('deselectAllDoctors');
    
    if (selectAllBtn) {
        selectAllBtn.addEventListener('click', function() {
            const checkboxes = document.querySelectorAll('.doctor-checkbox');
            checkboxes.forEach(checkbox => {
                checkbox.checked = true;
            });
        });
    }
    
    if (deselectAllBtn) {
        deselectAllBtn.addEventListener('click', function() {
            const checkboxes = document.querySelectorAll('.doctor-checkbox');
            checkboxes.forEach(checkbox => {
                checkbox.checked = false;
            });
        });
    }
});
</script>

</body>
</html>
