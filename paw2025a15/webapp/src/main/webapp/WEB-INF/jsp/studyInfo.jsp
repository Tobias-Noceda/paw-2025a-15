<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
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
            <h2 class="patient-name"><c:out value="${study.comment}" escapeXml="true"/></h2>

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
        <hr style="border: 1px solid #ccc; margin: 20px 0;" /><!--TODO: ver siesta bien esto con el estilo aca-->

        <div class="action-buttons">
            <c:url value="/view-study/${study.id}" var="studyLink" />

            <a href="${studyLink}"
               target="_blank"
               onclick="event.stopPropagation();">
                <button
                        class="view-button"


                >
                    <spring:message code="study.view"/>
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                        <path fill="currentColor"
                              d="M12 5c-7.633 0-11 7-11 7s3.367 7 11 7 11-7 11-7-3.367-7-11-7zm0 12c-2.761 0-5-2.239-5-5s2.239-5 5-5 5 2.239 5 5-2.239 5-5 5zm0-8c-1.657 0-3 1.343-3 3s1.343 3 3 3 3-1.343 3-3-1.343-3-3-3z"/>
                    </svg>
                </button>
            </a>

            <a                     href="${studyLink}"
                                   download="${studyName}"
                                   onclick="event.stopPropagation();"
            >
                <button
                    class="download-button"

            >
                    <spring:message code="study.download"/>
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                        <path fill="currentColor"
                              d="M12 16a1 1 0 0 1-.7-.29l-4-4a1 1 0 0 1 1.41-1.41L11 12.59V4a1 1 0 0 1 2 0v8.59l2.29-2.29a1 1 0 0 1 1.41 1.41l-4 4a1 1 0 0 1-.7.29zM19 14a1 1 0 0 0-1 1v3a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1v-3a1 1 0 0 0-2 0v3a3 3 0 0 0 3 3h10a3 3 0 0 0 3-3v-3a1 1 0 0 0-1-1z"/>
                    </svg>
                </button>
            </a>
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
                            <c:set var="hasAuth" value="${authMap[doctor.id]}" />
                            <c:set var="buttonClass" value="${hasAuth ? 'btn-red' : 'btn-green'}"/>
                            <c:set var="buttonLabel" value="${hasAuth ? 'Desautorizar' : 'Autorizar'}"/>

                            <c:url value="/doctors/${doctor.id}" var="doctorUrl"/>
                            <c:url value="/authFileDoctor/${doctor.id}/${study.id}" var="updateAuthUrl" />

                            <tr class="doctor-row" onclick="window.location='${doctorUrl}'" style="cursor:pointer;">
                                <td class="text-cell">
                                    <c:out value="${doctor.name}" escapeXml="true"/>
                                </td>
                                <td class="text-cell">
                                    <spring:message code="specialty.${doctor.specialty}"/>
                                </td>
                                <td class="deauthorize-cell">
                                    <form action="${updateAuthUrl}" method="post">
                                        <button
                                                type="submit"
                                                name="action"
                                                value="toggle"
                                                class="${buttonClass}"
                                                onclick="confirmAuthDoctor('${confirmationText}', null, '${buttonLabel}', '${authCancelText}', this.name, this.value);"
                                        >
                                            <c:out value="${buttonLabel}" escapeXml="true"/>
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

<%@include file="components/confirmDialog.jsp" %>
<script src="<c:url value='/js/main.js'/>"></script>
<script src="<c:url value='/js/buttonControl.js'/>"></script>
</body>
</html>
