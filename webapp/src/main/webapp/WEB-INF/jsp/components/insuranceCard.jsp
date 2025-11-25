<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="doctor-card"><!-- was: insurance-card card -->
  <div class="insurance-card__content"><!-- keep your padding wrapper -->

    <!-- Avatar -->
    <div class="doctor-card__avatar"><!-- was: insurance-card__logo -->
      <img
              src="<c:url value='/supersecret/insurance-picture/${param.insuranceId}'/>"
              alt="Logo de ${param.insuranceName}"
      />
    </div>

    <!-- Info -->
    <div class="doctor-card__info"><!-- was: insurance-card__info -->
      <h3 class="doctor-card__name"><!-- was: insurance-card__name -->
        <c:out value="${param.insuranceName}" escapeXml="true" />
      </h3>
      <p class="doctor-card__specialty"><!-- was: insurance-card__id -->
        ID: <c:out value="${param.insuranceId}" escapeXml="true" />
      </p>
    </div>

    <!-- Actions -->
    <div class="insurance-card__actions"><!-- keeps your flex actions styling -->
      <a
              href="<c:url value='/admin/insurances/edit/${param.insuranceId}'/>"
              class="btn-primary"
      >
        <spring:message code="admin.insurances.button.edit"/>
      </a>
      <c:set var="removeConfirmationMessage">
        <spring:message code="admin.insurances.delete.confirm"/>
      </c:set>
      <c:set var="secondaryText">
        <spring:message code="admin.insurances.delete.warning"/>
      </c:set>
      <c:set var="removeText">
        <spring:message code="admin.insurances.delete.confirmMessage"/>
      </c:set>
      <c:set var="cancelRemoveText">
        <spring:message code="admin.insurance.form.button.cancel"/>
      </c:set>
      <form
              style="display: inline;"
              action="<c:url value='/admin/insurances/delete/${param.insuranceId}'/>"
              method="POST"
      >
        <button
                type="button"
                onclick="openConfirmDialog(
                        this.form,
                        '${removeConfirmationMessage}',
                        '${secondaryText}',
                        '${removeText}',
                        '${cancelRemoveText}'
                        )"
                class="btn btn-danger insurance-btn"
        >
          <spring:message code="admin.insurances.button.delete"/>
        </button>
      </form>
    </div>

  </div>

  <%@ include file="../components/confirmDialog.jsp" %>
  <script src="<c:url value='/js/turnConfirmationModal.js'/>"></script>
  <script src="<c:url value='/js/buttonControl.js'/>"></script>
</div>