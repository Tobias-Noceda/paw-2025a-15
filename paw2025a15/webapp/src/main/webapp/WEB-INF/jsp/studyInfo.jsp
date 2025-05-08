<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/studies.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/patient-detail.css"/>">
</head>
<body>
<jsp:include page="components/header.jsp">
    <jsp:param name="username" value="${user.name}"/>
    <jsp:param name="id" value="${user.id}"/>
    <jsp:param name="role" value="${user.role}"/>
</jsp:include>

<div class="page-container" style="display: flex; flex-direction: row; gap: 2rem; padding: 2rem;">
    <!-- Panel izquierdo: Detalle del estudio (más angosto) -->
    <div class="patient-card" style="flex: 0.5;">
        <div class="patient-info">
            <h2 class="patient-name"><c:out value="${study.comment}"/></h2>

            <!-- Tipo de estudio -->
            <div class="study-detail">
                <strong><spring:message code="studyTable.typeColumn.title"/>:</strong>
                <span><spring:message code="studyType.${study.type}"/></span>
            </div>

            <!-- Fecha del estudio -->
            <c:set var="studyDay">
                <fmt:formatNumber value="${study.studyDate.dayOfMonth}" pattern="00"/>
            </c:set>
            <c:set var="studyMonth">
                <fmt:formatNumber value="${study.studyDate.monthValue}" pattern="00"/>
            </c:set>
            <c:set var="studyYear" value="${study.studyDate.year}" />
            <div class="study-detail">
                <strong><spring:message code="appointmentTable.dateColumn.title"/>:</strong>
                <span><spring:message code="dateFormat" arguments="${studyDay},${studyMonth},${studyYear}"/></span>
            </div>

            <!-- Fecha de subida -->
            <c:set var="uploadDay">
                <fmt:formatNumber value="${study.uploadDate.dayOfMonth}" pattern="00"/>
            </c:set>
            <c:set var="uploadMonth">
                <fmt:formatNumber value="${study.uploadDate.monthValue}" pattern="00"/>
            </c:set>
            <c:set var="uploadYear" value="${study.uploadDate.year}" />
            <div class="study-detail">
                <strong><spring:message code="studyTable.uploadDateColumn.title"/>:</strong>
                <span><spring:message code="dateFormat" arguments="${uploadDay},${uploadMonth},${uploadYear}"/></span>
            </div>
        </div>
    </div>

    <!-- Panel derecho: Otro contenido (por ejemplo, doctores autorizados) -->
    <div class="study-list-container" style="flex: 1;">
        <h3 class="table-title"><spring:message code="studies.authorizedDoctors"/></h3>
        <div class="study-table-container">
            <div class="studies-table-header">
                <table class="studies-table">
                    <thead>
                    <tr>
                        <th><spring:message code="appointmentTable.doctorColumn.title"/></th>
                        <th><spring:message code="studyTable.specialtyColumn.title"/></th>
                        <th class="doctors-last-column"><spring:message code="appointmentTable.actionColumn.title"/></th>
                    </tr>
                    </thead>
                </table>
            </div>

            <c:if test="${not empty patientAuthDoctors}">
                <div class="studies-table-body">
                    <table class="studies-table">
                        <tbody>
                        <c:set var="buttonText">
                            <spring:message code="doctorDetail.toggleButton.deauthorize"/>
                        </c:set>
                        <c:set var="confirmationText">
                            <spring:message code="doctorDetail.deauthorize.confirm"/>
                        </c:set>
                        <c:set var="authCancelText">
                            <spring:message code="doctorDetail.authorize.cancelButton"/>
                        </c:set>
                        <c:forEach var="doctor" items="${patientAuthDoctors}">
                            <c:url value="/doctors/${doctor.id}" var="doctorUrl"/>
                            <c:url value="/authFileDoctor/${doctor.id}/${study.id}" var="updateAuthUrl" />
                            <tr class="doctor-row" onclick="window.location='${doctorUrl}'" style="cursor:pointer;">
                                <td class="text-cell">
                                    <c:out value="${doctor.name}"/>
                                </td>
                                <td class="text-cell">
                                    <spring:message code="specialty.${doctor.specialty}"/>
                                </td>
                                <td class="deauthorize-cell">
                                    <form action="${updateAuthUrl}" method="post">
                                        <button
                                                type="button"
                                                name="action"
                                                value="toggle"
                                                class="deauthorize-button"
                                                onclick="confirmAuthDoctor('${confirmationText}', null, '${buttonText}', '${authCancelText}', this.name, this.value);"
                                        >
                                            <c:out value="${buttonText}"/>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
            <c:if test="${empty patientAuthDoctors}">
                <div class="no-studies-container">
                    <h4 class="no-studies-text"><spring:message code="studies.authorizedDoctors.empty"/></h4>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script src="<c:url value='/js/authConfirmationModal.js'/>"></script>
<%@include file="components/confirmDialog.jsp" %>
</body>
</html>
