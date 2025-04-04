<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<html>
<head>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="<c:url value="css/landing-page.css"/>"
  <title>CareTrace</title>
  <link rel="icon" type="image/png" href="<c:url value='resources/favicon.png'/>" />
</head>
<body>
<%@include file="header.jsp" %>
<h2>  Cartilla médica:</h2>

<div class="doctorLanding-container">
  <c:forEach var="doctor" items="${docList}">
    <jsp:include page="doctor-card.jsp">
      <jsp:param name="doctorName" value="${doctor.name}" />
      <jsp:param name="workingEnsurances" value="${doctor.workingEnsurances}" />
      <jsp:param name="speciality" value="${doctor.specialty}" />
    </jsp:include>
  </c:forEach>
</div>
</body>
</html>
