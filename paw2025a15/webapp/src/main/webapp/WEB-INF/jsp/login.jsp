<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
  <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
  <link rel="stylesheet" href="<c:url value='/css/doctor-form.css'/>">
</head>
<body>

<c:url value="/login" var="loginUrl" />
<c:url value="/register/choose" var="registerUrl" />
<c:url value="/forgot-password" var="forgotPasswordUrl" />

<div class="page-container" style="align-items: center;">
  <div class="doctor-form-container">
    <form action="${loginUrl}" method="post" class="doctor-form" enctype="application/x-www-form-urlencoded">

      <div class="field-container">
        <div class="field-info-container">
          <label class="form-title" for="username">Username:</label>
          <input id="username" name="username" type="text" class="doctor-form-input" value="${param.username}"/>
        </div>
      </div>

      <div class="field-container">
        <div class="field-info-container">
          <label class="form-title" for="password">Password:</label>
          <input id="password" name="password" type="password" class="doctor-form-input"/>
        </div>
      </div>

      <div class="field-container">
        <div class="field-info-container">
          <label class="form-title">
            <input name="remember-me" type="checkbox"/> Remember me
          </label>
        </div>
      </div>

      <c:if test="${not empty errorMessage}">
        <div style="color: red;">
          <spring:message code="${errorMessage}"/>
        </div>
      </c:if>

      <div class="doctor-div">
        <input type="submit" value="Login!" class="register-button"/>
      </div>

      <div class="extra-links">
        <p>¿No tienes cuenta? <a href="${registerUrl}">Regístrate</a></p>
        <p><a href="${forgotPasswordUrl}">¿Olvidaste tu contraseña?</a></p>
      </div>

    </form>
  </div>
</div>

</body>
</html>