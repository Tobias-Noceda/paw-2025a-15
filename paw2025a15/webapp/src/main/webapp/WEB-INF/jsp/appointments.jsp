<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
    <link rel="stylesheet" href="<c:url value="/css/appointments.css"/>">
  </head>
  <body>
    <c:set var="title">appointments</c:set>
    <jsp:include page="components/header.jsp">
      <jsp:param name="title" value="${title}"/>
    </jsp:include>
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
      <c:if test="${user_data.role == 'PATIENT'}">
        <div class="appointment-list-container">
          <h3 class="table-title"><spring:message code="appointments.future"></spring:message></h3>
          <div class="appointment-table-container">
            <div class="appointments-table-header">
              <table class="appointments-table">
                <thead>
                  <tr>
                    <th><spring:message code="appointmentTable.doctorColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.dateColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.timeColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.addressColumn.title"></spring:message></th>
                    <th class="last-column"><spring:message code="appointmentTable.actionColumn.title"></spring:message></th>
                  </tr>
                </thead>
              </table>
            </div>
            <c:if test="${not empty patientFutureAppointments}">
              <div class="appointments-table-body">
                <table class="appointments-table">
                  <tbody>
                    <c:forEach var="appointment" items="${patientFutureAppointments}">
                      <c:url value="/doctors/${appointment.doctorId}" var="doctorUrl" />
                      <tr
                        class="appointment-row"
                        onclick="window.location='${doctorUrl}'"
                        style="cursor: pointer;"
                      >
                        <c:set var="day">
                          <fmt:formatNumber value="${appointment.date.dayOfMonth}" pattern="00" />
                        </c:set>
                        <c:set var="month">
                          <fmt:formatNumber value="${appointment.date.monthValue}" pattern="00" />
                        </c:set>
                        <c:set var="year" value="${appointment.date.year}" />
                        <c:set var="formattedDate">
                          <spring:message code="dateFormat" arguments="${day},${month},${year}" htmlEscape="true"></spring:message>
                        </c:set>
                        <td class="text-cell"><c:out value="${appointment.doctorName}" escapeXml="true"/></td>
                        <td class="text-cell"><c:out value="${formattedDate}" escapeXml="true"/></td>
                        <td class="text-cell"><c:out value="${appointment.getStartToEndTime()}" escapeXml="true"/></td>
                        <td class="text-cell"><c:out value="${appointment.address}" escapeXml="true"/></td>
                        <td class="cancel-cell">
                          <form:form modelAttribute="appointmentForm" action="${cancelUrl}" method="post">
                            <form:hidden path="shiftId" value="${appointment.shiftId}" />
                            <form:hidden path="date" value="${appointment.date}" />
                            <button
                              class="cancel-button"
                              type="button"
                              onclick="openConfirmDialog(this.form, '${confirmationMessage}', null, '${confirmText}', '${cancelText}')"
                            >
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
          <h3 class="table-title"><spring:message code="appointments.past"></spring:message></h3>
          <div class="appointment-table-container">
            <div class="appointments-table-header">
              <table class="appointments-table">
                <thead>
                  <tr>
                    <th><spring:message code="appointmentTable.doctorColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.dateColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.timeColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.addressColumn.title"></spring:message></th>
                  </tr>
                </thead>
              </table>
            </div>
            <c:if test="${not empty patientOldAppointments}">
              <div class="appointments-table-body">
                <table class="appointments-table">
                  <tbody>
                    <c:forEach var="appointment" items="${patientOldAppointments}">
                      <c:url value="/doctors/${appointment.doctorId}" var="doctorUrl" />
                      <tr
                        class="appointment-row"
                        onclick="window.location='${doctorUrl}'"
                        style="cursor: pointer;"
                      >
                        <c:set var="day">
                          <fmt:formatNumber value="${appointment.date.dayOfMonth}" pattern="00" />
                        </c:set>
                        <c:set var="month">
                          <fmt:formatNumber value="${appointment.date.monthValue}" pattern="00" />
                        </c:set>
                        <c:set var="year" value="${appointment.date.year}" />
                        <c:set var="formattedDate">
                          <spring:message code="dateFormat" arguments="${day},${month},${year}" htmlEscape="true"></spring:message>
                        </c:set>
                        <td class="text-cell"><c:out value="${appointment.doctorName}" escapeXml="true"/></td>
                        <td class="text-cell"><c:out value="${formattedDate}" escapeXml="true"/></td>
                        <td class="text-cell"><c:out value="${appointment.getStartToEndTime()}" escapeXml="true"/></td>
                        <td class="text-cell"><c:out value="${appointment.address}" escapeXml="true"/></td>
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
      </c:if>
      <c:if test="${user_data.role == 'DOCTOR'}">
        <div class="appointment-list-container">
          <h3 class="table-title"><spring:message code="appointments.taken"></spring:message></h3>
          <div class="appointment-table-container">
            <div class="appointments-table-header">
              <table class="appointments-table">
                <thead>
                  <tr>
                    <th><spring:message code="appointmentTable.patientColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.dateColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.timeColumn.title"></spring:message></th>
                    <th class="last-column"><spring:message code="appointmentTable.actionColumn.title"></spring:message></th>
                  </tr>
                </thead>
              </table>
            </div>
            <c:if test="${not empty doctorTakenAppointments}">
              <div class="appointments-table-body">
                <table class="appointments-table">
                  <tbody>
                    <c:forEach var="appointment" items="${doctorTakenAppointments}">
                      <c:url value="/patient/${appointment.patientId}" var="patientUrl" />
                      <tr
                        class="appointment-row"
                        onclick="window.location='${patientUrl}'"
                        style="cursor: pointer;"
                      >
                        <c:set var="day">
                          <fmt:formatNumber value="${appointment.date.dayOfMonth}" pattern="00" />
                        </c:set>
                        <c:set var="month">
                          <fmt:formatNumber value="${appointment.date.monthValue}" pattern="00" />
                        </c:set>
                        <c:set var="year" value="${appointment.date.year}" />
                        <c:set var="formattedDate">
                          <spring:message code="dateFormat" arguments="${day},${month},${year}" htmlEscape="true"></spring:message>
                        </c:set>
                        <td class="text-cell"><c:out value="${appointment.patientName}" escapeXml="true"/></td>
                        <td class="text-cell"><c:out value="${formattedDate}" escapeXml="true"/></td>
                        <td class="text-cell"><c:out value="${appointment.getStartToEndTime()}" escapeXml="true"/></td>
                        <td class="cancel-cell">
                          <form:form modelAttribute="appointmentForm" action="${cancelUrl}" method="post">
                            <form:hidden path="shiftId" value="${appointment.shiftId}" />
                            <form:hidden path="date" value="${appointment.date}" />
                            <button
                              class="cancel-button"
                              type="button"
                              onclick="openConfirmDialog(this.form, '${confirmationMessage}', null, '${confirmText}', '${cancelText}')"
                            >
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
          <div class="doctor-free-header">
            <h3 class="table-title"><spring:message code="appointments.free"></spring:message></h3>
            <c:url value="/appointments" var="getPath"/>
            <form:form class="week-navigator-div" action="${getPath}" method="GET" modelAttribute="shiftsWeekForm">
              <form:hidden path="index" />
              <button
                type="submit"
                name="action"
                value="previous"
                class="navigation-button"
                <c:if test="${!shiftsWeekForm.hasPrevious()}">disabled</c:if>
              >
                <spring:message code="doctorDetail.previousWeek"/>
              </button>
              <div class="selected-month">
                <c:set var="month">
                  <spring:message code="month.${shiftsWeekForm.month}"/>
                </c:set>
                <spring:message code="doctorDetail.selectedWeek" arguments="${month},${shiftsWeekForm.startDate.year},${shiftsWeekForm.weekOfMonth + 1}"/>
              </div>
              <button
                type="submit"
                name="action"
                value="next"
                class="navigation-button"
                <c:if test="${!shiftsWeekForm.hasNext()}">disabled</c:if>
              >
                <spring:message code="doctorDetail.nextWeek"/>
              </button>
            </form:form>
          </div>
          <div class="appointment-table-container">
            <div class="appointments-table-header">
              <table class="appointments-table">
                <thead>
                  <tr>
                    <th><spring:message code="appointmentTable.weekdayColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.monthdayColumn.title"></spring:message></th>
                    <th><spring:message code="appointmentTable.timeColumn.title"></spring:message></th>
                    <th class="last-column"><spring:message code="appointmentTable.actionColumn.title"></spring:message></th>
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
                        <td class="text-cell"><spring:message code="weekday.${appointment.date.dayOfWeek}"></spring:message></td>
                        <td class="text-cell"><c:out value="${appointment.date.dayOfMonth}" escapeXml="true"/></td>
                        <td class="text-cell"><c:out value="${appointment.getStartToEndTime()}" escapeXml="true"/></td>
                        <td class="cancel-cell">
                          <c:set var="removeConfirmationMessage">
                            <spring:message code="appointments.removeConfirm" arguments="${appointment.getStartToEndTime()}"/>
                          </c:set>
                          <c:set var="secondaryText">
                            <spring:message code="appointments.remove.second"/>
                          </c:set>
                          <c:set var="removeText">
                            <spring:message code="appointments.remove"/>
                          </c:set>
                          <c:set var="cancelRemoveText">
                            <spring:message code="appointments.dismiss"/>
                          </c:set>
                          <c:url var="removeUrl" value="/removeAppointment" />
                          <form:form
                            modelAttribute="takeTurnForm"
                            action="${removeUrl}"
                            method="post"
                            id="removeAppointmentForm"
                          >
                            <form:hidden path="shiftId" value="${appointment.shiftId}" />
                            <form:hidden path="date" value="${appointment.date}" />
                            <button
                              class="cancel-button"
                              type="button"
                              onclick="openConfirmDialog(this.form, '${removeConfirmationMessage}', '${secondaryText}', '${removeText}', '${cancelRemoveText}')"
                            >
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
      </c:if>
    </div>
    <%@include file="components/confirmDialog.jsp" %>
    <script src="<c:url value='/js/turnConfirmationModal.js'/>"></script>
  </body>
</html>