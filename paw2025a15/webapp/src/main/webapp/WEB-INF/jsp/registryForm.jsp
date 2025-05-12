<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="icon" type="image/png" href="<c:url value='/favicon.ico'/>"/>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="<c:url value='/css/register-form.css'/>">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
</head>
<body>

<c:url value="/createPatient" var="patientPost"/>
<c:url value="/createMedic"   var="medicPost"/>
<c:url value="/login" var="login"/>

<div class="page-container">
  <div class="login-card">

    <div class="form-header">
      <h2 class="title"><spring:message code="register.signup"/></h2>
      <div class="toggle-btn-group">
        <button type="button" class="toggle-btn active" data-type="patient" onclick="switchForm('patient')">
          <spring:message code="appointmentTable.patientColumn.title"/>
        </button>
        <button type="button" class="toggle-btn" data-type="medic" onclick="switchForm('medic')">
          <spring:message code="appointmentTable.doctorColumn.title"/>
        </button>
      </div>
    </div>

    <!-- Sección Paciente -->
    <div id="patientSection" class="registration-section">
      <form:form cssClass="login-form" modelAttribute="registerPatientForm" action="${patientPost}" method="post">
        <form:errors cssClass="error-box" element="div"/>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.name"/></label>
          <form:input path="name" type="text" cssClass="login-input"/>
          <form:errors path="name" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.surname"/></label>
          <form:input path="surname" type="text" cssClass="login-input"/>
          <form:errors path="surname" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.email"/></label>
          <form:input path="email" type="text" cssClass="login-input"/>
          <form:errors path="email" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.phone"/></label>
          <form:input path="phoneNumber" type="text" cssClass="login-input"/>
          <form:errors path="phoneNumber" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <form:label cssClass="field-label" path="birthDate"><spring:message code="form.birthDate"/></form:label>
          <form:input
                  cssClass="login-input"
                  id="birthDate"
                  path="birthDate"
                  type="date" />
          <form:errors
                  path="birthDate"
                  cssClass="error-box"
                  element="div" />
        </div>

        <div class="field-container">
          <form:label cssClass="field-label" path="height"><spring:message code="profileInfo.height"/></form:label>
          <form:input id="patient-height"
                 path="height"
                 type="text"
                 maxlength="9"
                 pattern="^\d{1,6}([.]\d{1,2})?$"
                 class="login-input"
                 oninput="validateDecimal(this)"
                 onkeydown="return blockInvalidKeys(event)"
                 onpaste="return blockNegativePaste(event)"/>
        </div>

        <div class="field-container">
          <form:label cssClass="field-label" path="weight"><spring:message code="profileInfo.weight"/></form:label>
          <form:input id="patient-weight"
                 path="weight"
                 type="text"
                 maxlength="9"
                 pattern="^\d{1,6}([.]\d{1,2})?$"
                 class="login-input"
                 oninput="validateDecimal(this)"
                 onkeydown="return blockInvalidKeys(event)"
                 onpaste="return blockNegativePaste(event)"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.password"/></label>
          <div class="password-container">
            <form:input path="password" id="patientPassword" type="password" cssClass="login-input"/>
            <span class="toggle-password" onclick="togglePassword('patientPassword')">
              <i class="fas fa-eye"></i>
            </span>
          </div>
          <form:errors path="password" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.confirmPassword"/></label>
          <div class="password-container">
            <form:input path="confirmPassword" id="patientConfirmPassword" type="password" cssClass="login-input"/>
            <span class="toggle-password" onclick="togglePassword('patientConfirmPassword')">
              <i class="fas fa-eye"></i>
            </span>
          </div>
          <form:errors path="confirmPassword" cssClass="error-box" element="div"/>
        </div>

        <button type="submit" class="btn-primary">
          <spring:message code="doctorForm.registerButton"/>
        </button>

      </form:form>
    </div>

    <!-- Sección Médico -->
    <div id="medicSection" class="registration-section">
      <form:form cssClass="login-form" modelAttribute="registerMedicForm" action="${medicPost}" method="post">
        <form:errors cssClass="error-box" element="div"/>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.name"/></label>
          <form:input path="name" type="text" cssClass="login-input"/>
          <form:errors path="name" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.surname"/></label>
          <form:input path="surname" type="text" cssClass="login-input"/>
          <form:errors path="surname" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.email"/></label>
          <form:input path="email" type="text" cssClass="login-input"/>
          <form:errors path="email" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.password"/></label>
          <div class="password-container">
            <form:input path="password" id="medicPassword" type="password" cssClass="login-input"/>
            <span class="toggle-password" onclick="togglePassword('medicPassword')">
              <i class="fas fa-eye"></i>
            </span>
          </div>
          <form:errors path="password" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.confirmPassword"/></label>
          <div class="password-container">
            <form:input path="confirmPassword" id="medicConfirmPassword" type="password" cssClass="login-input"/>
            <span class="toggle-password" onclick="togglePassword('medicConfirmPassword')">
              <i class="fas fa-eye"></i>
            </span>
          </div>
          <form:errors path="confirmPassword" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.phone"/></label>
          <form:input path="phoneNumber" type="text" cssClass="login-input"/>
          <form:errors path="phoneNumber" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.doctorLicense"/></label>
          <form:input path="doctorLicense" type="text" cssClass="login-input" maxlength="50"/>
          <form:errors path="doctorLicense" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.specialty"/></label>
          <form:select path="specialty" cssClass="login-select">
            <form:options items="${specialtySelectItems}" itemValue="value" itemLabel="label"/>
          </form:select>
          <form:errors path="specialty" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.address"/></label>
          <form:input path="address" type="text" cssClass="login-input"/>
          <form:errors path="address" cssClass="error-box" element="div"/>
        </div>

        <div class="insurance-toggle-group">
          <c:forEach var="insurance" items="${obrasSocialesItems}">
            <div class="insurance-btn">
              <input type="checkbox" id="insurance-${insurance.id}" name="ObrasSociales" value="${insurance.id}" class="insurance-checkbox"/>
              <label for="insurance-${insurance.id}" class="insurance-label">${insurance.name}</label>
            </div>
          </c:forEach>
          <form:errors path="ObrasSociales" cssClass="error-box" element="div"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.schedules.weekday"/></label>
          <div class="weekday-toggle-group">
            <c:forEach var="day" items="${weekdaySelectItems}">
              <div class="weekday-btn">
                <input type="checkbox" id="day-${day.value}" name="schedules.weekday" value="${day.value}" class="weekday-checkbox"/>
                <label for="day-${day.value}" class="weekday-label">
                  <spring:message code="weekday.${day.value}.initial"/>
                </label>
              </div>
            </c:forEach>
          </div>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.schedules.startTime"/></label>
          <form:select path="schedules.startTime" cssClass="login-select" items="${hoursSelectItems}" itemLabel="label" itemValue="value"/>
        </div>

        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.schedules.endTime"/></label>
          <form:select path="schedules.endTime" cssClass="login-select" items="${hoursSelectItems}" itemLabel="label" itemValue="value"/>
        </div>
        <form:errors path="schedules" cssClass="error-box" element="div"/>
        <div class="field-container">
          <label class="field-label"><spring:message code="doctorForm.amount"/></label>
          <form:select path="amount" cssClass="login-select">
            <form:option value="15" label="15"/>
            <form:option value="30" label="30"/>
            <form:option value="45" label="45"/>
            <form:option value="60" label="60"/>
          </form:select>
          <form:errors path="amount" cssClass="error-box" element="div"/>
        </div>

        <button type="submit" class="btn-primary">
          <spring:message code="doctorForm.registerButton"/>
        </button>
      </form:form>

      <div class="extra-links">
        <p>
          <a href="${login}" class="link-primary">
            <spring:message code="register.back"/>
          </a>
        </p>
      </div>
    </div>

  </div>
</div>

<script>
  function switchForm(type) {
    state = type;
    localStorage.setItem("formState", type);

    document.getElementById('patientSection').style.display = type === 'patient' ? 'block' : 'none';
    document.getElementById('medicSection').style.display = type === 'medic' ? 'block' : 'none';

    document.querySelectorAll('.toggle-btn').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.type === type);
    });
  }

  window.onload = function () {
    const savedState = localStorage.getItem("formState");
    if (savedState === 'medic' || savedState === 'patient') {
      state = savedState;
    }
    switchForm(state);
  };

  function togglePassword(id) {
    const f = document.getElementById(id),
            i = f.nextElementSibling.querySelector('i');
    if (f.type === 'password') {
      f.type = 'text';
      i.classList.replace('fa-eye', 'fa-eye-slash');
    } else {
      f.type = 'password';
      i.classList.replace('fa-eye-slash', 'fa-eye');
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
</script>
<script src="<c:url value='/js/buttonControl.js'/>"></script>
</body>
</html>