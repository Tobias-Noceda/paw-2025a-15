<%--
  Created by IntelliJ IDEA.
  User: smrubio
  Date: 17/04/2025
  Time: 10:35 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
  <title>Recuperar contraseña</title>
  <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
  <link rel="stylesheet" href="<c:url value="/css/doctor-form.css"/>">
</head>
<body>

<c:url value="/recover-password" var="recoverPasswordUrl" />
<c:url value="/login" var="loginUrl" />
<c:url value="/register/choose" var="registerUrl" />

<div class="page-container" style="align-items: center;">
  <div class="doctor-form-container">
    <form action="${recoverPasswordUrl}" method="post" class="doctor-form" enctype="application/x-www-form-urlencoded">

      <div class="field-container">
        <div class="field-info-container">
          <label class="form-title" for="email">Email:</label>
          <input id="email" name="email" type="email" class="doctor-form-input" required/>
        </div>
      </div>

      <div class="doctor-div">
        <input type="submit" value="Enviar instrucciones" class="register-button"/>
      </div>

      <div class="extra-links">
        <p>¿Recordaste tu contraseña? <a href="${loginUrl}">Inicia sesión</a></p>
        <p>¿No tienes cuenta? <a href="${registerUrl}">Regístrate</a></p>
      </div>

    </form>
  </div>
</div>

</body>
</html>
