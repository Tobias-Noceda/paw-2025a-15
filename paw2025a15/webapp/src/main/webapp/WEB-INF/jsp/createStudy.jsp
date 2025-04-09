<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/file-charge.css"/>" />
    <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
  </head>
  <body>
    <div class="page-container">
      <h1 style="margin: 0;"><spring:message code="uploadStudies.title" arguments="${patientName}"></spring:message></h1>
      <div class="upload-card">
        <form action="${pageContext.request.contextPath}/supersecret/upload/${patientId}/${doctorId}" method="post" enctype="multipart/form-data" name>
            <div class="file-container">
              <input name="file" type="file" accept=".png, .jpg, .jpeg, .pdf">
            </div>
            <p class="description-label"><spring:message code="uploadStudies.description"></spring:message></p>
            <div class="description-container">
              <input name ="type" type="text" path="type" class="upload-input"/>
              <button type="submit" class="upload-button"><spring:message code="uploadStudies.button"></spring:message></button>
            </div>
        </form>
      </div>
    </div>
  </body>
</html>