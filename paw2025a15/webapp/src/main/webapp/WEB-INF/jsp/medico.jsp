<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link rel="icon" type="image/png" href="resources/favicon.png" />
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

      <div>
        <form:label path="name">Nombre: </form:label>
        <form:input type="text" path="name"/>
        <form:errors path="name" cssClass="formError" element="p"/>
      </div>

      <div>
        <form:label path="surname">Apellido: </form:label>
        <form:input type="text" path="surname" />
        <form:errors path="surname" cssClass="formError" element="p"/>
      </div>

      <div>
        <form:label path="email">Correo electronico: </form:label>
        <form:input type="text" path="email"/>
        <form:errors path="email" cssClass="formError" element="p"/>
      </div>
      <div>
        <form:label path="obrasSociales">Obras Sociales: </form:label>
        <form:select multiple="true" path="obrasSociales" items="${items}" itemLabel="label" itemValue="value" />
        <form:errors path="obrasSociales" cssClass="formError" element="p"/>
      </div>
      <div>
        <input type="submit" value="Registrarme"/>
      </div>
    </form:form>
  </body>
</html>
