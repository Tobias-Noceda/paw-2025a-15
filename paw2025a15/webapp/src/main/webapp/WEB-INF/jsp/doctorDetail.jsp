<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/doctor-detail.css"/>">
  </head>
  <body>
    <jsp:include page="components/header.jsp">
      <jsp:param name="username" value="${user.name}"/>
      <jsp:param name="pictureId" value="${user.pictureId}"/>
      <jsp:param name="role" value="${user.role}"/>
    </jsp:include>
    <div class="page-container" style="flex-direction: row;">
      <div class="doctor-card">
        <div class="doctor-info">
          <div class="doctor-name-div">
            <h2 class="doctor-name"><c:out value="${doctor.name}"/></h2>
            <c:url value="/patientAuthDoctor/${doctor.id}" var="authDoctorPath"/><!--TODO:front and inter here, this is placeholder with no inter nor style-->
            <form action="${authDoctorPath}" method="POST">
              <c:if test="${isAuthDoctor}">
              <label>
                <input type="checkbox" name="accessLevels" value="VIEW_BASIC" <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_BASIC')}">checked</c:if>/>
                  Basic Access
              </label>
              <label>
                  <input type="checkbox" name="accessLevels" value="VIEW_MEDICAL" <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_MEDICAL')}">checked</c:if>/>
                  Medical Access
              </label>
              <label>
                  <input type="checkbox" name="accessLevels" value="VIEW_LIFESTYLE" <c:if test="${isAuthDoctor && allowedAccessLevels.contains('VIEW_LIFESTYLE')}">checked</c:if>/>
                  Lifestyle Access
              </label>
              <button type="submit" name="action" value="update" class="doctor-auth-button">
                  Update
              </button>
            </c:if>
              <button type="submit" name="action" value="toggle" class="doctor-auth-button <c:if test="${isAuthDoctor}">auth</c:if>">
                <c:if test="${isAuthDoctor}">
                  <spring:message code="doctorDetail.toggleButton.deauthorize"/>
                </c:if>
                <c:if test="${!isAuthDoctor}">
                  <spring:message code="doctorDetail.toggleButton.authorize"/>
                </c:if>
              </button>
            </form>
          </div>
          <div class="doctor-image">
            <img src="<c:url value='/supersecret/files/${doctor.pictureId}'/>" alt="Doctor Image" />
          </div>
          <p class="doctor-email"><c:out value="${doctor.email}"/>, <c:out value="${doctor.telephone}"/></p>
          <div class="doctor-insurances-div">
            <p class="doctor-insurances-label"><spring:message code="doctor.details.insurances.label"/></p>
            <p class="doctor-insurances">
              <c:forEach var="insurance" items="${doctorInsurances}" varStatus="status">
                <c:out value="${insurance.name}"/><c:if test="${!status.last}">, </c:if>
              </c:forEach>
            </p>
          </div>
          <div class="doctor-specialty-div">
            <p class="doctor-specialty-label"><spring:message code="doctor.details.specialty.label"/></p>
            <p class="doctor-specialty"><spring:message code="specialty.${doctorDetail.specialty}"/></p>
          </div>
          <div class="doctor-address-div">
            <p class="doctor-address-label"><spring:message code="doctor.details.address.label"/></p>
            <p class="doctor-address"><c:out value="${doctorShifts[0].address}"/></p>
          </div>
          <div class="doctor-license-div">
            <p class="doctor-license-label"><spring:message code="doctor.details.license.label"/></p>
            <p class="doctor-license"><c:out value="${doctorDetail.licence}"/></p>
          </div>
          <p class="doctor-schedule-title"><spring:message code="doctor.details.schedule.label"/></p>
          <div class="doctor-schedule">
            <ul>
              <c:forEach var="schedule" items="${doctorShifts}">
                <c:set var="myWeekday">
                  <spring:message code="weekday.${schedule.weekday.name}"/>
                </c:set>
                <li>
                  <spring:message code="doctor.details.schedule" arguments="${myWeekday}, ${schedule.startTime}, ${schedule.endTime}" htmlEscape="true"/>
                </li>
              </c:forEach>
            </ul>
          </div>
        </div>
      </div>
      <div class="shifts-list-container">
        <c:url value="/doctors/${doctor.id}" var="getPath"/>
        <form:form class="month-dropdown-div" action="${getPath}" method="GET" modelAttribute="shiftsMonthForm">
          <form:select cssClass="month-dropdown" path="month" id="monthSelect" onchange="this.form.submit()">
            <form:options cssClass="dropdown-options" items="${possibleMonths}" itemValue="value" itemLabel="label" />
          </form:select>
        </form:form>
        <table class="appointments-table">
          <thead>
            <tr>
              <th><spring:message code="appointmentTable.dateColumn.title"></spring:message></th>
              <th><spring:message code="appointmentTable.timeColumn.title"></spring:message></th>
              <th><spring:message code="appointmentTable.addressColumn.title"></spring:message></th>
            </tr>
          </thead>
          <tbody>
            <c:url value="/takeAppointment" var="appointmentPath"/>
            <c:forEach var="appointment" items="${doctorAppointments}">
              <c:set var="day">
                <fmt:formatNumber value="${appointment.date.dayOfMonth}" pattern="00" />
              </c:set>
              <c:set var="month">
                <fmt:formatNumber value="${appointment.date.monthValue}" pattern="00" />
              </c:set>
              <c:set var="year" value="${appointment.date.year}" />
              <c:set var="formatedDate">
                <spring:message code="dateFormat" arguments="${day},${month},${year}" htmlEscape="true"></spring:message>
              </c:set>
              <c:set var="confirmMessage">
                <spring:message code="takeAppointment.confirmationMessage" arguments="${formatedDate}, ${appointment.startTime}, ${doctor.name}"/>
              </c:set>
              <tr class="appointment-row" onclick="submitAppointment(this, '${confirmMessage}')">
                <td><c:out value="${formatedDate}"/></td>
                <td><c:out value="${appointment.getStartToEndTime()}"/></td>
                <td><c:out value="${appointment.address}"/></td>
                <td style="display: none;">
                  <form:form
                    modelAttribute="takeTurnForm"
                    action="${appointmentPath}"
                    method="POST"
                  >
                    <form:input type="hidden" path="shiftId" value="${appointment.shiftId}"/>
                    <form:input type="hidden" path="date" value="${appointment.date}"/>
                  </form:form>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>

    <jsp:include page="components/confirmDialog.jsp" />

    <script src="<c:url value='/js/turnConfirmationModal.js'/>"></script>
  </body>
</html>