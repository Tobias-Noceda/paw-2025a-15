<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
  <link rel="icon" type="image/png" href="<c:url value="/resources/favicon.png"/>" />
  <link rel="stylesheet" href="<c:url value="/css/doctor-form.css"/>">
</head>
<body>
<c:set var="title">
  <spring:message code="header.doctor"/>
</c:set>
<jsp:include page="header.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>
<c:url value="/createMedic" var="postPath"/>
<form:form modelAttribute="registerMedicForm" action="${postPath}" method="post">

  <div style="padding: 7px">
    <form:label cssClass="form-title" path="name">
      <spring:message code="doctorForm.name"/>
    </form:label>
    <form:input type="text" path="name"/>
    <form:errors path="name" cssClass="formError" element="p"/>
  </div>

  <div style="padding: 7px">
    <form:label cssClass="form-title" path="surname">
      <spring:message code="doctorForm.surname"/>
    </form:label>
    <form:input type="text" path="surname"/>
    <form:errors path="surname" cssClass="formError" element="p"/>
  </div>

  <div style="padding: 7px">
    <form:label cssClass="form-title" path="email">
      <spring:message code="doctorForm.email"/>
    </form:label>
    <form:input type="text" path="email"/>
    <form:errors path="email" cssClass="formError" element="p"/>
  </div>

  <div style="padding: 7px">
    <form:label cssClass="form-title" path="specialty">
      <spring:message code="doctorForm.specialty"/>
    </form:label>
    <form:select path="specialty" cssClass="form-select">
      <form:options items="${specialtyItems}" />
    </form:select>
    <form:errors path="specialty" cssClass="formError" element="p"/>
  </div>

  <div style="padding: 7px">
    <form:label cssClass="form-title" path="address">
      <spring:message code="doctorForm.address"/>
    </form:label>
    <form:input type="text" path="address"/>
    <form:errors path="address" cssClass="formError" element="p"/>
  </div>

  <div style="padding: 7px">
    <form:label cssClass="form-title" path="obrasSociales">
      <spring:message code="doctorForm.obrasSociales"/>
    </form:label>
  </div>

  <div class="checkbox-group" style="padding: 7px">
    <form:checkboxes path="obrasSociales" items="${obrasSocialesItems}" itemLabel="name" itemValue="id" cssClass="checkbox-inline"/>
  </div>

  <div style="padding: 7px">
    <form:label cssClass="form-title" path="schedules.weekday">
      <spring:message code="doctorForm.schedules.weekday"/>
    </form:label>
  </div>

  <div class="checkbox-group" style="padding: 7px">
    <form:checkboxes path="schedules.weekday" items="${weekdaySelectItems}" itemLabel="name" itemValue="ordinal" cssClass="checkbox-inline"/>
  </div>

  <div style="padding: 7px">
    <form:label cssClass="form-title" path="schedules.startTime">
      <spring:message code="doctorForm.schedules.startTime"/>
    </form:label>
    <form:select path="schedules.startTime" itemLabel="label" itemValue="value" items="${hoursSelectItems}" />

    <form:label cssClass="form-title" path="schedules.endTime">
      <spring:message code="doctorForm.schedules.endTime"/>
    </form:label>
    <form:select path="schedules.endTime" itemLabel="label" itemValue="value" items="${hoursSelectItems}" />
  </div>

  <div style="padding: 7px">
    <input type="submit" value="<spring:message code="doctorForm.registerButton"/>" class="register-button"/>
  </div>

</form:form>
</body>
</html>
