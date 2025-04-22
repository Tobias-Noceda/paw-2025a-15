function submitAppointment(row, message) {
    const form = row.querySelector("form");
    if (form) {
        const event = new Event('submit', { cancelable: true });
        if (form.dispatchEvent(event)) {
            openConfirmDialog(form, message);
        }
    }
}