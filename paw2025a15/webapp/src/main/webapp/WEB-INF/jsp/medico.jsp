<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="resources/favicon.png"/>" />
    <link rel="stylesheet" href="<c:url value="css/doctor-form.css"/>">
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

      <div  style="padding: 7px">
        <form:label path="name">Nombre: </form:label>
        <form:input type="text" path="name"/>
        <form:errors path="name" cssClass="formError" element="p"/>
      </div>

      <div style="padding: 7px">
        <form:label path="surname">Apellido: </form:label>
        <form:input type="text" path="surname" />
        <form:errors path="surname" cssClass="formError" element="p"/>
      </div>

      <div style="padding: 7px">
        <form:label path="email">Correo electronico: </form:label>
        <form:input type="text" path="email"/>
        <form:errors path="email" cssClass="formError" element="p"/>
      </div>
      <div style="padding: 7px">
        <form:label path="specialty">Especialidad: </form:label>
        <form:input type="text" path="specialty"/>
        <form:errors path="email" cssClass="formError" element="p"/>
      </div>
      <div style="padding: 7px">
        <form:label path="address">Dirección: </form:label>
        <form:input type="text" path="address"/>
        <form:errors path="address" cssClass="formError" element="p"/>
      </div>
      <div style="padding: 7px">
        <form:label path="obrasSociales">Obras sociales:</form:label>
      </div>

      <div class="checkbox-group" style="padding: 7px">
          <form:checkboxes path="obrasSociales" items="${obrasSocialesItems}" itemLabel="label" itemValue="value"  cssClass="checkbox-inline"/>
      </div>
      <div style="padding: 7px">
        <form:label path="schedules.weekday">Días laborales:</form:label>
      </div>
      <div class="checkbox-group" style="padding: 7px">
        <form:checkboxes path="schedules.weekday" items="${weekdaySelectItems}" itemLabel="label" itemValue="value"  cssClass="checkbox-inline"/>
      </div>

      <div style="padding: 7px">
        <form:label path="schedules.startTime">Horario de inicio:</form:label>
        <form:select path="schedules.startTime" itemLabel="label" itemValue="value" items="${hoursSelectItems}" />
        <form:label path="schedules.endTime">Horario de fin:</form:label>
        <form:select path="schedules.endTime" itemLabel="label" itemValue="value" items="${hoursSelectItems}" />

      </div>

      <div >
        <input type="submit" value="Registrarme" class="register-btn"/>
      </div>

    </form:form>
  </body>
</html>
