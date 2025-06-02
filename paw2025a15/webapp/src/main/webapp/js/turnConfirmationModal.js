function submitAppointment(row, message, secondaryMessage, accept, cancel, areaLabel) {
    const form = row.querySelector("form");
    console.log(message, secondaryMessage, accept, cancel);
    if (form) {
        const event = new Event('submit', { cancelable: true });
        if (form.dispatchEvent(event)) {
            openTextAreaDialog(form, message, secondaryMessage, accept, cancel, areaLabel);
        }
    }
}
