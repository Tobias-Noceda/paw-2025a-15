function confirmStudyDelete(url, dateText, commentText) {

  const dialog = document.getElementById('studyDeleteDialog');
  const secTwo  = document.getElementById('studyDeleteSecondText');
  const secThree  = document.getElementById('studyDeleteThirdText');
  const okBtn  = document.getElementById('deleteConfirmButton');

  if (!dateText) {
    secTwo.style.display = 'none';
  } else {
    secTwo.style.display = '';
    secTwo.innerHTML = dateText;
  }
  if (!commentText) {
    secThree.style.display = 'none';
  } else {
    secThree.style.display = '';
    secThree.innerHTML = commentText;
  }
  okBtn.setAttribute('href', url);

  document.body.classList.add('dialog-opened');

  setTimeout(() => {
    dialog.showModal();
  }, 0);

  return false;
}

function closeDeleteDialog() {
  document.body.classList.remove('dialog-opened');
  const dialog = document.getElementById('studyDeleteDialog');
  dialog.close();
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
  let dialog = document.getElementById('studyDeleteDialog');

  if (window.dialogPolyfill) {
    dialogPolyfill.registerDialog(dialog);
  }

  // Listener para el evento 'close'
  dialog.addEventListener('close', () => {
    document.body.classList.remove('dialog-opened');
  });
});