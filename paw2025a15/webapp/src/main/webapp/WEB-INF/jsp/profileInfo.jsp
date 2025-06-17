<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form"  uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="profile.title"/></title>
    <link rel="icon" type="image/png" href="<c:url value='/favicon.ico'/>" />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/profile-info.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/base.css'/>">
  </head>
  <body>

  <c:if test="${not empty updateSuccessMessage}">
    <div class="toast" id="update-toast">
      <span class="toast-icon">✔️</span>
      <span class="toast-text">
        <c:out value="${updateSuccessMessage}" escapeXml="true"/>
      </span>
      <button class="toast-close" onclick="closeToast()">×</button>
    </div>
  </c:if>

  <jsp:include page="components/header.jsp"/>
  <c:url value='/profile' var="saveProfileUrl"/>
  <div class="page-container">
    <form:form modelAttribute="profileForm"
      action="${saveProfileUrl}"
      method="post"
      enctype="multipart/form-data"
      class="profile-form"
    >
      <!-- Cabecera -->
      <div class="image-header" style="display: flex; flex-direction: column; align-items: left;">
        <div class="card" style="display: flex; flex-direction: column; align-items: left;">
          <div class="profile-header"> 
            <div class="profile-image">
              <div class="edit-image-wrapper">
                <img
                  id="prof-image"
                  src="<c:url value='/supersecret/user-profile-pic/${user_data.id}'/>"
                  alt="<spring:message code='image.alt.userProfile'/>"
                />
                <button class="edit-button" type="button" onclick="handleImageChange()">
                  ✏️
                </button>
                <input type="file"
                  id="fileInput"
                  name="profileImage"
                  class="hidden-file-input"
                  accept=".png, .jpg, .jpeg"
                  onchange="handleImageChange(event)"
                />
              </div>
            </div>
            <div class="profile-info-block">
              <p class="name"><c:out value="${user_data.name}" escapeXml="true"/></p>
              <p class="email"><c:out value="${user_data.email}" escapeXml="true"/></p>
              <p class="role"><spring:message code="profile.role.label"/>: <spring:message code="role.${user_data.role}"/></p>
              <sec:authorize access="hasRole('ROLE_DOCTOR')">
                <p class="role"><spring:message code="doctor.details.license.label"/> <c:out value="${doctorDetail.licence}" escapeXml="true"/></p>
                <p class="role"><spring:message code="doctor.details.specialty.label"/> <spring:message code="specialty.${doctorDetail.specialty}"/></p>
              </sec:authorize>
            </div>
          </div>
          <form:errors path="profileImage" cssClass="error-box" element="div" style="margin-top: 13px;"/>
        </div>
      </div>

      <!-- Sección de datos básicos -->
      <div class="card">
        <h3 class="section-title"><spring:message code="profileInfo.basic"/></h3>
        <div class="field-grid">
          <div class="field-container full-width">
            <form:label path="phoneNumber"><spring:message code="profile.phone.label"/></form:label>
            <form:input
              path="phoneNumber"
              type="text"
              cssClass="input-field"
            />
            <form:errors path="phoneNumber" cssClass="error-box" element="div"/>
          </div>

          <div class="field-container full-width">
            <form:label path="mailLanguage"><spring:message code="profile.email.lang.label"/></form:label>
            <form:select path="mailLanguage" cssClass="input-field filter-select" items="${locales}" itemLabel="label" itemValue="value"/>
          </div>

          <sec:authorize access="hasRole('ROLE_DOCTOR')">
            <div class="field-container full-width">
              <div class="insurance-toggle-group">
                <form:label path="insurances">
                  <spring:message code="doctor.details.insurances.label"/>
                </form:label>
                <c:forEach var="insurance" items="${obrasSocialesItems}">
                  <div class="insurance-btn">
                    <form:checkbox
                      path="insurances"
                      id="insurance-${insurance.id}"
                      value="${insurance.id}"
                      cssClass="insurance-checkbox"/>
                    <label
                      for="insurance-${insurance.id}"
                      class="insurance-label"
                    >
                      <c:out value="${insurance.name}" escapeXml="true"/>
                    </label>
                  </div>
                </c:forEach>
                <form:errors path="insurances" cssClass="error-box" element="div"/>
              </div>
            </div>

            <div class="field-container full-width">
              <label class="field-label"><spring:message code="doctorForm.address"/></label>
              <form:input path="address" type="text" cssClass="login-input"/>
              <form:errors path="address" cssClass="error-box" element="div"/>
            </div>

            <div class="field-container full-width">
              <h3 class="section-subtitle"><spring:message code="doctorProfile.updateSchedule"/></h3>
              <form:checkbox
                path="updateSchedules"
                cssClass="switch-checkbox"
                value="false"
                id="updateScheduleSwitch"
                onclick="setUpdateScheduleFields()"
              />
            
              <div style="display: none; flex-direction: column; align-items: left; max-height: fit-content;" id="new-schedule-fields">
                <h3 class="section-subtitle"><spring:message code="doctorForm.schedule"/></h3>
                <div class="field-container full-width">
                  <label class="field-label"><spring:message code="doctorForm.schedules.weekday"/></label>
                  <div class="weekday-toggle-group">
                    <c:forEach var="day" items="${weekdaySelectItems}">
                      <div class="weekday-btn">
                        <input type="checkbox"
                          id="day-${day.value}"
                          name="schedules.weekday"
                          value="${day.value}"
                          class="weekday-checkbox"
                          <c:forEach var="selected" items="${selectedDays}">
                              <c:if test="${selected eq day.value}">checked</c:if>
                          </c:forEach>
                        />
                        <label for="day-${day.value}" class="weekday-label">
                          <spring:message code="weekday.${day.value}.initial"/>
                        </label>
                      </div>
                    </c:forEach>
                  </div>
                </div>

                <div class="field-container">
                  <label class="field-label"><spring:message code="doctorForm.schedules.startTime"/></label>
                  <form:select
                    path="schedules.startTime"
                    cssClass="input-field filter-select"
                    items="${hoursSelectItems}"
                    itemLabel="label"
                    itemValue="value"
                  />
                </div>

                <div class="field-container">
                  <label class="field-label"><spring:message code="doctorForm.schedules.endTime"/></label>
                  <form:select
                    path="schedules.endTime"
                    cssClass="input-field filter-select"
                    items="${hoursSelectItems}"
                    itemLabel="label"
                    itemValue="value"
                  />
                </div>

                <form:errors path="schedules" cssClass="error-box" element="div"/>

                <!-- Amount field -->
                <div class="field-container">
                  <label class="field-label"><spring:message code="doctorForm.amount"/></label>
                  <form:select path="amount" cssClass="input-field filter-select">
                    <form:option value="15" label="15"/>
                    <form:option value="30" label="30"/>
                    <form:option value="45" label="45"/>
                    <form:option value="60" label="60"/>
                  </form:select>
                  <form:errors path="amount" cssClass="error-box" element="div"/>
                </div>
              </div>
            </div>

            <form:hidden path="keepTurns" id="keepTurnsCheckBox" value="true" checked="true"/>
          </sec:authorize>
        
          <sec:authorize access="hasRole('ROLE_PATIENT')">
            <form:hidden path="insurances" value=""/>
            <form:hidden path="address" value="N/A"/>
            <div class="field-container">
              <form:label path="birthDate"><spring:message code="form.birthDate"/></form:label>
              <form:input
                value="${user_data.birthdate}"
                cssClass="input-field"
                id="birthDate"
                path="birthDate"
                type="date"
              />
              <form:errors
                path="birthDate"
                cssClass="sf-error"
                element="div"
              />
            </div>

            <div class="field-container">
              <form:label path="bloodType"><spring:message code="profileInfo.bloodType"/></form:label>
              <select
                id="patient-blood-type"
                name="bloodType"
                class="input-field filter-select"
              >
                <option value=""><spring:message code="profileInfo.bloodType.select"/></option>
                <c:forEach var="bloodType" items="${bloodTypes}">
                  <option
                    value="${bloodType.name()}"
                    <c:if test="${user_data.bloodType eq bloodType}">selected</c:if>
                  >
                    <c:out value="${bloodType.getName()}" escapeXml="true"/>
                  </option>
                </c:forEach>
              </select>
            </div>

            <div class="field-container">
              <form:label path="height"><spring:message code="profileInfo.height"/></form:label>
              <input
                id="patient-height"
                name="height"
                type="text"
                maxlength="9"
                pattern="^\d{1,6}([.]\d{1,2})?$"
                class="input-field"
                value="${user_data.height}"
                oninput="validateDecimal(this)"
                onkeydown="return blockInvalidKeys(event)"
                onpaste="return blockNegativePaste(event)"
              />
              <form:errors path="height" cssClass="error-box" element="div" />
            </div>

            <div class="field-container">
              <form:label path="weight"><spring:message code="profileInfo.weight"/></form:label>
              <input
                id="patient-weight"
                name="weight"
                type="text"
                maxlength="9"
                pattern="^\d{1,6}([.]\d{1,2})?$"
                class="input-field"
                value="${user_data.weight}"
                oninput="validateDecimal(this)"
                onkeydown="return blockInvalidKeys(event)"
                onpaste="return blockNegativePaste(event)"
              />
              <form:errors path="weight" cssClass="error-box" element="div" />
            </div>
        </div>
      </div>

      <!-- Sección hábitos -->
      <div class="card">
        <h3 class="section-title"><spring:message code="profileInfo.habits"/></h3>
        <div class="field-grid">
          <div class="field-container">
            <form:label path="smokes"><spring:message code="profileInfo.smokes"/></form:label>
            <select id="patient-smokes" name="smokes" class="input-field filter-select">
              <option value="" <c:if test="${user_data.smokes == null}">selected</c:if>>
                <spring:message code="profileInfo.select"/>
              </option>
              <option value="true" <c:if test="${user_data.smokes != null and user_data.smokes}">selected</c:if>>
                <spring:message code="profileInfo.yes"/>
              </option>
              <option value="false" <c:if test="${user_data.smokes != null and !user_data.smokes}">selected</c:if>>
                <spring:message code="profileInfo.no"/>
              </option>
            </select>
          </div>

          <div class="field-container">
            <form:label path="drinks"><spring:message code="profileInfo.drinks"/></form:label>
            <select id="patient-drinks" name="drinks" class="input-field filter-select">
              <option value="" <c:if test="${user_data.drinks == null}">selected</c:if>>
                <spring:message code="profileInfo.select"/>
              </option>
              <option value="true" <c:if test="${user_data.drinks != null and user_data.drinks}">selected</c:if>>
                <spring:message code="profileInfo.yes"/>
              </option>
              <option value="false" <c:if test="${user_data.drinks != null and !user_data.drinks}">selected</c:if>>
                <spring:message code="profileInfo.no"/>
              </option>
            </select>
          </div>


          <div class="field-container full-width">
            <form:label path="diet"><spring:message code="profileInfo.diet"/></form:label>
            <textarea id="patient-diet" name="diet" class="input-field" rows="2" maxlength="100">${user_data.diet}</textarea>
          </div>
        </div>
      </div>

      <!-- Sección médica -->
      <div class="card">
        <h3 class="section-title"><spring:message code="profileInfo.medical"/></h3>
        <div class="field-grid">
          <div class="field-container full-width">
            <form:label path="meds"><spring:message code="profileInfo.meds"/></form:label>
            <textarea id="patient-meds" name="meds" class="input-field" rows="2" maxlength="250">${user_data.meds}</textarea>
          </div>
          <div class="field-container full-width">
            <form:label path="conditions"><spring:message code="profileInfo.conditions"/></form:label>
            <textarea id="patient-conditions" name="conditions" class="input-field" rows="2" maxlength="250">${user_data.conditions}</textarea>
          </div>
          <div class="field-container full-width">
            <form:label path="allergies"><spring:message code="profileInfo.allergies"/></form:label>
            <textarea id="patient-allergies" name="allergies" class="input-field" rows="2" maxlength="250">${user_data.allergies}</textarea>
          </div>
        </div>
      </div>

      <!-- Sección social -->
      <div class="card">
        <h3 class="section-title"><spring:message code="profileInfo.social"/></h3>
        <div class="field-grid">
          <div class="field-container full-width">
            <form:label path="hobbies"><spring:message code="profileInfo.hobbies"/></form:label>
            <textarea id="patient-hobbies" name="hobbies" class="input-field" rows="2" maxlength="100">${user_data.hobbies}</textarea>
          </div>
          <div class="field-container full-width">
            <form:label path="job"><spring:message code="profileInfo.job"/></form:label>
            <textarea id="patient-job" name="job" class="input-field" rows="1" maxlength="50">${user_data.job}</textarea>
          </div>
        </sec:authorize>
      </div>

      <c:set var="updatePrimaryText">
        <spring:message code="doctorProfile.cancelAppointments"/>
      </c:set>
      <c:set var="updateSecondaryText">
        <ul>
          <li><spring:message code="doctorProfile.cancelAppointments.info.first"/></li>
          <li><spring:message code="doctorProfile.cancelAppointments.info.second"/></li>
        </ul>
      </c:set>
      <c:set var="updateRemoveText">
        <spring:message code="doctorProfile.updateSchedule.cancelAll"/>
      </c:set>
      <c:set var="updateCancelRemoveText">
        <spring:message code="doctorProfile.updateSchedule.dontCancel"/>
      </c:set>
      <c:set var="cancelUpdateText">
        <spring:message code="doctorProfile.updateSchedule.cancel"/>
      </c:set>
      <!-- Botón guardar -->
      <div class="save-container" style="margin-top: 15px;">
        <button
          type="button"
          class="btn-primary"
          onclick="updateScheduleModal(this.form, '${updatePrimaryText}', '${updateSecondaryText}', '${updateRemoveText}', '${updateCancelRemoveText}', '${cancelUpdateText}')"
        >
          <spring:message code="profile.save.button"/>
        </button>
      </div>
    </form:form>
  </div>
  <%@include file="components/updateScheduleDialog.jsp" %>
  <script src="<c:url value='/js/updateScheduleModal.js'/>"></script>

  <script>
    function handleImageChange(event) {
      if (event && event.target.files[0]) {
        document.getElementById("prof-image").src = URL.createObjectURL(event.target.files[0]);
      } else {
        document.getElementById("fileInput").click();
      }
    }

    function blockInvalidKeys(event) {
      const invalidChars = ['e', 'E', '-', '+'];
      if (invalidChars.includes(event.key)) {
        event.preventDefault();
        return false;
      }
      return true;
    }

    function blockInvalidPhoneKeys(event) {
      const invalidChars = ['e', 'E', '-', '+', '.'];
      if (invalidChars.includes(event.key)) {
        event.preventDefault();
        return false;
      }
      return true;
    }

    function blockNegativePaste(event) {
      const paste = (event.clipboardData || window.clipboardData).getData('text');
      if (paste.includes('-') || paste.includes('+')) {
        event.preventDefault();
        return false;
      }
      return true;
    }

    function validateDecimal(input) {
      const value = input.value.replace(',', '.'); // reemplaza coma por punto
      const regex = /^\d{0,6}(\.\d{0,2})?$/;
      if (!regex.test(value)) {
        input.value = value.slice(0, -1);
      } else {
        input.value = value; // actualiza con punto si venía con coma
      }
    }

    function closeToast() {
      const toast = document.getElementById("update-toast");
      if (!toast) return;

      if (!toast.classList.contains("hide")) {
        toast.classList.add("hide");
        setTimeout(() => toast.remove(), 500);
      }
    }

    document.addEventListener("DOMContentLoaded", () => {
      const toast = document.getElementById("update-toast");
      if (toast) {
        // auto-dismiss a los 3 segundos
        setTimeout(closeToast, 3000);
      }
    });

    function setUpdateScheduleFields() {
      const updateScheduleSwitch = document.getElementById("updateScheduleSwitch");
      const newScheduleFields = document.getElementById("new-schedule-fields");
      if (updateScheduleSwitch.checked) {
        newScheduleFields.style.display = "block";
      } else {
        newScheduleFields.style.display = "none";
      }
    }
  </script>
  <script src="<c:url value='/js/buttonControl.js'/>"></script>
</body>
