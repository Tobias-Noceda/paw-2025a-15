<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="/favicon.ico"/>" />
    <link rel="stylesheet" href="<c:url value="/css/base.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/doctor-detail.css"/>">
    <link rel="stylesheet" href="<c:url value='/css/appointments.css'/>">
  </head>
  <body>
    <jsp:include page="components/header.jsp"/>
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
          <div class="">
            <h2 class="doctor-name"><c:out value="${doctor.name}" escapeXml="true"/></h2>
            <div class="doctor-infopack">
              <div class="doctor-image">
                <img 
                  src="<c:url value='/supersecret/user-profile-pic/${doctor.id}'/>"
                  alt="<spring:message code='image.alt.doctorProfile'/>"
                />
              </div>
            </div>
            <p class="doctor-email"><c:out value="${doctor.email}" escapeXml="true"/></p>


            <div class="doctor-telephone-div">
              <p class="doctor-telephone-label"><spring:message code="patient.details.telephone.label"/></p>
              <p class="doctor-telephone"><c:out value="${doctor.telephone}" escapeXml="true"/></p>
            </div>

            <div class="doctor-insurances-div">
              <p class="doctor-insurances-label"><spring:message code="doctor.details.insurances.label"/></p>
              <p class="doctor-insurances">
                <c:forEach var="insurance" items="${doctor.insuranceNames}" varStatus="status">
                  <c:out value="${insurance}" escapeXml="true"/><c:if test="${!status.last}">, </c:if>
                </c:forEach>
              </p>
            </div>
            <div class="doctor-specialty-div">
              <p class="doctor-specialty-label"><spring:message code="doctor.details.specialty.label"/></p>
              <p class="doctor-specialty"><spring:message code="specialty.${doctor.specialty}"/></p>
            </div>
            <div class="doctor-address-div">
              <p class="doctor-address-label"><spring:message code="doctor.details.address.label"/></p>
              <p class="doctor-address"><c:out value="${doctor.singleShifts[0].address}" escapeXml="true"/></p>
            </div>
            <div class="doctor-license-div">
              <p class="doctor-license-label"><spring:message code="doctor.details.license.label"/></p>
              <p class="doctor-license"><c:out value="${doctor.licence}" escapeXml="true"/></p>
            </div>
            <p class="section-title"><spring:message code="doctor.details.schedule.label"/></p>
            <div class="doctor-schedule">
              <ul>
                <c:forEach var="schedule" items="${doctor.singleShifts}">
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
          <hr style="border: 1px solid #ccc; margin: 20px 0;" />
          <form id="authDoctorForm" action="${authDoctorPath}" method="POST">
            <c:if test="${!isAuthDoctor}">
              <div class="doctor-name-div">
                <c:set var="buttonText">
                  <spring:message code='doctorDetail.toggleButton.authorize'/>
                </c:set>

                <div class="action-buttons">
                  <button
                    type="button"
                    name="action"
                    value="toggle"
                    onclick="confirmAuthDoctor('${confirmationText}', null, '${buttonText}', '${authCancelText}', this.name, this.value)"
                    class="${isAuthDoctor ? 'doctor-auth-button auth' : 'doctor-update-button'}"
                  >
                    <c:out value="${buttonText}" escapeXml="true"/>
                  </button>
                </div>
              </div>
            </c:if>
            <c:if test="${isAuthDoctor}">
              <div class="doctor-name-div">
                <c:set var="buttonText">
                  <spring:message code='doctorDetail.toggleButton.deauthorize'/>
                </c:set>

              </div>
              <p class="section-title"><spring:message code='doctorDetail.update.currentPermits'/></p>
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
              <div class="action-buttons">

                <button 
                  type="button" 
                  name="action" 
                  value="update" 
                  onclick="confirmAuthDoctor('${confirmationTextUpdate}', '${confirmationSecondaryTextUpdate}', '${buttonTextUpdate}', '${authCancelTextUpdate}', this.name, this.value)" 
                  class="doctor-update-button"
                >
                  <spring:message code='doctorDetail.update.updateButton'/>
                </button>

                <button
                  type="button"
                  name="action"
                  value="toggle"
                  onclick="confirmAuthDoctor('${confirmationText}', null, '${buttonText}', '${authCancelText}', this.name, this.value)"
                  class="${isAuthDoctor ? 'doctor-auth-button auth' : 'doctor-update-button'}"
                >
                  <c:out value="${buttonText}" escapeXml="true"/>
                </button>
              </div>
            </c:if>
          </form>
        </div>
      </div>
      <div class="appointment-list-container" style="max-height: fit-content; position: sticky; top: 87px;">
        <c:url value="/doctors/${doctor.id}" var="getPath"/>
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
              <form:input cssClass="input-field" id="dateSelector" path="date" type="date" min="${today}" onchange="submitFormWithAction('setDate')"/>
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

        <c:if test="${not empty takeTurnErrors}">
          <div class="error-box">
            <spring:message code="doctor.details.takenTurn"></spring:message>
          </div>
        </c:if>

        <div class="appointment-table-container">
          <div class="appointments-table-header">
            <table class="appointments-table">
              <thead>
                <tr>
                  <th><spring:message code="appointmentTable.weekdayColumn.title"></spring:message></th>
                  <th><spring:message code="appointmentTable.timeColumn.title"></spring:message></th>
                </tr>
              </thead>
            </table>
          </div>
          <c:if test="${not empty doctorAppointments}">
            <div class="appointments-table-body">
              <table class="appointments-table">
                <tbody>
                  <c:url value="/takeAppointment" var="appointmentPath"/>
                  <c:forEach var="appointment" items="${doctorAppointments}">
                    <!-- Format the date -->
                    <c:set var="formattedDay">
                      <fmt:formatNumber value="${date.dayOfMonth}" pattern="00" />
                    </c:set>
                    <c:set var="monthName">
                      <spring:message code="month.${date.month}" />
                    </c:set>

                    <c:set var="confirmMessage">
                      <spring:message code="takeAppointment.confirmationMessage" arguments="${formattedDay}, ${monthName}, ${appointment.startTime}, ${doctor.name}" htmlEscape="true"/>
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

                    <tr
                      onclick="submitAppointment(this, '${confirmMessage}', '${secondText}', '${confirmText}', '${cancelText}')"
                      class="appointment-row"
                    >
                      <td class="sticky-column"><spring:message code="weekday.${date.dayOfWeek}"/></td>
                      <!--<td class="sticky-column"><c:out value="${formattedDay}" escapeXml="true"/></td>-->
                      <td><c:out value="${appointment.getStartToEndTime()}" escapeXml="true"/></td>
                      <td style="display: none;">
                        <form:form modelAttribute="takeTurnForm" action="${appointmentPath}" method="POST">
                          <form:input type="hidden" path="shiftId" value="${appointment.shiftId}"/>
                          <form:input type="hidden" path="date" value="${date}"/>
                          <form:input type="hidden" path="doctorId" value="${doctor.id}"/>
                          <form:hidden path="startTime" value="${appointment.startTime}" />
                          <form:hidden path="endTime" value="${appointment.endTime}" />
                        </form:form>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:if>
          <c:if test="${empty doctorAppointments}">
            <div class="no-appointments-container">
              <h4 class="no-appointments-text"><spring:message code="appointments.toTake.empty"/></h4>
            </div>
          </c:if>
        </div>
      </div>
    </div>

    <%@include file="components/confirmDialog.jsp" %>

    <script src="<c:url value='/js/turnConfirmationModal.js'/>"></script>
    <script src="<c:url value='/js/authConfirmationModal.js'/>"></script>
    <script src="<c:url value='/js/buttonControl.js'/>"></script>
    <script src="<c:url value='/js/doctorDetailNav.js'/>"></script>

  </body>
</html>