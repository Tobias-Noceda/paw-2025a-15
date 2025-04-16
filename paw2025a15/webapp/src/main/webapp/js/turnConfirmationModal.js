// restart form function:
// This function is called when the user clicks on a row in the table and when the dialog is closed.
const restartForm = function () {
    const dialog = document.getElementById('appointmentDialog');
    const form = dialog.querySelector('form');

    // Force clear all inputs (ignore Spring model)
    form.querySelectorAll('input').forEach(input => input.value = '');

    // Clear error messages
    form.querySelectorAll('.error-message').forEach(error => error.innerHTML = '');
}

document.addEventListener("DOMContentLoaded", function () {
    const rows = document.querySelectorAll(".appointment-row");
    const dialog = document.getElementById("appointmentDialog");
    const closeDialog = document.getElementById("closeDialog");

    rows.forEach(row => {
        row.addEventListener("click", function () {
            if(document.getElementById("dialogTime").value != this.dataset.time) {
                restartForm();
            }

            document.getElementById("timeSpan").innerText = this.dataset.time;
            document.getElementById("dateSpan").innerText = this.dataset.formatteddate;
            
            // Hidden fields
            document.getElementById("dialogLocalDate").value = this.dataset.localdate;
            document.getElementById("dialogTime").value = this.dataset.time;
            document.getElementById("dialogShiftId").value = this.dataset.shift;
            
            dialog.showModal();
        });
    });

    closeDialog.addEventListener("click", function () {
        restartForm();
        dialog.close();
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const fileDialog = document.getElementById("fileDialog");
    const closeFileDialog = document.getElementById("closeFileDialog");
    const fileViewer = document.getElementById("fileViewer");

    document.querySelectorAll(".appointment-row").forEach(row => {
        row.addEventListener("click", function () {
            const fileId = this.dataset.fileid;
            const fileUrl = `/supersecret/files/${fileId}`;
            fileViewer.src = fileUrl;
            fileDialog.showModal();
        });
    });

    closeFileDialog.addEventListener("click", function () {
        fileDialog.close();
        fileViewer.src = "";
    });
});