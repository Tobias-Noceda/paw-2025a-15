<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
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
      <c:set var="dialogDate" value="${takeTurnForm.date}"/>
      <c:set var="dialogTime" value="${takeTurnForm.timeRange}"/>
      <c:set var="dialogIdx" value="${takeTurnForm.index}"/>
      <c:set var="dialogShiftId" value="${takeTurnForm.shiftId}"/>
      <script>
        document.addEventListener("DOMContentLoaded", function() {
          // Set the values in the dialog
          document.getElementById("dateSpan").innerText = "${dialogDate}";
          document.getElementById("timeSpan").innerText = "${dialogTime}";

          document.getElementById("appointmentDialog").showModal();
        });
      </script>
    </c:if>
    <div class="page-container" style="flex-direction: row;">
      <div class="doctor-card">
        <div class="doctor-info">
          <h2 class="doctor-name"><c:out value="${doctor.name}"/></h2>
          <div class="doctor-image">
            <img src="<c:url value='/resources/avatar.jpg'/>" alt="Doctor Image" />
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
            <p class="doctor-specialty"><c:out value="${doctorDetail.specialty}"/></p>
          </div>
          <p class="doctor-schedule-title"><spring:message code="doctor.details.schedule.label"/></p>
          <div class="doctor-schedule">
            <ul>
              <c:forEach var="schedule" items="${doctorShifts}">
                <c:set var="myWeekday">
                  <spring:message code="weekday.${schedule.weekday.name}"/>
                </c:set>
                <li>
                  <spring:message code="doctor.details.schedule" arguments="${myWeekday}, ${schedule.getStartTime()}, ${schedule.getEndTime()}, ${schedule.address}"/>
                </li>
              </c:forEach>
            </ul>
          </div>
        </div>
      </div>
      <div class="shifts-list-container">
        <table class="appointments-table">
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Hora</th>
              <th>Dirección</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="appointment" items="${doctorAppointments}">
              <tr class="appointment-row"
                data-shift="${appointment.shiftId}"
                data-index="${appointment.index}"
                data-date="${appointment.date}"
                data-time="${appointment.getStartToEndTime()}">
                <td>${appointment.date}</td>
                <td>${appointment.getStartToEndTime()}</td>
                <td>${appointment.address}</td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Dialog Element -->
    <dialog id="appointmentDialog">
      <div class="dialog-content">
        <h2>Reservar Turno</h2>

        <div class="appointment-data"><p class="appointment-data-label">Fecha:</p> <span id="dateSpan"></span></div>
        <div class="appointment-data"><p class="appointment-data-label">Hora:</p> <span id="timeSpan"></span></div>

        <c:url value="/doctors/${doctorId}" var="postPath"/>
        <form:form modelAttribute="takeTurnForm" method="POST" action="${postPath}">
          <!-- Hidden Fields -->
          <form:input type="hidden" path="shiftId" id="dialogShiftId"/>
          <form:input type="hidden" path="index" id="dialogIdx"/>
          <form:input type="hidden" path="date" id="dialogDate"/>
          <form:input type="hidden" path="timeRange" id="dialogTime"/>

          <!-- Name -->
          <div class="form-group">
            <form:label path="name">Nombre:</form:label>
            <form:input type="text" path="name"/>
          </div>
          <form:errors path="name" cssClass="error-message"/>

          <!-- Surname -->
          <div class="form-group">
            <form:label path="surname">Apellido:</form:label>
            <form:input type="text" path="surname"/>
          </div>
          <form:errors path="surname" cssClass="error-message"/>

          <!-- Email -->
          <div class="form-group">
            <form:label path="email">Correo Electrónico:</form:label>
            <form:input type="text" path="email"/>
          </div>
          <form:errors path="email" cssClass="error-message"/>

          <!-- Phone -->
          <div class="form-group">
            <form:label path="phoneNumber">Teléfono:</form:label>
            <form:input type="tel" path="phoneNumber"/>
          </div>
          <form:errors path="phoneNumber" cssClass="error-message"/>

          <div class="form-buttons">
            <button type="submit">Reservar</button>
            <button type="button" id="closeDialog">Cancelar</button>
          </div>
        </form:form>
      </div>
    </dialog>

    <script src="<c:url value="/js/turnConfirmationModal.js"/>"></script>
  </body>
</html>