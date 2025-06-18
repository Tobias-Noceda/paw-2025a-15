<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
    <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/appointments.css"/>"/>
</head>
<body>
<c:set var="title" value="vacations"/>
<c:set var="confirmationMessage">
    <spring:message code="vacations.cancelConfirm" />
</c:set>
<c:set var="confirmText">
    <spring:message code="vacations.cancel"/>
</c:set>
<c:set var="cancelText">
    <spring:message code="appointments.dismiss"/>
</c:set>
<c:set var="confirmationVacationMessage">
    <spring:message code="vacations.createConfirm" />
</c:set>
<c:set var="confirmVacationText">
    <spring:message code="vacations.create.ok"/>
</c:set>
<jsp:include page="components/header.jsp">
    <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="container">

    <!-- FORMULARIO DE NUEVA VACACIÓN EN “vacation-card” -->

    <div class="vacation-card">

        <h3 class="table-title">

            <spring:message code="vacations.create.title"/>

        </h3>


        <c:url var="saveVacationUrl" value="/createVacations"/>
        <form:form modelAttribute="vacationForm"
                   action="${saveVacationUrl}"
                   method="post">
            <form:hidden path="doctorId" />
            <div class="vacation-form">


                <div class="form-group">

                    <form:label path="startDate">

                        <spring:message code="vacations.table.startDate" />

                    </form:label>

                    <form:input path="startDate" type="date" cssClass="input-field"/>

                    <form:errors path="startDate" cssClass="error-text"/>

                </div>

                <div class="form-group">

                    <form:label path="endDate">

                        <spring:message code="vacations.table.endDate" />

                    </form:label>

                    <form:input path="endDate" type="date" cssClass="input-field"/>

                    <form:errors path="endDate" cssClass="error-text"/>

                </div>


                <!-- Submit -->

                <button type="button" class="navigation-button" onclick="event.stopPropagation(); openConfirmDialog(this.form, '${confirmationVacationMessage}', null, '${confirmVacationText}', '${cancelText}')">

                    <spring:message code="vacations.create.submit" />

                </button>

            </div>

            <form:errors path="*" cssClass="error-box" element="div"/>


        </form:form>

    </div>

    <!-- DOS TABLAS EN 2 COLUMNAS: FUTURAS Y PASADAS -->
    <div class="page-container appointments-div" style="display:flex; flex-direction:row; gap:2rem;">

        <!-- VACACIONES PASADAS -->
        <div class="appointment-list-container">
            <h3 class="table-title">
                <spring:message code="vacations.past.title" />
            </h3>
            <div class="appointment-table-container">
                <div class="appointments-table-header">
                    <table class="appointments-table">
                        <thead>
                        <tr>
                            <th><spring:message code="vacations.table.startDate" /></th>
                            <th><spring:message code="vacations.table.endDate" /></th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <c:if test="${not empty pastVacations}">
                    <div class="appointments-table-body">
                        <table class="appointments-table">
                            <tbody>
                            <c:forEach items="${pastVacations}" var="vac">
                                <tr class="appointment-row">
                                    <td class="text-cell">
                                        <c:out value="${vac.id.startDate}" default="-" />
                                    </td>
                                    <td class="text-cell">
                                        <c:out value="${vac.id.endDate}" default="-" />
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
                <c:if test="${empty pastVacations}">
                    <div class="no-appointments-container">
                        <h4 class="no-appointments-text">
                            <spring:message code="vacations.past.empty"/>
                        </h4>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- VACACIONES FUTURAS -->
        <div class="appointment-list-container">
            <h3 class="table-title">
                <spring:message code="vacations.future.title" />
            </h3>
            <div class="appointment-table-container">
                <div class="appointments-table-header">
                    <table class="appointments-table">
                        <thead>
                        <tr>
                            <th><spring:message code="vacations.table.startDate" /></th>
                            <th><spring:message code="vacations.table.endDate"   /></th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <c:if test="${not empty futureVacations}">
                    <div class="appointments-table-body">
                        <table class="appointments-table">
                            <tbody>
                            <c:forEach items="${futureVacations}" var="vac">
                                <tr class="appointment-row">
                                    <td class="text-cell">
                                        <c:out value="${vac.id.startDate}" default="-" />
                                    </td>
                                    <td class="text-cell">
                                        <c:out value="${vac.id.endDate}" default="-" />
                                    </td>
                                    <c:url var="cancelUrl" value="/cancelVacation" />
                                    <form:form modelAttribute="vacationForm" action="${cancelUrl}" method="post">
                                        <form:hidden path="doctorId" value="${vac.id.doctorId}" />
                                        <form:hidden path="startDate" value="${vac.id.startDate}" />
                                        <form:hidden path="endDate" value="${vac.id.endDate}" />
                                        <form:hidden path="canceling" value="true"/>
                                        <td class="cancel-cell">
                                            <button class="cancel-button" type="button" onclick="event.stopPropagation(); openConfirmDialog(this.form, '${confirmationMessage}', null, '${confirmText}', '${cancelText}')">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                                                    <path d="M3 6h18v2H3V6zm2 3h14l-1.5 13h-11L5 9zm5 2v8h2v-8H10zm4 0v8h2v-8h-2zM9 4V3h6v1h5v2H4V4h5z" />
                                                </svg>
                                            </button>
                                        </td>
                                    </form:form>
                                </tr>
                            </c:forEach>

                            </tbody>
                        </table>|
                    </div>
                </c:if>
                <c:if test="${empty futureVacations}">
                    <div class="no-appointments-container">
                        <h4 class="no-appointments-text">
                            <spring:message code="vacations.future.empty" />
                        </h4>
                    </div>
                </c:if>
            </div>
        </div>


    </div>
</div>
<%@include file="components/confirmDialog.jsp" %>
<%@include file="components/patientDialog.jsp" %>
<script src="<c:url value='/js/turnConfirmationModal.js'/>"></script>
<script src="<c:url value='/js/buttonControl.js'/>"></script>
<script src="<c:url value='/js/doctorDetailNav.js'/>"></script>
</body>
</html>