function confirmDeauthDoctor(message, secondaryMessage, accept, cancel, name, value, id) {
    const form = document.getElementById('deauthDoctor_' + id);

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
