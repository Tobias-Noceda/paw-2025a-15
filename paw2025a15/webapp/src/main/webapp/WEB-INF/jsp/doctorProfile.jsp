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
    <div class="page-container" style="display: flex; flex-direction: row;">
        <div class="doctor-card">
            <div class="doctor-info">
              <h2 class="doctor-name"><c:out value="${doctor.name}"/></h2>
              <div class="doctor-image">
                <img src="<c:url value='/resources/avatar.jpg'/>" alt="Doctor Image" />
              </div>
              <p class="doctor-email"><c:out value="${doctor.email}"/></p>
              <div class="doctor-insurances-div">
                <p class="doctor-insurances">
                  <c:if test="${doctorInsurances != null}">
                    <p class="doctor-insurances-label"><spring:message code="doctor.profile.insurance.label"/></p>
                    <c:forEach var="insurance" items="${doctorInsurances}" varStatus="status">
                        <c:out value="${insurance.name}"/><c:if test="${!status.last}">, </c:if>
                    </c:forEach>
                  </c:if>
                </p>
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
                  <th>Paciente</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="appointment" items="${doctorFutureAppointments}">
                  <tr class="appointment-row">
                    <td><c:out value="${appointment.date}"/></td>
                    <td><c:out value="${appointment.getStartToEndTime()}"/></td>
                    <td><c:out value="${appointment.address}"/></td>
                    <td><c:out value="${appointment.patientName}"/></td>
                    <td>
                      <form action="/doctorCancelAppointment/${doctor.id}/${appointment.shiftId}/${appointment.date}" method="post" onsubmit="return confirm('¿Estás seguro que quieres cancelar esta cita?');">
                        <button type="submit">Cancelar</button>
                      </form>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
            <table class="appointments-table">
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Hora</th>
                  <th>Dirección</th>
                  <th>Paciente</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="appointment" items="${doctorOldAppointments}">
                  <tr class="appointment-row">
                    <td><c:out value="${appointment.date}"/></td>
                    <td><c:out value="${appointment.getStartToEndTime()}"/></td>
                    <td><c:out value="${appointment.address}"/></td>
                    <td><c:out value="${appointment.patientName}"/></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
        </div>
    </div>
  </body>
</html>