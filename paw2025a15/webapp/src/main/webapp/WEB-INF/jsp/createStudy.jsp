<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>
    <spring:message code="uploadStudies.title" arguments="${patient.name}" />
  </title>
  <link rel="stylesheet" href="<c:url value='/css/main.css'/>" />

  <link rel="stylesheet" href="<c:url value='/css/file-charge.css'/>" />
  <!-- cargamos sólo nuestro CSS de estudio -->
  <link rel="stylesheet" href="<c:url value='/css/study-form.css'/>" />
</head>
<body>
<jsp:include page="components/header.jsp"/>

<div class="study-page-container">
  <div class="study-card">
    <h2>
      <spring:message code="uploadStudies.title" arguments="${patient.name}" />
    </h2>

    <c:url value="/upload-study/${patient.id}" var="uploadUrl" />
    <form:form
            modelAttribute="createStudyForm"
            method="post"
            action="${uploadUrl}"
            enctype="multipart/form-data"
            class="study-form">

      <!-- File -->
      <div class="sf-field">
        <label for="file">
          <spring:message code="uploadStudies.fileLabel"/>:
        </label>
        <input id="file"
               type="file"
               name="file"
               accept=".png,.jpg,.jpeg,.pdf" />
      </div>

      <!-- Type -->
      <div class="sf-field">
        <label for="type">
          <spring:message code="createStudy.type"/>:
        </label>
        <form:select id="type" cssClass="filter-select" path="type">
          <form:options
                  items="${studyTypeSelectItems}"
                  itemValue="name"
                  itemLabel="displayName" />
        </form:select>
        <form:errors path="type" cssClass="sf-error"/>
      </div>

      <!-- Date -->
      <div class="sf-field">
        <label for="date">
          <spring:message code="createStudy.date"/>:
        </label>
        <form:input id="date" value="${today}" type="date" path="date"/>
        <form:errors path="date" cssClass="sf-error"/>
      </div>

      <!-- Comment -->
      <div class="sf-field">
        <label for="comment">
          <spring:message code="uploadStudies.description"/>:
        </label>
        <form:input id="comment" type="text" path="comment"/>
      </div>

      <button type="submit" class="sf-button">
        <spring:message code="uploadStudies.button"/>
      </button>
    </form:form>
  </div>
</div
<script src="<c:url value='/js/buttonControl.js'/>"></script>

</body>
</html>