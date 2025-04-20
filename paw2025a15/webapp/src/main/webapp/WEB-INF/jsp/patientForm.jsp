<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/doctor-form.css"/>">
</head>
<body>
<c:set var="title">
    <spring:message code="header.doctor"/>
</c:set>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="${title}"/>
</jsp:include>
<c:url value="/createPatient" var="postPath"/>
<div class="page-container" style="align-items: center;">
    <div class="doctor-form-container">
        <form:form cssClass="doctor-form" modelAttribute="registerPatientForm" action="${postPath}" method="post">
            <div class="field-container">
                <form:errors path="name" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="name">
                        <spring:message code="doctorForm.name"/>
                    </form:label>
                    <form:input type="text" path="name" class="doctor-form-input"/>
                </div>
            </div>

            <div class="field-container">
                <form:errors path="surname" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="surname">
                        <spring:message code="doctorForm.surname"/>
                    </form:label>
                    <form:input type="text" path="surname" class="doctor-form-input"/>
                </div>
            </div>

            <div class="field-container">
                <form:errors path="email" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="email">
                        <spring:message code="doctorForm.email"/>
                    </form:label>
                    <form:input type="text" path="email" class="doctor-form-input"/>
                </div>
            </div>

            <div class="field-container">
                <form:errors path="password" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="email">
                        <spring:message code="doctorForm.password"/>
                    </form:label>
                    <form:input type="password" path="password" class="doctor-form-input"/>
                </div>
            </div>


            <div class="doctor-div">
                <input type="submit" value="<spring:message code="doctorForm.registerButton"/>" class="register-button"/>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>