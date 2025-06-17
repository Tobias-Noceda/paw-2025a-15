<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/appointments.css"/>"/>
</head>
<body>
<c:set var="title" value="vacations"/>
<jsp:include page="components/header.jsp">
    <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="container">

    <!-- FORMULARIO DE NUEVA VACACIÓN EN “vacation-card” -->

    <div class="vacation-card">

        <h3 class="table-title">

            <spring:message code="vacations.create.title" text="Register a new vacation"/>

        </h3>


        <c:url var="saveVacationUrl" value="/createVacations"/>
        <form:form modelAttribute="vacationForm"
                   action="${saveVacationUrl}"
                   method="post">
            <form:hidden path="doctorId" />
            <div class="vacation-form">


            <form:errors path="*" cssClass="error-box" element="div"/>
                <div class="form-group">

                    <form:label path="startDate">

                        <spring:message code="vacations.table.startDate" text="Start Date"/>

                    </form:label>

                    <form:input path="startDate" type="date" cssClass="input-field"/>

                    <form:errors path="startDate" cssClass="error-text"/>

                </div>



                <div class="form-group">

                    <form:label path="endDate">

                        <spring:message code="vacations.table.endDate" text="End Date"/>

                    </form:label>

                    <form:input path="endDate" type="date" cssClass="input-field"/>

                    <form:errors path="endDate" cssClass="error-text"/>

                </div>


                <!-- Submit -->

                <button type="submit" class="navigation-button">

                    <spring:message code="vacations.create.submit" text="Submit vacation"/>

                </button>

            </div>


        </form:form>

    </div>

    <!-- DOS TABLAS EN 2 COLUMNAS: FUTURAS Y PASADAS -->
    <div class="page-container appointments-div" style="display:flex; flex-direction:row; gap:2rem;">





        <!-- VACACIONES FUTURAS -->
        <div class="appointment-list-container">
            <h3 class="table-title">
                <spring:message code="vacations.future.title" text="Vacaciones Futuras"/>
            </h3>
            <div class="appointment-table-container">
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
                                </tr>
                            </c:forEach>

                            </tbody>
                        </table>|
                    </div>
                </c:if>
                <c:if test="${empty futureVacations}">
                    <div class="no-appointments-container">
                        <h4 class="no-appointments-text">
                            <spring:message code="vacations.future.empty" text="No hay vacaciones futuras."/>
                        </h4>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- VACACIONES PASADAS -->
        <div class="appointment-list-container">
            <h3 class="table-title">
                <spring:message code="vacations.past.title" text="Vacaciones Pasadas"/>
            </h3>
            <div class="appointment-table-container">
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
                            <spring:message code="vacations.past.empty" text="No hay vacaciones pasadas."/>
                        </h4>
                    </div>
                </c:if>
            </div>
        </div>

    </div>
</div>
</body>
</html>