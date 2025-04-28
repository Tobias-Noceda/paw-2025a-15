<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
  <head>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>
  </head>
  <body>
    <dialog class="confirmation-dialog" id="customConfirmDialog">
      <h3 id="confirmDialogMessage"></h3>
      <p id="confirmDialogSecondText" class="confirm-dialog-second-text"></p>
      <form method="dialog" class="confirm-dialog-form">
        <button id="confirmButton" class="confirm-button" onclick="submitPendingForm()"></button>
        <button id="cancelButton" class="cancel-button" onclick="closeConfirmDialog()"></button>
      </form>
    </dialog>
    <script src="<c:url value='/js/confirmDialogModal.js'/>"></script>
  </body>
</html>