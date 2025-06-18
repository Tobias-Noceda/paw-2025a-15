var pendingForm = null;

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


function openPatientDialog(name,details, patientUrl){

  const dialog = document.getElementById('customPatientDialog');
  const msgEl  = document.getElementById('patientDialogMessage');
  const secEl  = document.getElementById('patientDialogSecondText');
  const okBtn  = document.getElementById('patientConfirmButton');

  msgEl.textContent = name;
  secEl.textContent = details;

  okBtn.onclick = function () {
    window.location = patientUrl;
  };

  document.body.classList.add('dialog-opened');

  dialog.showModal();

  return false;

}



function closeConfirmDialog() {
  pendingForm = null;
  document.body.classList.remove('dialog-opened');
  const dialog = document.getElementById('customConfirmDialog');
  dialog.close();
}

function submitPendingForm() {
  if (pendingForm) {
    pendingForm.submit();
    closeConfirmDialog();
  }
}

// Función para bulk actions
function confirmBulkAction(message, form, action, acceptText, cancelText) {

  
  // Crear el input hidden para la acción si no existe
  let actionInput = form.querySelector('input[name="action"]');
  if (!actionInput) {
    actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    form.appendChild(actionInput);
  }
  actionInput.value = action;

  // Llamar directamente al modal de confirmación
  openConfirmDialog(form, message, null, acceptText, cancelText);
  return false;
}

document.addEventListener('DOMContentLoaded', () => {
  let dialog = document.getElementById('customConfirmDialog');

  if (window.dialogPolyfill) {
    dialogPolyfill.registerDialog(dialog);
  }

  // Listener para el evento 'close'
  dialog.addEventListener('close', () => {
    pendingForm = null;
    document.body.classList.remove('dialog-opened');
  });
});

