<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><spring:message code="login.title"/></title>

  <!-- Favicon -->
  <link rel="icon" type="image/png" href="<c:url value='/favicon.ico'/>" />

  <!-- Fuente moderna -->
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

  <!-- Hoja de estilos mejorada -->
  <link rel="stylesheet" href="<c:url value='/css/login-form.css'/>">
</head>
<body>

<c:url value="/login" var="loginUrl" />
<c:url value="/register" var="registerUrl" />
<c:url value="/forgot-password" var="forgotPasswordUrl" />

<div class="page-container">
  <div class="login-card">

    <!-- Logo -->
    <img src="<c:url value='/favicon.ico'/>"
         alt="Caretrace Logo"
         class="logo">

    <!-- Título y subtítulo -->
    <h2 class="title"><spring:message code="login.title"/></h2>
    <p class="subtitle"><spring:message code="login.welcome"/></p>

    <!-- Formulario -->
    <form action="${loginUrl}"
          method="post"
          class="login-form"
          enctype="application/x-www-form-urlencoded">

      <!-- Email -->
      <div class="field-container">
        <label for="username" class="field-label">
          <spring:message code="doctorForm.email"/>
        </label>
        <input id="username"
               name="username"
               type="text"
               class="login-input"
               placeholder="you@example.com"
               value="${param.username}" />
      </div>

      <!-- Password -->
      <div class="field-container">
        <label for="password" class="field-label">
          <spring:message code="login.password"/>
        </label>
        <input id="password"
               name="password"
               type="password"
               class="login-input"
               placeholder="••••••••••" />
      </div>

      <!-- Remember + Forgot -->
      <div class="options-row">
        <label class="checkbox-container">
          <input name="remember-me" type="checkbox" />
          <span class="checkbox-label"><spring:message code="login.rememberMe"/></span>
        </label>
        <a href="${forgotPasswordUrl}" class="link-small">
          <spring:message code="login.forgotPassword"/>
        </a>
      </div>

      <!-- Error message -->
      <c:if test="${not empty errorMessage}">
        <div class="error-box">
          <spring:message code="${errorMessage}"/>
        </div>
      </c:if>

      <!-- Submit -->
      <button type="submit" class="btn-primary">
        <spring:message code="login.submit"/>
      </button>

      <!-- Link extra -->
      <div class="extra-links">
        <p>
          <spring:message code="login.noAccount"/>
          <a href="${registerUrl}" class="link-primary">
            <spring:message code="login.register"/>
          </a>
        </p>
      </div>

    </form>
  </div>
</div>
<script src="<c:url value='/js/buttonControl.js'/>"></script>

</body>
</html>