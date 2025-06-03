<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<meta charset="UTF-8"/>
<link rel="stylesheet" href="https://unpkg.com/dialog-polyfill/dist/dialog-polyfill.css" />

<script src="https://unpkg.com/dialog-polyfill/dist/dialog-polyfill.js"></script>


<link rel="stylesheet" href="<c:url value="/css/main.css"/>"/>

<dialog class="confirmation-dialog" id="customTextAreaDialog">

    <div class="dialog-header">

        <h3 id="txtDialogMessage"></h3>

    </div>

    <div class="dialog-body">

        <p id="txtDialogSecondText" class="confirm-dialog-second-text"></p>


        <p id="areaLabel"></p>
        <input type="text" id="txtDialogTextarea" class="field-container dialog-textarea"/>

    </div>

    <div class="dialog-footer confirm-dialog-form">

        <button

                id="txtCancelButton"

                type="button"

                class="btn cancel-button"

                onclick="closeConfirmDialog()"

        ></button>

        <button

                id="txtConfirmButton"

                type="button"

                class="btn confirm-button"

                onclick="submitPendingForm(document.getElementById('txtDialogTextarea').value)"

        ></button>

    </div>

</dialog>
<script src="<c:url value='/js/confirmDialogModal.js'/>"></script>

</html>