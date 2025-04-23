<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
  <title>Inicio</title>
  <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
  <link rel="stylesheet" href="<c:url value='/css/login-form.css'/>">
</head>
<body>

<div class="page-container">
  <div class="doctor-form-container">
    <img src="<c:url value='/resources/favicon.png'/>" alt="Logo" class="logo">
    <div class="button-group">
      <form action="<c:url value='/register/doctor-form'/>" method="get">
        <input type="submit" value="Soy Médico" class="register-button"/>
      </form>

      <form action="<c:url value='/register/patient-form'/>" method="get">
        <input type="submit" value="Soy Paciente" class="register-button"/>
      </form>
    </div>
  </div>
</div>

</body>
</html>