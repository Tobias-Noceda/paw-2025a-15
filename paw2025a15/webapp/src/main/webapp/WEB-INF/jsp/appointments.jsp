<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
  <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
  <link rel="stylesheet" href="<c:url value="/css/appointments.css"/>">
  <link rel="stylesheet" href="<c:url value="/css/main.css"/>">
</head>
<body>
<c:set var="title">appointments</c:set>
<jsp:include page="components/header.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>
<fmt:setLocale value="${pageContext.request.locale}" />
<div class="page-container appointments-div" style="display: flex; flex-direction: row;">
  <c:url var="cancelUrl" value="/cancelAppointment" />
  <c:set var="confirmationMessage">
    <spring:message code="appointments.cancelConfirm" />
  </c:set>
  <c:set var="confirmText">
    <spring:message code="appointments.cancel"/>
  </c:set>
  <c:set var="cancelText">
    <spring:message code="appointments.dismiss"/>
  </c:set>
  <sec:authorize access="hasRole('ROLE_PATIENT')">
    <div class="appointment-list-container">
      <h3 class="table-title"><spring:message code="appointments.future"/></h3>
      <div class="appointment-table-container">
        <div class="appointments-table-header">
          <table class="appointments-table">
            <thead>
            <tr>
              <th><spring:message code="appointmentTable.doctorColumn.title"/></th>
              <th><spring:message code="appointmentTable.dateColumn.title"/></th>
              <th><spring:message code="appointmentTable.timeColumn.title"/></th>
              <th><spring:message code="appointmentTable.addressColumn.title"/></th>
              <th class="last-column"><spring:message code="appointmentTable.actionColumn.title"/></th>
            </tr>
            </thead>
          </table>
        </div>
        <c:if test="${not empty patientFutureAppointments}">
          <div class="appointments-table-body">
            <table class="appointments-table">
              <tbody>
              <c:forEach var="appointment" items="${patientFutureAppointments}">
                <c:url value="/doctors/${appointment.shift.doctor.id}" var="doctorUrl" />
                <tr class="appointment-row" onclick="window.location='${doctorUrl}'" style="cursor: pointer;">
                  <c:set var="formattedDate">
                    <fmt:formatDate value="${appointment.id.dateAsDate}" dateStyle="short" />
                  </c:set>
                  <td class="text-cell"><c:out value="${appointment.shift.doctor.name}" escapeXml="true" /></td>
                  <td class="text-cell"><c:out value="${formattedDate}" escapeXml="true" /></td>
                  <td class="text-cell"><c:out value="${appointment.id.getStartToEndTime()}" escapeXml="true" /></td>
                  <td class="text-cell"><c:out value="${appointment.shift.address}" escapeXml="true" /></td>
                  <td class="cancel-cell">
                    <form:form modelAttribute="appointmentForm" action="${cancelUrl}" method="post">
                      <form:hidden path="shiftId" value="${appointment.id.shiftId}" />
                      <form:hidden path="date" value="${appointment.id.date}" />
                      <form:hidden path="startTime" value="${appointment.id.startTime}" />
                      <form:hidden path="endTime" value="${appointment.id.endTime}" />
                      <button class="cancel-button" type="button" onclick="event.stopPropagation(); openConfirmDialog(this.form, '${confirmationMessage}', null, '${confirmText}', '${cancelText}')">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                          <path d="M3 6h18v2H3V6zm2 3h14l-1.5 13h-11L5 9zm5 2v8h2v-8H10zm4 0v8h2v-8h-2zM9 4V3h6v1h5v2H4V4h5z" />
                        </svg>
                      </button>
                    </form:form>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:if>
        <c:if test="${empty patientFutureAppointments}">
          <div class="no-appointments-container">
            <h4 class="no-appointments-text"><spring:message code="appointments.future.empty"/></h4>
          </div>
        </c:if>
      </div>
    </div>
    <div class="appointment-list-container">
      <h3 class="table-title"><spring:message code="appointments.past"/></h3>
      <div class="appointment-table-container">
        <div class="appointments-table-header">
          <table class="appointments-table">
            <thead>
            <tr>
              <th><spring:message code="appointmentTable.doctorColumn.title"/></th>
              <th><spring:message code="appointmentTable.dateColumn.title"/></th>
              <th><spring:message code="appointmentTable.timeColumn.title"/></th>
              <th><spring:message code="appointmentTable.addressColumn.title"/></th>
            </tr>
            </thead>
          </table>
        </div>
        <c:if test="${not empty patientOldAppointments}">
          <div class="appointments-table-body">
            <table class="appointments-table">
              <tbody>
              <c:forEach var="appointment" items="${patientOldAppointments}">
                <c:url value="/doctors/${appointment.shift.doctor.id}" var="doctorUrl" />
                <tr class="appointment-row" onclick="window.location='${doctorUrl}'" style="cursor: pointer;">
                  <c:set var="formattedDate">
                    <fmt:formatDate value="${appointment.id.dateAsDate}" dateStyle="short" />
                  </c:set>
                  <td class="text-cell"><c:out value="${appointment.shift.doctor.name}" escapeXml="true" /></td>
                  <td class="text-cell"><c:out value="${formattedDate}" escapeXml="true" /></td>
                  <td class="text-cell"><c:out value="${appointment.id.getStartToEndTime()}" escapeXml="true" /></td>
                  <td class="text-cell"><c:out value="${appointment.shift.address}" escapeXml="true" /></td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:if>
        <c:if test="${empty patientOldAppointments}">
          <div class="no-appointments-container">
            <h4 class="no-appointments-text"><spring:message code="appointments.past.empty"/></h4>
          </div>
        </c:if>
      </div>
    </div>
  </sec:authorize>

  <sec:authorize access="hasRole('ROLE_DOCTOR')">
    <div class="appointment-list-container">
      <h3 class="table-title"><spring:message code="appointments.taken"/></h3>
      <div class="appointment-table-container">
        <div class="appointments-table-header">
          <table class="appointments-table">
            <thead>
            <tr>
              <th><spring:message code="appointmentTable.patientColumn.title"/></th>
              <th><spring:message code="appointmentTable.dateColumn.title"/></th>
              <th><spring:message code="appointmentTable.timeColumn.title"/></th>
              <th class="last-column"><spring:message code="appointmentTable.actionColumn.title"/></th>
            </tr>
            </thead>
          </table>
        </div>
        <c:if test="${not empty doctorTakenAppointments}">
          <div class="appointments-table-body">
            <table class="appointments-table">
              <tbody>
              <c:forEach var="appointment" items="${doctorTakenAppointments}">
                <c:url value="/patient/${appointment.patient.id}" var="patientUrl" />
                <tr class="appointment-row" onclick="openPatientDialog( '${appointment.patient.name}','${appointment.detail}', '${patientUrl}')" style="cursor: pointer;">
                  <c:set var="formattedDate">
                    <fmt:formatDate value="${appointment.id.dateAsDate}" dateStyle="short" />
                  </c:set>
                  <td class="text-cell"><c:out value="${appointment.patient.name}" escapeXml="true" /></td>
                  <td class="text-cell"><c:out value="${formattedDate}" escapeXml="true" /></td>
                  <td class="text-cell"><c:out value="${appointment.id.getStartToEndTime()}" escapeXml="true" /></td>
                  <td class="cancel-cell">
                    <form:form modelAttribute="appointmentForm" action="${cancelUrl}" method="post">
                      <form:hidden path="shiftId" value="${appointment.id.shiftId}" />
                      <form:hidden path="date" value="${appointment.id.date}" />
                      <form:hidden path="startTime" value="${appointment.id.startTime}" />
                      <form:hidden path="endTime" value="${appointment.id.endTime}" />
                      <button class="cancel-button" type="button" onclick="event.stopPropagation(); openConfirmDialog(this.form, '${confirmationMessage}', null, '${confirmText}', '${cancelText}')">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                          <path d="M3 6h18v2H3V6zm2 3h14l-1.5 13h-11L5 9zm5 2v8h2v-8H10zm4 0v8h2v-8h-2zM9 4V3h6v1h5v2H4V4h5z" />
                        </svg>
                      </button>
                    </form:form>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:if>
        <c:if test="${empty doctorTakenAppointments}">
          <div class="no-appointments-container">
            <h4 class="no-appointments-text"><spring:message code="appointments.taken.empty"/></h4>
          </div>
        </c:if>
      </div>
    </div>
    <div class="appointment-list-container">
      <div class="appointments-title-container">
        <h3 class="table-title"><spring:message code="appointments.free"/></h3>
        <form:form class="week-navigator-div" action="${getPath}" method="GET" modelAttribute="shiftsDayForm">
          <div class="flex-container">
            <div>
              <button
                type="button"
                class="navigation-button"
                onclick="submitFormWithAction('previous')"
                <c:if test="${!shiftsDayForm.hasPrevious()}">disabled</c:if>
              >
                <spring:message code="doctorDetail.previousWeek"/>
              </button>
            </div>
            <div>
              <form:input cssClass="input-field" id="dateSelector" path="date" type="date" min="${today}" max="${maxDate}" onchange="submitFormWithAction('setDate')"/>
            </div>
            <div>
              <button
                type="button"
                class="navigation-button"
                onclick="submitFormWithAction('next')"
              >
                <spring:message code="doctorDetail.nextWeek"/>
              </button>
            </div>
          </div>
        </form:form>
      </div>
      <c:if test="${not empty removeError}">
        <div class="error-box">
          <spring:message code="doctor.details.takenTurn"></spring:message>
        </div>
      </c:if>
      <div class="appointment-table-container">
        <div class="appointments-table-header">
          <table class="appointments-table">
            <thead>
            <tr>
              <th><spring:message code="appointmentTable.weekdayColumn.title"/></th>
              <th><spring:message code="appointmentTable.monthdayColumn.title"/></th>
              <th><spring:message code="appointmentTable.timeColumn.title"/></th>
              <th class="last-column"><spring:message code="appointmentTable.actionColumn.title"/></th>
            </tr>
            </thead>
          </table>
        </div>
        <c:if test="${not empty doctorFreeAppointments}">
          <div class="appointments-table-body">
            <table class="appointments-table">
              <tbody>
              <c:forEach var="appointment" items="${doctorFreeAppointments}">
                <tr class="appointment-row">
                  <td class="text-cell"><spring:message code="weekday.${appointment.date.dayOfWeek}"/></td>
                  <td class="text-cell"><c:out value="${appointment.date.dayOfMonth}" escapeXml="true" /></td>
                  <td class="text-cell"><c:out value="${appointment.getStartToEndTime()}" escapeXml="true" /></td>
                  <td class="cancel-cell">
                    <c:set var="removeConfirmationMessage">
                      <spring:message code="appointments.removeConfirm" arguments="${appointment.getStartToEndTime()}"/>
                    </c:set>
                    <c:set var="secondaryText"><spring:message code="appointments.remove.second"/></c:set>
                    <c:set var="removeText"><spring:message code="appointments.remove"/></c:set>
                    <c:set var="cancelRemoveText"><spring:message code="appointments.dismiss"/></c:set>
                    <c:url var="removeUrl" value="/removeAppointment" />
                    <form:form modelAttribute="takeTurnForm" action="${removeUrl}" method="post" id="removeAppointmentForm">
                      <form:hidden path="shiftId" value="${appointment.shiftId}" />
                      <form:hidden path="date" value="${appointment.date}" />
                      <form:hidden path="startTime" value="${appointment.startTime}" />
                      <form:hidden path="endTime" value="${appointment.endTime}" />
                      <form:hidden path="doctorId" value="${user_data.id}"/>
                      <button class="cancel-button" type="button" onclick="openConfirmDialog(this.form, '${removeConfirmationMessage}', '${secondaryText}', '${removeText}', '${cancelRemoveText}')">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                          <path d="M3 6h18v2H3V6zm2 3h14l-1.5 13h-11L5 9zm5 2v8h2v-8H10zm4 0v8h2v-8h-2zM9 4V3h6v1h5v2H4V4h5z"/>
                        </svg>
                      </button>
                    </form:form>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:if>
        <c:if test="${empty doctorFreeAppointments}">
          <div class="no-appointments-container">
            <h4 class="no-appointments-text"><spring:message code="appointments.free.empty"/></h4>
          </div>
        </c:if>
      </div>
    </div>
  </sec:authorize>
</div>
<%@include file="components/confirmDialog.jsp" %>
<%@include file="components/patientDialog.jsp" %>
<script src="<c:url value='/js/turnConfirmationModal.js'/>"></script>
<script src="<c:url value='/js/buttonControl.js'/>"></script>
<script src="<c:url value='/js/doctorDetailNav.js'/>"></script>
</body>
</html>