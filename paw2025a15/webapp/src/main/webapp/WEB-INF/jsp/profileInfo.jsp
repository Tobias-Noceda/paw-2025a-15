<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form"  uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><spring:message code="profile.title"/></title>
  <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="<c:url value='/css/profile-info.css'/>">
  <link rel="stylesheet" href="<c:url value='/css/base.css'/>">
</head>
<body>

<c:if test="${not empty updateSuccessMessage}">

  <div class="toast" id="update-toast">

    <span class="toast-icon">✔️</span>

    <span class="toast-text">

        <c:out value="${updateSuccessMessage}"/>

      </span>

    <button class="toast-close" onclick="closeToast()">×</button>

  </div>

</c:if>

<jsp:include page="components/header.jsp">
  <jsp:param name="username"  value="${user.name}"/>
  <jsp:param name="id" value="${user.id}"/>
  <jsp:param name="role"      value="${user.role}"/>
</jsp:include>

<c:url value='/save-profile' var="saveProfileUrl"/>
<div class="page-container">
  <form:form modelAttribute="profileForm"
             action="${saveProfileUrl}"
             method="post"
             enctype="multipart/form-data"
             class="profile-form">

    <!-- Cabecera -->
    <div class="card profile-header">
      <div class="profile-image">
        <div class="edit-image-wrapper">
          <img id="prof-image" src="<c:url value='/supersecret/user-profile-pic/${user.id}'/>" alt="User Image" />
          <button class="edit-button" type="button" onclick="handleImageChange()">
            ✏️
          </button>
          <input type="file"
                 id="fileInput"
                 name="profileImage"
                 class="hidden-file-input"
                 accept=".png, .jpg, .jpeg"
                 onchange="handleImageChange(event)"/>
        </div>
      </div>
      <div class="profile-info-block">
        <p class="name"><c:out value="${user.name}"/></p>
        <p class="email"><c:out value="${user.email}"/></p>
        <p class="role"><spring:message code="profile.role.label"/>: <spring:message code="role.${user.role}"/></p>
      </div>
    </div>

    <!-- Sección de datos básicos -->
    <div class="card">
      <h3 class="section-title"><spring:message code="profileInfo.basic"/></h3>
      <div class="field-grid">
        <div class="field-container full-width">
          <form:label path="phoneNumber"><spring:message code="profile.phone.label"/></form:label>
          <form:input path="phoneNumber"
                      type="number"
                      cssClass="input-field"
                      value="${user.telephone}"
                      onkeydown="return blockInvalidPhoneKeys(event)"
                      onpaste="return blockNegativePaste(event)"/>
          <form:errors path="phoneNumber" cssClass="error-box" element="div"/>
        </div>


        <c:if test="${patientDetails == null}">

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
                          class="insurance-label">
                      ${insurance.name}
                  </label>
                </div>
              </c:forEach>
              <form:errors path="insurances" cssClass="error-box" element="div"/>
            </div>
          </div>

      </c:if>


        <c:if test="${patientDetails != null}">
          <div class="sf-field">
            <!-- label -->
            <form:label path="birthDate"><spring:message code="form.birthDate"/></form:label>
            <!-- input date -->
            <form:input
                    id="birthDate"
                    path="birthDate"
                    type="date" />
            <!-- errores -->
            <form:errors
                    path="birthDate"
                    cssClass="sf-error"
                    element="div" />
          </div>

          <div class="field-container">
            <form:label path="bloodType"><spring:message code="profileInfo.bloodType"/></form:label>
            <select id="patient-blood-type"
                    name="bloodType"
                    class="input-field">
              <option value=""><spring:message code="profileInfo.bloodType.select"/></option>
              <c:forEach var="bloodType" items="${bloodTypes}">
                <option value="${bloodType.name()}"
                        <c:if test="${patientDetails.bloodType == bloodType}">selected</c:if>>
                    ${bloodType.getName()}
                </option>
              </c:forEach>
            </select>
          </div>

          <div class="field-container">
            <form:label path="height"><spring:message code="profileInfo.height"/></form:label>
            <input id="patient-height"
                   name="height"
                   type="text"
                   maxlength="9"
                   pattern="^\d{1,6}([.]\d{1,2})?$"
                   class="input-field"
                   value="${patientDetails.height}"
                   oninput="validateDecimal(this)"
                   onkeydown="return blockInvalidKeys(event)"
                   onpaste="return blockNegativePaste(event)"/>
          </div>

          <div class="field-container">
            <form:label path="weight"><spring:message code="profileInfo.weight"/></form:label>
            <input id="patient-weight"
                   name="weight"
                   type="text"
                   maxlength="9"
                   pattern="^\d{1,6}([.]\d{1,2})?$"
                   class="input-field"
                   value="${patientDetails.weight}"
                   oninput="validateDecimal(this)"
                   onkeydown="return blockInvalidKeys(event)"
                   onpaste="return blockNegativePaste(event)"/>
          </div>
      </div>
    </div>

    <!-- Sección hábitos -->
    <div class="card">
      <h3 class="section-title"><spring:message code="profileInfo.habits"/></h3>
      <div class="field-grid">
        <div class="field-container">
          <form:label path="smokes"><spring:message code="profileInfo.smokes"/></form:label>
          <select id="patient-smokes" name="smokes" class="input-field">
            <option value="" <c:if test="${patientDetails.smokes == null}">selected</c:if>>
              <spring:message code="profileInfo.select"/>
            </option>
            <option value="true" <c:if test="${patientDetails.smokes != null and patientDetails.smokes}">selected</c:if>>
              <spring:message code="profileInfo.yes"/>
            </option>
            <option value="false" <c:if test="${patientDetails.smokes != null and !patientDetails.smokes}">selected</c:if>>
              <spring:message code="profileInfo.no"/>
            </option>
          </select>
        </div>

        <div class="field-container">
          <form:label path="drinks"><spring:message code="profileInfo.drinks"/></form:label>
          <select id="patient-drinks" name="drinks" class="input-field">
            <option value="" <c:if test="${patientDetails.drinks == null}">selected</c:if>>
              <spring:message code="profileInfo.select"/>
            </option>
            <option value="true" <c:if test="${patientDetails.drinks != null and patientDetails.drinks}">selected</c:if>>
              <spring:message code="profileInfo.yes"/>
            </option>
            <option value="false" <c:if test="${patientDetails.drinks != null and !patientDetails.drinks}">selected</c:if>>
              <spring:message code="profileInfo.no"/>
            </option>
          </select>
        </div>


        <div class="field-container full-width">
          <form:label path="diet"><spring:message code="profileInfo.diet"/></form:label>
          <textarea id="patient-diet" name="diet" class="input-field" rows="2" maxlength="100">${patientDetails.diet}</textarea>
        </div>
      </div>
    </div>

    <!-- Sección médica -->
    <div class="card">
      <h3 class="section-title"><spring:message code="profileInfo.medical"/></h3>
      <div class="field-grid">
        <div class="field-container full-width">
          <form:label path="meds"><spring:message code="profileInfo.meds"/></form:label>
          <textarea id="patient-meds" name="meds" class="input-field" rows="2" maxlength="250">${patientDetails.meds}</textarea>
        </div>
        <div class="field-container full-width">
          <form:label path="conditions"><spring:message code="profileInfo.conditions"/></form:label>
          <textarea id="patient-conditions" name="conditions" class="input-field" rows="2" maxlength="250">${patientDetails.conditions}</textarea>
        </div>
        <div class="field-container full-width">
          <form:label path="allergies"><spring:message code="profileInfo.allergies"/></form:label>
          <textarea id="patient-allergies" name="allergies" class="input-field" rows="2" maxlength="250">${patientDetails.allergies}</textarea>
        </div>
      </div>
    </div>

    <!-- Sección social -->
    <div class="card">
      <h3 class="section-title"><spring:message code="profileInfo.social"/></h3>
      <div class="field-grid">
        <div class="field-container full-width">
          <form:label path="hobbies"><spring:message code="profileInfo.hobbies"/></form:label>
          <textarea id="patient-hobbies" name="hobbies" class="input-field" rows="2" maxlength="100">${patientDetails.hobbies}</textarea>
        </div>
        <div class="field-container full-width">
          <form:label path="job"><spring:message code="profileInfo.job"/></form:label>
          <textarea id="patient-job" name="job" class="input-field" rows="1" maxlength="50">${patientDetails.job}</textarea>
        </div>
        </c:if>
      </div>
    </div>




    <!-- Botón guardar -->
    <div class="save-container" style="margin-top: 15px;">
      <button type="submit" class="btn-primary">
        <spring:message code="profile.save.button"/>
      </button>
    </div>

  </form:form>
</div>

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

    // Si ya no tiene .hide, aplicarlo y luego remover

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


</script>
</body>
