<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
  <head>
    <link href="/css/main.css" rel="stylesheet"/>
    <title>CareTrace - medicos</title>
    <link rel="icon" type="image/png" href="resources/favicon.png" />
    <meta charset="UTF-8">
  </head>
  <body>
    <%@include file="header.jsp" %>
    <h2>Médicos</h2>
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
