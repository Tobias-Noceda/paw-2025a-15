function confirmAuthDoctor(message, secondaryMessage, accept, cancel) {
    const form = document.getElementById('authDoctorForm');
    const event = new Event('submit', { cancelable: true });
    if (form.dispatchEvent(event)) {
        openConfirmDialog(form, message, secondaryMessage, accept, cancel);
    }
}