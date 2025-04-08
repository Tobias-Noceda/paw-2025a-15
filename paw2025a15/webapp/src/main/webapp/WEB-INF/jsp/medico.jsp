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
    <div class="page-container" style="align-items: center;">
      <div class="doctor-form-container">
        <form:form cssClass="doctor-form" modelAttribute="registerMedicForm" action="${postPath}" method="post">
          <div class="field-container">
            <form:errors path="name" cssClass="formError" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="name">
                <spring:message code="doctorForm.name"/>
              </form:label>
              <form:input type="text" path="name" class="doctor-form-input"/>
            </div>
          </div>
    
          <div class="field-container">
            <form:errors path="surname" cssClass="formError" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="surname">
                <spring:message code="doctorForm.surname"/>
              </form:label>
              <form:input type="text" path="surname" class="doctor-form-input"/>
            </div>
          </div>
    
          <div class="field-container">
            <form:errors path="email" cssClass="formError" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="email">
                <spring:message code="doctorForm.email"/>
              </form:label>
              <form:input type="text" path="email" class="doctor-form-input"/>
            </div>
          </div>
    
          <div class="field-container">
            <form:errors path="speciality" cssClass="formError" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="speciality">
                <spring:message code="doctorForm.specialty"/>
              </form:label>
              <form:select  path="speciality" cssClass="form-select">
                <form:options items="${specialtyItems}" itemValue="name" />
              </form:select>
            </div>
          </div>
    
          <div class="field-container">
            <form:errors path="address" cssClass="formError" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="address">
                <spring:message code="doctorForm.address"/>
              </form:label>
              <form:input type="text" path="address" class="doctor-form-input"/>
            </div>
          </div>
    
          <div class="field-container">
            <form:label cssClass="form-title" path="obrasSociales" style="margin: 0 0 4px 0;">
              <spring:message code="doctorForm.obrasSociales"/>
            </form:label>

            <div class="checkbox-group">
              <form:checkboxes path="obrasSociales" items="${obrasSocialesItems}" itemLabel="name" itemValue="id" cssClass="checkbox-inline"/>
            </div>
          </div>
    
          <div class="field-container">
            <form:label cssClass="form-title" path="schedules.weekday" style="margin: 0 0 4px 0;">
              <spring:message code="doctorForm.schedules.weekday"/>
            </form:label>
            
            <div class="checkbox-group">
              <form:checkboxes path="schedules.weekday" items="${weekdaySelectItems}" itemLabel="name" itemValue="name" cssClass="checkbox-inline"/>
            </div>
          </div>
          
          <div class="field-container">
            <div class="field-info-container">
              <form:label cssClass="form-title" path="schedules.startTime">
                <spring:message code="doctorForm.schedules.startTime"/>
              </form:label>
              <form:select path="schedules.startTime" itemLabel="label" itemValue="value" items="${hoursSelectItems}" class="hour-select"/>
            </div>
          </div>
    
          <div class="field-container">
            <div class="field-info-container">
              <form:label cssClass="form-title" path="schedules.endTime">
                <spring:message code="doctorForm.schedules.endTime"/>
              </form:label>
              <form:select path="schedules.endTime" itemLabel="label" itemValue="value" items="${hoursSelectItems}" class="hour-select" />
            </div>
          </div>
          
          <div class="field-container">
            <form:errors path="amount" cssClass="formError" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="amount">
                <spring:message code="doctorForm.amount"/>
              </form:label>
              <form:input type="number" path="amount" class="doctor-form-amount"/>
            </div>
          </div>
    
          <div class="doctor-div">
            <input type="submit" value="<spring:message code="doctorForm.registerButton"/>" class="register-button"/>
          </div>
        </form:form>
      </div>
    </div>
  </body>
</html>