<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="<c:url value="resources/favicon.png"/>" />
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
          <p>Obras sociales:</p>
          <form:checkboxes path="obrasSociales" items="${obrasSocialesItems}" itemLabel="label" itemValue="value"  cssClass="checkbox-inline"/>
      </div>
      <div >
        <input type="submit" value="Registrarme" class="register-btn"/>
      </div>
    </form:form>
  </body>
</html>
