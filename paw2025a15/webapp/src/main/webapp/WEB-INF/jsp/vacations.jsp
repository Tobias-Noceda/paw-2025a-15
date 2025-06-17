<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>
</head>
<body>
<c:set var="title" value="vacations"/>
<jsp:include page="components/header.jsp">
    <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="container">
    <!-- LISTADO DE VACACIONES -->
    <div class="appointment-list-container">
        <h3 class="table-title">
            <spring:message code="vacations.title" text="Historial de Vacaciones"/>
        </h3>
        <div class="appointment-table-container">

            <!-- Cabecera fija -->
            <div class="appointments-table-header">
                <table class="appointments-table">
                    <thead>
                    <tr>
                        <th><spring:message code="vacations.table.startDate" text="Desde"/></th>
                        <th><spring:message code="vacations.table.endDate"   text="Hasta"/></th>
                    </tr>
                    </thead>
                </table>
            </div>

            <!-- Cuerpo con datos -->
            <c:if test="${not empty vacations}">
                <div class="appointments-table-body">
                    <table class="appointments-table">
                        <tbody>
                        <c:forEach items="${vacations}" var="vac">
                            <tr class="appointment-row">
                                <td class="text-cell">
                                    <fmt:formatDate value="${vac.startDate}" pattern="yyyy-MM-dd"/>
                                </td>
                                <td class="text-cell">
                                    <fmt:formatDate value="${vac.endDate}"   pattern="yyyy-MM-dd"/>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>

            <!-- Mensaje vacío -->
            <c:if test="${empty vacations}">
                <div class="no-appointments-container">
                    <h4 class="no-appointments-text">
                        <spring:message code="vacations.none" text="No hay vacaciones registradas."/>
                    </h4>
                </div>
            </c:if>

        </div>
    </div>

    <!-- FORMULARIO DE CREACIÓN -->
    <div class="appointment-list-container" style="margin-top: 2rem;">
        <h3 class="table-title">
            <spring:message code="vacations.create.title" text="Registrar Nueva Vacación"/>
        </h3>
        <c:url var="saveVacationUrl" value="/vacations/create"/>


        <form:form modelAttribute="vacationForm" action="${saveVacationUrl}" method="post" cssClass="create-vacation-form">
            <div class="flex-container" style="gap:1rem; align-items: flex-end;">

                <div class="form-group">
                    <form:label path="startDate">
                        <spring:message code="vacations.table.startDate" text="Desde"/>
                    </form:label>
                    <form:input path="startDate" type="date" cssClass="input-field"/>
                    <form:errors path="startDate" cssClass="error-text"/>
                </div>

                <div class="form-group">
                    <form:label path="endDate">
                        <spring:message code="vacations.table.endDate" text="Hasta"/>
                    </form:label>
                    <form:input path="endDate" type="date" cssClass="input-field"/>
                    <form:errors path="endDate" cssClass="error-text"/>
                </div>

                <div class="form-group">
                    <button type="submit" class="navigation-button">
                        <spring:message code="vacations.create.submit" text="Crear"/>
                    </button>
                </div>

            </div>
        </form:form>

    </div>
</div>

</body>
</html>