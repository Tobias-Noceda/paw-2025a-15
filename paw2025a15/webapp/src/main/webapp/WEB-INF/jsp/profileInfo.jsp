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
          <form:input path="phoneNumber" value="${user.telephone}" cssClass="doctor-phone" />
        </div>


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
