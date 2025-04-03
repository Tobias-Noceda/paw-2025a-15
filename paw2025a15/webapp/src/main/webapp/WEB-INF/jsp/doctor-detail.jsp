<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/doctor-detail.css"/>">
  </head>
  <body>
    <jsp:include page="header.jsp"/>
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
                data-date="${appointment.date}">
                <td>${appointment.date}</td>
                <td>${appointment.getStartTime()}</td>
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
          <h2>Detalles del Turno</h2>
          <p><strong>Fecha:</strong> <span id="dialogDate"></span></p>
          <p><strong>Indice</strong> <span id="dialogIdx"></span></p>
          <p><strong>Nro. Shift</strong> <span id="dialogShiftId"></span></p>
          <button id="closeDialog">Cerrar</button>
      </div>
    </dialog>

    <script>
      document.addEventListener("DOMContentLoaded", function () {
          const rows = document.querySelectorAll(".appointment-row");
          const dialog = document.getElementById("appointmentDialog");
          const closeDialog = document.getElementById("closeDialog");
      
          rows.forEach(row => {
              row.addEventListener("click", function () {
                  document.getElementById("dialogDate").innerText = this.dataset.date;
                  document.getElementById("dialogIdx").innerText = this.dataset.index;
                  document.getElementById("dialogShiftId").innerText = this.dataset.shift;
                  
                  dialog.showModal();
              });
          });
      
          closeDialog.addEventListener("click", function () {
              dialog.close();
          });
      });
      </script>
  </body>
</html>