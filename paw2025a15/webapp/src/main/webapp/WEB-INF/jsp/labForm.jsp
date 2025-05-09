<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
    <link rel="stylesheet" href="<c:url value="/css/doctor-form.css"/>">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        .password-container {
            position: relative;
            width: 100%;
        }
        .password-container input {
            width: 100%;
            padding-right: 40px;
        }
        .password-container .toggle-password {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            color: #666;
        }
    </style>
</head>
<body>
<jsp:include page="components/header.jsp"></jsp:include>
<c:url value="/createLab" var="postPath"/>
<div class="page-container" style="align-items: center;">
    <div class="doctor-form-container">
        <form:form cssClass="doctor-form" modelAttribute="registerLabForm" action="${postPath}" method="post">
            <form:errors cssClass="form-error" element="p"/>

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
                <form:errors path="address" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="address">
                        <spring:message code="doctorForm.address"/>
                    </form:label>
                    <form:input type="text" path="address" class="doctor-form-input"/>
                </div>
            </div>

            <div class="field-container">
                <form:errors path="email" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="email">
                        <spring:message code="doctorForm.email"/>
                    </form:label>
                    <form:input type="email" path="email" class="doctor-form-input"/>
                </div>
            </div>

            <div class="field-container">
                <form:errors path="phoneNumber" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="phoneNumber">
                        <spring:message code="doctorForm.phone"/>
                    </form:label>
                    <form:input type="text" path="phoneNumber" class="doctor-form-input"/>
                </div>
            </div>

            <div class="field-container">
                <form:errors path="password" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="password">
                        <spring:message code="doctorForm.password"/>
                    </form:label>
                    <div class="password-container">
                        <form:input type="password" path="password" class="doctor-form-input" id="password"/>
                        <span class="toggle-password" onclick="togglePassword('password')">
                            <i class="fas fa-eye"></i>
                        </span>
                    </div>
                </div>
            </div>

            <div class="field-container">
                <form:errors path="confirmPassword" cssClass="form-error" element="p"/>
                <div class="field-info-container">
                    <form:label cssClass="form-title" path="confirmPassword">
                        <spring:message code="doctorForm.confirmPassword"/>
                    </form:label>
                    <div class="password-container">
                        <form:input type="password" path="confirmPassword" class="doctor-form-input" id="confirmPassword"/>
                        <span class="toggle-password" onclick="togglePassword('confirmPassword')">
                            <i class="fas fa-eye"></i>
                        </span>
                    </div>
                </div>
            </div>

            <div class="doctor-div">
                <input type="submit" value="<spring:message code='doctorForm.registerButton'/>" class="register-button"/>
            </div>
        </form:form>
    </div>
</div>

<script>
    function togglePassword(fieldId) {
        const field = document.getElementById(fieldId);
        const icon = field.nextElementSibling.querySelector('i');
        if (field.type === "password") {
            field.type = "text";
            icon.classList.remove("fa-eye");
            icon.classList.add("fa-eye-slash");
        } else {
            field.type = "password";
            icon.classList.remove("fa-eye-slash");
            icon.classList.add("fa-eye");
        }
    }
</script>
</body>
</html>
