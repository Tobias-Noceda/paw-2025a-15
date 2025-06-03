function submitAppointment(row, message, secondaryMessage, accept, cancel) {
    const form = row.querySelector("form");
    console.log(message, secondaryMessage, accept, cancel);
    if (form) {
        const event = new Event('submit', { cancelable: true });
        if (form.dispatchEvent(event)) {
            openConfirmDialog(form, message, secondaryMessage, accept, cancel);
        }
    }
}
