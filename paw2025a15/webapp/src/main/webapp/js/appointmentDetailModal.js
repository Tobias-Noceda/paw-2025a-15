function closeAppointmentDetailDialog() {
    const dialog = document.getElementById('customPatientDialog');
    if (dialog) {
        dialog.style.display = "none";
        dialog.close();
    }
    document.body.classList.remove('dialog-opened');
}