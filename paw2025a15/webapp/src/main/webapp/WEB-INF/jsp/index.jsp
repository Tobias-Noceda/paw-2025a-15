<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/landing-page.css"/>" />
    <link rel="icon" type="image/png" href="<c:url value='/resources/favicon.png'/>" />
  </head>
  <body>
    <%@include file="header.jsp" %>
    <div class="page-container">
      <div class="landing-filters-box">
        <h1 class="landing-filters-title"><spring:message code="landing.filter.title"></spring:message></h1>
        <c:set var="filterUrl">
          <c:url value="/filter" />
        </c:set>
        <form:form modelAttribute="filterForm" action="${filterUrl}" method="get">
          <div class="landing-filters-container">
            <div class="filter-container">
              <form:label path="insurances" class="filter-label">
                <spring:message code="doctorForm.obrasSociales"/>
              </form:label>
              <form:select path="insurances" class="filter-select">
                <form:option value=""><spring:message code="landing.filter.insurances"></spring:message></form:option>
                <form:options itemLabel="name" itemValue="id" items="${insurances}" />
              </form:select>
            </div>
            
            <div class="filter-container">
              <form:label path="weekday" class="filter-label">
                <spring:message code="doctorForm.schedules.weekday"/>
              </form:label>
              <form:select path="weekday" class="filter-select">
                <form:option value=""><spring:message code="landing.filter.weekdays"></spring:message></form:option>
                <form:options items="${weekdaySelectItems}" itemLabel="label" itemValue="value" />
              </form:select>
            </div>
            
            <div class="filter-container">
              <form:label path="specialty" class="filter-label">
                <spring:message code="doctorForm.specialty"/>
              </form:label>
              <form:select path="specialty" class="filter-select">
                <form:option value=""><spring:message code="landing.filter.specialty"></spring:message></form:option>
                <form:options items="${specialtySelectItems}" itemLabel="label" itemValue="value" />
              </form:select>
            </div>

            <input type="submit" class="filter-button" value="<spring:message code='landing.filter'/>"/>
          </div>
        </form:form>
      </div>
  
      <h2 class="landing-title"><spring:message code="landing.title"></spring:message></h2>
      <c:if test="${not empty docList}">
        <div class="doctor-landing-container">
          <c:forEach var="doctor" items="${docList}">
            <jsp:include page="components/doctorCard.jsp">
              <jsp:param name="id" value="${doctor.id}" />
              <jsp:param name="doctorName" value="${doctor.name}" />
              <jsp:param name="imageId" value="${doctor.imageId}" />
              <jsp:param name="insurances" value="${doctor.insurances}" />
              <jsp:param name="speciality" value="${doctor.specialty}" />
              <jsp:param name="weekdays" value="${doctor.weekdays}" />
            </jsp:include>
          </c:forEach>
        </div>
      </c:if>
      <c:if test="${empty docList}">
        <div class="no-doctors-message-div">
          <h1><spring:message code="landing.noDoctors"></spring:message></h1>
        </div>
      </c:if>
    </div>
  </body>
</html>
