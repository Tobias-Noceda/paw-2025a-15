<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="changePassword.title"/></title>
    <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
    <!-- reutilizamos exactamente el mismo CSS -->
    <link rel="stylesheet" href="<c:url value='/css/login-form.css'/>">
</head>
<body>
<c:url value="/changePassword/${token}/${id}" var="postPath"/>

<div class="page-container">
    <div class="login-card">

        <!-- Título -->
        <h2 class="title">
            <spring:message code="changePassword.title"/>
        </h2>
        <p class="subtitle">

            <spring:message code="changePassword.subtitle"/>  <!-- si tienes un subtitle, si no, coméntalo -->

        </p>

        <!-- Formulario con la misma clase que forgotPassword -->
        <form:form
                action="${postPath}"
                modelAttribute="passwordForm"
                method="post"
                class="login-form">

            <!-- Mensaje de error general -->
            <c:if test="${not empty errorMessage}">
                <div class="error-box">
                    <spring:message code="changePassword.error.${errorMessage}"/>
                </div>
            </c:if>

            <!-- Nuevo password -->
            <div class="field-container">
                <label for="password" class="field-label">
                    <spring:message code="changePassword.newPassword"/>
                </label>
                <form:input
                        path="password"
                        id="password"
                        type="password"
                        cssClass="login-input"/>
                <form:errors path="password" cssClass="error-box"/>
            </div>

            <!-- Repetir password -->
            <div class="field-container">
                <label for="repeatPassword" class="field-label">
                    <spring:message code="changePassword.repeatPassword"/>
                </label>
                <form:input
                        path="repeatPassword"
                        id="repeatPassword"
                        type="password"
                        cssClass="login-input"/>
                <form:errors path="repeatPassword" cssClass="error-box"/>
            </div>

            <!-- Botón enviar -->
            <button type="submit" class="btn-primary">
                <spring:message code="changePassword.submit"/>
            </button>

            <!-- Links secundarios (opcional) -->
            <div class="extra-links">
                <p>
                    <a href="<c:url value='/login'/>" class="link-primary">
                        <spring:message code="changePassword.backToLogin"/>
                    </a>
                </p>
            </div>

        </form:form>
    </div>
</div>
</body>
</html>