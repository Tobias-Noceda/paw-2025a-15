let pendingForm = null;

function openConfirmDialog(form, message, secondaryMessage, accept, cancel) {
  pendingForm = form;

  const dialog = document.getElementById('customConfirmDialog');
  const msgEl  = document.getElementById('confirmDialogMessage');
  const secEl  = document.getElementById('confirmDialogSecondText');
  const okBtn  = document.getElementById('confirmButton');
  const noBtn  = document.getElementById('cancelButton');

  // 1) Rellenar textos
  msgEl.textContent = message;
  if (!secondaryMessage) {
    secEl.style.display = 'none';
  } else {
    secEl.style.display = '';
    secEl.innerHTML = secondaryMessage;
  }
  okBtn.textContent = accept;
  noBtn.textContent = cancel;

  // 2) Marcar body como “dialog abierto” para bloquear eventos fondo
  document.body.classList.add('dialog-opened');

  // 3) Abrir el modal con un tick de retraso (para que el click original no se propague)
  setTimeout(() => {
    dialog.showModal();
  }, 0);

  return false;  // evita submits accidentales
}

function closeConfirmDialog() {
  const dialog = document.getElementById('customConfirmDialog');
  dialog.close();
  pendingForm = null;
  // quitar el bloqueo de fondo
  document.body.classList.remove('dialog-opened');
}

function submitPendingForm() {
  if (pendingForm) {
    pendingForm.submit();
    closeConfirmDialog();
  }
}