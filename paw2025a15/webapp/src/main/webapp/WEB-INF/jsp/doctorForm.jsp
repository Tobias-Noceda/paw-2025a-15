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
    <c:url value="/createMedic" var="postPath"/>
    <div class="page-container" style="align-items: center;">
      <div class="doctor-form-container">
        <form:form cssClass="doctor-form" modelAttribute="registerMedicForm" action="${postPath}" method="post">
          <div class="field-container">
            <form:errors path="name" cssClass="form-error" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="name">
                <spring:message code="doctorForm.name"/>
              </form:label>
              <form:input type="text" path="name" class="doctor-form-input"/>
            </div>
          </div>
    
          <div class="field-container">
            <form:errors path="surname" cssClass="form-error" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="surname">
                <spring:message code="doctorForm.surname"/>
              </form:label>
              <form:input type="text" path="surname" class="doctor-form-input"/>
            </div>
          </div>
    
          <div class="field-container">
            <form:errors path="email" cssClass="form-error" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="email">
                <spring:message code="doctorForm.email"/>
              </form:label>
              <form:input type="text" path="email" class="doctor-form-input"/>
            </div>
          </div>

          <div class="field-container">
            <form:errors path="password" cssClass="form-error" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="email">
                <spring:message code="doctorForm.password"/>
              </form:label>
              <form:input type="password" path="password" class="doctor-form-input"/>
            </div>
          </div>
    
          <div class="field-container">
            <form:errors path="speciality" cssClass="form-error" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="speciality">
                <spring:message code="doctorForm.specialty"/>
              </form:label>
              <form:select  path="speciality" cssClass="form-select">
                <form:options items="${specialtySelectItems}" itemValue="value" itemLabel="label" />
              </form:select>
            </div>
          </div>
    
          <div class="field-container">
            <form:errors path="address" cssClass="form-error" element="p"/>
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
              <form:checkboxes path="schedules.weekday" items="${weekdaySelectItems}" itemLabel="label" itemValue="value" cssClass="checkbox-inline"/>
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
            <form:errors path="amount" cssClass="form-error" element="p"/>
            <div class="field-info-container">
              <form:label cssClass="form-title" path="amount">
                <spring:message code="doctorForm.amount"/>
              </form:label>
              <form:select path="amount" cssClass="doctor-form-amount">
                <form:option value="15" label="15"/>
                <form:option value="30" label="30"/>
                <form:option value="45" label="45"/>
                <form:option value="60" label="60"/>
              </form:select>
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