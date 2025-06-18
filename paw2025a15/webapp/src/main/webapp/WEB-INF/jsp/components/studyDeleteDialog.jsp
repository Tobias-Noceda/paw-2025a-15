<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <meta charset="UTF-8"/>
  <link rel="stylesheet" href="https://unpkg.com/dialog-polyfill/dist/dialog-polyfill.css" />
  <script src="https://unpkg.com/dialog-polyfill/dist/dialog-polyfill.js"></script>
  <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>

<dialog class="confirmation-dialog" id="studyDeleteDialog">
  <h3><spring:message code="study.delete.confirmation.title"/></h3>
  <p id="studyDeleteSecondText" class="confirm-dialog-second-text" style="font-size: 16px;"></p>
  <p id="studyDeleteThirdText" class="confirm-dialog-second-text" style="font-size: 16px;"></p>
  <p class="confirm-dialog-second-text" style="font-size: 16px;">
    <spring:message code="study.delete.confirmation"/>
  </p>
  <form class="confirm-dialog-form">
    <!-- Ahora explícitamente botones de tipo “button” -->
    <a
      id="deleteConfirmButton"
      class="confirm-button"
      style="text-decoration: none;"
    ><spring:message code="study.delete.confirmation.ok"/></a>
    <button
      id="cancelButton"
      type="button"
      class="cancel-button"
      onclick="closeDeleteDialog()"
    >
      <spring:message code="appointments.dismiss"/>
    </button>
  </form>
</dialog>
  <script src="<c:url value='/js/studyDeleteModal.js'/>"></script>

</html>