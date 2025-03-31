<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CareTrace <c:if test="${param.title != null}"> - ${param.title}</c:if></title>
    <link rel="icon" type="image/png" href="resources/favicon.png" />
    <link rel="stylesheet" href="css/main.css">
  </head>
  <body>
    <!-- the title with a "-" appended at the front (if it exists)-->
    <div class="topbar">
      <div class="logo">
        <a href='/' class="logo-link">
          <img src="resources/icono.jpg" alt="Logo">
        </a>
      </div>
      <nav class="nav-links">
        <a href="estudios" class="nav-item <c:if test='${param.title == "Estudios"}'>active</c:if>">Estudios</a>
        <a href="obras-sociales" class="nav-item <c:if test='${param.title == "Obras Sociales"}'>active</c:if>">Obras Sociales</a>
      </nav>
      <div class="search-bar-container">
        <div class="search-bar">
          <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20">
            <path fill="white" d="M10 2a8 8 0 015.29 13.71l3.94 3.94-1.42 1.42-3.94-3.94A8 8 0 1110 2m0 2a6 6 0 104.24 10.24A6 6 0 0010 4z"/>
          </svg>
          <input type="text" class="search-bar-text" placeholder="Buscar medico">
        </div>
      </div>
      <a href="medico">
        <button class="doctor-btn" href="medico">
          Soy Médico
        </button>
      </a>
    </div>
  </body>
</html>