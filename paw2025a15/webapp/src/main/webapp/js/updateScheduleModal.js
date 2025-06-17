var pendingForm = null;

function updateScheduleModal(form, message, secondaryMessage, cancelAppointments, keepAppointments, cancel) {
  console.log('updateScheduleModal called');
  pendingForm = form;
  const updatesSchedule = form && form.updateSchedules && form.updateSchedules.value === 'true';

  if (updatesSchedule) {
    const dialog = document.getElementById('updateConfirmDialog');
    const msgEl  = document.getElementById('updateDialogMessage');
    const secEl  = document.getElementById('updateDialogSecondText');
    const cancelBtn  = document.getElementById('cancelAllButton');
    const keepBtn = document.getElementById('keepAllButton');
    const noBtn  = document.getElementById('cancelButton');

    msgEl.textContent = message;
    if (!secondaryMessage) {
      secEl.style.display = 'none';
    } else {
      secEl.style.display = '';
      secEl.innerHTML = secondaryMessage;
    }
    cancelBtn.textContent = cancelAppointments;
    keepBtn.textContent = keepAppointments;
    noBtn.textContent = cancel;

    document.body.classList.add('dialog-opened');

    setTimeout(() => {
      dialog.showModal();
    }, 0);
  } else {
    form.submit();
  }

  return false;
}

function closeUpdateDialog() {
  pendingForm = null;
  document.body.classList.remove('dialog-opened');
  const dialog = document.getElementById('updateConfirmDialog');
  dialog.close();
}

function submitPendingForm(hasToCancel) {
  if (pendingForm) {
    // Set the value for this:
    // <form:hidden path="keepTurns" id="keepTurnsCheckBox" value="true" checked="true"/>
    const keepTurnsCheckBox = document.getElementById('keepTurnsCheckBox');
    if (keepTurnsCheckBox) {
      keepTurnsCheckBox.value = hasToCancel ? 'false' : 'true';
    }
    pendingForm.submit();
    closeUpdateDialog();
  }
}

document.addEventListener('DOMContentLoaded', () => {
  let dialog = document.getElementById('updateConfirmDialog');

  if (window.dialogPolyfill) {
    dialogPolyfill.registerDialog(dialog);
  }

  // Listener para el evento 'close'
  dialog.addEventListener('close', () => {
    pendingForm = null;
    document.body.classList.remove('dialog-opened');
  });
});

