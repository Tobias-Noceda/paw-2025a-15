pendingForm = null;

function openAppointmentDetailDialog(form, message, secondaryMessage, accept, cancel) {
  pendingForm = form;

  const dialog = document.getElementById('customPatientDialog');
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

function closeAppointmentDetailDialog() {
    const dialog = document.getElementById('customPatientDialog');
    if (dialog) {
        pendingForm = null;
        dialog.close();
    }
    document.body.classList.remove('dialog-opened');
}