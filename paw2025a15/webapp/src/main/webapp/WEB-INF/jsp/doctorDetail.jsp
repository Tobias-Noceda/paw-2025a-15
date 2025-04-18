<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/doctor-detail.css"/>">
  </head>
  <body>
    <jsp:include page="header.jsp"/>
    <c:if test="${takeTurnForm.name != null}">
      <c:set var="myLocalDate">
        <spring:message code="dateFormat" arguments="${takeTurnForm.monthDate},${takeTurnForm.month},${takeTurnForm.year}"></spring:message>
      </c:set>
      <c:set var="myTime" value="${takeTurnForm.timeRange}"/>
      <script>
        document.addEventListener("DOMContentLoaded", function() {
          document.getElementById("dateSpan").innerText = "${myLocalDate}";
          document.getElementById("timeSpan").innerText = "${myTime}";

          document.getElementById("dialogLocalDate").value = "${takeTurnForm.date}";
          document.getElementById("dialogTime").value = "${myTime}";

          document.getElementById("appointmentDialog").showModal();
        });
      </script>
    </c:if>
    <div class="page-container" style="flex-direction: row;">
      <div class="doctor-card">
        <div class="doctor-info">
          <h2 class="doctor-name"><c:out value="${doctor.name}"/></h2>
          <div class="doctor-image">
            <img src="<c:url value='/supersecret/files/${doctor.pictureId}'/>" alt="Doctor Image" />
          </div>
          <p class="doctor-email"><c:out value="${doctor.email}"/></p>
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
            <c:forEach var="appointment" items="${doctorAppointments}">
              <c:set var="day">
                <fmt:formatNumber value="${appointment.date.dayOfMonth}" pattern="00" />
              </c:set>
              <c:set var="month">
                <fmt:formatNumber value="${appointment.date.monthValue}" pattern="00" />
              </c:set>
              <c:set var="year" value="${appointment.date.year}" />
              <tr class="appointment-row"
                data-shift="${appointment.shiftId}"
                data-localDate="${appointment.date}"
                data-formattedDate='<spring:message code="dateFormat" arguments="${day},${month},${year}" htmlEscape="true"></spring:message>'
                data-time="${appointment.getStartToEndTime()}">
                <td>
                  <spring:message code="dateFormat" arguments="${day},${month},${year}" htmlEscape="true"></spring:message>
                </td>
                <td><c:out value="${appointment.getStartToEndTime()}"/></td>
                <td><c:out value="${appointment.address}"/></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Dialog Element -->
    <dialog id="appointmentDialog">
      <div class="dialog-content">
        <h2><spring:message code="doctorDetail.popup.title"></spring:message></h2>

        <div class="appointment-data">
          <p class="appointment-data-label">
            <spring:message code="doctorDetail.popup.date"></spring:message>
          </p>
          <span id="dateSpan"></span>
        </div>
        <div class="appointment-data">
          <p class="appointment-data-label"><spring:message code="doctorDetail.popup.time"></spring:message></p>
          <span id="timeSpan"></span>
        </div>

        <c:url value="/doctors/${doctor.id}" var="postPath"/>
        <form:form modelAttribute="takeTurnForm" method="POST" action="${postPath}">
          <!-- Hidden Fields -->
          <form:input type="hidden" path="shiftId" id="dialogShiftId"/>
          <form:input type="hidden" path="date" id="dialogLocalDate"/>
          <form:input type="hidden" path="timeRange" id="dialogTime"/>

          <!-- Name -->
          <div class="form-column">
            <form:errors path="name" cssClass="form-error"/>  
            <div class="form-group">
              <form:label path="name"><spring:message code="doctorDetail.popup.name"></spring:message></form:label>
              <form:input type="text" path="name"/>
            </div>
          </div>

          <!-- Surname -->
          <div class="form-column">
            <form:errors path="surname" cssClass="form-error"/>
            <div class="form-group">
              <form:label path="surname"><spring:message code="doctorDetail.popup.surname"></spring:message></form:label>
              <form:input type="text" path="surname"/>
            </div>
          </div>

          <!-- Email -->
          <div class="form-column">
            <form:errors path="email" cssClass="form-error"/>
            <div class="form-group">
              <form:label path="email"><spring:message code="doctorDetail.popup.email"></spring:message></form:label>
              <form:input type="text" path="email"/>
            </div>
          </div>

          <!-- Phone -->
          <div class="form-column">
            <form:errors path="phoneNumber" cssClass="form-error"/>
            <div class="form-group">
              <form:label path="phoneNumber"><spring:message code="doctorDetail.popup.telephone"></spring:message></form:label>
              <form:input type="tel" path="phoneNumber"/>
            </div>
          </div>

          <div class="form-buttons">
            <button type="submit"><spring:message code="doctorDetail.popup.takeAppointment"></spring:message></button>
            <button type="button" id="closeDialog"><spring:message code="doctorDetail.popup.cancelAppointment"></spring:message></button>
          </div>
        </form:form>
      </div>
    </dialog>

    <script src="<c:url value="/js/turnConfirmationModal.js"/>"></script>
  </body>
</html>