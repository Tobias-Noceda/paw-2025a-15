<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<meta charset="UTF-8"/>
<link rel="stylesheet" href="https://unpkg.com/dialog-polyfill/dist/dialog-polyfill.css" />

<script src="https://unpkg.com/dialog-polyfill/dist/dialog-polyfill.js"></script>


<link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>

<dialog class="confirmation-dialog" id="customPatientDialog">
  <h3 id="patientDialogMessage"></h3>
  <p id="patientDialogSecondText" class="confirm-dialog-second-text"></p>
  <form class="confirm-dialog-form">
    <!-- Ahora explícitamente botones de tipo “button” -->
    <button
            id="patientConfirmButton"
            type="button"
            class="confirm-button"
    ><spring:message code="patientDialog.visitPatient"/></button>
    <button
            id="patientCancelButton"
            type="button"
            class="cancel-button"
            onclick="closeConfirmDialog()"
    ><spring:message code="doctorDetail.update.cancelButton"/></button>
  </form>
</dialog>
<script src="<c:url value='/js/confirmDialogModal.js'/>"></script>

</html>