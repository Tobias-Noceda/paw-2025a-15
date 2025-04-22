<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <title><spring:message code="recoverPassword.title"/></title>
    <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
    <link rel="stylesheet" href="<c:url value='/css/doctor-form.css'/>">
    <style>
        .success { color: green; }
    </style>
</head>
<body>
<div class="page-container" style="align-items: center;">
    <div class="doctor-form-container">
        <h2><spring:message code="recoverPassword.title"/></h2>
        <c:if test="${not empty successMessage}">
            <p class="success"><spring:message code="recoverPassword.message.${successMessage}"/></p>
        </c:if>
        <p><a href="<c:url value='/login'/>"><spring:message code="recoverPassword.backToLogin"/></a></p>
    </div>
</div>
</body>
</html>