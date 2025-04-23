<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/appointments.css"/>">
  </head>
  <body>
    <c:set var="title">appointments</c:set>
    <jsp:include page="components/header.jsp">
      <jsp:param name="title" value="${title}"/>
      <jsp:param name="username" value="${user.name}"/>
      <jsp:param name="pictureId" value="${user.pictureId}"/>
      <jsp:param name="role" value="${user.role}"/>
    </jsp:include>
    <div class="page-container appointments-div" style="display: flex; flex-direction: row;">
      <c:set var="confirmationMessage">
        <spring:message code="appointments.cancelConfirm" />
      </c:set>
      <c:if test="${user.role == 'PATIENT'}">
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
                      <tr class="appointment-row">
                        <td class="text-cell"><c:out value="${appointment.doctorName}"/></td>
                        <td class="text-cell"><c:out value="${appointment.date}"/></td>
                        <td class="text-cell"><c:out value="${appointment.getStartToEndTime()}"/></td>
                        <td class="text-cell"><c:out value="${appointment.address}"/></td>
                        <td class="cancel-cell">
                          <c:url var="cancelUrl" value="/patientCancelAppointment/${user.id}/${appointment.shiftId}/${appointment.date}" />
                          <form action="${cancelUrl}" method="post" onsubmit="return openConfirmDialog(this, '${confirmationMessage}', event)">
                            <button class="cancel-button" type="submit">
                              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                                <path d="M3 6h18v2H3V6zm2 3h14l-1.5 13h-11L5 9zm5 2v8h2v-8H10zm4 0v8h2v-8h-2zM9 4V3h6v1h5v2H4V4h5z" />
                              </svg>
                            </button>
                          </form>

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
                      <tr class="appointment-row">
                        <td class="text-cell"><c:out value="${appointment.doctorName}"/></td>
                        <td class="text-cell"><c:out value="${appointment.date}"/></td>
                        <td class="text-cell"><c:out value="${appointment.getStartToEndTime()}"/></td>
                        <td class="text-cell"><c:out value="${appointment.address}"/></td>
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
      <c:if test="${user.role == 'DOCTOR'}">
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
                      <tr class="appointment-row">
                        <td class="text-cell"><c:out value="${appointment.patientName}"/></td>
                        <td class="text-cell"><c:out value="${appointment.date}"/></td>
                        <td class="text-cell"><c:out value="${appointment.getStartToEndTime()}"/></td>
                        <td class="cancel-cell">
                          <c:url var="doctorCancelUrl" value="/doctorCancelAppointment/${user.id}/${appointment.shiftId}/${appointment.date}" />
                          <form
                                  action="${doctorCancelUrl}"
                                  method="post"
                                  onsubmit="return openConfirmDialog(this, '${confirmationMessage}', event)"
                          >

                          <button class="cancel-button" type="submit">
                              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                                <path d="M3 6h18v2H3V6zm2 3h14l-1.5 13h-11L5 9zm5 2v8h2v-8H10zm4 0v8h2v-8h-2zM9 4V3h6v1h5v2H4V4h5z"/>
                              </svg>
                            </button>
                          </form>
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
            <form:form class="month-dropdown-div" action="${getPath}" method="GET" modelAttribute="shiftsMonthForm">
              <form:select cssClass="month-dropdown" path="month" id="monthSelect" onchange="this.form.submit()">
                <form:options cssClass="dropdown-options" items="${possibleMonths}" itemValue="value" itemLabel="label" />
              </form:select>
            </form:form>
          </div>
          <div class="appointment-table-container">
            <div class="appointments-table-header">
              <table class="appointments-table">
                <thead>
                  <tr>
                    <th><spring:message code="appointmentTable.dateColumn.title"></spring:message></th>
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
                        <td class="text-cell"><c:out value="${appointment.date}"/></td>
                        <td class="text-cell"><c:out value="${appointment.getStartToEndTime()}"/></td>
                        <td class="cancel-cell">
                          <form
                            action="/takeAppointment/${user.id}/${appointment.shiftId}/${appointments.date}" method="post"
                            onsubmit="return openConfirmDialog(this, '${confirmationMessage}', event)"
                          >
                            <button class="cancel-button" type="submit">
                              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" viewBox="0 0 24 24">
                                <path d="M3 6h18v2H3V6zm2 3h14l-1.5 13h-11L5 9zm5 2v8h2v-8H10zm4 0v8h2v-8h-2zM9 4V3h6v1h5v2H4V4h5z"/>
                              </svg>
                            </button>
                          </form>
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
  </body>
</html>