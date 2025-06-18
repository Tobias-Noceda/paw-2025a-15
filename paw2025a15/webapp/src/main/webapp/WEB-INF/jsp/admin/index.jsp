<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="admin.insurances.title"/></title>
    <link rel="icon" type="image/png" href="<c:url value='/favicon.ico'/>" />
    <link rel="stylesheet" href="<c:url value='/css/base.css'/>">
  </head>
  <body>
    <jsp:include page="../components/header.jsp"/>

    <div class="page-container">
      <!-- Mensajes de éxito/error -->
      <c:if test="${not empty successMessage}">
        <div class="message success">
          <spring:message code="admin.insurances.success.${successMessage}" />
        </div>
      </c:if>
    
      <c:if test="${not empty errorMessage}">
        <div class="message error">
          <spring:message code="admin.insurances.error.${errorMessage}" />
        </div>
      </c:if>

      <!-- Cabecera -->
      <div class="admin-header">
        <h1><spring:message code="admin.insurances.heading"/></h1>
        <a href="<c:url value='/admin/insurances/new'/>" class="btn-primary">
          <spring:message code="admin.insurances.button.new"/>
        </a>
      </div>

      <!-- Grid de obras sociales -->
      <div class="insurances-container">
        <c:choose>
          <c:when test="${empty insurances}">
            <div class="no-data-message">
              <p class="subtitle"><spring:message code="admin.insurances.empty"/></p>
            </div>
          </c:when>
          <c:otherwise>
            <div class="card-grid">
              <c:forEach var="insurance" items="${insurances}">
                <jsp:include page="../components/insuranceCard.jsp">
                  <jsp:param name="insuranceId" value="${insurance.id}" />
                  <jsp:param name="insuranceName" value="${insurance.name}" />
                </jsp:include>
              </c:forEach>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <!-- Paginación -->
      <c:if test="${totalPages > 0}">
        <c:url value='/admin/home' var="pageUrl"/>
        <form:form method="get" action="${pageUrl}" class="page-navigator-div" style="margin-block-end: 0px;">
          <input type="hidden" name="page" id="pageInput" value="${currentPage}" />
          <button
            type="submit"
            class="page-navigation-button"
            onclick="document.getElementById('pageInput').value = ${1}"
            <c:if test="${currentPage == 1}">disabled</c:if>
          >
            <spring:message code="landing.pagination.first"/>
          </button>

          <button
            type="submit"
            class="page-navigation-button"
            onclick="document.getElementById('pageInput').value = ${currentPage - 1}"
            <c:if test="${currentPage <= 1}">disabled</c:if>
          >
            <spring:message code="doctorDetail.previousWeek"/>
          </button>

          <div class="page-button">
            ${currentPage} / ${totalPages}
          </div>

          <button
            type="submit"
            class="page-navigation-button"
            onclick="document.getElementById('pageInput').value = ${currentPage + 1}"
            <c:if test="${currentPage >= totalPages}">disabled</c:if>
          >
            <spring:message code="doctorDetail.nextWeek"/>
          </button>

          <button
            type="submit"
            class="page-navigation-button"
            onclick="document.getElementById('pageInput').value = ${totalPages}"
            <c:if test="${currentPage == totalPages}">disabled</c:if>
          >
            <spring:message code="landing.pagination.last"/>
          </button>
        </form:form>
      </c:if>
    </div>
  </body>
</html> 
