var pendingForm = null;
var formStartTime = null;

function openTextAreaDialog(form, message, secondaryMessage, accept, cancel, areaLabel) {
  pendingForm = form;

  const dialog = document.getElementById('customTextAreaDialog');
  const msgEl  = document.getElementById('txtDialogMessage');
  const secEl  = document.getElementById('txtDialogSecondText');
  const okBtn  = document.getElementById('txtConfirmButton');
  const noBtn  = document.getElementById('txtCancelButton');
  const areaLbl  = document.getElementById('areaLabel');

  msgEl.textContent = message;
  if (!secondaryMessage) {
    secEl.style.display = 'none';
  } else {
    secEl.style.display = '';
    secEl.innerHTML = secondaryMessage;
  }
  okBtn.textContent = accept;
  noBtn.textContent = cancel;
  areaLbl.textContent = areaLabel;

  document.body.classList.add('dialog-opened');

  setTimeout(() => {
    dialog.showModal();
  }, 0);

  return false;
}

function submitTextAppointment(row, message, secondaryMessage, accept, cancel, areaLabel, startTime) {
    const form = row.querySelector("form");
    if (form) {
        const event = new Event('submit', { cancelable: true });
        if (form.dispatchEvent(event)) {
            openTextAreaDialog(form, message, secondaryMessage, accept, cancel, areaLabel);
        }
        formStartTime = startTime; // Store the start time of the form
    }
}

function closeConfirmTextDialog() {
  pendingForm = null;
  formStartTime = null;
  document.body.classList.remove('dialog-opened');
  const dialog = document.getElementById('customTextAreaDialog');
  dialog.close();
}

function submitPendingTextForm(reason) {
  if (pendingForm) {
    const textField = document.getElementById(`detailText-${formStartTime}`);
    if (textField) {
      if (reason && reason.length > 0) {
        textField.value = reason;
      } else {
        textField.value = 'N/A';
      }
      pendingForm.submit();
      closeConfirmTextDialog();
    }
  }
}

document.addEventListener('DOMContentLoaded', () => {
  let dialog = document.getElementById('customTextAreaDialog');

  if (window.dialogPolyfill) {
    dialogPolyfill.registerDialog(dialog);
  }

  // Listener para el evento 'close'
  dialog.addEventListener('close', () => {
    pendingForm = null;
    document.body.classList.remove('dialog-opened');
  });
});