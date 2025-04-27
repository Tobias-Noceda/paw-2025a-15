<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
  <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
  <link rel="stylesheet" href="<c:url value='/css/doctor-detail.css'/>">
  <style>
    .edit-image-wrapper {
      position: relative;
      display: inline-block;
    }

    /* Esto oculta las flechitas en la mayoría de los navegadores */
    input.doctor-phone::-webkit-inner-spin-button,
    input.doctor-phone::-webkit-outer-spin-button {
      -webkit-appearance: none;
      margin: 0;
    }

    input.doctor-phone {
      -moz-appearance: textfield; /* Firefox */
    }

    .doctor-phone {
      border: none;
      border-bottom: 1px solid #ccc;
      border-radius: 0;
      outline: none;
      padding: 4px 0;
      background-color: transparent; /* opcional si querés que no tenga fondo */
    }

    .edit-button {
      position: absolute;
      bottom: -10px;
      right: 10px;
      background-color: #0d4a85;
      border: none;
      border-radius: 50%;
      width: 35px;
      height: 35px;
      color: white;
      font-size: 18px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 4px rgba(0,0,0,0.2);
    }

    .edit-button:hover {
      background-color: #093a6b;
    }

    .hidden-file-input {
      display: none;
    }
  </style>
</head>
<body>
<jsp:include page="components/header.jsp">
  <jsp:param name="username" value="${user.name}"/>
  <jsp:param name="pictureId" value="${user.pictureId}"/>
  <jsp:param name="role" value="${user.role}"/>
</jsp:include>
<div class="profile-container" style="padding: 20px;">
  <h1 style="color: #0d4a85; margin-bottom: 20px;">
    <spring:message code="profile.title"/>
  </h1>
  <c:url value='/save-profile' var="saveProfileUrl"/>
  <form:form modelAttribute="profileForm" method="post" action="${saveProfileUrl}" enctype="multipart/form-data">
    <div class="doctor-card">
      <div class="doctor-image">
        <div class="edit-image-wrapper">
          <img id="prof-image" src="<c:url value='/supersecret/files/${user.pictureId}'/>" alt="Doctor Image" />
          <button class="edit-button" type="button" onclick="handleImageChange()">
            ✏️
          </button>
          <input type="file" id="fileInput"  name="profileImage" class="hidden-file-input" accept=".png, .jpg, .jpeg" onchange="handleImageChange(event)"/>
        </div>
      </div>

      <div class="doctor-info">
        <p class="doctor-name"><c:out value="${user.name}"/></p>
        <p class="doctor-email"><c:out value="${user.email}"/></p>
        <div class="doctor-specialty-div">
          <span class="doctor-specialty-label"><spring:message code="profile.role.label"/>:</span>
          <p class="doctor-specialty"><c:out value="${user.role}"/></p>
        </div>
        <div>
          <label for="phoneNumber"><spring:message code="profile.phone.label"/></label>
          <form:input type="number" path="phoneNumber" value="${user.telephone}" cssClass="doctor-phone" />
        </div>


      </div>
    </div>

    <div class="doctor-card">
      <div class="patient-info-div"><!--TODO: needs styling and inter, its only placeholder-->
        <label for="patient-age">Age:</label>
        <input type="number" id="patient-age" name="age" value="${patientDetails.age}" />
      </div>
      <div class="patient-info-div">
        <label for="patient-blood-type">Blood Type:</label>
        <select id="patient-blood-type" name="bloodType">
            <option value="" <c:if test="${patientDetails.bloodType == null}">selected</c:if>>Select Blood Type</option>
            <option value="A_POSITIVE" <c:if test="${patientDetails.bloodType == 'A_POSITIVE'}">selected</c:if>>A+</option>
            <option value="A_NEGATIVE" <c:if test="${patientDetails.bloodType == 'A_NEGATIVE'}">selected</c:if>>A-</option>
            <option value="B_POSITIVE" <c:if test="${patientDetails.bloodType == 'B_POSITIVE'}">selected</c:if>>B+</option>
            <option value="B_NEGATIVE" <c:if test="${patientDetails.bloodType == 'B_NEGATIVE'}">selected</c:if>>B-</option>
            <option value="O_POSITIVE" <c:if test="${patientDetails.bloodType == 'O_POSITIVE'}">selected</c:if>>O+</option>
            <option value="O_NEGATIVE" <c:if test="${patientDetails.bloodType == 'O_NEGATIVE'}">selected</c:if>>O-</option>
            <option value="AB_POSITIVE" <c:if test="${patientDetails.bloodType == 'AB_POSITIVE'}">selected</c:if>>AB+</option>
            <option value="AB_NEGATIVE" <c:if test="${patientDetails.bloodType == 'AB_NEGATIVE'}">selected</c:if>>AB-</option>
        </select>
      </div>
      <div class="patient-info-div">
        <label for="patient-height">Height (m):</label>
        <input type="number" step="0.01" id="patient-height" name="height" value="${patientDetails.height}" />
      </div>
      <div class="patient-info-div">
        <label for="patient-weight">Weight (kg):</label>
        <input type="number" step="0.01" id="patient-weight" name="weight" value="${patientDetails.weight}" />
      </div>
      <div class="patient-info-div">
        <label for="patient-smokes">Smokes:</label>
        <select id="patient-smokes" name="smokes">
          <option value="" <c:if test="${patientDetails.smokes == null}">selected</c:if>>Select</option>
            <option value="true" <c:if test="${patientDetails.smokes}">selected</c:if>>Yes</option>
            <option value="false" <c:if test="${patientDetails.smokes == false}">selected</c:if>>No</option>
        </select>
      </div>
      <div class="patient-info-div">
        <label for="patient-drinks">Drinks:</label>
        <select id="patient-drinks" name="drinks">
          <option value="" <c:if test="${patientDetails.drinks == null}">selected</c:if>>Select</option>
            <option value="true" <c:if test="${patientDetails.drinks}">selected</c:if>>Yes</option>
            <option value="false" <c:if test="${patientDetails.drinks == false}">selected</c:if>>No</option>
        </select>
      </div>
      <div class="patient-info-div">
        <label for="patient-meds">Medications:</label>
        <textarea id="patient-meds" name="meds"><c:out value="${patientDetails.meds}"/></textarea>
      </div>
      <div class="patient-info-div">
        <label for="patient-conditions">Conditions:</label>
        <textarea id="patient-conditions" name="conditions"><c:out value="${patientDetails.conditions}"/></textarea>
      </div>
      <div class="patient-info-div">
        <label for="patient-allergies">Allergies:</label>
        <textarea id="patient-allergies" name="allergies"><c:out value="${patientDetails.allergies}"/></textarea>
      </div>
      <div class="patient-info-div">
        <label for="patient-diet">Diet:</label>
        <textarea id="patient-diet" name="diet"><c:out value="${patientDetails.diet}"/></textarea>
      </div>
      <div class="patient-info-div">
        <label for="patient-hobbies">Hobbies:</label>
        <textarea id="patient-hobbies" name="hobbies"><c:out value="${patientDetails.hobbies}"/></textarea>
      </div>
      <div class="patient-info-div">
        <label for="patient-job">Job:</label>
        <textarea id="patient-job" name="job"><c:out value="${patientDetails.job}"/></textarea>
      </div>
    </div>

    <div class="save-container">
      <button type="submit" class="">
        <spring:message code="profile.save.button"/>
      </button>
    </div>
  </form:form>

</div>

<script>
  function handleImageChange(event) {
    if (event) {
      const file = event.target.files[0];
      if (file) {
        const objectURL = URL.createObjectURL(file);
        document.getElementById("prof-image").src = objectURL;
        console.log("Archivo seleccionado:", file.name);
      }
    } else {
      document.getElementById("fileInput").click();
    }
  }

</script>

</body>
</html>
