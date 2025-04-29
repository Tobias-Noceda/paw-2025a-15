function confirmAuthDoctor(message, secondaryMessage, accept, cancel, name, value) {
    const form = document.getElementById('authDoctorForm');

    let actionInput = form.querySelector('input[name="${name}"]');
    if (!actionInput) {
        actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        form.appendChild(actionInput);
    }
    actionInput.value = value; 

    const event = new Event('submit', { cancelable: true });
    if (form.dispatchEvent(event)) {
        openConfirmDialog(form, message, secondaryMessage, accept, cancel);
    }
}