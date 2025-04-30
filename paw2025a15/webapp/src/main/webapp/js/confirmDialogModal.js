let pendingForm = null;

function openConfirmDialog(form, message, secondaryMessage, accept, cancel) {
  pendingForm = form;
  const dialog = document.getElementById('customConfirmDialog');
  const messageElement = document.getElementById('confirmDialogMessage');
  messageElement.textContent = message;
  const secondaryMessageElement = document.getElementById('confirmDialogSecondText');
  if(secondaryMessage == null || secondaryMessage == undefined || secondaryMessage == "") {
    secondaryMessageElement.style.display = 'none';
  } else {
    secondaryMessageElement.innerHTML = secondaryMessage;
  }
  const acceptButton = document.getElementById('confirmButton');
  acceptButton.textContent = accept;
  const cancelButton = document.getElementById('cancelButton');
  cancelButton.textContent = cancel;
  dialog.showModal();
  return false;
}

function closeConfirmDialog() {
  const dialog = document.getElementById('customConfirmDialog');
  dialog.close();
  pendingForm = null;
}

function submitPendingForm() {
  if (pendingForm) {
    pendingForm.submit();
    closeConfirmDialog();
  }
}