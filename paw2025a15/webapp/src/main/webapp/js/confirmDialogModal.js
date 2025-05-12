let pendingForm = null;

function openConfirmDialog(form, message, secondaryMessage, accept, cancel) {
  pendingForm = form;

  const dialog = document.getElementById('customConfirmDialog');
  const msgEl  = document.getElementById('confirmDialogMessage');
  const secEl  = document.getElementById('confirmDialogSecondText');
  const okBtn  = document.getElementById('confirmButton');
  const noBtn  = document.getElementById('cancelButton');

  msgEl.textContent = message;
  if (!secondaryMessage) {
    secEl.style.display = 'none';
  } else {
    secEl.style.display = '';
    secEl.innerHTML = secondaryMessage;
  }
  okBtn.textContent = accept;
  noBtn.textContent = cancel;

  document.body.classList.add('dialog-opened');

  setTimeout(() => {
    dialog.showModal();
  }, 0);

  return false;
}


function closeConfirmDialog() {
  const dialog = document.getElementById('customConfirmDialog');
  dialog.close();
}


function submitPendingForm() {
  if (pendingForm) {
    pendingForm.submit();
    closeConfirmDialog();
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const dialog = document.getElementById('customConfirmDialog');

  if (window.dialogPolyfill) {
    dialogPolyfill.registerDialog(dialog);
  }

  // Listener para el evento 'close'
  dialog.addEventListener('close', () => {
    pendingForm = null;
    document.body.classList.remove('dialog-opened');
  });
});

