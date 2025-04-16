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
              <h2 class="doctor-name"><c:out value="${patient.name}"/></h2>
              <div class="doctor-image">
                <img src="<c:url value='/resources/avatar.jpg'/>" alt="Patient Image" />
              </div>
              <p class="doctor-email"><c:out value="${patient.email}"/></p>
              <div class="doctor-insurances-div">
                <p class="doctor-insurances">
                  <c:if test="${patientInsurance != null}">
                      <p class="doctor-insurances-label"><spring:message code="patient.profile.insurance.label"/></p>
                      <c:out value="${insurance.name}"/>
                  </c:if>
                </p>
              </div>
            </div>
        </div>
        <div class="shifts-list-container">
            <table class="appointments-table">
              <thead>
                <tr>
                  <th>Medico</th>
                  <th>Fecha</th>
                  <th>Hora</th>
                  <th>Dirección</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="appointment" items="${patientFutureAppointments}">
                  <tr class="appointment-row">
                    <td><c:out value="${appointment.doctorName}"/></td>
                    <td><c:out value="${appointment.date}"/></td>
                    <td><c:out value="${appointment.getStartToEndTime()}"/></td>
                    <td><c:out value="${appointment.address}"/></td>
                    <td>
                      <form action="/patientCancelAppointment/${patient.id}/${appointment.shiftId}/${appointment.date}" method="post" onsubmit="return confirm('¿Estás seguro que quieres cancelar esta cita?');">
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
                  <th>Medico</th>
                  <th>Fecha</th>
                  <th>Hora</th>
                  <th>Dirección</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="appointment" items="${patientOldAppointments}">
                  <tr class="appointment-row">
                    <td><c:out value="${appointment.doctorName}"/></td>
                    <td><c:out value="${appointment.date}"/></td>
                    <td><c:out value="${appointment.getStartToEndTime()}"/></td>
                    <td><c:out value="${appointment.address}"/></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
            <table class="appointments-table">
              <thead>
                <tr>
                  <th>Tipo de estudio</th>
                  <th>Fecha de subida</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="study" items="${patientStudies}">
                  <tr class="appointment-row" data-fileid="<c:out value='${study.fileId}'/>">
                    <td><c:out value="${study.type}"/></td>
                    <td><c:out value="${study.uploadDate}"/></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
        </div>
    </div>

    <dialog id="fileDialog" class="file-dialog">
      <button id="closeFileDialog" class="close-button">Cerrar</button>
      <iframe id="fileViewer" src="" frameborder="0" style="width: 100%; height: 80vh;"></iframe>
    </dialog>
    <script src="<c:url value="/js/turnConfirmationModal.js"/>"></script>
  </body>
</html>