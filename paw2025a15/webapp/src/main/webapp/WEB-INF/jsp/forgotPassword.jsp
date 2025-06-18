<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><spring:message code="forgotPassword.title"/></title>

  <!-- Favicon -->
  <link rel="icon" type="image/png" href="<c:url value='/favicon.ico'/>" />

  <!-- Fuente Inter -->
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

  <!-- Mismo CSS que el login -->
  <link rel="stylesheet" href="<c:url value='/css/login-form.css'/>">
</head>
<body>

<c:url value="/recover-password"       var="recoverPasswordUrl"/>
<c:url value="/login"                  var="loginUrl"/>
<c:url value="/register"        var="registerUrl"/>

<div class="page-container">
  <div class="login-card">

    <!-- Título -->
    <h2 class="title">
      <spring:message code="forgotPassword.title"/>
    </h2>
    <p class="subtitle">
      <spring:message code="forgotPassword.subtitle"/>
    </p>

    <!-- Formulario Spring -->
    <form:form action="${recoverPasswordUrl}"
               modelAttribute="recoverPass"
               method="post"
               class="login-form">


      <!-- Campo email -->
      <div class="field-container">
        <label for="email" class="field-label">
          <spring:message code="forgotPassword.email"/>
        </label>
        <form:input path="email"
                    id="email"
                    type="email"
                    cssClass="login-input"
                    placeholder="you@example.com"
                    required="true"/>
        <form:errors path="email" cssClass="error-box"/>
      </div>

      <!-- Mensajes -->
      <c:if test="${not empty errorMessage}">
        <div class="error-box">
          <spring:message code="forgotPassword.error.${errorMessage}"/>
        </div>
      </c:if>
      <c:if test="${not empty successMessage}">
        <div class="success-box">
          <spring:message code="forgotPassword.message.${successMessage}"/>
        </div>
      </c:if>
      <!-- Botón enviar -->
      <button type="submit" class="btn-primary">
        <spring:message code="forgotPassword.submit"/>
      </button>

      <!-- Links secundarios -->
      <div class="extra-links">
        <p>
          <spring:message code="forgotPassword.rememberedPassword"/>
          <a href="${loginUrl}" class="link-primary">
            <spring:message code="forgotPassword.login"/>
          </a>
        </p>
        <p>
          <spring:message code="forgotPassword.noAccount"/>
          <a href="${registerUrl}" class="link-primary">
            <spring:message code="forgotPassword.register"/>
          </a>
        </p>
      </div>

    </form:form>
  </div>
</div>
<script src="<c:url value='/js/buttonControl.js'/>"></script>

</body>
</html>