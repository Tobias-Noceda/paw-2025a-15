<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<html>
<head>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="<c:url value="/css/landing-page.css"/>" />
  <link rel="stylesheet" href="<c:url value="/css/main.css"/>" />
  <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
</head>
<body>
<%@include file="header.jsp" %>

<form:form modelAttribute="filterForm" action="/filter" method="get">
  <div>
    <form:label path="insurances">
      <spring:message code="doctorForm.obrasSociales"/>
    </form:label>
    <form:select path="insurances">
      <form:option value="">Todas</form:option>
      <form:options itemLabel="name" itemValue="id" items="${insurances}" />
    </form:select>
  </div>

  <div>
    <form:label path="specialty">
      <spring:message code="doctorForm.specialty"/>
    </form:label>
    <form:select path="specialty">
      <form:option value="">Todas</form:option>
      <form:options itemLabel="name" itemValue="name" items="${specialty}" />
    </form:select>
  </div>

  <div>
    <form:label path="weekday">
      <spring:message code="doctorForm.schedules.weekday"/>
    </form:label>
    <form:select path="weekday">
      <form:option value="">Todos</form:option>
      <form:options itemLabel="name" itemValue="name" items="${weekdays}" />
    </form:select>
  </div>

  <div class="doctor-div">
    <input type="submit" value="<spring:message code='doctorForm.registerButton'/>"/>
  </div>
</form:form>

    <h2>Cartilla médica:</h2>

<div class="doctorLanding-container">
  <c:forEach var="doctor" items="${docList}">
    <jsp:include page="doctor-card.jsp">
      <jsp:param name="id" value="${doctor.id}" />
      <jsp:param name="doctorName" value="${doctor.name}" />
      <jsp:param name="insurances" value="${doctor.insurances}" />
      <jsp:param name="speciality" value="${doctor.specialty}" />
    </jsp:include>
  </c:forEach>
</div>
</body>
</html>
