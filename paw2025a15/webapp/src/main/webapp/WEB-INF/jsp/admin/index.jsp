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
    <style>
      .admin-container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 20px;
      }
      .admin-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 30px;
      }
      .btn-primary {
        background-color: #007bff;
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        text-decoration: none;
        display: inline-block;
      }
      .btn-secondary {
        background-color: #6c757d;
        color: white;
        padding: 8px 16px;
        border: none;
        border-radius: 4px;
        text-decoration: none;
        display: inline-block;
        margin-right: 5px;
      }
      .btn-danger {
        background-color: #dc3545;
        color: white;
        padding: 8px 16px;
        border: none;
        border-radius: 4px;
        text-decoration: none;
        display: inline-block;
      }
      /* Insurance Cards */
      .insurance-card {
        width: 100%;
        max-width: 280px;
        background: var(--clr-card, white);
        border-radius: 12px;
        box-shadow: 0 8px 24px rgba(0,0,0,0.1);
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        overflow: hidden;
      }
      .insurance-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 32px rgba(0,0,0,0.15);
      }
      .insurance-card__content {
        padding: 24px;
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
      }
      .insurance-card__logo {
        width: 120px;
        height: 120px;
        margin-bottom: 16px;
        border-radius: 12px;
        overflow: hidden;
        border: 3px solid #e9ecef;
      }
      .insurance-card__logo img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      .insurance-card__info {
        margin-bottom: 20px;
      }
      .insurance-card__name {
        font-size: 1.3rem;
        font-weight: 600;
        color: #2E4A7D;
        margin: 0 0 8px 0;
      }
      .insurance-card__id {
        font-size: 0.9rem;
        color: #6c757d;
        margin: 0;
      }
      .insurance-card__actions {
        display: flex;
        gap: 12px;
        flex-wrap: wrap;
        justify-content: center;
      }
      .insurance-btn {
        padding: 8px 16px;
        border: none;
        border-radius: 6px;
        font-size: 0.9rem;
        font-weight: 500;
        cursor: pointer;
        text-decoration: none;
        transition: all 0.2s ease;
      }
      .message {
        padding: 15px;
        margin-bottom: 20px;
        border-radius: 5px;
      }
      .success {
        background-color: #d4edda;
        color: #155724;
        border: 1px solid #c3e6cb;
      }
      .error {
        background-color: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
      }
      
      /* Paginación - Estilos idénticos a la cartilla médica */
      .page-navigator-div {
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 20px 0 5px 0;
      }

      .page-navigation-button {
        background-color: #007bff;
        margin-right: 5px;
        margin-left: 5px;
        width: 90px;
        color: white;
        border: none;
        padding: 8px 12px;
        font-size: 14px;
        font-weight: 700;
        border-radius: 5px;
        cursor: pointer;
        transition: background-color 0.3s;
        text-decoration: none;
      }

      .page-navigation-button:hover {
        background-color: #0056b3;
      }

      .page-navigation-button:disabled {
        background-color: #ccc;
        color: gray;
        cursor: not-allowed;
      }

      .page-button {
        background-color: white;
        color: #256395;
        border: none;
        padding: 8px 10px;
        margin: 0 3px;
        font-size: 14px;
        font-weight: 700;
        border-radius: 5px;
        cursor: pointer;
        transition: background-color 0.3s;
        text-decoration: none;
      }
    </style>
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
