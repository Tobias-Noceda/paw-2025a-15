<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title><spring:message code="changePassword.title"/></title>
    <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
    <link rel="stylesheet" href="<c:url value='/css/doctor-form.css'/>">
</head>
<body>
<c:url value="/changePassword/${token}/${id}" var="postPath"/>
<div class="page-container" style="align-items: center;">
    <div class="doctor-form-container">
        <h2><spring:message code="changePassword.title"/></h2>
        <form:form cssClass="doctor-form" modelAttribute="passwordForm" action="${postPath}" method="post">
            <c:if test="${not empty errorMessage}">
                <p class="error-message"><spring:message code="changePassword.error.${errorMessage}"/></p>
            </c:if>
            <div class="field-container">
                <form:errors path="password" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="password">
                        <spring:message code="changePassword.newPassword"/>
                    </form:label>
                    <form:input type="password" path="password" class="doctor-form-input"/>
                </div>
            </div>

            <div class="field-container">
                <form:errors path="repeatPassword" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="repeatPassword">
                        <spring:message code="changePassword.repeatPassword"/>
                    </form:label>
                    <form:input type="password" path="repeatPassword" class="doctor-form-input"/>
                </div>
            </div>

            <div class="doctor-div">
                <input type="submit" value="<spring:message code='changePassword.submit'/>" class="register-button"/>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>