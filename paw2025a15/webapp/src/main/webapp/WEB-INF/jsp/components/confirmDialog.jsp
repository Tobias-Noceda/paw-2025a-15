<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <head>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>
  </head>
  <body>
    <dialog class="confirmation-dialog" id="customConfirmDialog">
      <h3 id="confirmDialogMessage"></h3>
      <form method="dialog" style="margin-top: 15px; text-align: right;">
        <button class="cancel-button" onclick="submitPendingForm()">
          <spring:message code="confirmDialog.yes"></spring:message>
        </button>
        <button class="cancel-button" style="background-color: gray;" onclick="closeConfirmDialog()">
          <spring:message code="confirmDialog.no"></spring:message>
        </button>
      </form>
    </dialog>
    <script src="<c:url value='/js/confirmDialogModal.js'/>"></script>
  </body>
</html>