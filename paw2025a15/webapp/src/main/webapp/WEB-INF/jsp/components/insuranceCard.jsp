<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="insurance-card card">
  <div class="insurance-card__content">

    <!-- Logo grande -->
    <div class="insurance-card__logo">
      <img
        src="<c:url value='/supersecret/insurance-picture/${param.insuranceId}'/>"
        alt="<spring:message code='admin.insurance.alt' arguments='${param.insuranceName}' htmlEscape='true'/>"
      />
    </div>

    <!-- Información -->
    <div class="insurance-card__info">
      <h3 class="insurance-card__name">
        <c:out value="${param.insuranceName}" escapeXml="true" />
      </h3>
      <p class="insurance-card__id">
        ID: <c:out value="${param.insuranceId}" escapeXml="true" />
      </p>
    </div>

    <!-- Acciones -->
    <div class="insurance-card__actions">
      <a
        href="<c:url value='/admin/insurances/edit/${param.insuranceId}'/>" 
        class="btn btn-secondary insurance-btn"
      >
        <spring:message code="admin.insurances.button.edit"/>
      </a>
      <c:set var="removeConfirmationMessage">
        <spring:message code="admin.insurances.delete.confirm"/>
      </c:set>
      <c:set var="secondaryText">
        <spring:message code="admin.insurances.delete.warning"/>
      </c:set>
      <c:set var="removeText"><spring:message code="admin.insurances.delete.confirmMessage"/></c:set>
      <c:set var="cancelRemoveText"><spring:message code="admin.insurance.form.button.cancel"/></c:set>
      <form
        style="display: inline;" 
        action="<c:url value='/admin/insurances/delete/${param.insuranceId}'/>" 
        method="POST"
      >
        <button
          type="button" 
          onclick="openConfirmDialog(this.form, '${removeConfirmationMessage}', '${secondaryText}', '${removeText}', '${cancelRemoveText}')"
          class="btn btn-danger insurance-btn"
        >
          <spring:message code="admin.insurances.button.delete"/>
        </button>
      </form>
    </div>
  </div>
  <%@include file="../components/confirmDialog.jsp" %>
  <script src="<c:url value='/js/turnConfirmationModal.js'/>"></script>
  <script src="<c:url value='/js/buttonControl.js'/>"></script>
</div> 