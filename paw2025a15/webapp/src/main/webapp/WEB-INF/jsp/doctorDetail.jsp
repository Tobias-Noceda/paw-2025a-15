<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="/css/base.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/doctor-detail.css"/>">
  </head>
  <body>
    <jsp:include page="components/header.jsp">
      <jsp:param name="username" value="${user.name}"/>
      <jsp:param name="pictureId" value="${user.pictureId}"/>
      <jsp:param name="role" value="${user.role}"/>
    </jsp:include>
    <div class="page-container" style="flex-direction: row;">
      <div class="doctor-detail-card">
        <div class="doctor-info">
          <c:url value="/patientAuthDoctor/${doctor.id}" var="authDoctorPath"/>
          <c:set var="confirmationText">
            <spring:message code='doctorDetail.authorize.confirm'/>
          </c:set>
          <c:set var="authCancelText">
            <spring:message code='doctorDetail.authorize.cancelButton'/>
          </c:set>
          <div class="doctor-infopack">
            <h2 class="doctor-name"><c:out value="${doctor.name}"/></h2>
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
          <hr style="border: 1px solid #ccc; margin: 20px 0;" /><!--TODO: ver siesta bien esto con el estilo aca-->
          <form id="authDoctorForm" action="${authDoctorPath}" method="POST">
            <c:if test="${!isAuthDoctor}">
              <div class="doctor-name-div">
                <c:set var="buttonText">
                  <spring:message code='doctorDetail.toggleButton.authorize'/>
                </c:set>
                <button 
                  type="button" 
                  name="action"
                  value="toggle" 
                  onclick="confirmAuthDoctor('${confirmationText}', null, '${buttonText}', '${authCancelText}', this.name, this.value)" 
                  class="${isAuthDoctor ? 'doctor-auth-button auth' : 'doctor-auth-button'}"
                >
                  <c:out value="${buttonText}"/>
                </button>
              </div>
            </c:if>
            <c:if test="${isAuthDoctor}">
              <div class="doctor-name-div">
                <c:set var="buttonText">
                  <spring:message code='doctorDetail.toggleButton.deauthorize'/>
                </c:set>
                <button 
                  type="button" 
                  name="action"
                  value="toggle" 
                  onclick="confirmAuthDoctor('${confirmationText}', null, '${buttonText}', '${authCancelText}', this.name, this.value)" 
                  class="${isAuthDoctor ? 'doctor-auth-button auth' : 'doctor-auth-button'}"
                >
                  <c:out value="${buttonText}"/>
                </button>
              </div>
              <hr style="border: 1px solid #ccc; margin: 10px 0;" />
              <h2 class="doctor-name"><spring:message code='doctorDetail.update.currentPermits'/></h2>
              <div class="doctor-name-div">
                <c:forEach var="access" items="${['VIEW_MEDICAL','VIEW_SOCIAL','VIEW_HABITS']}">
                  <c:set var="idSafe" value="${fn:toLowerCase(fn:replace(access, '_', '-'))}" />
                  <div class="access-btn">
                    <input
                      class="access-checkbox"
                      type="checkbox"
                      id="access-${idSafe}"
                      name="accessLevels"
                      value="${access}"
                      <c:if test="${isAuthDoctor && allowedAccessLevels.contains(access)}">checked</c:if>
                    />
                    <label for="access-${idSafe}" class="access-label">
                      <spring:message code="doctorDetail.update.${fn:toLowerCase(fn:substringAfter(access, 'VIEW_'))}Access"/>
                    </label>
                  </div>
                </c:forEach>
              </div>
              <c:set var="buttonTextUpdate">
                <spring:message code='doctorDetail.update.confirmButton'/>
              </c:set>
              <c:set var="authCancelTextUpdate">
                <spring:message code='doctorDetail.update.cancelButton'/>
              </c:set>
              <c:set var="confirmationTextUpdate">
                <spring:message code='doctorDetail.update.confirm'/>
              </c:set>
              <spring:message code="doctorDetail.update.confirm.habits" var="msgHabits"/>
              <spring:message code="doctorDetail.update.confirm.medical" var="msgMedical"/>
              <spring:message code="doctorDetail.update.confirm.social" var="msgSocial"/>
              <spring:message code="doctorDetail.update.confirm.extra" var="msgExtra"/>
              <c:set var="confirmationSecondaryTextUpdate">
                <ul><li>${msgHabits}</li><li>${msgMedical}</li><li>${msgSocial}</li></ul>${msgExtra}    
              </c:set>
              <div style="display: flex; justify-content: right; flex: 1;">
                <button 
                  type="button" 
                  name="action" 
                  value="update" 
                  onclick="confirmAuthDoctor('${confirmationTextUpdate}', '${confirmationSecondaryTextUpdate}', '${buttonTextUpdate}', '${authCancelTextUpdate}', this.name, this.value)" 
                  class="doctor-update-button"
                >
                  <spring:message code='doctorDetail.update.updateButton'/>
                </button>
              </div>
            </c:if>
          </form>

        </div>
      </div>
      <div class="shifts-list-container">
        <c:url value="/doctors/${doctor.id}" var="getPath"/>
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
        <table class="appointments-table">
          <thead>
            <tr>
              <th><spring:message code="appointmentTable.weekdayColumn.title"></spring:message></th>
              <th><spring:message code="appointmentTable.monthdayColumn.title"></spring:message></th>
              <th><spring:message code="appointmentTable.timeColumn.title"></spring:message></th>
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
              <c:set var="formattedDate">
                <spring:message code="dateFormat" arguments="${day},${month},${year}" htmlEscape="true"></spring:message>
              </c:set>
              <c:set var="confirmMessage">
                <spring:message code="takeAppointment.confirmationMessage" arguments="${formattedDate}, ${appointment.startTime}, ${doctor.name}"/>
              </c:set>
              <c:set var="secondText">
                <spring:message code="doctorDetail.confirmApp.secondText" arguments="${doctor.name}"/>
              </c:set>
              <c:set var="cancelText">
                <spring:message code="doctorDetail.confirmApp.cancel"/>
              </c:set>
              <c:set var="confirmText">
                <spring:message code="doctorDetail.confirmApp.confirm"/>
              </c:set>
              <tr class="appointment-row" onclick="submitAppointment(this, '${confirmMessage}', '${secondText}', '${confirmText}', '${cancelText}')">
                <td><spring:message code="weekday.${appointment.date.dayOfWeek}"></spring:message></td>
                <td><c:out value="${appointment.date.dayOfMonth}" escapeXml="true"/></td>
                <td><c:out value="${appointment.getStartToEndTime()}" escapeXml="true"/></td>
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

    <%@include file="components/confirmDialog.jsp" %>

    <script src="<c:url value='/js/turnConfirmationModal.js'/>"></script>
    <script src="<c:url value='/js/authConfirmationModal.js'/>"></script>
  </body>
</html>