let pendingForm = null;

function openConfirmDialog(form, message) {
  pendingForm = form;
  const dialog = document.getElementById('customConfirmDialog');
  const messageElement = document.getElementById('confirmDialogMessage');
  messageElement.textContent = message;
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