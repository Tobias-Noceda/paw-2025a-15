<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
  <title><spring:message code="forgotPassword.title"/></title>
  <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
  <link rel="stylesheet" href="<c:url value="/css/doctor-form.css"/>">
  <style>
    .error { color: red; }
    .success { color: green; }
  </style>
</head>
<body>

<c:url value="/recover-password" var="recoverPasswordUrl" />
<c:url value="/login" var="loginUrl" />
<c:url value="/register/choose" var="registerUrl" />

<div class="page-container" style="align-items: center;">
  <div class="doctor-form-container">
    <h2><spring:message code="forgotPassword.title"/></h2>
    <form:form action="${recoverPasswordUrl}" modelAttribute="recoverPass" method="post" class="doctor-form" enctype="application/x-www-form-urlencoded">
      <c:if test="${not empty errorMessage}">
        <p class="error"><spring:message code="forgotPassword.error.${errorMessage}"/></p>
      </c:if>
      <c:if test="${not empty successMessage}">
        <p class="success"><spring:message code="forgotPassword.message.${successMessage}"/></p>
      </c:if>

      <div class="field-container">
        <div class="field-info-container">
          <label class="form-title" for="email"><spring:message code="forgotPassword.email"/></label>
          <form:input path="email" id="email" type="email" cssClass="doctor-form-input" required="true"/>
          <form:errors path="email" cssClass="error"/>
        </div>
      </div>

      <div class="doctor-div">
        <input type="submit" value="<spring:message code='forgotPassword.submit'/>" class="register-button"/>
      </div>

      <div class="extra-links">
        <p><spring:message code="forgotPassword.rememberedPassword"/> <a href="${loginUrl}"><spring:message code="forgotPassword.login"/></a></p>
        <p><spring:message code="forgotPassword.noAccount"/> <a href="${registerUrl}"><spring:message code="forgotPassword.register"/></a></p>
      </div>
    </form:form>
  </div>
</div>

</body>
</html>